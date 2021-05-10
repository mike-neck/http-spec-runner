package org.mikeneck.httpspec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mikeneck.httpspec.impl.HttpResponseAssertionFactory.failure;
import static org.mikeneck.httpspec.impl.HttpResponseAssertionFactory.success;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.data.Index;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.junit.jupiter.api.Test;

class StdoutExtensionTest {

  static Stdout create(
      Consumer<String> success, Consumer<String> failure, Consumer<String> normal) {
    return new Stdout() {
      @Override
      public void success(@NotNull String line) {
        success.accept(line);
      }

      @Override
      public void failure(@NotNull String line) {
        failure.accept(line);
      }

      @Override
      public void normal(@NotNull String line) {
        normal.accept(line);
      }
    };
  }

  static VerificationResult verificationResult(
      @NotNull String specName, @NotNull HttpResponseAssertion<?>... assertions) {
    return new VerificationResult() {
      @Override
      public @NotNull String specName() {
        return specName;
      }

      @Override
      public @UnmodifiableView @NotNull List<HttpResponseAssertion<?>> allAssertions() {
        return List.of(assertions);
      }
    };
  }

  @Test
  void beforeAllWillNotDoAnything() {
    Stdout stdout =
        create(
            line -> {
              throw new RuntimeException(String.format("success called with %s", line));
            },
            line -> {
              throw new RuntimeException(String.format("failure called with %s", line));
            },
            line -> {
              throw new RuntimeException(String.format("normal called with %s", line));
            });

    StdoutExtension extension = new StdoutExtension(stdout);

    assertThatNoException().isThrownBy(() -> extension.beforeAllSpecs(List.of(() -> "test")));
  }

  @Test
  void beforeEachWillNoDoAnything() {
    Stdout stdout =
        create(
            line -> {
              throw new RuntimeException(String.format("success called with %s", line));
            },
            line -> {
              throw new RuntimeException(String.format("failure called with %s", line));
            },
            line -> {
              throw new RuntimeException(String.format("normal called with %s", line));
            });

    StdoutExtension extension = new StdoutExtension(stdout);

    assertThatNoException().isThrownBy(() -> extension.beforeEachSpec(() -> "test"));
  }

  @Test
  void afterEachWillCallSuccessMethodOfStdoutWithEachSuccessAssertion() {
    List<String> called = new ArrayList<>();
    Stdout stdout =
        create(
            called::add,
            line -> {
              throw new RuntimeException(String.format("failure called with %s", line));
            },
            line -> {
              throw new RuntimeException(String.format("normal called with %s", line));
            });
    StdoutExtension extension = new StdoutExtension(stdout);

    VerificationResult result =
        verificationResult(
            "test-spec", success("http status", 100), success("test-text", "test-expected"));

    extension.afterEachSpec(result);

    assertThat(called)
        .hasSize(2)
        .allSatisfy(line -> assertThat(line).contains("[test-spec]", "ok"))
        .satisfies(line -> assertThat(line).contains("http status"), Index.atIndex(0))
        .satisfies(line -> assertThat(line).contains("test-text"), Index.atIndex(1));
  }

  @Test
  void afterEachWillCallFailureMethodOfStdoutWithEachFailureAssertion() {
    List<String> called = new ArrayList<>();
    Stdout stdout =
        create(
            line -> {
              throw new RuntimeException(String.format("success called with %s", line));
            },
            called::add,
            line -> {
              throw new RuntimeException(String.format("normal called with %s", line));
            });
    StdoutExtension extension = new StdoutExtension(stdout);

    VerificationResult result =
        verificationResult(
            "all-failure-spec",
            failure("http status", 200, 400),
            failure("example value", "foo", "bar"));

    extension.afterEachSpec(result);

    assertThat(called)
        .hasSize(4)
        .satisfies(
            line -> assertThat(line).contains("[all-failure-spec]", "failed"), Index.atIndex(0))
        .satisfies(
            line -> assertThat(line).contains("expected", "actual", "200", "400"), Index.atIndex(1))
        .satisfies(
            line -> assertThat(line).contains("[all-failure-spec]", "failed"), Index.atIndex(2))
        .satisfies(
            line -> assertThat(line).contains("expected", "actual", "foo", "bar"),
            Index.atIndex(3));
  }
}
