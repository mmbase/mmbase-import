/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.core;

import java.util.*;
import org.mmbase.bridge.Descriptor;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id$
 */

abstract public class AbstractDescriptor implements Descriptor, Cloneable {

    private static final Logger log = Logging.getLoggerInstance(AbstractDescriptor.class);

    protected String key;
    protected LocalizedString description;
    protected LocalizedString guiName;

    protected AbstractDescriptor() {}

    /**
     * Create a data type object
     * @param name the name of the data type
     */
    protected AbstractDescriptor(String name) {
        key = name;
        setGUIName(name);
        setDescription("");
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
            description = (LocalizedString)descriptor.getLocalizedDescription();
            guiName = (LocalizedString)descriptor.getLocalizedGUIName();
        }
    }

    protected AbstractDescriptor(String name, Descriptor descriptor) {
        this(name, descriptor, true);
    }


    /**
     * The locale wihch must be used if no locale is specified .  Returns <code>null</code> for the
     * defaul of this. This method can be overriden if another more logical default is
     * available. E.g. in BasicField, where the locale of the current cloud is returned here.
     * @since MMBase-1.8.1
     */
    protected Locale getDefaultLocale() {
        return null;
    }

    /**
     * Returns the name or 'key' of this data type.
     * @return the name as a String
     */
    public String getName() {
        return key;
    }

    public String getDescription(Locale locale) {
        if (description == null) description = new LocalizedString(key);
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

    public void setDescription(String desc, Locale locale) {
        if (description == null) description = new LocalizedString(key);
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
        if (guiName == null) guiName = new LocalizedString(key);
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
        if (guiName == null) guiName = new LocalizedString(key);
        guiName.set(g, locale);
    }

    public void setGUIName(String g) {
        setGUIName(g, getDefaultLocale());
    }

    public LocalizedString getLocalizedGUIName() {
        return guiName;
    }

    protected void setLocalizedGUIName(LocalizedString value) {
        guiName = value;
    }

    public String toString() {
        return key;
    }

    public Object clone() throws CloneNotSupportedException {
        return clone(getName() + ".clone");
    }

    public Object clone(String name) throws CloneNotSupportedException {
        AbstractDescriptor clone = (AbstractDescriptor)super.clone();
        clone.description = (LocalizedString)description.clone();
        clone.guiName = (LocalizedString)guiName.clone();
        if (name != null) {
            clone.key = name;
            clone.description.setKey(name);
            clone.guiName.setKey(name);
        }
        return clone;
    }

}
