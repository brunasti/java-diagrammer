/*
 * Java-Diagrammer core component.
 * Generate a PrintUML class diagram from the compiled Java classes.
 *
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassLoaderRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Generate a PrintUML class diagram from the compiled Java classes.
 *
 * @author  Paolo Brunasti
 * @version %I%, %G%
 * @see     it.brunasti.java
 */
public class ClassDiagrammer {
  // TODO: Create a UI version where you can graphically select the directory, config, flags....
  // TODO: Add the option to extract Methods and Attributes, Protected Private or Abstract (MA-PPA)
  // TODO: Manage arrays (LString...)
  // TODO: Remove "Lorem ipsum" (in test classes)
  // TODO: create more meaningful tests
  // TODO: Avoid generic catch(Exception)

  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  // CONFIGURATION SETTINGS ------------------------
  // Lists of the packages and classes to be excluded in the diagram
  private HashSet<String> toBeExcludedPackages = null;
  private HashSet<String> toBeExcludedClasses = null;

  private String includeFileName = "";
  private boolean flagAvoidSelfReferring = false;


  // List of already written Uses, to avoid multiple connections
  private HashSet<String> usesWritten = new HashSet<>();

  /**
   * Instantiate a ClassDiagrammer with output directed to StandardOut.
   */
  public ClassDiagrammer() {
    this.output = System.out;
  }

  /**
   * Instantiate a ClassDiagrammer with output directed to a passed PrintStream.
   * Used in case to create an output file, passing a PrintStream pointing
   * to the desired file.
   * If System.out is passed, the output is to StandardOut.
   *
   * @param output The PrintStream to which the output will be directed.
   */
  public ClassDiagrammer(PrintStream output) {
    this.output = output;
  }

  private ClassLoader getClassLoader(final String path) {
    try {
      File file = new File(path);

      if (!file.exists()) {
        System.err.println("Class loader directory not existing");
        return null;
      }

      // Convert File to a URL
      URL url = file.toURI().toURL();
      URL[] urls = new URL[]{url};

      // Create a new class loader with the directory
      return new URLClassLoader(urls);
    } catch (IOException e) {
      System.err.println("Error getting the ClassLoader : " + e.getMessage());
      return null;
    }
  }



  // ------------------------------------------------------
  // Load and manage Configuration ------------------------

  private void setDefaultConfiguration() {
    // TODO: how to manage Ljava and similar?
    // toBeExcludedPackages.add("Ljava.lang.");
    toBeExcludedPackages.add("java.lang.");
    includeFileName = "";
    flagAvoidSelfReferring = false;
  }

  private void loadJsonConfiguration(JSONObject jsonObject) {
    loadExcludeConfiguration(jsonObject);
    loadIncludeFileConfiguration(jsonObject);
    loadAvoidSelfReflectionConfiguration(jsonObject);
  }

  private void loadIncludeFileConfiguration(JSONObject jsonObject) {
    Object includeFile = jsonObject.get("includeFile");
    if (includeFile != null) {
      includeFileName = includeFile.toString();
      Debugger.debug(4, "  - includeFile [" + includeFileName + "]");
    }
  }

  private void loadAvoidSelfReflectionConfiguration(JSONObject jsonObject) {
    Object selfReflection = jsonObject.get("selfReflection");
    if (selfReflection != null) {
      flagAvoidSelfReferring = selfReflection.toString().equalsIgnoreCase("false");
      Debugger.debug(4, "  - selfReflection [" + flagAvoidSelfReferring + "]");
    }
  }

  private void loadExcludeConfiguration(JSONObject jsonObject) {
    JSONObject exclude = (JSONObject) jsonObject.get("exclude");

    if (exclude != null) {
      JSONArray classes = (JSONArray) exclude.get("classes");
      Iterator<JSONObject> iterator = (Iterator<JSONObject>) classes.iterator();
      while (iterator.hasNext()) {
        JSONObject object = iterator.next();
        Debugger.debug(2, "  - excludeClass JSONObject [" + object + "]");
        String excludeClass = object.get("class").toString();
        Debugger.debug(2, "  - excludeClass [" + excludeClass + "]");
        toBeExcludedClasses.add(excludeClass);
      }

      JSONArray packages = (JSONArray) exclude.get("packages");
      iterator = (Iterator<JSONObject>) packages.iterator();
      while (iterator.hasNext()) {
        JSONObject object = iterator.next();
        Debugger.debug(2, "  - excludePackage JSONObject [" + object + "]");
        String excludePackage = object.get("package").toString();
        Debugger.debug(2, "  - excludePackage [" + excludePackage + "]");
        toBeExcludedPackages.add(excludePackage);
      }
    }
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
    toBeExcludedPackages = new HashSet<>();
    toBeExcludedClasses = new HashSet<>();

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
    Debugger.debug(2,
            "iterateSubDirectories : " + path);
    String newPath = path + "/" + localPackage.replace(".", "/");
    Debugger.debug(2,
            "iterateSubDirectories : " + newPath);
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


  // ---------------------------------------------------------
  // Check if Type is to be connected ------------------------

  private boolean isTypeToBeConnected(final JavaClass javaClass,
                                      final Field field) {
    String type = field.getType().toString();
    return isTypeToBeConnected(javaClass, type);
  }

  private boolean isTypeToBeConnected(final JavaClass javaClass,
                                      final String type) {
    Debugger.debug(8, "isTypeToBeConnected " + javaClass.getClassName() + " to " + type);

    // Avoid self referencing loops
    if (flagAvoidSelfReferring) {
      if (type.equals(javaClass.getClassName())) {
        Debugger.debug(10, "  - self ref ");
        return false;
      }
    }

    // If the type is all lowercase, then is a primitive type
    if (type.toLowerCase().equals(type)) {
      Debugger.debug(10, "  - primitive");
      return false;
    }

    if (toBeExcludedClasses.contains(type)) {
      Debugger.debug(10, "  - exclude class");
      return false;
    }

    AtomicReference<Boolean> excludedPackage = new AtomicReference<>(false);
    toBeExcludedPackages.forEach(localPackage -> {
      Debugger.debug(8, "      - package : " + localPackage);
      if (type.startsWith(localPackage)) {
        Debugger.debug(10,"    -> exclude package " + type);
        excludedPackage.set(true);
      }
    });
    Debugger.debug(8, "  - exclude package : " + excludedPackage.get());

    return !excludedPackage.get();
  }


  private boolean isToBeExcluded(final String type) {
//    Debugger.debug("isToBeExcluded " + type);

//    // Avoid self referencing loops
//    if (flagAvoidSelfReferring) {
//      if (type.equals(javaClass.getClassName())) {
//        Debugger.debug("  - self ref ");
//        return false;
//      }
//    }
//
//    // If the type is all lowercase, then is a primitive type
//    if (type.toLowerCase().equals(type)) {
//      Debugger.debug("  - primitive");
//      return false;
//    }
//
    if (toBeExcludedClasses.contains(type)) {
      Debugger.debug(2, "  - exclude class");
      return true;
    }

    AtomicReference<Boolean> excludedPackage = new AtomicReference<>(false);
    toBeExcludedPackages.forEach(localPackage -> {
      Debugger.debug(10, "      - package : " + localPackage);
      if (type.startsWith(localPackage)) {
        Debugger.debug(12, "    -> exclude package " + type);
        excludedPackage.set(true);
      }
    });
    Debugger.debug(12,"  - exclude package : " + excludedPackage.get());

    return excludedPackage.get();
  }



  // ---------------------------------
  // Functions to generate the diagram

  private void writeUses(final JavaClass javaClass, final String type) {
    if (isTypeToBeConnected(javaClass, type)) {
      String correctedType = type.replace("[","").replace("]", "");
      String use = javaClass.getClassName() + " ..> " + correctedType;
      if (!usesWritten.contains(use)) {
        output.println(use);
        usesWritten.add(use);
      }
    }
  }

  private void generateUses(final ArrayList<JavaClass> classes) {
    Debugger.debug(8, "generateUses() ------------------");

    String currentPackage = "";
    boolean flagClosePackage = false;
    int counter = 0;

    output.println("' USES =======");
    for (JavaClass javaClass : classes) {
      if (!javaClass.getPackageName().equals(currentPackage)) {
        if (flagClosePackage) {
          output.println("!endsub");
        }
        flagClosePackage = true;
        counter ++;
        output.println("!startsub USES_" + javaClass.getPackageName().replace('.','_').toUpperCase());
//          output.println("!startsub PACKAGE" + counter);
        currentPackage = javaClass.getPackageName();
      }

      if (!javaClass.isEnum()) {
        Method[] methods = javaClass.getMethods();
        for (Method method : methods) {
          Debugger.debug(10, "generateUses " + method.getName()
                  + " : " + method.getSignature());
          String type = method.getReturnType().toString();
          writeUses(javaClass, type);

          Type[] arguments = method.getArgumentTypes();
          for (Type argument : arguments) {
            type = argument.getSignature()
                    .substring(1)
                    .replace("/", ".")
                    .replace(";", "");
            writeUses(javaClass, type);
          }
        }
      }
    };
    output.println("!endsub");
    output.println();
  }

  private void generateFields(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateFields() ------------------");
    output.println("' FIELDS =======");
    output.println("!startsub FIELDS");
    List done = new ArrayList();
    classes.forEach(javaClass -> {
      if (!javaClass.isEnum()) {
        for (Field field : javaClass.getFields()) {
          if (isTypeToBeConnected(javaClass, field)) {
            String correctedType = field.getType().toString().replace("[","").replace("]", "");
            String rel = javaClass.getClassName()
                    + " --> " + correctedType;
            if (!done.contains(rel)) {
              output.println(rel);
            }
            done.add(rel);
          }
        }
      }
    });
    output.println("!endsub FIELDS");
    output.println();
  }

  private void generateImports(final ArrayList<JavaClass> classes, String javaFilesPath) {
    Debugger.debug(2, "generateImports() ------------------");
    output.println("' IMPORTS =======");
    output.println("' Java Files Path : " + javaFilesPath);
    output.println("!startsub IMPORTS");
    if ((null != javaFilesPath) && (!javaFilesPath.isBlank())) {
      ImportsIdentifier importsIdentifier = new ImportsIdentifier();

      classes.forEach(javaClass -> {
        Debugger.debug(3, "  ---------- " + javaClass.getClassName());
        output.println("' " + javaClass.getClassName());
        String javaClassName = javaFilesPath
                + javaClass.getClassName().replace(".", "/")
                + ImportsIdentifier.FILE_TYPE;
        Set<String> imports = importsIdentifier.extractImports(javaClassName, javaFilesPath);
        for (String importString : imports) {
          Debugger.debug(3, "    -------- " + importString);
          String importedFile = importString;
          if (importedFile.startsWith("static")) {
            Debugger.debug(4, " replace static in " + importedFile);
            importedFile = importedFile.replace("static", "");
          }
          if (importedFile.endsWith(".*")) {
            Debugger.debug(4, " replace .* in " + importedFile);
            importedFile = importedFile.replace(".*", "");
          }
          Debugger.debug(" <- " + importString + "(" + importedFile + ")");
          if (isTypeToBeConnected(javaClass, importedFile)) {
            output.println(javaClass.getClassName()
                    + " ..o " + importedFile);
          }
        }
        output.println();
      });
    } else {
      Debugger.debug(3,
              "generateImports() --- Not generated because javaFilesPath not provided");
    }
    output.println("!endsub IMPORTS");
    output.println();
  }

  private void generateImplements(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateImplements() ------------------");
    output.println("' IMPLEMENT INTERFACE =======");
    output.println("!startsub IMPLEMENTS");
    List done = new ArrayList();
    classes.forEach(javaClass -> {
      try {
        for (JavaClass javaInterface : javaClass.getInterfaces()) {
          String rel = javaInterface.getClassName() + " <|.. "
                  + javaClass.getClassName();
          if (!done.contains(rel)) {
            output.println(rel);
          }
          done.add(rel);
        }
      } catch (ClassNotFoundException ex) {
        Debugger.debug(2, "Error generating `Implements` relations : " + ex.getMessage());
      }
    });
    output.println("!endsub IMPLEMENTS");
    output.println();
  }

  private void generateInheritances(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateInheritances() ------------------");
    output.println("' INHERITANCES =======");
    output.println("!startsub INHERITANCES");
    List done = new ArrayList();
    classes.forEach(javaClass -> {
      try {
        if (!"java.lang.Object".equals(
                javaClass.getSuperClass().getClassName())) {
          String rel = javaClass.getSuperClass().getClassName() + " <|-- "
                  + javaClass.getClassName();
          if (!done.contains(rel)) {
            output.println(rel);
          }
          done.add(rel);
        }
      } catch (ClassNotFoundException ex) {
        Debugger.debug(2, "Error generating `Inheritance` relations : " + ex.getMessage());
      }
    });
    output.println("!endsub INHERITANCES");
    output.println();
  }

  private void generateEnum(JavaClass javaClass, ClassLoader classLoader) {
    Debugger.debug("ENUM : " + javaClass.getClassName());
    String[] enumFields = new String[0];

    try {
      Class loadedClass = classLoader.loadClass(javaClass.getClassName());
      Debugger.debug(4, "  - loadedClass : " + loadedClass);
      Debugger.debug(4, "  - loadedClass : " + loadedClass.isEnum());

      enumFields = new String[loadedClass.getFields().length];
      int i = 0;
      for (java.lang.reflect.Field field : loadedClass.getFields()) {
        Debugger.debug(4, "      - loadedClass.Field : " + field);
        Debugger.debug(4, "      - loadedClass.Field.Name : " + field.getName());
        enumFields[i] = field.getName();
        i++;
      }
    } catch (ClassNotFoundException cex) {
      Debugger.debug(2,"Error generating `Enum` class : " + cex.getMessage());
    }

    output.println("enum " + javaClass.getClassName() + "{");
    for (String enumValue : enumFields) {
      output.println("  " + enumValue);
    }
    output.println("}");    
  }
  
  private void generateClasses(final ArrayList<JavaClass> classes, ClassLoader classLoader) {
    Debugger.debug(2, "generateClasses() ------------------");
    output.println();
    output.println();
    output.println("' CLASSES =======");

    String currentPackage = "";
    boolean flagClosePackage = false;
    int counter = 0;

    List<String> done = new ArrayList<>();

    classes.sort((a,b) -> { return a.getPackageName().compareTo(b.getPackageName());});
//    classes.forEach(javaClass -> {
    for (JavaClass javaClass : classes) {
      if (!done.contains(javaClass.getClassName())) {

        if (!javaClass.getPackageName().equals(currentPackage)) {
          if (flagClosePackage) {
            output.println("!endsub");
          }
          flagClosePackage = true;
          counter ++;
          output.println("!startsub " + javaClass.getPackageName().replace('.','_').toUpperCase());
//          output.println("!startsub PACKAGE" + counter);
          currentPackage = javaClass.getPackageName();
        }

        if (javaClass.isEnum()) {
          generateEnum(javaClass, classLoader);
        } else if (javaClass.isInterface()) {
          output.println("interface " + javaClass.getClassName());
        } else if (javaClass.isAbstract()) {
          output.println("abstract " + javaClass.getClassName());
        } else {
          output.println("class " + javaClass.getClassName());
        }
        done.add(javaClass.getClassName());
      }
    };
    output.println("!endsub");
    output.println();
  }

  private void generateFooter() {
    Debugger.debug(2, "generateFooter() ------------------");
    output.println();
    output.println("@enduml");
  }

  private void generateHeader(final String path,
                              final String configurationFile,
                              String javaFilesPath,
                              ArrayList<String> files,
                              ArrayList<JavaClass> classes
                              ) {
    Debugger.debug(2, "generateHeader() ------------------");
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' GENERATE CLASS DIAGRAM ===========");
    output.println("' Generator       : " + this.getClass().getName());
    output.println("' Path            : [" + path + "]");
    if ((null != javaFilesPath) && (!javaFilesPath.isBlank())) {
      output.println("' Java Files Path : [" + javaFilesPath + "]");
    }
    output.println("' Configuration   : [" + configurationFile + "]");
    output.println("' Generated at    : " + new Date());
    output.println("'");


    output.println("'   Stat infos    :");
    int totelLines = 0;
    for (String file : files) {
      String javaFile = javaFilesPath + file.replace(".", "/") + ".java";

      int lines = Utils.countLinesInFile(javaFile);
      if (lines > 0) {
        totelLines = totelLines + lines;
      }
    }

    int methods = 0;
    int fields = 0;
    for (JavaClass javaClass : classes) {
//        output.println("'     javaClass : [" +  javaClass.getMethods().length + "]");
      methods = methods + javaClass.getMethods().length;
      fields = fields + javaClass.getFields().length;
    }
    output.println("'       Files : [" +  files.size() + "]");
    output.println("'       Lines : [" +  totelLines + "]");
    output.println("'     Classes : [" +  classes.size() + "]");
    output.println("'     Methods : [" +  methods + "]");
    output.println("'      Fields : [" +  fields + "]");

      String includeFileContent = Utils.readFileToString(includeFileName);
    if (!includeFileContent.isBlank()) {
      output.println();
      output.println("' Include         : [" + includeFileName + "] ---------");
      output.println(includeFileContent);
      output.println("' Include end     : --------------------------");
    }
    output.println();
  }

  private void cleanLocalVars() {
    // Reset all variables to avoid conflicts in case of multiple run
    toBeExcludedPackages = null;
    toBeExcludedClasses = null;
    usesWritten = new HashSet<>();
  }

  /** Generate a diagram for the class files.
   * Generate a diagram for the class files located in the path,
   * with a configuration written in configurationFile and
   * in case searching for source in javaFilesPath
   *
   * @param path The path to the compiled Java classes directory.
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   * @param javaFilesPath The path to the source Java files directory.
   */
  public void generateDiagram(final String path,
                              final String configurationFile,
                              final String javaFilesPath) {
    Debugger.debug(1,"generateDiagram ["+path+"] ["+configurationFile+"] ["+javaFilesPath+"]");
    cleanLocalVars();

    boolean initiated = loadConfiguration(configurationFile);
    if (!initiated) {
      Debugger.debug(1,"Configuration JSON not loaded");
      return;
    }

    Debugger.debug(2,"Path : " + path);
    ArrayList<String> files = new ArrayList<>();
    try {
      Set<String> dirs = Utils.listDirectories(path);
      dirs.add("");

      Debugger.debug(4,"Directories : " + dirs.toString());
      dirs.forEach(dir -> files.addAll(iterateSubDirectories(path, dir)));
    } catch (IOException | NullPointerException e) {
      Debugger.debug(1,"Error listing directories : " + e.getMessage());
    }

    ClassLoader classLoader = getClassLoader(path);
    Debugger.debug(2, "classLoader [" + classLoader + "]");
    if (null == classLoader) {
      Debugger.debug(1,"Class loader not created");
      return;
    }

    ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);

    ArrayList<JavaClass> classes = new ArrayList<>();

    files.forEach(file -> {
      Debugger.debug(2, "Loading class : " + file);
      try {
        String fileName = file;
        if ((fileName != null) && (fileName.startsWith("."))) {
          fileName = fileName.substring(1);
        }
        // Exclude files which are in the config "exclude" sections
        if (!isToBeExcluded(fileName)) {
          JavaClass javaClass = rep.loadClass(fileName);
          classes.add(javaClass);
        }
      } catch (ClassNotFoundException e) {
        Debugger.debug(2,"Error loading class : " + file + " - " + e.getMessage());
      }
    });

    generateHeader(path, configurationFile, javaFilesPath, files, classes);
    generateClasses(classes, classLoader);
    generateInheritances(classes);
    generateImplements(classes);
    generateFields(classes);
    generateUses(classes);
    generateImports(classes, javaFilesPath);
    generateFooter();
  }
}
