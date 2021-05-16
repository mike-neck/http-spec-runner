package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public class StdoutExtension implements Extension {

  private final Stdout stdout;

  public StdoutExtension(Stdout stdout) {
    this.stdout = stdout;
  }

  @Override
  public void beforeAllSpecs(@NotNull Iterable<@NotNull ? extends SpecName> allSpecNames) {}

  @Override
  public void beforeEachSpec(@NotNull SpecName specName) {}

  @Override
  public void afterEachSpec(@NotNull VerificationResult result) {
    String specName = String.format("[%s]", result.specName());
    for (HttpResponseAssertion<?> assertion : result.allAssertions()) {
      if (assertion.isSuccess()) {
        String message = String.format("%s%s - ok", specName, assertion.subtitle());
        stdout.success(message);
      } else {
        stdout.failure(String.format("%s - failed", specName));
        stdout.failure(assertion.description());
      }
    }
  }

  @Override
  public void afterAllSpecs(@NotNull Iterable<@NotNull ? extends VerificationResult> results) {}
}
