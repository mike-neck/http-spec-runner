package org.mikeneck.httpspec.impl.specs;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.specs.json.JsonPathOperatorImpl;

public class HttpBodyJsonSpec implements HttpElementSpec {

  @NotNull private final JsonPathOperator jsonPathOperator;
  @NotNull private final JsonItem expectedValue;
  @NotNull private final String path;

  public HttpBodyJsonSpec(@NotNull String path, @NotNull JsonItem expectedValue) {
    this.path = path;
    this.jsonPathOperator = new JsonPathOperatorImpl(path);
    this.expectedValue = expectedValue;
  }

  @Override
  public @NotNull HttpResponseAssertion<?> apply(@NotNull HttpResponse<byte[]> httpResponse) {
    byte[] bytes = httpResponse.body();
    String body = new String(bytes, StandardCharsets.UTF_8);
    JsonPathProduct jsonPathProduct = jsonPathOperator.apply(body);
    return jsonPathProduct.assertBy(expectedValue);
  }

  @Override
  public @NotNull String description() {
    return String.format("expecting path=[%s] value=%s", path, expectedValue.describeValue());
  }
}
