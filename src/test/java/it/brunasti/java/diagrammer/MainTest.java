package it.brunasti.java.diagrammer;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainTest implements TestConstants {

  // Test Main methods ---------------------------
  @Test
  @DisplayName("Call Main public methods")
  void testMainPublicMethods() {
    System.err.println("Main.debug ------ false");
    assertDoesNotThrow(() -> Main.debug("Message false"));

    System.err.println("Main.debug ------ true");
    Main.setDebug(true);
    assertDoesNotThrow(() -> Main.debug("Message true"));

    System.err.println("Main.debug ------ 4");
    Main.setDebug(4);
    assertDoesNotThrow(() -> Main.debug("Message"));
    assertDoesNotThrow(() -> Main.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Main.debug(4,"Message 4"));

    System.err.println("Main.debug ------ 0");
    Main.setDebug(0);
    assertDoesNotThrow(() -> Main.debug("Message"));
    assertDoesNotThrow(() -> Main.debug(0,"Message 0"));
    assertDoesNotThrow(() -> Main.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Main.debug(4,"Message 4"));

    System.err.println("Main.debug ------ 2");
    Main.setDebug(2);
    assertDoesNotThrow(() -> Main.debug("Message"));
    assertDoesNotThrow(() -> Main.debug(0,"Message 0"));
    assertDoesNotThrow(() -> Main.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Main.debug(4,"Message 4"));

    System.err.println("Main.debug ------ false");
    Main.setDebug(false);
    assertDoesNotThrow(() -> Main.debug("Message"));
    assertDoesNotThrow(() -> Main.debug(0,"Message 0"));
    assertDoesNotThrow(() -> Main.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Main.debug(4,"Message 4"));

    System.err.println("Main.debug ------ end -----");


    Options options = new Options();


    System.err.println("Main.printHelp ------");
    assertDoesNotThrow(() -> Main.printHelp());
    assertDoesNotThrow(() -> Main.printHelp(null));
    assertDoesNotThrow(() -> Main.printHelp(options));

    System.err.println("Main.printUsage ------");
    assertDoesNotThrow(() -> Main.printUsage(null));
    assertDoesNotThrow(() -> Main.printUsage(options));
  }

  @Test
  @DisplayName("Call Main main without parameters")
  void testMainMethod_NoParams() {
    assertDoesNotThrow(() -> Main.main(null));
    String[] args = new String[0];
    assertDoesNotThrow(() -> Main.main(args));
    String[] args2 = new String[2];
    assertDoesNotThrow(() -> Main.main(args2));
  }

  @Test
  @DisplayName("Call Main main with parameters")
  void testMainMethod_Params() {
    System.err.println("Main.main ------ 2 args -----");
    String[] args = new String[2];
    args[0] = classesDirectory;
    args[1] = outputFileName;
    assertDoesNotThrow(() -> Main.main(args));

    System.err.println("Main.main ------ 11 args -----");
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
    assertDoesNotThrow(() -> Main.main(fullArgs));

    System.err.println("Main.main ------ 8 args -----");
    String[] moreArgs = new String[8];
    moreArgs[0] = "-c";
    moreArgs[1] = configurationFileName;
    moreArgs[2] = "-o";
    moreArgs[3] = outputFileName;
    moreArgs[4] = "-p";
    moreArgs[5] = classesDirectory;
    moreArgs[6] = "-i";
    moreArgs[7] = javaSrcDirectory;
    assertDoesNotThrow(() -> Main.main(moreArgs));

    System.err.println("Main.main ------ 6 args ----- configurationWithLegendFileName");
    String[] lastArgs = new String[6];
    lastArgs[0] = "-c";
    lastArgs[1] = configurationWithLegendFileName;
    lastArgs[2] = "-o";
    lastArgs[3] = outputFileName;
    lastArgs[4] = "-p";
    lastArgs[5] = classesDirectory;
    assertDoesNotThrow(() -> Main.main(lastArgs));
  }

  @Test
  @DisplayName("Call Main main helps")
  void testMainMethod_Helps() {
    String[] args = new String[1];
    args[0] = "-h";
    assertDoesNotThrow(() -> Main.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-?";
    assertDoesNotThrow(() -> Main.main(argsHelp));
  }

  @Test
  @DisplayName("Call Main main with errors")
  void testMainMethod_Errors() {
    String[] args = new String[1];
    args[0] = "-x";
    assertDoesNotThrow(() -> Main.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-z";
    assertDoesNotThrow(() -> Main.main(argsHelp));
  }

  @Test
  @DisplayName("Call Main main with debug")
  void testMainMethod_Debug() {
    System.err.println("Call Main main with debug ---- ");

    System.err.println("[-d]");
    String[] args = new String[1];
    args[0] = "-d";
    assertDoesNotThrow(() -> Main.main(args));

    System.err.println("[-d 2]");
    String[] argsHelp = new String[2];
    argsHelp[0] = "-d";
    argsHelp[1] = "2";
    assertDoesNotThrow(() -> Main.main(argsHelp));

    System.err.println("[-d 999]");
    argsHelp[0] = "-d";
    argsHelp[1] = "999";
    assertDoesNotThrow(() -> Main.main(argsHelp));

    System.err.println("[-d xxx]");
    argsHelp[0] = "-d";
    argsHelp[1] = "xxx";
    assertDoesNotThrow(() -> Main.main(argsHelp));
  }

  @Test
  @DisplayName("Call Main main to StdOut")
  void testMainMethod_StdOut() {
    String[] args = new String[1];
    args[0] = classesDirectory;
    assertDoesNotThrow(() -> Main.main(args));
  }

  @Test
  @DisplayName("Call Main main on test classes")
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
    assertDoesNotThrow(() -> Main.main(fullArgs));

    String[] args = new String[3];
    args[0] = testClassesDirectory;
    args[1] = testOutputFileName;
    args[2] = "-d";
    assertDoesNotThrow(() -> Main.main(args));


  }

  @Test
  @DisplayName("Call Main main with output for non existing file")
  void testMainMethod_ErrorOutputFile() {
    String[] args = new String[2];
    args[0] = testClassesDirectory;
    args[1] = nonExistingDirectoryAndFile;
    assertDoesNotThrow(() -> Main.main(args));
  }

  // Test Constructor ---------------------------
  @Test
  @DisplayName("Constructor")
  void testConstructor() {
    Main main = getMain();
    assertNotNull(main);
  }

  Main getMain() {
    return new Main();
  }


}
