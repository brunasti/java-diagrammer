package it.brunasti.java.diagrammer;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassDiagrammer {

    private Set<String> extractClassesListFromDirectory(String _path, String _package) {
        return null;
    }

    private Set<String> iterateSubDirectories(String _path, String _package) {
        System.out.println("iterateSubDirectories ================ ");
        Set<String> files = new HashSet<>();
        return iterateSubDirectories(_path,_package,files);
    }
    private Set<String> iterateSubDirectories(String _path, String _package, Set<String> files) {
        String newPath = _path+"/"+_package.replace(".","/");
        System.out.println("iterateSubDirectories ---------- "+_path+" "+_package);
        System.out.println(newPath);
//        System.out.println("---------- ");
        try {
            Utils.dump("Add new files from ["+newPath+"]", Utils.listFilesUsingFilesList(newPath));

            Utils.listFilesUsingFilesList(newPath).forEach(file -> {
                String className = file.replace(".class","");
                files.add(_package+"."+className);
            });
//            files.addAll(Utils.listFilesUsingFilesList(newPath));

            Set<String> dirs = Utils.listDirectories(newPath);

            dirs.forEach(dir -> {
                iterateSubDirectories(_path, _package+"."+dir, files);
            });

            return files;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ClassLoader classLoader = null;

    private void setClassLoader(String path) {
        try {
            System.out.println("setClassLoader ---------- ");
            System.out.println(path);
            System.out.println("---------- ");

            File file = new File(path);

            // Convert File to a URL
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            classLoader = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateDiagram(String path) {
        ArrayList<String> files = new ArrayList<String>();

        try {
            System.out.println("generateDiagram ---------- ");
            System.out.println(path);
            System.out.println("---------- ");

            setClassLoader(path);

            Set<String> dirs = Utils.listDirectories(path);
            System.out.println(dirs);
            Utils.dump("Dirs: ", dirs.toArray());

            dirs.forEach(dir -> {
                files.addAll(iterateSubDirectories(path, dir));
            });

            Utils.dump("Files", files);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("BCEL ===========");
        try {
            ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);
            files.forEach(file -> {
                System.out.println(">> "+file);
                try {
                    JavaClass objectClazz = rep.loadClass(file);
                    System.out.println("  class name :"+objectClazz.getClassName());
//                    System.out.println(objectClazz.getInterfaces());
                    Utils.dump(file+" Interfaces", Arrays.stream(objectClazz.getInterfaces()).toArray());
//                    Utils.dump("Methods", Arrays.stream(objectClazz.getMethods()).toArray());
//                    Utils.dump("Attributes", Arrays.stream(objectClazz.getAttributes()).toArray());
                } catch (ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        String path = "/Users/paolobrunasti/Work/BAH/bah-cng-common-lib/build/classes/java/main";

        ClassDiagrammer classDiagrammer = new ClassDiagrammer();
        classDiagrammer.generateDiagram(path);

//        ClassLoader classLoader = null;
//
//        try {
//            Set<String> files = Utils.listDirectories(path);
//            System.out.println(files);
//            Utils.dump("Files", files.toArray());
//
//            File file = new File(path);
//            // Convert File to a URL
//            URL url = file.toURI().toURL();
//            URL[] urls = new URL[]{url};
//
//            // Create a new class loader with the directory
//            classLoader = new URLClassLoader(urls);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("BCEL ===========");
//        try {
//            ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);
//            JavaClass objectClazz = rep.loadClass("com.iairgroup.cng.common.builder.PropositionResponseBuilder");
//            System.out.println(objectClazz.getClassName());
//            System.out.println(objectClazz.getInterfaces());
//            Utils.dump("Interfaces", Arrays.stream(objectClazz.getInterfaces()).toArray());
//            Utils.dump("Methods", Arrays.stream(objectClazz.getMethods()).toArray());
//            Utils.dump("Attributes", Arrays.stream(objectClazz.getAttributes()).toArray());
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

}