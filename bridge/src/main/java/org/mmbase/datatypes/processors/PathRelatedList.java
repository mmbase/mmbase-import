/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.7
 * @version $Id: RelatedList.java 36281 2009-06-22 20:47:35Z michiel $
 */

public class PathRelatedList {

    private static final Logger log = Logging.getLoggerInstance(PathRelatedList.class);

    public abstract static class  AbstractProcessor implements Processor {

        protected String path = "object";
        protected String searchDirs = "destination";

        public void setPath(String p) {
            path = p;
        }
        public void setSearchDirs(String d) {
            searchDirs = d;
        }
    }

    public static class Getter extends AbstractProcessor {
        private static final long serialVersionUID = 1L;

        @Override
        public Object process(Node node, Field field, Object value) {
            NodeQuery q = Queries.createNodeQuery(node);
            Queries.addPath(q, path, searchDirs);
            q.setNodeStep(q.getSteps().get(q.getSteps().size() - 1));
            NodeList nl = q.getNodeManager().getList(q);
            if (log.isDebugEnabled()) {
                log.debug("related nodes for  "  + node  + " " + nl);
            }
            if (nl.size() == 0) {
                return null;
            } else {
                return nl;
            }
        }
    }

}
