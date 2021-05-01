package org.mikeneck.httpspec;

import java.net.http.HttpResponse;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface HttpHeaderItem extends NameValuePair<String> {

  boolean canBeFoundIn(@NotNull HttpResponse<byte[]> httpResponse);

  @Override
  @NotNull
  String name();

  @Override
  @NotNull
  String value();

  @NotNull
  List<HttpHeaderItem> extractSameNameHeaders(HttpResponse<byte[]> httpResponse);
}
