/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.util.logging.*;

/**
 * A helper class to implement referids attribute.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Referids.java,v 1.2 2004-02-11 20:40:14 keesj Exp $
 * @since MMBase-1.7
 */
public class  Referids  {
    private static final Logger log = Logging.getLoggerInstance(Referids.class);

    public static Map getReferids(Attribute referids, ContextReferrerTag tag) throws JspTagException {
        Map result = new HashMap();
        // log.info("" + referids + " : " + referids.getList(this));
        Iterator i = referids.getList(tag).iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            int at = key.indexOf('@');
            String urlKey;
            if (at > -1) {
                urlKey = key.substring(at + 1, key.length());
                key = key.substring(0, at);
            } else {
                urlKey = key;
            }

            boolean mayBeMissing;
            if (key.endsWith("?")) {
                mayBeMissing = true;
                boolean keyIsUrlKey = key.equals(urlKey);
                key = key.substring(0, key.length() - 1);
                if (keyIsUrlKey) urlKey = key;
            } else {
                mayBeMissing = false;
            }
            if (key.equals("_")) {
                if (urlKey.equals("_")) throw new JspTagException("Should use '@' when using '_' in referids");
                Object value = tag.findWriter().getWriterValue();
                result.put(urlKey, value);
            } else if ((! mayBeMissing) || tag.getContextProvider().getContextContainer().isPresent(key)) {
                Object value = tag.getObject(key);
                if (value != null) {                       
                    if (log.isDebugEnabled()) {
                        log.debug("adding parameter (with referids) " + key + "/" + value);
                    }
                    result.put(urlKey, value);
                }
            } else {
                log.debug("No key '" + key + "' in context, not adding to referids");
            }
        }
        return result;
    }

}
