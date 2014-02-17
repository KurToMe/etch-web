class EtchController
  constructor: (@scope, @geolocation) ->
    @scope.hasLocation = false
    @geolocation.getLocation().then @updateLocation, @unableToGetLocation

  updateLocation: (data) =>
    @scope.hasLocation = true
    @scope.coords = data.coords

  unableToGetLocation: (data) =>
    @scope.hasLocation = false
    @scope.coords = {}


define ['app/module'], (module) ->
  module.controller 'etchController', [
    '$scope', 'geolocation'
    EtchController
  ]