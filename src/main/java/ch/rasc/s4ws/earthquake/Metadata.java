package ch.rasc.s4ws.earthquake;

public class Metadata {

	private Long generated;

	private String url;

	private String title;

	private Integer status;

	private String api;

	private Integer count;

	public Long getGenerated() {
		return this.generated;
	}

	public void setGenerated(Long generated) {
		this.generated = generated;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getApi() {
		return this.api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Metadata [generated=" + this.generated + ", url=" + this.url + ", title="
				+ this.title + ", status=" + this.status + ", api=" + this.api
				+ ", count=" + this.count + "]";
	}

}
