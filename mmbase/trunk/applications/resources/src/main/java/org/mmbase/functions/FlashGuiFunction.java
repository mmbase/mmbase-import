/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.functions;

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
 * A gui function for object of builders representing 'flash objects'.
 * Add this to the flashobjects builder xml.
 <pre><![CDATA[
  <functionlist>
    <function name="gui">
      <class>org.mmbase.functions.FlashGuiFunction</class>
    </function>
  </functionlist>
 ]]><pre>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class FlashGuiFunction extends NodeFunction<String> {

    private static final Logger log = Logging.getLoggerInstance(FlashGuiFunction.class);

    private String handleField = "handle";
    private String guiWidth    = "100";
    private String guiHeight   = "100";


    public FlashGuiFunction() {
        super("gui", GuiFunction.PARAMETERS);
    }


    public void setHandleField(String hf) {
        handleField = hf;
    }
    public void setGuiWidth(String w) {
        guiWidth = w;
    }
    public void setGuiHeight(String h) {
        guiHeight = h;
    }


    protected StringBuilder appendFlash(StringBuilder buf, String url) {
        buf.append("<object class='mm_gui'  type=\"application/x-shockwave-flash\" ");
        buf.append("width='").append(guiWidth).append("' height='").append(guiHeight).append("' id=\"");
        buf.append(name);
        buf.append("\" data=\"").append(url).append("\">");
        buf.append("<param name=\"movie\" value=\"");
        buf.append(url);
        buf.append("\" />");
        buf.append("</object>");
        return buf;
    }

    /**
     * @since MMBase-1.9.2
     */
    protected String getGuiForNewFlash(Node node, Parameters parameters) throws IOException  {
        FileServlet instance = FileServlet.getInstance();
        String number = node.getStringValue("_number");
        SerializableInputStream is = Casting.toSerializableInputStream(node.getInputStreamValue(handleField));
        if (is.getFileName() != null) {
            if (instance == null) {
                return "<span class='mm_gui nofileservlet'>NO FILE SERVLET</span>";
            } else {
                String files = FileServlet.getBasePath("files").substring(1);
                HttpServletRequest req = parameters.get(Parameter.REQUEST);
                String root = req.getContextPath();
                String url =  root + "/" + files + "uploads/" + UrlEscaper.INSTANCE.transform(is.getFileName());
                return appendFlash(new StringBuilder(), url).toString();
            }
        } else {
            return "<span class='mm_gui'>--</span>";
        }
    }

    @Override
    protected String getFunctionValue(Node node, Parameters parameters) {
        if (log.isDebugEnabled()) {
            log.debug("GUI of builder with " + parameters);
        }
        String fieldName = parameters.get(Parameter.FIELD);
        if (fieldName == null || "".equals(fieldName) || fieldName.equals(handleField)) {
            int num = node.getNumber();
            if (num < 0 || node.getChanged().contains(handleField)) {
                try {
                    return getGuiForNewFlash(node, parameters);
                } catch (IOException ioe) {
                    return Xml.INSTANCE.transform(ioe.getMessage());
                }
            }
            String n = "mm_flashobject_" + node.getNumber();
            String url = node.getFunctionValue("servletpath", null).toString();
            return appendFlash(new StringBuilder(), url).toString();
        } else {
            MMObjectNode n = (MMObjectNode) parameters.get(Parameter.CORENODE);
            return n.getBuilder().getGUIIndicator(n, parameters);
        }
    }

}
