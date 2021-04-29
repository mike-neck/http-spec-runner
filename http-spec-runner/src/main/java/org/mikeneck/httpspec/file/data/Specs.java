package org.mikeneck.httpspec.file.data;

import java.net.http.HttpClient;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.HttpSpecRunner;

public class Specs implements Iterable<Spec>, HttpSpecRunner.BaseBuilder {

  public List<Spec> spec;

  @NotNull
  @Override
  public Iterator<Spec> iterator() {
    return spec.iterator();
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Specs{");
    sb.append("spec=").append(spec);
    sb.append('}');
    return sb.toString();
  }

  @SuppressWarnings("NullableProblems")
  private static final Client DEFAULT_CLIENT = HttpClient::newHttpClient;

  @Override
  public @NotNull HttpSpecRunner build() {
    return build(DEFAULT_CLIENT, Extension.noOp());
  }

  @Override
  public @NotNull HttpSpecRunner build(@NotNull Client client) {
    return build(client, Extension.noOp());
  }

  @Override
  public @NotNull HttpSpecRunner build(@NotNull Extension extension) {
    return build(DEFAULT_CLIENT, extension);
  }

  @Override
  public @NotNull HttpSpecRunner build(@NotNull Client client, @NotNull Extension extension) {
    if (spec == null || spec.isEmpty()) {
      throw new IllegalStateException("spec is not configured");
    }
    HttpSpecRunner.Builder builder = HttpSpecRunner.builder();
    for (Spec s : this) {
      builder.addSpec(
          httpSpec -> {
            httpSpec.name(s.name);
            s.request.accept(httpSpec);
            httpSpec.response(s.response);
          });
    }
    return builder.build();
  }
}
