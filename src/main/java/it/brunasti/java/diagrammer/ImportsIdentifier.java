/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;

public class ImportsIdentifier {

  public static final String FILE_TYPE = ".java";

  public static Set<String> extractImports(String path, String sysPath) {
    Set<String> importFiles = new HashSet<>();
    // TODO : Could even extract packages....
    //    Set<String> packages = new HashSet<>();

    try {
      JavaProjectBuilder jp = new JavaProjectBuilder();
      jp.addSource(new FileReader(path));

      Collection<JavaSource> srcs = jp.getSources();

      for (JavaSource src : srcs) {
        //        packages.add(src.getPackage().toString());

        for (String imprt : src.getImports()) {
          // TODO: could exclude imports with a test like : if (imprt.startsWith("")) {
          try {
            if (importFiles.contains(imprt)) {
              continue;
            }
            importFiles.add(imprt);
            imprt = sysPath + imprt.replaceAll("\\.", "/") + FILE_TYPE;
            extractImports(imprt, sysPath);
          } catch (Exception ex) {
            Main.debug(4, "  Error ImportsIdentifier.extractImports : " + ex.getMessage() + " = " + path + " - " + imprt);
          }
        }
      }
    } catch (Exception ex) {
      Main.debug(3, "  Error ImportsIdentifier.extractImports : " + ex.getMessage() + " = " + path);
    }
    return importFiles;
  }
}