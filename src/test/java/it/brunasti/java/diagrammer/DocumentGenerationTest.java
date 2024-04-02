package it.brunasti.java.diagrammer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DocumentGenerationTest implements TestConstants {

  @Test
  void generateDocumentations() {
    System.err.println("generateDocumentations");
    DiagramPlotter diagramPlotter = new DiagramPlotter();

    System.err.println("generateDocumentations - output.puml");
    String[] args = new String[2];
    args[0] = classesDirectory;
    args[1] = outputFileName;
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(args));

    System.err.println("generateDocumentations - output.png");
    assertDoesNotThrow(() -> diagramPlotter.renderFile(outputFileName, docsDirectory, "output"));


    System.err.println("generateDocumentations - output-import.puml");
    String[] fullArgs = new String[11];
    fullArgs[0] = classesDirectory;
    fullArgs[1] = outputWithImportFileName;
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = configurationFileName;
    fullArgs[5] = "-o";
    fullArgs[6] = outputWithImportFileName;
    fullArgs[7] = "-p";
    fullArgs[8] = classesDirectory;
    fullArgs[9] = "-i";
    fullArgs[10] = javaSrcDirectory;
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));

    System.err.println("generateDocumentations - output-import.png");
    assertDoesNotThrow(() -> diagramPlotter.renderFile(outputWithImportFileName, docsDirectory, "output-import"));

    System.err.println("generateDocumentations - " + testOutputFileName);
    String[] testArgs = new String[2];
    testArgs[0] = testClassesDirectory;
    testArgs[1] = testOutputFileName;
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(testArgs));

    System.err.println("generateDocumentations - output-test.png");
    assertDoesNotThrow(() -> diagramPlotter.renderFile(testOutputFileName, docsDirectory, "output-test"));
  }

  @Test
  void generateDocumentations_extras() {
    System.err.println("generateDocumentations_extras - Diagram with included file");
    String[] lastArgs = new String[6];
    lastArgs[0] = "-c";
    lastArgs[1] = configurationWithIncludeFileName;
    lastArgs[2] = "-o";
    lastArgs[3] = outputWithIncludeFileName;
    lastArgs[4] = "-p";
    lastArgs[5] = testClassesDirectory;
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(lastArgs));

  }

}