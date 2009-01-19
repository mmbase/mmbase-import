package nl.natuurmonumenten.activiteiten;

import java.util.Arrays;
import java.util.Date;

public class MockActiviteitenService implements IActiviteitenService {
    public DeelnemersCategorie[] getDeelnemersCategorieen() {
        // TODO Auto-generated method stub
        return null;
    }

    public EventType[] getEventTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public Event[] getEvents(Date start, Date eind, String[] eventTypeIds, String provincieId, String natuurgebiedenId) {
        // TODO Auto-generated method stub
        System.out.println("eventTypeIds:" + Arrays.asList(eventTypeIds));
        return null;
    }

    public MediaType[] getMediaTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public Natuurgebied[] getNatuurgebieden() {
        // TODO Auto-generated method stub
        return null;
    }

    public Provincie[] getProvincies() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDetails getEventDetails(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Vertrekpunt[] getVertrekpunten() {
        // TODO Auto-generated method stub
        return null;
    }

    public String subscribeEvent(Subscription subscription) {
        // TODO Auto-generated method stub
        return null;
    }
}
