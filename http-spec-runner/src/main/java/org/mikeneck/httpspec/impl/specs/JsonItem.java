package org.mikeneck.httpspec.impl.specs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;
import org.mikeneck.httpspec.impl.HttpResponseAssertionFactory;

public interface JsonItem {

  String describeValue();

  @NotNull
  default HttpResponseAssertion<?> testJson(@NotNull JsonPathProduct jsonPathProduct) {
    Optional<JsonItem> optional = jsonPathProduct.get();
    if (optional.isEmpty()) {
      return HttpResponseAssertionFactory.failure(this, null);
    }
    JsonItem item = optional.get();
    if (equals(item)) {
      return HttpResponseAssertionFactory.success(this);
    } else {
      return HttpResponseAssertionFactory.failure(this, item);
    }
  }
}
