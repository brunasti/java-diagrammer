package it.brunasti.java.diagrammer.spring.api;

import it.brunasti.java.diagrammer.Debugger;
import it.brunasti.java.diagrammer.Utils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Analyses Java source files (plain text) to discover Spring REST API endpoints.
 * Finds all classes annotated with @RestController, extracts @RequestMapping
 * (class-level base path) and all method-level HTTP mappings, and produces
 * a list of fully-qualified API URLs with their class and method context.
 */
public class ApiDefinitionAnalyser {

    private final PrintStream output;
    private final String javaFilesPath;
    private boolean recursive = false;

    ArrayList<RestControllerInfo> controllers = new ArrayList<>();
    ArrayList<ApiEndpoint>        endpoints   = new ArrayList<>();

    private static final String REST_CONTROLLER   = "@RestController";
    private static final String MAP_ID            = "Mapping";
    private static final int    MAP_BLOCK_LENGTH  = 500;
    private static final int    PRE_MAP_LENGTH    = 20;

    // Only these prefixes form valid Spring mapping annotations
    private static final Set<String> VALID_MAPPING_PREFIXES =
            Set.of("Get", "Post", "Put", "Delete", "Patch", "Request");

    public ApiDefinitionAnalyser(String javaFilesPath) {
        this.output = System.out;
        this.javaFilesPath = javaFilesPath;
    }

    public ApiDefinitionAnalyser(PrintStream output, String javaFilesPath) {
        this.output = output;
        this.javaFilesPath = javaFilesPath;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    private void cleanLocalVars() {
        controllers = new ArrayList<>();
        endpoints   = new ArrayList<>();
    }

    private void setDefaultConfiguration() {
    }

    private void loadJsonConfiguration(JSONObject jsonObject) {
    }

    private boolean loadJsonConfigurationFromFile(String configurationFileName) {
        Debugger.debug(10, "loadJsonConfigurationFromFile : " + configurationFileName);
        JSONObject jsonObject = Utils.loadJsonFile(configurationFileName);
        if (null == jsonObject) {
            Debugger.debug(12,
                    "loadJsonConfigurationFromFile : no data in config file " + configurationFileName);
            return false;
        }
        loadJsonConfiguration(jsonObject);
        return true;
    }

    private boolean loadConfiguration(String configurationFileName) {
        if ((null == configurationFileName) || (configurationFileName.isBlank())) {
            setDefaultConfiguration();
            return true;
        } else {
            return loadJsonConfigurationFromFile(configurationFileName);
        }
    }

    // ---- Text-parsing helpers ----

    private String extractPackageName(String content) {
        int start = content.indexOf("package ");
        if (start < 0) return "";
        start += "package ".length();
        int end = content.indexOf(";", start);
        return end > start ? content.substring(start, end).trim() : "";
    }

    private String extractClassName(String content) {
        int idx = content.indexOf(" class ");
        if (idx < 0) return "";
        int nameStart = idx + " class ".length();
        int nameEnd = nameStart;
        while (nameEnd < content.length() && Character.isJavaIdentifierPart(content.charAt(nameEnd))) {
            nameEnd++;
        }
        return content.substring(nameStart, nameEnd);
    }

    private int findClassBodyStart(String content, String className) {
        if (className.isEmpty()) return 0;
        int classIdx = content.indexOf("class " + className);
        if (classIdx < 0) return 0;
        int braceIdx = content.indexOf("{", classIdx);
        return braceIdx >= 0 ? braceIdx : 0;
    }

    private boolean isRestController(String content) {
        int idx = 0;
        while (idx < content.length()) {
            int pos = content.indexOf(REST_CONTROLLER, idx);
            if (pos < 0) return false;

            // Annotation form: @RestController must be the first non-whitespace token on its
            // line (optionally preceded by other annotations starting with '@').
            // This filters out occurrences inside string literals ("@RestController") and
            // Javadoc/comment lines (* ... @RestController).
            int lineStart = content.lastIndexOf('\n', pos) + 1;
            String linePrefix = content.substring(lineStart, pos).trim();
            boolean atLineStart = linePrefix.isEmpty() || linePrefix.startsWith("@");
            if (!atLineStart) {
                idx = pos + 1;
                continue;
            }

            // Guard against @RestControllerAdvice and similar
            int after = pos + REST_CONTROLLER.length();
            if (after >= content.length() || !Character.isJavaIdentifierPart(content.charAt(after))) {
                return true;
            }
            idx = after;
        }
        return false;
    }

    /**
     * Find the closing ')' of an annotation's parameter list, correctly skipping
     * any ')' characters that appear inside string literals.
     */
    private int findAnnotationCloseParen(String text) {
        boolean inQuote = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inQuote = !inQuote;
            } else if (ch == ')' && !inQuote) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Extract the URL string from the text immediately following the annotation name,
     * e.g. from {@code ("/api/users")} or {@code (value = "/api/users")}.
     * Only searches inside the annotation's own parentheses; returns "" if the
     * annotation has no parameters or contains no quoted string.
     */
    private String extractUrlFromAnnotationText(String textAfterAnnotationName) {
        // Annotation must open with '(' to have a URL parameter
        if (!textAfterAnnotationName.stripLeading().startsWith("(")) return "";
        int closeParen = findAnnotationCloseParen(textAfterAnnotationName);
        if (closeParen < 0) return "";
        String params = textAfterAnnotationName.substring(0, closeParen);
        int firstQuote = params.indexOf('"');
        if (firstQuote < 0) return "";
        int secondQuote = params.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return "";
        return params.substring(firstQuote + 1, secondQuote);
    }

    /**
     * Extract the method function name from the block of text starting at "Mapping...".
     * Skips past the annotation's closing ')' then finds the word immediately
     * before the method's parameter-list '('.
     */
    private String extractFunctionName(String blockFromAnnotation) {
        String afterAnnotationName = blockFromAnnotation.substring(MAP_ID.length());

        // Skip annotation parameters if present
        String rest;
        if (afterAnnotationName.stripLeading().startsWith("(")) {
            int annotationClose = findAnnotationCloseParen(afterAnnotationName);
            if (annotationClose < 0) return "";
            rest = afterAnnotationName.substring(annotationClose + 1);
        } else {
            rest = afterAnnotationName;
        }

        // The method body opens with '{'; everything before it is the signature
        int bodyStart = rest.indexOf('{');
        if (bodyStart < 0) return "";
        String signature = rest.substring(0, bodyStart);

        // Method name is the word immediately before the parameter-list '('
        int methodParen = signature.lastIndexOf('(');
        if (methodParen < 0) return "";
        String beforeParen = signature.substring(0, methodParen).trim();
        int lastBreak = Math.max(beforeParen.lastIndexOf(' '), beforeParen.lastIndexOf('\n'));
        return lastBreak >= 0 ? beforeParen.substring(lastBreak + 1).trim() : beforeParen;
    }

    /**
     * Map annotation type prefix to an HTTP method string.
     * For @RequestMapping, tries to read the {@code method = RequestMethod.XXX} attribute.
     */
    private String resolveHttpMethod(String annotationType, String blockFromAnnotation) {
        switch (annotationType) {
            case "Get":    return "GET";
            case "Post":   return "POST";
            case "Put":    return "PUT";
            case "Delete": return "DELETE";
            case "Patch":  return "PATCH";
            case "Request": {
                int rmIdx = blockFromAnnotation.indexOf("RequestMethod.");
                if (rmIdx >= 0) {
                    rmIdx += "RequestMethod.".length();
                    int end = rmIdx;
                    while (end < blockFromAnnotation.length()
                            && Character.isUpperCase(blockFromAnnotation.charAt(end))) {
                        end++;
                    }
                    if (end > rmIdx) return blockFromAnnotation.substring(rmIdx, end);
                }
                return "REQUEST";
            }
            default: return annotationType.toUpperCase();
        }
    }

    // ---- File analysis ----

    public void analyseFile(String javaFilePath) {
        Debugger.debug(3, "analyseFile : " + javaFilePath);

        String content = Utils.readFileToString(javaFilePath);
        if (content.isEmpty()) return;
        if (!isRestController(content)) return;

        String packageName    = extractPackageName(content);
        String className      = extractClassName(content);
        int    classBodyStart = findClassBodyStart(content, className);

        Debugger.debug(2, "analyseFile - @RestController: " + packageName + "." + className);

        String classUrl = "";
        int index = 0;

        while (index < content.length()) {
            int location = content.indexOf(MAP_ID, index);
            if (location < 0) break;

            // Skip "Mapping" that appears on an import line
            int lineStart = content.lastIndexOf('\n', location);
            String linePrefix = content.substring(Math.max(0, lineStart), location);
            if (linePrefix.contains("import ")) {
                index = location + MAP_ID.length();
                continue;
            }

            // Extract annotation type from the '@' immediately preceding "Mapping"
            String beforeMapping = content.substring(Math.max(0, location - PRE_MAP_LENGTH), location);
            int atIdx = beforeMapping.lastIndexOf('@');
            if (atIdx < 0) {
                index = location + MAP_ID.length();
                continue;
            }
            String annotationType = beforeMapping.substring(atIdx + 1).trim();
            if (!VALID_MAPPING_PREFIXES.contains(annotationType)) {
                index = location + MAP_ID.length();
                continue;
            }

            // Grab a working block starting at "Mapping..."
            String block = content.length() > (location + MAP_BLOCK_LENGTH)
                    ? content.substring(location, location + MAP_BLOCK_LENGTH)
                    : content.substring(location);

            String url = extractUrlFromAnnotationText(block.substring(MAP_ID.length()));

            Debugger.debug(3, " - annotation: @" + annotationType + "Mapping  url: [" + url + "]");

            // Class-level @RequestMapping — record as base URL and move on
            if (annotationType.equals("Request") && location < classBodyStart) {
                classUrl = url;
                Debugger.debug(3, "  Class base URL: [" + classUrl + "]");
                index = location + MAP_ID.length();
                continue;
            }

            String functionName = extractFunctionName(block);
            String httpMethod   = resolveHttpMethod(annotationType, block);
            String fullUrl      = classUrl + url;

            ApiEndpoint endpoint = new ApiEndpoint();
            endpoint.packageName  = packageName;
            endpoint.className    = className;
            endpoint.functionName = functionName;
            endpoint.httpMethod   = httpMethod;
            endpoint.url          = fullUrl;

            Debugger.debug(2, "  Found endpoint: " + endpoint);
            endpoints.add(endpoint);

            index = location + MAP_ID.length();
        }

        // Register this @RestController with the base URL resolved above
        RestControllerInfo info = new RestControllerInfo();
        info.packageName = packageName;
        info.className   = className;
        info.baseUrl     = classUrl;
        controllers.add(info);
        Debugger.debug(2, "  Registered controller: " + info);
    }

    // ---- Source file discovery and loading ----

    public void loadStructure(final String configurationFile, final String javaFilesPath) {
        Debugger.debug(1, "loadStructure [" + configurationFile + "] [" + javaFilesPath + "]");
        cleanLocalVars();

        boolean initiated = loadConfiguration(configurationFile);
        if (!initiated) {
            Debugger.debug(1, "Configuration JSON not loaded");
            return;
        }

        String rootPath = javaFilesPath.endsWith("/")
                ? javaFilesPath.substring(0, javaFilesPath.length() - 1)
                : javaFilesPath;

        Debugger.debug(1, "Scanning " + (recursive ? "recursively" : "non-recursively")
                + " in [" + rootPath + "]");

        try (Stream<Path> stream = recursive
                ? Files.walk(Paths.get(rootPath))
                : Files.list(Paths.get(rootPath))) {

            List<Path> javaFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .sorted()
                    .toList();

            Debugger.debug(2, "Found " + javaFiles.size()
                    + " Java source files in [" + rootPath + "]");

            javaFiles.forEach(filePath -> {
                try {
                    analyseFile(filePath.toString());
                } catch (Exception ex) {
                    Debugger.debug(2, "Error analysing file: " + filePath
                            + " - " + ex.getMessage());
                }
            });

        } catch (IOException ex) {
            System.err.println("Error scanning source directory ["
                    + javaFilesPath + "] : " + ex.getMessage());
        }
    }

    // ---- Output generation ----

    private void generateOutput(final String configurationFile, final String javaFilesPath) {
        Debugger.debug(2, "generateOutput() ------------------");

        output.println("=== API Definition Report ===");
        output.println("' Generator    : " + this.getClass().getName());
        output.println("' Source Path  : [" + javaFilesPath + "]");
        output.println("' Config       : [" + configurationFile + "]");
        output.println("' Generated    : " + new Date());
        output.println("' Controllers  : " + controllers.size());
        output.println("' Endpoints    : " + endpoints.size());
        output.println();

        // --- Section 1: RestController classes ---
        output.println("--- RestController Classes ---");
        output.println();

        if (controllers.isEmpty()) {
            output.println("No @RestController classes found.");
        } else {
            controllers.sort(Comparator.comparing((RestControllerInfo c) -> c.packageName)
                                       .thenComparing(c -> c.className));

            output.printf("%-40s  %s%n", "BASE PATH", "CLASS");
            output.println("-".repeat(80));
            controllers.forEach(c -> output.println(c));
        }

        output.println();

        // --- Section 2: API endpoints ---
        output.println("--- API Endpoints ---");
        output.println();

        if (endpoints.isEmpty()) {
            output.println("No API endpoints found.");
            return;
        }

        endpoints.sort(Comparator.comparing((ApiEndpoint e) -> e.url)
                                 .thenComparing(e -> e.httpMethod));

        output.printf("%-10s  %-60s  %s%n", "METHOD", "URL", "CLASS :: METHOD");
        output.println("-".repeat(120));

        endpoints.forEach(ep -> output.println(ep));
        output.println();
    }

    public void process(final String configurationFile) {
        loadStructure(configurationFile, javaFilesPath);
        generateOutput(configurationFile, javaFilesPath);
    }
}
