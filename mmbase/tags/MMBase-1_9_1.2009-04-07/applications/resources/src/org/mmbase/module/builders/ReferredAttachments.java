/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * An attachment builder where, aside from storing the binary data in the database, you can point out a
 * binary resource on another server using an url.
 * Basic support for sucha  field is in AbstractServletBuidler.
 * This builder defines a default url field ('url'), has a better GUI function, and determines file size,
 * filename, and mimetype from a referred to file when the url changes.
 *
 * @author Pierre van Rooden
 * @version $Id: ReferredAttachments.java,v 1.4 2008-07-29 08:38:53 pierre Exp $
 * @since   MMBase-1.8
 */
public class ReferredAttachments extends Attachments {

    public static final String DEFAULT_EXTERNAL_URL_FIELD = "url";

    private static final Logger log = Logging.getLoggerInstance(ReferredAttachments.class);

    /**
     * Sets a default for the 'externalUrlField' property
     */
    public boolean init() {
        externalUrlField = DEFAULT_EXTERNAL_URL_FIELD;
        return super.init();
    }

    protected void checkHandle(MMObjectNode node) {
        String url = node.getStringValue(externalUrlField);
        if (url != null && !url.equals("")) {
            try {
                URL reference = new URL(url);
                URLConnection connection  = reference.openConnection();
                if (getField(FIELD_SIZE) != null) {
                    if (node.getIntValue(FIELD_SIZE) == -1) {
                        node.setValue(FIELD_SIZE, connection.getContentLength());
                    }
                }
                if (getField(FIELD_MIMETYPE) != null) {
                    node.setValue(FIELD_MIMETYPE, connection.getContentType());
                }
                if (getField(FIELD_FILENAME) != null) {
                    String filename = url;
                    int pos = filename.lastIndexOf('/');
                    if (pos > 0 && pos < filename.length()-1) {
                        filename = filename.substring(pos+1);
                    }
                    node.setValue(FIELD_FILENAME, filename);
                }
            } catch (MalformedURLException mue) {
                log.warn("wrong url format:" + url);
            } catch (IOException ie) {
                log.warn("cannot connect to:" + url);
            }
        } else {
            super.checkHandle(node);
        }
    }

    protected String getSGUIIndicator(MMObjectNode node, Parameters a) {
        String field = a.getString("field");
        if (field.equals("handle") || field.equals("")) {
            String url = node.getStringValue(externalUrlField);
            if (url != null && !url.equals("")) {
                String fileName = getFileName(node, new StringBuilder()).toString();
                String title;
                if (fileName == null || fileName.equals("")) {
                title = "[*]";
                } else {
                    title = "[" + fileName + "]";
                }
                HttpServletResponse res = (HttpServletResponse) a.get("response");
                return "<a href=\"" + res.encodeURL(url) + "\" onclick=\"window.open(this.href);return false;\" >" + title + "</a>";
            }
        }
        return super.getSGUIIndicator(node, a);
    }

}
