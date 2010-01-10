/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.core.CoreField;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Rico Jansen
 * @version $Id$
 */
public class TransactionResolver {
    private static final Logger log = Logging.getLoggerInstance(TransactionResolver.class);
    private final MMBase mmbase;

    public TransactionResolver(MMBase mmbase) {
        this.mmbase = mmbase;
    }

    public boolean resolve(final Collection nodes) {
        Map numbers = new HashMap(); /* Temp key -> Real node number */
        Map nnodes  = new HashMap(); /* MMObjectNode --> List of changed fields */
        boolean success = true;

        // Find all unique keys and store them in a map to remap them later
        // Also store the nodes with which fields uses them.
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            MMObjectNode node = (MMObjectNode) i.next();
            MMObjectBuilder bul = mmbase.getBuilder(node.getName());
            if (log.isDebugEnabled()) {
                log.debug("TransactionResolver - builder " + node.getName() + " builder " + bul);
            }
            for (Iterator f = bul.getFields().iterator();f.hasNext();) {
                CoreField fd = (CoreField)f.next();
                int dbtype = fd.getType();
                if (log.isDebugEnabled()) {
                    log.debug("TransactionResolver - type " + dbtype + "," + fd.getName() + "," + fd.getState());
                }
                if (dbtype == Field.TYPE_INTEGER || dbtype == Field.TYPE_NODE) {
                    int state = fd.getState();
                    if (fd.inStorage()) {
                        // Database field of type integer 
                        String field = fd.getName();
                        String tmpField = "_" + field;
                        if (node.getDBState(tmpField) == Field.STATE_VIRTUAL) {
                            if (node.isNull(field)) {
                                if (! node.isNull(tmpField)) {
                                    String key = node.getStringValue(tmpField);
                                    log.debug("TransactionResolver - key,field " + field + " - " + key);
                                    // keep fieldnumber key
                                    if (! numbers.containsKey(key)) {
                                        numbers.put(key, null);
                                    }
                                    // keep node + field to change
                                    Collection changedFields = (Collection) nnodes.get(node);
                                    if (changedFields != null) {
                                        changedFields.add(field);
                                    } else {
                                        changedFields = new ArrayList();
                                        changedFields.add(field);
                                        nnodes.put(node, changedFields);
                                    }
                                } else if (log.isDebugEnabled()) {
                                    log.debug("TransactionResolver - Can't find key for field " + tmpField + " node " + node + " (warning)");
                                }
                                if (field.equals("number")) {
                                    node.setValue(MMObjectBuilder.TMP_FIELD_EXISTS, TransactionManager.EXISTS_NO);
                                }
                            } else {
                                // Key is already set
                                int ikey = node.getIntValue(field);
                                log.debug("TransactionResolver - Key for value " + field + " is already set " + ikey);
                                // Mark it as existing
                                if (field.equals("number")) {
                                    // test for remove here
                                    String exists = node.getStringValue(MMObjectBuilder.TMP_FIELD_EXISTS);
                                    if (exists == null || !exists.equals(TransactionManager.EXISTS_NOLONGER)) {
                                        node.setValue(MMObjectBuilder.TMP_FIELD_EXISTS, TransactionManager.EXISTS_YES);
                                    }
                                    String key = node.getStringValue(tmpField);
                                    if (key != null) {
                                        numbers.put(key, new Integer(ikey));
                                    } else if (log.isDebugEnabled()) {
                                        log.debug("TransactionResolver - Can't find key for field " + tmpField + " node " + node);
                                    }
                                }
                            }
                        } else {
                            log.debug("TransctionResolver - DBstate for " + tmpField + " is not VIRTUAL but is " + org.mmbase.core.util.Fields.getStateDescription(node.getDBState(tmpField)));
                        }
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("TransactionResolver - nnodes " + nnodes);
        }

        // Get the numbers
        for (Iterator i = numbers.entrySet().iterator(); i.hasNext();) {
            Map.Entry numberEntry = (Map.Entry)i.next();
            Object key = numberEntry.getKey();
            Integer num = (Integer)numberEntry.getValue();
            if (num == null || num.intValue() == -1) {
                numbers.put(key, new Integer(mmbase.getStorageManager().createKey()));
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("TransactionResolver -  numbers: " + numbers);
        }


        // put numbers in the right place
        for (Iterator i = nnodes.entrySet().iterator(); i.hasNext();) {
            Map.Entry nnodeEntry = (Map.Entry)i.next();
            MMObjectNode node = (MMObjectNode)nnodeEntry.getKey();
            Collection changedFields = (Collection)nnodeEntry.getValue();
            for (Iterator j = changedFields.iterator(); j.hasNext();) {
                String field = (String)j.next();
                String tmpField = "_" + field;
                String key = node.getStringValue(tmpField);
                int number = ((Integer)numbers.get(key)).intValue();
                node.setValue(field, number);
            }
        }

        for (Iterator i = nodes.iterator(); i.hasNext();) {
            MMObjectNode node = (MMObjectNode)i.next();
            MMObjectBuilder bul = mmbase.getMMObject(node.getName());
            for (Iterator j = bul.getFields().iterator();j.hasNext();) {
                CoreField fd = (CoreField)j.next();
                int dbtype = fd.getType();
                if ((dbtype == Field.TYPE_INTEGER)||
                    (dbtype == Field.TYPE_NODE)) {
                    
                    String field = fd.getName();
                    String tmpField = "_" + field;
                    if (node.getDBState(tmpField) == Field.STATE_VIRTUAL) {
                        int number = node.getIntValue(field);
                        if (number == -1) {
                            String key = node.getStringValue(tmpField);
                            if (key != null && key.length() > 0) {
                                success = false;
                            }
                        }
                    }
                }
            }
        }
        return success;
    }
}
