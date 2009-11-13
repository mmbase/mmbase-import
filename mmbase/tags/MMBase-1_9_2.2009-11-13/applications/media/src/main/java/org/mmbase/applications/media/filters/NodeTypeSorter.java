 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;

import org.mmbase.module.core.MMObjectBuilder;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 */
public class NodeTypeSorter extends  PreferenceSorter {
    private static final Logger log = Logging.getLoggerInstance(NodeTypeSorter.class);

    private final List<String> nodeTypes = new ArrayList<String>();


    public  NodeTypeSorter() {
    }

    public void setList(String nt) {
        nodeTypes.addAll(org.mmbase.util.StringSplitter.split(nt));
        Collections.reverse(nodeTypes);
        log.service("Found list " + nodeTypes);
    }

    protected int getPreference(MMObjectBuilder buil) {
        return nodeTypes.indexOf(buil.getTableName());
    }

    @Override
    protected int getPreference(URLComposer ri) {
        MMObjectBuilder buil = ri.getSource().getBuilder();
        int p = getPreference(buil);
        if (p == -1 && buil.getParentBuilder() != null) {
            buil = buil.getParentBuilder();
            p = getPreference(buil);
        }
        return p;
    }

}

