package nl.didactor.events;

public class DidactorEventBroker extends org.mmbase.core.event.AbstractEventBroker {
    @Override
    public boolean canBrokerForListener(org.mmbase.core.event.EventListener listener) {
        return listener instanceof DidactorEventListener;
    }

    @Override
    public boolean canBrokerForEvent(org.mmbase.core.event.Event event) {
        return event instanceof Event;
    }

    @Override
    protected void notifyEventListener(org.mmbase.core.event.Event event, org.mmbase.core.event.EventListener listener) {
        Event de = (Event) event; //!!!!!
        DidactorEventListener del = (DidactorEventListener) listener;
        del.notify(de);
    }

    public String toString() {
        return "Didactor Event Broker";
    }

}
