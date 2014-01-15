angular.module('numbers.app', ['d3.directives', 'numbers.directives'])
  .controller('NumbersCtrl', ['$scope', function($scope) {
	
	var absession;
	  
    $scope.numbers = {};
    $scope.running = false;

    $scope.stop = function() {
      if (absession != null) absession.close();
      absession = null;
      $scope.running = false;
    };

    $scope.executeExpression = function() {
      if (absession != null) absession.close();
      
	  var path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')+1);
	  ab._construct = function(url, protocols) {
		return new SockJS(url);
	  };
	
	  ab.connect(path + 'wamp', function(session) {
	    absession = session;
	    console.log("Connected to ", absession);
        $scope.running = true;
        
		absession.subscribe("data", function(topic, data) {
			$scope.$apply(function() {
		          $scope.numbers = data;
		    });
		});
                
	  }, function(code, reason) {
		absession = null;
		console.log("Connection lost (" + reason + ")");
	  }, {
		skipSubprotocolCheck: true
	  });      
      
      
    };

  }]);
