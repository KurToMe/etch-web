package etch

case class EtchE6(
  gzipImage: Array[Byte],
  latitudeE6: Int,
  longitudeE6: Int,
  s3Backfill: Boolean = false)

