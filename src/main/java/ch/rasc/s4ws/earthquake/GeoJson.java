package ch.rasc.s4ws.earthquake;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GeoJson {

	private String type;

	private Metadata metadata;

	private List<Feature> features = new ArrayList<>();

	private List<BigDecimal> bbox = new ArrayList<>();

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Metadata getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public List<Feature> getFeatures() {
		return this.features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public List<BigDecimal> getBbox() {
		return this.bbox;
	}

	public void setBbox(List<BigDecimal> bbox) {
		this.bbox = bbox;
	}

	@Override
	public String toString() {
		return "GeoJson [type=" + this.type + ", metadata=" + this.metadata
				+ ", features=" + this.features + ", bbox=" + this.bbox + "]";
	}

}
