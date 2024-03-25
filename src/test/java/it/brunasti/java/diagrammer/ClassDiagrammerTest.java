package it.brunasti.java.diagrammer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassDiagrammerTest implements TestConstants {

  // Test generateDiagram function ---------------------------
  @Test
  @DisplayName("Generate Diagram from dir without classes")
  void testGenerateDiagram_Empty() {
    ClassDiagrammer classDiagrammer = new ClassDiagrammer();
    assertDoesNotThrow(() -> classDiagrammer.generateDiagram(javaSrcDirectory,configurationFileName, null));
  }

  @Test
  @DisplayName("Generate Diagram from this same system")
  void testGenerateDiagram_Successful() {
    ClassDiagrammer classDiagrammer = new ClassDiagrammer();
    assertDoesNotThrow(() -> classDiagrammer.generateDiagram(classesDirectory,configurationFileName,javaSrcDirectory));
  }

  @Test
  @DisplayName("Generate Diagram from non existing directory")
  void testGenerateDiagram_Nodir() {
    ClassDiagrammer classDiagrammer = new ClassDiagrammer();
    assertDoesNotThrow(() -> classDiagrammer.generateDiagram(nonExistingDirectory,configurationFileName, null));
  }

  @Test
  @DisplayName("Config file non existing")
  void testGenerateDiagram_NoConfig() {
    ClassDiagrammer classDiagrammer = new ClassDiagrammer();
    assertDoesNotThrow(() -> classDiagrammer.generateDiagram(classesDirectory,nonExistingJsonFileName, null));
  }

}