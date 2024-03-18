package it.brunasti.java.diagrammer;

import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

// This Main uses https://commons.apache.org/proper/commons-cli/usage.html
public class Main {

    static ClassDiagrammer classDiagrammer;

    static boolean debug = false;
    static boolean dump = false;
    static boolean trace = false;

    static String classesPackagePath = "";
    static String outputFile = "";
    static String configurationFile = "";

    private static Options options;

    public static boolean processCommandLine(String[] args) {
        CommandLine commandLine;

        // Reset all the flags, to avoid multiple sequence runs interfering
        debug = false;
        dump = false;
        trace = false;

        options = new Options();
        Option optionHelp = new Option("h", "help", false, "Help");
        Option optionDebug = new Option("d", "debug", false, "Execute in debug mode");
        Option optionOutputFile = new Option("o", "output", true, "Output File");
        Option optionConfigFile = new Option("c", "config", true, "Configuration File");
        Option optionClassesPackagePath = new Option("p", "path", true, "Classes Package path");

        options.addOption(optionHelp);
        options.addOption(optionDebug);
        options.addOption(optionOutputFile);
        options.addOption(optionConfigFile);
        options.addOption(optionClassesPackagePath);

        try {
            // TODO : Handle BasicParser deprecation
            CommandLineParser parser = new BasicParser();

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

            if (commandLine.getArgs().length > 0) {
                classesPackagePath = commandLine.getArgs()[0];
                if (commandLine.getArgs().length > 1) {
                    outputFile = commandLine.getArgs()[1];
                }
            }

            if (commandLine.hasOption(optionClassesPackagePath.getOpt())) {
                classesPackagePath = commandLine.getOptionValue(optionClassesPackagePath.getOpt());
                if (debug) {
                    System.err.println(optionClassesPackagePath.getDescription() + " set to [" + classesPackagePath + "]");
                }
            }
            if (commandLine.hasOption(optionOutputFile.getOpt())) {
                outputFile = commandLine.getOptionValue(optionOutputFile.getOpt());
                if (debug) {
                    System.err.println(optionOutputFile.getDescription() + " set to [" + outputFile + "]");
                }
            }
            if (commandLine.hasOption(optionConfigFile.getOpt())) {
                configurationFile = commandLine.getOptionValue(optionConfigFile.getOpt());
                if (debug) {
                    System.err.println(optionConfigFile.getDescription() + " set to [" + configurationFile + "]");
                }
            }

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
            return false;
        }
        return true;
    }

    private static void printHelp() {
        printHelp(options);
    }

    private static void printHelp(Options options) {
        HelpFormatter helper = new HelpFormatter();

        String className = Main.class.getCanonicalName();
        PrintWriter outError = new PrintWriter(System.err);

        // TODO : Handle deprecations
        helper.printHelp(outError, helper.defaultWidth,
                "java " + className + " <query> <options>",
                "",
                options,
                helper.defaultLeftPad,
                helper.defaultDescPad,
                "");
        outError.close();
    }

    public static void main(String[] args) {
        boolean correctCLI = processCommandLine(args);

        if (debug) {
            System.err.println("CommandLine parsed [" + correctCLI + "]");
        }

        if (!correctCLI) {
            printHelp();
            return;
        }

        if (debug) {
            System.err.println("Path              [" + classesPackagePath + "]");
            System.err.println("OutputFile        [" + outputFile + "]");
            System.err.println("ConfigurationFile [" + configurationFile + "]");
        }

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

        classDiagrammer = new ClassDiagrammer(output);
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