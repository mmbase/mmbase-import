package nl.natuurmonumenten.activiteiten;

import java.io.Serializable;

public class Foto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String titel;
    private String mimeType;
    private byte[] data;
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
    public String getMimeType() {
        return mimeType;
    }
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
}

