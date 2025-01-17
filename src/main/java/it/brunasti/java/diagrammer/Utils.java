package it.brunasti.java.diagrammer;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import org.json.simple.JSONObject;


/**
 * Generic utility functions.
 */
public final class Utils {

  Utils() {
  }

  /**
   * Read into memory (String) the content of a file.
   *
   * @param fileName Name of the file to be read from.
   * @return The content of the file as a String.
   *     In case of exception, an empty String.
   */
  public static String readFileToString(String fileName) {
    return it.brunasti.java.utils.Utils.readFileToString(fileName);
  }

  /**
   * Writes to System.out the content of the Object[] array.
   *
   * @param title Identification title of the output text.
   * @param array Array of Objects to be dumped.
   */
  public static void dump(final String title, final Object[] array) {
    it.brunasti.java.utils.Utils.dump(title, array);
  }

  /**
   * Writes to the passes PrintStream the content of the Object[] array.
   *
   * @param title Identification title of the output text.
   * @param array Array of Objects to be dumped to the output PrintStream
   * @param output PrintStream to be written to.
   */
  public static void dump(final String title, final Object[] array, PrintStream output) {
    it.brunasti.java.utils.Utils.dump(title, array, output);
  }

  /**
   * Writes to System.out the content of an ArrayList of Objects.
   *
   * @param title Identification title of the output text.
   * @param array ArrayList of Objects to be dumped.
   */
  public static void dump(final String title, final ArrayList<Object> array) {
    it.brunasti.java.utils.Utils.dump(title, array);
  }

  /**
   * Writes to System.out the content of a Set of Objects.
   *
   * @param title Identification title of the output text.
   * @param set Set of Objects to be dumped.
   */
  public static void dump(final String title, final Set<Object> set) {
    it.brunasti.java.utils.Utils.dump(title, set);
  }

  // Directories functions -----------------------------------------

  /**
   * List files in a directory.
   *
   * @param dir Directory from which extract the list of files
   * @return Set of file names
   * @throws IOException If the directory is not found
   */
  public static Set<String> listFiles(final String dir)
          throws IOException {
    return it.brunasti.java.utils.Utils.listFiles(dir);
  }

  /**
   * List directories in a directory.
   *
   * @param dir Directory from which extract the list of directories
   * @return Set of directory names
   * @throws IOException If the root directory is not found
   */
  public static Set<String> listDirectories(final String dir)
          throws IOException {
    return it.brunasti.java.utils.Utils.listDirectories(dir);
  }


  // JSON functions ----------------------------------

  /**
   * Load a JSON File content.
   *
   * @param jsonFileName Name of the file in JSON format
   * @return The JSONObject corresponding to the file content
   */
  public static JSONObject loadJsonFile(String jsonFileName) {
    return it.brunasti.java.utils.Utils.loadJsonFile(jsonFileName);
  }

  public static int countLinesInFile(String jsonFileName) {
    return it.brunasti.java.utils.Utils.countLinesInFile(jsonFileName);
  }


}
