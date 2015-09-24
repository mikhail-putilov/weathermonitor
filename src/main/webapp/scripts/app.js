"use strict";

angular.module('siTableExampleApp', [
    'siTable'
]);

angular.module('siTableExampleApp').controller('WeatherController', function($scope, $http) {
    $scope.params = {};

    $http.get('/api/weatherData').then(function(response) {
        $scope.weatherData = response.data;
    });
});