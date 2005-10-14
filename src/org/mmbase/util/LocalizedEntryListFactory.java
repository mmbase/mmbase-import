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
 * These factories can produce Collections based on a Locale (The {@link #get} method is
 * essential). The other methods besides get are methods to define these lists. There are two ways
 * to add entries to the produced collections. The first one is to explicitely add them, using the
 * {@link #add} method. This gives precise control, and the collections can have different orders
 * for different languages. The size of the collections are always the same, so if for a certain
 * locale less entries are added, these are completed with the unused keys. If for a certain locale
 * <em>no</em> entries are added, it will behave itself like as the default locale of {@link
 * LocalizedString#getDefault}.
 *
 * It is also possible to add entire 'bundles'. For this use {@link #addBundle}. When a Collection instance
 * for a certain Locale is requested these informations are used to call {@link
 * SortedBundle#getResource}.
 *
 * It is possible to mix both methods, so having an enumeration partially defined by a bundle,
 * partially by explicit values, though this is not recommended.
 *
 * @author Michiel Meeuwissen
 * @version $Id: LocalizedEntryListFactory.java,v 1.5 2005-10-14 18:35:11 michiel Exp $
 * @since MMBase-1.8
 */
public class LocalizedEntryListFactory implements java.io.Serializable, Cloneable {

    private static final Logger log = Logging.getLoggerInstance(LocalizedEntryListFactory.class);
    private int size = 0;
    private int bundleSize = 0;
    private List bundles = new ArrayList(); // contains Bundles
    private Map localized = new HashMap(); //Locale -> List of Entries and Bundles
    private List fallBack = new ArrayList();
    private Map unusedKeys = new HashMap(); // Locale -> unused Keys;

    public LocalizedEntryListFactory() {

    }

    /**
     * Ass a value for a certain key and Locale
     * @return The create Map.Entry.
     */
    public Map.Entry add(Locale locale, Object key, Object value) {
        List localizedList = (List) localized.get(locale);
        List unused = (List) unusedKeys.get(locale);
        if (localizedList == null) {
            localizedList = new ArrayList();
            localizedList.addAll(bundles);
            localized.put(locale, localizedList);
            unused = new ArrayList();
            unused.addAll(fallBack);
            unusedKeys.put(locale, unused);
        }
        Entry entry = new Entry(key, value);
        localizedList.add(entry);
        if (! fallBack.contains(key)) {
            size++;
            fallBack.add(key);
            Iterator i = unusedKeys.values().iterator();
            while (i.hasNext()) {
                List uu = (List) i.next();
                uu.add(key);
            }
        }
        unused.remove(key);
        return entry;
    }

    /**
     * Adds a bundle, to the (current) end of all maintained collections. Actually, only the
     * definition of the bundle is added, it is instantiated only later, when requested for a
     * specific locale.
     */
    public void addBundle(String baseName, ClassLoader classLoader, Class constantsProvider, Class wrapper, Comparator comparator) {
        // just for the count
        Bundle b = new Bundle(baseName, classLoader, constantsProvider, wrapper, comparator);
        bundleSize += b.get(null).size();
        bundles.add(b);
        Iterator i = localized.values().iterator();
        while(i.hasNext()) {
            List localizedList = (List) i.next();
            localizedList.add(b);
        }
    }
    /**
     * Returns a Collection of Map.Entries for the given Locale. The collection is kind of 'virtual',
     * it only reflects the underlying memory structures.
     * @param locale The locale of <code>null</code> for the default locale.
     */
    public Collection /* <Map.Entry> */ get(final Locale locale) {
        return new AbstractCollection() {
                public int size() {
                    return LocalizedEntryListFactory.this.size();
                }
                public Iterator iterator() {
                    return new Iterator() {
                            private List loc = (List) localized.get(locale);
                            private List  uu  = (List) unusedKeys.get(locale);
                            private Iterator iterator;
                            private Iterator subIterator;
                            private boolean fallenBack = false;
                            {
                                if (loc == null) {
                                   loc = (List) localized.get(LocalizedString.getDefault());
                                    uu  = (List) unusedKeys.get(LocalizedString.getDefault());
                                }
                                if (loc == null) loc = bundles;
                                iterator = loc.iterator();
                                if (uu == null) uu = fallBack;
                                if (! iterator.hasNext()) {
                                    iterator = uu.iterator();
                                    fallenBack = true;
                                }
                            }
                            public boolean hasNext() {
                                return iterator.hasNext() || (subIterator != null && subIterator.hasNext()) || (! fallenBack && uu.size() > 0);

                            }
                            public Object next() {
                                Object res;
                                if (subIterator != null) {
                                    res = subIterator.next();
                                    if (! subIterator.hasNext()) {
                                        subIterator = null;
                                    }
                                } else {
                                    res = iterator.next();
                                    if (fallenBack) {
                                        res = new Entry(res, res);
                                    }
                                    if (res instanceof Bundle) {
                                        subIterator = ((Bundle) res).get(locale).iterator();
                                        res = subIterator.next();
                                    }
                                }

                                if (subIterator == null && !fallenBack && !iterator.hasNext()) {
                                    iterator = uu.iterator();
                                    fallenBack = true;
                                }
                                return res;
                            }
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                }
            };
    }
    /**
     * The size of the collections returned by {@link #get}
     */
    public int size() {
        return size + bundleSize;
    }

    /**
     * Since keys may be somehow wrapped, you can also 'unwrap' by this. If e.g. a constants
     * provider was used, that values can be indicated by the name of the constants and this method
     * casts to the value.
     */
    public Object castKey(final Object key) {
        String string = Casting.toString(key);
        Iterator i = bundles.iterator();
        while (i.hasNext()) {
            Bundle b = (Bundle) i.next();
            Object nk = SortedBundle.castKey(string, null, b.constantsProvider, b.wrapper);
            if (string != nk) return nk;
        }
        return key;

    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return this;
        }
    }

    public String toString() {
        return "" + get(null);
    }

    private static class Bundle {
        Bundle(String r, ClassLoader cl, Class cp, Class w, Comparator comp) {
            resource = r; classLoader = cl; constantsProvider = cp ; wrapper = w; comparator = comp;
        }
        private String      resource;
        private ClassLoader classLoader;
        private Class       constantsProvider;
        private Class       wrapper;
        private Comparator  comparator;
        /**
         * Collection of Map.Entry's
         */
        Collection get(Locale loc) {
            return  SortedBundle.getResource(resource, loc, classLoader, constantsProvider, wrapper, comparator).entrySet();
        }
    }

    /**
     * For testing only.
     */
    public static void main(String argv[]) {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.bridge.util.fields.resources.boolean.onoff";
        String resource2 = "org.mmbase.bridge.util.fields.resources.boolean.yesno";
        Locale nl = new Locale("nl");
        Locale en = new Locale("en");
        Locale dk = new Locale("dk");
        fact.add(nl, "a", "hallo");
        fact.add(nl, "b", "daag");
        fact.add(en, "b", "hello");
        fact.add(en, "a", "good bye");
        try {
            fact.addBundle(resource1, null, SortedBundle.NO_CONSTANTSPROVIDER, Boolean.class, SortedBundle.NO_COMPARATOR);
        } catch (Exception e) {
            System.out.println("" + e.getMessage() + ", probably using java 1.4");
        }
        fact.add(nl, "c", "doegg");
        fact.addBundle(resource2, null, SortedBundle.NO_CONSTANTSPROVIDER, String.class, SortedBundle.NO_COMPARATOR);

        System.out.println("size: " + fact.size());
        System.out.println("" + fact.get(en));
        System.out.println("" + fact.get(nl));
        System.out.println("" + fact.get(dk));

        LocalizedEntryListFactory fact2 = new LocalizedEntryListFactory();
        fact2.addBundle("org.mmbase.module.builders.resources.states", null, org.mmbase.module.builders.MMServers.class, SortedBundle.NO_WRAPPER, SortedBundle.NO_COMPARATOR);
        
        System.out.println("size: " + fact2.size());
        System.out.println("" + fact2.get(en));
        System.out.println("" + fact2.get(nl));
        Object error = fact2.castKey("ERROR");
        System.out.println("ERROR=" + error.getClass().getName() + " " + error);

        
    }

}
