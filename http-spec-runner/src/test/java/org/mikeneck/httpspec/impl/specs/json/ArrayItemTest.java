package org.mikeneck.httpspec.impl.specs.json;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ArrayItemTest {

  @TestFactory
  Stream<DynamicTest> allTests() {
    return new JsonItemTestFactory<>(
            ArrayItem.class,
            ArrayItem.ofString("foo", "bar", "baz"),
            new IntItem(20L),
            ArrayItem.ofString("qux", "quux"),
            ArrayItem.ofString("foo", "bar", "baz"))
        .allTests();
  }
}
