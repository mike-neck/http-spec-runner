package org.mikeneck.httpspec.impl.specs;

import java.net.http.HttpResponse;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpHeaderItem;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpHeaderItemImpl;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public class HttpHeadersSpec implements HttpElementSpec {

  @NotNull private final HttpHeaderItem httpHeaderItem;

  public HttpHeadersSpec(String name, String value) {
    this.httpHeaderItem = new HttpHeaderItemImpl(name, value);
  }

  @Override
  public @NotNull HttpResponseAssertion<?> apply(@NotNull HttpResponse<byte[]> httpResponse) {
    if (httpHeaderItem.canBeFoundIn(httpResponse)) {
      List<HttpHeaderItem> actualHeaders = httpHeaderItem.extractSameNameHeaders(httpResponse);
      return HttpResponseAssertion.itemFoundInCollection(httpHeaderItem, actualHeaders);
    } else {
      return HttpResponseAssertion.failure(httpHeaderItem, null);
    }
  }
}
