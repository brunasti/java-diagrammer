package it.brunasti.java.diagrammer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.sourceforge.plantuml.FileFormat.PNG;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FileImageData;
import net.sourceforge.plantuml.PSystemUtils;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.file.SuggestedFile;
import net.sourceforge.plantuml.preproc.Defines;

/**
 * Functions to plot an image of a diagram.
 */
public class DiagramPlotter {

  /**
   * Render a file diagram into a file located in a specific directory.
   *
   * @param diagramFileName File containing the diagram
   * @param outputDirName Directory where to write the image of the diagram
   * @param renderedImageFile Name of the image file
   * @return A list of FileImageData, where the first contain the reference
   *     to the image file
   * @throws IOException If the diagramFileName is not found or the outputDirName
   *     doesn't exist
   */
  public List<FileImageData> renderFile(String diagramFileName,
                                        String outputDirName,
                                        String renderedImageFile)
          throws IOException {
    Debugger.debug(5, "Debugger.renderFile - 01 [" + diagramFileName
            + "] [" + outputDirName + "] [" + renderedImageFile + "] ");
    String diagramContent = Utils.readFileToString(diagramFileName);
    Debugger.debug(7, "Debugger.renderFile - 02 ");
    Path path = Paths.get(outputDirName);
    Debugger.debug(7, "Debugger.renderFile - 03 ");
    return render(diagramContent, path, renderedImageFile);
  }

  /**
   * Render an in memory diagram into a file located in a specific directory.
   *
   * @param source String containing the diagram to be rendered
   * @param outputDir Directory where to write the image of the diagram
   * @param outFile Name of the image file
   * @return A list of FileImageData, where the first contain the reference
   *     to the image file
   * @throws IOException If the diagramFileName is not found or the outputDirName
   *     doesn't exist
   */
  public List<FileImageData> render(String source, Path outputDir, String outFile)
          throws IOException {
    final SourceStringReader reader = new SourceStringReader(Defines.createEmpty(),
            source, UTF_8.name(), Collections.emptyList());
    final Diagram diagram = reader.getBlocks().getFirst().getDiagram();
    final SuggestedFile suggestedFile
            = SuggestedFile.fromOutputFile(outputDir.resolve(outFile).toFile(), PNG, 0);
    return PSystemUtils.exportDiagrams(diagram, suggestedFile, new FileFormatOption(PNG), false);
  }
}
