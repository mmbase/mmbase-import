package nl.didactor.events;

public interface DidactorEventListener extends org.mmbase.core.event.EventListener {
    public void notify(Event event);
}
