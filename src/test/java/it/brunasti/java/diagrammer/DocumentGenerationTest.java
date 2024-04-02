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
    assertDoesNotThrow(() -> Main.main(args));

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
    assertDoesNotThrow(() -> Main.main(fullArgs));

    System.err.println("generateDocumentations - output-import.png");
    assertDoesNotThrow(() -> diagramPlotter.renderFile(outputWithImportFileName, docsDirectory, "output-import"));

    System.err.println("generateDocumentations - "+testOutputFileName);
    String[] testArgs = new String[2];
    testArgs[0] = testClassesDirectory;
    testArgs[1] = testOutputFileName;
    assertDoesNotThrow(() -> Main.main(testArgs));

    System.err.println("generateDocumentations - output-test.png");
    assertDoesNotThrow(() -> diagramPlotter.renderFile(testOutputFileName, docsDirectory, "output-test"));

  }

}