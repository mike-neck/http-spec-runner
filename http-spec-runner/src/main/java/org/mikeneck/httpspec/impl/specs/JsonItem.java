package org.mikeneck.httpspec.impl.specs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.impl.HttpResponseAssertion;

public interface JsonItem {

  @NotNull
  default HttpResponseAssertion<?> testJson(@NotNull JsonPathProduct jsonPathProduct) {
    Optional<JsonItem> optional = jsonPathProduct.get();
    if (optional.isEmpty()) {
      return HttpResponseAssertion.failure(this, null);
    }
    JsonItem item = optional.get();
    if (equals(item)) {
      return HttpResponseAssertion.success(this);
    } else {
      return HttpResponseAssertion.failure(this, item);
    }
  }
}
