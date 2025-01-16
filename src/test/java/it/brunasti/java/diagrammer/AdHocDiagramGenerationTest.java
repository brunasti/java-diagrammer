package it.brunasti.java.diagrammer;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdHocDiagramGenerationTest implements TestConstants {

  // Test ClassDiagrammerMain methods ---------------------------
  @Test
  @DisplayName("Call ClassDiagrammerMain for Tadaah projects")
  void testMainPublicMethods() {
    Options options = new Options();

    System.err.println("ClassDiagrammerMain.main ------ for Tadaah projects -----");
    String[] fullArgs = new String[9];
    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-commons";
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[6] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-commons.puml";;
//    fullArgs[7] = "-p";
//    fullArgs[8] = classesDirectory;
    fullArgs[7] = "-i";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons/src/main/java";
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));
  }

  ClassDiagrammerMain getMain() {
    return new ClassDiagrammerMain();
  }


}
