package etch

import com.mongodb.casbah.{MongoClientURI, MongoClient, MongoURI}
import play.Play
import com.mongodb.casbah.Imports._

object EtchDao {


  val rawUriString = Play.application().configuration().getString("mongo.uri")
  val uri = MongoURI(rawUriString)
  val clientUri = MongoClientURI(rawUriString)

  val mongoClient = MongoClient(clientUri)
  val etchDb = mongoClient.getDB("etch")
  val etchesCollection = etchDb.getCollection("etch")

  val sorts = MongoDBObject(
    EtchFields.latitude -> 1,
    EtchFields.longitude -> 1
  )
  etchesCollection.ensureIndex(sorts)

  object EtchFields {
    val base64Image = "base64Image"
    val latitude = "latitude"
    val longitude = "longitude"
  }

  def upsertEtch(etch: Etch) = {
    val document = MongoDBObject(
      EtchFields.base64Image -> etch.base64Image,
      EtchFields.latitude -> etch.latitude,
      EtchFields.longitude -> etch.longitude
    )
    val upsertQuery = MongoDBObject(
      EtchFields.latitude -> etch.latitude,
      EtchFields.longitude -> etch.longitude
    )

    etchesCollection.update(
      upsertQuery,
      document,
      true,
      false
    )
  }

  def getEtch(latitude: Double, longitude: Double) = {
    val query = MongoDBObject(
      EtchFields.latitude -> latitude,
      EtchFields.longitude -> longitude
    )

    val result = etchesCollection.findOne( query )

    Etch(
      result.as[String](EtchFields.base64Image),
      result.as[Double](EtchFields.latitude),
      result.as[Double](EtchFields.longitude)
    )

  }




}
