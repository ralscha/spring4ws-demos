package ch.rasc.s4ws.earthquake;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Geometry {

	private String type;

	private List<BigDecimal> coordinates = new ArrayList<>();

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<BigDecimal> getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(List<BigDecimal> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public String toString() {
		return "Geometry [type=" + this.type + ", coordinates=" + this.coordinates + "]";
	}

}
