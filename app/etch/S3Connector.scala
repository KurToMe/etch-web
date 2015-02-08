package etch

import java.io.ByteArrayInputStream
import java.io.Closeable
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.S3ClientOptions
import com.amazonaws.services.s3.model.ObjectMetadata
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils

class CloseableLoan[C <: Closeable](closeable: C) {
  def map[A](f: (C) => A) = {
    try {
      f(closeable)
    }
    finally {
      closeable.close()
    }
  }
}

object CloseableLoan {
  def borrow[C <: Closeable, A](closeable: C)(f: (C) => A) = {
    new CloseableLoan(closeable).map(f)
  }
}

import etch.CloseableLoan.borrow

object S3Connector {

  // TODO - eventually move this out of code and re-gen the keys so it's not in version control
  private val awsKey = "AKIAIABEMN4LI6457JOA"
  private val awsSecret = "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"

  private val credentials = new BasicAWSCredentials(awsKey, awsSecret)
  private val client = new AmazonS3Client(credentials)
  private val options = new S3ClientOptions()

  private val bucket = "kurtome-etch-image"

  private object KeyNotFound {
    def unapply(throwable: Throwable): Option[Throwable] = throwable match {
      case e: AmazonServiceException => {
        if (e.getErrorCode == "NoSuchKey") Some(e)
        else None
      }
    }
  }

  def get(key: String): Option[Array[Byte]] = {
    try {
      borrow(client.getObject(bucket, key)) { s3Obj =>
        Some(IOUtils.toByteArray(s3Obj.getObjectContent))
      }
    }
    catch {
      case KeyNotFound(e) => None
    }
  }

  def put(key: String, content: Array[Byte]): Unit = {
    val md5 = Base64.getEncoder.encodeToString(DigestUtils.md5(content))
    borrow(new ByteArrayInputStream(content)) { contentStream =>
      val metadata = new ObjectMetadata()
      metadata.setContentLength(content.length)
      metadata.setContentMD5(md5)

      client.putObject(bucket, key, contentStream, metadata)
    }
  }
}

/**
 * This provides an async layer over the blocking HTTP requests to S3
 * in order to separate the slow I/O from blocking the app request handlers
 */
object AsyncS3Connector {

  import etch.S3Connector.{get => syncGet}
  import etch.S3Connector.{put => syncPut}

  def get(key: String) = Future {
    syncGet(key)
  }

  def put(key: String, content: Array[Byte]) = Future {
    syncPut(key, content)
  }
}
