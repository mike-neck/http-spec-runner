package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mikeneck.httpspec.BodyAssertion;
import org.mikeneck.httpspec.HttpResponseSpec;
import org.mikeneck.httpspec.JsonBody;
import org.mikeneck.httpspec.impl.Multimap;

class MockHttpResponseSpec implements HttpResponseSpec {

  int status = -1;
  final Multimap headers = new Multimap();
  final List<@NotNull Predicate<@NotNull String>> jsonBodies = new ArrayList<>();

  @Override
  public void status(int expectedHttpStatus) {
    this.status = expectedHttpStatus;
  }

  @Override
  public void header(String expectedHeaderName, String expectedHeaderValue) {
    headers.add(expectedHeaderName, expectedHeaderValue);
  }

  private static final Configuration CONFIGURATION =
      new Configuration.ConfigurationBuilder()
          .jsonProvider(new JacksonJsonNodeJsonProvider())
          .mappingProvider(new JacksonMappingProvider())
          .build();

  @SafeVarargs
  @NotNull
  private static <T> Predicate<@NotNull String> json(
      @NotNull String jsonPath,
      @NotNull Function<@NotNull ? super JsonNode, @Nullable ? extends T> transform,
      @NotNull Predicate<@Nullable ? super T>... predicates) {
    return json -> {
      JsonNode node = JsonPath.compile(jsonPath).read(json, CONFIGURATION);
      T value = transform.apply(node);
      for (Predicate<? super T> predicate : predicates) {
        if (!predicate.test(value)) return false;
      }
      return true;
    };
  }

  @Override
  public void jsonBody(@NotNull Consumer<@NotNull JsonBody> jsonContentsTest) {
    JsonBody jsonBody =
        jsonPath ->
            new BodyAssertion() {
              @Override
              public void toBe(String expectedValue) {
                jsonBodies.add(
                    json(
                        jsonPath,
                        JsonNode::textValue,
                        string ->
                            (string == null && expectedValue == null)
                                || (string != null && string.equals(expectedValue))));
              }

              @Override
              public void toBe(long expectedValue) {
                jsonBodies.add(
                    json(
                        jsonPath,
                        JsonNode::longValue,
                        Objects::nonNull,
                        value -> value == expectedValue));
              }

              @Override
              public void toBe(boolean expectedValue) {
                jsonBodies.add(
                    json(
                        jsonPath,
                        JsonNode::booleanValue,
                        Objects::nonNull,
                        value -> value == expectedValue));
              }

              @Override
              public void toBe(double expectedValue) {
                jsonBodies.add(
                    json(
                        jsonPath,
                        JsonNode::doubleValue,
                        Objects::nonNull,
                        value -> value == expectedValue));
              }
            };
    jsonContentsTest.accept(jsonBody);
  }
}
