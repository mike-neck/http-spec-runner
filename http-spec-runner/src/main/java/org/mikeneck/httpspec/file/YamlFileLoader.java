package org.mikeneck.httpspec.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.Client;
import org.mikeneck.httpspec.Extension;
import org.mikeneck.httpspec.FileLoader;
import org.mikeneck.httpspec.HttpSpecRunner;
import org.mikeneck.httpspec.file.data.Specs;

public class YamlFileLoader implements FileLoader {

  @Override
  public @NotNull HttpSpecRunner load(@NotNull File file) {
    @SuppressWarnings("NullableProblems")
    Client client = HttpClient::newHttpClient;
    return load(file, client, Extension.noOp());
  }

  @Override
  public HttpSpecRunner load(
      @NotNull File file, @NotNull Client client, @NotNull Extension extension) {
    try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
      Specs specs = loadSpec(reader);
      return specs.build(client, extension);
    } catch (IOException e) {
      throw new UncheckedIOException(String.format("error while reading %s", file), e);
    }
  }

  @NotNull
  Specs loadSpec(@NotNull Reader reader) {
    try (Stream<String> lines = new BufferedReader(reader).lines()) {
      String yaml = lines.collect(Collectors.joining("\n"));
      return loadSpec(yaml);
    }
  }

  @NotNull
  Specs loadSpec(@NotNull String yaml) {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    try {
      return objectMapper.readValue(yaml, Specs.class);
    } catch (IOException e) {
      throw new UncheckedIOException("error while reading yaml", e);
    }
  }
}
