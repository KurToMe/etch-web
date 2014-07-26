package etch

import java.util.Date

import play.api.mvc.{Controller, Action}
import play.api.libs.json
import play.api.mvc.BodyParsers.parse
import play.api.libs.json.{JsValue, Writes, Json}
import java.io.File


object EtchController extends Controller {

  def saveEtch() = {
    Action(parse.json) { request =>
      val json = request.body

      val base64Image = (json \ "base64Image").as[String]
      val latitude = (json \ "coords" \ "latitude").as[Double]
      val longitude = (json \ "coords" \ "longitude").as[Double]

      val etch = Etch(base64Image, truncate(latitude), truncate(longitude))
      EtchDao.upsertEtch(etch)

      Ok("")
    }
  }

  def saveEtchE6(latitudeE6: Int, longitudeE6: Int) = {
    Action(parse.temporaryFile) { request =>

      val epochTime = new Date().getTime
      val path = s"./tmp/$latitudeE6/$longitudeE6/$epochTime"
      val file = new File(path)
      request.body.moveTo(file)

      val source = scala.io.Source.fromFile(file)
      val byteArray = source.map(_.toByte).toArray
      source.close()


      val etch = EtchE6(byteArray, latitudeE6, longitudeE6)
      EtchDao.upsertEtchE6(etch)
      Ok("")
    }
  }

  import EtchConstants._

  def truncate(d: Double): Double = {

    val multiplier: Double = math.pow(10, PrecisionDigits)
    (d * multiplier).round / multiplier
  }

  implicit val etchWrites = new Writes[Etch] {
    override def writes(etch: Etch): JsValue = {
      Json.obj(
        "base64Image" -> etch.base64Image,
        "latitude" -> etch.latitude,
        "longitude" -> etch.longitude
      )
    }
  }

//  implicit val etchE6Writes = new Writes[EtchE6] {
//    override def writes(etch: EtchE6): JsValue = {
//      Json.obj(
//        "base64Image" -> etch.base64Image,
//        "latitudeE6" -> etch.latitudeE6,
//        "longitudeE6" -> etch.longitudeE6
//      )
//    }
//  }

  def getEtch(latitude:Double, longitude:Double) = {
    Action.apply {
      val etch = EtchDao.getEtch(truncate(latitude), truncate(longitude))

      Ok(Json.toJson(etch))
    }
  }

  def getEtchE6(latitudeE6: Int, longitudeE6: Int) = {
    Action.apply {
      val bytes: Array[Byte] = EtchDao.getEtchE6(latitudeE6, longitudeE6) match {
        case Some(etch) => etch.gzipImage
        case _ => Array()
      }

      Ok(bytes).as("application/gzip")
    }
  }
}
 
object EtchConstants {
  // 4 decimal places is about 30 ft precision,
  //  which is about as accurate as most phones can hope for nowadays.
  val PrecisionDigits: Int = 4

}
