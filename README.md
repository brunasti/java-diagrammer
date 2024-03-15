# java-diagrammer
Generate a PlantUML Class diagram from a compiled Java class collection

---

Usage via CLI

```
usage: java it.brunasti.java.diagrammer.Main <query> <options>
 -d,--debug     Execute in debug mode
 -h,--help      Help
 -o,--output    Output File
 -p,--path      Classes Package path
```


---

Execution example:

Single command line execution:

> java it.brunasti.java.diagrammer.Main -p /Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes -o ./temp/output.puml 


More complete command line execution, with all libraries needed:


> java 
> -classpath /Users/paolobrunasti/.m2/repository/org/ow2/asm/asm/9.6/asm-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-util/9.6/asm-util-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-tree/9.6/asm-tree-9.6.jar:/Users/paolobrunasti/.m2/repository/org/ow2/asm/asm-analysis/9.6/asm-analysis-9.6.jar -p /Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes:/Users/paolobrunasti/.m2/repository/org/apache/bcel/bcel/6.5.0/bcel-6.5.0.jar:/Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/lib/org.apache.commons.cli-4.3.1.jar 
> -m java.diagrammer/it.brunasti.java.diagrammer.Main 
> -p /Users/paolobrunasti/Work/Mine/java-diagrammer/java-diagrammer/target/classes 
> -o ./temp/output.puml



