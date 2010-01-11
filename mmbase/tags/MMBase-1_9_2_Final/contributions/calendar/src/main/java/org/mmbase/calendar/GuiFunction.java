/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.calendar;

import org.mmbase.util.functions.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The gui function of calendar items and calendar item types.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class GuiFunction extends org.mmbase.util.functions.GuiFunction {

    private static final Logger log = Logging.getLoggerInstance(GuiFunction.class);

    public GuiFunction() {
        super();
    }

    protected String getFunctionValue(Node node, Parameters parameters) {
        String fieldName = (String) parameters.get(Parameter.FIELD);
        boolean nodeGui = fieldName == null || "".equals(fieldName);
        String color = null;
        if ("type".equals(fieldName) || (nodeGui && node.getNodeManager().hasField("type"))) {
            Node typeNode = node.getNodeValue("type");
            if (typeNode != null) {
                color = typeNode.getStringValue("color");
            }
        } else if ("color".equals(fieldName) || (nodeGui && node.getNodeManager().hasField("color"))) {
            color = node.getStringValue("color");
        }
        if (color != null) {
            return "<span style='background-color: " + color + ";'>" + super.getFunctionValue(node, parameters) + "</span>";
        } else {
            return (String) super.getFunctionValue(node, parameters);
        }

    }

}
