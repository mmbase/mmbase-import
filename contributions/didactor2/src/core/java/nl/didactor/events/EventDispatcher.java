package nl.didactor.events;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventDispatcher {
    private static List listeners = new Vector();

    public static void register(EventListener listener) {
        listeners.add(listener);
    }

    public static void report(Event event, HttpServletRequest request, HttpServletResponse response) {
        for (int i=0; i<listeners.size(); i++) {
            ((EventListener)listeners.get(i)).report(event, request, response);
        }
    }
}
