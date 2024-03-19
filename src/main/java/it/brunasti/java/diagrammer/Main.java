package it.brunasti.java.diagrammer;

import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

// This Main uses https://commons.apache.org/proper/commons-cli/usage.html
public class Main {

    private static boolean debug = false;

    private static String classesPackagePath = "";
    private static String outputFile = "";
    private static String configurationFile = "";

    private static Options options;

    public static void setDebug(boolean value) {
        debug = value;
    }

    public static void debug(String message) {
        if (debug) {
            System.err.println(message);
        }
    }

    private static boolean processCommandLine(String[] args) {
        CommandLine commandLine;

        // Reset all the flags, to avoid multiple sequence runs interfering
        debug = false;

        options = new Options();
        Option optionHelp = new Option("h", "help", false, "Help");
        Option optionShortUsage = new Option("?", false, "Quick Reference");
        Option optionDebug = new Option("d", "debug", false, "Execute in debug mode");
        Option optionOutputFile = new Option("o", "output", true, "Output File");
        Option optionConfigFile = new Option("c", "config", true, "Configuration File");
        Option optionClassesPackagePath = new Option("p", "path", true, "Classes Package path");

        options.addOption(optionHelp);
        options.addOption(optionShortUsage);
        options.addOption(optionDebug);
        options.addOption(optionOutputFile);
        options.addOption(optionConfigFile);
        options.addOption(optionClassesPackagePath);

        try {
            CommandLineParser parser = new DefaultParser();

            commandLine = parser.parse(options, args);

            if (commandLine.hasOption("d")) {
                debug = true;
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
                debug(optionClassesPackagePath.getDescription() + " set to [" + classesPackagePath + "]");
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

        String className = Main.class.getCanonicalName();
        PrintWriter outError = new PrintWriter(System.err);

        helper.printHelp(outError,
                100,
                "java " + className,
                "",
                options,
                4,
                4,
                "");

        outError.close();
    }

    static void printUsage(Options options) {
        if (null == options) {
            System.err.println("ERROR: No options provided to printUsage");
            return;
        }

        HelpFormatter helper = new HelpFormatter();

        String className = Main.class.getCanonicalName();
        PrintWriter outError = new PrintWriter(System.err);

        helper.printUsage(outError, 100, "java " + className, options);

        outError.close();
    }

    public static void main(String[] args) {
        boolean correctCLI = processCommandLine(args);

        debug("CommandLine parsed [" + correctCLI + "]");

        if (!correctCLI) {
            printHelp();
            return;
        }

        debug(    "Path              [" + classesPackagePath + "]\n"
                + "OutputFile        [" + outputFile + "]\n"
                + "ConfigurationFile [" + configurationFile + "]");

        if ((null == classesPackagePath) || (classesPackagePath.isBlank())) {
            System.err.println("Path not defined  [" + classesPackagePath + "]");
            printHelp();
            return;
        }

        FileOutputStream file = null;

        PrintStream output = null;

        if ((null != outputFile) && (!outputFile.isBlank())){
            try {
                // Creates a FileOutputStream
                file = new FileOutputStream(outputFile);

                // Creates a PrintWriter
                output = new PrintStream(file, true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            output = System.out;
        }

        ClassDiagrammer classDiagrammer = new ClassDiagrammer(output);
        classDiagrammer.generateDiagram(classesPackagePath, configurationFile);

        if (null != file) {
            try {
                file.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}