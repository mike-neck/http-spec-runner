package org.mikeneck.httpspec.impl

import java.net.http.HttpResponse
import java.nio.charset.Charset

sealed class WaitCondition {
  abstract fun satisfy(httpResponse: HttpResponse<ByteArray>): Boolean

  data class HttpStatus(val status: Int) : WaitCondition() {
    override fun satisfy(httpResponse: HttpResponse<ByteArray>): Boolean =
        httpResponse.statusCode() == this.status
  }

  data class BodyContains(val fragment: String, val charset: Charset = Charsets.UTF_8) :
      WaitCondition() {
    override fun satisfy(httpResponse: HttpResponse<ByteArray>): Boolean {
      return try {
        String(httpResponse.body(), charset).contains(fragment)
      } catch (e: Exception) {
        false
      }
    }
  }
}
