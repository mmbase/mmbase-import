/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.DataTypeReader;
import org.mmbase.util.LocalizedString;
import org.w3c.dom.Element;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Representation of component related settings.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Setting.java,v 1.2 2007-07-26 21:03:11 michiel Exp $
 * @since MMBase-1.9
 */
public class Setting<C> {

    private static final Logger log = Logging.getLoggerInstance(Setting.class);
    protected final DataTypeCollector dataTypeCollector = new DataTypeCollector(BasicComponent.class);

    private final String name;
    private final LocalizedString description;
    private final Component parent;
    private final DataType<C> dataType;


    public Setting(Component component, Element element) {
        name = element.getAttribute("name");
        description = new LocalizedString(name);
        description.fillFromXml("description", element);
        parent = component;
        Element dataTypeElement = (Element) element.getElementsByTagName("datatype").item(0);
        BasicDataType base = dataTypeCollector.getDataType(dataTypeElement.getAttribute("base"), true);
        dataType = DataTypeReader.readDataType(dataTypeElement, base, dataTypeCollector).dataType;
    }

    /**
     */
    public String getName() {
        return name;
    }
    public LocalizedString getDescription() {
        return description;
    }

    public Component getComponent() {
        return parent;
    }
    public DataType<C> getDataType() {
        return dataType;
    }

    public String toString() {
        return "Setting_" + getName() + " " + getDataType();
    }

    public int hashCode() {
        return 13 * parent.hashCode() + dataType.hashCode();
    }
    public boolean equals(Object o) {
        if (o instanceof Setting) {
            Setting s = (Setting) o;
            return s.parent.equals(parent) && s.dataType.equals(dataType);
        } else {
            return false;
        }
    }

}
