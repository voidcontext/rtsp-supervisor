package hu.vdx.supervisor

import java.util.concurrent.Executors

import akka.actor.{ActorSystem, Props}

import scala.concurrent.ExecutionContext

object Supervisor extends App {

  implicit val system: ActorSystem = ActorSystem("supervisor")

  val config = Config.load()

  // The Raspberry Pi has only 1 CPU, so we'll need to provide a big enough thread pool to run the openRTSP
  // sub-processes.
  val subProcessPool = Executors.newFixedThreadPool(config.rtspStream.servers.length)
  val executionContext = ExecutionContext.fromExecutorService(subProcessPool)

  val streams = config.rtspStream.servers
    .map { server =>
      StreamRecorder(server.ip.value, server.location.value, config)
    }
    .map { recorder =>
      system.actorOf(
        Props(new Task(recorder, executionContext)),
        "task-" + recorder.name
      )
    }

  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run(): Unit = {
      streams.foreach { actorRef =>
        actorRef ! Terminate
      }
    }
  })

  var command = scala.io.StdIn.readLine("command -> ")
  while (command != "quit") {
    command = scala.io.StdIn.readLine("command -> ")
  }

  streams.foreach(_ ! Terminate)
  Thread.sleep(1000)

  system.terminate()
  executionContext.shutdown()
}
