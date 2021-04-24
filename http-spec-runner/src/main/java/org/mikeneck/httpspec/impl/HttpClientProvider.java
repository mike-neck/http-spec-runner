package org.mikeneck.httpspec.impl;

import java.net.http.HttpClient;
import org.jetbrains.annotations.NotNull;

public interface HttpClientProvider {

  @NotNull
  HttpClient get();
}
