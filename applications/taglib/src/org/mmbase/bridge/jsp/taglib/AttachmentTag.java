/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.File;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.images.*;
import org.mmbase.util.UriParser;
import org.mmbase.module.builders.Images;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Produces an url to the image servlet mapping. Using this tag makes
 * your pages more portable to other system, and hopefully less
 * sensitive for future changes in how the image servlet works.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class AttachmentTag  extends ImageTag {

    private static final Logger log = Logging.getLoggerInstance(AttachmentTag.class);

    public String getTemplate(Node node, String t, int widthTemplate, int heightTemplate, String cropTemplate) {
        return t;
    }

}

