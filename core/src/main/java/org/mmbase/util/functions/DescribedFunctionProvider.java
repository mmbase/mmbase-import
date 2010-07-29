/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.functions;

import java.util.*;

import org.mmbase.bridge.Descriptor;
import org.mmbase.util.LocalizedString;

/**
 * A described function provider maintains a set of {@link Function} objects, and also contains functionality to add
 * gui information to the provider itself.
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 *
 * @version $Id$
 */
public abstract class DescribedFunctionProvider extends FunctionProvider implements Descriptor {

    /**
     * Name or key of the provider.
     */
    protected String name;

    /**
     * Descriptions per locale
     */
    protected LocalizedString description;

    /**
     *(GUI) names per locale
     */
    protected LocalizedString guiName;

    protected DescribedFunctionProvider() {
        super();
        setDescription("");
    }

    /**
     * Create a described function provider
     * @param name the name of the function provider
     */
    protected DescribedFunctionProvider(String name) {
        super();
        this.name = name;
        setGUIName(name);
        setDescription("");
    }


    /**
     * Returns the name or 'key' of this descriptor.
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * @deprecated
     */
    public final void setName(String n) {
        if (n == null) throw new IllegalArgumentException();
        this.name = n;
        setGUIName(name);
    }


    /**
     * The locale which must be used if no locale is specified.
     * The default implementation returns <code>null</code>.
     * This method can be overriden if another more logical default is
     * available. E.g. in BasicField the locale of the current cloud is returned.
     * @since MMBase-1.8.1
     */
    protected Locale getDefaultLocale() {
        return LocalizedString.getDefault();
    }

    public String getDescription(Locale locale) {
        if (description == null) description = new LocalizedString(name);
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
        if (description == null) description = new LocalizedString("");
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
        if (guiName == null) guiName = new LocalizedString(name);
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
        if (guiName == null) guiName = new LocalizedString(name);
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

}
