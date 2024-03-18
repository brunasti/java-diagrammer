/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
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
  // TODO: Manage arrays (LString...)

  // Reference to a PrintStream to bre used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  // Lists of the packages and classes to be excluded in the diagram
  private HashSet<String> toBeExcludedPackages = null;
  private HashSet<String> toBeExcludedClasses = null;

  private String configurationFileName = "";

  // List of already written Uses, to avoid multiple connections
  private HashSet<String> usesWritten = new HashSet<>();

  private ClassLoader classLoader = null;


  public ClassDiagrammer() {
    this.output = System.out;
  }

  public ClassDiagrammer(PrintStream output) {
    this.output = output;
  }

  private void setClassLoader(final String path) {
    try {
      File file = new File(path);

      // Convert File to a URL
      URL url = file.toURI().toURL();
      URL[] urls = new URL[]{url};

      // Create a new class loader with the directory
      classLoader = new URLClassLoader(urls);
    } catch (IOException e) {
      e.printStackTrace();
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

      dirs.forEach(dir -> {
        iterateSubDirectories(path, localPackage + "." + dir, files);
      });

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
      Iterator iterator = classes.iterator();
      while (iterator.hasNext()) {
        JSONObject object = (JSONObject) iterator.next();
        Main.debug("  - excludeClass JSONObject [" + object + "]");
        String excludeClass = object.get("class").toString();
        Main.debug("  - excludeClass [" + excludeClass + "]");
        toBeExcludedClasses.add(excludeClass);
      }

      JSONArray packages = (JSONArray) exclude.get("packages");
      iterator = packages.iterator();
      while (iterator.hasNext()) {
        JSONObject object = (JSONObject) iterator.next();
        Main.debug("  - excludePackage JSONObject [" + object + "]");
        String excludePackage = object.get("package").toString();
        Main.debug("  - excludePackage [" + excludePackage + "]");
        toBeExcludedPackages.add(excludePackage);
      }
    }
    return true;
  }

  private boolean isTypeToBeConnected(final JavaClass objectClazz,
                                      final Field field) {
    String type = field.getType().toString();
    return isTypeToBeConnected(objectClazz, type);
  }

  private boolean isTypeToBeConnected(final JavaClass objectClazz,
                                      final String type) {
    Main.debug("isTypeToBeConnected " + objectClazz.getClassName() + " to " + type);

    // Avoid self referencing loops
    if (type.equals(objectClazz.getClassName())) {
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

  private void writeUses(final JavaClass objectClazz, final String type) {
    if (isTypeToBeConnected(objectClazz, type)) {
      String use = objectClazz.getClassName() + " ..> " + type;
      if (!usesWritten.contains(use)) {
        output.println(use);
        usesWritten.add(use);
      }
    }
  }

  private void generateUses(final ArrayList<JavaClass> classes) {
    output.println("' USES =======");
    classes.forEach(objectClazz -> {
      if (!objectClazz.isEnum()) {
        try {
          Method[] methods = objectClazz.getMethods();
          for (Method method : methods) {
            String type = method.getReturnType().toString();
            writeUses(objectClazz, type);

            Type[] arguments = method.getArgumentTypes();
            for (Type argument : arguments) {
              type = argument.getSignature()
                      .substring(1)
                      .replace("/", ".")
                      .replace(";", "");
              writeUses(objectClazz, type);
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
    classes.forEach(objectClazz -> {
      // TODO Handle enumeration "fields"
      if (!objectClazz.isEnum()) {
        try {
          Field[] fields = objectClazz.getFields();
          for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (isTypeToBeConnected(objectClazz, field)) {
              output.println(objectClazz.getClassName()
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

  private void generateImplements(final ArrayList<JavaClass> classes) {
    output.println("' IMPLEMENT INTERFACE =======");
    classes.forEach(objectClazz -> {
      try {
        JavaClass[] interfaces = objectClazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
          output.println(objectClazz.getClassName() + " ..|> "
                  + interfaces[i].getClassName());
        }
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }
    });
    output.println();
  }

  private void generateInheritances(final ArrayList<JavaClass> classes) {
    output.println("' INHERITANCES =======");
    classes.forEach(objectClazz -> {
      try {
        if (!"java.lang.Object".equals(
                objectClazz.getSuperClass().getClassName())) {
          output.println(objectClazz.getClassName() + " --|> "
                  + objectClazz.getSuperClass().getClassName());
        }
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }
    });
    output.println();
  }

  private void generateClasses(final ArrayList<JavaClass> classes) {
    output.println();
    output.println();
    output.println("' CLASSES =======");
    classes.forEach(objectClazz -> {
      if (objectClazz.isEnum()) {
        output.println("enum " + objectClazz.getClassName());
      } else if (objectClazz.isInterface()) {
        output.println("interface " + objectClazz.getClassName());
      } else if (objectClazz.isAbstract()) {
        output.println("abstract " + objectClazz.getClassName());
      } else {
        output.println("class " + objectClazz.getClassName());
      }
    });
    output.println();
  }

  private void generateFooter() {
    output.println();
    output.println("@enduml");
  }

  private void generateHeader(final String path, final String configurationFile) {
    Date now = new Date();
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' GENERATE CLASS DIAGRAM ===========");
    output.println("' Generator     : " + this.getClass().getName());
    output.println("' Path          : [" + path + "]");
    output.println("' Configuration : [" + configurationFile + "]");
    output.println("' Generated at  : " + now);
    output.println();
  }

  private void cleanLocalVars() {
    // Reset all variables to avoid conflicts in case of multiple run
    toBeExcludedPackages = null;
    toBeExcludedClasses = null;
    configurationFileName = "";
    usesWritten = new HashSet<>();
    classLoader = null;
  }

  public void generateDiagram(final String path, final String configurationFile) {
    cleanLocalVars();

    configurationFileName = configurationFile;
    boolean initiated = initToBeExcluded(configurationFileName);
    if (!initiated) {
      System.err.println("Exclusion config not loaded");
      return;
    }

    ArrayList<String> files = new ArrayList<>();

    try {
      setClassLoader(path);
      Set<String> dirs = Utils.listDirectories(path);
      dirs.forEach(dir -> {
        files.addAll(iterateSubDirectories(path, dir));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);

      ArrayList<JavaClass> classes = new ArrayList<>();

      files.forEach(file -> {
        try {
          JavaClass objectClazz = rep.loadClass(file);
          classes.add(objectClazz);
        } catch (ClassNotFoundException e) {
          System.err.println(e.getMessage());
        }
      });

      generateHeader(path, configurationFile);
      generateClasses(classes);
      generateInheritances(classes);
      generateImplements(classes);
      generateFields(classes);
      generateUses(classes);
      generateFooter();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
