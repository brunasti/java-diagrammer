/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ImportsIdentifier {

  public static final String FILE_TYPE = ".java";

  public Set<String> extractImports(String path, String sysPath) {
    ClassDiagrammerMain.debug(4, "ImportsIdentifier.extractImports : " + path + " - " + sysPath);

    Set<String> importFiles = new HashSet<>();
    // TODO : Could even extract packages....
    //    Set<String> packages = new HashSet<>();

    try {
      JavaProjectBuilder jp = new JavaProjectBuilder();
      jp.addSource(new FileReader(path));

      Collection<JavaSource> srcs = jp.getSources();

      for (JavaSource src : srcs) {
        ClassDiagrammerMain.debug(8, "ImportsIdentifier.extractImports : src " + src);
        //        packages.add(src.getPackage().toString());

        for (String imprt : src.getImports()) {
          ClassDiagrammerMain.debug(5, "ImportsIdentifier.extractImports : imprt " + imprt);
          // TODO: could exclude imports with a test like : if (imprt.startsWith("")) {
          try {
            if (importFiles.contains(imprt)) {
              continue;
            }
            importFiles.add(imprt);
            String newImprt = sysPath + imprt.replaceAll("\\.", "/") + FILE_TYPE;
            ClassDiagrammerMain.debug(6, "ImportsIdentifier.extractImports : newImprt " + newImprt);
            extractImports(newImprt, sysPath);
          } catch (Exception ex) {
            ClassDiagrammerMain.debug(4, "  Error ImportsIdentifier.extractImports : "
                    + ex.getMessage() + " = " + path + " - " + imprt);
          }
        }
      }
    } catch (Exception ex) {
      ClassDiagrammerMain.debug(3, "  Error ImportsIdentifier.extractImports : "
              + ex.getMessage() + " = " + path);
    }
    return importFiles;
  }
}