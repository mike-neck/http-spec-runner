package org.mikeneck.httpspec

import org.gradle.api.Plugin
import org.gradle.api.Project

open class HttpSpecRunnerPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val httpSpecRunnerExtension = httpSpecRunnerExtension(project)
    project.convention.add(
        HttpSpecRunnerExtension::class.java, "httpSpecRunnerExtension", httpSpecRunnerExtension)
    TODO("not implemented")
  }

  companion object {
    fun httpSpecRunnerExtension(project: Project): HttpSpecRunnerExtension =
        HttpSpecRunnerExtensionFactory.instance().create(project)

    val version: String
      get() {
        val loader =
            HttpSpecRunnerPlugin::class.java.classLoader
                ?: throw IllegalStateException("class loader not found")
        return loader.getResourceAsStream("plugin-version.txt")?.use {
          it.reader(Charsets.UTF_8).readText()
        }
            ?: "v0.0.0"
      }
  }
}
