package ch.rasc.s4ws.scheduler;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties({ "Cls", "Draggable", "Resizable" })
public class CustomEvent {

	@JsonProperty("Id")
	private int id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("StartDate")
	@JsonSerialize(using = ISO8601DateTimeSerializer.class)
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private DateTime startDate;

	@JsonProperty("EndDate")
	@JsonSerialize(using = ISO8601DateTimeSerializer.class)
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private DateTime endDate;

	@JsonProperty("ResourceId")
	private int resourceId;

	// true, false, 'start' or 'end'
	@JsonProperty("Resizable")
	private Object resizable;

	@JsonProperty("Draggable")
	private Boolean draggable;

	@JsonProperty("Cls")
	private String cls;

	@JsonProperty("Blocked")
	private Boolean blocked;

	@JsonProperty("BlockedBy")
	private String blockedBy;

	@JsonProperty("Done")
	private Boolean done;

	public CustomEvent() {
		// default constructor
	}

	public CustomEvent(int id, int resourceId, String name, DateTime startDate,
			DateTime endDate, boolean done) {
		this.id = id;
		this.resourceId = resourceId;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.done = done;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DateTime getStartDate() {
		return this.startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		return this.endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public int getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public Object getResizable() {
		return this.resizable;
	}

	public void setResizable(Object resizable) {
		this.resizable = resizable;
	}

	public Boolean getDraggable() {
		return this.draggable;
	}

	public void setDraggable(Boolean draggable) {
		this.draggable = draggable;
	}

	public String getCls() {
		return this.cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public Boolean getBlocked() {
		return this.blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public String getBlockedBy() {
		return this.blockedBy;
	}

	public void setBlockedBy(String blockedBy) {
		this.blockedBy = blockedBy;
	}

	public Boolean getDone() {
		return this.done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return "CustomEvent [id=" + this.id + ", name=" + this.name + ", startDate="
				+ this.startDate + ", endDate=" + this.endDate + ", resourceId="
				+ this.resourceId + ", resizable=" + this.resizable + ", draggable="
				+ this.draggable + ", cls=" + this.cls + ", blocked=" + this.blocked
				+ ", blockedBy=" + this.blockedBy + ", done=" + this.done + "]";
	}

}
