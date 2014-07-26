package etch

@deprecated
case class Etch(
  base64Image: String, latitude: Double, longitude: Double
)


case class EtchE6(
  gzipImage: Array[Byte],
  latitudeE6: Int,
  longitudeE6: Int
)

