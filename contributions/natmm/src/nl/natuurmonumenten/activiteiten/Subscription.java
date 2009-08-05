package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class Subscription implements Serializable {
    private String evenementId;
    private Aanmelding[] aanmeldingen;
    private String voornaam;
    private String voorletter;
    private String tussenvoegsel;
    private String achternaam;
    private String telefoon;
    private String email;
    private String straat;
    private String huisnummer;
    private String postcode;
    private String plaats;
    private String land;
    private String lidnummer;
    private String bijzonderheden;
    private String mediaTypeId;
    public String getEvenementId() {
        return evenementId;
    }
    public void setEvenementId(String evenementId) {
        this.evenementId = evenementId;
    }
    public void setAanmeldingen(Aanmelding[] aanmeldingen) {
        this.aanmeldingen = aanmeldingen;
    }
    public Aanmelding[] getAanmeldingen() {
        return aanmeldingen;
    }
    public String getVoornaam() {
        return voornaam;
    }
    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }
    public void setVoorletter(String voorletter) {
        this.voorletter = voorletter;
    }
    public String getVoorletter() {
        return voorletter;
    }
    public String getTussenvoegsel() {
        return tussenvoegsel;
    }
    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }
    public String getAchternaam() {
        return achternaam;
    }
    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }
    public String getTelefoon() {
        return telefoon;
    }
    public void setTelefoon(String telefoon) {
        this.telefoon = telefoon;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getStraat() {
        return straat;
    }
    public void setStraat(String straat) {
        this.straat = straat;
    }
    public String getHuisnummer() {
        return huisnummer;
    }
    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }
    public String getPostcode() {
        return postcode;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public String getPlaats() {
        return plaats;
    }
    public void setPlaats(String plaats) {
        this.plaats = plaats;
    }
    public String getLand() {
        return land;
    }
    public void setLand(String land) {
        this.land = land;
    }
    public String getLidnummer() {
        return lidnummer;
    }
    public void setLidnummer(String lidnummer) {
        this.lidnummer = lidnummer;
    }
    public String getBijzonderheden() {
        return bijzonderheden;
    }
    public void setBijzonderheden(String bijzonderheden) {
        this.bijzonderheden = bijzonderheden;
    }
    public String getMediaTypeId() {
        return mediaTypeId;
    }
    public void setMediaTypeId(String mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }
}
