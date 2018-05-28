package hu.vdx.supervisor

import akka.actor.{ActorSystem, Props}

object Supervisor extends App {

  implicit val system: ActorSystem = ActorSystem("supervisor")

  val config = Config.load()

  val streams = config.rtspStream.servers
    .map { server =>
      CameraStream(server.ip.value, server.location.value, config)
    }
    .map { cs =>
      system.actorOf(Props(new Task(cs)), "task-" + cs.name)
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
    var command = scala.io.StdIn.readLine("command -> ")
  }

  streams.foreach(_ ! Terminate)
  Thread.sleep(1000)

  system.terminate()
}
