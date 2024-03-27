/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import java.io.*;
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
 *
 *
 */
public class ClassDiagrammer {
  // TODO: Add import connections (for "closed" classes) as a flag
  //   with the specification of the source code directory (where the Java files are...)
  // TODO: add flag to avoid or not the self reflection
  // TODO: Manage arrays (LString...)

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

      dirs.forEach(dir ->
        iterateSubDirectories(path, localPackage + "." + dir, files)
      );

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
      toBeExcludedPackages.add("java.lang.");
      toBeExcludedPackages.add("java.util.");

      toBeExcludedClasses.add("org.slf4j.Logger");
      toBeExcludedClasses.add("org.joda.time.DateTime");
    } else {
      JSONObject jsonObject = Utils.loadConfigurationFile(configurationFileName);
      if (null == jsonObject) {
        Main.debug("initToBeExcluded : no data in config file " + configurationFileName);
        return false;
      }
      JSONObject exclude = (JSONObject) jsonObject.get("exclude");

      JSONArray classes = (JSONArray) exclude.get("classes");
      Iterator<JSONObject> iterator = (Iterator<JSONObject>)classes.iterator();
      while (iterator.hasNext()) {
        JSONObject object = iterator.next();
        Main.debug("  - excludeClass JSONObject [" + object + "]");
        String excludeClass = object.get("class").toString();
        Main.debug("  - excludeClass [" + excludeClass + "]");
        toBeExcludedClasses.add(excludeClass);
      }

      JSONArray packages = (JSONArray) exclude.get("packages");
      iterator = (Iterator<JSONObject>)packages.iterator();
      while (iterator.hasNext()) {
        JSONObject object = iterator.next();
        Main.debug("  - excludePackage JSONObject [" + object + "]");
        String excludePackage = object.get("package").toString();
        Main.debug("  - excludePackage [" + excludePackage + "]");
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

    // Avoid self referencing loops
    if (type.equals(javaClass.getClassName())) {
      Main.debug("  - self ref");
      return false;
    }

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
      Main.debug("      - package : " + localPackage);
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
    if ((null != javaFilesPath) && (!javaFilesPath.isBlank())) {

      output.println("' IMPORTS =======");
      output.println("' Java Files Path : " + javaFilesPath);
      classes.forEach(javaClass -> {
        output.println("' " + javaClass.getClassName());
        String javaClassName = javaFilesPath + javaClass.getClassName().replace(".", "/") + ImportsIdentifier.FILE_TYPE;
        Set<String> imports = ImportsIdentifier.extractImports(javaClassName, javaFilesPath);
        try {
          for (String imprt : imports) {
            String importedFile = imprt;
            if (importedFile.startsWith("static")) {
              Main.debug("' replace static in " + importedFile);
              importedFile = importedFile.replace("static", "");
            }
            if (importedFile.endsWith(".*")) {
              Main.debug("' replace .* in " + importedFile);
              importedFile = importedFile.replace(".*", "");
            }
            Main.debug("' <- " + imprt + "(" + importedFile + ")");
            if (isTypeToBeConnected(javaClass, importedFile)) {
              output.println(javaClass.getClassName()
                      + " ..o " + importedFile);
            }
          }
        } catch (Exception ex) {
          System.err.println("generateImports : "+ ex.getMessage());
        }
        output.println();
      });
      output.println();
    }
  }

  private void generateImplements(final ArrayList<JavaClass> classes) {
    output.println("' IMPLEMENT INTERFACE =======");
    classes.forEach(javaClass -> {
      try {
        for (JavaClass javaInterface : javaClass.getInterfaces()) {
          output.println( javaInterface.getClassName() + " <|.. "
                  + javaClass.getClassName());
        }
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }
    });
    output.println();
  }

  private void generateInheritances(final ArrayList<JavaClass> classes) {
    output.println("' INHERITANCES =======");
    classes.forEach(javaClass -> {
      try {
        if (!"java.lang.Object".equals(
                javaClass.getSuperClass().getClassName())) {
          output.println( javaClass.getSuperClass().getClassName() + " <|-- "
                  + javaClass.getClassName());
        }
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }
    });
    output.println();
  }

  private void generateEnum(JavaClass javaClass, ClassLoader classLoader) {
    // TODO Handle enumeration "fields"
    Main.debug("ENUM : " + javaClass.getClassName());
    String[] enumFields = null;

    try {
      Class loadedClass = classLoader.loadClass(javaClass.getClassName());
//      Main.debug("  - o : " + loadedClass);
//      Main.debug("  - o : " + loadedClass.isEnum());

//      Main.debug("  - o.f : " + loadedClass.getFields());
//      Main.debug("  - o.f : " + loadedClass.getFields().length);
      enumFields = new String[loadedClass.getFields().length];

      int i = 0;
      for (java.lang.reflect.Field field : loadedClass.getFields()) {
//        Main.debug("      - o.Field : " + field);
//        Main.debug("      - o.Name : " + field.getName());
        enumFields[i] = field.getName();
        i++;
      }

//      Main.debug("  - o.ec : " + loadedClass.getEnumConstants());
//      Main.debug("  - o.ec : " + loadedClass.getEnumConstants().length);
//      for (Object field : loadedClass.getEnumConstants()) {
//        Main.debug("      - o.ec.object : " + field);
////            Main.debug("      - o.Name : " + field.getName());
//      }
//      Main.debug("  - o.m : " + loadedClass.getDeclaredMethods());
//      Main.debug("  - o.m : " + loadedClass.getDeclaredMethods().length);
//      for (Object field : loadedClass.getDeclaredMethods()) {
//        Main.debug("      - o.m .method: " + field);
////            Main.debug("      - o.Name : " + field.getName());
//      }
//
//      try {
//        Main.debug("      - o.m .values : ");
//        java.lang.reflect.Method valuesMethod = loadedClass.getMethod("values");
//        Main.debug("      - o.m .valuesMethod : " + valuesMethod);
//        Object[] values = (Object[])valuesMethod.invoke(loadedClass); // pass arg
//        Main.debug("      - o.m .values : " + values);
//        Main.debug("      - o.m .values : " + values.length);
//        for (Object value : values) {
//          Main.debug("      - o.m .value: " + value);
//        }
//
//
//        Main.debug("      - o.e : ");
//        try {
//          java.lang.reflect.Method m = loadedClass.getMethod("values", null);
//          Main.debug("   - invoke " + m.invoke(loadedClass, null));
//          Object[] objects = (Object[]) m.invoke(loadedClass, null);
//          Main.debug("   - invoke objects " + objects);
//          Main.debug("   - invoke objects " + objects.length);
//          for(Object obj : objects) {
//            Main.debug("   - invoke - " + obj);
//          }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
//          Main.debug("could not find enum");
//        }
//
//        Main.debug("      - o.e 2 : ");
//        Object[] objects = loadedClass.getEnumConstants();
//// now this is not what I want, but almost
//        for(Object obj : objects) {
//          try {
//            java.lang.reflect.Field keyField = obj.getClass().getDeclaredField("value");
//            keyField.setAccessible(true); // if it is private for example.
//            Main.debug("value : " + keyField.get(obj));
//          } catch (NoSuchFieldException e) {
//            // fallback to toString()
//            Main.debug("value : " + obj);
//          }
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//
    } catch (Exception ex) {
      ex.printStackTrace();
    }
//
//    Main.debug("  - Fields : " + javaClass.getFields());
//    for (Field field : javaClass.getFields()) {
//      Main.debug("    - Field : " + field);
//      Main.debug("    - Sign : " + field.getSignature());
//      Main.debug("    - Name : " + field.getName());
//    }
//    Main.debug("  - constant : " + javaClass.getConstantPool().getConstantPool());
//    Main.debug("  - constant : " + javaClass.getConstantPool().getConstantPool().length);
//    for (Constant c : javaClass.getConstantPool().getConstantPool()) {
//      Main.debug("      - Const : " + c);
//    }
//    Main.debug("  - methods : " + javaClass.getMethods());
//    Main.debug("  - methods : " + javaClass.getMethods().length);
//    for (Method c : javaClass.getMethods()) {
//      Main.debug("      - method : " + c);
//    }

    output.println("enum " + javaClass.getClassName() + "{");
    for (String enumValue : enumFields) {
      output.println("  "+enumValue);
    }
    output.println("}");    
  }
  
  private void generateClasses(final ArrayList<JavaClass> classes, ClassLoader classLoader) {
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

//    Main.setDebug(false);
  }

  private void generateFooter() {
    output.println();
    output.println("@enduml");
  }

  private void generateHeader(final String path, final String configurationFile, String javaFilesPath) {
    Date now = new Date();
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
    output.println("' Generated at    : " + now);
    output.println();
  }

  private void cleanLocalVars() {
    // Reset all variables to avoid conflicts in case of multiple run
    toBeExcludedPackages = null;
    toBeExcludedClasses = null;
    configurationFileName = "";
    usesWritten = new HashSet<>();
  }

  public void generateDiagram(final String path, final String configurationFile, String javaFilesPath) {
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
      dirs.forEach(dir ->
        files.addAll(iterateSubDirectories(path, dir))
      );
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
