package org.mikeneck.httpspec;

import java.io.File;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface HttpSpecRunner {

  static Builder builder() {
    ServiceLoader<Builder> serviceLoader = ServiceLoader.load(Builder.class);
    return serviceLoader
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException("Implementation of HttpSpecRunner.Builder is not found"));
  }

  static HttpSpecRunner from(File yamlFile) {
    throw new UnsupportedOperationException("not implemented");
  }

  @NotNull
  List<@NotNull VerificationResult> runForResult();

  void run();

  interface BaseBuilder {

    @NotNull
    HttpSpecRunner build();

    @NotNull
    HttpSpecRunner build(@NotNull Client client);

    @NotNull
    HttpSpecRunner build(@NotNull Extension extension);

    @NotNull
    HttpSpecRunner build(@NotNull Client client, @NotNull Extension extension);
  }

  interface Builder extends BaseBuilder {

    void addSpec(@NotNull Consumer<@NotNull ? super HttpSpec> configuration);
  }
}
