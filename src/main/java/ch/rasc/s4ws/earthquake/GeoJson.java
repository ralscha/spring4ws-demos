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
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public List<BigDecimal> getBbox() {
		return bbox;
	}

	public void setBbox(List<BigDecimal> bbox) {
		this.bbox = bbox;
	}

	@Override
	public String toString() {
		return "GeoJson [type=" + type + ", metadata=" + metadata + ", features=" + features + ", bbox=" + bbox + "]";
	}

}
