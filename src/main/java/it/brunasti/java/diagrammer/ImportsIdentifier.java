/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;

public class ImportsIdentifier {

//  private static String sysPath ="/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/src/main/java/";
  private static String sysPath ="/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/src/test/java/";
  private static String fileType = ".java";
  private static Set<String> importFiles = new HashSet<>();
  private static Set<String> packages = new HashSet<>();

  public static void main(String[] args) throws FileNotFoundException {
    String className = "it/brunasti/java/diagrammer/ClassDiagrammerTest";
    String path = sysPath + className + fileType;

    Set<String> imports = printImports(path);
//    System.out.println("Import: " + importFiles);

    for (String imp : imports) {
      System.out.println(" - : " + imp);
    }

//    System.out.println("Packages: " + packages);
  }

  private static Set<String> printImports(String path) throws FileNotFoundException {
//    System.out.println("printImports - path " + path);
    JavaProjectBuilder jp = new JavaProjectBuilder();
    try {
      jp.addSource(new FileReader(path));

      Collection<JavaSource> srcs = jp.getSources();

      for (JavaSource src : srcs) {
//        System.out.println("printImports - Package " + src.getPackage());
        packages.add(src.getPackage().toString());

        for (String imprt : src.getImports()) {
          if (imprt.startsWith("")) {

            try {
              if (importFiles.contains(imprt)) {
                continue;
              }
              importFiles.add(imprt);
//              System.out.println("  - File " + imprt);

              imprt = sysPath + imprt.replaceAll("\\.", "/") + fileType;
              printImports(imprt);
            } catch (Exception ex) {
              System.err.println("  Error printImports : " + path + " = " + ex.getMessage());
            }
          }
        }
      }
    } catch (Exception ex) {
      System.err.println("  Error : " + path + " = " + ex.getMessage());
    }
    return importFiles;
  }

}