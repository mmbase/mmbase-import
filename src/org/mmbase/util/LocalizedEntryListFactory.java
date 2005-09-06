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
 * This Factory can produce Collections based on a Locale (The {@link #get} method is
 * essential). The other methods besides get are methods to define these lists. There are two ways
 * to add entries to the produced collections. The first one is to explicitely add them, using the
 * {@link #add} method. This gives precise control, and the collections can have different orders
 * for different languages. The size of the collections are always the same, so if for a certain
 * locale less entries are added, these are completed with the unused keys. If for a certain locale
 * <em>no</em> entries are added, it will behave itself like as the default locale of {@link
 * LocalizedString#getDefault}. 
 *
 * It is also possible to add entired 'bundles'. For this use {@link addBundle}. When a Collection instance
 * for a certain Locale is requested these informations are used to call {@link
 * SortedBundle#getResource}.
 *
 * It is possible to mix both methods, so having an enumeration partially defined by a bundle,
 * partially by explicit values, though this is not recommended.
 *
 * @author Michiel Meeuwissen
 * @version $Id: LocalizedEntryListFactory.java,v 1.1 2005-09-06 21:09:39 michiel Exp $
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

    //public static SortedMap getResource(String baseName, Locale locale, ClassLoader loader, Class constantsProvider, Class wrapper, Comparator comparator) {
    public LocalizedEntryListFactory() {

    }

    public Entry add(Locale locale, String key, Object value) {
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
    public void addBundle(String baseName, ClassLoader classLoader, Class constantsProvider, Class wrapper, Comparator comparator) {
        // just for the count
        Bundle b = new Bundle(baseName, classLoader, constantsProvider, wrapper, comparator);
        bundles.add(b);
        Iterator i = localized.values().iterator();
        while(i.hasNext()) {
            List localizedList = (List) i.next();
            localizedList.add(b);
        }
        bundleSize += b.get(null).size();
    }
    public Collection /* <Entry> */ get(final Locale locale) {
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
                                        subIterator = ((Bundle) res).get(locale).entrySet().iterator();
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
    public int size() {
        return size + bundleSize;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return this;
        }
    }

    private static class Bundle {
        Bundle(String r, ClassLoader cl, Class cp, Class w, Comparator comp) {
            resource = r; classLoader = cl; constantsProvider = cp ; wrapper = w; comparator = comp;
        }
        public String      resource;
        public ClassLoader classLoader;
        public Class       constantsProvider;
        public Class         wrapper;
        public Comparator  comparator;
        SortedMap get(Locale loc) {
            return  SortedBundle.getResource(resource, loc, classLoader, constantsProvider, wrapper, comparator);
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
        fact.addBundle(resource1, null, null, Boolean.class, null);
        fact.add(nl, "c", "doegg");
        fact.addBundle(resource2, null, null, String.class, null);

        System.out.println("size: " + fact.size());
        System.out.println("" + fact.get(en));
        System.out.println("" + fact.get(nl));
        System.out.println("" + fact.get(dk));
    }

}
