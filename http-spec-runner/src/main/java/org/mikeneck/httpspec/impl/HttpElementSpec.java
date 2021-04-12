package org.mikeneck.httpspec.impl;

import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

public interface HttpElementSpec {
  @NotNull
  HttpResponseAssertion<Integer> apply(@NotNull HttpResponse<byte[]> httpResponse);
}
