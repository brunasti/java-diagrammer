package it.brunasti.java.diagrammer.spring.web;

import it.brunasti.java.diagrammer.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OctaWebFlowAnalyserTest implements TestConstants {

  void testParametricFlowAnalyser(String name) {
    System.err.println("WebFlowAnalyserMain.main OCTA "+name);
    String[] fullArgs = new String[6];
    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-"+name+"-flow_01.puml";
    fullArgs[1] = "-d";
    fullArgs[2] = "-i";
    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/"+name+"/src/main/java";
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
  }

    @Test
    @DisplayName("Call WebFlowAnalyserMain for deelverpakking")
    void testDeelverpakking() {
      testParametricFlowAnalyser("deelverpakking");
    }

  @Test
  @DisplayName("Call WebFlowAnalyserMain for Labelscout")
  void testLabelscout() {
    testParametricFlowAnalyser("labelscout");
  }

  @Test
  @DisplayName("Call WebFlowAnalyserMain for pireceive")
  void testpireceive() {
    testParametricFlowAnalyser("pireceive");
  }

  @Test
  @DisplayName("Call WebFlowAnalyserMain for pisend")
  void testpisend() {
    testParametricFlowAnalyser("pisend");
  }

  @Test
  @DisplayName("Call WebFlowAnalyserMain for Stock")
  void testStock() {
    testParametricFlowAnalyser("stock");
  }

  @Test
  @DisplayName("Call WebFlowAnalyserMain for zindexproductsservice")
  void testzindexproductsservice() {
    testParametricFlowAnalyser("zindexproductsservice");
  }


  @Test
  @DisplayName("Call WebFlowAnalyserMain for receipt-handler")
  void testParametricForReceipt() {
    System.err.println("WebFlowAnalyserMain.main OCTA receipt-handler");
    String[] fullArgs = new String[6];
    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-receipt-handler-flow_01.puml";
    fullArgs[1] = "-d";
    fullArgs[2] = "-i";
    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/receipt-handler/backend/src/main/java";
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
  }

//  @Test
//  @DisplayName("Call WebFlowAnalyserMain for receipt-handler frontend")
//  void testParametricForReceiptFrontend() {
//    System.err.println("WebFlowAnalyserMain.main OCTA receipt-handler frontend");
//    String[] fullArgs = new String[6];
//    fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-receipt-handler-frontend-flow_01.puml";
//    fullArgs[1] = "-d";
//    fullArgs[2] = "-i";
//    fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/receipt-handler/frontend/src/main/java";
//    fullArgs[4] = "-c";
//    fullArgs[5] = configurationFileName;
//    assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
//  }

//  @Test
//    @DisplayName("Call WebFlowAnalyserMain for pharmacomstocksender")
//    void testPharmacomstocksender() {
//      testParametricFlowAnalyser("pharmacomstocksender");
//    }

//  @Test
//  @DisplayName("Call WebFlowAnalyserMain for picommon")
//  void testpicommon() {
//    testParametricFlowAnalyser("picommon");
//  }

//  @Test
//  @DisplayName("Call WebFlowAnalyserMain for pickorderio")
//  void testpickorderio() {
//    testParametricFlowAnalyser("pickorderio");
//  }

}
