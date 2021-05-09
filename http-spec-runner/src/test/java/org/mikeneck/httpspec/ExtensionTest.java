package org.mikeneck.httpspec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ExtensionTest {

  static DynamicTest noException(String name, Runnable runnable) {
    return DynamicTest.dynamicTest(name, () -> assertThatNoException().isThrownBy(runnable::run));
  }

  static <@NotNull T> Iterable<T> throwingIterable(String message) {
    return () -> {
      throw new RuntimeException(message);
    };
  }

  static class ThrowingVerificationResult implements VerificationResult {

    final String message;

    ThrowingVerificationResult(String message) {
      this.message = message;
    }

    @Override
    public @NotNull String specName() {
      throw new RuntimeException(message);
    }

    @Override
    public @UnmodifiableView @NotNull List<HttpResponseAssertion<?>> allAssertions() {
      throw new RuntimeException(message);
    }
  }

  static class MockVerificationResult implements VerificationResult {
    final String name;

    MockVerificationResult(String name) {
      this.name = name;
    }

    @Override
    public @NotNull String specName() {
      return name;
    }

    @Override
    public @UnmodifiableView @NotNull List<HttpResponseAssertion<?>> allAssertions() {
      return List.of();
    }
  }

  @TestFactory
  Iterable<DynamicTest> buildWithoutAnyCallback() {
    Extension.Builder builder = Extension.builder();
    Extension extension = builder.build();

    return List.of(
        noException(
            "beforeAllSpecs", () -> extension.beforeAllSpecs(throwingIterable("beforeAll"))),
        noException(
            "beforeEachSpec",
            () ->
                extension.beforeEachSpec(
                    () -> {
                      throw new RuntimeException("beforeEach");
                    })),
        noException(
            "afterEach",
            () -> extension.afterEachSpec(new ThrowingVerificationResult("afterEach"))),
        noException("afterAll", () -> extension.afterAllSpecs(throwingIterable("afterAll"))));
  }

  @TestFactory
  Iterable<DynamicTest> buildWithBeforeAllSpecsOnly() {
    List<SpecName> specNames = new ArrayList<>();

    Extension.BeforeAllSpecsRegistry registry = Extension.builder();
    Extension.Builder builder =
        registry.onCallBeforeAllSpecs(iterable -> iterable.forEach(specNames::add));

    Extension extension = builder.build();

    return List.of(
        DynamicTest.dynamicTest(
            "beforeAllSpecs",
            () -> extension.beforeAllSpecs(List.of(() -> "test1", () -> "test2"))),
        noException(
            "beforeEachSpec",
            () ->
                extension.beforeEachSpec(
                    () -> {
                      throw new RuntimeException("beforeEach");
                    })),
        noException(
            "afterEach",
            () -> extension.afterEachSpec(new ThrowingVerificationResult("afterEach"))),
        noException("afterAll", () -> extension.afterAllSpecs(throwingIterable("afterAll"))),
        DynamicTest.dynamicTest("size should be 2", () -> assertThat(specNames).hasSize(2)));
  }

  @SuppressWarnings("NullableProblems")
  @TestFactory
  Iterable<DynamicTest> buildWithBeforeEachSpecOnly() {
    List<String> specNames = new ArrayList<>();

    Extension.BeforeEachSpecsRegistry registry = Extension.builder();
    Consumer<SpecName> beforeEach = specName -> specNames.add(specName.specName());
    Extension.Builder builder = registry.onCallBeforeEachSpecs(beforeEach);

    Extension extension = builder.build();

    Iterator<@NotNull String> names = List.of("foo", "bar").iterator();

    return List.of(
        noException(
            "beforeAllSpecs", () -> extension.beforeAllSpecs(throwingIterable("beforeAll"))),
        DynamicTest.dynamicTest(
            "beforeEachSpec",
            () -> {
              while (names.hasNext()) {
                extension.beforeEachSpec(names::next);
              }
            }),
        noException(
            "afterEach",
            () -> extension.afterEachSpec(new ThrowingVerificationResult("afterEach"))),
        noException("afterAll", () -> extension.afterAllSpecs(throwingIterable("afterAll"))),
        DynamicTest.dynamicTest("size should be 2", () -> assertThat(specNames).hasSize(2)));
  }

  @TestFactory
  Iterable<DynamicTest> buildWithAfterEachSpecOnly() {
    List<String> specNames = new ArrayList<>();

    Extension.AfterEachSpecsRegistry registry = Extension.builder();
    Consumer<VerificationResult> afterEach =
        verificationResult -> specNames.add(verificationResult.specName());
    Extension.Builder builder = registry.onCallAfterEachSpecs(afterEach);

    Extension extension = builder.build();

    Iterator<VerificationResult> results =
        List.<VerificationResult>of(
                new MockVerificationResult("foo"), new MockVerificationResult("bar"))
            .iterator();

    return List.of(
        noException(
            "beforeAllSpecs", () -> extension.beforeAllSpecs(throwingIterable("beforeAll"))),
        noException(
            "beforeEachSpec",
            () ->
                extension.beforeEachSpec(
                    () -> {
                      throw new RuntimeException("beforeEach");
                    })),
        DynamicTest.dynamicTest(
            "afterEach",
            () -> {
              while (results.hasNext()) {
                extension.afterEachSpec(results.next());
              }
            }),
        noException("afterAll", () -> extension.afterAllSpecs(throwingIterable("afterAll"))),
        DynamicTest.dynamicTest("size should be 2", () -> assertThat(specNames).hasSize(2)));
  }

  @TestFactory
  Iterable<DynamicTest> buildWithAfterAllSpecsOnly() {
    List<String> specNames = new ArrayList<>();

    Extension.AfterAllSpecsRegistry registry = Extension.builder();
    Consumer<? super Iterable<? extends VerificationResult>> afterAll =
        results -> results.forEach(result -> specNames.add(result.specName()));
    Extension.Builder builder = registry.onCallAfterAllSpecs(afterAll);

    Extension extension = builder.build();

    List<VerificationResult> results =
        List.of(new MockVerificationResult("foo"), new MockVerificationResult("bar"));

    return List.of(
        noException(
            "beforeAllSpecs", () -> extension.beforeAllSpecs(throwingIterable("beforeAll"))),
        noException(
            "beforeEachSpec",
            () ->
                extension.beforeEachSpec(
                    () -> {
                      throw new RuntimeException("beforeEach");
                    })),
        noException(
            "afterEach",
            () -> extension.afterEachSpec(new ThrowingVerificationResult("afterEach"))),
        noException("afterAll", () -> extension.afterAllSpecs(results)),
        DynamicTest.dynamicTest("size should be 2", () -> assertThat(specNames).hasSize(2)));
  }
}
