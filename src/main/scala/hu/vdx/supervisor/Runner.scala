package hu.vdx.supervisor

import cats.effect.IO
import com.typesafe.scalalogging.Logger

object Runner {
  def apply(recorder: StreamRecorder)(cb: (Long, Process) => Unit): IO[Unit] =
    IO({
      val logger = Logger("runner-" + recorder.name)

      logger.info("run process")
      val (pid, process, output) = recorder.record
      cb(pid, process)
      logger.info("wait for termination ({})", pid)
      process.waitFor()

      if (output.file.length() == 0) {
        logger.info("delete empty file")
        output.file.delete()
      }
    })
}
