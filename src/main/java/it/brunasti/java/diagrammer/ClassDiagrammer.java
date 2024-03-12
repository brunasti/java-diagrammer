package it.brunasti.java.diagrammer;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;

public class ClassDiagrammer {

    private Set<String> extractClassesListFromDirectory(String _path, String _package) {
        return null;
    }

    private Set<String> iterateSubDirectories(String _path, String _package) {
        try {
            Set<String> files = Utils.listDirectories(_path);
            return files;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ClassLoader classLoader = null;

    private void setClassLoader(String path) {
        try {
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
        try {
            setClassLoader(path);

            Set<String> files = Utils.listDirectories(path);
            System.out.println(files);
            Utils.dump("Files", files.toArray());

//            File file = new File(path);
//            // Convert File to a URL
//            URL url = file.toURI().toURL();
//            URL[] urls = new URL[]{url};
//
//            // Create a new class loader with the directory
//            classLoader = new URLClassLoader(urls);

//            // Load in the class; MyClass.class should be located in
//            // the directory file:/c:/myclasses/com/mycompany
//            Class myclass  = classLoader.loadClass("com.iairgroup.cng.common.builder.PropositionResponseBuilder");
//            System.out.println(myclass);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("BCEL ===========");
        try {
            ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);
            JavaClass objectClazz = rep.loadClass("com.iairgroup.cng.common.builder.PropositionResponseBuilder");
            System.out.println(objectClazz.getClassName());
            System.out.println(objectClazz.getInterfaces());
            Utils.dump("Interfaces", Arrays.stream(objectClazz.getInterfaces()).toArray());
            Utils.dump("Methods", Arrays.stream(objectClazz.getMethods()).toArray());
            Utils.dump("Attributes", Arrays.stream(objectClazz.getAttributes()).toArray());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        String path = "/Users/paolobrunasti/Work/BAH/bah-cng-common-lib/build/classes/java/main/";

        ClassLoader classLoader = null;

        try {
            Set<String> files = Utils.listDirectories(path);
            System.out.println(files);
            Utils.dump("Files", files.toArray());

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

        System.out.println("BCEL ===========");
        try {
            ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);
            JavaClass objectClazz = rep.loadClass("com.iairgroup.cng.common.builder.PropositionResponseBuilder");
            System.out.println(objectClazz.getClassName());
            System.out.println(objectClazz.getInterfaces());
            Utils.dump("Interfaces", Arrays.stream(objectClazz.getInterfaces()).toArray());
            Utils.dump("Methods", Arrays.stream(objectClazz.getMethods()).toArray());
            Utils.dump("Attributes", Arrays.stream(objectClazz.getAttributes()).toArray());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}