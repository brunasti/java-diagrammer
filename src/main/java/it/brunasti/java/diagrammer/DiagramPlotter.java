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
 *
 */
public class DiagramPlotter {

  private List<FileImageData> renderFile(String fileName, String outputDir)
          throws IOException {
    return renderFile(fileName, outputDir, "output");
  }

  private List<FileImageData> renderFile(String fileName, String outputDir, String targetFile)
          throws IOException {
    String diagramContent = Utils.readFileToString(fileName);
    Path path = Paths.get(outputDir);
    return render(diagramContent, path, targetFile);
  }

  private List<FileImageData> renderFile(String fileName, Path outputDir)
          throws IOException {
    String diagramContent = Utils.readFileToString(fileName);
    return render(diagramContent, outputDir);
  }

  private List<FileImageData> render(String source, Path outputDir)
          throws IOException {
    return render(source, outputDir, "output");
  }

  private List<FileImageData> render(String source, Path outputDir, String outFile)
          throws IOException {
    final SourceStringReader reader = new SourceStringReader(Defines.createEmpty(),
            source, UTF_8.name(), Collections.emptyList());
    final Diagram diagram = reader.getBlocks().getFirst().getDiagram();
    final SuggestedFile suggestedFile
            = SuggestedFile.fromOutputFile(outputDir.resolve(outFile).toFile(), PNG, 0);
    return PSystemUtils.exportDiagrams(diagram, suggestedFile, new FileFormatOption(PNG), false);
  }

  public static void main(String[] args) {
    try {
      DiagramPlotter diagramPlotter = new DiagramPlotter();
      diagramPlotter.renderFile("./docs/output.puml", "./temp/", "test-output");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
