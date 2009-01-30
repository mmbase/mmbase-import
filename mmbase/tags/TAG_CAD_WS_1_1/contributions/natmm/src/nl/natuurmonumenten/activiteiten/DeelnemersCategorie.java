package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class DeelnemersCategorie implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String naam;
    private int aantalPlaatsenPerDeelnemer;
    private boolean groepsExcursie;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public int getAantalPlaatsenPerDeelnemer() {
        return aantalPlaatsenPerDeelnemer;
    }

    public void setAantalPlaatsenPerDeelnemer(int aantalPlaatsenPerDeelnemer) {
        this.aantalPlaatsenPerDeelnemer = aantalPlaatsenPerDeelnemer;
    }

    public boolean isGroepsExcursie() {
        return groepsExcursie;
    }

    public void setGroepsExcursie(boolean groepsExcursie) {
        this.groepsExcursie = groepsExcursie;
    }
}
