package org.mikeneck.httpspec;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class MockServer implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Port {
    int DEFAULT_PORT = 8000;
    int value() default DEFAULT_PORT;
  }

  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MockServer.class);

  @Override
  public void beforeAll(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    Port portAnnotation = testClass.getAnnotation(Port.class);
    int port = portAnnotation == null ? Port.DEFAULT_PORT : portAnnotation.value();

    ExtensionContext.Store store = context.getStore(NAMESPACE);

    WireMockServer wireMockServer = new WireMockServer(options().port(port));
    wireMockServer.start();
    WireMock.configureFor(port);

    store.put(MockServer.class, wireMockServer);
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    WireMock.reset();
  }

  @Override
  public void afterAll(ExtensionContext context) {
    ExtensionContext.Store store = context.getStore(NAMESPACE);
    WireMockServer wireMockServer = store.remove(MockServer.class, WireMockServer.class);
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }
}
