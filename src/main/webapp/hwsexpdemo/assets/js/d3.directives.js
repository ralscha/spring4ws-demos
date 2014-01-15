// creates the d3.core module, which contains the various D3 charts
angular.module('d3.directives', ['d3.core'])
  /**
   * Draws basic 2D chart, as in ``<barchart2d data="{{numbers}}"/>``,
   * where ``{{numbers}}`` is a simple array of numbers.
   *
   * It "requires" the ``.barchart2d div`` style definition, which should be
   *
   * ```
   * .barchart2d div {
   *   font: 10px sans-serif;
   *   background-color: steelblue;
   *   text-align: right;
   *   padding: 3px;
   *   margin: 1px;
   *   color: white;
   * }
   * ```
   */
  .directive('barchart2d', ['d3Service', function(d3Service) {
    return {
      restrict: 'E',
      transclude: true,
      scope: { data: '@' },
      template: '<div class="barchart2d" ng-transclude></div>',
      replace: true,
      link: function(scope, element, attrs) {
              d3Service.d3().then(function(d3) {
                function fmt(element, x) {
                  element.style("width", function(d) { return x(d) + "px"; })
                         .text(function(d) { return d; });
                }

                attrs.$observe('data', function(rawValue) {
                  var data = JSON.parse(rawValue);

                  var x = d3.scale.linear()
                      .domain([0, d3.max(data)])
                      .range([0, 420]);

                  var p = d3.select(element[0]).selectAll("div").data(data);
                  fmt(p.enter().append("div"), x);
                  fmt(p.transition(), x);
                  p.exit().remove();
                });
              });
            }
    };
  }]);