package org.mikeneck.httpspec.impl

import java.io.File
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.mikeneck.httpspec.HttpSpecCase

class DefaultHttpSpecCase(
    override val name: Property<String>,
    override val useFile: RegularFileProperty
) : HttpSpecCase {
  override fun setName(name: String) {
    this.name.set(name)
  }

  override fun setUseFile(file: File) {
    this.useFile.set(file)
  }
}
