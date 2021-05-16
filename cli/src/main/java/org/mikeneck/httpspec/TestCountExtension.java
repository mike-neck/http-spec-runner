package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public class TestCountExtension implements Extension {

  private int successSpecCount;
  private int failedSpecCount;
  private int successAssertionCount;
  private int failedAssertionCount;

  boolean isSuccess() {
    return failedSpecCount == 0;
  }

  @Override
  public void beforeAllSpecs(@NotNull Iterable<@NotNull ? extends SpecName> allSpecNames) {}

  @Override
  public void beforeEachSpec(@NotNull SpecName specName) {}

  @Override
  public void afterEachSpec(@NotNull VerificationResult result) {
    int success = 0;
    int failure = 0;
    for (HttpResponseAssertion<?> assertion : result) {
      if (assertion.isSuccess()) {
        success++;
      } else {
        failure++;
      }
    }
    successAssertionCount += success;
    failedAssertionCount += failure;
    if (failure == 0 && 0 < success) {
      successSpecCount++;
    } else if (0 < failure) {
      failedSpecCount++;
    }
  }

  @Override
  public void afterAllSpecs(@NotNull Iterable<@NotNull ? extends VerificationResult> results) {}

  public void writeCount(@NotNull Stdout stdout) {
    if (isSuccess()) {
      stdout.normal("spec runner finished");
      stdout.success(String.format("all-spec         : %6d", successSpecCount));
      stdout.success(String.format("failed-spec      : %6d", 0));
      stdout.success(String.format("all-assertions   : %6d", successAssertionCount));
      stdout.success(String.format("failed-assertions: %6d", 0));
    } else {
      stdout.failure("spec runner finished");
      stdout.normal(String.format("all-spec         : %6d", successSpecCount + failedSpecCount));
      stdout.failure(String.format("failed-spec      : %6d", failedSpecCount));
      stdout.normal(
          String.format("all-assertions   : %6d", successAssertionCount + failedAssertionCount));
      stdout.failure(String.format("failed-assertions: %6d", failedAssertionCount));
    }
  }
}
