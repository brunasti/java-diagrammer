/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
  // TODO: Move files from temp directory to more appropriate ones
  // TODO: Manage arrays (LString...)
  // TODO: Add the option to extract Methods and Attributes, Protected Private or Abstract (MA-PPA)
  // TODO: Avoid generic catch(Exception)
  // TODO: option to add "hide empty members" in diagram
  // TODO: add Javadoc comments
  // TODO: create more meaningful tests
  // TODO: Rebuild the project in a clean env (libs dependencies....)
  // TODO: Create a UI version where you can graphically select the directory, config, flags....

  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  // CONFIGURATION SETTINGS ------------------------
  // Lists of the packages and classes to be excluded in the diagram
  private HashSet<String> toBeExcludedPackages = null;
  private HashSet<String> toBeExcludedClasses = null;

  private String includeFileName = "";


  // List of already written Uses, to avoid multiple connections
  private HashSet<String> usesWritten = new HashSet<>();

  /**
   * Instantiate a CLassDiagrammer with output directed to StandardOut.
   */
  public ClassDiagrammer() {
    this.output = System.out;
  }

  /**
   * Instantiate a CLassDiagrammer with output directed to a passed PrintStream.
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
      e.printStackTrace();
      return null;
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
      Utils.listFilesUsingFilesList(newPath).forEach(file -> {
        String className = file.replace(".class", "");
        files.add(localPackage + "." + className);
      });

      Set<String> dirs = Utils.listDirectories(newPath);

      dirs.forEach(dir -> iterateSubDirectories(path, localPackage + "." + dir, files));

      return files;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


  // Load Configuration ------------------------

  private void setDefaultConfiguration() {
    toBeExcludedPackages.add("Ljava.lang.");
    toBeExcludedPackages.add("java.lang.");
  }

  private void loadJsonConfiguration(JSONObject jsonObject) {
    loadExcludeConfiguration(jsonObject);
    loadIncludeFileConfiguration(jsonObject);
  }

  private void loadIncludeFileConfiguration(JSONObject jsonObject) {
    Object includeFile = jsonObject.get("includeFile");
    if (includeFile != null) {
      includeFileName = includeFile.toString();
      Debugger.debug(4, "  - includeFile [" + includeFileName + "]");
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
    Debugger.debug(1, "loadJsonConfigurationFromFile : " + configurationFileName);
    JSONObject jsonObject = Utils.loadConfigurationFile(configurationFileName);
    if (null == jsonObject) {
      Debugger.debug(2,
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


  private boolean isTypeToBeConnected(final JavaClass javaClass,
                                      final Field field) {
    String type = field.getType().toString();
    return isTypeToBeConnected(javaClass, type);
  }

  private boolean isTypeToBeConnected(final JavaClass javaClass,
                                      final String type) {
    Debugger.debug("isTypeToBeConnected " + javaClass.getClassName() + " to " + type);

    // TODO: add flag to avoid or not the self reflection
    //    // Avoid self referencing loops
    //    if (type.equals(javaClass.getClassName())) {
    //      Debugger.debug("  - self ref");
    //      return false;
    //    }

    // If the type is all lowercase, then is a primitive type
    if (type.toLowerCase().equals(type)) {
      Debugger.debug("  - primitive");
      return false;
    }

    if (toBeExcludedClasses.contains(type)) {
      Debugger.debug("  - exclude class");
      return false;
    }

    AtomicReference<Boolean> excludedPackage = new AtomicReference<>(false);
    toBeExcludedPackages.forEach(localPackage -> {
      Debugger.debug(4, "      - package : " + localPackage);
      if (type.startsWith(localPackage)) {
        Debugger.debug("    -> exclude package " + type);
        excludedPackage.set(true);
      }
    });
    Debugger.debug("  - exclude package : " + excludedPackage.get());

    return !excludedPackage.get();
  }

  // ---------------------------------
  // Functions to generate the diagram

  private void writeUses(final JavaClass javaClass, final String type) {
    if (isTypeToBeConnected(javaClass, type)) {
      String use = javaClass.getClassName() + " ..> " + type;
      if (!usesWritten.contains(use)) {
        output.println(use);
        usesWritten.add(use);
      }
    }
  }

  private void generateUses(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateUses() ------------------");
    output.println("' USES =======");
    classes.forEach(javaClass -> {
      if (!javaClass.isEnum()) {
        try {
          Method[] methods = javaClass.getMethods();
          for (Method method : methods) {
            Debugger.debug("generateUses " + method.getName()
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
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
        }
      }
    });
    output.println();
  }

  private void generateFields(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateFields() ------------------");
    output.println("' FIELDS =======");
    classes.forEach(javaClass -> {
      if (!javaClass.isEnum()) {
        try {
          for (Field field : javaClass.getFields()) {
            if (isTypeToBeConnected(javaClass, field)) {
              output.println(javaClass.getClassName()
                      + " --> " + field.getType());
            }
          }
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
        }
      }
    });
    output.println();
  }

  private void generateImports(final ArrayList<JavaClass> classes, String javaFilesPath) {
    Debugger.debug(2, "generateImports() ------------------");
    output.println("' IMPORTS =======");
    output.println("' Java Files Path : " + javaFilesPath);
    if ((null != javaFilesPath) && (!javaFilesPath.isBlank())) {
      ImportsIdentifier importsIdentifier = new ImportsIdentifier();

      classes.forEach(javaClass -> {
        Debugger.debug(3, "  ---------- " + javaClass.getClassName());
        output.println("' " + javaClass.getClassName());
        String javaClassName = javaFilesPath
                + javaClass.getClassName().replace(".", "/")
                + ImportsIdentifier.FILE_TYPE;
        Set<String> imports = importsIdentifier.extractImports(javaClassName, javaFilesPath);
        try {
          for (String imprt : imports) {
            Debugger.debug(3, "    -------- " + imprt);
            String importedFile = imprt;
            if (importedFile.startsWith("static")) {
              Debugger.debug(4, " replace static in " + importedFile);
              importedFile = importedFile.replace("static", "");
            }
            if (importedFile.endsWith(".*")) {
              Debugger.debug(4, " replace .* in " + importedFile);
              importedFile = importedFile.replace(".*", "");
            }
            Debugger.debug(" <- " + imprt + "(" + importedFile + ")");
            if (isTypeToBeConnected(javaClass, importedFile)) {
              output.println(javaClass.getClassName()
                      + " ..o " + importedFile);
            }
          }
        } catch (Exception ex) {
          System.err.println("generateImports : " + ex.getMessage());
        }
        output.println();
      });
    } else {
      Debugger.debug(3,
              "generateImports() --- Not generated because javaFilesPath not provided");
    }
    output.println();
  }

  private void generateImplements(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateImplements() ------------------");
    output.println("' IMPLEMENT INTERFACE =======");
    classes.forEach(javaClass -> {
      try {
        for (JavaClass javaInterface : javaClass.getInterfaces()) {
          output.println(javaInterface.getClassName() + " <|.. "
                  + javaClass.getClassName());
        }
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }
    });
    output.println();
  }

  private void generateInheritances(final ArrayList<JavaClass> classes) {
    Debugger.debug(2, "generateInheritances() ------------------");
    output.println("' INHERITANCES =======");
    classes.forEach(javaClass -> {
      try {
        if (!"java.lang.Object".equals(
                javaClass.getSuperClass().getClassName())) {
          output.println(javaClass.getSuperClass().getClassName() + " <|-- "
                  + javaClass.getClassName());
        }
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }
    });
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
      cex.printStackTrace();
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
    classes.forEach(javaClass -> {
      if (javaClass.isEnum()) {
        generateEnum(javaClass, classLoader);
      } else if (javaClass.isInterface()) {
        output.println("interface " + javaClass.getClassName());
      } else if (javaClass.isAbstract()) {
        output.println("abstract " + javaClass.getClassName());
      } else {
        output.println("class " + javaClass.getClassName());
      }
    });
    output.println();
  }

  private void generateFooter() {
    Debugger.debug(2, "generateFooter() ------------------");
    output.println();
    output.println("@enduml");
  }

  private void generateHeader(final String path,
                              final String configurationFile,
                              String javaFilesPath) {
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
    String includeFileContent = Utils.readFileToString(includeFileName);
    if (!includeFileContent.isBlank()) {
      output.println(includeFileContent);
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
    cleanLocalVars();

    boolean initiated = loadConfiguration(configurationFile);
    if (!initiated) {
      System.err.println("Exclusion config not loaded");
      return;
    }

    ArrayList<String> files = new ArrayList<>();
    ClassLoader classLoader = getClassLoader(path);
    if (null == classLoader) {
      System.err.println("Class loader not created");
      return;
    }

    try {
      Set<String> dirs = Utils.listDirectories(path);
      dirs.forEach(dir -> files.addAll(iterateSubDirectories(path, dir)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);

      ArrayList<JavaClass> classes = new ArrayList<>();

      files.forEach(file -> {
        try {
          JavaClass javaClass = rep.loadClass(file);
          classes.add(javaClass);
        } catch (ClassNotFoundException e) {
          System.err.println(e.getMessage());
        }
      });

      generateHeader(path, configurationFile, javaFilesPath);
      generateClasses(classes, classLoader);
      generateInheritances(classes);
      generateImplements(classes);
      generateFields(classes);
      generateUses(classes);
      generateImports(classes, javaFilesPath);
      generateFooter();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
