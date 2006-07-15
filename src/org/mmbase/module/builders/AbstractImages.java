/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.images.*;


/**
 * AbstractImages holds the images and provides ways to insert, retrieve and
 * search them.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java,v 1.24.2.4 2005-08-17 14:32:19 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends AbstractServletBuilder {

    private static final Logger log = Logging.getLoggerInstance(AbstractImages.class);

    /**
     * Cache with 'ckey' keys.
     * @since MMBase-1.6.2
     */
    abstract protected static class  CKeyCache extends org.mmbase.cache.Cache {
        protected CKeyCache(int i) {
            super(i);
        }
        /**
         * Remove all cache entries associated with a certain images node
         * This depends now on the fact that ckeys start with the original node-number
         */

        void   remove(int originalNodeNumber) {
            String prefix = "" + originalNodeNumber;
            log.debug("removing " + prefix);
            Iterator keys  = keySet().iterator();
            List removed = new ArrayList();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                log.debug("checking " + key);
                if (key.startsWith(prefix)) {
                    // check is obviously to crude, e.g. if node number happens to be 4,
                    // about one in 10 cache entries will be removed which need not be removed,
                    // but well, it's only a cache, it's only bad luck...
                    // 4 would be a _very_ odd number for an Image, btw..
                    log.debug("removing " + key + " " + get(key));
                    removed.add(key);
                    // cannot use keys.remove(), becaus then cache.remove is not called.
                }

            }
            keys = removed.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                remove(key);
            }
        }

        void removeCacheNumber(int icacheNumber) {
            Iterator entries  = entrySet().iterator();
            List removed = new ArrayList();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();                    
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof ByteFieldContainer) {
                    ByteFieldContainer bf = (ByteFieldContainer) value;                
                    if (bf.number == icacheNumber) {
                        removed.add(key);
                    }
                } else if (value instanceof Integer) {
                    Integer i = (Integer) value;
                    if (i.intValue() == icacheNumber) {
                        removed.add(key);
                    }
                }
                
            }
            Iterator keys = removed.iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                remove(key);
            }
        }
    }


    protected String getAssociation() {
        return "images";
    }
    protected String getDefaultPath() {
        return "/img.db";
    }

    /**
     * An image's gui-indicator is of course some &lt;img src&gt;, but it depends on what kind of image
     * (cached, original) what excactly it must be.
     */
    abstract protected String getGUIIndicatorWithAlt(MMObjectNode node, String title, Parameters a);

    /**
     * Returns GUI Indicator for node
     */
    protected String getSGUIIndicatorForNode(MMObjectNode node, Parameters a) {
        return getGUIIndicatorWithAlt(node, "*", a); /// Gui indicator of a whole node.
    }

    protected String getSGUIIndicator(MMObjectNode node, Parameters a) {
        String field = a.getString("field");
        if (field.equals("handle") || field.equals("")) {
            return getSGUIIndicatorForNode(node, a);
        }
        // other fields can be handled by the orignal gui function...
        return getSuperGUIIndicator(field, node);
    }

    /**
     * Returns the format of the image. Like 'jpg' or 'gif'.
     */
    abstract protected String getImageFormat(MMObjectNode node);

    /**
     * Determine the MIME type of this image node, baseImagd on the image format.
     */
    public String getImageMimeType(MMObjectNode node) {
        return mmb.getMimeType(getImageFormat(node));
    }

    /**
     * Returns an image which belongs to the given parameter set.  The
     * parameters exist of a list of string values, starting with the
     * orginal image object alias or number, followed by operations
     * (format and transformation instructions)
     *
     * This function is not used by ImageServlet. Perhaps it should be deprecated.
     *
     * @param params A list of parameters, containign at least the id of the image, possibly followed by operations
     * @return the image as a <a>byte[]</code>, or <code>null</code> if something went wrong
     */
    abstract public byte[] getImageBytes(List params);
    private static final String FIELD_WIDTH = "width";
    private static final String FIELD_HEIGHT = "height";
    

    /**
     * Whether this builders has width and height fields
     */
    protected boolean storesDimension() {
        return getField(FIELD_WIDTH) != null && getField(FIELD_HEIGHT) != null;
    }


    /**
     * Gets the dimension of given node. Also when the fields are missing, it will result a warning then.
     */
    protected Dimension getDimension(MMObjectNode node) {
        if (storesDimension()) {
            int width  = node.getIntValue(FIELD_WIDTH);
            int height = node.getIntValue(FIELD_HEIGHT);
            if (width >= 0 && height > 0 ) {
                return new Dimension(width, height);
            }
        }
        byte[] data = node.getByteValue("handle");
        if (data == null || data.length == 0) {
            log.warn("Cannot get dimension of Node with not 'handle' " + node.getNumber());
            return new Dimension(-1, -1);
        }
        ImageInformer ii = new DummyImageInformer();
        Dimension dim;
        try {
            dim = ii.getDimension(data);
            log.debug("Found dimension " + dim);
        } catch (Exception ioe) {
            log.error(ioe);
            dim = new Dimension(-1, -1);
            return dim;
        }
        if (storesDimension()) {
            node.setValue(FIELD_WIDTH,  dim.getWidth());
            node.setValue(FIELD_HEIGHT, dim.getHeight());
        } else {
            log.warn("Requested dimension on image object without height / width fields, this may be heavy on resources!");
        }

        return dim;        
    }

    /**
     * Every image of course has a format and a mimetype. Two extra functions to get them.
     *
     */

    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (function.equals("mimetype")) {
            return getImageMimeType(node);
        } else if (function.equals("format")) {
            return getImageFormat(node);
        } else if ("dimension".equals(function)) {
            return getDimension(node);
        } else {
            return super.executeFunction(node, function, args);
        }
    }


}

