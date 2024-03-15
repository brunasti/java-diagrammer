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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassLoaderRepository;

/**
 *
 *
 */
public class ClassDiagrammer {

  private static PrintStream output = null;

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

  private ClassLoader classLoader = null;

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


  private static HashSet<String> toBeExcludedPackages = null;
  private static HashSet<String> toBeExcludedClasses = null;

  private void initToBeExcluded() {
    // TODO Load from config file
    toBeExcludedPackages = new HashSet<>();
    toBeExcludedClasses = new HashSet<>();

    toBeExcludedPackages.add("java.lang.");
    toBeExcludedPackages.add("java.util.");

    toBeExcludedClasses.add("org.slf4j.Logger");
    toBeExcludedClasses.add("org.joda.time.DateTime");
  }

  private boolean isTypeToBeConnected(final JavaClass objectClazz,
                                      final Field field) {
    String type = field.getType().toString();
    return isTypeToBeConnected(objectClazz, type);
  }

  private boolean isTypeToBeConnected(final JavaClass objectClazz,
                                      final String type) {
    if (toBeExcludedPackages == null) {
      initToBeExcluded();
    }

    if (type.equals(objectClazz.getClassName())) {
      return false;
    }

    // If the type is all lowercase, then is a primitive type
    if (type.toLowerCase().equals(type)) {
      return false;
    }

    if (toBeExcludedClasses.contains(type)) {
      return false;
    }

    AtomicReference<Boolean> excludedPackage = new AtomicReference<>(false);
    toBeExcludedPackages.forEach(localPackage -> {
      if (type.startsWith(localPackage)) {
        excludedPackage.set(true);
      }
    });

    if (excludedPackage.get()) {
      return false;
    }

    return true;
  }

  private final HashSet<String> usesWritten = new HashSet<>();

  private void writeUses(final JavaClass objectClazz, final String type) {
    if (isTypeToBeConnected(objectClazz, type)) {
      String use = objectClazz.getClassName() + " ..> " + type;
      if (!usesWritten.contains(use)) {
        output.println(use);
        usesWritten.add(use);
      }
    }

  }

  // TODO : too complex or too long, decompose in sub functions
  private void generateDiagram(final String path) {
    // TODO : Replace System.out with flexible stream,
    //  so that the file name can be given as input parameter to main
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

    Date now = new Date();
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' GENERATE CLASS DIAGRAM ===========");
    output.println("' Generator    : " + this.getClass().getName());
    output.println("' Path         : " + path);
    output.println("' Generated at : " + now);
    output.println();
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

      output.println();
      output.println();
      output.println("@enduml");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // TODO : Add CLI as in it/brunasti/engine/inferential/Main.java
  public static void main(final String[] args) {

    // TODO : Read the path from args
//    String path = "/Users/paolobrunasti/Work/Mine/java-diagrammer"
//            + "/java-diagrammer/target/classes";

//    String path = "/Users/paolobrunasti/Work/BAH/bah-solr-api-springboot/build/classes/java/main";
    String path = "/Users/paolobrunasti/Work/Mine/java_tools/target/classes";

    String outputFile = null;
//    String outputFile = "./temp/output.puml";

    FileOutputStream file = null;

    if (null != outputFile) {
      try {
        // Creates a FileOutputStream
        file = new FileOutputStream(outputFile);

        // Creates a PrintWriter
        output = new PrintStream(file, true);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else {
      output = System.out;
    }

    ClassDiagrammer classDiagrammer = new ClassDiagrammer();
    classDiagrammer.generateDiagram(path);

    if (null != file) {
      try {
        file.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

}
