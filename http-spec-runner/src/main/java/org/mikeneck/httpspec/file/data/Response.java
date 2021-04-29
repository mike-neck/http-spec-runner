package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.function.Consumer;
import org.mikeneck.httpspec.HttpResponseSpec;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(ResponseImpl.class)})
public interface Response extends Consumer<HttpResponseSpec> {
  @Override
  void accept(HttpResponseSpec httpResponseSpec);
}
