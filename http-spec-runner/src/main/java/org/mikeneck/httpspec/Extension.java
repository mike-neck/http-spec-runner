package org.mikeneck.httpspec;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface Extension {

  static Extension noOp() {
    return builder().build();
  }

  static BeforeAllSpecsRegistry builder() {
    return beforeAll ->
        beforeEach ->
            afterEach ->
                afterAll ->
                    () ->
                        new Extension() {
                          @Override
                          public void beforeAllSpecs(
                              @NotNull Iterable<@NotNull SpecName> allSpecNames) {
                            beforeAll.accept(allSpecNames);
                          }

                          @Override
                          public void beforeEachSpec(@NotNull SpecName specName) {
                            beforeEach.accept(specName);
                          }

                          @Override
                          public void afterEachSpec(@NotNull VerificationResult result) {
                            afterEach.accept(result);
                          }

                          @Override
                          public void afterAllSpecs(
                              @NotNull Iterable<@NotNull VerificationResult> results) {
                            afterAll.accept(results);
                          }
                        };
  }

  void beforeAllSpecs(@NotNull Iterable<@NotNull SpecName> allSpecNames);

  void beforeEachSpec(@NotNull SpecName specName);

  void afterEachSpec(@NotNull VerificationResult result);

  void afterAllSpecs(@NotNull Iterable<@NotNull VerificationResult> results);

  interface Builder {
    @NotNull
    Extension build();
  }

  @FunctionalInterface
  interface BeforeAllSpecsRegistry extends BeforeEachSpecsRegistry {
    @NotNull
    @Override
    default AfterEachSpecsRegistry onCallBeforeEachSpecs(
        @NotNull Consumer<@NotNull SpecName> beforeEach) {
      return onCallBeforeAllSpecs(specNames -> {}).onCallBeforeEachSpecs(beforeEach);
    }

    @NotNull
    BeforeEachSpecsRegistry onCallBeforeAllSpecs(
        @NotNull Consumer<@NotNull Iterable<@NotNull SpecName>> beforeAll);
  }

  @FunctionalInterface
  interface BeforeEachSpecsRegistry extends AfterEachSpecsRegistry {
    @Override
    default @NotNull AfterAllSpecsRegistry onCallAfterEachSpecs(
        Consumer<VerificationResult> afterEach) {
      return onCallBeforeEachSpecs(specName -> {}).onCallAfterEachSpecs(afterEach);
    }

    @NotNull
    AfterEachSpecsRegistry onCallBeforeEachSpecs(@NotNull Consumer<@NotNull SpecName> beforeEach);
  }

  @FunctionalInterface
  interface AfterEachSpecsRegistry extends AfterAllSpecsRegistry {
    @Override
    @NotNull
    default Builder onCallAfterAllSpecs(
        @NotNull Consumer<@NotNull Iterable<@NotNull VerificationResult>> afterAll) {
      return onCallAfterEachSpecs(result -> {}).onCallAfterAllSpecs(afterAll);
    }

    @NotNull
    AfterAllSpecsRegistry onCallAfterEachSpecs(Consumer<VerificationResult> afterEach);
  }

  interface AfterAllSpecsRegistry extends Builder {
    default @Override @NotNull Extension build() {
      return onCallAfterAllSpecs(result -> {}).build();
    }

    @NotNull
    Builder onCallAfterAllSpecs(
        @NotNull Consumer<@NotNull Iterable<@NotNull VerificationResult>> afterAll);
  }
}
