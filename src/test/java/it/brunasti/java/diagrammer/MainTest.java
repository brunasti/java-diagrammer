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
    assertDoesNotThrow(() -> Main.debug("Message 1"));

    Main.setDebug(true);
    assertDoesNotThrow(() -> Main.debug("Message 2"));

    assertDoesNotThrow(() -> Main.printHelp());
    assertDoesNotThrow(() -> Main.printHelp(null));
    assertDoesNotThrow(() -> Main.printUsage(null));

    Options options = new Options();
    assertDoesNotThrow(() -> Main.printHelp(options));
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
    String[] args = new String[2];
    args[0] = classesDirectory;
    args[1] = outputFileName;
    assertDoesNotThrow(() -> Main.main(args));

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

    String[] moreArgs = new String[8];
    moreArgs[0] = "-c";
    moreArgs[1] = configurationFileName;
    moreArgs[2] = "-o";
    moreArgs[3] = outputFileName;
    moreArgs[4] = "-p";
    moreArgs[5] = classesDirectory;
    fullArgs[6] = "-i";
    fullArgs[7] = javaSrcDirectory;
    assertDoesNotThrow(() -> Main.main(moreArgs));
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
  @DisplayName("Call Main main to StdOut")
  void testMainMethod_StdOut() {
    String[] args = new String[1];
    args[0] = classesDirectory;
    assertDoesNotThrow(() -> Main.main(args));
  }

  @Test
  @DisplayName("Call Main main on test classes")
  void testMainMethod_Test() {
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
