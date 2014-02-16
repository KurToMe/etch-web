define [
  'angular',
  'ngRoute',

  'app'
], (ng) ->
  etch = ng.module 'etch', [
    'ngRoute',
    'app'
  ]

  etch.config ['$routeProvider', '$locationProvider', '$httpProvider',
    ($routeProvider, $locationProvider, $httpProvider) ->
      $routeProvider.when '/etch',
        templateUrl: '/assets/templates/hello.html'
        controller: 'etchController'
        title: 'Hello Title'

      $locationProvider.html5Mode true

      $httpProvider.defaults.cache = true
  ]

  etch


