var idMatch = '1234';
var clientId = Array.from(crypto.getRandomValues(new Uint8Array(16)),
	byte => byte.toString(16).padStart(2, '0')).join('');
var stompClient = DemoMessaging.createClient();

var m1comments, m1title, m1p1serve, m1p2serve;
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

	stompClient.onConnect = function(frame) {
		document.getElementById("m1-status").textContent = 'CONNECTED';
		stompClient.subscribe("/topic/tennis/match/" + idMatch, function(msg) {
			var obj = JSON.parse(msg.body);
			m1title.textContent = obj.title;
			// comments
			m1comments.value = m1comments.value + obj.liveComments;
			m1comments.scrollTop = 999999;
			// serve
			if (obj.serve === obj.player1.name) {
				m1p1serve.textContent = "S";
				m1p2serve.textContent = "";
			} else {
				m1p1serve.textContent = "";
				m1p2serve.textContent = "S";
			}
			// player1
			m1p1.textContent = obj.player1.name;
			m1p1games.textContent = obj.player1.gamesInCurrentSet;
			m1p1sets.textContent = obj.player1.sets;
			m1p1points.textContent = obj.player1Score;
			m1p1set1.textContent = obj.player1.set1;
			m1p1set2.textContent = obj.player1.set2;
			m1p1set3.textContent = obj.player1.set3;
			// player2
			m1p2.textContent = obj.player2.name;
			m1p2games.textContent = obj.player2.gamesInCurrentSet;
			m1p2sets.textContent = obj.player2.sets;
			m1p2points.textContent = obj.player2Score;
			m1p2set1.textContent = obj.player2.set1;
			m1p2set2.textContent = obj.player2.set2;
			m1p2set3.textContent = obj.player2.set3;

			document.getElementById("m1-status").textContent = 'LIVE';
		});
		
		stompClient.subscribe("/queue/tennis/bet/" + clientId + '/' + idMatch, function(msg) {
			var obj = msg.body;
			document.getElementById("m1-betmatchwinner-result").textContent = obj;
		});
		
	};
	stompClient.onStompError = function(error) {
		document.getElementById("m1-status").textContent = 'Connection error';
	};
	stompClient.activate();

}
function betMatchWinner(player) {
	if (!stompClient.connected) {
		return;
	}
	document.getElementById("m1-betmatchwinner").textContent = player;
	document.getElementById("m1-betmatchwinner-result").textContent = "";
	
	stompClient.publish({ destination: "/app/tennis/bet/"+clientId+'/'+idMatch, body: player });
}

window.addEventListener("load", connect, false);
