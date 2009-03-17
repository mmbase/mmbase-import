package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String id;
    private String titel;
    private String omschrijving;
    private Foto foto;
    private boolean volgeboekt;
    private boolean aanmeldingGesloten;
    private Date aanvangstijd;
    private Date eindtijd;

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

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
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

    public void setAanvangstijd(Date aanvangstijd) {
        this.aanvangstijd = aanvangstijd;
    }

    public Date getAanvangstijd() {
        return aanvangstijd;
    }

    public void setEindtijd(Date eindtijd) {
        this.eindtijd = eindtijd;
    }

    public Date getEindtijd() {
        return eindtijd;
    }
}
