package it.brunasti.java.diagrammer;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    fullArgs[3] = "-d";
    fullArgs[4] = "-d";
    fullArgs[5] = "-c";
    fullArgs[6] = configurationFileName;
    fullArgs[7] = "-o";
    fullArgs[8] = outputFileName;
    fullArgs[9] = "-p";
    fullArgs[10] = classesDirectory;
    assertDoesNotThrow(() -> Main.main(fullArgs));
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

}