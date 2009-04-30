/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * An annotated node is an object that wraps another {@link Node} but wich can be annotated with
 * some extra information. This information can be retrieved by {@link #getAnnotation}, or as a
 * matter of convience in e.g. JSPs using {@link #getValue} with a field name starting with "annotation:".
 *
 * @author  Michiel Meeuwissen
 * @version $Id: AnnotatedNode.java,v 1.2 2009-04-30 10:06:12 michiel Exp $
 * @since   MMBase-1.9
 */

public class AnnotatedNode extends NodeWrapper  {

    private static final Logger log = Logging.getLoggerInstance(AnnotatedNode.class);


    private final Map<String, Object> annotations = new ConcurrentHashMap<String, Object>();
    public AnnotatedNode(Node n) {
        super(n);
    }

    public Object getAnnotation(String a) {
        return annotations.get(a);
    }
    public Object putAnnotation(String a, Object o) {
        return annotations.put(a, o);
    }

    public Object getValue(String fieldName) {
        if (fieldName.startsWith("annotation:")) {
            log.debug("Getting annotation " + fieldName.substring(11));
            return getAnnotation(fieldName.substring(11));
        } else {
            return super.getValue(fieldName);
        }
    }


}
