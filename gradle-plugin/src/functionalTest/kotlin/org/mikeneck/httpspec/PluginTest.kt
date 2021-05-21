package org.mikeneck.httpspec

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TestProjectBuilder::class)
class PluginTest {
    
    @Files([
        File(
            path = "build.gradle",
            //language=gradle
            contents = """
plugins {
  id 'java'
  id 'org.mikeneck.http-spec-runner-gradle-plugin'
}

repositories {
  mavenCentral()
}

dependencies {
  implementation 'io.servicetalk:servicetalk-http-netty:0.39.2'
  implementation 'io.servicetalk:servicetalk-annotations:0.39.2'
  implementation 'io.servicetalk:servicetalk-data-jackson:0.39.2'
  implementation 'org.slf4j:slf4j-api:1.7.30'
  implementation 'ch.qos.logback:logback-classic:1.2.3'
  implementation 'com.google.guava:guava:30.1.1-jre'
}

httpSpecRunner {
  runInBackground {
    it.classpath.from(sourceSets.main.runtimeClasspath)
    it.mainClass = 'com.example.Main'
    it.waitUntilGet('http://localhost:8800/health') { condition ->
      condition.status = 200
      condition.retryStrategy = [1000L, 2000L, 4000L, 8000L, 16000L, 32000L]
    }
  }
  addSpec { specCase ->
    specCase.name = 'single'
    specCase.useFile = file('src/spec/single.yml')
  }
}

tasks.named('http-spec-runner').configure {
  it.dependsOn 'classes'
}
"""),
        File(
            path = "src/main/java/com/example/Main.java",
            //language=java
            contents = """
package com.example;

import com.google.common.collect.Iterables;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.data.jackson.JacksonSerializationProvider;
import io.servicetalk.http.api.HttpSerializationProviders;
import io.servicetalk.http.netty.HttpServers;
import io.servicetalk.serialization.api.TypeHolder;
import java.util.Map;import org.slf4j.LoggerFactory;

public class Main {
  @SuppressWarnings("RedundantThrows")
  public static void main(String[] args) throws Exception {
    var jacksonSerializationProvider = new JacksonSerializationProvider();
    var mapType = new TypeHolder<Map<String, String>>() {};
    var serializer = HttpSerializationProviders
        .jsonSerializer(jacksonSerializationProvider)
        .serializerFor(mapType);

    var logger = LoggerFactory.getLogger(Main.class);

    logger.info("starting application on localhost:8800");

    HttpServers.forPort(8800)
      .listenAndAwait((ctx, request, responseFactory) -> {
        String path = request.path();
        Iterable<String> names = request.queryParameters("name");
        String name = Iterables.getFirst(names, "test");
        logger.info("request path: {}, names: {}", path, names);
        if (path.endsWith("test")) {
            return Single.fromSupplier(() -> 
              responseFactory.ok()
                .addHeader("content-type", "application/json")
                .payloadBody(Map.of("name", name), serializer));
        } else if (path.endsWith("health")) {
            return Single.fromSupplier(() -> 
              responseFactory.ok()
                .addHeader("content-type", "application/json")
                .payloadBody(Map.of("state", "ok"), serializer));
        } else {
            return Single.fromSupplier(() ->
              responseFactory.notFound()
                .addHeader("content-type", "application/json")
                .payloadBody(Map.of("path", path), serializer));
        }
      })
      .awaitShutdown();
  }
}
"""
        ),
    File(
        path = "src/spec/single.yml",
        //language=yaml
        contents = """
spec:
  - name: 'request to /test without name query'
    request:
      get: 'http://localhost:8800/test'
      headers:
        accept: application/json
    response:
      status: 200
      headers:
        content-type: application/json
      body:
        - path: ${'$'}.name
          expect:
            type: string
            value: test

  - name: 'request to /test with name query "foo"'
    request:
      get: 'http://localhost:8800/test'
      queries:
        name: foo
      headers:
        accept: application/json
    response:
      status: 200
      headers:
        content-type: application/json
      body:
        - path: ${'$'}.name
          expect:
            type: string
            value: foo

  - name: 'request to /path with name query "foo"'
    request:
      get: 'http://localhost:8800/path'
      queries:
        name: foo
      headers:
        accept: application/json
    response:
      status: 404
      headers:
        content-type: application/json
      body:
        - path: ${'$'}.path
          expect:
            type: string
            value: /path
""")
    ])
    @Test
    fun singleTestCase(testProject: TestProject) {
        val result = testProject.gradle("http-spec-runner", "--info", "--stacktrace")
        assertAll(
            { assertThat(result.successTaskPaths()).contains(":compileJava", ":http-spec-runner") },
            { assertThat(testProject.file("build/http-spec-runner")).exists().isDirectory() },
            { assertThat(testProject.file("build/http-spec-runner/single.yml")).exists().canRead() }
        )
    }
}
