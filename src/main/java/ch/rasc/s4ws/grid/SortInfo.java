package ch.rasc.s4ws.grid;

public class SortInfo {
	private String property;

	private SortDirection direction;

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public SortDirection getDirection() {
		return this.direction;
	}

	public void setDirection(SortDirection direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "SortInfo [property=" + this.property + ", direction=" + this.direction
				+ "]";
	}

}
