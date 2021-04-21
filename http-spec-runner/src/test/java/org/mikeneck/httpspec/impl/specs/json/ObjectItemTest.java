package org.mikeneck.httpspec.impl.specs.json;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ObjectItemTest {
  @TestFactory
  Stream<DynamicTest> allTests() {
    return new JsonItemTestFactory<>(
            ObjectItem.class,
            new ObjectItem(
                Map.of(
                    "name",
                    new StringItem("test-name"),
                    "value",
                    new IntItem(20L),
                    "avg",
                    new DoubleItem(10.23),
                    "items",
                    ArrayItem.ofString("foo", "bar"))),
            new DoubleItem(20.11),
            new ObjectItem(Map.of("date", new StringItem("2020-01-02T11:22:33Z"))),
            new ObjectItem(
                Map.of(
                    "name",
                    new StringItem("test-name"),
                    "value",
                    new IntItem(20L),
                    "avg",
                    new DoubleItem(10.23),
                    "items",
                    ArrayItem.ofString("foo", "bar"))))
        .allTests();
  }
}
