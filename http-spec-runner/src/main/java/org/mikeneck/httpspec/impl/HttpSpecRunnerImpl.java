package org.mikeneck.httpspec.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.SpecName;
import org.mikeneck.httpspec.VerificationResult;

class HttpSpecRunnerImpl implements HttpSpecRunner, Extension {

  private final @NotNull Client client;
  private final @NotNull List<@NotNull NamedHttpSpecVerifier> httpSpecVerifiers;
  private final @NotNull Extension extension;

  HttpSpecRunnerImpl(
      @NotNull Client client,
      @NotNull Extension extension,
      @NotNull List<@NotNull NamedHttpSpecVerifier> httpSpecVerifiers) {
    this.client = client;
    this.httpSpecVerifiers = httpSpecVerifiers;
    this.extension = extension;
  }

  @Override
  public @NotNull @UnmodifiableView List<@NotNull VerificationResult> runForResult() {
    List<@NotNull VerificationResult> results = new ArrayList<>();
    beforeAllSpecs(httpSpecVerifiers.stream().collect(Collectors.toUnmodifiableList()));
    for (NamedHttpSpecVerifier verifier : httpSpecVerifiers) {
      beforeEachSpec(verifier);
      VerificationResult result = verifier.invokeOn(client);
      afterEachSpec(result);
      results.add(result);
    }
    List<@NotNull VerificationResult> verificationResultList =
        Collections.unmodifiableList(results);
    afterAllSpecs(verificationResultList);
    return verificationResultList;
  }

  @Override
  public void run() {
    runForResult();
  }

  @Override
  public void beforeAllSpecs(@NotNull Iterable<@NotNull SpecName> allSpecNames) {
    extension.beforeAllSpecs(allSpecNames);
  }

  @Override
  public void beforeEachSpec(@NotNull SpecName specName) {
    extension.beforeEachSpec(specName);
  }

  @Override
  public void afterEachSpec(@NotNull VerificationResult result) {
    extension.afterEachSpec(result);
  }

  @Override
  public void afterAllSpecs(@NotNull Iterable<@NotNull VerificationResult> results) {
    extension.afterAllSpecs(results);
  }
}
