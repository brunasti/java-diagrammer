package it.brunasti.java.diagrammer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.brunasti.java.diagrammer.teststructure.TestBaseClass;
import org.junit.jupiter.api.Test;

import java.util.Set;

// TODO: implement the test cases for this class
class ImportsIdentifierTest implements TestConstants {

  @Test
  void extractImports_errors() {
    ImportsIdentifier importsIdentifier = new ImportsIdentifier();
    String newSysPath = testJavaSrcDirectory + '/';
    assertDoesNotThrow(() -> importsIdentifier.extractImports(javaSrcDirectory, ""));

    String javaClassName = newSysPath
            + this.getClass().getName().replace(".", "/")
            + ImportsIdentifier.FILE_TYPE;
    assertDoesNotThrow(() -> importsIdentifier.extractImports(javaClassName, newSysPath));
    Main.setDebug(10);
    Set<String> imports = importsIdentifier.extractImports(javaClassName, newSysPath);
    Main.debug("  - imports : " + imports);
    Main.debug("  - imports : " + imports.size());
    assertEquals(5, imports.size());
  }


  @Test
  void extractImports_testStructureClasses() {
    Main.setDebug(7);
    Main.debug("extractImports_testStructureClasses ---------");
    ImportsIdentifier importsIdentifier = new ImportsIdentifier();
    String className = TestBaseClass.class.getCanonicalName();
    Main.debug(" - " + className);
    String newSysPath = testJavaSrcDirectory + '/';
    String javaClassName = newSysPath
            + className.replace(".", "/")
            + ImportsIdentifier.FILE_TYPE;
    Set<String> imports = importsIdentifier.extractImports(javaClassName, newSysPath);
    Main.debug("  - imports : " + imports);
    Main.debug("  - imports : " + imports.size());
    assertEquals(1, imports.size());
  }


}