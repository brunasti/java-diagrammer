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
    assertThrows(NoSuchElementException.class, () -> diagramPlotter.renderFile("./docs/output-notExist.puml", "./temp/", "test-output"));

    System.err.println("diagramPlotterTest_renderFile - NoSuchElementException no target dir");
    assertThrows(FileNotFoundException.class, () -> diagramPlotter.renderFile("./docs/output.puml", "./xxtemp/", "test-output"));
  }

}