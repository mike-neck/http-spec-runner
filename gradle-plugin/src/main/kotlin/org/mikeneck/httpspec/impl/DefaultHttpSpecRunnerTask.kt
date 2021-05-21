package org.mikeneck.httpspec.impl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.HasScriptServices
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecFactory
import org.gradle.process.internal.ExecHandle
import org.mikeneck.httpspec.*

open class DefaultHttpSpecRunnerTask
@Inject
constructor(private val extension: DefaultHttpSpecRunnerExtension) :
    HttpSpecRunnerTask, HttpSpecRunnerTaskProperties by extension, DefaultTask() {

  @TaskAction
  fun runTask() {
    val backgroundApplication = forkBackgroundApplication()
    defer { backgroundApplication.shutdown() }.use {
      extension.waitApplicationReady()
      runHttpRunnerSpecs()
    }
  }

  private fun forkBackgroundApplication(): ExecHandle {
    logger.info("forking new background application")
    val execFactory = execFactory()
    val builder = execFactory.newJavaExec()
    val javaExecSpec = backgroundApplicationExecSpec.get()
    javaExecSpec.execute(builder)
    val handle = builder.build()
    return handle.start()
  }

  private fun execFactory(): ExecFactory {
    if (project !is HasScriptServices) {
      val message =
          decorateErrorMessage("expected project to be HasScriptService/(but ${project.javaClass})")
      logger.info("error HttpSpecRunnerTask#execFactory {}", message)
      throw IllegalStateException(message)
    }
    val processOperations = (project as HasScriptServices).processOperations
    return if (processOperations is ExecFactory) {
      processOperations
    } else {
      val message =
          decorateErrorMessage(
              "expected ProcessOperations to be instance of ExecFactory/(but ${processOperations.javaClass})")
      logger.info("error HttpSpecRunnerTask#execFactory {}", message)
      throw IllegalStateException(message)
    }
  }

  private fun decorateErrorMessage(message: String): String {
    val version = project.gradle.gradleVersion
    return "[gradle:$version,plugin:${HttpSpecRunnerPlugin.version}] $message"
  }

  private fun defer(onClose: () -> Unit): AutoCloseable = AutoCloseable { onClose() }

  private fun runHttpRunnerSpecs() {
    val specs = this.specs.get()
    val reports = specs.runAll()
    val objectMapper =
        ObjectMapper(YAMLFactory())
            .registerKotlinModule()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    val directory = reportDirectory.asFile.get()
    if (!directory.exists()) {
      directory.mkdirs()
    }
    reports.forEach { specReportItem ->
      val reportFile = directory.resolve(specReportItem.file)
      objectMapper.writeValue(reportFile, specReportItem)
    }
  }

  private fun Iterable<HttpSpecCase>.runAll(): SpecReports {
    val results =
        this.map { it.name.get() to it.useFile.asFile.get() }
            .map { it to HttpSpecRunner.from(it.second) }
            .map { it.first to it.second.runForResult() }
    return results.map { SpecReportItem(it) }
  }

  private fun ExecHandle.shutdown() {
    logger.info("shutting down background application")
    this.abort()
    val execResult = this.waitForFinish()
    logger.info("background application was exited with exit code {}", execResult.exitValue)
  }
}
