require.config
  baseUrl: 'assets/'

  paths:
    'angular': 'lib/angular-1.2.13/angular.min'
    'ngRoute': 'lib/angular-1.2.13/angular-route.min'

  shim:
    'angular':
      exports: 'angular'

    'ngRoute': ['angular']

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