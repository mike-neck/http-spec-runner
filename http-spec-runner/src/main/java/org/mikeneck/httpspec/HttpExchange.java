package org.mikeneck.httpspec;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.mikeneck.httpspec.impl.Client;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public interface HttpExchange {
  @NotNull
  @UnmodifiableView
  List<HttpResponseAssertion<?>> run(@NotNull Client client);
}
