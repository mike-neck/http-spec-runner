package org.mikeneck.httpspec.impl.specs;

import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpResponseAssertionFactory;

public class HttpStatusSpec implements HttpElementSpec {

  private static final String SUBTITLE = "http status";

  private final int expectedStatusCode;

  public HttpStatusSpec(int expectedStatusCode) {
    this.expectedStatusCode = expectedStatusCode;
  }

  @Override
  @NotNull
  public HttpResponseAssertion<?> apply(@NotNull HttpResponse<byte[]> httpResponse) {
    int actualStatusCode = httpResponse.statusCode();
    if (actualStatusCode == expectedStatusCode) {
      return HttpResponseAssertionFactory.success(SUBTITLE, expectedStatusCode);
    } else {
      return HttpResponseAssertionFactory.failure(SUBTITLE, expectedStatusCode, actualStatusCode);
    }
  }

  @Override
  public @NotNull String description() {
    return String.format("expecting status=%d", expectedStatusCode);
  }
}
