/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.Locale;
import java.util.Collection;
import org.mmbase.util.LocalizedString;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;


/**
 * Wraps another Field (and makes it unmodifiable). You can use this if you want to implement Field, and want to base that
 * implementation on a existing <code>Field</code> instance.
 *
 * To implement a modifiable field, you need to override the setters too.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8.1
 */

public class FieldWrapper implements Field {
    protected final Field field;

    public FieldWrapper(Field field)  {
        this.field = field;
    }
    @Override
    public NodeManager getNodeManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getState() {
        return field.getState();
    }

    @Override
    public DataType<?> getDataType() { return field.getDataType(); }
    @Override
    public boolean isUnique() { return field.isUnique(); }
    @Override
    public boolean hasIndex() { return field.hasIndex(); }
    @Override
    public int getType() {  return field.getType(); }
    @Override
    public int getListItemType() { return field.getListItemType(); }
    @Override
    public int getSearchPosition() { return field.getSearchPosition(); }
    @Override
    public int getListPosition() { return field.getListPosition(); }
    @Override
    public int getEditPosition() { return field.getEditPosition(); }
    @Override
    public int getStoragePosition() { return field.getStoragePosition(); }

    @Override
    @SuppressWarnings("deprecation")
    public String getGUIType() { return field.getGUIType(); }
    @Override
    public boolean isRequired() { return field.isRequired(); }
    @Override
    public int getMaxLength() { return field.getMaxLength(); }
    @Override
    public Collection<String> validate(Object value) { return field.validate(value); }
    @Override
    public boolean isVirtual() { return field.isVirtual(); }
    @Override
    public boolean isReadOnly() { return field.isReadOnly(); }
    @Override
    public String getName() { return field.getName(); }
    @Override
    public String getGUIName() { return field.getGUIName(); }
    @Override
    public String getGUIName(Locale locale) { return field.getGUIName(locale); }
    @Override
    public LocalizedString getLocalizedGUIName() { return field.getLocalizedGUIName(); }
    @Override
    public void setGUIName(String g, Locale locale) { throw new UnsupportedOperationException(); }
    @Override
    public void setGUIName(String g) { throw new UnsupportedOperationException(); }
    @Override
    public LocalizedString getLocalizedDescription() { return field.getLocalizedDescription(); }
    @Override
    public String getDescription(Locale locale) { return field.getDescription(locale); }
    @Override
    public String getDescription() { return field.getDescription(); }
    @Override
    public void setDescription(String description, Locale locale) { throw new UnsupportedOperationException(); }
    @Override
    public void setDescription(String description) { throw new UnsupportedOperationException(); }
    @Override
    public int compareTo(Field f) { return field.compareTo(f); }


    public Field getField() {
        return field;
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + Fields.getStateDescription(getState()) + ":" + getStoragePosition() + ":" + getNodeManager().getName() + ":" + getName();
    }
}
