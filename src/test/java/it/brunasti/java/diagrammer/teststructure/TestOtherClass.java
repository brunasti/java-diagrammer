/*
 * Copyright (c) 2024.
 * Paolo Brunasti
 */

package it.brunasti.java.diagrammer.teststructure;

import it.brunasti.java.diagrammer.TestConstants;

public class TestOtherClass extends TestAbstractClass implements TestConstants {
  TestBaseClass selfTest;
  TestEnumClass e;

  public TestOtherClass() {
    selfTest = new TestBaseClass();
    TestEnumClass e1 = TestEnumClass.FIRST;
    e = e1;
  }

}
