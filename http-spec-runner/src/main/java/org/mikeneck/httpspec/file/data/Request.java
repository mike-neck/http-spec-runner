package org.mikeneck.httpspec.file.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.function.Consumer;
import org.mikeneck.httpspec.HttpSpec;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(Get.class)})
public interface Request extends Consumer<HttpSpec> {}
