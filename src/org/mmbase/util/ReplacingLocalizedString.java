/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Extends and wraps LocalizedString. It extends to look like a 'normal' LocalizedString, but it
 * overrides 'get' to do token-replacements first.
 * 
 * This functionality is not in LocalizedString itself, because now you can have different
 * replacements on the same value set represented by a LocalizedString withouth having to copy
 * everything every time.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class ReplacingLocalizedString extends LocalizedString {
    
    private static final Logger log = Logging.getLoggerInstance(ReplacingLocalizedString.class);


    private LocalizedString wrapped;
    private List replacements = new ArrayList();

    
    // just for the contract of Serializable
    protected ReplacingLocalizedString() {

    }

    /**
     * @param s The wrapped LocalizedString.
     */
    public ReplacingLocalizedString(LocalizedString s) {
        if (s == null) s = new LocalizedString("NULL");
        wrapped = s;
    }
    
    public void replaceAll(String regexp, String replacement) {
        replacements.add(new Entry(regexp, replacement));
    }

    protected String replace(String input) {
        String output = input;
        Iterator i = replacements.iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            try {
                output = output.replaceAll((String) entry.getKey(), (String) entry.getValue());
            } catch (Throwable t) {
                log.warn("Could not replace " + entry + " in " + input + " because " + t);
            }
        }
        return output;
    }

    //javadoc inherited
    public String getKey() {
        return wrapped.getKey();
    }

    //javadoc inherited
    public void setKey(String key) {
        wrapped.setKey(key);
    }

    // javadoc inherited
    public String get(Locale locale) {
        return replace(wrapped.get(locale));
    }

    //javadoc inherited
    public void set(String value, Locale locale) {
        wrapped.set(value, locale);
    }

    /**
     * {@inheritDoc}
     *
     * Also takes into account the replacements in the values (but only 'lazily', when actually requested).
     */
    public Map asMap() {
        final Map map = super.asMap();
        return new AbstractMap() {
                public Set entrySet() {
                    return new AbstractSet() {
                            public int size() {
                                return map.size();
                            }
                            public Iterator iterator() {
                                final Iterator it = map.entrySet().iterator();
                                return new Iterator() {
                                        public boolean hasNext() {
                                            return it.hasNext();
                                        }
                                        public Object next() {
                                            final Map.Entry value = (Map.Entry) it.next();
                                            return new Map.Entry() {
                                                    public Object getKey() {
                                                        return value.getKey();
                                                    }
                                                    public Object getValue() {
                                                        return replace((String) value.getValue());
                                                    }
                                                    public Object setValue(Object v) {
                                                        throw new UnsupportedOperationException(); // map is umodifiable
                                                    }
                                                };
                                        }
                                        public void remove() {
                                            throw new UnsupportedOperationException(); // map is umodifiable
                                        }
                                    };
                            }
                        };
                }
            };
    }

    // javadoc inherited
    public void setBundle(String b) {
        wrapped.setBundle(b);
    }

    public String toString() {
        return "replacing-" + wrapped.toString();
    }

    public Object clone() {
        ReplacingLocalizedString clone = (ReplacingLocalizedString) super.clone();
        clone.replacements = (List)((ArrayList)replacements).clone();
        return clone;

    }
    /**
     * Utility method for second argument of replaceAll
     */
    public static String makeLiteral(String s) {
        // sometimes, implementing java looks rather idiotic, but honestely, this is correct!
        s =  s.replaceAll("\\\\", "\\\\\\\\"); 
        return s.replaceAll("\\$", "\\\\\\$");
    }


    public static void main(String argv[]) {
        ReplacingLocalizedString s = new ReplacingLocalizedString(new LocalizedString("abcd"));
        s.replaceAll("b", makeLiteral(argv[0]));
        System.out.println(s.get(null));
    }

}
