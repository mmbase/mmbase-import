package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class Aanmelding implements Serializable {
    private String deelnemersCategorieId;
    private int aantal;
    public String getDeelnemersCategorieId() {
        return deelnemersCategorieId;
    }
    public void setDeelnemersCategorieId(String deelnemersCategorieId) {
        this.deelnemersCategorieId = deelnemersCategorieId;
    }
    public int getAantal() {
        return aantal;
    }
    public void setAantal(int aantal) {
        this.aantal = aantal;
    }

}
