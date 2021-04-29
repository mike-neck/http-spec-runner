package org.mikeneck.httpspec.impl;

import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public interface HttpElementSpec {
  @NotNull
  HttpResponseAssertion<?> apply(@NotNull HttpResponse<byte[]> httpResponse);

  @NotNull
  String description();
}
