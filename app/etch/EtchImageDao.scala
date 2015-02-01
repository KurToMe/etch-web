package etch

import scala.concurrent.Future

object EtchImageDao {

  private def path(latitudeE6: Int, longitudeE6: Int) = s"global/$latitudeE6/$longitudeE6.png.gz"

  def findEtch(latitudeE6: Int, longitudeE6: Int): Future[Option[Array[Byte]]] = {
    val etchPath = path(latitudeE6, longitudeE6)
    AsyncS3Connector.get(etchPath)
  }

  def saveEtch(etch: EtchE6): Future[Unit] = {
    val etchPath = path(etch.latitudeE6, etch.longitudeE6)
    AsyncS3Connector.put(etchPath, etch.gzipImage)

  }

}
