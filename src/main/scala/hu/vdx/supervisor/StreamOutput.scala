package hu.vdx.supervisor

import java.io.{File, FileOutputStream}

case class StreamOutput(dirName: String, fileName: String) {
  private[this] val dir: File = new File(dirName)
  if (!dir.exists()) dir.mkdirs()

  private[this] val outputFile = new File(dir.getAbsolutePath + "/" + fileName)
  if (!outputFile.exists()) new FileOutputStream(outputFile).close()

  def file: File = outputFile
}
