/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.ByteArrayInputStream;
import org.mmbase.module.core.*;
//import org.mmbase.util.ImageInfo;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * AbstractImages holds the images and provides ways to insert, retrieve and
 * search them.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java,v 1.28 2004-10-03 11:03:46 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends AbstractServletBuilder {

    private static final Logger log = Logging.getLoggerInstance(AbstractImages.class);

    /*
    static final ImageInfo imageInfo = new ImageInfo();

    public final static Parameter[] WIDTH_PARAMETERS    = {};
    public final static Parameter[] HEIGHT_PARAMETERS   = {};
    */

    /**
     * Cache with 'ckey' keys.
     * @since MMBase-1.6.2
     */
    abstract protected static class CKeyCache extends org.mmbase.cache.Cache {
        protected CKeyCache(int i) {
            super(i);
        }
        /**
         * Remove all cache entries associated with a certain images node
         * This depends now on the fact that ckeys start with the original node-number
         */

        void remove(int originalNodeNumber) {
            String prefix = "" + originalNodeNumber;
            if (log.isDebugEnabled()) {
                log.debug("removing " + prefix);
            }
            Iterator keys  = keySet().iterator();
            List removed = new ArrayList();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (log.isDebugEnabled()) {
                    log.debug("checking " + key);
                }
                if (key.startsWith(prefix)) {
                    // check is obviously to crude, e.g. if node number happens to be 4,
                    // about one in 10 cache entries will be removed which need not be removed,
                    // but well, it's only a cache, it's only bad luck...
                    // 4 would be a _very_ odd number for an Image, btw..
                    if (log.isDebugEnabled()) {
                        log.debug("removing " + key + " " + get(key));
                    }
                    keys.remove();
                }

            }
        }
        
        void removeCacheNumber(int icacheNumber) {
            Iterator entries  = entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();                    
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof ByteFieldContainer) {
                    ByteFieldContainer bf = (ByteFieldContainer) value;                
                    if (bf.number == icacheNumber) {
                        entries.remove();
                    }
                } else if (value instanceof Integer) {
                    Integer i = (Integer) value;
                    if (i.intValue() == icacheNumber) {
                        entries.remove();
                    }
                }
                
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
     * @since MMBase-1.7
     */
    protected String getSGUIIndicatorForNode(MMObjectNode node, Parameters a) {
        return getGUIIndicatorWithAlt(node, "*", a); /// Gui indicator of a whole node.
    }

    /**
     * @since MMBase-1.7
     */
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
    

    /**
     */
    /*
    protected int getHeight(MMObjectNode node) {       
        byte[] data = node.getByteValue("handle");      
        imageInfo.setInput(new ByteArrayInputStream(data));
        
        if (log.isServiceEnabled()) {
            if (!imageInfo.check()) {
                log.service("ImageBuilder: Error parsing image");  
            }
        }
        int height = imageInfo.getHeight();
        if (log.isDebugEnabled()) {
            log.debug("ImageBuilder: height being returned = " + height);
        }
        return height;
    }
    */
    /**
     */
    /*
    protected int getWidth(MMObjectNode node) {

       byte[] data = node.getByteValue("handle");      
       imageInfo.setInput(new ByteArrayInputStream(data));
       
       if (log.isServiceEnabled()) {
           if (!imageInfo.check()) {
               log.service("ImageBuilder: Error parsing image");  
           }
       }
       int width = imageInfo.getWidth();
       if (log.isDebugEnabled()) {
           log.debug("ImageBuilder: width being returned = " + width);
       }
       return width;
    }
    */

    /**
     * {@inheritDoc}
     */
    public Parameter[] getParameterDefinition(String function) {
        return org.mmbase.util.functions.NodeFunction.getParametersByReflection(AbstractImages.class, function);
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
            /*
        } else if ("width".equals(function)) {    
            return new Integer(getWidth(node));
        } else if ("height".equals(function)) {     
            return new Integer(getHeight(node));
            */
        } else {
            return super.executeFunction(node, function, args);
        }
    }

}
