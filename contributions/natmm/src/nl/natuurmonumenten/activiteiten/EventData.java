package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;
import java.util.Date;

// detail gegevens child event
public class EventData implements Serializable {
    private String id;
    private Date aanvangstijd;
    private Date eindtijd;
    private int aantalPlaatsenBeschikbaar;
    private boolean volgeboekt;
    private boolean aanmeldingGesloten;
    private boolean geannuleerd;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public int getAantalPlaatsenBeschikbaar() {
        return aantalPlaatsenBeschikbaar;
    }
    public void setAantalPlaatsenBeschikbaar(int aantalPlaatsenBeschikbaar) {
        this.aantalPlaatsenBeschikbaar = aantalPlaatsenBeschikbaar;
    }
    public boolean isVolgeboekt() {
        return volgeboekt;
    }
    public void setVolgeboekt(boolean volgeboekt) {
        this.volgeboekt = volgeboekt;
    }
    public boolean isAanmeldingGesloten() {
        return aanmeldingGesloten;
    }
    public void setAanmeldingGesloten(boolean aanmeldingGesloten) {
        this.aanmeldingGesloten = aanmeldingGesloten;
    }
    public boolean isGeannuleerd() {
        return geannuleerd;
    }
    public void setGeannuleerd(boolean geannuleerd) {
        this.geannuleerd = geannuleerd;
    }
    
}
