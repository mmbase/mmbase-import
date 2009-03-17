package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;
import java.util.Date;

// detail gegevens parent event
public class EventDetails implements Serializable {
    private String id;
    private String titel;
    private String korteOmschrijving;
    private String volledigeOmschrijving;
    private Date aanvangstijd;
    private Date eindtijd;
    private Foto foto;
    private String contactPersoon;
    private String[] eventTypeId;
    private String[] natuurgebiedId;
    private Kosten[] kosten;
    private String[] vertrekpuntId;
    private String[] extraInfo;
    private String typeAanmeldMogelijkheid;
    // als een event eenmalig is dan heeft het geen childs, we zetten dan de parent info in EventData
    private boolean eenmaligEvent;
    // detail gegevens per child event
    private EventData[] eventData;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitel() {
        return titel;
    }
    public void setTitel(String titel) {
        this.titel = titel;
    }
    public String getKorteOmschrijving() {
        return korteOmschrijving;
    }
    public void setKorteOmschrijving(String korteOmschrijving) {
        this.korteOmschrijving = korteOmschrijving;
    }
    public String getVolledigeOmschrijving() {
        return volledigeOmschrijving;
    }
    public void setVolledigeOmschrijving(String volledigeOmschrijving) {
        this.volledigeOmschrijving = volledigeOmschrijving;
    }
    public Date getAanvangstijd() {
        return aanvangstijd;
    }
    public void setAanvangstijd(Date aanvangstijd) {
        this.aanvangstijd = aanvangstijd;
    }
    public Date getEindtijd() {
        return eindtijd;
    }
    public void setEindtijd(Date eindtijd) {
        this.eindtijd = eindtijd;
    }
    public Foto getFoto() {
        return foto;
    }
    public void setFoto(Foto foto) {
        this.foto = foto;
    }
    public String getContactPersoon() {
        return contactPersoon;
    }
    public void setContactPersoon(String contactPersoon) {
        this.contactPersoon = contactPersoon;
    }
    public String[] getEventTypeId() {
        return eventTypeId;
    }
    public void setEventTypeId(String[] eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
    public void setNatuurgebiedId(String[] natuurgebiedId) {
        this.natuurgebiedId = natuurgebiedId;
    }
    public String[] getNatuurgebiedId() {
        return natuurgebiedId;
    }
    public void setKosten(Kosten[] kosten) {
        this.kosten = kosten;
    }
    public Kosten[] getKosten() {
        return kosten;
    }
    public String[] getVertrekpuntId() {
        return vertrekpuntId;
    }
    public void setVertrekpuntId(String[] vertrekpuntId) {
        this.vertrekpuntId = vertrekpuntId;
    }
    public void setExtraInfo(String[] extraInfo) {
        this.extraInfo = extraInfo;
    }
    public String[] getExtraInfo() {
        return extraInfo;
    }
    public void setTypeAanmeldMogelijkheid(String typeAanmeldMogelijkheid) {
        this.typeAanmeldMogelijkheid = typeAanmeldMogelijkheid;
    }
    public String getTypeAanmeldMogelijkheid() {
        return typeAanmeldMogelijkheid;
    }
    public void setEventData(EventData[] eventData) {
        this.eventData = eventData;
    }
    public EventData[] getEventData() {
        return eventData;
    }
    public void setEenmaligEvent(boolean eenmaligEvent) {
        this.eenmaligEvent = eenmaligEvent;
    }
    public boolean isEenmaligEvent() {
        return eenmaligEvent;
    }
}
