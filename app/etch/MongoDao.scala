package etch

import scala.collection.JavaConversions._

import com.mongodb.QueryBuilder
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoClientURI
import com.mongodb.casbah.MongoURI
import play.Play

@deprecated(
  "Leaving just in case I want to store metadata in" +
    "mongo and don't want to re-write the connection code"
)
object MongoDao {


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
  val etchesCollection = etchDb.getCollection("etch")

  val sortsE6 = MongoDBObject(
    EtchFields.latitudeE6 -> 1,
    EtchFields.longitudeE6 -> 1
  )
  etchesCollection.ensureIndex(sortsE6)

  object EtchFields {
    val imageGzip = "imgGz"

    val latitudeE6 = "latitudeE6"
    val longitudeE6 = "longitudeE6"

    val s3Backfill = "s3Backfill"
  }

  private def toDocument(etch: EtchE6) = MongoDBObject(
    EtchFields.imageGzip -> etch.gzipImage,
    EtchFields.latitudeE6 -> etch.latitudeE6,
    EtchFields.longitudeE6 -> etch.longitudeE6
  )

  def upsertEtchE6(etch: EtchE6) = {
    val document = toDocument(etch)

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

  def getEtchE6(latitudeE6: Int, longitudeE6: Int): Option[EtchE6] = {
    val query = MongoDBObject(
      EtchFields.latitudeE6 -> latitudeE6,
      EtchFields.longitudeE6 -> longitudeE6
    )

    Option(etchesCollection.findOne(query))
      .map(toEtch)
  }

  def toEtch(result: DBObject) = {
    val backfilled = result.containsField(EtchFields.s3Backfill) &&
      result.as[Boolean](EtchFields.s3Backfill)

    EtchE6(
      gzipImage = result.as[Array[Byte]](EtchFields.imageGzip),
      latitudeE6 = result.as[Int](EtchFields.latitudeE6),
      longitudeE6 = result.as[Int](EtchFields.longitudeE6)
    )
  }

  def notBackfilled(): Seq[EtchE6] = {
    val notBackfilled = MongoDBObject(
      EtchFields.s3Backfill -> false
    )
    val doesntHaveProp = MongoDBObject (
      EtchFields.s3Backfill -> MongoDBObject("$exists" -> false)
    )
    val query = QueryBuilder.start()
      .or(notBackfilled, doesntHaveProp)
      .get()

    etchesCollection.find(query)
      .toArray
      .map(toEtch)
  }


}
