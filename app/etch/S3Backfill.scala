package etch

import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global

object S3Backfill {

  val logger = LoggerFactory.getLogger(getClass)

  EtchDao.notBackfilled().foreach { etch =>
    EtchImageDao.saveEtch(etch) map { _ =>
      val updated = etch.copy(s3Backfill = true)
      EtchDao.upsertEtchE6(updated)
      logger.info(s"backfilled ${etch.latitudeE6},${etch.longitudeE6}")
    }
  }

}
