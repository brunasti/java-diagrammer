# Generation of JavaDoc for Java-diagrammer

## Execution of the javadoc command to generate the documentation from the Java source of the current project


In the directory java-diagrammer/src/main/java
execute the command following multiline command:
```
javadoc it.brunasti.java.diagrammer \
-sourcepath . \
-verbose \
-d ../../../../javadoc  \
-classpath /Users/paolobrunasti/.m2/repository/org/ow2/asm/asm/9.6/asm-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-util/9.6/asm-util-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-tree/9.6/asm-tree-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-analysis/9.6/asm-analysis-9.6.jar:/Users/paolobrunasti/.m2/repository/junit/junit/4.10/junit-4.10.jar:/Users/paolobrunasti/.m2/repository/org/graalvm/js/js/22.0.0/js-22.0.0.jar:/Users/paolobrunasti/.m2/repository/org/graalvm/regex/regex/22.0.0/regex-22.0.0.jar:/Users/paolobrunasti/.m2/repository/org/graalvm/truffle/truffle-api/22.0.0/truffle-api-22.0.0.jar:/Users/paolobrunasti/.m2/repository/org/graalvm/sdk/graal-sdk/22.0.0/graal-sdk-22.0.0.jar:/Users/paolobrunasti/.m2/repository/org/graalvm/js/js-scriptengine/22.0.0/js-scriptengine-22.0.0.jar:/Users/paolobrunasti/.m2/repository/org/hamcrest/hamcrest-core/1.2.1/hamcrest-core-1.2.1.jar -p /Users/paolobrunasti/.m2/repository/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar:/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes:/Users/paolobrunasti/.m2/repository/com/thoughtworks/qdox/qdox/2.0.3/qdox-2.0.3.jar:/Users/paolobrunasti/.m2/repository/net/sourceforge/plantuml/plantuml-gplv2/1.2024.3/plantuml-gplv2-1.2024.3.jar:/Users/paolobrunasti/.m2/repository/org/apache/bcel/bcel/6.5.0/bcel-6.5.0.jar:/Users/paolobrunasti/.m2/repository/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar
```

