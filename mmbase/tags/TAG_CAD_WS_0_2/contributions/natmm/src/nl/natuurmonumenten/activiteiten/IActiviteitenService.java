package nl.natuurmonumenten.activiteiten;

import java.util.Date;

public interface IActiviteitenService {
    public abstract String getVersion();

    /*
     * Geeft lijst van Provincies
     * 
     * @return array van Provincies of een lege array indien er geen resultaten
     * zijn
     */
    public abstract Provincie[] getProvincies();

    public abstract Event[] getEvents(Date start, Date eind, String[] eventTypeIds, String provincieId,
            String natuurgebiedenId);

    public abstract EventType[] getEventTypes();

    public abstract MediaType[] getMediaTypes();

    public abstract DeelnemersCategorie[] getDeelnemersCategorieen();

    public abstract Natuurgebied[] getNatuurgebieden();
    
    public abstract EventDetails getEventDetails(String id);

    public abstract Vertrekpunt[] getVertrekpunten();
}