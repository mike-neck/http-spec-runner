package org.mikeneck.httpspec.impl.specs;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.httpspec.HttpResponseAssertion;

public interface JsonPathProduct {

  @NotNull
  String path();

  @NotNull
  Optional<@NotNull JsonItem> get();

  default @NotNull HttpResponseAssertion<JsonItem> assertBy(@NotNull JsonItem expectedItem) {
    try {
      Optional<@NotNull JsonItem> fromRemote = get();
      if (fromRemote.isEmpty()) {
        return new JsonItemResponseAssertionFailure(path(), expectedItem);
      }
      JsonItem actual = fromRemote.get();
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
