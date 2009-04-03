/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.didactor.shoutbox;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**

 * @author Michiel Meeuwissen
 * @version $Id: ReferenceDataType.java,v 1.1 2008-09-04 13:23:12 michiel Exp $
 * @since MMBase-1.9
 */
public class ReferenceDataType extends NodeDataType {

    private static final Logger log = Logging.getLoggerInstance(ReferenceDataType.class);

    private static final long serialVersionUID = 1L;

    public ReferenceDataType(String name) {
        super(name);
    }

    @Override public Node getDefaultValue(Locale locale, Cloud cloud, Field field) {
        String key = cloud == null ? null : (String) cloud.getProperty("org.mmbase.shoutbox.reference");
        if (key != null && ! "".equals(key)) {
            return cloud.getNode(key);
        } else {
            return super.getDefaultValue(locale, cloud, field);
        }
    }

    @Override public Iterator<Map.Entry<Node, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        if (node == null && cloud == null) return null; // we don't know..
        HttpServletRequest request = (HttpServletRequest) cloud.getProperty(Cloud.PROP_REQUEST);
        if (request == null) return null;
        Node education = Casting.toNode(request.getAttribute("education"), cloud);
        if (education == null) return null;

        NodeQuery q = Queries.createRelatedNodesQuery(education, cloud.getNodeManager("learnobjects"), "posrel", "destination");
        Queries.addSortOrders(q, "posrel.pos", "up");
        final Iterator<Node> iterator = q.getNodeManager().getList(q).iterator();
        return new Iterator<Map.Entry<Node, String>>() {

            public boolean hasNext() {
                return iterator.hasNext();
            }
            public Map.Entry<Node, String> next() {
                Node val = iterator.next();
                return new Entry<Node, String>(val, val.getFunctionValue("gui", null).toString());
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


}
