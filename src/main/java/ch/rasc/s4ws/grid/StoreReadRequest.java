package ch.rasc.s4ws.grid;

import java.util.Collections;
import java.util.Map;

public class StoreReadRequest {

	private String query;

	private Integer limit;

	private Integer start;

	private Integer page;

	private String sort;

	private Map<String, Object> params;

	public StoreReadRequest() {
		this.params = Collections.emptyMap();
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getLimit() {
		return this.limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getStart() {
		return this.start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getPage() {
		return this.page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public String getSort() {
		return this.sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Map<String, Object> getParams() {
		return Collections.unmodifiableMap(this.params);
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "StoreReadRequest [query=" + this.query + ", limit=" + this.limit
				+ ", start=" + this.start + ", page=" + this.page + ", sort=" + this.sort
				+ ", params=" + this.params + "]";
	}

}
