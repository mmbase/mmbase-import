/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class helps you to handle strings that are formatted in a certain way. The idea is that you have a comma
 * separated list of values attached to a namespace, like 'namespace:value1,value2,value3'.<br>
 * the input string can contain more that one of these constructs for different namespaces, separated by a 
 * space. When using this class you have to give an input string, and then you can get values for different namespaces.
 * The input string will be parsed once.
 * 
 * @author ebunders
 * 
 */
public class TokenizerCacheNameResolver implements CacheNameResolver {

    
    private Map<String, List<String>> namesForNamespace = null;
    private List<String> globalValues;
    private String input = null;
    
    private final String reNamespace = "^[\\w_]+:";
    private final String reValue = "[\\w_]+";
    private final String reTemplate = "(" + reValue + "(\\[[\\w_]+(:[0-9])?\\])?)?";
    private final String reComposite = reNamespace + reTemplate + "(," + reTemplate + ")*";

    private static Logger log = Logging.getLoggerInstance(TokenizerCacheNameResolver.class);


    /* (non-Javadoc)
     * @see org.mmbase.applications.vprowizard.spring.cache.CacheNameResolver#getNamesForNamespace(java.lang.String)
     */
    public List<String> getNames(String nameSpace) {
        if(StringUtils.isEmpty(nameSpace)) {
            throw new IllegalStateException("attribute namespace is empty");
        }
        
        if (namesForNamespace == null) {
            tokenize();
        }
        List<String> result = new ArrayList<String>();
        if(namesForNamespace.get(nameSpace) != null){
            result.addAll(namesForNamespace.get(nameSpace));
        }
        result.addAll(globalValues);
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.mmbase.applications.vprowizard.spring.cache.CacheNameResolver#setInput(java.lang.String)
     */
    public void setInput(String input) {
        reset();
        this.input = input;
    }
    
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
         * Tokenize the input string with all the configured tokens. All values for each token are then put thrugh all the
         * modifiers
         *TODO: what if the string starts with a namespace that has not been registered.
         *@throws IllegalStateException when input is not set yet.
         */
        private void tokenize() {
            if(StringUtils.isEmpty(input)) {
                throw new IllegalStateException("set input first");
            }
            
            //init 
            namesForNamespace = new HashMap<String, List<String>>();
            globalValues = new ArrayList<String>();
            
            List<String> parts = Arrays.asList(input.trim().split(" "));
            for (String part : parts) {
                part = part.trim();
                boolean partHasNamespace = false;
    //            boolean matches = part.matches("^[\\w_]+:[\\w,]+");
                boolean matches = part.matches(reComposite);
                if (matches) {
                    partHasNamespace = true;
                    String nameSpace = part.substring(0, part.indexOf(":"));
                    part = part.substring(part.indexOf(":")+1);
                    if(namesForNamespace.get(nameSpace) == null){
                        namesForNamespace.put(nameSpace, new ArrayList<String>());
                    }
                    namesForNamespace.get(nameSpace).addAll(Arrays.asList(part.split(",")));
                }
                
                
                if (!partHasNamespace) {
                    globalValues.addAll(Arrays.asList(part.split(",")));
                }
            }
        }



    private void reset() {
        namesForNamespace = null;
    }

    public List<String> getNames() {
        throw new UnsupportedOperationException("this method is not supported for this cache name resolver");
    }
    
    
    
}
