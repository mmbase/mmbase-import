package org.mmbase.util.transformers;

/**
 * Describes what encoding is configured.
 */

public class Config {
    public Class clazz;
    public int   config;
    public String info;
    public Config(Class c, int i ) {
        clazz = c;
        config = i;
        info = "";
    }
    public Config(Class c, int i, String in ) {
        clazz = c;
        config = i;
        info = in;
    }
}
