package ch.rasc.s4ws.earthquake;

import java.math.BigDecimal;

public class Properties {

	private BigDecimal mag;

	private String place;

	private Long time;

	private Long updated;

	private Integer tz;

	private String url;

	private String detail;

	private Integer felt;

	private BigDecimal cdi;

	private BigDecimal mmi;

	private String alert;

	private String status;

	private Integer tsunami;

	private Integer sig;

	private String net;

	private String code;

	private String ids;

	private String sources;

	private String types;

	private Integer nst;

	private BigDecimal dmin;

	private BigDecimal rms;

	private BigDecimal gap;

	private String magType;

	private String type;

	private String title;

	public BigDecimal getMag() {
		return this.mag;
	}

	public void setMag(BigDecimal mag) {
		this.mag = mag;
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Long getTime() {
		return this.time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getUpdated() {
		return this.updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	public Integer getTz() {
		return this.tz;
	}

	public void setTz(Integer tz) {
		this.tz = tz;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Integer getFelt() {
		return this.felt;
	}

	public void setFelt(Integer felt) {
		this.felt = felt;
	}

	public BigDecimal getCdi() {
		return this.cdi;
	}

	public void setCdi(BigDecimal cdi) {
		this.cdi = cdi;
	}

	public BigDecimal getMmi() {
		return this.mmi;
	}

	public void setMmi(BigDecimal mmi) {
		this.mmi = mmi;
	}

	public String getAlert() {
		return this.alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTsunami() {
		return this.tsunami;
	}

	public void setTsunami(Integer tsunami) {
		this.tsunami = tsunami;
	}

	public Integer getSig() {
		return this.sig;
	}

	public void setSig(Integer sig) {
		this.sig = sig;
	}

	public String getNet() {
		return this.net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIds() {
		return this.ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getSources() {
		return this.sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getTypes() {
		return this.types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public Integer getNst() {
		return this.nst;
	}

	public void setNst(Integer nst) {
		this.nst = nst;
	}

	public BigDecimal getDmin() {
		return this.dmin;
	}

	public void setDmin(BigDecimal dmin) {
		this.dmin = dmin;
	}

	public BigDecimal getRms() {
		return this.rms;
	}

	public void setRms(BigDecimal rms) {
		this.rms = rms;
	}

	public BigDecimal getGap() {
		return this.gap;
	}

	public void setGap(BigDecimal gap) {
		this.gap = gap;
	}

	public String getMagType() {
		return this.magType;
	}

	public void setMagType(String magType) {
		this.magType = magType;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Properties [mag=" + this.mag + ", place=" + this.place + ", time="
				+ this.time + ", updated=" + this.updated + ", tz=" + this.tz + ", url="
				+ this.url + ", detail=" + this.detail + ", felt=" + this.felt + ", cdi="
				+ this.cdi + ", mmi=" + this.mmi + ", alert=" + this.alert + ", status="
				+ this.status + ", tsunami=" + this.tsunami + ", sig=" + this.sig
				+ ", net=" + this.net + ", code=" + this.code + ", ids=" + this.ids
				+ ", sources=" + this.sources + ", types=" + this.types + ", nst="
				+ this.nst + ", dmin=" + this.dmin + ", rms=" + this.rms + ", gap="
				+ this.gap + ", magType=" + this.magType + ", type=" + this.type
				+ ", title=" + this.title + "]";
	}

}
