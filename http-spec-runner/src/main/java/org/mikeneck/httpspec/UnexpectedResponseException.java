package org.mikeneck.httpspec;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class UnexpectedResponseException extends RuntimeException {

  private final List<VerificationResult> results;

  public UnexpectedResponseException(
      @NotNull String message, @NotNull List<@NotNull VerificationResult> results) {
    super(message);
    this.results = results;
  }
}
