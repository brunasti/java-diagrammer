package it.brunasti.java.diagrammer;

public interface TestConstants {

  // Test config ---------------------------
  String tempDirectory = "./temp";
  String nonExistingDirectory = "./errorDir";
  String nonExistingDirectoryAndFile = "./errorDir/test.txt";
  String srcDirectory = "./src";
  String javaSrcDirectory = "./src/main/java";
  String classesDirectory = "./target/classes";
  String testClassesDirectory = "./target/test-classes";
  String configurationFileName = "./temp/config.json";
  String outputFileName = "./temp/output.puml";
  String testOutputFileName = "./temp/output-test.puml";
  String wrongFileName = outputFileName;
  String wrongJsonFileName = "./temp/wrong.json";
  String nonExistingJsonFileName = "./temp/nofile.json";


}
