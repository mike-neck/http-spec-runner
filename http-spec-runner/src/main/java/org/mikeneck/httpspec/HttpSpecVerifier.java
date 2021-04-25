package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.Client;

public interface HttpSpecVerifier {
  @NotNull
  VerificationResult invokeOn(@NotNull Client client);
}
