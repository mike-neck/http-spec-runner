package org.mikeneck.httpspec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Env {

  @NotNull
  String name();

  @NotNull
  String defaultValue() default "";

  class Resolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      Parameter parameter = parameterContext.getParameter();
      Class<?> parameterType = parameter.getType();
      Env env = parameter.getAnnotation(Env.class);

      return env != null && parameterType.equals(String.class);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      Parameter parameter = parameterContext.getParameter();
      Env env = parameter.getAnnotation(Env.class);

      String key = env.name();
      String value = System.getenv(key);
      return value != null ? value : env.defaultValue();
    }
  }
}
