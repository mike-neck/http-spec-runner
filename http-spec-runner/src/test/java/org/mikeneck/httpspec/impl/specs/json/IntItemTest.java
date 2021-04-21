package org.mikeneck.httpspec.impl.specs.json;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class IntItemTest {

  final IntItem item = new IntItem(200L);

  @TestFactory
  Stream<DynamicTest> allTests() {
    JsonItemTestFactory<IntItem> factory =
        new JsonItemTestFactory<>(
            IntItem.class, item, new StringItem("foo"), new IntItem(100L), new IntItem(200L));
    return factory.allTests();
  }
}
