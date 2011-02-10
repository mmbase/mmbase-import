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
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Rico Jansen
 * @version $Id$
 */
class TransactionResolver {
    private static final Logger log = Logging.getLoggerInstance(TransactionResolver.class);
    private final MMBase mmbase;

    // Using predicatable iteration order to ensure that object which are created first in the transaction also receive the lowest number
    private static final Comparator<MMObjectNode> COMP = new Comparator<MMObjectNode>() {
        @Override
        public int compare(MMObjectNode o1, MMObjectNode o2) {
            if (o1 == null || o2 == null) return 0;
            return o1.getStringValue("_number").compareTo(o2.getStringValue("_number"));
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
    };

    TransactionResolver(MMBase mmbase) {
        this.mmbase = mmbase;
    }



    /**
     * Given a map where the keys are temporary identifiers, sets the values to actual new node
     * numbers, unless this was already done.
     * The numbers map constains as key:
     * - a negative number for non existing nodes
     * - a string ending in the real node for existing nodes

     */
    private void getNewNumbers(final Map<String, Integer> numbers) {
        // Get the numbers
        for (Map.Entry<String, Integer> numberEntry : numbers.entrySet()) {
            Integer num = numberEntry.getValue();
            if (num == null || num.intValue() == -1) {
                int newNumber = mmbase.getStorageManager().createKey();
                if (log.isDebugEnabled()) {
                    log.debug("" + numberEntry.getKey() + " -> " + newNumber);
                }
                numberEntry.setValue(newNumber);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("TransactionResolver -  numbers: " + numbers);
        }
    }

    private void setNewNumbers(final Map<MMObjectNode, Collection<String>> nnodes,
                               final Map<String, Integer> numbers) {
        // put numbers in the right place
        for (Map.Entry<MMObjectNode, Collection<String>> nnodeEntry : nnodes.entrySet()) {
            MMObjectNode node = nnodeEntry.getKey();
            Collection<String> changedFields = nnodeEntry.getValue();

            for (String field : changedFields) {
                String tmpField = "_" + field;
                String key = node.getStringValue(tmpField);
                Integer number = numbers.get(key);
                if (number != null) {
                    node.setValue(field, number.intValue());
                }
            }
        }
    }

    /**
     * If you made a relation to a new node, and then delete the new node. The relation cannot be made any more.
     * The 'deleteRelations' code in bridge does not work for new nodes. For now, simply implicelty drop these kind of dangling relationss too.
     * @since MMBase-1.9.2
     */
    private void ditchDanglingRelations(final Collection<MMObjectNode> nodes) throws TransactionManagerException {

        synchronized(nodes) {

            for (MMObjectNode node : nodes) {
                MMObjectBuilder bul = mmbase.getMMObject(node.getName());
                if (bul instanceof InsRel) {
                    if (node.isNull("snumber") || node.isNull("dnumber")) {
                        log.debug("Will drop unresolved relation " + node);
                        node.setValue("_exists", TransactionManager.Exists.NOLONGER.toString());
                        node.setValue("number", -1);
                    }
                }
            }
        }
    }

    /**
     * Checks whether all NODE field are indeed filled now, which would mean that this transaction
     * was indeed sucessfully resolved now.
     */
    private void check(final Collection<MMObjectNode> nodes) throws TransactionManagerException {

        // Check now whether resolving was completely successfull
        synchronized(nodes) {
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

                                if (key != null && key.length() > 0 && TransactionManager.Exists.toExists(node.getStringValue("_exists")) != TransactionManager.Exists.NOLONGER) {
                                    throw new TransactionManagerException("For node " + node + " and field " + field + ". Found value for " + tmpField + ": " + key + ". Should be empty.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Result a transaction. ie. resolves all 'node' fields to actual number wich will be committed
     * to the database
     *
     * @throws TransactionManagerException if the transactiosn could not be successfully completely resolved.
    */
    Map<String, Integer> resolve(final Collection<MMObjectNode> nodes) throws TransactionManagerException {
        final Map<String, Integer> numbers = new TreeMap<String, Integer>(); /* Temp key -> Real node number */
        final Map<MMObjectNode, Collection<String>> nnodes  = new TreeMap<MMObjectNode, Collection<String>>(COMP); /* MMObjectNode --> List of changed fields */



        // Find all unique keys and store them in a map to remap them later
        // Also store the nodes with which fields uses them.
        synchronized(nodes) {
            for (MMObjectNode node : nodes) {
                MMObjectBuilder bul = mmbase.getBuilder(node.getName());
                if (log.isDebugEnabled()) {
                    log.debug("TransactionResolver - builder " + node.getName() + " builder " + bul);
                }
                if (Boolean.TRUE.equals(node.getValue(MMObjectBuilder.TMP_FIELD_RESOLVED))) {
                    log.debug("Node was resolved already");
                    continue;
                }
                final TransactionManager.Exists exists = TransactionManager.Exists.toExists(node.getStringValue(MMObjectBuilder.TMP_FIELD_EXISTS));
                for (CoreField fd : bul.getFields()) {
                    int dbtype = fd.getType();
                    if (log.isDebugEnabled()) {
                        log.debug("TransactionResolver - type " + dbtype + "," + fd.getName() + "," + fd.getState());
                    }
                    if (dbtype == Field.TYPE_INTEGER || dbtype == Field.TYPE_NODE) {
                        if (fd.inStorage()) {
                            // Database field of type integer
                            String field = fd.getName();
                            String tmpField = "_" + field;
                            if (node.getDBState(tmpField) == Field.STATE_VIRTUAL) {

                                if (node.isNull(field)) {
                                    if (! node.isNull(tmpField)) {
                                        String key = node.getStringValue(tmpField);
                                        log.debug("TransactionResolver - key,field " + field + " - " + key);
                                        // keep node + field to change
                                        Collection<String> changedFields = nnodes.get(node);
                                        if (changedFields != null) {
                                            changedFields.add(field);
                                        } else {
                                            changedFields = new ArrayList<String>();
                                            changedFields.add(field);
                                            nnodes.put(node, changedFields);
                                        }
                                        if (field.equals("number")) {
                                            if (exists == TransactionManager.Exists.NOLONGER) {
                                                numbers.remove(key);
                                            } else {
                                                if (! numbers.containsKey(key)) {
                                                    numbers.put(key, null);                                                }

                                            }
                                        }
                                    } else if (log.isDebugEnabled()) {
                                        log.debug("TransactionResolver - Can't find key for field " + tmpField + " node " + node + " (warning)");
                                    }
                                    if (field.equals("number") && exists != TransactionManager.Exists.NOLONGER) {
                                        node.storeValue(MMObjectBuilder.TMP_FIELD_EXISTS, TransactionManager.Exists.NO.toString());
                                    }
                                } else {
                                    // Key is already set
                                    int ikey = node.getIntValue(field);
                                    log.debug("TransactionResolver - Key for value " + field + " is already set " + ikey);
                                    // Mark it as existing
                                    if (field.equals("number")) {
                                        // test for remove here
                                        if (exists != TransactionManager.Exists.NOLONGER) {
                                            node.storeValue(MMObjectBuilder.TMP_FIELD_EXISTS, TransactionManager.Exists.YES.toString());
                                            String key = node.getStringValue(tmpField);
                                            if (key != null) {
                                                numbers.put(key, ikey);
                                            } else if (log.isDebugEnabled()) {
                                                log.debug("TransactionResolver - Can't find key for field " + tmpField + " node " + node);
                                            }
                                        } else {
                                        }
                                    }
                                }
                            } else {
                                log.debug("TransctionResolver - DBstate for " + tmpField + " is not VIRTUAL but is " + org.mmbase.core.util.Fields.getStateDescription(node.getDBState(tmpField)));
                            }
                        }
                    }
                }
                node.storeValue(MMObjectBuilder.TMP_FIELD_RESOLVED, Boolean.TRUE);

            }

            if (log.isDebugEnabled()) {
                log.debug("TransactionResolver - nnodes " + nnodes);
            }


            getNewNumbers(numbers);

            assert numbers != null;
            setNewNumbers(nnodes, numbers);


            ditchDanglingRelations(nodes);


            check(nodes);

        }
        return numbers;
    }
}

