/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.filters;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id$
 */
public abstract class FilterUtils {

    private static Logger log = Logging.getLoggerInstance(FilterUtils.class);

    public static void propertiesConfigure(Object o, DocumentReader reader, Element e) {
        NodeList params = e.getChildNodes();
        for (int i = 0 ; i < params.getLength(); i++) {
            try {
                Node node = params.item(i);
                if (node instanceof Element && node.getNodeName().equals("property")) {
                    Element param = (Element)node;
                    String name = param.getAttribute("name");
                    String value = DocumentReader.getNodeTextValue(param);
                    org.mmbase.util.xml.Instantiator.setProperty(name, o.getClass(), o, value);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * @since MMBase-1.9.6
     * @param urlcomposer
     * @param a
     * @return
     */
    public static Object getClientAttribute(URLComposer urlcomposer, String a) {       
        Map attributes = (Map) urlcomposer.getInfo().get("attributes");
        if (attributes != null && attributes.containsKey(a)) return attributes.get(a);
        HttpServletRequest req = (HttpServletRequest) urlcomposer.getInfo().get(Parameter.REQUEST.getName());
        if (req != null) {
            Object o = req.getAttribute(a);
            if (o != null) return o;
        }
        return null;

    }
}

