/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package it.brunasti.java.diagrammer;

public interface TestConstants {

  // Test config ---------------------------
  String tempDirectory = "./temp";
  String nonExistingDirectory = "./errorDir";
  String nonExistingDirectoryAndFile = "./errorDir/test.txt";


  String srcDirectory = "./src";
  String javaSrcDirectory = "./src/main/java";
  String testJavaSrcDirectory = "./src/test/java";

  String classesDirectory = "./target/classes";
  String testClassesDirectory = "./target/test-classes";

  String outputFileName = "./temp/output.puml";
  String testOutputFileName = "./temp/output-test.puml";

  String configurationFileName = "./temp/config.json";
  String wrongConfigurationFileName = outputFileName;

  String wrongJsonFileName = "./temp/wrong.json";
  String nonExistingJsonFileName = "./temp/nofile.json";

  String defaultLegendFileName = "./temp/default_legend.txt";

}
