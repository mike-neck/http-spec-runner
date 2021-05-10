package org.mikeneck.httpspec.impl.specs;

import java.net.http.HttpResponse;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpHeaderItem;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.HttpElementSpec;
import org.mikeneck.httpspec.impl.HttpHeaderItemImpl;
import org.mikeneck.httpspec.impl.HttpResponseAssertionFactory;

public class HttpHeadersSpec implements HttpElementSpec {

  @NotNull private final HttpHeaderItem httpHeaderItem;

  public HttpHeadersSpec(String name, String value) {
    this.httpHeaderItem = new HttpHeaderItemImpl(name, value);
  }

  @Override
  public @NotNull HttpResponseAssertion<?> apply(@NotNull HttpResponse<byte[]> httpResponse) {
    String subtitle = String.format("http header(%s)", httpHeaderItem.name());
    if (httpHeaderItem.canBeFoundIn(httpResponse)) {
      List<HttpHeaderItem> actualHeaders = httpHeaderItem.extractSameNameHeaders(httpResponse);
      return HttpResponseAssertionFactory.pairFoundInCollection(
          subtitle, httpHeaderItem, actualHeaders);
    } else {
      return HttpResponseAssertionFactory.failure(subtitle, httpHeaderItem, null);
    }
  }

  @Override
  public @NotNull String description() {
    return String.format(
        "expecting header=%s value=%s", httpHeaderItem.name(), httpHeaderItem.value());
  }
}
