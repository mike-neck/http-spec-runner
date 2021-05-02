package org.mikeneck.httpspec;

import java.io.File;
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

  static HttpSpecRunner from(File yamlFile) {
    ServiceLoader<FileLoader> serviceLoader = ServiceLoader.load(FileLoader.class);
    FileLoader fileLoader =
        serviceLoader
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("implementation of FileLoader is not found"));
    return fileLoader.load(yamlFile);
  }

  @NotNull
  List<@NotNull VerificationResult> runForResult();

  void run();

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
