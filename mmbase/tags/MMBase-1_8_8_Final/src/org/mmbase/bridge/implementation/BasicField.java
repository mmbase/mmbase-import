/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.*;
import org.mmbase.core.AbstractField;
import org.mmbase.core.CoreField;
import org.mmbase.util.LocalizedString;
import java.util.Collection;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicField extends AbstractField implements Field {

    private final NodeManager nodeManager;
    protected final CoreField coreField;

    public BasicField(Field field, NodeManager nodeManager) {
        super(field.getName(), field);
        this.nodeManager = nodeManager;
        if (field instanceof CoreField) {
            this.coreField = (CoreField) field;
        } else {
            this.coreField = new CoreField(field);
        }
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public int getSearchPosition(){
        return coreField.getSearchPosition();
    }

    public int getListPosition(){
        return coreField.getListPosition();
    }

    public int getEditPosition(){
        return coreField.getEditPosition();
    }

    public int getStoragePosition(){
        return coreField.getStoragePosition();
    }

    public Collection validate(Object value) {
        Collection errors = getDataType().validate(value, null, this);
        return LocalizedString.toStrings(errors, getNodeManager().getCloud().getLocale());
    }

    public int getMaxLength() {
        return coreField.getMaxLength();
    }


    protected java.util.Locale getDefaultLocale() {
        return nodeManager.getCloud().getLocale();
    }

    // deprecated methods
    public String getGUIType() {
        return coreField.getGUIType();
    }

}
