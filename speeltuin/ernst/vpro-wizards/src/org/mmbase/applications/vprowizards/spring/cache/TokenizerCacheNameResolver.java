package org.mmbase.applications.vprowizards.spring.cache;

import java.util.*;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class helps you to handle strings that are formatted in a certain way. The idear is that you have a comma
 * seperated list of values attached to a namespace, like 'namespace:value1,value2,value3'.<br>
 * the input string can contain more that one of these constructs for different namespaces, separated by a 
 * space. When using this class you have to register the namespaces you look for. If one of the sections of
 * the input string dous not start with any of the registered namespaces, it will assume the values are 'global',
 * and they are added to the values for all the namespaces you have registered.
 * In this way you can define various cache groups to be flushed for request, node and/or relation type of cache flush hints.
 * 
 * @author ebunders
 * 
 */
public class TokenizerCacheNameResolver implements CacheNameResolver {

    private List<String> nameSpaces = new ArrayList<String>();
    private List<Modifier> modifiers = new ArrayList<Modifier>();
    private Map<String, List<String>> namesForNamespace = null;
    private String input = null;
    private String nameSpace = null;

    private static Logger log = Logging.getLoggerInstance(TokenizerCacheNameResolver.class);


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
        if(StringUtils.isEmpty(nameSpace)) {
            throw new IllegalStateException("set nameSpace first");
        }
        if(nameSpaces.size() == 0) {
            throw new IllegalStateException("set namespaces first");
        }
        namesForNamespace = new HashMap<String, List<String>>();
        List<String> parts = Arrays.asList(input.trim().split(" "));
        List<String> globals = new ArrayList<String>();
        for (String part : parts) {
            part = part.trim();
            boolean partHasNamespace = false;
            for (String namespace : nameSpaces) {
                String _namespace = namespace + ":";
                if (part.startsWith(_namespace)) {
                    partHasNamespace = true;
                    part = part.substring(_namespace.length());
                    //TODO: het zou kunnen gebeuren date er twee 'parts' zijn voor dezelfde namespace
                    namesForNamespace.put(namespace, modify(Arrays.asList(part.split(","))));
                }
            }
            if (!partHasNamespace) {
                globals.addAll(modify(Arrays.asList(part.split(","))));
            }
        }

        // are there globals? add them to all the namespaces
        if (globals.size() > 0) {
            for (String namespace : namesForNamespace.keySet()) {
                namesForNamespace.get(namespace).addAll(globals);
            }
        }
    }

    /**
     * apply all the modifiers to a list of strings.
     * All the cache names that are resolved will be put through all the registered modifiers
     * before they are returned.
     * 
     * @param items
     * @return
     */
    private List<String> modify(List<String> items) {
        List<String> result = new ArrayList<String>();
        for (String item: items) {
            for (Modifier modifier : modifiers) {
                item = modifier.modify(item);
            }
            result.add(item);
        }
        return result;
    }

    public TokenizerCacheNameResolver addModifier(Modifier modifier) {
        modifiers.add(modifier);
        return this;
    }
    
    public void addModifiers(List<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    /* (non-Javadoc)
     * @see org.mmbase.applications.vprowizard.spring.cache.CacheNameResolver#getNamesForNamespace(java.lang.String)
     */
    public List<String> getNames() {
        if(StringUtils.isEmpty(nameSpace)) {
            throw new IllegalStateException("attribute namespace is empty");
        }
        if(!nameSpaces.contains(nameSpace)) {
            throw new IllegalStateException("namespace '"+nameSpace+"' is not known" );
        }
        if (namesForNamespace == null) {
            tokenize();
        }
        return namesForNamespace.get(nameSpace);
    }
    
    private void reset() {
        namesForNamespace = null;
    }
    
    /* (non-Javadoc)
     * @see org.mmbase.applications.vprowizard.spring.cache.CacheNameResolver#setInput(java.lang.String)
     */
    public void setInput(String input) {
        reset();
        this.input = input;
    }
    
    /**
     * Set the nameSpace that you want to fetch the cache names for 
     * @param nameSpace
     */
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    
    /**
     * Add the namespaces that are used to tokenize the input string
     * TODO: why? why not tokenize on all the namespaces you find?
     * @param namespaces
     */
    public void addNameSpaces(String[] namespaces) {
        List<String> disco = Arrays.asList(namespaces);
        for (String string : disco) {
            nameSpaces.add(string);
        }
    }
    
}
