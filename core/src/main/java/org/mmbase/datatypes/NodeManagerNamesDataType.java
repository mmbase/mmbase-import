/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A StringDataType with can take all possible node manager as values.
 * The default value can be indicated as a comma-seperated string, in which case the first existing
 * nodemanager will be used as the actual node manager.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class NodeManagerNamesDataType extends StringDataType {

    private static final Logger log = Logging.getLoggerInstance(NodeManagerNamesDataType.class);

    private static final long serialVersionUID = 1L;

    public NodeManagerNamesDataType(String name) {
        super(name);
    }


    /*
    protected String preCast(String value, Cloud cloud, Node node, Field field) {
        if (value == null) return null;
        D preCast =  enumerationRestriction.preCast(value, cloud);
        return preCast;
    }
    */

    @Override public String getDefaultValue(Locale locale, Cloud cloud, Field field) {
        if (defaultValue != null) {
            for (String def : Casting.toString(defaultValue).split("\\s*,\\s*")) {
                log.debug("Considering " + def);
                if (cloud.hasNodeManager(def)) return def;
            }
        }
        return null;
    }

    @Override public Iterator<Map.Entry<String, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        if (node == null && cloud == null) return null; // we don't know..
        return new Iterator<Map.Entry<String, String>>() {
            List<NodeManager> list = node == null ? cloud.getNodeManagers() : node.getCloud().getNodeManagers();
            Iterator<NodeManager> iterator = list.iterator();
            NodeManager next = findNext();

            protected NodeManager findNext() {
                while (iterator.hasNext()) {
                    NodeManager prop = iterator.next();
                    if (NodeManagerNamesDataType.this.validate(prop.getName(), node, field).size() == 0) {
                        return prop;
                    }
                }
                return null;
            }
            public boolean hasNext() {
                return next != null;
            }
            public Map.Entry<String, String> next() {
                NodeManager prev = next;
                next = findNext();
                return new Entry(prev.getName(), prev.getGUIName(NodeManager.GUI_PLURAL, locale));
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


}
