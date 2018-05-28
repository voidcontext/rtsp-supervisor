package hu.vdx.supervisor

import java.time.format.DateTimeFormatter

case class CameraStream(ip: String, name: String, config: Config) {

  def run: (Long, Process, StreamOutput) = {
    // format: off
    val opts = Seq(
      "-D", "5",
      "-d", "3600",
      "-K",
      "-v",
      "-V",
      "-4",
      "-t",
      "-f", "25",
      "-h", "1280",
      "-w", "720",
      "-b", "200000"
    )
    // format: on

    val url =
      s"rtsp://${config.rtspStream.user.value}:${config.rtspStream.password.value}@$ip${config.rtspStream.path.value}"
    val command: Seq[String] = List("openRTSP") ++ opts :+ url

    val date = java.time.LocalDateTime.now
    val output = StreamOutput(
      config.rtspStream.savePath.value + "/" + java.time.LocalDate.now + "/" + name,
      date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".mp4"
    )

    val process = new ProcessBuilder(command: _*).redirectOutput(output.file).start()

    val f = process.getClass.getDeclaredField("pid")
    f.setAccessible(true)
    val pid = f.getLong(process)

    (pid, process, output)
  }
}
