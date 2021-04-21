package org.mikeneck.httpspec.impl.specs.json;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class DoubleItemTest {
  @TestFactory
  Stream<DynamicTest> allTests() {
    return new JsonItemTestFactory<>(
            DoubleItem.class,
            new DoubleItem(20.124),
            new ObjectItem(Map.of("name", new StringItem("test"))),
            new DoubleItem(11.22),
            new DoubleItem(20.124))
        .allTests();
  }
}
