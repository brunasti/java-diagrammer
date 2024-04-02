package it.brunasti.java.diagrammer;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Entry point for the CLI version of the ClassDiagrammer.
 * This ClassDiagrammerMain uses the library form Apache for Command Line Interface:
 * <a href="https://commons.apache.org/proper/commons-cli/usage.html">commons-cli</a>
 */
public class ClassDiagrammerMain {

  private static final int DEFAULT_DEBUG_LEVEL = 3;

  static CommandLine commandLine;
  static ClassDiagrammer classDiagrammer;

  private static boolean debug = false;
  private static int debugLevel = 3;

  private static String javaPackagePath = "";
  private static String classesPackagePath = "";
  private static String outputFile = "";
  private static String configurationFile = "";

  private static Options options;

  private static void reset() {
    debug = false;
    debugLevel = DEFAULT_DEBUG_LEVEL;

    classesPackagePath = "";
    javaPackagePath = "";
    outputFile = "";
    configurationFile = "";

    options = null;
  }

  public static void setDebug(boolean value) {
    debug = value;
  }

  public static void setDebug(int value) {
    setDebug(value > 0);
    debugLevel = value;
  }

  /**
   * Print to StdError a message if the debug level is higher than the DEFAULT_DEBUG_LEVEL.
   *
   * @param message Debugging message
   */
  public static void debug(String message) {
    debug(DEFAULT_DEBUG_LEVEL, message);
  }

  /**
   * Print to StdError a message if the debug level is higher than the one passed in the level.
   *
   * @param level Level of this message, to be compared with the current debug level
   * @param message Debugging message
   */
  public static void debug(int level, String message) {
    if (debug) {
      if (level <= debugLevel) {
        System.err.println(message);
      }
    }
  }

  private static boolean processCommandLine(String[] args) {

    // Reset all the flags, to avoid multiple sequence runs interfering
    debug = false;

    options = new Options();
    Option optionHelp = new Option("h", "help", false, "Help");
    Option optionShortUsage = new Option("?", false, "Quick Reference");
    Option optionDebug = Option.builder().option("d")
            .longOpt("debug").hasArg(true).optionalArg(true)
            .desc("Execute in debug mode, optionally with desired level").build();
    Option optionOutputFile = new Option("o", "output", true, "Output File");
    Option optionConfigFile = new Option("c", "config", true,
            "Configuration File");
    Option optionClassesPackagePath = new Option("p", "path", true,
            "Classes Package path");
    Option optionIncludeImports = new Option("i", "includeImport", true,
            "Include imports as define in Java files in path");

    options.addOption(optionHelp);
    options.addOption(optionShortUsage);
    options.addOption(optionDebug);
    options.addOption(optionOutputFile);
    options.addOption(optionConfigFile);
    options.addOption(optionClassesPackagePath);
    options.addOption(optionIncludeImports);

    try {
      CommandLineParser parser = new DefaultParser();

      commandLine = parser.parse(options, args);

      if (commandLine.hasOption(optionDebug.getOpt())) {
        String debugLevelString = commandLine.getOptionValue(optionDebug.getOpt());
        System.err.println(optionDebug.getDescription()
                + " set to [" + debugLevelString + "]");
        if (debugLevelString != null) {
          try {
            int dl = Integer.parseInt(debugLevelString);
            setDebug(dl);
          } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return false;
          }
        } else {
          setDebug(true);
        }
      }
      if (debug) {
        Utils.dump("ARGS", args, System.err);
        Utils.dump("CMD", commandLine.getArgs(), System.err);
      }
      if (commandLine.hasOption("h")) {
        printHelp(options);
        return false;
      }

      if (commandLine.hasOption("?")) {
        printUsage(options);
        return false;
      }

      if (commandLine.getArgs().length > 0) {
        classesPackagePath = commandLine.getArgs()[0];
        if (commandLine.getArgs().length > 1) {
          outputFile = commandLine.getArgs()[1];
        }
      }

      if (commandLine.hasOption(optionClassesPackagePath.getOpt())) {
        classesPackagePath = commandLine.getOptionValue(optionClassesPackagePath.getOpt());
        debug(optionClassesPackagePath.getDescription()
                + " set to [" + classesPackagePath + "]");
      }
      if (commandLine.hasOption(optionIncludeImports.getOpt())) {
        javaPackagePath = commandLine.getOptionValue(optionIncludeImports.getOpt());
        if (!javaPackagePath.endsWith("/")) {
          javaPackagePath = javaPackagePath + "/";
        }
        debug(optionIncludeImports.getDescription() + " set to [" + javaPackagePath + "]");
      }
      if (commandLine.hasOption(optionOutputFile.getOpt())) {
        outputFile = commandLine.getOptionValue(optionOutputFile.getOpt());
        debug(optionOutputFile.getDescription() + " set to [" + outputFile + "]");
      }
      if (commandLine.hasOption(optionConfigFile.getOpt())) {
        configurationFile = commandLine.getOptionValue(optionConfigFile.getOpt());
        debug(optionConfigFile.getDescription() + " set to [" + configurationFile + "]");
      }

    } catch (ParseException | NullPointerException e) {
      System.err.println(e.getMessage());
      printHelp(options);
      return false;
    }
    return true;
  }

  static void printHelp() {
    printHelp(options);
  }

  static void printHelp(Options options) {
    if (null == options) {
      System.err.println("ERROR: No options provided to printHelp");
      return;
    }

    HelpFormatter helper = new HelpFormatter();

    String className = ClassDiagrammerMain.class.getCanonicalName();
    PrintWriter outError = new PrintWriter(System.err);

    helper.printHelp(outError,
            100,
            "java " + className,
            "",
            options,
            4,
            4,
            "");
  }

  static void printUsage(Options options) {
    if (null == options) {
      System.err.println("ERROR: No options provided to printUsage");
      return;
    }

    HelpFormatter helper = new HelpFormatter();

    String className = ClassDiagrammerMain.class.getCanonicalName();
    PrintWriter outError = new PrintWriter(System.err);

    helper.printUsage(outError, 100, "java " + className, options);
  }

  /**
   * Entry point for the CLI version of the ClassDiagrammer.
   *
   * @param args Command Line options
   */
  public static void main(String[] args) {
    reset();

    boolean cliIsCorrect = processCommandLine(args);

    debug("CommandLine parsed [" + cliIsCorrect + "]");

    if (!cliIsCorrect) {
      printHelp();
      return;
    }

    debug("Path              [" + classesPackagePath + "]\n"
            + "Java files Path   [" + javaPackagePath + "]\n"
            + "OutputFile        [" + outputFile + "]\n"
            + "ConfigurationFile [" + configurationFile + "]");

    if ((null == classesPackagePath) || (classesPackagePath.isBlank())) {
      System.err.println("Path not defined  [" + classesPackagePath + "]");
      printHelp();
      return;
    }

    FileOutputStream file = null;

    try {
      PrintStream output = System.out;

      if ((null != outputFile) && (!outputFile.isBlank())) {
        // Creates a FileOutputStream
        file = new FileOutputStream(outputFile);

        // Creates a PrintWriter
        output = new PrintStream(file, true);
      }

      classDiagrammer = new ClassDiagrammer(output);
      classDiagrammer.generateDiagram(classesPackagePath, configurationFile, javaPackagePath);

      if (null != file) {
        file.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}