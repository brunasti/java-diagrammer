package it.brunasti.java.diagrammer;

import net.sourceforge.plantuml.*;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.file.SuggestedFile;
import net.sourceforge.plantuml.preproc.Defines;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.net.SocketFactory;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.sourceforge.plantuml.FileFormat.PNG;

public class DiagramPlotter {


  private static List<FileImageData> renderFile(String fileName, String outputDir) throws IOException {
    return renderFile(fileName, outputDir, "output");
  }

  private static List<FileImageData> renderFile(String fileName, String outputDir, String targetFile) throws IOException {
    String diagramContent = Utils.readFileToString(fileName);
    Path path = Paths.get(outputDir);
    return render(diagramContent, path, targetFile);
  }

  private static List<FileImageData> renderFile(String fileName, Path outputDir) throws IOException {
    String diagramContent = Utils.readFileToString(fileName);
    return render(diagramContent, outputDir);
  }

  private static List<FileImageData> render(String source, Path outputDir) throws IOException {
    return render(source, outputDir, "output");
  }
  private static List<FileImageData> render(String source, Path outputDir, String outFile) throws IOException {
    System.out.println("  render");
    final SourceStringReader reader = new SourceStringReader(Defines.createEmpty(), source, UTF_8.name(), Collections.<String>emptyList());

    final Diagram diagram = reader.getBlocks().get(0).getDiagram();

    final SuggestedFile suggestedFile = SuggestedFile.fromOutputFile(outputDir.resolve(outFile).toFile(), PNG, 0);

    return PSystemUtils.exportDiagrams(diagram, suggestedFile, new FileFormatOption(PNG), false);
  }
  public static void main(String[] args) {
    try {
      System.out.println("main");
      Path path = Paths.get("./temp/");
      renderFile("./docs/output.puml", "./temp/", "test-output");
      System.out.println("main - end");


////      File source = new File("/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/docs/output.puml");
////      SourceFileReader reader = new SourceFileReader(source);
////      List<GeneratedImage> list = reader.getGeneratedImages();
////      // Generated files
////      File png = list.get(0).getPngFile();
//
//      String source = "@startuml\n";
//      source += "Bob -> Alice : hello\n";
//      source += "@enduml\n";
//
//      SourceStringReader reader = new SourceStringReader(source);
//      final ByteArrayOutputStream os = new ByteArrayOutputStream();
//// Write the first image to "os"
//      String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
//      os.close();
//
//// The XML is stored into svg
//      final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));


    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
