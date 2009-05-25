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
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.w3c.dom.Element;
import java.util.*;

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
 * @version $Id$
 * @since MMBase-1.9
 */
public class Setting<C> {

    private static final Logger log = Logging.getLoggerInstance(Setting.class);
    protected final DataTypeCollector dataTypeCollector = new DataTypeCollector(new Object());

    private final String name;
    private final LocalizedString description;
    private final Component parent;
    private final org.mmbase.datatypes.DataType<C> dataType;


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
            de.setCollector(dataTypeCollector);
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
    public org.mmbase.datatypes.DataType<C> getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return "Setting_" + getName() + " " + getDataType();
    }

    @Override
    public int hashCode() {
        return 13 * parent.hashCode() + dataType.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Setting) {
            Setting s = (Setting) o;
            return s.parent.equals(parent) && s.dataType.equals(dataType);
        } else {
            return false;
        }
    }

    public static class DataType extends org.mmbase.datatypes.StringDataType {

        private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

        private final String componentField  = "component";
        /**
         * Constructor for string data type.
         * @param name the name of the data type
         */
        public DataType(String name) {
            super(name);
        }

        @Override
        public Iterator<Map.Entry<String, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {

            final Iterator<Setting<?>> iterator;
            if (node != null) {
                iterator = ComponentRepository.getInstance().toMap().get(node.getStringValue(componentField)).getSettings().iterator();
            } else {
                ChainedIterator chain = new ChainedIterator<Setting<?>>();
                for (Component comp : ComponentRepository.getInstance().toMap().values()) {
                    chain.addIterator(comp.getSettings().iterator());
                }
                iterator = chain;
            }
            return new Iterator<Map.Entry<String, String>> () {

                public boolean hasNext() {
                    return iterator.hasNext();
                }
                public Map.Entry<String, String> next() {
                    String val = iterator.next().getName();
                    return new Entry<String, String>(val, val);
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

    }


}
