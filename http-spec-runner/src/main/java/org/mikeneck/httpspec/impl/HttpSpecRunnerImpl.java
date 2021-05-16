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
import org.mikeneck.httpspec.UnexpectedResponseException;
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
  public void run() throws UnexpectedResponseException, IllegalStateException {
    List<@NotNull VerificationResult> results = runForResult();
    List<@NotNull VerificationResult> failures =
        results.stream()
            .filter(
                result ->
                    result.allAssertions().stream().anyMatch(assertion -> !assertion.isSuccess()))
            .collect(Collectors.toUnmodifiableList());
    if (!failures.isEmpty()) {
      StringBuilder sb = new StringBuilder("There are failed tests in ");
      String specNames =
          failures.stream()
              .map(VerificationResult::specName)
              .collect(Collectors.joining(",", "[", "]"));
      sb.append(specNames);
      throw new UnexpectedResponseException(sb.toString(), failures);
    }
  }

  @Override
  public @NotNull Iterable<VerificationResult> runningAsIterable() {
    return new HttpSpecRunnerIterableImpl(client, httpSpecVerifiers, extension);
  }

  @Override
  public @NotNull HttpSpecRunner addExtension(@NotNull Extension anotherExtension) {
    Extension extension = this.extension.merge(anotherExtension);
    return new HttpSpecRunnerImpl(client, extension, httpSpecVerifiers);
  }

  @Override
  public void beforeAllSpecs(@NotNull Iterable<@NotNull ? extends SpecName> allSpecNames) {
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
  public void afterAllSpecs(@NotNull Iterable<@NotNull ? extends VerificationResult> results) {
    extension.afterAllSpecs(results);
  }
}
