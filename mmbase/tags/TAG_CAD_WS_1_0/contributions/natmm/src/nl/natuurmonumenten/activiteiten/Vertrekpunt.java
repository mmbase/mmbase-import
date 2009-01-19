package nl.natuurmonumenten.activiteiten;

public class Vertrekpunt {
    private String id;
    private String titel;
    private String intro;
    private String omschrijving;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String getTitel() {
        return titel;
    }
    public void setTitel(String titel) {
        this.titel = titel;
    }
    public String getIntro() {
        return intro;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
    public String getOmschrijving() {
        return omschrijving;
    }
    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }
}
