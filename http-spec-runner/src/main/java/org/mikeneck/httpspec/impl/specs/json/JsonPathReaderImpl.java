package org.mikeneck.httpspec.impl.specs.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.specs.JsonItem;
import org.mikeneck.httpspec.impl.specs.JsonPathProduct;
import org.mikeneck.httpspec.impl.specs.JsonPathReader;

public class JsonPathReaderImpl implements JsonPathReader {

  static final Configuration CONFIGURATION =
      new Configuration.ConfigurationBuilder()
          .jsonProvider(new JacksonJsonNodeJsonProvider())
          .mappingProvider(new JacksonMappingProvider())
          .build();

  @NotNull private final JsonPath jsonPath;
  @NotNull private final String path;

  public JsonPathReaderImpl(@NotNull String path) {
    this.jsonPath = JsonPath.compile(path);
    this.path = path;
  }

  @Override
  public @NotNull JsonPathProduct read(@NotNull String json) {
    return new JsonPathProduct() {
      @Override
      public @NotNull String path() {
        return path;
      }

      @Override
      public @NotNull Optional<@NotNull JsonItem> get() {
        try {
          JsonNode node = jsonPath.read(json, CONFIGURATION);
          JsonItem item = JsonItemFactory.fromNode(node);
          return Optional.of(item);
        } catch (PathNotFoundException | UnsupportedOperationException ignored) {
          System.out.println("JsonPathReaderImpl$JsonPathProduct:");
          System.out.println(json);
          return Optional.empty();
        }
      }
    };
  }
}
