package org.mikeneck.httpspec;

import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public interface VerificationResult extends Iterable<HttpResponseAssertion<?>> {

  @NotNull
  String specName();

  @UnmodifiableView
  @NotNull
  List<HttpResponseAssertion<?>> allAssertions();

  @NotNull
  @Override
  default Iterator<HttpResponseAssertion<?>> iterator() {
    return allAssertions().iterator();
  }
}
