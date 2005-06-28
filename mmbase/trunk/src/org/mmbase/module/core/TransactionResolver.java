/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.MMBaseType;
import org.mmbase.core.CoreField;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * @author Rico Jansen
 * @version $Id: TransactionResolver.java,v 1.16 2005-06-28 14:01:41 pierre Exp $
 */
public class TransactionResolver {
    private static Logger log = Logging.getLoggerInstance(TransactionResolver.class.getName());
    private MMBase mmbase;

    public TransactionResolver(MMBase mmbase) {
        this.mmbase = mmbase;
    }

    public boolean resolve(Collection nodes) throws TransactionManagerException {
        Map numbers = new HashMap();
        Map nnodes = new HashMap();
        boolean success = true;

        // Find all unique keys and store them in a map to remap them later
        // Also store the nodes with which fields uses them.
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            MMObjectNode node = (MMObjectNode)i.next();
            MMObjectBuilder bul = mmbase.getMMObject(node.getName());
            log.debug("TransactionResolver - builder " + node.getName() + " builder " + bul);
            for (Enumeration f = bul.getFields().elements();f.hasMoreElements();) {
                CoreField fd = (CoreField)f.nextElement();
                int dbtype = fd.getDataType().getType();
                log.debug("TransactionResolver - type " + dbtype + "," + fd.getName() + "," + fd.getState());
                if ((dbtype == MMBaseType.TYPE_INTEGER)||
                    (dbtype == MMBaseType.TYPE_NODE)) {
                    int state = fd.getState();
                    if (state == Field.STATE_PERSISTENT || state == Field.STATE_SYSTEM) {
                        // Database field of type integer
                        String field = fd.getName();
                        String tmpfield = "_" + field;
                        if (node.getDBState(tmpfield) == Field.STATE_VIRTUAL) {
                            int ikey = node.getIntValue(field);
                            if (ikey < 0) {
                                // Key is not set
                                String key = node.getStringValue(tmpfield);
                                if (key!=null) {
                                    log.debug("TransactionResolver - key,field " + field + " - " + key);
                                    // keep fieldnumber key
                                    if (!numbers.containsKey(key)) numbers.put(key,new Integer(-1));
                                    // keep node + field to change
                                    Collection changedFields = (Collection)nnodes.get(node);
                                    if (changedFields!=null) {
                                        changedFields.add(field);
                                    } else {
                                        changedFields=new ArrayList();
                                        changedFields.add(field);
                                        nnodes.put(node,changedFields);
                                    }
                                } else {
                                    log.debug("TransactionResolver - Can't find key for field " + tmpfield + " node "+node+" (warning)");
                                }
                                if (field.equals("number")) node.setValue("_exists",TransactionManager.EXISTS_NO);
                            } else {
                                // Key is already set
                                log.debug("TransactionResolver - Key for value " + field + " is already set "+ikey);
                                // Mark it as existing
                                if (field.equals("number")) {
                                    // test for remove here
                                    String exists=node.getStringValue("_exists");
                                    if (exists == null || !exists.equals(TransactionManager.EXISTS_NOLONGER)) {
                                        node.setValue("_exists",TransactionManager.EXISTS_YES);
                                    }
                                    String key=node.getStringValue(tmpfield);
                                    if (key!=null) {
                                        numbers.put( key, new Integer(ikey));
                                    } else {
                                        log.debug("TransactionResolver - Can't find key for field "+tmpfield+" node "+node);
                                    }
                                }
                            }
                        } else {
                            log.debug("TransctionResolver - DBstate for "+tmpfield+" is not set to 0 but is "+node.getDBState(field));
                        }
                    }
                }
            }
        }

        log.debug("TransactionResolver - nnodes "+nnodes);

        // Get the numbers
        for (Iterator i = numbers.entrySet().iterator(); i.hasNext();) {
            Map.Entry numberEntry = (Map.Entry)i.next();
            Object key = numberEntry.getKey();
            Integer num = (Integer)numberEntry.getValue();
            if (num.intValue() == -1) {
                numbers.put(key, new Integer(mmbase.getDBKey()));
            }
        }

        // put numbers in the right place
        for (Iterator i = nnodes.entrySet().iterator(); i.hasNext();) {
            Map.Entry nnodeEntry = (Map.Entry)i.next();
            MMObjectNode node = (MMObjectNode)nnodeEntry.getKey();
            Collection changedFields = (Collection)nnodeEntry.getValue();
            for (Iterator j = changedFields.iterator(); j.hasNext();) {
                String field = (String)j.next();
                String tmpfield = "_"+field;
                String key = node.getStringValue(tmpfield);
                int number = ((Integer)numbers.get(key)).intValue();
                node.setValue(field, number);
            }
        }

        for (Iterator i = nodes.iterator(); i.hasNext();) {
            MMObjectNode node = (MMObjectNode)i.next();
            MMObjectBuilder bul=mmbase.getMMObject(node.getName());
            for (Iterator j = bul.getFields().iterator();j.hasNext();) {
                CoreField fd = (CoreField)j.next();
                int dbtype = fd.getDataType().getType();
                if ((dbtype == MMBaseType.TYPE_INTEGER)||
                    (dbtype == MMBaseType.TYPE_NODE)) {
                    String field = fd.getName();
                    int number = node.getIntValue(field);
                    if (number == -1) {
                        String tmpfield = "_"+field;
                        if (node.getDBState(tmpfield) == 0) {
                            String key = node.getStringValue(tmpfield);
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
