package it.brunasti.java.diagrammer;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdHocDiagramGenerationTest implements TestConstants {



  void generateDiagram(String service, String subService) {
    String[] fullArgs = new String[9];
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[7] = "-i";

    String repository = service;
    String diagram = service;
    if (subService != null) {
      repository = repository + "/" + subService;
      diagram = diagram + "-" + subService;
    }

    // Commons
    System.err.println("ClassDiagrammerMain.main ------ for Tadaah project ----- ["+service+"]["+subService+"]");

    fullArgs[0] = "/Users/paolo/Work/Tadaah/" +repository+ "/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-"+diagram;
    fullArgs[6] = fullArgs[1] + ".puml";
    fullArgs[8] = "/Users/paolo/Work/Tadaah/" +repository+ "/src/main/java";
    ClassDiagrammerMain.main(fullArgs);
  }


    // Test ClassDiagrammerMain methods ---------------------------
  @Test
  @DisplayName("Call ClassDiagrammerMain for Tadaah projects")
  void testMainPublicMethods() {
    Options options = new Options();
    String[] fullArgs = new String[9];
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[5] = "-o";
    fullArgs[7] = "-i";

    // Commons
//    System.err.println("ClassDiagrammerMain.main ------ for Tadaah projects -----");
//    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons/build/classes/java/main";
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-commons";
//    fullArgs[6] = fullArgs[1]+".puml";;
//    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons/src/main/java";
//    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));

    generateDiagram("fp-backend-commons","fp-commons");

    // Authemntication
//    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-authentication/build/classes/java/main";
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-autentication";
//    fullArgs[6] = fullArgs[1]+".puml";;
//    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-autentication/src/main/java";
//    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));
    generateDiagram("fp-backend-commons","fp-autentication");

    // Invoice
//    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons-invoice/build/classes/java/main";
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-commons-invoice";
//    fullArgs[6] = fullArgs[1]+".puml";;
//    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-commons-invoice/src/main/java";
//    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));
    generateDiagram("fp-backend-commons","fp-commons-invoice");

    // Http-client
//    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-http-client/build/classes/java/main";
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-http-client";
//    fullArgs[6] = fullArgs[1]+".puml";;
//    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-http-client/src/main/java";
    generateDiagram("fp-backend-commons","fp-http-client");

    // Http-client DTO
//    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-http-client-dto/build/classes/java/main";
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-commons-fp-http-client-dto";
//    fullArgs[6] = fullArgs[1]+".puml";;
//    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-commons/fp-http-client-dto/src/main/java";
    generateDiagram("fp-backend-commons","fp-http-client-dto");

    // notification
//    fullArgs[0] = "/Users/paolo/Work/Tadaah/fp-backend-notification-service/build/classes/java/main";
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-fp-backend-notification";
//    fullArgs[6] = fullArgs[1]+".puml";;
//    fullArgs[8] = "/Users/paolo/Work/Tadaah/fp-backend-notification/src/main/java";
//    assertDoesNotThrow(() -> ClassDiagrammerMain.main(fullArgs));
    generateDiagram("fp-backend-notification-service",null);

    assertNotNull(this);

  }

  ClassDiagrammerMain getMain() {
    return new ClassDiagrammerMain();
  }


}
