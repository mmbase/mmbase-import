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
 * @version $Id: TransactionResolver.java,v 1.28 2006-11-11 13:56:32 michiel Exp $
 */
class TransactionResolver {
    private static final Logger log = Logging.getLoggerInstance(TransactionResolver.class);
    private final MMBase mmbase;

    TransactionResolver(MMBase mmbase) {
        this.mmbase = mmbase;
    }

    public boolean resolve(final Collection<MMObjectNode> nodes) {
        Map<String, Integer> numbers = new HashMap(); /* Temp key -> Real node number */
        Map<MMObjectNode, Collection<String>> nnodes  = new HashMap(); /* MMObjectNode --> List of changed fields */
        boolean success = true;

        // Find all unique keys and store them in a map to remap them later
        // Also store the nodes with which fields uses them.
        for (MMObjectNode node : nodes) {
            MMObjectBuilder bul = mmbase.getBuilder(node.getName());
            if (log.isDebugEnabled()) {
                log.debug("TransactionResolver - builder " + node.getName() + " builder " + bul);
            }
            for (CoreField fd : bul.getFields()) {
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
                                    Collection<String> changedFields = nnodes.get(node);
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
                                    node.storeValue(MMObjectBuilder.TMP_FIELD_EXISTS, TransactionManager.EXISTS_NO);
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
                                        node.storeValue(MMObjectBuilder.TMP_FIELD_EXISTS, TransactionManager.EXISTS_YES);
                                    }
                                    String key = node.getStringValue(tmpField);
                                    if (key != null) {
                                        numbers.put(key, ikey);
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
        for (Map.Entry<String, Integer> numberEntry : numbers.entrySet()) {
            String key =   numberEntry.getKey();
            Integer num = numberEntry.getValue();
            if (num == null || num.intValue() == -1) {
                numberEntry.setValue(mmbase.getStorageManager().createKey());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("TransactionResolver -  numbers: " + numbers);
        }


        // put numbers in the right place
        for (Map.Entry<MMObjectNode, Collection<String>> nnodeEntry : nnodes.entrySet()) {
            MMObjectNode node = nnodeEntry.getKey();
            Collection<String> changedFields = nnodeEntry.getValue();
            for (String field : changedFields) {
                String tmpField = "_" + field;
                String key = node.getStringValue(tmpField);
                int number = numbers.get(key);
                node.setValue(field, number);
            }
        }

        for (MMObjectNode node : nodes) {
            MMObjectBuilder bul = mmbase.getMMObject(node.getName());
            for (CoreField fd : bul.getFields()) {
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
