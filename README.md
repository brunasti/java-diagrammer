# java-diagrammer
Generate a PlantUML Class diagram from a compiled Java class collection


Execution example:

java it.brunasti.java.diagrammer.Main -p /Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes -o ./temp/output.puml 




java -classpath /Users/paolobrunasti/.m2/repository/org/ow2/asm/asm/9.6/asm-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-util/9.6/asm-util-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-tree/9.6/asm-tree-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-analysis/9.6/asm-analysis-9.6.jar -p /Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes:/Users/paolobrunasti/.m2/repository/org/apache/bcel/bcel/6.5.0/bcel-6.5.0.jar:/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/lib/org.apache.commons.cli-4.3.1.jar -m java.diagrammer/it.brunasti.java.diagrammer.Main -p /Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes -o ./temp/output.puml



