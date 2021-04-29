package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HttpResponseAssertion<@NotNull T> {

  @NotNull
  T expected();

  @Nullable
  T actual();

  @NotNull
  String description();

  boolean isSuccess();
}
