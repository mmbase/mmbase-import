/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.applications.vprowizards.spring.cache;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * <pre>
 * 
 * This class has a no-arg constructor so you can use it as a bean.
 * It is reusable, but not thread safe.
 * To see what kind of formats it can tokenize: {@link TokenizerCacheNameResolverTest}.
 * The purpose of this class is to tokenize a cashflush command, where values can be grouped
 * in names paces. Each name space could relate to a kind of cache flush hint {@link CacheFlushHint}.
 * There is also support for templates. See: {@link FlushNameTemplateBean}
 * </pre>
 * 
 * @author ebunders
 */
public class TokenizerCacheNameResolver implements CacheNameResolver {

    private Map<String, List<String>> namesForNamespace = null;
    private List<String> globalValues;
    private String input = null;

    private static final String REGEX_NAMESPACE = "^[\\w_]+:";
    private static final String REGEX_COMPOSITE = REGEX_NAMESPACE +  ".+$";

    public List<String> getNames(String nameSpace) {
        if (StringUtils.isEmpty(nameSpace)) {
            throw new IllegalStateException("attribute namespace is empty");
        }
        tokenizeIfNecessary();
        List<String> result = new ArrayList<String>();
        if (namesForNamespace.get(nameSpace) != null) {
            result.addAll(namesForNamespace.get(nameSpace));
        }
        result.addAll(globalValues);
        return result;
    }

    private void tokenizeIfNecessary() {
        if (namesForNamespace == null) {
            tokenize();
        }
    }

    public void setInput(String input) {
        reset();
        this.input = input;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     *@throws IllegalStateException
     *             when input is not set yet.
     */
    private void tokenize() {
        if (StringUtils.isEmpty(input)) {
            throw new IllegalStateException("set input first");
        }

        // init
        namesForNamespace = new HashMap<String, List<String>>();
        globalValues = new ArrayList<String>();

        List<String> parts = Arrays.asList(input.trim().split(" "));
        for (String part : parts) {
            part = part.trim();
            boolean partHasNamespace = false;
            // boolean matches = part.matches("^[\\w_]+:[\\w,]+");
            boolean matches = part.matches(REGEX_COMPOSITE);
            if (matches) {
                partHasNamespace = true;
                String nameSpace = part.substring(0, part.indexOf(":"));
                part = part.substring(part.indexOf(":") + 1);
                if (namesForNamespace.get(nameSpace) == null) {
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
