package org.mikeneck.httpspec.impl;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.mikeneck.httpspec.VerificationResult;

public class VerificationResultImpl implements VerificationResult {

  @NotNull private final String specName;
  @NotNull @UnmodifiableView private final List<@NotNull HttpResponseAssertion<?>> assertions;

  VerificationResultImpl(
      @NotNull String specName,
      @NotNull @UnmodifiableView List<@NotNull HttpResponseAssertion<?>> assertions) {
    this.specName = specName;
    this.assertions = assertions;
  }

  @Override
  public @NotNull String specName() {
    return specName;
  }

  @Override
  public @UnmodifiableView @NotNull List<HttpResponseAssertion<?>> allAssertions() {
    return assertions;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("VerificationResultImpl{");
    sb.append("specName='").append(specName).append('\'');
    sb.append(", assertions=").append(assertions);
    sb.append('}');
    return sb.toString();
  }
}
