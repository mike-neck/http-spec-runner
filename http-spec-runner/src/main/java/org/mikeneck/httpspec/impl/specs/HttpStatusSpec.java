package org.mikeneck.httpspec.impl.specs;

import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public class HttpStatusSpec implements HttpElementSpec {

  private final int expectedStatusCode;

  public HttpStatusSpec(int expectedStatusCode) {
    this.expectedStatusCode = expectedStatusCode;
  }

  @Override
  @NotNull
  public HttpResponseAssertion<Integer> apply(@NotNull HttpResponse<byte[]> httpResponse) {
    int actualStatusCode = httpResponse.statusCode();
    if (actualStatusCode == expectedStatusCode) {
      return HttpResponseAssertion.success(expectedStatusCode);
    } else {
      return HttpResponseAssertion.failure(expectedStatusCode, actualStatusCode);
    }
  }
}
