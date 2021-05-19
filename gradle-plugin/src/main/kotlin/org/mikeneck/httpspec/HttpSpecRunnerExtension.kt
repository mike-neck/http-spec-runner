package org.mikeneck.httpspec

import org.gradle.api.Action

interface HttpSpecRunnerExtension : HttpSpecRunnerTaskProperties {

  fun addSpec(specConfig: Action<HttpSpecCase>)

  fun runInBackground(backgroundAppSpec: Action<BackgroundApplicationExecSpec>)

  companion object {
    @JvmStatic val DEFAULT_REPORT_DIRECTORY: String = "http-spec-runner"
  }
}
