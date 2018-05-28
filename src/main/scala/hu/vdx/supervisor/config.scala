package hu.vdx.supervisor

case class RtspUser(value: String) extends AnyVal
case class RtspPassword(value: String) extends AnyVal
case class RtspPath(value: String) extends AnyVal
case class RtspSavePath(value: String) extends AnyVal

case class RtspServerIp(value: String) extends AnyVal
case class RtspServerLocation(value: String) extends AnyVal

case class RtspServer(ip: RtspServerIp, location: RtspServerLocation)

case class RtspStream(
  user: RtspUser,
  password: RtspPassword,
  path: RtspPath,
  savePath: RtspSavePath,
  servers: Seq[RtspServer]
)
case class Config(rtspStream: RtspStream)

object Config {
  def load(): Config = {
    val eitherConfig = pureconfig.loadConfig[Config]

    eitherConfig.right.get
  }
}
