package org.mikeneck.httpspec

import java.net.http.HttpClient

fun httpSpecRunner(
    client: Client = Client { HttpClient.newHttpClient() },
    extension: Extension = Extension.noOp(),
    configure: DelegateHttpSpecRunnerBuilderDsl.() -> Unit
): HttpSpecRunner {

  val builder = DelegateHttpSpecRunnerBuilderDsl()
  builder.configure()

  return builder.build(client, extension)
}

class DelegateHttpSpecRunnerBuilderDsl(
    private val delegate: HttpSpecRunner.Builder = HttpSpecRunner.builder()
) : HttpSpecRunner.Builder by delegate {

  operator fun String.invoke(configureHttpSpec: DelegateHttpSpecDsl.() -> Unit) {
    spec(this, configureHttpSpec)
  }

  fun spec(specName: String, configureHttpSpec: DelegateHttpSpecDsl.() -> Unit) {
    delegate.addSpec { httpSpec ->
      httpSpec.name(specName)
      DelegateHttpSpecDsl(httpSpec).configureHttpSpec()
    }
  }
}

class DelegateHttpSpecDsl(private val delegate: HttpSpec) {
  fun get(url: String, configureRequestSpec: HttpRequestSpec.() -> Unit) {
    delegate.request().get(url) { httpRequestSpec -> httpRequestSpec.configureRequestSpec() }
  }

  fun response(configureHttpResponseSpec: DelegateHttpResponseSpecDsl.() -> Unit) {
    delegate.response { httpResponseSpec ->
      DelegateHttpResponseSpecDsl(httpResponseSpec).configureHttpResponseSpec()
    }
  }
}

class DelegateHttpResponseSpecDsl(private val httpResponseSpec: HttpResponseSpec) :
    HttpResponseSpec by httpResponseSpec {
  fun jsonBody(configureJsonBodySpec: JsonBody.() -> Unit) {
    httpResponseSpec.jsonBody { jsonBody -> jsonBody.configureJsonBodySpec() }
  }
}
