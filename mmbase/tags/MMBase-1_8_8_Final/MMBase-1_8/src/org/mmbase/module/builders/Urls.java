/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @application Tools
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class Urls extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(Urls.class);

    public String getGUIIndicator(MMObjectNode node) {
        String str = node.getStringValue("url");
        if (str != null) {
            if (str.indexOf("http://") == 0) {
                str = str.substring(7);
            }
            str = org.mmbase.util.transformers.Xml.XMLEscape(str);
        }
        return str;
    }

    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("url")) {
            String url = node.getStringValue("url");
            if (url != null) {
                url = org.mmbase.util.transformers.Xml.XMLEscape(url);
                return "<a href=\"" + url + "\" class=\"mm_gui\" onclick=\"window.open(this.href); return false;\">" + url + "</a>";
            }
        }
        return null;
    }


    public String getDefaultUrl(int src) {
        MMObjectNode node = getNode(src);
        return node.getStringValue("url");
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
         if(tableName.equals(event.getBuilderName())){
             try {
                 Jumpers jumpers = (Jumpers)mmb.getBuilder("jumpers");
                 if (jumpers == null) {
                     log.debug("Urls builder - Could not get Jumper builder");
                 } else {
                     jumpers.delJumpCache(""+event.getNodeNumber());
                 }
             } catch (ClassCastException cce) {
                 log.debug("probably jumper is now org.mmbase.jumpers.Jumpers. Never mind. We'll make that listen to urls itself " + cce.getMessage());

             }
         }
        super.notify(event);
    }
}
