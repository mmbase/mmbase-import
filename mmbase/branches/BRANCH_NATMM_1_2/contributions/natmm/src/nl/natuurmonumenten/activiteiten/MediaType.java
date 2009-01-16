package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class MediaType implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String naam;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getNaam() {
        return naam;
    }
    
}
