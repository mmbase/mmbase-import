/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * AbstractImages holds the images and provides ways to insert, retrieve and
 * search them.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java,v 1.10 2002-06-28 22:36:11 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends AbstractServletBuilder {

    private static Logger log = Logging.getLoggerInstance(AbstractImages.class.getName());


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
    abstract protected String getGUIIndicatorWithAlt(MMObjectNode node, String title);

    /**
     * Gui indicator of a whole node.
     */
    public String getGUIIndicator(MMObjectNode node) {
        return getGUIIndicatorWithAlt(node, "*");
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if (field.equals("handle")) {
            return getGUIIndicatorWithAlt(node, "*");
        }
        // other fields can be handled by the gui function...
        return null;
    }

    /**
     * Returns the format of the image. Like 'jpg' or 'gif'.
     */ 
    abstract protected String getImageFormat(MMObjectNode node);

    /**
     * Determine the MIME type of this image node. If the node is not
     * an icache node, but e.g. an images node, then it will return
     * the default mime format, which is 'jpg'. This should be done
     * better, since there is a field itype in the images table.
     *
     */
    public String getImageMimeType(MMObjectNode node) {
        return mmb.getMimeType(getImageFormat(node));
    }

    /**
     * Returns an image which belongs to the given parameter set.  The
     * parameters exist of a list of string values, staring with the
     * proginal image object alias or number, followed by operations
     * (format and transformation instructions)
     *
     * This function is not used by ImageServlet. Perhaps it should be deprecated.
     *
     * @param params A list of parameters, containign at least the id of the image, possibly followed by operations
     * @return the image as a <a>byte[]</code>, or <code>null</code> if something went wrong
     */
    abstract public byte[] getImageBytes(List params);

    /**
     * Every image of course has a format and a mimetype. Two extra functions to get them.
     *
     */

    protected Object executeFunction(MMObjectNode node, String function, String field) {
        if (function.equals("mimetype")) {
            return getImageMimeType(node);
        } else if (function.equals("format")) {
            return getImageFormat(node);
        } else {
            return super.executeFunction(node, function, field);
        }
    }

}

