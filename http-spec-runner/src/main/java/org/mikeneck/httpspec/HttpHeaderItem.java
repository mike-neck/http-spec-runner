package org.mikeneck.httpspec;

import java.net.http.HttpResponse;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface HttpHeaderItem {

  boolean canBeFoundIn(@NotNull HttpResponse<byte[]> httpResponse);

  @NotNull
  String name();

  @NotNull
  String value();

  @NotNull
  List<HttpHeaderItem> extractSameNameHeaders(HttpResponse<byte[]> httpResponse);
}
