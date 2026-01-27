package it.brunasti.java.diagrammer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OctaDiagramGenerationTest implements TestConstants {

  void generateDiagramToolsGradle(String service, String subService) {
    String repository = service;
    String diagram = service;
    if (subService != null) {
      repository = repository + "/" + subService;
      diagram = diagram + "-" + subService;
    }

    System.err.println("ClassDiagrammerMain.main GRADLE ------ for OCTA project ----- ["+service+"]["+subService+"]");

    String[] fullArgs = new String[12];
    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/build/classes/java/main";
    fullArgs[1] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-"+diagram+"-class-diagram";
    fullArgs[2] = "-c";
    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/config.json";
    fullArgs[4] = "-o";
    fullArgs[5] = fullArgs[1] + ".puml";
    fullArgs[6] = "-i";
    fullArgs[7] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/src/main/java";
    fullArgs[8] = "-d";
    fullArgs[9] = "1";
    fullArgs[10] = "-b";
    fullArgs[11] = "OCTA-"+diagram+"-class-diagram";

    ClassDiagrammerMain.main(fullArgs);
  }

  void generateDiagramToolsMaven(String service, String subService) {
    String repository = service;
    String diagram = service;
    if (subService != null) {
      repository = repository + "/" + subService;
      diagram = diagram + "-" + subService;
    }

    System.err.println("ClassDiagrammerMain.main MAVEN ------ for OCTA project ----- ["+service+"]["+subService+"]");

    String[] fullArgs = new String[12];
    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/target/classes";
    fullArgs[1] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-"+diagram+"-class-diagram";
    fullArgs[2] = "-c";
    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/config.json";
    fullArgs[4] = "-o";
    fullArgs[5] = fullArgs[1] + ".puml";
    fullArgs[6] = "-i";
    fullArgs[7] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/src/main/java";
    fullArgs[8] = "-d";
    fullArgs[9] = "1";
    fullArgs[10] = "-b";
    fullArgs[11] = "OCTA-"+diagram+"-class-diagram";

    ClassDiagrammerMain.main(fullArgs);
  }

  // Generate diagrams for Octa repos ---------------------------
  @Test
  @DisplayName("Generate diagrams for Octa repos")
  void testOctaRepoClassDiagrams() {
    generateDiagramToolsGradle("deelverpakking",null);
    generateDiagramToolsGradle("labelscout",null);
    generateDiagramToolsGradle("pharmacomstocksender",null);
    generateDiagramToolsGradle("zindexproductsservice",null);

    generateDiagramToolsMaven("pickorderio",null);
    generateDiagramToolsMaven("picommon",null);
    generateDiagramToolsMaven("pireceive",null);
    generateDiagramToolsMaven("pisend",null);
    generateDiagramToolsMaven("stock",null);

    assertNotNull(this);
  }

}
