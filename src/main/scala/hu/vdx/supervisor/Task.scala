package hu.vdx.supervisor

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.Logger
import sys.process._

class Task(cs: CameraStream) extends Actor {

  private[this] val logger = Logger(s"task-${cs.name}")

  private[this] val runner = context.actorOf(Props(new Runner(cs)), "runner-" + cs.name)

  override def preStart(): Unit =
    super.preStart()
  logger.info("Starting preStart sequence.")
  self ! Start

  override def postRestart(reason: Throwable): Unit =
    super.postRestart(reason)
  logger.info("Starting postRestart sequence.")
  self ! Start

  def idle(): Receive = {
    case Start =>
      logger.info("Start message received.")
      context.become(waiting())
      runner ! Start
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
    s"kill -HUP $pid" !
  }
}
