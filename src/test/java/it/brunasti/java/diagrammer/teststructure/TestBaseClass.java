/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer.teststructure;

import it.brunasti.java.diagrammer.TestConstants;

public class TestBaseClass extends TestAbstractClass implements TestConstants {
  TestBaseClass selfTest;
  TestEnumClass e;

  public TestBaseClass() {
    selfTest = this;
    TestEnumClass e1 = TestEnumClass.FIRST;
    e = e1;
  }

}
