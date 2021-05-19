package org.mikeneck.httpspec

import java.util.*
import org.gradle.api.Project

interface HttpSpecRunnerExtensionFactory {

  fun create(project: Project): HttpSpecRunnerExtension

  companion object {
    fun instance(): HttpSpecRunnerExtensionFactory =
        ServiceLoader.load(HttpSpecRunnerExtensionFactory::class.java).findFirst().orElseThrow()
  }
}
