package org.mikeneck.httpspec.impl.specs.json;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class StringItemTest {

  @TestFactory
  Stream<DynamicTest> allTests() {
    JsonItemTestFactory<StringItem> factory =
        new JsonItemTestFactory<>(
            StringItem.class,
            new StringItem("test"),
            ArrayItem.ofString("foo", "bar"),
            new StringItem("different value"),
            new StringItem("test"));
    return factory.allTests();
  }
}
