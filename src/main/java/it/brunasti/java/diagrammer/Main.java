package it.brunasti.java.diagrammer;

import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

// This Main uses https://commons.apache.org/proper/commons-cli/usage.html
public class Main {

    static ClassDiagrammer classDiagrammer;

    static CommandLine cmd;
    static HelpFormatter helper;
    static Options options;

    static String classesPackagePath = "";
    static String outputFile = "";

    static boolean debug = false;
    static boolean dump = false;
    static boolean trace = false;


    public static boolean processCommandLine(String[] args) {
        // Reset all the flags, to avoid multiple sequence runs interfering
        debug = false;
        dump = false;
        trace = false;

        options = new Options();
        Option optionHelp = new Option("h", "help", false, "Help");
        Option optionDebug = new Option("d", "debug", false, "Execute in debug mode");
        Option optionOutputFile = new Option("o", "output", true, "Output File");
        Option optionClassesPackagePath = new Option("p", "path", true, "Classes Package path");

        options.addOption(optionHelp);
        options.addOption(optionDebug);
        options.addOption(optionOutputFile);
        options.addOption(optionClassesPackagePath);

        helper = new HelpFormatter();

        try {
            CommandLineParser parser = new BasicParser();

            cmd = parser.parse(options, args);

            if (cmd.hasOption("d")) {
                debug = true;
            }
            if (debug) {
                Utils.dump("ARGS", args, System.err);
                Utils.dump("CMD", cmd.getArgs(), System.err);
            }
            if (cmd.hasOption("h")) {
                printHelp();
                return false;
            }

            if (cmd.getArgs().length > 0) {
                classesPackagePath = cmd.getArgs()[0];
                if (cmd.getArgs().length > 1) {
                    outputFile = cmd.getArgs()[1];
                }
            }

            if (cmd.hasOption(optionClassesPackagePath.getOpt())) {
                classesPackagePath = cmd.getOptionValue(optionClassesPackagePath.getOpt());
                if (debug) {
                    System.err.println(optionClassesPackagePath.getDescription() + " set to [" + classesPackagePath + "]");
                }
            }
            if (cmd.hasOption(optionOutputFile.getOpt())) {
                outputFile = cmd.getOptionValue(optionOutputFile.getOpt());
                if (debug) {
                    System.err.println(optionOutputFile.getDescription() + " set to [" + outputFile + "]");
                }
            }

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp();
            return false;
        }
        return true;
    }

    private static void printHelp() {
        String className = Main.class.getCanonicalName();
        PrintWriter outError = new PrintWriter(System.err);
        helper.printHelp(outError, helper.defaultWidth, "java " + className + " <query> <options>", "", options, helper.defaultLeftPad, helper.defaultDescPad, "");
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
            System.err.println("Path [" + classesPackagePath + "]");
            System.err.println("OutputFile [" + outputFile + "]");
        }

        if ((null == classesPackagePath) || (classesPackagePath.isBlank())) {
            System.err.println("Path not defined [" + classesPackagePath + "]");
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
        classDiagrammer.generateDiagram(classesPackagePath);

        if (null != file) {
            try {
                file.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}