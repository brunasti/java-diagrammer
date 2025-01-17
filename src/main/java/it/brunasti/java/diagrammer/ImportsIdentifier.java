/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Analyse a Java file and determinate the imported Java sources and Packages.
 */
public class ImportsIdentifier {

  public static final String FILE_TYPE = ".java";

  private Set<String> exploredPaths = new HashSet<>();

  /**
   * Extract the list of imported Java sources and Packages of a Java class source.
   *
   * @param path Java class source file.
   * @param sysPath Path to the Java source files.
   * @return Set (list) of imported Java sources or Packages.
   */
  public Set<String> extractImports(String path, String sysPath) {
    Debugger.debug(4, "ImportsIdentifier.extractImports : " + path + " - " + sysPath);

    Set<String> importFiles = new HashSet<>();

    String key = path+"#"+sysPath;
    if (exploredPaths.contains(key)) {
      Debugger.debug(4, "ImportsIdentifier.extractImports : ALREADY EXPLORED " + path + " - " + sysPath);
      return importFiles;
    }

    exploredPaths.add(key);

    // TODO : Could even extract packages....
    //    Set<String> packages = new HashSet<>();
    try {
      Debugger.debug(0, "ImportsIdentifier.extractImports : path " + path);
      JavaProjectBuilder jp = new JavaProjectBuilder();
      jp.addSource(new FileReader(path));

      Collection<JavaSource> srcs = jp.getSources();

      for (JavaSource src : srcs) {
//        Debugger.debug(8, "ImportsIdentifier.extractImports : src " + src);

        for (String importName : src.getImports()) {
          Debugger.debug(5, "ImportsIdentifier.extractImports : importName " + importName);
          // TODO: could exclude imports with a test like : if (importName.startsWith("")) {
          if (importFiles.contains(importName)) {
            continue;
          }
          importFiles.add(importName);
          String newImport = sysPath + importName.replaceAll("\\.", "/") + FILE_TYPE;
          Debugger.debug(6, "ImportsIdentifier.extractImports : newImport " + newImport);
          extractImports(newImport, sysPath);
        }
      }
    } catch (FileNotFoundException ex) {
      Debugger.debug(3, "  Error FileNotFoundException ImportsIdentifier.extractImports : "
              + ex.getMessage() + " = " + path);
    } catch (ParseException ex) {
      Debugger.debug(3, "  Error ParseException ImportsIdentifier.extractImports : "
              + ex.getMessage() + " = " + path);
    }
    return importFiles;
  }
}