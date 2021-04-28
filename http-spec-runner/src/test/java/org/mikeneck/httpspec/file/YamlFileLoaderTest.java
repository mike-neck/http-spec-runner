package org.mikeneck.httpspec.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Reader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mikeneck.httpspec.ResourceFile;
import org.mikeneck.httpspec.ResourceFileLoader;
import org.mikeneck.httpspec.file.data.Specs;

@ExtendWith(ResourceFileLoader.class)
class YamlFileLoaderTest {

  @Test
  @ResourceFile("yaml-file/get-spec.yml")
  void loadGetSpec(@NotNull String yaml) {
    YamlFileLoader yamlFileLoader = new YamlFileLoader();
    Specs specs = yamlFileLoader.loadSpec(yaml);

    assertThat(specs).hasSize(1);
  }

  @Test
  @ResourceFile("yaml-file/get-spec.yml")
  void loadGetSpec(@NotNull Reader reader) {
    YamlFileLoader yamlFileLoader = new YamlFileLoader();
    Specs specs = yamlFileLoader.loadSpec(reader);

    assertThat(specs).hasSize(1);
  }
}
