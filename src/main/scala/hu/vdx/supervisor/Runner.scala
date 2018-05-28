package hu.vdx.supervisor

import akka.actor.Actor
import com.typesafe.scalalogging.Logger

// Todo: use cats-effect for async IO
class Runner(cs: CameraStream) extends Actor {
  private[this] val logger = Logger(s"runner-${cs.name}")

  override def receive: Receive = {
    case Start =>
      logger.info("run process")
      val (pid, process, output) = cs.run
      sender() ! Running(pid, process)
      logger.info("wait for termination ({})", pid)
      process.waitFor()

      if (output.file.length() == 0) {
        logger.info("delete empty file")
        output.file.delete()
      }

      sender() ! Stopped
  }
}
