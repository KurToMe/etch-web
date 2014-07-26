package etch

@deprecated
case class Etch(
  base64Image: String, latitude: Double, longitude: Double
)


case class EtchE6(
  base64Image: String,
  latitudeE6: Int,
  longitudeE6: Int
)

