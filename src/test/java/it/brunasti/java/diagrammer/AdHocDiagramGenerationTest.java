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

    // Commons
    System.err.println("ClassDiagrammerMain.main ------ for Tadaah projects -----");
    String[] fullArgs = new String[9];
    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-commons";
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[6] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-commons.puml";;
    fullArgs[7] = "-i";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons/src/main/java";
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));

    // Authemntication
    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-authentication/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-autentication";
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[6] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-autentication.puml";;
    fullArgs[7] = "-i";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-autentication/src/main/java";
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));

    // Invoice
    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons-invoice/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-invoice";
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[6] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-invoice.puml";;
    fullArgs[7] = "-i";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons-invoice/src/main/java";
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));

    // Invoice
    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-notification-service/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-notification";
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[6] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-notification.puml";;
    fullArgs[7] = "-i";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-notification/src/main/java";
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));

    // Http-client
    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-http-client/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-http-client";
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[6] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-http-client.puml";;
    fullArgs[7] = "-i";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-http-client/src/main/java";
    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));
  }

  ClassDiagrammerMain getMain() {
    return new ClassDiagrammerMain();
  }


}
