var idMatch = '1234';
var clientId = uuid.v4();
var path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/') + 1);
var sock = new SockJS(path + '../sockjs');
var stompClient = Stomp.over(sock);

var m1comments, m1title;
// Player1 elements
var m1p1, m1p1games, m1p1sets, m1p1points, m1p1set1, m1p1set2, m1p1set3;
// Player2 elements
var m1p2, m1p2games, m1p2sets, m1p2points, m1p2set1, m1p2set2, m1p2set3;

function connect() {
	iniHtmlElements();
	startConnection();
}

function iniHtmlElements() {
	m1p1 = document.getElementById("m1-p1");
	m1p1games = document.getElementById("m1-p1-games");
	m1p1sets = document.getElementById("m1-p1-sets");
	m1p1points = document.getElementById("m1-p1-points");
	m1p1set1 = document.getElementById("m1-p1-set1");
	m1p1set2 = document.getElementById("m1-p1-set2");
	m1p1set3 = document.getElementById("m1-p1-set3");
	m1p1serve = document.getElementById("m1-p1-serve");
	// player2
	m1p2 = document.getElementById("m1-p2");
	m1p2games = document.getElementById("m1-p2-games");
	m1p2sets = document.getElementById("m1-p2-sets");
	m1p2points = document.getElementById("m1-p2-points");
	m1p2set1 = document.getElementById("m1-p2-set1");
	m1p2set2 = document.getElementById("m1-p2-set2");
	m1p2set3 = document.getElementById("m1-p2-set3");
	m1p2serve = document.getElementById("m1-p2-serve");
	// comments
	m1comments = document.getElementById("m1-comments");
	m1title = document.getElementById("m1-title");
}

function startConnection() {

	stompClient.connect({}, function(frame) {
		document.getElementById("m1-status").innerHTML = 'CONNECTED';
		stompClient.subscribe("/topic/tennis/match/" + idMatch, function(msg) {
			var obj = JSON.parse(msg.body);
			m1title.innerHTML = obj.title;
			// comments
			m1comments.value = m1comments.value + obj.liveComments;
			m1comments.scrollTop = 999999;
			// serve
			if (obj.serve === obj.player1.name) {
				m1p1serve.innerHTML = "S";
				m1p2serve.innerHTML = "";
			} else {
				m1p1serve.innerHTML = "";
				m1p2serve.innerHTML = "S";
			}
			// player1
			m1p1.innerHTML = obj.player1.name;
			m1p1games.innerHTML = obj.player1.gamesInCurrentSet;
			m1p1sets.innerHTML = obj.player1.sets;
			m1p1points.innerHTML = obj.player1Score;
			m1p1set1.innerHTML = obj.player1.set1;
			m1p1set2.innerHTML = obj.player1.set2;
			m1p1set3.innerHTML = obj.player1.set3;
			// player2
			m1p2.innerHTML = obj.player2.name;
			m1p2games.innerHTML = obj.player2.gamesInCurrentSet;
			m1p2sets.innerHTML = obj.player2.sets;
			m1p2points.innerHTML = obj.player2Score;
			m1p2set1.innerHTML = obj.player2.set1;
			m1p2set2.innerHTML = obj.player2.set2;
			m1p2set3.innerHTML = obj.player2.set3;

			document.getElementById("m1-status").innerHTML = 'LIVE';
		});
		
		stompClient.subscribe("/queue/tennis/bet/" + clientId + '/' + idMatch, function(msg) {
			var obj = JSON.parse(msg.body);
			document.getElementById("m1-betmatchwinner-result").innerHTML = obj;
		});
		
	}, function(error) {
		document.getElementById("m1-status").innerHTML = 'An ERROR occured: ' + error;
	});

}
function betMatchWinner(player) {
	document.getElementById("m1-betmatchwinner").innerHTML = player;
	document.getElementById("m1-betmatchwinner-result").innerHTML = "";
	
	stompClient.send("/app/tennis/bet/"+clientId+'/'+idMatch, {}, JSON.stringify(player));
}

window.addEventListener("load", connect, false);