package org.mmbase.applications.vprowizards.spring.cache;


public interface Modifier {
    public Modifier copy();
    public String modify(String input);
}
