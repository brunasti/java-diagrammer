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

  public static final String FILE_TYPE = ".java";

  private static final String mySysPath ="/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/src/test/java/";
  private static Set<String> packages = new HashSet<>();

  public static void main(String[] args) throws FileNotFoundException {
    String className = "it/brunasti/java/diagrammer/ClassDiagrammerTest";
    String path = mySysPath + className + FILE_TYPE;

    Set<String> imports = extractImports(path, mySysPath);

    System.out.println("Imports : ");
    for (String imp : imports) {
      System.out.println(" - : " + imp);
    }
    System.out.println("Packages : ");
    for (String pack : packages) {
      System.out.println(" - : " + pack);
    }
  }

  public static Set<String> extractImports(String path, String sysPath) {
    Set<String> importFiles = new HashSet<>();

    try {
      JavaProjectBuilder jp = new JavaProjectBuilder();
      jp.addSource(new FileReader(path));

      Collection<JavaSource> srcs = jp.getSources();

      for (JavaSource src : srcs) {
        packages.add(src.getPackage().toString());

        for (String imprt : src.getImports()) {
//          if (imprt.startsWith("")) {
            try {
              if (importFiles.contains(imprt)) {
                continue;
              }
              importFiles.add(imprt);
              imprt = sysPath + imprt.replaceAll("\\.", "/") + FILE_TYPE;
              extractImports(imprt, sysPath);
            } catch (Exception ex) {
              Main.debug("  Error printImports : " + path + " = " + ex.getMessage());
            }
//          }
        }
      }
    } catch (Exception ex) {
      Main.debug("  Error : " + path + " = " + ex.getMessage());
    }
    return importFiles;
  }
}