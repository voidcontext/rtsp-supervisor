package hu.vdx

package object supervisor {
  case object Start
  case object Stop
  case object Stopped
  case object Terminate
  case class Running(pid: Long, process: Process)
}
