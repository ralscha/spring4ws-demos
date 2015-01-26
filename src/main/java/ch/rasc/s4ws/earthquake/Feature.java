package ch.rasc.s4ws.earthquake;

public class Feature {

	private String type;

	private Properties properties;

	private Geometry geometry;

	private String id;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Geometry getGeometry() {
		return this.geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Feature [type=" + this.type + ", properties=" + this.properties
				+ ", geometry=" + this.geometry + ", id=" + this.id + "]";
	}

}
