package it.brunasti.java.diagrammer;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import org.json.*;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public final class Utils {

  private Utils() {
  }

  public static void dump(final String t, final Object[] arr) {
    dump(t,arr,System.out);
  }

  public static void dump(final String t, final Object[] arr, PrintStream output) {
    output.println("--------" + t + "-------------");
    if (arr != null) {
      if (arr.length == 0) {
        output.println("-- EMPTY --");
      } else {
        for (int i = 0; i < arr.length; i++) {
          output.println("#" + i + "='" + arr[i] + "'");
        }
      }
    } else {
      output.println("-- NULL --");
    }
  }

  public static void dump(final String t, final ArrayList<Object> arr) {
    dump(t, arr.toArray());
  }

  public static void dump(final String t, final Set<Object> arr) {
    dump(t, arr.toArray());
  }

  // Directories functions -----------------------------------------

  public static Set<String> listFilesUsingFilesList(final String dir)
          throws IOException {
    try (Stream<Path> stream = Files.list(Paths.get(dir))) {
      return stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::getFileName)
              .map(Path::toString)
              .collect(Collectors.toSet());
    }
  }

  public static Set<String> listDirectories(final String dir)
          throws IOException {
    try (Stream<Path> stream = Files.list(Paths.get(dir))) {
      return stream
              .filter(file -> Files.isDirectory(file))
              .map(Path::getFileName)
              .map(Path::toString)
              .collect(Collectors.toSet());
    }
  }

  // JSON functions ----------------------------------
  public static void testJsonRead() {
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(new FileReader("./temp/config.json"));

      System.err.println(obj);

      // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
      JSONObject jsonObject = (JSONObject) obj;
      // A JSON array. JSONObject supports java.util.List interface.
      JSONObject exclude = (JSONObject) jsonObject.get("exclude");
      JSONArray classes = (JSONArray) exclude.get("classes");
      // An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
      // Iterators differ from enumerations in two ways:
      // 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
      // 2. Method names have been improved.
      Iterator iterator = classes.iterator();
      while (iterator.hasNext()) {
        System.out.println(iterator.next());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static JSONObject loadConfigurationFile(String configurationFileName) {
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(new FileReader(configurationFileName));

      // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
      return (JSONObject) obj;
    } catch (Exception e) {
      e.printStackTrace(System.err);
      return null;
    }
  }

}
