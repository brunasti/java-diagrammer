/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer.teststructure;

import it.brunasti.java.diagrammer.TestConstants;

public class TestBaseClass extends TestAbstractClass implements TestConstants {
  TestBaseClass test;

  public TestBaseClass() {
    test = this;
  }

}
