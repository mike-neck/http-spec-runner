package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public interface NameValuePair<T> {

  @NotNull
  String name();

  @NotNull
  T value();
}
