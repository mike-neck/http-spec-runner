package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.impl.specs.HttpBodyJsonSpec;

class JsonBodyImplTest {

  @Test
  void pathThenToBeString() {
    List<HttpElementSpec> list = new ArrayList<>();
    JsonBodyImpl jsonBody = new JsonBodyImpl(list);

    jsonBody.path("$.name").toBe("John");

    assertAll(
        () -> assertThat(list).hasSize(1),
        () ->
            assertThat(list)
                .satisfies(
                    spec -> assertThat(spec).isInstanceOf(HttpBodyJsonSpec.class),
                    Index.atIndex(0)));
  }

  @Test
  void pathThenToBeLong() {
    List<HttpElementSpec> list = new ArrayList<>();
    JsonBodyImpl jsonBody = new JsonBodyImpl(list);

    jsonBody.path("$.value").toBe(10L);

    assertAll(
        () -> assertThat(list).hasSize(1),
        () ->
            assertThat(list)
                .satisfies(
                    spec -> assertThat(spec).isInstanceOf(HttpBodyJsonSpec.class),
                    Index.atIndex(0)));
  }

  @Test
  void pathThenToBeBoolean() {
    List<HttpElementSpec> list = new ArrayList<>();
    JsonBodyImpl jsonBody = new JsonBodyImpl(list);

    jsonBody.path("$.value").toBe(true);

    assertAll(
        () -> assertThat(list).hasSize(1),
        () ->
            assertThat(list)
                .satisfies(
                    spec -> assertThat(spec).isInstanceOf(HttpBodyJsonSpec.class),
                    Index.atIndex(0)));
  }

  @Test
  void pathThenToBeDouble() {
    List<HttpElementSpec> list = new ArrayList<>();
    JsonBodyImpl jsonBody = new JsonBodyImpl(list);

    jsonBody.path("$.value").toBe(10.23);

    assertAll(
        () -> assertThat(list).hasSize(1),
        () ->
            assertThat(list)
                .satisfies(
                    spec -> assertThat(spec).isInstanceOf(HttpBodyJsonSpec.class),
                    Index.atIndex(0)));
  }

  @Test
  void retrievingSpecs() {
    List<HttpElementSpec> list = new ArrayList<>();
    JsonBodyImpl jsonBody = new JsonBodyImpl(list);

    jsonBody.path("$.name").toBe("John");
    jsonBody.path("$.age").toBe(20);
    jsonBody.path("$.approved").toBe(false);
    jsonBody.path("$.meanTime").toBe(20.15);

    List<HttpElementSpec> actual = jsonBody.getSpecs();

    assertThat(actual).isEqualTo(list);
  }
}
