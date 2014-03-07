require.config
  baseUrl: 'assets/'

  paths:
    'angular': 'lib/components/angular/angular'
    'angular-geolocation': 'lib/components/angularjs-geolocation/dist/angularjs-geolocation.min'
    'ngRoute': 'lib/components/angular-route/angular-route'
    'literallycanvas': 'lib/literallycanvas/literallycanvas-0.3-rc4/js/literallycanvas.jquery'
    'jquery': 'lib/components/jquery/dist/jquery'

  shim:
    'jquery':
      exports: 'jquery'

    'literallycanvas':
      deps: ['jquery']

    'angular':
      deps: ['jquery']
      exports: 'angular'

    'ngRoute': ['angular']
    'angular-geolocation': ['angular']

  packages:
    [
      name: 'app', package: 'app/'
    ]

require [
  'angular'
  'etch',
  'literallycanvas'
],
  (ng, etch) ->
    ng.bootstrap(document, [etch.name])