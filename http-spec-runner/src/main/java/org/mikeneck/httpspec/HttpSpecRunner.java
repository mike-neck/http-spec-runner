package org.mikeneck.httpspec;

import java.io.File;
import java.net.http.HttpClient;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpSpecRunner {

  @NotNull
  static Builder builder() {
    ServiceLoader<Builder> serviceLoader = ServiceLoader.load(Builder.class);
    return serviceLoader
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException("Implementation of HttpSpecRunner.Builder is not found"));
  }

  @NotNull
  static HttpSpecRunner from(@NotNull File yamlFile) {
    @SuppressWarnings("NullableProblems")
    Client client = HttpClient::newHttpClient;
    return from(yamlFile, client, Extension.noOp());
  }

  @SuppressWarnings("NullableProblems")
  @NotNull
  static HttpSpecRunner from(@NotNull File yamlFile, @NotNull Extension extension) {
    @SuppressWarnings("NullableProblems")
    Client client = HttpClient::newHttpClient;
    return from(yamlFile, client, extension);
  }

  @NotNull
  static HttpSpecRunner from(@NotNull File yamlFile, @NotNull Client client) {
    return from(yamlFile, client, Extension.noOp());
  }

  @NotNull
  static HttpSpecRunner from(
      @NotNull File yamlFile, @NotNull Client client, @NotNull Extension extension) {
    ServiceLoader<FileLoader> serviceLoader = ServiceLoader.load(FileLoader.class);
    FileLoader fileLoader =
        serviceLoader
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("implementation of FileLoader is not found"));
    return fileLoader.load(yamlFile, client, extension);
  }

  @NotNull
  List<@NotNull VerificationResult> runForResult();

  void run();

  @NotNull
  Iterable<@NotNull VerificationResult> runningAsIterable();

  @NotNull
  HttpSpecRunner addExtension(@NotNull Extension anotherExtension);

  interface BaseBuilder {

    @NotNull
    HttpSpecRunner build() throws IllegalArgumentException;

    @NotNull
    HttpSpecRunner build(@NotNull Client client) throws IllegalArgumentException;

    @NotNull
    HttpSpecRunner build(@NotNull Extension extension) throws IllegalArgumentException;

    @NotNull
    HttpSpecRunner build(@NotNull Client client, @NotNull Extension extension)
        throws IllegalArgumentException;
  }

  interface Builder extends BaseBuilder {

    void addSpec(@NotNull Consumer<@NotNull ? super HttpSpec> configuration);
  }
}
