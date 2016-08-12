'use strict';

angular.module('sbAdminApp').controller('WordCloudCtrl', function($scope) {
    $scope.words = [
        {text: "동해물과", weight: 13},
        {text: "백두산이", weight: 10.5},
        {text: "마르고", weight: 9.4},
        {text: "닳도록", weight: 8},
        {text: "하느님이", weight: 6.2},
        {text: "보우하사", weight: 5},
        {text: "우리나라만세", weight: 5},
        {text: "Elit", weight: 5},
        {text: "Nam et", weight: 5},
        {text: "Leo", weight: 4},
        {text: "Sapien", weight: 4},
        {text: "Pellentesque", weight: 3},
        {text: "habitant", weight: 3},
        {text: "morbi", weight: 3},
        {text: "tristisque", weight: 3},
        {text: "senectus", weight: 3},
        {text: "et netus", weight: 3},
        {text: "et malesuada", weight: 3},
        {text: "fames", weight: 2},
        {text: "ac turpis", weight: 2},
        {text: "egestas", weight: 2},
        {text: "Aenean", weight: 2},
        {text: "vestibulum", weight: 2},
        {text: "elit", weight: 2},
        {text: "sit amet", weight: 2},
        {text: "metus", weight: 2},
        {text: "adipiscing", weight: 2},
        {text: "ut ultrices", weight: 2}
    ];

    $scope.colors = ["#800026", "#bd0026", "#e31a1c", "#fc4e2a", "#fd8d3c", "#feb24c", "#fed976"];

    $scope.update = function() {
        $scope.words.splice(-5);
    };
});
