class EtchController
  constructor: (@scope, @geolocation, @http, @timeout) ->
    @scope.hasLocation = false
    @scope.canvasControl = {}
    @scope.save = @save
    @scope.canvasControl.ready = =>
      @geolocation.getLocation().then @updateLocation, @unableToGetLocation
      @timeout @save, 10 * 1000

  save: =>
    base64Image = @scope.canvasControl.getImageBase64()
    data =
      coords: @scope.coords
      base64Image: base64Image
    url = '/json/etch'
    @http.post(url, data).then ->
      console.log 'saved ' + data.toString()
    @timeout @save, 10 * 1000

  updateLocation: (data) =>
    @scope.hasLocation = true
    @scope.coords = data.coords
    @getUpdatedEtch()

  getUpdatedEtch: =>
    url = '/json/etch'
    config =
      cache: false
      params:
        latitude: @scope.coords.latitude
        longitude: @scope.coords.longitude
    @http.get(url, config).then @updateEtch
    @timeout @getUpdatedEtch, 5 * 1000

  updateEtch: (data) =>
    @scope.canvasControl.setSrc data.data.base64Image

  unableToGetLocation: (data) =>
    @scope.hasLocation = false
    @scope.coords = {}


define ['app/module'], (module) ->
  module.controller 'etchController', [
    '$scope', 'geolocation', '$http', '$timeout'
    EtchController
  ]