package org.mikeneck.httpspec.impl

import org.gradle.api.Project
import org.mikeneck.httpspec.*

class DefaultHttpSpecRunnerExtensionFactory : HttpSpecRunnerExtensionFactory {

  override fun create(project: Project): HttpSpecRunnerExtension =
      DefaultHttpSpecRunnerExtension(
          project.objects.directoryProperty(),
          project.objects.listProperty(),
          project.objects.property(),
          project.objects)
}
