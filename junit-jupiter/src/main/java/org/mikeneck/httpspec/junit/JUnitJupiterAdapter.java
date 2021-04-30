package org.mikeneck.httpspec.junit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicTest;
import org.mikeneck.httpspec.HttpSpecRunner;

public class JUnitJupiterAdapter {

  public static @NotNull Stream<@NotNull DynamicTest> apply(
      @NotNull HttpSpecRunner httpSpecRunner) {
    return Stream.generate(() -> httpSpecRunner)
        .limit(1L)
        .map(HttpSpecRunner::runForResult)
        .flatMap(List::stream)
        .flatMap(
            result ->
                result.allAssertions().stream()
                    .map(
                        assertion -> {
                          String description = assertion.description().replace("\n", ", ");
                          return DynamicTest.dynamicTest(
                              String.format("spec: %s %s", result.specName(), description),
                              () -> assertTrue(assertion.isSuccess(), assertion::description));
                        }));
  }
}
