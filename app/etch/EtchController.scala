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

      EtchDao.upsertEtch(Etch(base64Image, latitude, longitude))

      Ok("")
    }
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
      val etch = EtchDao.getEtch(latitude, longitude)

      Ok(Json.toJson(etch))
    }
  }


}
