/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.module.core.*;
import org.mmbase.security.*;
import java.util.*;
import org.mmbase.cache.Cache;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The rightsrel relation, connects a 'context' with a 'group'. The
 * 'operation' field then indicates which operation is allowed because
 * of this relation.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: RightsRel.java,v 1.3 2003-07-18 13:40:22 michiel Exp $
 */
public class RightsRel extends InsRel {

    protected static class OperationsCache extends Cache {
        OperationsCache() {
            super(100);
        }
        public String getName()        { return "CCS:SecurityOperations"; }
        public String getDescription() { return "The groups associated with a security operation";}
        
        public Object put(MMObjectNode context, Operation op, List groups) {
            return super.put(op.toString() + context.getNumber(), groups);
        }
        public List get(MMObjectNode context, Operation op) {
            return (List) super.get(op.toString() + context.getNumber());
        }
        
    };

    protected static OperationsCache operationsCache = new OperationsCache();


    public boolean init() {
        operationsCache.putCache();
        CacheInvalidator.getInstance().addCache(operationsCache);
        mmb.addLocalObserver(getTableName(), CacheInvalidator.getInstance());
        mmb.addRemoteObserver(getTableName(), CacheInvalidator.getInstance());
        return super.init();
    }


    /**
     * The field of this relations which present the operation.
     */
    public static String OPERATION_FIELD = "operation";

    private static Logger log = Logging.getLoggerInstance(RightsRel.class.getName());

    /**
     * Util method to get this Builder.
     *
     * @return The RightsRel MMObjectBuilder
     */
    public static RightsRel getBuilder() {
        return (RightsRel) MMBase.getMMBase().getBuilder("rightsrel");
    }

    /**
     * @return a List of all groups which are allowed to for operation operation.
     */
    public List getGroups(MMObjectNode contextNode, Operation operation) {
        
        
        List found = operationsCache.get(contextNode, operation);
        if (found == null) {
            found = new ArrayList();
            for(Enumeration enumeration = contextNode.getRelations(); enumeration.hasMoreElements();) {
                // needed to get the correct type of builder!!
                MMObjectNode relation = getNode(((MMObjectNode) enumeration.nextElement()).getNumber());
                if (relation.parent instanceof RightsRel) {
                    String nodeOperation = relation.getStringValue(OPERATION_FIELD);
                    if (nodeOperation.equals(operation.toString()) || nodeOperation.equals("all")) {
                        int source      = relation.getIntValue("snumber");
                        MMObjectNode destination = relation.getNodeValue("dnumber");
                        if (source == contextNode.getNumber()) {
                            if (log.isDebugEnabled()) {
                                log.debug("found group # " + destination.getNumber() + " for operation" + operation + "(because " + nodeOperation + ")");
                            }
                            found.add(destination);
                        } else {
                            log.warn("source was not the same as out contextNode");
                        }
                    }  
                } 
            }
            log.debug("found groups for operation " + operation + " " + found);
            operationsCache.put(contextNode, operation, found);
        }
        return found;
    }


    // inherited
    public String getGUIIndicator(MMObjectNode node) {
        return node.getStringValue(OPERATION_FIELD) + " " + super.getGUIIndicator(node);
    }

    /**
     * Operation defaults to 'read'.
     */
    public void setDefaults(MMObjectNode node) {
        // default -> read
        node.setValue(OPERATION_FIELD, Operation.READ.toString());
        super.setDefaults(node);
    }

    /**
     * Check on possible values for operation.
     */
    public boolean setValue(MMObjectNode node, String fieldName) {
        // most situations, handle in inherited class
        if (!fieldName.equals(OPERATION_FIELD)) super.setValue(node, fieldName);

        // mm: not sure I like this.
        String value = (String) node.values.get(OPERATION_FIELD);
        if (value == null)        return true;
        if (value.equals("all"))  return true;
        if (value.equals(Operation.READ.toString())) return true;
        if (value.equals(Operation.WRITE.toString())) return true;
        if (value.equals(Operation.CREATE.toString())) return true;
        if (value.equals(Operation.CHANGE_RELATION.toString())) return true;
        if (value.equals(Operation.DELETE.toString())) return true;
        if (value.equals(Operation.CHANGECONTEXT.toString())) return true;
        String msg = 
            "field with name operation must contain a valid opertion( value was: '" + value + "')\n" +
            "valid operations are: all, " + Operation.READ + ", " + Operation.WRITE + ", " + Operation.CREATE +
            ", " + Operation.CHANGE_RELATION + ", " + Operation.DELETE + ", " + Operation.CHANGECONTEXT + ", ";
        throw new RuntimeException(msg);
    }
}
