package org.mikeneck.httpspec.impl.specs;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.specs.json.JsonPathReaderImpl;

public class HttpBodyJsonSpec implements HttpElementSpec {

  @NotNull private final JsonPathReader jsonPathReader;
  @NotNull private final JsonItem expectedValue;

  public HttpBodyJsonSpec(@NotNull String path, @NotNull JsonItem expectedValue) {
    this.jsonPathReader = new JsonPathReaderImpl(path);
    this.expectedValue = expectedValue;
  }

  @Override
  public @NotNull HttpResponseAssertion<?> apply(@NotNull HttpResponse<byte[]> httpResponse) {
    byte[] bytes = httpResponse.body();
    String body = new String(bytes, StandardCharsets.UTF_8);
    JsonPathProduct jsonPathProduct = jsonPathReader.read(body);
    return expectedValue.testJson(jsonPathProduct);
  }
}
