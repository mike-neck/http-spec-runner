package org.mikeneck.httpspec.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.VerificationResult;

public class HttpSpecRunnerIterableImpl implements Iterable<@NotNull VerificationResult> {

  private final @NotNull Client client;
  private final @NotNull List<@NotNull NamedHttpSpecVerifier> httpSpecVerifiers;
  private final @NotNull Extension extension;

  public HttpSpecRunnerIterableImpl(
      @NotNull Client client,
      @NotNull List<@NotNull NamedHttpSpecVerifier> httpSpecVerifiers,
      @NotNull Extension extension) {
    this.client = client;
    this.httpSpecVerifiers = httpSpecVerifiers;
    this.extension = extension;
  }

  @NotNull
  @Override
  public Iterator<@NotNull VerificationResult> iterator() {
    return new Iterator<>() {

      final int lastIndex = httpSpecVerifiers.size() - 1;
      final List<VerificationResult> results = new ArrayList<>(httpSpecVerifiers.size());
      int index = 0;

      @Override
      public boolean hasNext() {
        return index <= lastIndex;
      }

      @Override
      public VerificationResult next() {
        if (index == 0) {
          extension.beforeAllSpecs(httpSpecVerifiers);
        }

        int current = index;
        index++;
        NamedHttpSpecVerifier verifier = httpSpecVerifiers.get(current);
        extension.beforeEachSpec(verifier);

        VerificationResult result = verifier.invokeOn(client);
        results.add(result);

        extension.afterEachSpec(result);

        if (lastIndex < index) {
          extension.afterAllSpecs(Collections.unmodifiableList(results));
        }

        return result;
      }
    };
  }
}
