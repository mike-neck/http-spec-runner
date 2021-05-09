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
                              @NotNull Iterable<@NotNull ? extends SpecName> allSpecNames) {
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
                              @NotNull Iterable<@NotNull ? extends VerificationResult> results) {
                            afterAll.accept(results);
                          }
                        };
  }

  void beforeAllSpecs(@NotNull Iterable<@NotNull ? extends SpecName> allSpecNames);

  void beforeEachSpec(@NotNull SpecName specName);

  void afterEachSpec(@NotNull VerificationResult result);

  void afterAllSpecs(@NotNull Iterable<@NotNull ? extends VerificationResult> results);

  default @NotNull Extension merge(@NotNull Extension nest) {
    return Extension.builder()
        .onCallBeforeAllSpecs(
            all -> {
              this.beforeAllSpecs(all);
              nest.beforeAllSpecs(all);
            })
        .onCallBeforeEachSpecs(
            each -> {
              this.beforeEachSpec(each);
              nest.beforeEachSpec(each);
            })
        .onCallAfterEachSpecs(
            each -> {
              nest.afterEachSpec(each);
              this.afterEachSpec(each);
            })
        .onCallAfterAllSpecs(
            all -> {
              nest.afterAllSpecs(all);
              this.afterAllSpecs(all);
            })
        .build();
  }

  interface Builder {
    @NotNull
    Extension build();
  }

  @FunctionalInterface
  interface BeforeAllSpecsRegistry extends BeforeEachSpecsRegistry {
    @NotNull
    @Override
    default AfterEachSpecsRegistry onCallBeforeEachSpecs(
        @NotNull Consumer<@NotNull ? super SpecName> beforeEach) {
      return onCallBeforeAllSpecs(specNames -> {}).onCallBeforeEachSpecs(beforeEach);
    }

    @NotNull
    BeforeEachSpecsRegistry onCallBeforeAllSpecs(
        @NotNull Consumer<@NotNull ? super Iterable<@NotNull ? extends SpecName>> beforeAll);
  }

  @FunctionalInterface
  interface BeforeEachSpecsRegistry extends AfterEachSpecsRegistry {
    @Override
    default @NotNull AfterAllSpecsRegistry onCallAfterEachSpecs(
        Consumer<@NotNull ? super VerificationResult> afterEach) {
      return onCallBeforeEachSpecs(specName -> {}).onCallAfterEachSpecs(afterEach);
    }

    @NotNull
    AfterEachSpecsRegistry onCallBeforeEachSpecs(
        @NotNull Consumer<@NotNull ? super SpecName> beforeEach);
  }

  @FunctionalInterface
  interface AfterEachSpecsRegistry extends AfterAllSpecsRegistry {
    @Override
    @NotNull
    default Builder onCallAfterAllSpecs(
        @NotNull
            Consumer<@NotNull ? super Iterable<@NotNull ? extends VerificationResult>> afterAll) {
      return onCallAfterEachSpecs(result -> {}).onCallAfterAllSpecs(afterAll);
    }

    @NotNull
    AfterAllSpecsRegistry onCallAfterEachSpecs(
        Consumer<@NotNull ? super VerificationResult> afterEach);
  }

  interface AfterAllSpecsRegistry extends Builder {
    default @Override @NotNull Extension build() {
      return onCallAfterAllSpecs(result -> {}).build();
    }

    @NotNull
    Builder onCallAfterAllSpecs(
        @NotNull
            Consumer<@NotNull ? super Iterable<@NotNull ? extends VerificationResult>> afterAll);
  }
}
