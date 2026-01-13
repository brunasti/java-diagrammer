package it.brunasti.java.diagrammer.spring.web;

import it.brunasti.java.diagrammer.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OctaWebFlowAnalyserTest implements TestConstants {

    // Test WebFlowAnalyserMain methods ---------------------------
    @Test
    @DisplayName("Call WebFlowAnalyserMain for Labelscout")
    void testLabelscout() {
        System.err.println("WebFlowAnalyserMain.main OCTA Labelscout");
        String[] fullArgs = new String[6];
        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-Labelscout-flow_01.puml";
        fullArgs[1] = "-d";
        fullArgs[2] = "-i";
        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/labelscout/src/main/java";
        fullArgs[4] = "-c";
        fullArgs[5] = configurationFileName;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain for Stock")
    void testStock() {
        System.err.println("WebFlowAnalyserMain.main OCTA Stock");
        String[] fullArgs = new String[6];
        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/OCTA-Stock-flow_01.puml";
        fullArgs[1] = "-d";
        fullArgs[2] = "-i";
        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/stock/src/main/java";
        fullArgs[4] = "-c";
        fullArgs[5] = configurationFileName;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }


}
