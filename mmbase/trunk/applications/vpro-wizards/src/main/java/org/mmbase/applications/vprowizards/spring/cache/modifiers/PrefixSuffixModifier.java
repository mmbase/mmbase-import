/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache.modifiers;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.Modifier;

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
        if(prefix == null){
            prefix = "";
        }
        this.prefix = prefix.trim();
    }

    public void setSuffix(String suffix) {
        if(suffix == null){
            suffix  = "";
        }
        this.suffix = suffix.trim();
    }

    public String modify(String input) {
        if (!StringUtils.isBlank(prefix)) {
            input = prefix + input;
        }
        if (!StringUtils.isBlank(suffix)) {
            input = input + suffix;
        }
        return input;
    }
    
    public Modifier copy() {
        return new PrefixSuffixModifier(prefix, suffix);
    }

}
