package org.mikeneck.httpspec.file.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.HttpSpec;
import org.mikeneck.httpspec.HttpSpecRunner;

class SpecsTest {

  @Test
  void nullSpecsThrowsIllegalStateException() {
    Specs specs = new Specs();
    assertThatThrownBy(specs::build).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void emptySpecsThrowsIllegalStateException() {
    Specs specs = new Specs();
    specs.spec = List.of();
    assertThatThrownBy(specs::build).isInstanceOf(IllegalStateException.class);
  }

  @TestFactory
  Iterable<DynamicTest> canBuildFromSingleSpec() {
    HttpSpec[] httpSpecs = new HttpSpec[1];
    HttpResponseSpec[] httpResponseSpecs = new HttpResponseSpec[1];

    Specs specs = new Specs();
    specs.spec =
        List.of(
            new Spec(
                "test",
                httpSpec -> httpSpecs[0] = httpSpec,
                httpResponseSpec -> httpResponseSpecs[0] = httpResponseSpec));

    HttpSpecRunner httpSpecRunner = specs.build();
    return List.of(
        dynamicTest(
            "httpSpecRunner should be returned", () -> assertThat(httpSpecRunner).isNotNull()),
        dynamicTest(
            "httpSpec configuration is called",
            () -> assertThat(httpSpecs).hasSize(1).allMatch(Objects::nonNull)),
        dynamicTest(
            "httpResponseSpec configuration is called",
            () -> assertThat(httpResponseSpecs).hasSize(1).allMatch(Objects::nonNull)));
  }
}
