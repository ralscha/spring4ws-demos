var range_rec = 512; // KBps
var range_snd = 512; // KBps

window.onload = function() {
    
    var path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')+1);
    var sock  = new SockJS(path + '../sockjs');
    var stompClient = Stomp.over(sock);

    stompClient.connect({}, function(frame) {
    	stompClient.subscribe("/topic/networkinfo", function(msg) {
    		update(msg.body);
    	});
    	   	
    }); 
	
	var rate_rec = 0;
	var rate_snd = 0;
    
	function update(e) {
		var seconds = 1;
		var data = JSON.parse(e);
		var new_rec = data.rec;
		var new_snd = data.snd;

		if ( typeof(old_rec) != "undefined" && typeof(old_snd) != "undefined") 
		{ 
			var bytes_rec = new_rec - old_rec;
			var bytes_snd = new_snd - old_snd;

			rate_rec = bytes_rec / seconds / 1024;
			rate_snd = bytes_snd / seconds / 1024;

			// Check over/under flow
			if ( rate_rec > range_rec  || rate_rec < 0 )
				rate_rec = old_rate_rec;
			else
				old_rate_rec = rate_rec;

			if ( rate_snd > range_snd  || rate_snd < 0 )
				rate_snd = old_rate_snd;
			else
				old_rate_snd = rate_snd;

			document.getElementById("rec_result").innerHTML="Receive: " + Math.round(rate_rec*100)/100 + " KBps";
			document.getElementById("snd_result").innerHTML="Send: "    + Math.round(rate_snd*100)/100 + " KBps";
		}
		old_rec = new_rec;
		old_snd = new_snd;		
	}
	
	rec_graph = new Graph({
		'id': "rec_graph",
		'interval': 1000,
		'strokeStyle': "#819C58",
		'fillStyle': "rgba(64,128,0,0.25)",
		'grid': [ 32, 32 ],
		'range': [ 0, range_rec ],

		'call': function() {
			return (Math.round(rate_rec));
		}
	});

	snd_graph = new Graph({
		'id': "snd_graph",
		'interval': 1000,
		'strokeStyle': "#58819C",
		'fillStyle': "rgba(0,88,145,0.25)",
		'grid': [ 32, 32 ],
		'range': [ 0, range_snd ],

		'call': function() {
			return (Math.round(rate_snd));
		}
	});

};
