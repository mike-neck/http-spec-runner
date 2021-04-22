package org.mikeneck.httpspec.impl.specs.json;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class BooleanItemTest {
  @TestFactory
  Stream<DynamicTest> allTests() {
    return new JsonItemTestFactory<>(
            BooleanItem.class,
            new BooleanItem(true),
            JsonItemFactory.intItem(20L),
            new BooleanItem(false),
            new BooleanItem(true))
        .allTests();
  }
}
