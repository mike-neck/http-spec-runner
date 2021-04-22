package org.mikeneck.httpspec.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.BodyAssertion;
import org.mikeneck.httpspec.JsonBody;
import org.mikeneck.httpspec.impl.specs.HttpBodyJsonSpec;
import org.mikeneck.httpspec.impl.specs.json.JsonItemFactory;

class JsonBodyImpl implements JsonBody {

  private final List<HttpElementSpec> specs;

  JsonBodyImpl() {
    this(new ArrayList<>());
  }

  JsonBodyImpl(List<HttpElementSpec> specs) {
    this.specs = specs;
  }

  @Override
  public @NotNull BodyAssertion path(@NotNull String jsonPath) {
    return new BodyAssertion() {
      @Override
      public void toBe(String expectedValue) {
        specs.add(new HttpBodyJsonSpec(jsonPath, JsonItemFactory.stringItem(expectedValue)));
      }

      @Override
      public void toBe(long expectedValue) {
        specs.add(new HttpBodyJsonSpec(jsonPath, JsonItemFactory.intItem(expectedValue)));
      }

      @Override
      public void toBe(boolean expectedValue) {
        specs.add(new HttpBodyJsonSpec(jsonPath, JsonItemFactory.booleanItem(expectedValue)));
      }

      @Override
      public void toBe(double expectedValue) {
        specs.add(new HttpBodyJsonSpec(jsonPath, JsonItemFactory.doubleItem(expectedValue)));
      }
    };
  }

  public List<HttpElementSpec> getSpecs() {
    return Collections.unmodifiableList(specs);
  }
}
