/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.core.CoreField;
import org.mmbase.storage.Storable;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicField.java,v 1.20 2005-06-28 14:01:41 pierre Exp $
 */
public class BasicField extends AbstractField implements Field {

    // Logger routine
    private static final Logger log = Logging.getLoggerInstance(BasicField.class);

    NodeManager nodeManager = null;
    CoreField coreField = null;

    BasicField(CoreField field, NodeManager nodeManager) {
        super(field.getName(),field);
        this.nodeManager = nodeManager;
        this.coreField = field;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public int getState(){
        return coreField.getState();
    }

    public boolean isUnique(){
        return coreField.isUnique();
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

    // deprecated methods
    public int getMaxLength() {
        return coreField.getSize();
    }

    public String getGUIType() {
        return coreField.getGUIType();
    }

}
