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
 * @version $Id: FlashGuiFunction.java,v 1.1 2008-09-03 13:06:28 michiel Exp $
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
            StringBuilder buf = new StringBuilder("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0\" ");
            buf.append("width='").append(guiWidth).append("' height='").append(guiHeight).append("' id=\"");
            buf.append(name);
            buf.append("\" align=\"middle\"> <param name=\"allowScriptAccess\" value=\"sameDomain\" /> <param name=\"allowFullScreen\" value=\"true\" /> <param name=\"movie\" value=\"");
            buf.append(url);
            buf.append("\" /><param name=\"quality\" value=\"high\" /><param name=\"bgcolor\" value=\"#ffffff\" /> <embed src=\"");
            buf.append(url);
            buf.append("\" quality=\"high\" bgcolor=\"#ffffff\" ");
            buf.append("width='").append(guiWidth).append("' height='").append(guiHeight).append("' name=\"");
            buf.append(name);
            buf.append("\" align=\"middle\" allowScriptAccess=\"sameDomain\" allowFullScreen=\"true\" type=\"");
            buf.append(node.getStringValue("mimetype"));
            buf.append("\"  pluginspage=\"http://www.macromedia.com/go/getflashplayer\" /></object>");
            return buf.toString();
        } else {
            MMObjectNode n = (MMObjectNode) parameters.get(Parameter.CORENODE);
            return n.getBuilder().getGUIIndicator(n, parameters);
        }
    }

}
