package it.brunasti.java.diagrammer;

import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.PrintStream;

// This Main uses https://commons.apache.org/proper/commons-cli/usage.html
public class Main {

//    static String query = "X";
    static String classesPackagePath = "";

    static String outputFile = "";

    static boolean debug = false;
    static boolean dump = false;
    static boolean trace = false;




    public static boolean processCommandLine(String[] args) {

        // Reset all the flags, to avoid multiple sequence runs interfering
        debug = true;
        dump = false;
        trace = false;

        Options options = new Options();
//        Option optionTrace = new Option("T", "trace", true, "Trace the execution in a file");
//        Option optionDump = new Option("D", "dump", true, "Dump the KnowledgeBase at the end of the execution");
        Option optionHelp = new Option("h", "help", false, "Help");
        Option optionDebug = new Option("d", "debug", false, "Execute in debug mode");
        Option optionOutputFile = new Option("o", "output", true, "Output File");
        Option optionClassesPackagePath = new Option("p", "path", true, "Classes Package path");
//        Option optionFactsFile = new Option("f", "facts", true, "KnowledgeBase facts file");
//        Option optionRulesFile = new Option("r", "rules", true, "KnowledgeBase rules file");
//        Option optionKnowledgeBaseFile = new Option("k", "knowledge", true, "KnowledgeBase JSON file");
//        Option optionNoJSON = new Option("x", "no-json", false, "Read from non JSON format");

        options.addOption(optionHelp);
//        options.addOption(optionDump);
//        options.addOption(optionTrace);
        options.addOption(optionDebug);
//        options.addOption(optionKnowledgeBaseFile);
//        options.addOption(optionFactsFile);
//        options.addOption(optionRulesFile);
//        options.addOption(optionNoJSON);
        options.addOption(optionOutputFile);
        options.addOption(optionClassesPackagePath);

        HelpFormatter helper = new HelpFormatter();

        try {

            CommandLine cmd;
            CommandLineParser parser = new BasicParser();

            cmd = parser.parse(options, args);

            if (cmd.hasOption("d")) {
                debug = true;
            }
//            if (cmd.hasOption("D")) {
//                dumpKnowledgeBaseFile = cmd.getOptionValue(optionDump.getLongOpt());
//                if (debug) System.out.println(optionDump.getDescription() + " set to [" + dumpKnowledgeBaseFile + "]");
//                dump = true;
//            }
//            if (cmd.hasOption("T")) {
//                traceFile = cmd.getOptionValue(optionTrace.getLongOpt());
//                if (debug) System.out.println(optionTrace.getDescription() + " set to [" + traceFile + "]");
//                trace = true;
//            }
            if (debug) {
                Utils.dump("ARGS", args, System.err);
                Utils.dump("CMD", cmd.getArgs(), System.err);
            }
            if (cmd.hasOption("h")) {
                helper.printHelp("java Main <query> <options>", options);
                return false;
            }


//            if (cmd.getArgs().length > 0) {
//                query = cmd.getArgs()[0];
//            }



            if (cmd.hasOption(optionClassesPackagePath.getOpt())) {
                System.err.println(optionClassesPackagePath);
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


//            if (cmd.hasOption("f")) {
//                factsFile = cmd.getOptionValue(optionFactsFile.getLongOpt());
//                if (debug) System.out.println(optionFactsFile.getDescription() + " set to [" + factsFile + "]");
//                readFromKB = false;
//            }
//            if (cmd.hasOption("r")) {
//                rulesFile = cmd.getOptionValue(optionRulesFile.getLongOpt());
//                if (debug) System.out.println(optionRulesFile.getDescription() + " set to [" + rulesFile + "]");
//                readFromKB = false;
//            }
//            if (cmd.hasOption("x")) {
//                readJSON = false;
//            }


        } catch (ParseException e) {
            System.err.println(e.getMessage());
            helper.printHelp("Usage:", options);
            return false;
        }
        return true;
    }


    // TODO : find a way to return the output of the elaboration in a "computer oriented" way
    // TODO : decompose into simpler basic functions
    public static void main(String[] args) {
        boolean correctCLI = processCommandLine(args);
        if (debug) { System.err.println("CommandLine parsed [" + correctCLI + "]"); }

        if (!correctCLI) return;


        // TODO : Read the path from args
//        String path = "/Users/paolobrunasti/Work/Mine/java-diagrammer"
//                + "/java-diagrammer/target/classes";
//
////        String outputFile = "./temp/output.puml";
//        String outputFile = "";

        if (debug) {
            System.err.println("Path [" + classesPackagePath + "]");
            System.err.println("OutputFile [" + outputFile + "]");
        }

        FileOutputStream file = null;

        PrintStream output = null;

        if ((null != outputFile) && (!"".equals(outputFile))){
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