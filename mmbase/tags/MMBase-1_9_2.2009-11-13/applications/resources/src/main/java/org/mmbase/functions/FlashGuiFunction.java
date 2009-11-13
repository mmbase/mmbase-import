/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.functions;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.functions.*;
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
    private String guiWidth = "100";
    private String guiHeight = "100";


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

    @Override protected String getFunctionValue(Node node, Parameters parameters) {
        if (log.isDebugEnabled()) {
            log.debug("GUI of builder with " + parameters);
        }
        String fieldName = parameters.get(Parameter.FIELD);
        if (fieldName == null || "".equals(fieldName) || fieldName.equals(handleField)) {
            String name = "mm_flashobject_" + node.getNumber();
            String url = node.getFunctionValue("servletpath", null).toString();
            StringBuilder buf = new StringBuilder("<object  type=\"application/x-shockwave-flash\" ");
            buf.append("width='").append(guiWidth).append("' height='").append(guiHeight).append("' id=\"");
            buf.append(name);
            buf.append("\" data=\"").append(url).append("\">");
            buf.append("<param name=\"movie\" value=\"");
            buf.append(url);
            buf.append("\" />");
            buf.append("</object>");
            return buf.toString();
        } else {
            MMObjectNode n = (MMObjectNode) parameters.get(Parameter.CORENODE);
            return n.getBuilder().getGUIIndicator(n, parameters);
        }
    }

}
