/**
 * Generate a PlantUML Class diagram from a collection of compiled Java classes.

 * @since 1.0
 */

module it.brunasti.java.diagrammer {
  requires org.apache.bcel;
  requires commons.cli;
  requires json.simple;
  requires com.thoughtworks.qdox;
  requires net.sourceforge.plantuml;
  requires it.brunasti.java.utils.Utils;
}