package nl.vpro.redactie.actions;


public class CreateTrackAction extends Action {
	private String subtype;
	private Boolean realmedia = false;
	private Boolean windowsmedia = false;
	private Boolean vodcast = false;
	private Boolean webradio = false;
	private Duration start = new Duration();
	private Duration stop = new Duration();
	

	public Boolean getRealmedia() {
		return realmedia;
	}
	public void setRealmedia(Boolean realmedia) {
		this.realmedia = realmedia;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public Boolean getVodcast() {
		return vodcast;
	}
	public void setVodcast(Boolean vodcast) {
		this.vodcast = vodcast;
	}
	public Boolean getWindowsmedia() {
		return windowsmedia;
	}
	public void setWindowsmedia(Boolean windowsmedia) {
		this.windowsmedia = windowsmedia;
	}
	public Boolean getWebradio() {
		return webradio;
	}
	public void setWebradio(Boolean webradio) {
		this.webradio = webradio;
	}
	public Duration getStart() {
		return start;
	}
	public void setStart(Duration start) {
		this.start = start;
	}
	public Duration getStop() {
		return stop;
	}
	public void setStop(Duration stop) {
		this.stop = stop;
	}
}