package it.brunasti.java.diagrammer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.brunasti.java.diagrammer.teststructure.TestBaseClass;
import it.brunasti.java.diagrammer.teststructure.TestExtendedClass;
import it.brunasti.java.diagrammer.teststructure.TestOtherClass;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ImportsIdentifierTest implements TestConstants {

  @Test
  void extractImports_success() {
    ImportsIdentifier importsIdentifier = new ImportsIdentifier();
    String newSysPath = testJavaSrcDirectory + '/';
    assertDoesNotThrow(() -> importsIdentifier.extractImports(javaSrcDirectory, ""));

    String javaClassName = newSysPath
            + this.getClass().getName().replace(".", "/")
            + ImportsIdentifier.FILE_TYPE;
    assertDoesNotThrow(() -> importsIdentifier.extractImports(javaClassName, newSysPath));
    Set<String> imports = importsIdentifier.extractImports(javaClassName, newSysPath);
    ClassDiagrammerMain.debug("  - imports : " + imports);
    ClassDiagrammerMain.debug("  - imports : " + imports.size());
    assertEquals(7, imports.size());
  }


  @Test
  void extractImports_testStructureClasses() {
    ClassDiagrammerMain.debug("extractImports_testStructureClasses ---------");
    ImportsIdentifier importsIdentifier = new ImportsIdentifier();
    String className = TestBaseClass.class.getCanonicalName();
    ClassDiagrammerMain.debug(" - " + className);
    String newSysPath = testJavaSrcDirectory + '/';
    String javaClassName = newSysPath
            + className.replace(".", "/")
            + ImportsIdentifier.FILE_TYPE;
    Set<String> imports = importsIdentifier.extractImports(javaClassName, newSysPath);
    ClassDiagrammerMain.debug("   - imports : " + imports);
    ClassDiagrammerMain.debug("   - imports : " + imports.size());
    assertEquals(1, imports.size());

    className = TestOtherClass.class.getCanonicalName();
    ClassDiagrammerMain.debug(" - " + className);
    javaClassName = newSysPath
            + className.replace(".", "/")
            + ImportsIdentifier.FILE_TYPE;
    imports = importsIdentifier.extractImports(javaClassName, newSysPath);
    ClassDiagrammerMain.debug("   - imports : " + imports);
    ClassDiagrammerMain.debug("   - imports : " + imports.size());
    assertEquals(3, imports.size());

    className = TestExtendedClass.class.getCanonicalName();
    javaClassName = newSysPath
            + className.replace(".", "/")
            + ImportsIdentifier.FILE_TYPE;
    imports = importsIdentifier.extractImports(javaClassName, newSysPath);
    assertEquals(0, imports.size());
  }


}