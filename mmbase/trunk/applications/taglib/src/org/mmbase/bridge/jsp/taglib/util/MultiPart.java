/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import java.util.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.*;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.apache.commons.fileupload.*;

/**
 * Taglib needs to read Multipart request sometimes. Functionallity is centralized here.
 * @author Michiel Meeuwissen
 * @version $Id$
 * @deprecated
 **/

public class MultiPart extends org.mmbase.datatypes.handlers.html.MultiPart {

    private static final Logger log = Logging.getLoggerInstance(MultiPart.class);

    static String MULTIPARTREQUEST_KEY = "__multipart";

    public static boolean isMultipart(PageContext pageContext) {
        return isMultipart((HttpServletRequest)pageContext.getRequest());
    }

    public static MMultipartRequest getMultipartRequest(PageContext pageContext) {
        return getMultipartRequest((HttpServletRequest) pageContext.getRequest(), ContextContainer.getDefaultCharacterEncoding(pageContext));

    }




}
