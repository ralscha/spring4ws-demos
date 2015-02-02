package ch.rasc.s4ws.tail;

public class Access {

	private String ip;

	private long date;

	private Double[] ll;

	private String message;

	private String city;

	private String country;

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getDate() {
		return this.date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Double[] getLl() {
		return this.ll;
	}

	public void setLl(Double[] ll) {
		this.ll = ll;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
