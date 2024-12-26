

package com.ms.jedi.dl.common

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import org.apache.commons.io.FileUtils

class TempDirectory private(prefix: String, suffix: String, pathOnly: Boolean) {
  val path: Path = Files.createTempFile(prefix, suffix)
  Files.delete(path)
  if (!pathOnly) Files.createDirectory(path)

  private lazy val hook = new Thread() {
    override def run(): Unit = delete()
  }

  def deleteOnExit(): this.type = synchronized {
    Runtime.getRuntime.removeShutdownHook(hook)
    Runtime.getRuntime.addShutdownHook(hook)
    this
  }

  def delete(): Unit = synchronized {
    if (Files.exists(path))
      Files.walkFileTree(path, new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          FileUtils.deleteQuietly(file.toFile)
          FileVisitResult.CONTINUE
        }

        override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
          FileUtils.deleteQuietly(dir.toFile)
          FileVisitResult.CONTINUE
        }
      })
  }
}

object TempDirectory {
  def apply(prefix: String = "", suffix: String = "", pathOnly: Boolean = false): TempDirectory =
    new TempDirectory(prefix, suffix, pathOnly)
}
