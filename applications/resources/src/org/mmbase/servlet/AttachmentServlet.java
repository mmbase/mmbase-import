/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.util.Map;
import org.mmbase.bridge.*;

/**
 * Serves attachments. An attachments can be any object, as long as it has a byte[] field named
 * 'handle'.  Also the fields 'filename', 'mimetype' and 'title' can be taken into consideration by
 * this servlet and preferably the node has also those fields.
  *
 * @version $Id: AttachmentServlet.java,v 1.2 2008-09-29 16:32:41 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @see HandleServlet
 * @see ImageServlet
 */
public class AttachmentServlet extends HandleServlet {


    public String getServletInfo()  {
        return "Serves MMBase nodes as attachments";
    }

    protected Map<String, Integer> getAssociations() {
        Map<String, Integer> a = super.getAssociations();
        a.put("attachments", 50); // Is very good in attachments (determines mime-type
                                               // starting with 'attachments' builder fields),

        a.put("images",      10); // And also can do images (but is not aware of // icaches)
        a.put("downloads",   0);
        return a;
    }

    // just to get AttachmentServlet in the stacktrace.
    protected final Cloud getClassCloud() {
        return super.getClassCloud();
    }

    /**
     * Determines the mimetype. Can be overridden.
     */
    protected String getMimeType(Node node) {
        String mimeType = null;
        if (node.getNodeManager().hasField("mimetype")) mimeType = node.getStringValue("mimetype");
        if (mimeType == null || mimeType.equals("")) {
            // mime-type missing, try to suppose that this is an image node, which has the mimetype
            // as a function.
            mimeType = node.getFunctionValue("mimetype", null).toString();
            if (mimeType == null || mimeType.equals("")) {
                return super.getMimeType(node);
            }
        }
        return mimeType;
    }

}
