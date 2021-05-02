package org.mikeneck.httpspec

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ResourceFile.Loader::class)
class HttpSpecRunnerKtTest {

  @Test
  @ResourceFile("http-spec-runner-kt.json")
  fun runRequestByKotlinCode(responseBody: String) {
    stubFor(
        get(urlPathEqualTo("/path"))
            .withHeader("authorization", containing("bearer 11aa22bb33cc"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(responseBody)))

    val httpSpecRunner: HttpSpecRunner = httpSpecRunner {
      "getting path resource with paging parameters" {
        get("http://localhost:8000/path") {
          query("q", "test")
          query("page", 4)
          query("limit", 10)

          header("accept", "application/json")
          header("authorization", "bearer 11aa22bb33cc")
        }
        response {
          status(200)
          header("content-type", "application/json")
          jsonBody {
            path("$.firstName").toBe("John")
            path("$.lastName").toBe("doe")
          }
        }
      }
    }
    httpSpecRunner.run()
  }

  @BeforeEach
  fun resetMockServer() {
    reset()
  }

  companion object {

    @JvmStatic lateinit var wireMockServer: WireMockServer

    @JvmStatic
    @BeforeAll
    fun setupServer() {
      wireMockServer = WireMockServer(options().port(8000))
      wireMockServer.start()
      configureFor(8000)
    }

    @JvmStatic
    @AfterAll
    fun shutdownServer() {
      wireMockServer.stop()
    }
  }
}
