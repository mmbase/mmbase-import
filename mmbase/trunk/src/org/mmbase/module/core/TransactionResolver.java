/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * The TransactionResolver takes care of generating new id's (number fields and references to those)
 * for all nodes of a transaction. So it is used when committing.
 *
 * @author Rico Jansen 
 * @version $Id: TransactionResolver.java,v 1.14 2004-06-03 16:03:55 michiel Exp $
 */
public class TransactionResolver {
    private static final Logger log = Logging.getLoggerInstance(TransactionResolver.class);
    private MMBase mmbase;

    private static final Integer NEG = new Integer(-1);

    public TransactionResolver(MMBase mmbase) {
        this.mmbase = mmbase;
    }


    /**      
     * Defaulting version of {@link #resolve(Vector, debug)}, with debug=false.
     */
    public final boolean resolve(final  Vector nodes) throws TransactionManagerException {
        return resolve(nodes, false);
    }

    /**
     * Resolves temporary Id's to new real id's, which are created (see {@link MMBase#getDBKey})
     * @param nodes A set of nodes in the transaction. All fields like 'number' and 'snumber' are
     *              resolved to new node numbers.
     * @param debug If this is true, no real database numbers are created, but numbers which start counting at 0.
     * @return true if successfull, and no nodes remain which have fields which are tempary id's.
     */
    public boolean resolve(final Vector nodes, final boolean debug) throws TransactionManagerException { 
        Map numbers = new HashMap(); // temporary id --> real id e.g.: {U-16_-74=1877715, U-16_-73=1877716, U-16_-72=1877717}
        Map nnodes =  new HashMap(); // MMObjectNode --> List of field containing temporary id's, which will be resolved to real id's


        // Find all unique keys and store them in a hashtable to remap them later
        // Also store the nodes with which fields uses them.
        for (Enumeration e = nodes.elements();e.hasMoreElements();) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            MMObjectBuilder bul = node.getBuilder();
            if (log.isDebugEnabled()) {
                log.debug("TransactionResolver - builder " + node.getName() + " builder " + bul);
            }
            for (Enumeration f = bul.getFields().elements(); f.hasMoreElements();) {
                FieldDefs fd = (FieldDefs) f.nextElement();
                int dbtype = fd.getDBType();
                if (log.isDebugEnabled()) {
                    log.debug("TransactionResolver - type " + dbtype+"," + fd.getDBName()+"," + fd.getDBState());
                }
                if ((dbtype == FieldDefs.TYPE_INTEGER)|| (dbtype==FieldDefs.TYPE_NODE)) {
                    int state = fd.getDBState();
                    if (state == FieldDefs.DBSTATE_PERSISTENT || state==FieldDefs.DBSTATE_SYSTEM) {
                        // Database field of type integer
                        String field = fd.getDBName();
                        String tmpfield = "_" + field;
                        if (node.getDBState(tmpfield) == FieldDefs.DBSTATE_VIRTUAL) {
                            int ikey = node.getIntValue(field);
                            if (ikey < 0) {
                                // Key is not set
                                String key = node.getStringValue(tmpfield);
                                if (key != null) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("TransactionResolver - key,field "+field+" - "+key);
                                    }
                                    // keep fieldnumber key
                                    if (!numbers.containsKey(key)) { 
                                        numbers.put(key, NEG);
                                    }
                                    // keep node + field to change
                                    List v = (List)nnodes.get(node);
                                    if (v != null) {
                                        v.add(field);
                                    } else {
                                        v = new ArrayList();
                                        v.add(field);
                                        nnodes.put(node, v);
                                    }
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("TransactionResolver - Can't find key for field " + tmpfield + " node " + node + " (warning)");
                                    }
                                }
                                if (field.equals("number")) {
                                    node.setValue("_exists", TransactionManager.EXISTS_NO);
                                }
                            } else {
                                // Key is already set
                                if (log.isDebugEnabled()) {
                                    log.debug("TransactionResolver - Key for value " + field + " is already set " + ikey);
                                }
                                // Mark it as existing
                                if (field.equals("number")) {
                                    // test for remove here
                                    String exists = node.getStringValue("_exists");
                                    if (exists == null || !exists.equals(TransactionManager.EXISTS_NOLONGER)) {
                                        node.setValue("_exists", TransactionManager.EXISTS_YES);
                                    }
                                    String key = node.getStringValue(tmpfield);
                                    if (key != null) {
                                        numbers.put(key, new Integer(ikey));
                                    } else {
                                        if (log.isDebugEnabled()) {
                                            log.debug("TransactionResolver - Can't find key for field " + tmpfield + " node " + node);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("TransctionResolver - DBstate for "+tmpfield+" is not set to 0 but is "+node.getDBState(field));
                            }
                        }
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("TransactionResolver - nnodes " + nnodes);
        }

        {
            int number = 0;
            // Get the numbers
            for (Iterator i = numbers.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String key = (String) entry.getKey();
                Integer num = (Integer) entry.getValue();
                if (num.intValue() == NEG.intValue()) {
                    if (debug) {
                        number++; // get real number later
                    } else {
                        number = mmbase.getDBKey();
                    }
                    numbers.put(key, new Integer(number));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("TransactionResolver - numbers " + numbers);
            }
        }

        // put numbers in the right place
        for (Iterator i = nnodes.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            MMObjectNode node = (MMObjectNode) entry.getKey();
            List v = (List) entry.getValue();
            for (Iterator f = v.iterator(); f.hasNext();) {
                String field = (String)f.next();
                String key = node.getStringValue("_" + field);
                if (key == null) throw new RuntimeException("Could not find field _" + field + " in node " + node);
                int number=((Integer)numbers.get(key)).intValue();
                if (log.isDebugEnabled()) {
                    log.debug("TransactionResolver - Field " + field);
                    log.debug("TransactionResolver - Key " + key);
                    log.debug("TransactionResolver - Number " + number);
                }
                node.setValue(field, number);
            }
        }

        // Verify resolving stage
        // i.e. check if there are still nodes which have node/integer fields wich are -1.
        for (Enumeration e = nodes.elements();e.hasMoreElements();) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            MMObjectBuilder bul = node.getBuilder();
            for (Enumeration f = bul.getFields().elements(); f.hasMoreElements();) {
                FieldDefs fd = (FieldDefs)f.nextElement();
                int dbtype = fd.getDBType();
                if ((dbtype == FieldDefs.TYPE_INTEGER) || (dbtype == FieldDefs.TYPE_NODE)) {
                    String field = fd.getDBName();
                    int number = node.getIntValue(field);
                    if (number == -1) {
                        String tmpfield = "_" + field;
                        if (node.getDBState(tmpfield) == 0) {
                            String key = node.getStringValue(tmpfield);
                            if (key != null && key.length() > 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true; // verifying succeeded
    }
}
