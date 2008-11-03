/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import org.apache.commons.lang.StringUtils;

/**
 * A simple modifier that lets you add a preconfigured prefix and suffix to a string
 * 
 * @author ebunders
 * 
 */
public class PrefixSuffixModifier implements Modifier {

    protected String prefix;
    protected String suffix;

    public PrefixSuffixModifier(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    public PrefixSuffixModifier() {
        
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String modify(String input) {
        if (!StringUtils.isEmpty(prefix)) {
            input = prefix + input;
        }
        if (!StringUtils.isEmpty(suffix)) {
            input = input + suffix;
        }
        return input;
    }
    
    public Modifier copy() {
        return new PrefixSuffixModifier(prefix, suffix);
    }

}
