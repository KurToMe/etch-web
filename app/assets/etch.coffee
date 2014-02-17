define [
  'angular',
  'ngRoute',
  'angular-geolocation'

  'app'
], (ng) ->
  etch = ng.module 'etch', [
    'ngRoute',
    'app'
    'geolocation'
  ]

  etch.config ['$routeProvider', '$locationProvider', '$httpProvider',
    ($routeProvider, $locationProvider, $httpProvider) ->
      $routeProvider.when '/etch',
        templateUrl: '/assets/templates/main.html'
        controller: 'etchController'
      $routeProvider.otherwise
        redirectTo: '/etch'


      $locationProvider.html5Mode true

      $httpProvider.defaults.cache = true
  ]

  etch


