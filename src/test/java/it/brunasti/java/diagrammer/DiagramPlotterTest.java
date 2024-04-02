package it.brunasti.java.diagrammer;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DiagramPlotterTest {

  @Test
  void diagramPlotterTest_renderFile() {
    System.err.println("diagramPlotterTest_renderFile");
    DiagramPlotter diagramPlotter = new DiagramPlotter();

    System.err.println("diagramPlotterTest_renderFile - success");
    assertDoesNotThrow(() -> diagramPlotter.renderFile("./docs/output.puml", "./temp/", "test-output"));

    System.err.println("diagramPlotterTest_renderFile - NoSuchElementException inputFile");
    Debugger.setDebug(10);
    assertThrows(NoSuchElementException.class, () -> diagramPlotter.renderFile("./docs/output-notExist.puml", "./temp/", "xxx-test-output"));
    Debugger.setDebug(false);

    System.err.println("diagramPlotterTest_renderFile - NoSuchElementException no target dir");
    assertThrows(FileNotFoundException.class, () -> diagramPlotter.renderFile("./docs/output.puml", "./xxtemp/", "test-output"));
  }

  @Test
  void diagramPlotterTest_generateDocumentations() {
    System.err.println("diagramPlotterTest_generateDocumentations");
    DiagramPlotter diagramPlotter = new DiagramPlotter();

    System.err.println("diagramPlotterTest_generateDocumentations - output.puml");
    assertDoesNotThrow(() -> diagramPlotter.renderFile("./docs/output.puml", "./docs/", "output"));

  }

}