package it.brunasti.java.diagrammer.spring.api;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;
import it.brunasti.java.diagrammer.Debugger;
import it.brunasti.java.diagrammer.Utils;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Analyses Java source files to discover Spring REST API endpoints.
 * Finds all classes annotated with @RestController, extracts @RequestMapping
 * (class-level base path) and all method-level HTTP mappings, and produces
 * a list of fully-qualified API URLs with their class and method context.
 */
public class ApiDefinitionAnalyser {

    private final PrintStream output;
    private final String javaFilesPath;
    private boolean recursive = false;

    ArrayList<ApiEndpoint> endpoints = new ArrayList<>();

    private static final String REST_CONTROLLER = "RestController";

    // Pairs of [annotation simple name, HTTP method string].
    // null HTTP method means extract from @RequestMapping's "method" attribute.
    private static final String[][] MAPPING_ANNOTATIONS = {
        {"GetMapping",    "GET"},
        {"PostMapping",   "POST"},
        {"PutMapping",    "PUT"},
        {"DeleteMapping", "DELETE"},
        {"PatchMapping",  "PATCH"},
        {"RequestMapping", null}
    };

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
        endpoints = new ArrayList<>();
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

    // ---- QDox annotation helpers ----

    private JavaAnnotation findAnnotation(JavaAnnotatedElement element, String simpleName) {
        for (JavaAnnotation annotation : element.getAnnotations()) {
            if (annotation.getType().getSimpleName().equals(simpleName)) {
                return annotation;
            }
        }
        return null;
    }

    private String extractUrlFromAnnotation(JavaAnnotation annotation) {
        if (annotation == null) return "";
        Object value = annotation.getNamedParameter("value");
        if (value == null) {
            value = annotation.getNamedParameter("path");
        }
        if (value == null) return "";
        return resolveAnnotationStringValue(value);
    }

    @SuppressWarnings("unchecked")
    private String resolveAnnotationStringValue(Object value) {
        if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            if (list.isEmpty()) return "";
            return resolveAnnotationStringValue(list.get(0));
        }
        String str = value.toString().trim();
        // QDox returns string literals with their surrounding double-quote characters
        if (str.startsWith("\"") && str.endsWith("\"") && str.length() >= 2) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    private String extractHttpMethodFromRequestMapping(JavaAnnotation annotation) {
        Object methodValue = annotation.getNamedParameter("method");
        if (methodValue == null) return "REQUEST";
        String methodStr = resolveAnnotationStringValue(methodValue);
        // Strip "RequestMethod." prefix, e.g. "RequestMethod.GET" -> "GET"
        int dotIdx = methodStr.lastIndexOf('.');
        if (dotIdx >= 0) {
            methodStr = methodStr.substring(dotIdx + 1);
        }
        return methodStr.isBlank() ? "REQUEST" : methodStr;
    }

    // ---- Class and method analysis ----

    public void analyseClass(JavaClass javaClass) {
        if (findAnnotation(javaClass, REST_CONTROLLER) == null) return;

        String packageName = javaClass.getPackageName();
        String className = javaClass.getSimpleName();
        Debugger.debug(2, "analyseClass - @RestController: " + packageName + "." + className);

        // Class-level @RequestMapping defines the base path for all methods
        String baseUrl = extractUrlFromAnnotation(findAnnotation(javaClass, "RequestMapping"));
        Debugger.debug(3, "  Base URL: [" + baseUrl + "]");

        for (JavaMethod method : javaClass.getMethods()) {
            processMethod(method, packageName, className, baseUrl);
        }

        // Recurse into nested @RestController classes if any
        for (JavaClass nested : javaClass.getNestedClasses()) {
            analyseClass(nested);
        }
    }

    private void processMethod(JavaMethod method,
                               String packageName, String className, String baseUrl) {
        for (String[] mapping : MAPPING_ANNOTATIONS) {
            String annotationName = mapping[0];
            String fixedHttpMethod = mapping[1];

            JavaAnnotation annotation = findAnnotation(method, annotationName);
            if (annotation == null) continue;

            String url = extractUrlFromAnnotation(annotation);
            String httpMethod = (fixedHttpMethod != null)
                    ? fixedHttpMethod
                    : extractHttpMethodFromRequestMapping(annotation);

            ApiEndpoint endpoint = new ApiEndpoint();
            endpoint.packageName = packageName;
            endpoint.className = className;
            endpoint.functionName = method.getName();
            endpoint.httpMethod = httpMethod;
            endpoint.url = baseUrl + url;

            Debugger.debug(2, "  Found endpoint: " + endpoint);
            endpoints.add(endpoint);
        }
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

        JavaProjectBuilder builder = new JavaProjectBuilder();

        Debugger.debug(1, "Scanning " + (recursive ? "recursively" : "non-recursively") + " in [" + rootPath + "]");

        try (Stream<Path> stream = recursive ? Files.walk(Paths.get(rootPath)) : Files.list(Paths.get(rootPath))) {
            List<Path> javaFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .sorted()
                    .toList();

            Debugger.debug(2, "Found " + javaFiles.size() + " Java source files in [" + rootPath + "]");

            for (Path filePath : javaFiles) {
                Debugger.debug(3, "Parsing: " + filePath);
                try {
                    builder.addSource(new FileReader(filePath.toFile()));
                } catch (FileNotFoundException | ParseException ex) {
                    Debugger.debug(1, "Skipping unparseable file: " + filePath + " - " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("Error scanning source directory [" + javaFilesPath + "] : " + ex.getMessage());
            return;
        }

        for (JavaSource source : builder.getSources()) {
            for (JavaClass javaClass : source.getClasses()) {
                try {
                    analyseClass(javaClass);
                } catch (Exception ex) {
                    Debugger.debug(2, "Error analysing class "
                            + javaClass.getFullyQualifiedName() + " : " + ex.getMessage());
                }
            }
        }
    }

    // ---- Output generation ----

    private void generateOutput(final String configurationFile, final String javaFilesPath) {
        Debugger.debug(2, "generateOutput() ------------------");

        output.println("=== API Definition Report ===");
        output.println("' Generator   : " + this.getClass().getName());
        output.println("' Source Path : [" + javaFilesPath + "]");
        output.println("' Config      : [" + configurationFile + "]");
        output.println("' Generated   : " + new Date());
        output.println("' Endpoints   : " + endpoints.size());
        output.println();

        if (endpoints.isEmpty()) {
            output.println("No @RestController API endpoints found.");
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
