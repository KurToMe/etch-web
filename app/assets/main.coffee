require.config
  baseUrl: 'assets/'

  paths:
    'angular': 'lib/components/angular/angular.min'
    'angular-geolocation': 'lib/components/angularjs-geolocation/dist/angularjs-geolocation.min'
    'ngRoute': 'lib/components/angular-route/angular-route.min'

  shim:
    'angular':
      exports: 'angular'

    'ngRoute': ['angular']
    'angular-geolocation': ['angular']

  packages:
    [
      name: 'app', package: 'app/'
    ]

require [
  'angular'
  'etch'
],
  (ng, etch) ->
    ng.bootstrap(document, [etch.name])