package it.brunasti.java.diagrammer;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ClassDiagrammer {

//    private Set<String> extractClassesListFromDirectory(String _path, String _package) {
//        return null;
//    }

    private Set<String> iterateSubDirectories(String _path, String _package) {
//        System.out.println("iterateSubDirectories ================ ");
        Set<String> files = new HashSet<>();
        return iterateSubDirectories(_path,_package,files);
    }
    private Set<String> iterateSubDirectories(String _path, String _package, Set<String> files) {
        String newPath = _path+"/"+_package.replace(".","/");
//        System.out.println("iterateSubDirectories ---------- "+_path+" "+_package);
//        System.out.println(newPath);
        try {
//            Utils.dump("Add new files from ["+newPath+"]", Utils.listFilesUsingFilesList(newPath));

            Utils.listFilesUsingFilesList(newPath).forEach(file -> {
                String className = file.replace(".class","");
                files.add(_package+"."+className);
            });

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
//            System.out.println("setClassLoader ---------- ");
//            System.out.println(path);
//            System.out.println("---------- ");

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


    private static HashSet toBeExcludedPackages = null;
    private static HashSet toBeExcludedClasses = null;
    private void initToBeExcluded() {
        toBeExcludedPackages = new HashSet();
        toBeExcludedClasses = new HashSet();

        toBeExcludedPackages.add("java.lang.");
        toBeExcludedPackages.add("java.util.");

//        toBeExcludedClasses.add("int");
//        toBeExcludedClasses.add("boolean");
//        toBeExcludedClasses.add("double");
//        toBeExcluded.add("java.lang.Float");
//        toBeExcluded.add("java.lang.Long");
//        toBeExcluded.add("java.lang.Double");
//        toBeExcluded.add("java.lang.Integer");
//        toBeExcluded.add("java.lang.String");

        toBeExcludedClasses.add("org.slf4j.Logger");
        toBeExcludedClasses.add("org.joda.time.DateTime");
    }


    private boolean isFieldTypeToBeConnected(JavaClass objectClazz, Field field) {
        if (toBeExcludedPackages == null) {
            initToBeExcluded();
        }

        String type =  field.getType().toString();

        if (type.equals(objectClazz.getClassName())) {
            return false;
        }

        // If the type is all lowercase, then is a primitive type
        if (type.toLowerCase().equals(type)) {
            return false;
        }
//        if (type.startsWith("java.lang.")) {
//            return false;
//        }

        AtomicReference<Boolean> excludedPackage = new AtomicReference<>(false);
        toBeExcludedPackages.forEach(_package -> {
            if (type.startsWith(_package.toString())) {
                excludedPackage.set(true);
            }
        });

        if (excludedPackage.get() == true) {
            return false;
        }

        if (toBeExcludedClasses.contains(type)) {
            return false;
        }
        return true;
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

            dirs.forEach(dir -> {
                files.addAll(iterateSubDirectories(path, dir));
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println( "@startuml" );
        System.out.println( "'https://plantuml.com/class-diagram" );
        System.out.println();
        System.out.println();
        System.out.println("' GENERATE CLASS DIAGRAM ===========");
        Date now = new Date();
        System.out.println("' "+ now.toString());
        System.out.println("' "+ this.getClass().getName());
        System.out.println();
        try {
            ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);

            ArrayList<JavaClass> classes = new ArrayList<>();

            files.forEach(file -> {
                try {
                    JavaClass objectClazz = rep.loadClass(file);
                    classes.add(objectClazz);
                } catch (ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                }
            });

            System.out.println();
            System.out.println();
            System.out.println("' CLASSES =======");
            classes.forEach( objectClazz -> {
                if (objectClazz.isEnum()) {
                    System.out.println("enum " + objectClazz.getClassName());
                } else if (objectClazz.isInterface()) {
                    System.out.println("interface " + objectClazz.getClassName());
                } else if (objectClazz.isAbstract()) {
                    System.out.println("abstract " + objectClazz.getClassName());
                } else {
                    System.out.println("class " + objectClazz.getClassName());
                }
            });
            System.out.println();

            System.out.println("' INHERITANCES =======");
            classes.forEach( objectClazz -> {
                try {
                    if (!"java.lang.Object".equals(objectClazz.getSuperClass().getClassName())) {
                        System.out.println("" + objectClazz.getClassName() + " --|> " + objectClazz.getSuperClass().getClassName());
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            });
            System.out.println();

//            System.out.println("ATTRIBUTES =======");
//            classes.forEach( objectClazz -> {
//                System.out.println("class " + objectClazz.getClassName());
//                try {
//                    Attribute[] attributes = objectClazz.getAttributes();
//                    for (int i = 0; i < attributes.length; i++) {
//                        Attribute attribute = attributes[i];
//                        System.out.println("  --  attribute name :" + attribute.getName() +"  "+attribute.getClass().getName());
//                    }
//
//                } catch (Exception ex) {
//                    System.err.println(ex.getMessage());
//                }
//            });
//            System.out.println();



            System.out.println("' FIELDS =======");
            classes.forEach( objectClazz -> {
                if (objectClazz.isEnum()) {

                } else {
//                System.out.println("class " + objectClazz.getClassName());
                    try {
                        Field[] fields = objectClazz.getFields();
                        for (int i = 0; i < fields.length; i++) {
                            Field field = fields[i];
                            if (isFieldTypeToBeConnected(objectClazz, field)) {
//                                System.out.println();
//                                System.out.println("  --  attribute name :" + field.getName() + "  " + field.getSignature());
//                                System.out.println("      " + field.getType() + "  " + field.getModifiers());
                                System.out.println("" + objectClazz.getClassName() + " --> " + field.getType());
                            }

                        }

                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            });
            System.out.println();

//            classes.forEach( objectClazz -> {
//                try {
//                    System.out.println("class " + objectClazz.getClassName());
//
//                    JavaClass[] interfaces = objectClazz.getInterfaces();
//
//                    System.out.println("  class name :" + objectClazz.getClassName());
//                    System.out.println("  -- super class name :" + objectClazz.getSuperClass().getClassName());
//
//                    if (interfaces.length > 0) {
//                        for (int i = 0; i < interfaces.length; i++) {
//                            JavaClass _interface = interfaces[i];
//                            System.out.println("  --  class name :" + _interface.getClassName());
//
//                        }
//                    }
////                    Utils.dump(file+" Interfaces", objectClazz.getInterfaces());
//                } catch (ClassNotFoundException e) {
//                    System.err.println(e.getMessage());
//                }
//            });
            System.out.println( "@enduml" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        String path = "/Users/paolobrunasti/Work/BAH/bah-cng-common-lib/build/classes/java/main";

        ClassDiagrammer classDiagrammer = new ClassDiagrammer();
        classDiagrammer.generateDiagram(path);
    }

}