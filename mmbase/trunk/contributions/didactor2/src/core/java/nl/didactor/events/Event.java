package nl.didactor.events;

public class Event {
    private String username;
    private String sessionid;
    private Integer provider;
    private Integer education;
    private Integer cls;
    private String eventtype;
    private String eventvalue;
    private String note;
    
    public Event(String username, String sessionid, Integer provider, Integer education, Integer cls, String eventtype, String eventvalue, String note) {
        this.username = username;
        this.sessionid = sessionid;
        this.provider = provider;
        this.education = education;
        this.cls = cls;
        this.eventtype = eventtype;
        this.eventvalue = eventvalue;
        this.note = note;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionid;
    }

    public Integer getProvider() {
        return provider;
    }

    public Integer getEducation() {
        return education;
    }

    public Integer getClassId() {
        return cls;
    }

    public String getEventType() {
        return eventtype;
    }

    public String getEventValue() {
        return eventvalue;
    }

    public String getNote() {
        return note;
    }
}
