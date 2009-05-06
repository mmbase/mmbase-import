/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.jsp.taglib.util.Referids;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Combines the parameters from the 'referids' attribute with the explicit mm:param's
 * subtags. This happens 'lazily'. So, the referids are evaluated only when used.
 * @since MMBase-1.9
 */
public class UrlParameters extends AbstractMap<String, Object> {

    private static final Logger log = Logging.getLoggerInstance(UrlParameters.class);


    Map<String, Object> wrapped = null;
    private UrlTag tag;
    UrlParameters(UrlTag tag) {
        this.tag = tag;
    }
    protected void getWrapped(boolean dereference) {
        if (wrapped == null) {
            try {
                wrapped = new TreeMap<String, Object>();
                List<Map.Entry<String, Object>> refs = Referids.getList(tag.referids, tag);
                for (Map.Entry<String, Object> e : refs) {
                    wrapped.put(e.getKey(), e.getValue());
                }
                if (tag.extraParameters != null) {
                    wrapped.putAll(tag.extraParameters);
                }
                if (log.isDebugEnabled()) {
                    log.debug("url parameters " + wrapped + " " + refs + "/" + tag.extraParameters);
                }
                if (dereference) tag = null;
            } catch (JspTagException je) {
                throw new RuntimeException(je);
            }
        } else {
            log.debug("url parameters. " + wrapped);
        }
    }
    public int size() {
        getWrapped(false);
        return wrapped.size();
    }
    public Set<Map.Entry<String, Object>> entrySet() {
        getWrapped(false);
        return wrapped.entrySet();
    }

    protected void invalidate() {
        wrapped = null;
    }


}
