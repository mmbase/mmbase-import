package nl.vpro.redactie.actions;

public class Duration {
	
	private long milliseconds = 0L;
	private long seconds = 0L;
	private long minutes = 0L;
	private long hours = 0L;
	
	public long getHours() {
		return hours;
	}

	public void setHours(long hours) {
		this.hours = hours;
	}

	public long getMilliseconds() {
		return milliseconds;
	}

	public void setMilliseconds(long milliseconds) {
		this.milliseconds = milliseconds;
	}

	public long getMinutes() {
		return minutes;
	}

	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}

	public long getSeconds() {
		return seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}
	
	public long getDuration() {
		return milliseconds+seconds*1000+minutes*1000*60+hours*1000*60*60;
	}
	
	public String toString(){
		return "Duration = "+getDuration();
	}
}