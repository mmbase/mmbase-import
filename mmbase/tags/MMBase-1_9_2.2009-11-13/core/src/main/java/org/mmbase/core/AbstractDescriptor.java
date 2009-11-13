/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.core;

import java.util.*;
import org.mmbase.util.PublicCloneable;
import org.mmbase.bridge.Descriptor;
import org.mmbase.util.LocalizedString;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id$
 */

abstract public class AbstractDescriptor implements Descriptor, PublicCloneable<AbstractDescriptor> {

    protected String key; // clone sucks

    protected LocalizedString description; // clone sucks. so it cannot be final
    protected LocalizedString guiName;

    protected AbstractDescriptor() {}

    /**
     * Create a data type object
     * @param name the name of the data type
     */
    protected AbstractDescriptor(String name) {
        key = name;
        guiName = new LocalizedString(key);
        description = new LocalizedString(key);
    }

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param descriptor
     */
    protected AbstractDescriptor(String name, Descriptor descriptor, boolean cloneDataForRewrite) {
        key = name;
        if (cloneDataForRewrite) {
            description = (LocalizedString)descriptor.getLocalizedDescription().clone();
            guiName = (LocalizedString)descriptor.getLocalizedGUIName().clone();
        } else {
            description = descriptor.getLocalizedDescription();
            guiName = descriptor.getLocalizedGUIName();
        }
    }

    protected AbstractDescriptor(String name, Descriptor descriptor) {
        this(name, descriptor, true);
    }


    /**
     * The locale which must be used if no locale is specified.
     * The default implementation returns {@link LocalizedString#getDefault}
     * This method can be overriden if another more logical default is
     * available. E.g. in BasicField the locale of the current cloud is returned.
     * @since MMBase-1.8.1
     */
    protected Locale getDefaultLocale() {
        return LocalizedString.getDefault();
    }

    /**
     * Returns the name or 'key' of this descriptor.
     * @return the name as a String
     */
    public String getName() {
        return key;
    }

    public String getDescription(Locale locale) {
        return description.get(locale == null ? getDefaultLocale() : locale);
    }

    public String getDescription() {
        return getDescription(getDefaultLocale());
    }

    public LocalizedString getLocalizedDescription() {
        return description;
    }
    protected void setLocalizedDescription(LocalizedString description) {
        this.description = description;
    }
    protected void setLocalizedGUIName(LocalizedString value) {
        guiName = value;
    }


    public void setDescription(String desc, Locale locale) {
        description.set(desc, locale);
    }

    public void setDescription(String desc) {
        setDescription(desc, getDefaultLocale());
    }


    /**
     * Retrieve the GUI name of the field depending on specified langauge.
     * If the language is not available, the "en" value is returned instead.
     * If that one is unavailable the internal fieldname is returned.
     * @return the GUI Name
     */
    public String getGUIName(Locale locale) {
        return guiName.get(locale == null ? getDefaultLocale() : locale);
    }

    /**
     * Retrieve the GUI name of the field.
     * If possible, the "en" value is returned.
     * If that one is unavailable the internal fieldname is returned.
     * @return the GUI Name
     */
    public String getGUIName() {
        return getGUIName(getDefaultLocale());
    }

    public void setGUIName(String g, Locale locale) {
        guiName.set(g, locale);
    }

    public void setGUIName(String g) {
        setGUIName(g, getDefaultLocale());
    }

    public LocalizedString getLocalizedGUIName() {
        return guiName;
    }

    public String toString() {
        return key;
    }

    public AbstractDescriptor clone()  {
        return clone(getName() + ".clone");
    }

    public AbstractDescriptor clone(String name) {
        try {
            AbstractDescriptor clone = (AbstractDescriptor)super.clone();
            clone.description = description.clone();
            clone.guiName = guiName.clone();
            if (name != null) {
                clone.key = name;
                clone.description.setKey(name);
                clone.guiName.setKey(name);
            }
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

}
