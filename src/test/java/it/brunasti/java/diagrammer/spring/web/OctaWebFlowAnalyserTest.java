package it.brunasti.java.diagrammer.spring.web;

import it.brunasti.java.diagrammer.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OctaWebFlowAnalyserTest implements TestConstants {

    // Test WebFlowAnalyserMain methods ---------------------------
    @Test
    @DisplayName("Call WebFlowAnalyserMain public methods")
    void testMainPublicMethods() {
        System.err.println("WebFlowAnalyserMain.main OCTA");
        String[] fullArgs = new String[6];
        fullArgs[0] = "/Users/paolobrunasti/IdeaProjects/mine/java-diagrammer/docs/octa/output.puml";
        fullArgs[1] = "-d";
        fullArgs[2] = "-i";
        fullArgs[3] = "/Users/paolobrunasti/IdeaProjects/OCTA/labelscout/src/main/java";
        fullArgs[4] = "-c";
        fullArgs[5] = configurationFileName;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
    }


}
