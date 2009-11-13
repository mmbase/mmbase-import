package org.mmbase.applications.vprowizards.spring.util;

public class Option {
    private String label;
    private String value;

    public Option(String value, String label) {
        this.label = label;
        this.value = value;
    }

    public Option() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}