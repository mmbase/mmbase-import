/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.*;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.*;
import java.io.File;
import java.io.Reader;
import java.io.IOException;

/**
 * This is the main class for the filter process. It maintains list of
 * Filters (which content can be configured by the 'filters.xml'
 * configuration file. It does not do any filtering itself, it is only
 * the access to the actual filters, so filtering is completably
 * configurable.
 *
 * Since there can be only one 'main' filter this class is a
 * Singleton, and its one instance can be gotten by the getInstance
 * function (and this is done by the Media builders when they need url
 * representations to the stream they describe).
 *
 *
 * @author Rob Vermeulen (VPRO)
 * @author Michiel Meeuwissen
 */
public class MainFilter {
    private static Logger log = Logging.getLoggerInstance(MainFilter.class);

    public static final String MAIN_TAG          = "mainFilter";
    public static final String FILTERCONFIGS_TAG = "filterConfigs";
    public static final String FILTERCONFIG_TAG  = "config";
    public static final String CHAIN_TAG         = "chain";
    public static final String FILTER_TAG        = "filter";
    public static final String FILTER_ATT        = "filter";
    public static final String ID_ATT            = "id";
    //don't use File.separator here to replace the forward slash. It creates an error on windows
    public static final String CONFIG_FILE       = "media/filters.xml";
        
    private ResourceWatcher configWatcher = new ResourceWatcher() {
        public void onChange(String res) {
                    try {
		            Reader reader = getResourceLoader().getReader(res);
		            readConfiguration(reader);
                    } catch (IOException ie) {
                        log.warn("Can't load "+CONFIG_FILE);
                        log.warn("Message "+ie.getMessage());
                    }
        }
    };

    
    private List filters = new ArrayList();

    /**
     * Construct the MainFilter
     */
    private MainFilter() {
        configWatcher.add(CONFIG_FILE);
        configWatcher.onChange();
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();
    }

    
    private static MainFilter filter = null;

    public static MainFilter getInstance() {
        if (filter == null) filter = new MainFilter();
        return filter;
    }

    
    /**
     * read the MainFilter configuration
     */
    private synchronized void readConfiguration(Reader configreader) {
        if (log.isServiceEnabled()) {
            log.service("Reading " + CONFIG_FILE);
        }
        filters.clear();

        DocumentReader reader = new XMLBasicReader(new InputSource(configreader), getClass());
        Element filterConfigs = reader.getElementByPath(MAIN_TAG + "." + FILTERCONFIGS_TAG);

        ChainSorter chainComp = new ChainSorter();
        // When chaining 'comparators' then they are combined to one comparator
        // Then only one 'sort' has to be done, which is more efficient.

        for(Iterator e = reader.getChildElements(MAIN_TAG + "." + CHAIN_TAG, FILTER_TAG); e.hasNext();) {
            Element chainElement =(Element)e.next();
            String  clazz        = reader.getElementValue(chainElement);
            String  elementId    = chainElement.getAttribute(ID_ATT);
            try {
                Class newclass = Class.forName(clazz);
                Filter filter = (Filter) newclass.newInstance();
                if (filter instanceof Sorter) {
                    chainComp.add((Sorter) filter);
                } else {
                    filters.add(filter);
                }
                log.service("Added filter " + clazz + "(id=" + elementId + ")");
                if (elementId != null && ! "".equals(elementId)) {                    
                    // find right configuration
                    // not all filters necessarily have there own configuration
                    boolean found = false;
                    
                    for (Iterator configIter = reader.getChildElements(filterConfigs, FILTERCONFIG_TAG); configIter.hasNext();) {
                        Element config = (Element) configIter.next();
                        String filterAtt = reader.getElementAttributeValue(config, FILTER_ATT);
                        if (filterAtt.equals(elementId)) {
                            log.service("Configuring " + elementId);
                            filter.configure(reader, config);
                            found = true;
                            break;
                        }
                    }
                    if (! found) log.debug("No configuration found for filter " + elementId);
                }
            } catch (ClassNotFoundException ex) {
                log.error("Cannot load filter " + clazz + "\n" + ex);
            } catch (InstantiationException ex1) {
                log.error("Cannot load filter " + clazz + "\n" + ex1);
            } catch (IllegalAccessException ex2) {
                log.error("Cannot load filter " + clazz + "\n" + ex2);
            }                   
        }
        if (chainComp.size() > 0) {
            filters.add(chainComp); // make sure it is at least empty
        }
    }

    /**
     * Perform the actual filter task
     */

    public List filter(List urls) {
        Iterator i = filters.iterator();
        while (i.hasNext()) {
            Filter filter = (Filter) i.next();
            if (log.isDebugEnabled()) {
                log.debug("Using filter " + filter);
                log.debug("before: " + urls);
            }
  
            try {         
                urls = filter.filter(urls);
            } catch (Exception filterException) {
		log.error("Check filter "+filter+" "+filterException);
            }
            if (log.isDebugEnabled()) {
                log.debug("after: " + urls);
            }
        }
        return urls;
    }


    // ====================================================================================
    // Test-code

    private static void addTestData(Collection c) {
        c.add("hoi");
        c.add("hallo");
        c.add("heeej");
        c.add("hello");
        c.add("daag");
        c.add("ajuus");
        c.add("saluton");
        c.add("cao");
        c.add("arriverderci");
        c.add("gxis");
        c.add("hej");
        c.add("komop");
        c.add("1234");
    }
    private static class TestComparator implements Comparator {
        private int i;
        TestComparator(int i) {
            this.i = i;
        }
        public int compare(Object o1, Object o2) {
            return o1.hashCode() - o2.hashCode();
        }
        
    }

    public static void main(String[] args) {
        // what is quicker: sorting a list, or creating a new sortedset:
        final int ITERATIONS = 200000;

        List list = new ArrayList();
        addTestData(list);
        long start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            Comparator c = new TestComparator(i);
            Collections.sort(list, c);
        }
        log.debug("list duration: " + (System.currentTimeMillis() - start));
        log.debug(list);
        SortedSet  sortedSet   = new TreeSet();
        addTestData(sortedSet);
        start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            SortedSet s = new TreeSet(new TestComparator(i));
            s.addAll(sortedSet);
            sortedSet = s;
        }
        log.debug("sortedset duration: " + (System.currentTimeMillis() - start));
        log.debug(sortedSet);
    }

    
}
