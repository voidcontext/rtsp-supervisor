package hu.vdx.supervisor

import akka.actor.Actor
import cats.effect.IO
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext
import scala.sys.process._

class Task(recorder: StreamRecorder, ec: ExecutionContext) extends Actor {

  private[this] val logger = Logger(s"task-${recorder.name}")

  private[this] def runner() =
    Runner(recorder) { (pid, process) =>
      self ! Running(pid, process)
    }

  override def preStart(): Unit = {
    super.preStart()
    logger.info("Starting preStart sequence.")
    self ! Start
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    logger.info("Starting postRestart sequence.")
    self ! Start
  }

  def idle(): Receive = {
    case Start =>
      logger.info("Start message received.")
      context.become(waiting())

      IO.shift(ec)
        .flatMap(_ => runner())
        .map(_ => self ! Stopped)
        .start
        .unsafeRunSync()
  }

  def waiting(): Receive = {
    case Running(pid, process) =>
      logger.info("Running message received.")
      context.become(running(pid, process))
  }

  def running(pid: Long, process: java.lang.Process): Receive = {
    case Stop =>
      logger.info("Stop message received.")
      terminatePid(pid)
      context.become(idle())
    case Stopped =>
      logger.info("Stopped message received.")
      context.become(idle())
      self ! Start
    case Terminate =>
      logger.info("Terminate message received.")
      terminatePid(pid)
      context.become(terminated())
  }

  def terminated(): Receive = {
    case _ => Unit
  }

  override def receive: Receive = idle()

  private[this] def terminatePid(pid: Long) = {
    logger.info("Terminating pid {}", pid)
    s"kill -HUP $pid".!
  }
}
