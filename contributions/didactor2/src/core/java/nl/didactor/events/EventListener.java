package nl.didactor.events;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface EventListener {
    public void report(Event event, HttpServletRequest request, HttpServletResponse response);
}
