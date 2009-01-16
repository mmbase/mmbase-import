package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class BaseBean implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
}
