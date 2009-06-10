/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.core.event.*;
import org.mmbase.util.*;
import org.mmbase.storage.search.*;
import java.util.concurrent.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * This processor can be used as a 'set' processor on keyword fields. It maintains then a count on
 * all keywords. These counts are aquirable via the static method {@link #getKeywords} (and via a
 * function 'keywords' on the 'utils' set). Two properties can be set on this
 * processor. The first one is the 'repository' which is a key with with to store the Map with the
 * counts. This ensures that you can use this processor for different 'clouds' of keywords. The
 * second one is the 'field', which is a String of the form &lt;builder name&gt;.&lt;field
 * name&gt;. This information cannot be aquired auomaticly because a field knows about it's
 * datatype, but not inversely. It is essential to use this last property.
 *
 * <pre><![CDATA<
    <field name="keywords">
      <gui>
        <guiname xml:lang="nl">Sleutelwoorden</guiname>
        <guiname xml:lang="en">Keywords</guiname>
      </gui>
      <datatype base="line" xmlns="http://www.mmbase.org/xmlns/datatypes">
        <maxLength value="255" />
        <setprocessor>
          <class name="org.mmbase.datatypes.processors.KeywordsProcessor">
            <param name="repository" value="episodes" />
            <param name="field" value="episodes.keywords" />
          </class>
        </setprocessor>
      </datatype>
    </field>
 >]></pre>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public class KeywordsProcessor implements Processor, NodeEventListener {

    private static final Logger log = Logging.getLoggerInstance(KeywordsProcessor.class);

    private static final long serialVersionUID = 1L;

    private static Map<String, Map<String, Integer>> repositories = new ConcurrentHashMap<String, Map<String, Integer>>();


    /**
     * Returns all keywords in a certain repository, as an unmodifiable SortedSet of Map.Entry's. The most occuring on
     * on top.
     */
    public static SortedSet<Map.Entry<String, Integer>> getKeywords(String repository) {
        Set<Map.Entry<String, Integer>> set = repositories.get(repository).entrySet();
        SortedSet<Map.Entry<String, Integer>> sorted = new TreeSet<Map.Entry<String, Integer>>(new EntryComparator());
        sorted.addAll(set);
        return Collections.unmodifiableSortedSet(sorted);
    }



    private String repository = "keywords";
    private String field = null;
    private String builder = null;
    /**
     * Sets the 'repository' in which this processor will store its keyword counts. This defaults to 'keywords'.
     */
    public void setRepository(String r) {
        repository = r;
    }

    protected  static Map<String, Integer> getRepository(String rep) {
        if (rep == null || "".equals(rep)) rep = "keywords";
        Map<String, Integer> keywords = repositories.get(rep);
        if (keywords == null) {
            keywords = new ConcurrentHashMap<String, Integer>();
            repositories.put(rep, keywords);
        }
        return keywords;
    }
    /**
     * Sets the 'field'  on which this keywords processor is working.
     */
    public void setField(final String f) {
        if (field == null) {
            ThreadPools.jobsExecutor.execute(new Runnable() {
                    public void run() {
                        getRepository(repository);
                        log.service("Filling keyword repository for " + f);
                        String[] array = f.split("\\.");
                        builder = array[0];
                        field   = array[1];
                        EventManager.getInstance().addEventListener(KeywordsProcessor.this);
                        ContextProvider.getDefaultCloudContext().assertUp();
                        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
                        NodeManager nm = cloud.getNodeManager(builder);
                        NodeQuery q = nm.createQuery();
                        Constraint c = q.createConstraint(q.createStepField(field));
                        q.setInverse(c, true);
                        q.setConstraint(c);
                        NodeIterator ni = new HugeNodeListIterator(q);
                        long i = 0;
                        while(ni.hasNext()) {
                            Node n = ni.nextNode();
                            String keywords = n.getStringValue(field);
                            i++;
                            if (i % 1000 == 0) {
                                log.service("Found keywords " + getKeywords(repository));
                            }
                            if (! "".equals(keywords)) {
                                addKeywords(repository, keywords.toLowerCase());
                            }
                        }
                        log.info("Ready " + getKeywords(repository));
                    }
                }
                );
        }
    }

    protected static void addKeywords(String repository, String k) {
        Map<String, Integer> keywords = getRepository(repository);
        for (String keyword : k.split(",")) {
            keyword = keyword.trim().toLowerCase();
            if (! "".equals(keyword)) {
                Integer i = keywords.get(keyword);
                if (i == null) {
                    i = 1;
                } else {
                    i++;
                }
                keywords.put(keyword, i);
            }
        }
    }
    protected static void removeKeywords(String repository, String k) {
        Map<String, Integer> keywords = getRepository(repository);
        for (String keyword : k.split(",")) {
            keyword = keyword.trim().toLowerCase();
            if (! "".equals(keyword)) {
                Integer i = keywords.get(keyword);
                if (i == null) {
                    i = 0;
                } else {
                    i--;
                }
                keywords.put(keyword, i);
            }
        }
    }


    protected void change(String oldValues, String newValues) {
        log.debug("Changing keywords from " + oldValues + " to " + newValues);
        if (oldValues != null) removeKeywords(repository, oldValues);
        addKeywords(repository, newValues);
        if (log.isDebugEnabled()) {
            log.debug("Keywords now: " + getKeywords(repository));
        }
    }

    public Object process(Node node, Field field, Object value) {
        if (field == null) throw new IllegalStateException("Field property should have been set");
        String oldValues = node.getStringValue(field.getName());
        String newValues = Casting.toString(value);
        change(node.isNew() ? null : oldValues, newValues);
        return value;
    }

    public void notify(NodeEvent event) {
        if (! event.isLocal()) {
            if (event.getBuilderName().equals(builder)) {
                change(Casting.toString(event.getOldValue(field)),
                       Casting.toString(event.getNewValue(field)));
            } else {
                log.debug("ignoring event while of wrong type" + event);
            }
        } else {
            log.debug("ignoring local event " + event);
        }
    }


    static class EntryComparator implements Comparator<Map.Entry<String, Integer>> {
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            int res = o2.getValue() - o1.getValue();
            if (res != 0) return res;
            return o2.getKey().compareTo(o1.getKey());
        }
        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof EntryComparator;
        }

    }

}


