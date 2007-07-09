/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.w3c.dom.*;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.functions.*;

/**
 * A component is a piece of pluggable functionality that typically has dependencies on other
 * components, and may be requested several views.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicComponent.java,v 1.2 2006-09-12 19:25:59 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicComponent implements Component {

    private final String name;
    private final LocalizedString description;
    private final Map<String, View> views = new HashMap();

    public BasicComponent(String name) {
        this.name = name;
        this.description = new LocalizedString(name);
    }
    public String getName() {
        return name;
    }
    public LocalizedString getDescription() {
        return description;
    }

    public void configure(Element el) {
        description.fillFromXml("description", el);
        NodeList views = el.getElementsByTagName("view");
        for (int i = 0 ; i < views.getLength(); i++) {
            Element view = (Element) views.item(i);
            String name = view.getAttribute("name");
            //View view = ComponentRepository.getInstance((Element) view.getElementsByTagName("class").item(0));
            
        }
    }

    public String toString() {
        return getName();
    }

    public Map<String, View> getViews() {
        return Collections.unmodifiableMap(views);
    }

}
