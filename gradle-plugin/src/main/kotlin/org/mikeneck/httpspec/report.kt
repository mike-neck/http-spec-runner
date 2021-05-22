package org.mikeneck.httpspec

import java.io.File

typealias FileResult = Pair<Pair<String, File>, List<VerificationResult>>

typealias SpecReports = Iterable<SpecReportItem>

data class SpecReportItem(val name: String, val file: String, val items: List<SpecResult>) {
  companion object {
    operator fun invoke(fileResult: FileResult): SpecReportItem =
        SpecReportItem(
            fileResult.first.first,
            fileResult.first.second.name,
            fileResult.second.map { SpecResult(it) })
  }
}

data class SpecResult(val name: String, val assertion: List<Assertion>) {
  companion object {
    operator fun invoke(verificationResult: VerificationResult): SpecResult =
        SpecResult(
            verificationResult.specName(), verificationResult.allAssertions().map { Assertion(it) })
  }
}

data class Assertion(
    val result: String,
    val subtitle: String,
    val expected: String,
    val actual: String?,
    val description: String
) {
  companion object {
    operator fun invoke(httpResponseAssertion: HttpResponseAssertion<*>): Assertion {
      val (result, actual) =
          if (httpResponseAssertion.isSuccess) {
            "success" to null
          } else {
            "failure/error" to "${ httpResponseAssertion.actual() }"
          }
      return Assertion(
          result,
          httpResponseAssertion.subtitle(),
          "${ httpResponseAssertion.expected() }",
          actual,
          httpResponseAssertion.description())
    }
  }
}
