http-spec
===

http-spec is a library for web services to describe REST API specification.

This library build on top of Java 11's http API.

Usage
===

(1) YAML base
---

```yaml
spec:
  - name: 'getting path resource with paging parameters'
    request:
      get: 'http://localhost:8080/path'
      queries:
        q: test
        page: 4
        limit: 10
      headers:
        accept: application/json
        authorization: bearer 11aa22bb33cc
    response:
      status: 200
      headers:
        content-type: application/json
      body:
        - path: $.firstName
          expect:
            - type: string
              value: John
```

```java
class RestApiTest {
    @Test
    void runRequestFromYamlFile() {
        var yamlFile = new File("/path/to/spec.yaml");
        HttpSpecRunner.from(yamlFile).run(); // runs all requests and asserts responses.
    }
}
```


(2) Java base
---

```java
class RestApiTest {
    @Test
    void runRequestByJavaCode() {
        HttpSpecRunner.Builder builder = HttpSpecRunner.builder();
        builder.addSpec(spec -> {
            spec.name("getting path resource with paging parameters");
            spec.request.get("http://localhost:8080/path", request -> {
                    request.query("q", "test");
                    request.query("page", 4);
                    request.query("limit", 10);
                    request.header("accept", "application/json");
                    request.header("authorization", "bearer 11aa22bb33cc");
            });
            spec.response(response -> {
                response.status(200);
                response.header("content-type", "application/json");
                response.body("$.firstName").toBe("John");
            });
        });
        HttpSpecRunner httpSpecRunner = builder.build();
        httpSpecRunner.run(); // runs all requests and asserts responses.
    }
}
```


