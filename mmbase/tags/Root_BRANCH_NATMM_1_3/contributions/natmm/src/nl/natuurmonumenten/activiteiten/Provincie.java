package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class Provincie implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String naam;
    private String omschrijving;

    public Provincie() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

}
