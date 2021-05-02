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

  @Override
  public String getMessage() {
    StringBuilder builder = new StringBuilder(super.getMessage());
    for (VerificationResult result : results) {
      StringBuilder sb = new StringBuilder();
      for (HttpResponseAssertion<?> assertion : result) {
        if (!assertion.isSuccess()) {
          sb.append(assertion.description()).append('\n');
        }
      }
      String description = sb.toString();
      if (!description.isEmpty()) {
        builder.append('\n').append('\n').append(result.specName()).append('\n').append("====");
        builder.append('\n').append(description);
      }
    }
    return builder.toString();
  }
}
