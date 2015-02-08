package etch

import java.io.File
import java.io.FileInputStream
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global

import org.apache.commons.io.IOUtils
import play.api.mvc.Action
import play.api.mvc.Controller

object EtchController extends Controller {

  def saveEtchE6(latitudeE6: Int, longitudeE6: Int) = Action.async(parse.temporaryFile) { request =>
    S3Backfill.run()

    val epochTime = new Date().getTime
    val path = s"/tmp/$latitudeE6-$longitudeE6-$epochTime.png.gz"
    val file = new File(path)
    request.body.moveTo(file)

    val stream = new FileInputStream(path)
    val byteArray = IOUtils.toByteArray(stream)
    stream.close()
    file.delete()


    val etch = EtchE6(byteArray, latitudeE6, longitudeE6)
    EtchImageDao.saveEtch(etch) map { _ =>
      Ok("")
    }
  }

  def toResponseBody(opt: Option[Array[Byte]]): Array[Byte] = opt match {
    case Some(imageBytes) => imageBytes
    case None => Array()
  }

  def getEtchE6(latitudeE6: Int, longitudeE6: Int) = Action.async {
    EtchImageDao.findEtch(latitudeE6, longitudeE6) map toResponseBody map { bytes =>
      Ok(bytes).as("application/gzip")
    }
  }

}
