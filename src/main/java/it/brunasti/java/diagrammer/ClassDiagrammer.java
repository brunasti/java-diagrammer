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
 *
 *
 */
public class ClassDiagrammer {
  // TODO: Move files from temp directory to more appropriate ones
  // TODO: Manage arrays (LString...)
  // TODO: Add the option to extract Methods and Attributes, Protected Private or Abstract (MA-PPA)
  // TODO: Avoid generic catch(Exception)
  // TODO: option to add "hide empty members" in diagram
  // TODO: add Javadoc comments

  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  // Lists of the packages and classes to be excluded in the diagram
  private HashSet<String> toBeExcludedPackages = null;
  private HashSet<String> toBeExcludedClasses = null;

  private String configurationFileName = "";

  // List of already written Uses, to avoid multiple connections
  private HashSet<String> usesWritten = new HashSet<>();

  public ClassDiagrammer() {
    this.output = System.out;
  }

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


  private boolean initToBeExcluded(String configurationFileName) {
    toBeExcludedPackages = new HashSet<>();
    toBeExcludedClasses = new HashSet<>();
    if ((null == configurationFileName) || (configurationFileName.isBlank())) {
      toBeExcludedPackages.add("Ljava.lang.");
      toBeExcludedPackages.add("java.lang.");
    } else {
      JSONObject jsonObject = Utils.loadConfigurationFile(configurationFileName);
      if (null == jsonObject) {
        Main.debug(2, "initToBeExcluded : no data in config file " + configurationFileName);
        return false;
      }
      JSONObject exclude = (JSONObject) jsonObject.get("exclude");

      JSONArray classes = (JSONArray) exclude.get("classes");
      Iterator<JSONObject> iterator = (Iterator<JSONObject>) classes.iterator();
      while (iterator.hasNext()) {
        JSONObject object = iterator.next();
        Main.debug(2, "  - excludeClass JSONObject [" + object + "]");
        String excludeClass = object.get("class").toString();
        Main.debug(2, "  - excludeClass [" + excludeClass + "]");
        toBeExcludedClasses.add(excludeClass);
      }

      JSONArray packages = (JSONArray) exclude.get("packages");
      iterator = (Iterator<JSONObject>) packages.iterator();
      while (iterator.hasNext()) {
        JSONObject object = iterator.next();
        Main.debug(2, "  - excludePackage JSONObject [" + object + "]");
        String excludePackage = object.get("package").toString();
        Main.debug(2, "  - excludePackage [" + excludePackage + "]");
        toBeExcludedPackages.add(excludePackage);
      }
    }
    return true;
  }

  private boolean isTypeToBeConnected(final JavaClass javaClass,
                                      final Field field) {
    String type = field.getType().toString();
    return isTypeToBeConnected(javaClass, type);
  }

  private boolean isTypeToBeConnected(final JavaClass javaClass,
                                      final String type) {
    Main.debug("isTypeToBeConnected " + javaClass.getClassName() + " to " + type);

    // TODO: add flag to avoid or not the self reflection
    //    // Avoid self referencing loops
    //    if (type.equals(javaClass.getClassName())) {
    //      Main.debug("  - self ref");
    //      return false;
    //    }

    // If the type is all lowercase, then is a primitive type
    if (type.toLowerCase().equals(type)) {
      Main.debug("  - primitive");
      return false;
    }

    if (toBeExcludedClasses.contains(type)) {
      Main.debug("  - exclude class");
      return false;
    }

    AtomicReference<Boolean> excludedPackage = new AtomicReference<>(false);
    toBeExcludedPackages.forEach(localPackage -> {
      Main.debug(4, "      - package : " + localPackage);
      if (type.startsWith(localPackage)) {
        Main.debug("    -> exclude package " + type);
        excludedPackage.set(true);
      }
    });
    Main.debug("  - exclude package : " + excludedPackage.get());

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
    Main.debug(2, "generateUses() ------------------");
    output.println("' USES =======");
    classes.forEach(javaClass -> {
      if (!javaClass.isEnum()) {
        try {
          Method[] methods = javaClass.getMethods();
          for (Method method : methods) {
            Main.debug("generateUses " + method.getName() + " : " + method.getSignature());
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
    Main.debug(2, "generateFields() ------------------");
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
    Main.debug(2, "generateImports() ------------------");
    if ((null != javaFilesPath) && (!javaFilesPath.isBlank())) {

      output.println("' IMPORTS =======");
      output.println("' Java Files Path : " + javaFilesPath);
      classes.forEach(javaClass -> {
        Main.debug(3, "  ---------- " + javaClass.getClassName());
        output.println("' " + javaClass.getClassName());
        String javaClassName = javaFilesPath
                + javaClass.getClassName().replace(".", "/")
                + ImportsIdentifier.FILE_TYPE;
        Set<String> imports = ImportsIdentifier.extractImports(javaClassName, javaFilesPath);
        try {
          for (String imprt : imports) {
            Main.debug(3, "    -------- " + imprt);
            String importedFile = imprt;
            if (importedFile.startsWith("static")) {
              Main.debug(4, " replace static in " + importedFile);
              importedFile = importedFile.replace("static", "");
            }
            if (importedFile.endsWith(".*")) {
              Main.debug(4, " replace .* in " + importedFile);
              importedFile = importedFile.replace(".*", "");
            }
            Main.debug(" <- " + imprt + "(" + importedFile + ")");
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
      output.println();
    }
  }

  private void generateImplements(final ArrayList<JavaClass> classes) {
    Main.debug(2, "generateImplements() ------------------");
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
    Main.debug(2, "generateInheritances() ------------------");
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
    Main.debug("ENUM : " + javaClass.getClassName());
    String[] enumFields = new String[0];

    try {
      Class loadedClass = classLoader.loadClass(javaClass.getClassName());
      Main.debug(4, "  - loadedClass : " + loadedClass);
      Main.debug(4, "  - loadedClass : " + loadedClass.isEnum());

      enumFields = new String[loadedClass.getFields().length];
      int i = 0;
      for (java.lang.reflect.Field field : loadedClass.getFields()) {
        Main.debug(4, "      - loadedClass.Field : " + field);
        Main.debug(4, "      - loadedClass.Field.Name : " + field.getName());
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
    Main.debug(2, "generateClasses() ------------------");
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
    Main.debug(2, "generateFooter() ------------------");
    output.println();
    output.println("@enduml");
  }

  private void generateHeader(final String path,
                              final String configurationFile,
                              String javaFilesPath) {
    Main.debug(2, "generateHeader() ------------------");
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
    // TODO : Add legend for the types of links - specified by CLI param
    String legendFileContent = Utils.readFileToString("./temp/default_legend.txt");
    if (!legendFileContent.isBlank()) {
      output.println("legend");
      output.println(legendFileContent);
      output.println("end legend");
    }
    output.println();
  }

  private void cleanLocalVars() {
    // Reset all variables to avoid conflicts in case of multiple run
    toBeExcludedPackages = null;
    toBeExcludedClasses = null;
    configurationFileName = "";
    usesWritten = new HashSet<>();
  }

  public void generateDiagram(final String path,
                              final String configurationFile,
                              final String javaFilesPath) {
    cleanLocalVars();

    configurationFileName = configurationFile;
    boolean initiated = initToBeExcluded(configurationFileName);
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
