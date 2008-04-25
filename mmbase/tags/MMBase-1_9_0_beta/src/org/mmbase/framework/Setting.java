/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.DataTypeReader;
import org.mmbase.datatypes.util.xml.DependencyException;
import org.mmbase.util.LocalizedString;
import org.w3c.dom.Element;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Representation of {@link Component} related settings. 
 * These settings can be defined in their proper Component XML.
 *
 * @todo The <em>values</em> of the settings can still only be set in memory and in the component
 * xml. There must be some way to persistify them. There should also be a editor in the admin pages.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Setting.java,v 1.7 2008-04-25 14:31:39 andre Exp $
 * @since MMBase-1.9
 */
public class Setting<C> {

    private static final Logger log = Logging.getLoggerInstance(Setting.class);
    protected final DataTypeCollector dataTypeCollector = new DataTypeCollector(new Object());

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
        BasicDataType dt;
        try {
            dt = DataTypeReader.readDataType(dataTypeElement, base, dataTypeCollector).dataType;
        } catch (DependencyException de) {
            dt = de.fallback();
        }
        dataType = dt;
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
    /**
     *
     */
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
