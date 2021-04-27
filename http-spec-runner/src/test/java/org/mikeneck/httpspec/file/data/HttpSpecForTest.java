package org.mikeneck.httpspec.file.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.mikeneck.httpspec.HttpRequestMethodSpec;
import org.mikeneck.httpspec.HttpRequestSpec;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.HttpSpec;

public interface HttpSpecForTest {

  @TestOnly
  static HttpSpecInterceptor newHttpSpec() {
    return new HttpSpecInterceptor();
  }

  class HttpSpecInterceptor implements HttpSpec {

    String specName;
    HttpRequestSpecInterceptor request = null;
    String url;

    @Override
    public void name(@NotNull String specName) {
      this.specName = specName;
    }

    private void setUrl(String url) {
      this.url = url;
    }

    @SuppressWarnings("Convert2Lambda")
    @Override
    public @NotNull HttpRequestMethodSpec request() {
      this.request = new HttpRequestSpecInterceptor();
      HttpRequestSpecInterceptor request = this.request;
      return new HttpRequestMethodSpec() {
        @Override
        public void get(
            @NotNull String url,
            @NotNull Consumer<@NotNull ? super HttpRequestSpec> configuration) {
          setUrl(url);
          configuration.accept(request);
        }
      };
    }

    @Override
    public void response(@NotNull Consumer<@NotNull ? super HttpResponseSpec> configuration) {}
  }

  class HttpRequestSpecInterceptor implements HttpRequestSpec {

    final Map<String, List<Object>> queries = new HashMap<>();
    final Map<String, List<String>> headers = new HashMap<>();

    private void addQuery(String name, Object value) {
      queries.compute(
          name,
          (key, list) -> {
            if (list == null) {
              ArrayList<Object> objects = new ArrayList<>();
              objects.add(value);
              return objects;
            } else {
              list.add(value);
              return list;
            }
          });
    }

    private void addHeader(String name, Collection<String> values) {
      headers.compute(
          name,
          (key, list) -> {
            if (list == null) {
              return new ArrayList<>(values);
            } else {
              list.addAll(values);
              return list;
            }
          });
    }

    @Override
    public @NotNull HttpRequestSpec query(@NotNull String name, @NotNull String value) {
      addQuery(name, value);
      return this;
    }

    @Override
    public @NotNull HttpRequestSpec query(@NotNull String name, int value) {
      addQuery(name, value);
      return this;
    }

    @Override
    public @NotNull HttpRequestSpec query(@NotNull String name, long value) {
      addQuery(name, value);
      return this;
    }

    @Override
    public @NotNull HttpRequestSpec header(@NotNull String name, @NotNull String... value) {
      addHeader(name, List.of(value));
      return this;
    }
  }
}
