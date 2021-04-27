package org.mikeneck.httpspec.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.FileLoader;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.file.data.Specs;

public class YamlFileLoader implements FileLoader {

  @Override
  public @NotNull HttpSpecRunner load(@NotNull File file) {
    return null;
  }

  public @NotNull Specs load(@NotNull Reader reader) {
    try (Stream<String> lines = new BufferedReader(reader).lines()) {
      String yaml = lines.collect(Collectors.joining("\n"));
      return load(yaml);
    }
  }

  public @NotNull Specs load(@NotNull String yaml) {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    try {
      return objectMapper.readValue(yaml, Specs.class);
    } catch (IOException e) {
      throw new UncheckedIOException("error while reading yaml", e);
    }
  }
}
