package org.mikeneck.httpspec.impl;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpHeaderItem;

public class HttpHeaderItemImpl implements HttpHeaderItem {

  @NotNull private final String name;

  @NotNull private final String value;

  public HttpHeaderItemImpl(@NotNull String name, @NotNull String values) {
    this.name = name;
    this.value = values;
  }

  @Override
  public boolean canBeFoundIn(@NotNull HttpResponse<byte[]> httpResponse) {
    Map<String, List<String>> headers = httpResponse.headers().map();
    Stream<Map.Entry<String, List<String>>> stream = matchingHeaders(headers);
    return stream
        .flatMap(header -> header.getValue().stream())
        .anyMatch(value -> value.equals(value()));
  }

  @NotNull
  private Stream<Map.Entry<String, List<String>>> matchingHeaders(
      Map<String, List<String>> headers) {
    return headers.entrySet().stream().filter(header -> header.getKey().equalsIgnoreCase(name));
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull String value() {
    return value;
  }

  @NotNull
  @Override
  public List<HttpHeaderItem> extractSameNameHeaders(HttpResponse<byte[]> httpResponse) {
    Map<String, List<String>> headers = httpResponse.headers().map();
    Stream<Map.Entry<String, List<String>>> stream = matchingHeaders(headers);
    return stream
        .flatMap(
            header ->
                header.getValue().stream()
                    .map(value -> new HttpHeaderItemImpl(header.getKey(), value)))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("HttpHeaderItemImpl{");
    sb.append("name='").append(name).append('\'');
    sb.append(", value='").append(value).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HttpHeaderItemImpl)) return false;
    HttpHeaderItemImpl that = (HttpHeaderItemImpl) o;
    return name.equals(that.name) && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
