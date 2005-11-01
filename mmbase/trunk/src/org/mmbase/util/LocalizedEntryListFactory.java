/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.xml.query.QueryReader;
import org.mmbase.util.xml.DocumentSerializable;
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
 * @version $Id: LocalizedEntryListFactory.java,v 1.12 2005-11-01 23:39:30 michiel Exp $
 * @since MMBase-1.8
 */
public class LocalizedEntryListFactory implements Serializable, Cloneable {

    private static final Logger log = Logging.getLoggerInstance(LocalizedEntryListFactory.class);
    private static final int serialVersionUID = 1; // increase this if object serialization changes (which we shouldn't do!)

    private int size = 0; // number of explicitely added keys

    // we don't use interfaces here, because of Serializability
    private HashMap localized = new HashMap();   //Locale -> List of Entries, Bundles and DocumentSerializable's
    private HashMap unusedKeys = new HashMap();  // Locale -> unused Keys;

    private ArrayList bundles = new ArrayList(); // contains all Bundles
    private ArrayList fallBack = new ArrayList(); // List of known keys, used as fallback, notheing defind for locale


    public LocalizedEntryListFactory() {
        
    }

    /**
     * Adds a value for a certain key and Locale
     * @return The created Map.Entry.
     */
    public Map.Entry add(Locale locale, Serializable key, Serializable value) {
        Entry entry = new Entry(key, value);
        List unused = add(locale, entry);

        if (! fallBack.contains(key)) {
            size++;
            fallBack.add(key);
            Iterator i = unusedKeys.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                List uu = (List) e.getValue();
                if (!e.getKey().equals(locale)) {
                    uu.add(key);
                }
            }
        }
        unused.remove(key);
        return entry;
    }
    
    /**
     * Add entry to 'localized' 
     * @param object the object, which has not Locale suppoft of itself (Entry, DocumentSerializable)
     * @param locale Can be <code>null</code> too, in which case default locale is used
     * @return List of currently unused keys for this locale.
     */

    protected List add(Locale locale, Object entry) {
        if (locale == null) locale = LocalizedString.getDefault();
        List localizedList = (List) localized.get(locale);
        List unused        = (List) unusedKeys.get(locale);
        if (localizedList == null) {
            localizedList = new ArrayList();
            localizedList.addAll(bundles);
            localized.put(locale, localizedList);
            unused = new ArrayList();
            unused.addAll(fallBack);
            unusedKeys.put(locale, unused);
        }

        localizedList.add(entry);
        return unused;
    }

    /**
     * Adds a bundle, to the (current) end of all maintained collections. Actually, only the
     * definition of the bundle is added, it is instantiated only later, when requested for a
     * specific locale.
     */
    public void addBundle(String baseName, ClassLoader classLoader, Class constantsProvider, Class wrapper, Comparator comparator) {
        // just for the count
        Bundle b = new Bundle(baseName, classLoader, constantsProvider, wrapper, comparator);
        if (bundles.contains(b)) {
            log.info("Adding bundle " + b + " for second time in " + b + ", because " + Logging.stackTrace());
        }
        bundles.add(b);
        Iterator i = localized.values().iterator();
        while(i.hasNext()) {
            List localizedList = (List) i.next();
            localizedList.add(b);
        }
    }
    
    /**
     * XXX locale is ignored
     */
    public void addQuery(Locale locale, Document queryElement) {
        DocumentSerializable doc = new DocumentSerializable(queryElement);
        add(locale, doc);
        log.info("NOW:" + this);
    }

   
    /**
     * Defaulting version of {@link #get(Locale, Cloud)}. Using default anonymous cloud.
     */
    public Collection get(final Locale locale) {
        Cloud cloud = null;
        try {
            cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
            cloud.setLocale(locale);
        } catch (Exception e) {
            // could find no cloud whatsoever. Trying without one.
        }
        return get(locale, cloud);
    }
    /**
     * Returns a Collection of Map.Entries for the given Locale. The collection is kind of 'virtual',
     * it only reflects the underlying memory structures.
     * @param locale The locale of <code>null</code> for the default locale.
     * @param cloud  The cloud to use. Can be <code>null</code> if no queries added (see {@link #addQuery}).
     *               If Locale is <code>null</code>, but cloud isn't, the locale of the cloud is used.
     */
    public Collection /* <Map.Entry> */ get(final Locale locale, final Cloud cloud) {
        return new AbstractCollection() {
                public int size() {
                    return LocalizedEntryListFactory.this.size(cloud);
                }
                public Iterator iterator() {
                    return new Iterator() {
                            Locale useLocale = locale;
                            {
                                if (useLocale == null) useLocale = cloud.getLocale();
                            }
                            private ChainedIterator iterator = new ChainedIterator();
                            private Iterator subIterator = null;
                            private Map.Entry next = null;

                            {
                                List loc = (List) localized.get(useLocale);
                                List  uu  = (List) unusedKeys.get(useLocale);
                                if (loc == null) {
                                    loc = (List) localized.get(LocalizedString.getDefault());
                                    uu  = (List) unusedKeys.get(LocalizedString.getDefault());
                                }
                                
                                if (loc == null) {
                                    loc = bundles;
                                    assert(uu == null);
                                    uu = fallBack;
                                }                                    
                                iterator.addIterator(loc.iterator());
                                iterator.addIterator(uu.iterator());
                                findNext();
                            }
                            protected void findNext() {
                                next = null;
                                while(next == null && iterator.hasNext()) {
                                    Object candidate = iterator.next();
                                    if (candidate instanceof String) {
                                        next = new Entry(candidate, candidate);
                                    } else if (candidate instanceof Map.Entry) {
                                        next = (Map.Entry) candidate;
                                    } else if (candidate instanceof Bundle) {
                                        subIterator = ((Bundle) candidate).get(useLocale).iterator(); 
                                        if (subIterator.hasNext()) {
                                            break;
                                        } else {
                                            subIterator = null;
                                        }
                                    } else if (candidate instanceof DocumentSerializable) {
                                        Element element = ((DocumentSerializable) candidate).getDocument().getDocumentElement();
                                        try {
                                            final Query query = QueryReader.parseQuery(element, cloud, null).query;
                                            log.info("Executing query " + query);
                                            subIterator = new Iterator() {
                                                    final NodeIterator nodeIterator = cloud.getList(query).nodeIterator();
                                                    public boolean hasNext() {
                                                        return nodeIterator.hasNext();
                                                    }
                                                    public Object next() {
                                                        org.mmbase.bridge.Node next = nodeIterator.nextNode();
                                                        return new Entry(next, next.getFunctionValue("gui", null));
                                                    }
                                                    public void remove() {
                                                        throw new UnsupportedOperationException();
                                                    }
                                                };
                                            if (subIterator.hasNext()) {
                                                break;
                                            } else {
                                                subIterator = null;
                                            }
                                        } catch (Exception e) {
                                            log.error(e);
                                        }
                                    }
                                }
                            }

                            public boolean hasNext() {
                                return next != null || subIterator != null;
                            }
                            public Object next() {
                                Map.Entry res;
                                if (subIterator != null) {
                                    res = (Map.Entry) subIterator.next();
                                    if (!subIterator.hasNext()) {
                                        subIterator = null;
                                        findNext();
                                    }
                                } else {
                                    res = next;
                                    findNext();
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
    public int size(Cloud cloud) {
        int queriesSize = 0;
        Locale locale = cloud.getLocale();
        List localizedList = (List) localized.get(locale);
        if (localizedList == null) {
            locale = LocalizedString.getDefault();
            localizedList = (List) localized.get(locale);
        }
        if (localizedList != null) {
            Iterator i = localizedList.iterator();
            while (i.hasNext()) {   
                Object o = i.next();
                if (o instanceof Bundle) {
                    queriesSize += ((Bundle) o).get(locale).size();
                } else if (o instanceof DocumentSerializable) {
                    Element element = ((DocumentSerializable) o).getDocument().getDocumentElement();
                    try {
                        queriesSize += Queries.count(QueryReader.parseQuery(element, cloud, null).query);
                    } catch (Exception e) {
                        log.warn(e);
                    }
                }
            }
        }
        
        return size + queriesSize;
    }

    public int size() {
        int bundleSize = 0;
        Iterator i = bundles.iterator();
        while (i.hasNext()) {
            Bundle b = (Bundle) i.next();
            try {
                bundleSize += b.get(null).size();
            } catch (MissingResourceException mre) {
                log.error(mre);
            }
        }
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
            LocalizedEntryListFactory clone = (LocalizedEntryListFactory) super.clone();
            clone.bundles   = (ArrayList) bundles.clone();
            clone.localized = (HashMap) localized.clone();
            clone.fallBack  = (ArrayList) fallBack.clone();
            clone.unusedKeys = (HashMap) unusedKeys.clone();
            return clone;
        } catch (Exception e) {
            log.error(e);
            return this;
        }
    }

    public String toString() {
        return "" + localized;
    }

    private static class Bundle implements Serializable {
        private static final int serialVersionUID = 1; // increase this if object serialization changes (which we shouldn't do!)
        
        private String      resource;
        private ClassLoader classLoader;
        private Class       constantsProvider;
        private Class       wrapper;
        private Comparator  comparator;

        // implementation of serializable
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeUTF(resource);
            // dont'r write class-loader, it is not serializable
            out.writeObject(constantsProvider);
            out.writeObject(wrapper);
            if (comparator instanceof Serializable) {
                out.writeObject(comparator);
            } else {
                out.writeObject((Comparator) null);
            }
        }
        // implementation of serializable
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            resource          = in.readUTF();
            classLoader       = getClass().getClassLoader();
            constantsProvider = (Class) in.readObject();
            wrapper           = (Class) in.readObject();
            comparator        = (Comparator) in.readObject();
        }
        
        
        
        Bundle(String r, ClassLoader cl, Class cp, Class w, Comparator comp) {
            resource = r; classLoader = cl; constantsProvider = cp ; wrapper = w; comparator = comp;
        }
        /**
         * Collection of Map.Entry's
         */
        Collection get(Locale loc) throws MissingResourceException {
            try {
                return  SortedBundle.getResource(resource, loc, classLoader, constantsProvider, wrapper, comparator).entrySet();
            } catch (IllegalArgumentException iae) {
                log.error(iae);
                return Collections.EMPTY_LIST;
            }
        }

        public String toString() {
            return resource + " " + constantsProvider + " " + wrapper + " " + comparator;
        }
        public boolean equals(Object o) {
            if (o instanceof Bundle) {
                Bundle b = (Bundle) o;
                return 
                    (resource == null ? b.resource == null : resource.equals(b.resource)) &&
                    (classLoader == null ? b.classLoader == null : classLoader.equals(b.classLoader)) &&
                    (constantsProvider == null ? b.constantsProvider == null : constantsProvider.equals(b.constantsProvider)) &&
                    (wrapper == null ? b.wrapper == null : wrapper.equals(b.wrapper)) &&
                    (comparator == null ? b.comparator == null : comparator.equals(b.comparator));
                    
            } else {
                return false;
            }
        }
        public int hashCode() {
            int result = 0;
            result = HashCodeUtil.hashCode(result, resource);
            result = HashCodeUtil.hashCode(result, classLoader);
            result = HashCodeUtil.hashCode(result, constantsProvider);
            result = HashCodeUtil.hashCode(result, wrapper);
            result = HashCodeUtil.hashCode(result, comparator);
            return result;
        }
    }

    /**
     * For testing only.
     */
    public static void main(String argv[]) {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.onoff";
        String resource2 = "org.mmbase.datatypes.resources.boolean.yesno";
        Locale nl = new Locale("nl");
        Locale en = new Locale("en");
        Locale dk = new Locale("dk");
        fact.add(nl, "a", "hallo");
        fact.add(nl, "b", "daag");
        fact.add(en, "b", "hello");
        fact.add(en, "a", "good bye");
        fact.addBundle(resource1, null, SortedBundle.NO_CONSTANTSPROVIDER, Boolean.class, SortedBundle.NO_COMPARATOR);
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
