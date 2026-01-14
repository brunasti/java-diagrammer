package it.brunasti.java.diagrammer.spring.web;

import it.brunasti.java.diagrammer.Debugger;
import it.brunasti.java.diagrammer.Utils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class WebFlowAnalyser {
    // Reference to a PrintStream to be used for the diagram
    // By default is the Standard.out, but it can be redirected
    // to a file.
    private final PrintStream output;

    private final String javaFilesPath;

    /**
     * Instantiate a ClassDiagrammer with output directed to StandardOut.
     */
    public WebFlowAnalyser(String javaFilesPath) {
        this.output = System.out;
        this.javaFilesPath = javaFilesPath;
    }

    /**
     * Instantiate a ClassDiagrammer with output directed to a passed PrintStream.
     * Used in case to create an output file, passing a PrintStream pointing
     * to the desired file.
     * If System.out is passed, the output is to StandardOut.
     *
     * @param output The PrintStream to which the output will be directed.
     */
    public WebFlowAnalyser(PrintStream output, String javaFilesPath) {
        this.output = output;
        this.javaFilesPath = javaFilesPath;
    }

    private void cleanLocalVars() {
        // Reset all variables to avoid conflicts in case of multiple run
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


    // ----------------------------------------------------
    // Functions to iterate through packages subdirectories
    private Set<String> iterateSubDirectories(final String path,
                                              final String localPackage) {
        Set<String> files = new HashSet<>();
        return iterateSubDirectories(path, localPackage, files);
    }

    private Set<String> iterateSubDirectories(final String path,
                                              final String localPackage,
                                              final Set<String> files) {
        String newPath = path + "/" + localPackage.replace(".", "/");
        try {
            Utils.listFiles(newPath).forEach(file -> {
                String className = file.replace(".class", "");
                files.add(localPackage + "." + className);
            });

            Set<String> dirs = Utils.listDirectories(newPath);

            dirs.forEach(dir -> iterateSubDirectories(path, localPackage + "." + dir, files));

            return files;
        } catch (IOException ex) {
            System.err.println("Error iterating along a subDirectory : " + ex.getMessage());
            return null;
        }
    }


    private void generateFooter() {
        Debugger.debug(2, "generateFooter() ------------------");
        output.println();
        output.println("@enduml");
    }

    private void generateHeader(final String configurationFile,
                                String javaFilesPath
    ) {
        Debugger.debug(2, "generateHeader() ------------------");
        output.println("@startuml");
        output.println("'https://plantuml.com/class-diagram");
        output.println();
        output.println("' GENERATE PAGES DIAGRAM ===========");
        output.println("' Generator       : " + this.getClass().getName());
        output.println("' Java Files Path : [" + javaFilesPath + "]");
        output.println("' Configuration   : [" + configurationFile + "]");
        output.println("' Generated at    : " + new Date());
        output.println("'");

        output.println("'   Pages infos    :");
        for (WebPageMaping page : pages) {
            Debugger.debug(2, " -- "+page.fileName);
            // rectangle "/" as homePage
            output.println("class \""+page.method.toUpperCase()+" "+page.mapping+"\" as "+page.function+ " {");
            output.println("    "+page.method);
            output.println("    "+page.fileName);
            output.println("    "+page.function);
            output.println("}");
            output.println();

        }

        output.println();
    }


    /** Generate a diagram for the class files.
     * Generate a diagram for the class files located in the path,
     * with a configuration written in configurationFile and
     * in case searching for source in javaFilesPath
     *
     * @param configurationFile The configuration file with the list
     *                          of packages and classes to exclude.
     * @param javaFilesPath The path to the source Java files directory.
     */
    public void generateDiagram(final String configurationFile,
                                final String javaFilesPath) {
        Debugger.debug(1,"generateDiagram ["+configurationFile+"] ["+javaFilesPath+"]");

        generateHeader(configurationFile, javaFilesPath);
        generateFooter();
    }

    ArrayList<WebPageMaping> pages = new ArrayList<>();
    static final private String controllerId = "Controller";
    static final private String mapId = "Mapping";
    static final private int mapIdBlockLenght = 450;
    static final private int preMapIdBlockLenght = 10;
    static final private int preIncludeMapIdBlockLenght = 100;

    public void analyseFile(String fileName) {
        int lastINdex = fileName.lastIndexOf(".");
        fileName = fileName.substring(0,lastINdex);
        String className = fileName.substring(fileName.lastIndexOf(".")+1);
        String javaFile = javaFilesPath + fileName.replace(".", "/") + ".java";

        String classUrl = "";
        String fileText = Utils.readFileToString(javaFile);
        if (!fileText.isEmpty()) {
            if (fileText.indexOf(controllerId)>0) {
                Debugger.debug(2, "analyseFile - "+fileName);
                Debugger.debug(2, " ---- "+javaFile);
                Debugger.debug(2, " ---- length : "+fileText.length());
                int index = 0;
                while (fileText.indexOf(mapId, index) > 0) {
                    Debugger.debug(2, " - ");
                    Debugger.debug(2, " ---- - index : [" + index + "]");
                    Debugger.debug(2, " ---- "+javaFile);
                    int location = fileText.indexOf(mapId, index);
                    String block = "";
                    if (fileText.length() > (location + mapIdBlockLenght)) {
                      block = fileText.substring(location - preIncludeMapIdBlockLenght, location + mapIdBlockLenght);
                    } else {
                      block = fileText.substring(location - preIncludeMapIdBlockLenght);
                    }

                    if (block.contains("import org.springframework.web.bind.annotation.")) {

                    } else {

                      try {
                        if (fileText.length() > (location + mapIdBlockLenght)) {
                          block = fileText.substring(location - preMapIdBlockLenght, location + mapIdBlockLenght);
                        } else {
                          block = fileText.substring(location - preMapIdBlockLenght);
                        }
//                        block = fileText.substring(location - preMapIdBlockLenght, location + mapIdBlockLenght);
                        Debugger.debug(2, " ---- - block : [" + block + "]");
                        int end = block.indexOf(" {");
                        if (end > 0) {
                          block = block.substring(0, end);
                        }
                        Debugger.debug(2, " ---- - block 0 : [" + block + "]");

                        String method = block.substring(block.indexOf('@') + 1, block.indexOf(mapId));
                        Debugger.debug(2, " ---- - method : [" + method + "]");

                        block = block.substring(block.indexOf(mapId) + mapId.length());
                        Debugger.debug(2, " ---- - block 2 : [" + block + "]");
                        String url = "";
                        if (block.contains("\"")) {
                          block = block.substring(block.indexOf("\"")+1);
                          Debugger.debug(2, " ---- - block 2.1 : [" + block + "]");
                          url = block.substring(0, block.indexOf("\""));
                          block = block.substring(block.indexOf("\"")+2);
                          Debugger.debug(2, " ---- - block 2.2 : [" + block + "]");
                        }
                        Debugger.debug(2, " ---- - url : [" + url + "]");

                        if (method.equals("Request")) {
                          classUrl = url;
                        }
//                        else
                        {
                          Debugger.debug(2, " ---- - block 3.1 : [" + block + "]");

                          String function = "";
                          if (block.contains(")")) {
                            block = block.substring(0, block.indexOf("("));
                            Debugger.debug(2, " ---- - block 3.2 : [" + block + "]");
                            block = block.substring(block.lastIndexOf(" ")+1);
                            Debugger.debug(2, " ---- - block 3.3 : [" + block + "]");
                            function = block;

//                            function = block.substring(block.lastIndexOf(" ") + 1);
                            Debugger.debug(2, " ---- - function : [" + function + "]");
                          }

                          WebPageMaping webPageMaping = new WebPageMaping();
                          webPageMaping.fileName = fileName;
                          webPageMaping.method = method;
                          if (!classUrl.equals(url)) {
                            url = classUrl + url;
                          }
                          webPageMaping.mapping = url;
                          webPageMaping.function = className + "_" + function;
                          Debugger.debug(2, " ---- - webPageMaping : [" + webPageMaping + "]");

                          if (!method.equals("Request")) {
                            pages.add(webPageMaping);
                          }
                          Debugger.debug(2, "");
                        }
                      } catch (Exception ex) {
                        ex.printStackTrace();
                      }
                    }

                    Debugger.debug(2, "");
                    index = location+mapId.length();
                }
            }
        }

    }


    /** Load the flow structure from the Java files.
     *
     * @param configurationFile The configuration file with the list
     *                          of packages and classes to exclude.
     * @param javaFilesPath The path to the source Java files directory.
     */
    public void loadStructure(final String configurationFile,
                                final String javaFilesPath) {
        Debugger.debug(1, "loadStructure [" + configurationFile + "] [" + javaFilesPath + "]");
        cleanLocalVars();

        boolean initiated = loadConfiguration(configurationFile);
        if (!initiated) {
            Debugger.debug(1, "Configuration JSON not loaded");
            return;
        }

        ArrayList<String> files = new ArrayList<>();
        try {
            Set<String> dirs = Utils.listDirectories(javaFilesPath);
            dirs.add("");

            Debugger.debug(4, "Directories : " + dirs);
            dirs.forEach(dir -> files.addAll(iterateSubDirectories(javaFilesPath, dir)));
        } catch (IOException | NullPointerException e) {
            Debugger.debug(1, "Error listing directories : " + e.getMessage());
        }

        files.sort(null);

        files.forEach(file -> {
            try {
                if ((file != null) && (!file.startsWith("."))) {
                    Debugger.debug(2, "process : " + file);

                    analyseFile(file);
                }
            } catch (Exception e) {
                Debugger.debug(2, "Error loading file structure : " + file + " - " + e.getMessage());
                e.printStackTrace(System.err);
            }
        });
    }

    public void process(final String configurationFile) {
        loadStructure(configurationFile, javaFilesPath);
        generateDiagram(configurationFile, javaFilesPath);
    }

}
