package org.mikeneck.httpspec.impl

import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.gradle.api.Action
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.process.JavaExecSpec
import org.jetbrains.annotations.NotNull
import org.mikeneck.httpspec.*

class DefaultBackgroundApplicationExecSpec(
    override val classpath: ConfigurableFileCollection,
    override val args: ListProperty<String>,
    override val mainClass: Property<String>,
    override val jvmArgs: ListProperty<String>,
    override val environment: MapProperty<String, String>,
    private val out: Property<OutputStream>,
    @get:Internal val waitUrl: Property<String>,
    @get:Internal val conditions: ListProperty<WaitCondition>,
    @get:Internal val retryStrategy: Property<RequestRetryStrategy>
) : BackgroundApplicationExecSpec {

  private val logger = logger<DefaultBackgroundApplicationExecSpec>()

  override fun setMainClass(mainClass: String) {
    this.mainClass.set(mainClass)
  }

  @get:Internal
  override var stdout: OutputStream
    get() = out.get()
    set(output) = this.out.set(output)

  override fun waitUntilGet(url: String, condition: Action<ResponseCondition>) {
    this.waitUrl.set(url)
    condition.execute(responseCondition)
  }

  private val responseCondition: ResponseCondition
    get() =
        object : ResponseCondition {
          override fun setStatus(status: Int) {
            conditions.add(WaitCondition.HttpStatus(status))
          }

          override fun setBodyContains(fragment: String) {
            conditions.add(WaitCondition.BodyContains(fragment))
          }

          override fun setRetryStrategy(retryStrategy: RequestRetryStrategy) {
            this@DefaultBackgroundApplicationExecSpec.retryStrategy.set(retryStrategy)
          }
        }

  override fun execute(javaExec: JavaExecSpec) {
    javaExec.mainClass.set(mainClass)
    javaExec.classpath(classpath)
    javaExec.jvmArgs(jvmArgs.get())
    javaExec.args(args.get())
    javaExec.environment(environment.get())
    if (out.isPresent) {
      javaExec.standardOutput = out.get()
    }
  }

  fun waitApplicationReady() {
    val url =
        waitUrl.orNull
            ?: run {
              logger.info("no check required by user")
              return
            }
    val uri =
        URI.create(url)
            ?: throw IllegalStateException("java.net.URI is broken for creating uri from $url")

    val waitConditions =
        conditions.orNull
            ?: throw InvalidUserDataException(
                "runInBackground.waitUntil condition is required for plugin to wait application ready")

    val strategy =
        retryStrategy.orNull
            ?: throw InvalidUserDataException(
                "runInBackground.waitUntil retryStrategy is required for plugin to wait application ready")
    val intervals = strategy.retryInterval()

    val client = HttpClient.newHttpClient()
    val get =
        HttpRequest.newBuilder(uri).GET().build()
            ?: throw IllegalStateException("java.net.http.HttpRequest is broken")

    if (!client.waitForApplicationReady(get, waitConditions, intervals)) {
      throw IllegalStateException("application seems unready on $uri")
    }
    logger.info("application $uri is ready")
  }

  private fun HttpClient.waitForApplicationReady(
      get: HttpRequest,
      waitConditions: List<WaitCondition>,
      intervals: @NotNull Iterator<@NotNull Long>
  ): Boolean {
    while (true) {
      try {
        val response = this.send(get, HttpResponse.BodyHandlers.ofByteArray())
        if (waitConditions.allSatisfy(response)) {
          return true
        }
      } catch (e: IOException) {
        logger.info("exception while waiting application ready: {}", e)
      }
      if (!intervals.hasNext()) {
        logger.info("waited application to be ready but it still keep unready")
        return false
      }
      val interval = intervals.next()
      Thread.sleep(interval)
    }
  }

  private fun List<WaitCondition>.allSatisfy(response: HttpResponse<ByteArray>): Boolean =
      this.all { it.satisfy(response) }

  companion object {
    operator fun invoke(objectFactory: ObjectFactory): DefaultBackgroundApplicationExecSpec =
        DefaultBackgroundApplicationExecSpec(
            objectFactory.fileCollection(),
            objectFactory.listProperty(),
            objectFactory.property(),
            objectFactory.listProperty(),
            objectFactory.mapProperty(),
            objectFactory.property(),
            objectFactory.property(),
            objectFactory.listProperty(),
            objectFactory.property())
  }
}
