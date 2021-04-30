package org.mikeneck.httpspec.junit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResourceFile {

  String value();

  class Loader implements ParameterResolver, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
        ExtensionContext.Namespace.create(Loader.class);

    private enum SupportedTypes {
      STRING {
        @Override
        boolean supports(@NotNull Class<?> klass) {
          return String.class.equals(klass);
        }

        @Override
        @NotNull
        Object toSupportedType(@NotNull InputStream inputStream) {
          try (var reader =
              new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }
      },
      BUFFERED_READER {
        @Override
        boolean supports(@NotNull Class<?> klass) {
          return BufferedReader.class.equals(klass);
        }

        @Override
        @NotNull
        Object toSupportedType(@NotNull InputStream inputStream) {
          return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }
      },
      READER {
        @Override
        boolean supports(@NotNull Class<?> klass) {
          return Reader.class.equals(klass);
        }

        @Override
        @NotNull
        Object toSupportedType(@NotNull InputStream inputStream) {
          return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        }
      },
      INPUT_STREAM {
        @Override
        boolean supports(@NotNull Class<?> klass) {
          return InputStream.class.equals(klass);
        }

        @Override
        @NotNull
        Object toSupportedType(@NotNull InputStream inputStream) {
          return inputStream;
        }
      },
      ;

      abstract boolean supports(@NotNull Class<?> klass);

      abstract @NotNull Object toSupportedType(@NotNull InputStream inputStream);

      static boolean supporting(@NotNull Class<?> klass) {
        return Arrays.stream(SupportedTypes.values()).anyMatch(type -> type.supports(klass));
      }

      @NotNull
      static Optional<@NotNull SupportedTypes> of(@NotNull Class<?> klass) {
        return Arrays.stream(SupportedTypes.values())
            .filter(type -> type.supports(klass))
            .findFirst();
      }
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      Method method = extensionContext.getRequiredTestMethod();
      ResourceFile resourceFile = method.getAnnotation(ResourceFile.class);
      if (resourceFile == null) {
        return false;
      }
      String resourceName = resourceFile.value();
      ClassLoader classLoader = Loader.class.getClassLoader();
      URL resource = classLoader.getResource(resourceName);
      if (resource == null) {
        return false;
      }
      Parameter parameter = parameterContext.getParameter();
      Annotation[] annotations = parameter.getAnnotations();
      if (annotations.length != 0) {
        return false;
      }
      Class<?> parameterType = parameter.getType();
      return SupportedTypes.supporting(parameterType);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      Method method = extensionContext.getRequiredTestMethod();
      ResourceFile resourceFile = method.getAnnotation(ResourceFile.class);
      if (resourceFile == null) {
        throw new IllegalStateException("@ResourceFile is not annotated");
      }
      String resourceName = resourceFile.value();
      ClassLoader classLoader = Loader.class.getClassLoader();
      if (classLoader.getResource(resourceName) == null) {
        throw new IllegalStateException("resource file " + resourceName + " is not found");
      }

      Parameter parameter = parameterContext.getParameter();
      Class<?> parameterType = parameter.getType();

      SupportedTypes supportedTypes =
          SupportedTypes.of(parameterType)
              .orElseThrow(() -> new IllegalStateException("unsupported type " + parameterType));

      InputStream inputStream = classLoader.getResourceAsStream(resourceName);
      if (inputStream == null) {
        throw new IllegalStateException("file not found : " + resourceName);
      }

      Object object = supportedTypes.toSupportedType(inputStream);

      ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);
      store.put(ResourceFile.class, object);

      return object;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
      ExtensionContext.Store store = context.getStore(NAMESPACE);
      Object object = store.remove(ResourceFile.class);
      if (object instanceof AutoCloseable) {
        ((AutoCloseable) object).close();
      }
    }
  }
}
