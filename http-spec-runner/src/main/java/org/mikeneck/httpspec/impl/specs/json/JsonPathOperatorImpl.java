package org.mikeneck.httpspec.impl.specs.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.specs.InvalidJsonPathException;
import org.mikeneck.httpspec.impl.specs.JsonItem;
import org.mikeneck.httpspec.impl.specs.JsonItemNotFoundException;
import org.mikeneck.httpspec.impl.specs.JsonPathOperator;
import org.mikeneck.httpspec.impl.specs.JsonPathProduct;
import org.mikeneck.httpspec.impl.specs.UnexpectedBodyTextException;

public class JsonPathOperatorImpl implements JsonPathOperator {

  static final Configuration CONFIGURATION =
      new Configuration.ConfigurationBuilder()
          .jsonProvider(new JacksonJsonNodeJsonProvider())
          .mappingProvider(new JacksonMappingProvider())
          .build();

  @NotNull private final String path;

  public JsonPathOperatorImpl(@NotNull String path) {
    this.path = path;
  }

  @Override
  public @NotNull JsonPathProduct apply(@NotNull String json) {
    return new JsonPathProduct() {
      @Override
      public @NotNull String path() {
        return path;
      }

      @Override
      public @NotNull JsonItem get() {
        try {
          JsonPath jsonPath = JsonPath.compile(path);
          JsonNode node = jsonPath.read(json, CONFIGURATION);
          return JsonItemFactory.fromNode(node);
        } catch (IllegalArgumentException | InvalidJsonException e) {
          throw new UnexpectedBodyTextException(e.getMessage(), json);
        } catch (PathNotFoundException e) {
          String message = e.getMessage();
          if (message.contains("No results for path: ")) {
            throw new JsonItemNotFoundException(e, json);
          } else {
            throw new UnexpectedBodyTextException(e, json);
          }
        } catch (InvalidPathException e) {
          throw new InvalidJsonPathException(e, json);
        }
      }
    };
  }
}
