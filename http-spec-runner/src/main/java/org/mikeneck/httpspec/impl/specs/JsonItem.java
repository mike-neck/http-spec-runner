package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public interface JsonItem {

  String describeValue();

  @NotNull
  default HttpResponseAssertion<?> testJson(@NotNull JsonPathProduct jsonPathProduct) {
    return jsonPathProduct.assertBy(this);
  }
}
