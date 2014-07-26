package etch

import com.mongodb.casbah.{MongoClientURI, MongoClient, MongoURI}
import play.Play
import com.mongodb.casbah.Imports._

object EtchDao {


  val rawUriString = Play.application().configuration().getString("mongo.uri")
  val uri = MongoURI(rawUriString)
  val clientUri = MongoClientURI(rawUriString)

  val mongoClient = MongoClient(clientUri)
  val dbName = clientUri.database match {
    case Some(name) => name
    case _ => "etch"
  }
  val etchDb = mongoClient.getDB(dbName)
  println(s"Using mongo db: $dbName")
//  clientUri.username match {
//    case Some(username) => {
//      println(s"Authenticating mongo user $username on db $dbName")
//      val pass = String.valueOf(clientUri.password.get)
//      etchDb.authenticate(username, pass)
//    }
//    case _ => // No need to auth
//  }
  val etchesCollection = etchDb.getCollection("etch")

  val sorts = MongoDBObject(
    EtchFields.latitude -> 1,
    EtchFields.longitude -> 1
  )
  etchesCollection.ensureIndex(sorts)

  val sortsE6 = MongoDBObject(
    EtchFields.latitudeE6 -> 1,
    EtchFields.longitudeE6 -> 1
  )
  etchesCollection.ensureIndex(sortsE6)

  object EtchFields {
    val base64Image = "base64Image"
    val latitude = "latitude"
    val longitude = "longitude"
    val latitudeE6 = "latitudeE6"
    val longitudeE6 = "longitudeE6"
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

  def upsertEtchE6(etch: EtchE6) = {
    val document = MongoDBObject(
      EtchFields.base64Image -> etch.base64Image,
      EtchFields.latitudeE6 -> etch.latitudeE6,
      EtchFields.longitudeE6 -> etch.longitudeE6
    )
    val upsertQuery = MongoDBObject(
      EtchFields.latitudeE6 -> etch.latitudeE6,
      EtchFields.longitudeE6 -> etch.longitudeE6
    )

    val upsert = true
    val multi = false
    etchesCollection.update(
      upsertQuery,
      document,
      upsert,
      multi
    )
  }

  def getEtchE6(latitudeE6: Int, longitudeE6: Int): Option[Etch] = {
    val query = MongoDBObject(
      EtchFields.latitudeE6 -> latitudeE6,
      EtchFields.longitudeE6 -> longitudeE6
    )

    val result = etchesCollection.findOne( query )

    if (result == null) {
      None
    }
    else {
      Some(Etch(
        result.as[String](EtchFields.base64Image),
        result.as[Double](EtchFields.latitude),
        result.as[Double](EtchFields.longitude)
      ))
    }
  }

  def getEtch(latitude: Double, longitude: Double): Option[Etch] = {
    val query = MongoDBObject(
      EtchFields.latitude -> latitude,
      EtchFields.longitude -> longitude
    )

    val result = etchesCollection.findOne( query )

    if (result == null) {
      None
    }
    else {
      Some(Etch(
        result.as[String](EtchFields.base64Image),
        result.as[Double](EtchFields.latitude),
        result.as[Double](EtchFields.longitude)
      ))
    }
  }




}
