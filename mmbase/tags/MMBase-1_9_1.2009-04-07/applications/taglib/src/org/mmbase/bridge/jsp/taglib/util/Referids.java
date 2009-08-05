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
import org.mmbase.util.Entry;
import org.mmbase.util.logging.*;

/**
 * A helper class to implement referids attribute.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Referids.java,v 1.8 2007-07-18 07:50:48 michiel Exp $
 * @since MMBase-1.7
 */
public abstract class  Referids  {
    private static final Logger log = Logging.getLoggerInstance(Referids.class);

    protected static Map.Entry<String, Object> get(String key, ContextReferrerTag tag) throws JspTagException {
        if (key.length() == 0) return null;
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
            return new Entry<String, Object>(urlKey, value);
        } else if ((! mayBeMissing) || tag.getContextProvider().getContextContainer().isPresent(key)) {
            Object value = tag.getObject(key);
            if (value != null) {
                if (log.isDebugEnabled()) {
                    log.debug("adding parameter (with referids) " + key + "/" + value);
                }
                return new Entry<String, Object>(urlKey, value);
            } else {
                log.debug("No key '" + key + "' in context, not adding to referids");
                return null;
            }
        } else {
            // exception ?
            return null;
        }
    }
    public static Map<String, Object> getReferids(Attribute referids, ContextReferrerTag tag) throws JspTagException {
        Map<String, Object> result = new LinkedHashMap<String, Object>(); // using a 'linked' hash map ensures that the key/value pairs are added in the same order as they are in the referids attribute
        // log.info("" + referids + " : " + referids.getList(this));
        for (String key : referids.getList(tag)) {
            Map.Entry<String, Object> entry = get(key, tag);
            if (entry != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 
     * @since MMBase-1.9
     */
    public static List<Map.Entry<String, Object>> getList(Attribute referids, ContextReferrerTag tag) throws JspTagException {
        List<Map.Entry<String, Object>> result = new ArrayList<Map.Entry<String, Object>>();
        for (String key : referids.getList(tag)) {
            Map.Entry<String, Object> entry = get(key, tag);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result;
    }

}
