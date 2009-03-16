package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class Kosten implements Serializable {
    private String deelnemersCategorieId;
    private int kostenInCenten;
    public String getDeelnemersCategorieId() {
        return deelnemersCategorieId;
    }
    public void setDeelnemersCategorieId(String deelnemersCategorieId) {
        this.deelnemersCategorieId = deelnemersCategorieId;
    }
    public int getKostenInCenten() {
        return kostenInCenten;
    }
    public void setKostenInCenten(int kostenInCenten) {
        this.kostenInCenten = kostenInCenten;
    }
}
