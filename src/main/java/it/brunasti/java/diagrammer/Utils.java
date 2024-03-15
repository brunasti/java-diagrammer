package it.brunasti.java.diagrammer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public static void dump(final String t, final ArrayList arr) {
    dump(t, arr.toArray());
  }

  public static void dump(final String t, final Set arr) {
    dump(t, arr.toArray());
  }

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
}
