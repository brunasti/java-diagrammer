package it.brunasti.java.diagrammer.spring.web;

import it.brunasti.java.diagrammer.TestConstants;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebFlowAnalyserMainTest implements TestConstants {

  @Test
  @DisplayName("Call WebFlowAnalyserMain main on test classes")
  void testMainMethod2_Test() {

    String[] fullArgs = new String[12];
    fullArgs[0] = testClassesDirectory;
    fullArgs[1] = testOutputFileName;
    fullArgs[2] = "-d";
    fullArgs[3] = "6";
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    fullArgs[6] = "-o";
    fullArgs[7] = testOutputFileName;
    fullArgs[8] = "-p";
    fullArgs[9] = testClassesDirectory;
    fullArgs[10] = "-i";
    fullArgs[11] = testJavaSrcDirectory;
    assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));
  }

    // Test WebFlowAnalyserMain methods ---------------------------
    @Test
    @DisplayName("Call WebFlowAnalyserMain public methods")
    void testMainPublicMethods() {
        Options options = new Options();


        System.err.println("WebFlowAnalyserMain.printHelp ------");
        assertDoesNotThrow(() -> WebFlowAnalyserMain.printHelp());
        assertDoesNotThrow(() -> WebFlowAnalyserMain.printHelp(null));
        assertDoesNotThrow(() -> WebFlowAnalyserMain.printHelp(options));

        System.err.println("WebFlowAnalyserMain.printUsage ------");
        assertDoesNotThrow(() -> WebFlowAnalyserMain.printUsage(null));
        assertDoesNotThrow(() -> WebFlowAnalyserMain.printUsage(options));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main without parameters")
    void testMainMethod_NoParams() {
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(null));
        String[] args = new String[0];
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));
        String[] args2 = new String[2];
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args2));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main with parameters")
    void testMainMethod_Params() {
        System.err.println("WebFlowAnalyserMain.main ------ 2 args -----");
        String[] args = new String[2];
        args[0] = classesDirectory;
        args[1] = outputFileName;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));

        System.err.println("WebFlowAnalyserMain.main ------ 11 args -----");
        String[] fullArgs = new String[11];
        fullArgs[0] = classesDirectory;
        fullArgs[1] = outputFileName;
        fullArgs[2] = "-d";
        fullArgs[3] = "-c";
        fullArgs[4] = configurationFileName;
        fullArgs[5] = "-o";
        fullArgs[6] = outputFileName;
        fullArgs[7] = "-p";
        fullArgs[8] = classesDirectory;
        fullArgs[9] = "-i";
        fullArgs[10] = javaSrcDirectory;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));

        System.err.println("WebFlowAnalyserMain.main ------ 8 args -----");
        String[] moreArgs = new String[8];
        moreArgs[0] = "-c";
        moreArgs[1] = configurationFileName;
        moreArgs[2] = "-o";
        moreArgs[3] = outputFileName;
        moreArgs[4] = "-p";
        moreArgs[5] = classesDirectory;
        moreArgs[6] = "-i";
        moreArgs[7] = javaSrcDirectory;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(moreArgs));

        System.err.println("WebFlowAnalyserMain.main ------ 6 args ----- configurationWithLegendFileName");
        String[] lastArgs = new String[6];
        lastArgs[0] = "-c";
        lastArgs[1] = configurationWithIncludeFileName;
        lastArgs[2] = "-o";
        lastArgs[3] = outputFileName;
        lastArgs[4] = "-p";
        lastArgs[5] = classesDirectory;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(lastArgs));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main helps")
    void testMainMethod_Helps() {
        String[] args = new String[1];
        args[0] = "-h";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));

        String[] argsHelp = new String[1];
        argsHelp[0] = "-?";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(argsHelp));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main with errors")
    void testMainMethod_Errors() {
        String[] args = new String[1];
        args[0] = "-x";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));

        String[] argsHelp = new String[1];
        argsHelp[0] = "-z";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(argsHelp));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main with debug")
    void testMainMethod_Debug() {
        System.err.println("Call WebFlowAnalyserMain main with debug ---- ");

        System.err.println("[-d]");
        String[] args = new String[1];
        args[0] = "-d";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));

        System.err.println("[-d 2]");
        String[] argsHelp = new String[2];
        argsHelp[0] = "-d";
        argsHelp[1] = "2";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(argsHelp));

        System.err.println("[-d 999]");
        argsHelp[0] = "-d";
        argsHelp[1] = "999";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(argsHelp));

        System.err.println("[-d xxx]");
        argsHelp[0] = "-d";
        argsHelp[1] = "xxx";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(argsHelp));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main to StdOut")
    void testMainMethod_StdOut() {
        String[] args = new String[1];
        args[0] = classesDirectory;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));
    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main on test classes")
    void testMainMethod_Test() {

        String[] fullArgs = new String[12];
        fullArgs[0] = testClassesDirectory;
        fullArgs[1] = testOutputFileName;
        fullArgs[2] = "-d";
        fullArgs[3] = "6";
        fullArgs[4] = "-c";
        fullArgs[5] = configurationFileName;
        fullArgs[6] = "-o";
        fullArgs[7] = testOutputFileName;
        fullArgs[8] = "-p";
        fullArgs[9] = testClassesDirectory;
        fullArgs[10] = "-i";
        fullArgs[11] = testJavaSrcDirectory;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(fullArgs));

        String[] args = new String[3];
        args[0] = testClassesDirectory;
        args[1] = testOutputFileName;
        args[2] = "-d";
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));


    }

    @Test
    @DisplayName("Call WebFlowAnalyserMain main with output for non existing file")
    void testMainMethod_ErrorOutputFile() {
        String[] args = new String[2];
        args[0] = testClassesDirectory;
        args[1] = nonExistingDirectoryAndFile;
        assertDoesNotThrow(() -> WebFlowAnalyserMain.main(args));
    }

    // Test Constructor ---------------------------
    @Test
    @DisplayName("Constructor")
    void testConstructor() {
        WebFlowAnalyserMain main = getMain();
        assertNotNull(main);
    }

    WebFlowAnalyserMain getMain() {
        return new WebFlowAnalyserMain();
    }
  
}
