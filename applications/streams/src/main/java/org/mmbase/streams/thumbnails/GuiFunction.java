/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.thumbnails;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.servlet.FileServlet;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.UrlEscaper;
import org.mmbase.util.transformers.Xml;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: FlashGuiFunction.java 40156 2009-12-08 15:56:39Z michiel $
 * @since MMBase-1.9
 */
public class GuiFunction extends NodeFunction<String> {

    private static final Logger LOG = Logging.getLoggerInstance(GuiFunction.class);

    public GuiFunction() {
        super("gui", org.mmbase.util.functions.GuiFunction.PARAMETERS);
    }

    @Override
    protected String getFunctionValue(Node node, Parameters parameters) {
        LOG.info("Field " + parameters.get(Parameter.FIELD));
        if (parameters.get(Parameter.FIELD) == null) {
            Node thumb = ThumbNailFunction.getThumbNail(node, null);
            return thumb.getFunctionValue("gui", parameters).toString();
        } else {
            return null;
        }
    }

}
