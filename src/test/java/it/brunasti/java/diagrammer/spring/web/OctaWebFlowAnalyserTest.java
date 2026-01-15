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

  // Test WebFlowAnalyserMain methods ---------------------------
    @Test
    @DisplayName("Call WebFlowAnalyserMain for Labelscout")
    void testLabelscout() {
        testParametricFlowAnalyser("labelscout");
//        System.err.println("WebFlowAnalyserMain.main OCTA Labelscout");
//        String[] fullArgs = new String[6];
//        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-Labelscout-flow_01.puml";
//        fullArgs[1] = "-d";
//        fullArgs[2] = "-i";
//        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/labelscout/src/main/java";
//        fullArgs[4] = "-c";
//        fullArgs[5] = configurationFileName;
//        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain for Stock")
    void testStock() {
//        System.err.println("WebFlowAnalyserMain.main OCTA Stock");
      testParametricFlowAnalyser("stock");
//        String[] fullArgs = new String[6];
//        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-Stock-flow_01.puml";
//        fullArgs[1] = "-d";
//        fullArgs[2] = "-i";
//        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/stock/src/main/java";
//        fullArgs[4] = "-c";
//        fullArgs[5] = configurationFileName;
//        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain for deelverpakking")
    void testDeelverpakking() {
      testParametricFlowAnalyser("deelverpakking");
//        System.err.println("WebFlowAnalyserMain.main OCTA deelverpakking");
//        String[] fullArgs = new String[6];
//        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-deelverpakking-flow_01.puml";
//        fullArgs[1] = "-d";
//        fullArgs[2] = "-i";
//        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/deelverpakking/src/main/java";
//        fullArgs[4] = "-c";
//        fullArgs[5] = configurationFileName;
//        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain for pharmacomstocksender")
    void testPharmacomstocksender() {
      testParametricFlowAnalyser("pharmacomstocksender");
//        System.err.println("WebFlowAnalyserMain.main OCTA pharmacomstocksender");
//        String[] fullArgs = new String[6];
//        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-pharmacomstocksender-flow_01.puml";
//        fullArgs[1] = "-d";
//        fullArgs[2] = "-i";
//        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/pharmacomstocksender/src/main/java";
//        fullArgs[4] = "-c";
//        fullArgs[5] = configurationFileName;
//        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }

  @Test
  @DisplayName("Call WebFlowAnalyserMain for picommon")
  void testpicommon() {
    testParametricFlowAnalyser("picommon");
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
  @DisplayName("Call WebFlowAnalyserMain for zindexproductsservice")
  void testzindexproductsservice() {
    testParametricFlowAnalyser("zindexproductsservice");
  }


}
