package org.mikeneck.httpspec.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mikeneck.httpspec.HttpSpecRunner;

class HttpSpecRunnerBuilderTest {

  @Test
  void addSpec() {
    HttpSpecRunnerBuilder builder = new HttpSpecRunnerBuilder();

    ((HttpSpecRunner.Builder) builder)
        .addSpec(
            httpSpec -> {
              httpSpec.name("builder-test");
              httpSpec.request().get("https://example.com");
              httpSpec.response(response -> response.status(200));
            });

    assertAll(() -> assertThat(builder.size()).isEqualTo(1), () -> assertThat(builder).hasSize(1));
  }

  @Test
  void addSpec2Times() {
    HttpSpecRunnerBuilder builder = new HttpSpecRunnerBuilder();

    ((HttpSpecRunner.Builder) builder)
        .addSpec(
            httpSpec -> {
              httpSpec.name("builder-test");
              httpSpec.request().get("https://example.com");
              httpSpec.response(response -> response.status(200));
            });
    builder.addSpec(
        httpSpec -> {
          httpSpec.name("builder-test2");
          httpSpec.request().get("https://example.com");
          httpSpec.response(response -> response.status(200));
        });

    assertAll(() -> assertThat(builder.size()).isEqualTo(2), () -> assertThat(builder).hasSize(2));
  }

  @Test
  void buildsHttpSpecRunnerImpl() {
    HttpSpecRunnerBuilder builder = new HttpSpecRunnerBuilder();

    ((HttpSpecRunner.Builder) builder)
        .addSpec(
            httpSpec -> {
              httpSpec.name("builder-test");
              httpSpec.request().get("https://example.com");
              httpSpec.response(response -> response.status(200));
            });

    HttpSpecRunner httpSpecRunner = builder.build();

    assertThat(httpSpecRunner).isInstanceOf(HttpSpecRunnerImpl.class);
  }

  @Test
  void configurationShouldBeExecuted() {
    HttpSpecRunnerBuilder builder = new HttpSpecRunnerBuilder();
    List<String> list = new ArrayList<>();

    ((HttpSpecRunner.Builder) builder).addSpec(httpSpec -> list.add(httpSpec.toString()));

    assertThat(list).hasSize(1);
  }

  @Test
  void throwsIllegalArgumentExceptionWhenSpecIsEmpty() {
    assertThatThrownBy(() -> new HttpSpecRunnerBuilder().build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("specs is empty");
  }
}
