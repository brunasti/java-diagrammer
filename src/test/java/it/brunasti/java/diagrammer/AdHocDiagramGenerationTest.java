package it.brunasti.java.diagrammer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdHocDiagramGenerationTest implements TestConstants {



  void generateDiagram(String service, String subService) {
    String repository = service;
    String diagram = service;
    if (subService != null) {
      repository = repository + "/" + subService;
      diagram = diagram + "-" + subService;
    }

    // Commons
    System.err.println("ClassDiagrammerMain.main ------ for Tadaah project ----- ["+service+"]["+subService+"]");

    String[] fullArgs = new String[8];
    fullArgs[0] = "/Users/paolo/Work/Tadaah/" +repository+ "/build/classes/java/main";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-"+diagram;
    fullArgs[2] = "-c";
    fullArgs[3] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
    fullArgs[4] = "-o";
    fullArgs[5] = fullArgs[1] + ".puml";
    fullArgs[6] = "-i";
    fullArgs[7] = "/Users/paolo/Work/Tadaah/" +repository+ "/src/main/java";
//    fullArgs[8] = "-d";

    ClassDiagrammerMain.main(fullArgs);
  }


    // Test ClassDiagrammerMain methods ---------------------------
  @Test
  @DisplayName("Call ClassDiagrammerMain for Tadaah projects")
  void testMainPublicMethods() {
//    Options options = new Options();
//    String[] fullArgs = new String[9];
//    fullArgs[2] = "-d";
//    fullArgs[3] = "-c";
//    fullArgs[4] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
//    fullArgs[5] = "-o";
//    fullArgs[7] = "-i";

    // Commons
    generateDiagram("fp-backend-commons","fp-commons");

    // Authemntication
    generateDiagram("fp-backend-commons","fp-authentication");

    // Invoice
    generateDiagram("fp-backend-commons","fp-commons-invoice");

    // Http-client
    generateDiagram("fp-backend-commons","fp-http-client");

    // Http-client DTO
    generateDiagram("fp-backend-commons","fp-http-client-dto");


    // auth-proxy
    // /Users/paolo/Work/Tadaah/fp-backend-auth-proxy/build/classes/java/main
    generateDiagram("fp-backend-auth-proxy",null);

    // documents-service
    // /Users/paolo/Work/Tadaah/fp-backend-documents-service/build/classes/java/main
    generateDiagram("fp-backend-documents-service",null);

    // documents-service
    // /Users/paolo/Work/Tadaah/fp-backend-hiring-service/build/classes/java/main
    generateDiagram("fp-backend-hiring-service",null);

    // invoice-service
    // /Users/paolo/Work/Tadaah/fp-backend-invoice-service/build/classes/java/main
    generateDiagram("fp-backend-invoice-service",null);

    // kvk-integration
    // /Users/paolo/Work/Tadaah/fp-backend-kvk-integration/build/classes/java/main
    generateDiagram("fp-backend-kvk-integration",null);

    // lrk-integration
    // /Users/paolo/Work/Tadaah/fp-backend-lrk-integration/build/classes/java/main
    generateDiagram("fp-backend-lrk-integration",null);

    // migrator
    // /Users/paolo/Work/Tadaah/fp-backend-migrator/build/classes/java/main
    generateDiagram("fp-backend-migrator",null);

    // notification
    generateDiagram("fp-backend-notification-service",null);

    // payment
    generateDiagram("fp-backend-payment-service",null);

    // user
    generateDiagram("fp-backend-user-service",null);


    assertNotNull(this);
  }


}
