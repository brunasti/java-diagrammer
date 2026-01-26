package it.brunasti.java.diagrammer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OctaDiagramGenerationTest implements TestConstants {



//  void generateDiagram(String service, String subService) {
//    String repository = service;
//    String diagram = service;
//    if (subService != null) {
//      repository = repository + "/" + subService;
//      diagram = diagram + "-" + subService;
//    }
//
//    // Commons
//    System.err.println("ClassDiagrammerMain.main ------ for Tadaah project ----- ["+service+"]["+subService+"]");
//
//    String[] fullArgs = new String[10];
//    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/build/classes/java/main";
//    fullArgs[1] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-"+diagram;
//    fullArgs[2] = "-c";
//    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/config.json";
//    fullArgs[4] = "-o";
//    fullArgs[5] = fullArgs[1] + ".puml";
//    fullArgs[6] = "-i";
//    fullArgs[7] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/src/main/java";
//    fullArgs[8] = "-d";
//    fullArgs[9] = "0";
//
//    ClassDiagrammerMain.main(fullArgs);
//  }


  void generateDiagramTools(String service, String subService) {
    String repository = service;
    String diagram = service;
    if (subService != null) {
      repository = repository + "/" + subService;
      diagram = diagram + "-" + subService;
    }

    // Commons
    System.err.println("ClassDiagrammerMain.main ------ for OCTA project ----- ["+service+"]["+subService+"]");

    String[] fullArgs = new String[10];
    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/build/classes/java/main";
    fullArgs[1] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-"+diagram+"-class-diagram";
    fullArgs[2] = "-c";
    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/config.json";
    fullArgs[4] = "-o";
    fullArgs[5] = fullArgs[1] + ".puml";
    fullArgs[6] = "-i";
    fullArgs[7] = "/Users/paolobrunasti/IdeaProjects/OCTA/" +repository+ "/src/main/java";
    fullArgs[8] = "-d";
    fullArgs[9] = "5";


    ClassDiagrammerMain.main(fullArgs);
  }



  // Test ClassDiagrammerMain methods ---------------------------
  @Test
  @DisplayName("Call ClassDiagrammerMain for Tadaah projects")
  void testOctaRepoClassDiagrams() {
    // labelscout
    generateDiagramTools("labelscout",null);

    assertNotNull(this);
  }


//  void generateDiagramEntities(String repository, String classesPath, String javaPath) {
//    // Commons
//    String[] fullArgs = new String[10];
//
//    String diagram = repository;
//    System.err.println("generateDiagramEntity ------ for Tadaah project Entities----- [" + repository + "][" + diagram + "]");
//    fullArgs[0] = classesPath;
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/Tadaah-entities-" + diagram;
//    fullArgs[2] = "-c";
//    fullArgs[3] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
//    fullArgs[4] = "-o";
//    fullArgs[5] = fullArgs[1] + ".puml";
//    fullArgs[6] = "-i";
//    fullArgs[7] = javaPath;
//    fullArgs[8] = "-d";
//    fullArgs[9] = "3";
//
//    ClassDiagrammerMain.main(fullArgs);
//  }
//
////  @Test
//  @DisplayName("Call generateDiagramEntity for Tadaah projects Entities")
//  void generateDiagramsEntities() {
//
//    generateDiagramEntities("documents",
//            "/Users/paolo/Work/Tadaah/fp-backend-documents-service/build/classes/java/main/com/freelanceplaza/documents/entity",
//            "/Users/paolo/Work/Tadaah/fp-backend-documents-service/src/main/java/com/freelanceplaza/documents/entity");
//
//    generateDiagramEntities("hiring",
//            "/Users/paolo/Work/Tadaah/fp-backend-hiring-service/build/classes/java/main/com/freelanceplaza/hiringservice/entities",
//            "/Users/paolo/Work/Tadaah/fp-backend-hiring-service/src/main/java/com/freelanceplaza/hiringservice/entities");
//
//    generateDiagramEntities("invoice",
//            "/Users/paolo/Work/Tadaah/fp-backend-invoice-service/build/classes/java/main/freelanceplaza/fpbackendinvoiceservice/entity",
//            "/Users/paolo/Work/Tadaah/fp-backend-invoice-service/src/main/java/freelanceplaza/fpbackendinvoiceservice/entity");
//
//    generateDiagramEntities("notification",
//            "/Users/paolo/Work/Tadaah/fp-backend-notification-service/build/classes/java/main/com/freelanceplaza/notification/entities",
//            "/Users/paolo/Work/Tadaah/fp-backend-notification-service/src/main/java/com/freelanceplaza/notification/entities");
//
//    generateDiagramEntities("payment",
//            "/Users/paolo/Work/Tadaah/fp-backend-payment-service/build/classes/java/main/com/freelanceplaza/payment/entities",
//            "/Users/paolo/Work/Tadaah/fp-backend-payment-service/src/main/java/com/freelanceplaza/payment/entities");
//
//    generateDiagramEntities("user",
//            "/Users/paolo/Work/Tadaah/fp-backend-user-service/build/classes/java/main/freelanceplaza/fpbackenduserservice/entities",
//            "/Users/paolo/Work/Tadaah/fp-backend-user-service/src/main/java/freelanceplaza/fpbackenduserservice/entities");
//
//  }



//  @Test
//  void generateDiagramForTadaahDbErGenerator() {
//    // Commons
//    String repository = "";
//    String classesPath = "/Users/paolo/Work/Tadaah/tadaah_fp2_data_tools/target/classes";
//    String javaPath = "/Users/paolo/Work/Tadaah/tadaah_fp2_data_tools/src/main/java";
////    String classesPath = "/Users/paolo/Work/Tadaah/tadaah_fp2_data_tools/target/classes/com/freelancerplaza/analysis";
////    String javaPath = "/Users/paolo/Work/Tadaah/tadaah_fp2_data_tools/src/main/java/com/freelancerplaza/analysis";
//
//    String[] fullArgs = new String[10];
//
//    String diagram = repository;
//    System.err.println("generateDiagramForTadaahDbErGenerator ------ for Tadaah Db Er Generator----- [" + repository + "][" + diagram + "]");
//    fullArgs[0] = classesPath;
//    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/analysis/Tadaah-Analysis" + diagram;
//    fullArgs[2] = "-c";
//    fullArgs[3] = "/Users/paolo/IdeaProjects/mine/java-diagrammer/temp/tadaah/config.json";
//    fullArgs[4] = "-o";
//    fullArgs[5] = fullArgs[1] + ".puml";
//    fullArgs[6] = "-i";
//    fullArgs[7] = javaPath;
//    fullArgs[8] = "-d";
//    fullArgs[9] = "1";
//
//    ClassDiagrammerMain.main(fullArgs);
//  }

}
