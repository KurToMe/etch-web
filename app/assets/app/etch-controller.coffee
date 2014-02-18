class EtchController
  constructor: (@scope, @geolocation, @http) ->
    @scope.hasLocation = false
    @geolocation.getLocation().then @updateLocation, @unableToGetLocation
    @scope.canvasControl = {}
    @scope.save = @save

  save: =>
    base64Image = @scope.canvasControl.getImageBase64()
    data =
      coords: @scope.coords
      base64Image: base64Image

    url = '/json/etch'
    promise = @http.post url, data
    promise.then ->
      console.log 'saved ' + data.toString()

  updateLocation: (data) =>
    @scope.hasLocation = true
    @scope.coords = data.coords

  unableToGetLocation: (data) =>
    @scope.hasLocation = false
    @scope.coords = {}


define ['app/module'], (module) ->
  module.controller 'etchController', [
    '$scope', 'geolocation', '$http'
    EtchController
  ]