package ch.rasc.s4ws.map;

public class LatLng {
	private final double lat;

	private final double lng;

	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return this.lat;
	}

	public double getLng() {
		return this.lng;
	}

}
