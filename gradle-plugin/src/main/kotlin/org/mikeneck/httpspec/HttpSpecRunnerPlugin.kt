package org.mikeneck.httpspec

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.mikeneck.httpspec.impl.DefaultHttpSpecRunnerTask

open class HttpSpecRunnerPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val httpSpecRunnerExtension = httpSpecRunnerExtension(project)
    project.convention.add(
        HttpSpecRunnerExtension::class.java, "httpSpecRunner", httpSpecRunnerExtension)
    httpSpecRunnerExtension.reportDirectory.convention(
        project
            .objects
            .directoryProperty()
            .dir("${project.buildDir}/${HttpSpecRunnerExtension.DEFAULT_REPORT_DIRECTORY}"))

    val httpSpecRunnerTask =
        project.tasks.create(
            "http-spec-runner", DefaultHttpSpecRunnerTask::class.java, httpSpecRunnerExtension)
    httpSpecRunnerTask.group = "http-spec-runner"
    httpSpecRunnerTask.description = "runs http-spec-runner with given spec files."
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
