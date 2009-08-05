package nl.didactor.events;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @javadoc
 */
public class Event extends org.mmbase.core.event.Event {
    private final String username;
    private final transient HttpServletRequest request;
    private final Integer provider;
    private final Integer education;
    private final Integer cls;
    private final String eventtype;
    private final String eventvalue;
    private final String note;

    public Event(String username, HttpServletRequest  req, Integer provider, Integer education, Integer cls, String eventtype, String eventvalue, String note) {
        this.username = username;
        this.request = req;
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

    public HttpServletRequest getRequest() {
        return request;
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
