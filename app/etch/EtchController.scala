package etch

import play.api.mvc.{Controller, Action}
import play.api.libs.json
import play.api.mvc.BodyParsers.parse
import play.api.libs.json.{JsValue, Writes, Json}


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

  private def truncate(d: Double): Double = {
    val digits: Int = 3
    val multiplier: Int = 10 * digits
    (d * multiplier).floor / multiplier
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

  def getEtch(latitude:Double, longitude:Double) = {
    Action.apply {
      val etch = EtchDao.getEtch(truncate(latitude), truncate(longitude))

      Ok(Json.toJson(etch))
    }
  }


}
