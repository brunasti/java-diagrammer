package it.brunasti.java.diagrammer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {


    public static void dump(String t, Object[] arr) {
        System.out.println("--------" + t + "-------------");
        if (arr != null) {
            if (arr.length == 0) {
                System.out.println("-- EMPTY --");
            }
            else {
                for (int i = 0; i < arr.length; i++) {
                    System.out.println("#" + i + "='" + arr[i] + "'");
                }
            }
        } else {
            System.out.println("-- NULL --");
        }
    }

    public static void dump(String t, ArrayList arr) {
        dump(t,arr.toArray());
    }

    public static void dump(String t, Set arr) {
        dump(t,arr.toArray());
    }

    public static Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public static Set<String> listDirectories(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }
}
