package org.mikeneck.httpspec;

import org.jetbrains.annotations.NotNull;

public interface HttpSpecVerifier {
  @NotNull
  VerificationResult invokeOn(@NotNull Client client);
}
