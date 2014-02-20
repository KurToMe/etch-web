package etch

import com.mongodb.casbah.{MongoClientURI, MongoClient, MongoURI}
import play.Play
import com.mongodb.casbah.commons.MongoDBObject

object EtchDao {


  val rawUriString = Play.application().configuration().getString("mongo.uri")
  val uri = MongoURI(rawUriString)
  val clientUri = MongoClientURI(rawUriString)

  val mongoClient = MongoClient(clientUri)
  val etchDb = mongoClient.getDB("etch")
  val etchesCollection = etchDb.getCollection("etches")

  val x = 3

  def upsertEtch(etch: Etch) = {
    val document = MongoDBObject(
      "base64Image" -> etch.base64Image,
      "latitude" -> etch.latitude,
      "longitude" -> etch.longitude
    )
    val query = MongoDBObject("latitude" -> etch.latitude, "longitude" -> etch.longitude)
    etchesCollection.update(
      query,
      document,
      true,
      false
    )
  }


}
