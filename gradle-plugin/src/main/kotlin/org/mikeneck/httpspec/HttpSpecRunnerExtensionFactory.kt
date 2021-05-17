package org.mikeneck.httpspec

import org.gradle.api.Project

interface HttpSpecRunnerExtensionFactory {

  fun create(project: Project): HttpSpecRunnerExtension
}
