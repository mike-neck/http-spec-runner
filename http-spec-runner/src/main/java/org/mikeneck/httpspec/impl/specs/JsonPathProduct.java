package org.mikeneck.httpspec.impl.specs;

import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public interface JsonPathProduct {

  @NotNull
  String path();

  @NotNull
  JsonItem get()
      throws UnexpectedBodyTextException, InvalidJsonPathException, JsonItemNotFoundException;

  default @NotNull HttpResponseAssertion<JsonItem> assertBy(@NotNull JsonItem expectedItem) {
    try {
      JsonItem actual = get();
      if (expectedItem.equals(actual)) {
        return new JsonItemResponseAssertionSuccess(path(), expectedItem);
      } else {
        return new JsonItemResponseAssertionFailure(path(), expectedItem, actual);
      }
    } catch (UnexpectedBodyTextException e) {
      return new JsonItemResponseAssertionError(
          "body contains unknown data type", path(), expectedItem, e);
    } catch (InvalidJsonPathException e) {
      return new JsonItemResponseAssertionError(
          "failed to compile jsonpath", path(), expectedItem, e);
    } catch (JsonItemNotFoundException e) {
      return new JsonItemResponseAssertionError(
          "given jsonpath not found", path(), expectedItem, e);
    }
  }
}
