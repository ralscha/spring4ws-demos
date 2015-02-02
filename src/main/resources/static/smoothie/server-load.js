function init() {
	initHost('host1');
	initHost('host2');
	initHost('host3');
	initHost('host4');
}

var cpuDataSets = {
	host1: [ new TimeSeries(), new TimeSeries(), new TimeSeries(), new TimeSeries() ],
	host2: [ new TimeSeries(), new TimeSeries(), new TimeSeries(), new TimeSeries() ],
	host3: [ new TimeSeries(), new TimeSeries(), new TimeSeries(), new TimeSeries() ],
	host4: [ new TimeSeries(), new TimeSeries(), new TimeSeries(), new TimeSeries() ]
};

var seriesOptions = [ {
	strokeStyle: 'rgba(255, 0, 0, 1)',
	fillStyle: 'rgba(255, 0, 0, 0.1)',
	lineWidth: 3
}, {
	strokeStyle: 'rgba(0, 255, 0, 1)',
	fillStyle: 'rgba(0, 255, 0, 0.1)',
	lineWidth: 3
}, {
	strokeStyle: 'rgba(0, 0, 255, 1)',
	fillStyle: 'rgba(0, 0, 255, 0.1)',
	lineWidth: 3
}, {
	strokeStyle: 'rgba(255, 255, 0, 1)',
	fillStyle: 'rgba(255, 255, 0, 0.1)',
	lineWidth: 3
} ];

var path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')+1);

var sock  = new SockJS(path + '../smoothieSockJS');
sock.onmessage = function(e) {
	var data = JSON.parse(e.data);
	addDataToDataSets(data.time, data.host1, cpuDataSets.host1);
	addDataToDataSets(data.time, data.host2, cpuDataSets.host2);
	addDataToDataSets(data.time, data.host3, cpuDataSets.host3);
	addDataToDataSets(data.time, data.host4, cpuDataSets.host4);    
};

function addDataToDataSets(time, serverData, dataSets) {
	for ( var i = 0; i < dataSets.length; i++) {
		dataSets[i].append(time, serverData[i]);
	}
}

function initHost(hostId) {

	// Build the timeline
	var timeline = new SmoothieChart({
		fps: 30,
		millisPerPixel: 20,
		grid: {
			strokeStyle: '#555555',
			lineWidth: 1,
			millisPerLine: 1000,
			verticalSections: 4
		}
	});
	for ( var i = 0; i < cpuDataSets[hostId].length; i++) {
		timeline.addTimeSeries(cpuDataSets[hostId][i], seriesOptions[i]);
	}
	timeline.streamTo(document.getElementById(hostId + 'Cpu'), 1000);
}
