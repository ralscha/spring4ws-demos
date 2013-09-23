package ch.rasc.s4ws.grid;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StoreReadRequest {

	private String query;

	private Integer limit;

	private Integer start;

	private Integer page;

	private List<SortInfo> sorters;

	private Map<String, Object> params;

	public StoreReadRequest() {
		this.sorters = Collections.emptyList();
		this.params = Collections.emptyMap();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public List<SortInfo> getSorters() {
		return Collections.unmodifiableList(sorters);
	}

	public void setSorters(List<SortInfo> sorters) {
		this.sorters = sorters;
	}

	public Map<String, Object> getParams() {
		return Collections.unmodifiableMap(params);
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "StoreReadRequest [query=" + query + ", limit=" + limit + ", start=" + start + ", page=" + page
				+ ", sorters=" + sorters + ", params=" + params + "]";
	}

}
