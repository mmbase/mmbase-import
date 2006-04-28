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
 * @author Rico Jansen
 * @version $Id: TransactionResolver.java,v 1.13 2002-04-17 13:17:43 pierre Exp $
 */
public class TransactionResolver {
    private static Logger log = Logging.getLoggerInstance(TransactionResolver.class.getName());
    private MMBase mmbase;

    public TransactionResolver(MMBase mmbase) {
        this.mmbase=mmbase;
    }

    public boolean resolve(Vector nodes)
        throws TransactionManagerException {
        return resolve(nodes,false);
    }

    public boolean resolve(Vector nodes,boolean debug)
        throws TransactionManagerException {
        Hashtable numbers=new Hashtable();
        Hashtable nnodes=new Hashtable();
        MMObjectNode node;
        Integer neg=new Integer(-1),num;
        MMObjectBuilder bul;
        FieldDefs fd;
        String field,tmpfield,key,exists;
        int state,number,ikey,dbtype,tmpstate;
        Vector v;
        boolean rtn=true;

        // Find all unique keys and store them in a hashtable to remap them later
        // Also store the nodes with which fields uses them.
        for (Enumeration e=nodes.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            bul=mmbase.getMMObject(node.getName());
            log.debug("TransactionResolver - builder "+node.getName()+" builder "+bul);
            for (Enumeration f=bul.getFields().elements();f.hasMoreElements();) {
                fd=(FieldDefs)f.nextElement();
                dbtype=fd.getDBType();
                log.debug("TransactionResolver - type "+dbtype+","+fd.getDBName()+","+fd.getDBState());
                if ((dbtype==FieldDefs.TYPE_INTEGER)||
                    (dbtype==FieldDefs.TYPE_NODE)) {
                    state=fd.getDBState();
                    if (state==FieldDefs.DBSTATE_PERSISTENT || state==FieldDefs.DBSTATE_SYSTEM) {
                        // Database field of type integer
                        field=fd.getDBName();
                        tmpfield="_"+field;
                        tmpstate=node.getDBState(tmpfield);
                        if (tmpstate==FieldDefs.DBSTATE_VIRTUAL) {
                            ikey=node.getIntValue(field);
                            if (ikey<0) {
                                // Key is not set
                                key=node.getStringValue(tmpfield);
                                if (key!=null) {
                                    log.debug("TransactionResolver - key,field "+field+" - "+key);
                                    // keep fieldnumber key
                                    if (!numbers.containsKey(key)) numbers.put(key,neg);
                                    // keep node + field to change
                                    v=(Vector)nnodes.get(node);
                                    if (v!=null) {
                                        v.addElement(field);
                                    } else {
                                        v=new Vector();
                                        v.addElement(field);
                                        nnodes.put(node,v);
                                    }
                                } else {
                                    log.debug("TransactionResolver - Can't find key for field "+tmpfield+" node "+node+" (warning)");
                                }
                                if (field.equals("number")) node.setValue("_exists",TransactionManager.EXISTS_NO);
                            } else {
                                // Key is already set
                                log.debug("TransactionResolver - Key for value "+field+" is already set "+ikey);
                                // Mark it as existing
                                if (field.equals("number")) {
                                    // test for remove here
                                    exists=node.getStringValue("_exists");
                                    if (exists==null || !exists.equals(TransactionManager.EXISTS_NOLONGER)) {
                                        node.setValue("_exists",TransactionManager.EXISTS_YES);
                                    }
                                    key=node.getStringValue(tmpfield);
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
        number=0;
        for (Enumeration e=numbers.keys();e.hasMoreElements();) {
            key=(String)e.nextElement();
            num=(Integer)numbers.get(key);
            if (num.intValue()==neg.intValue()) {
                if (debug) {
                    number++; // get real number later
                } else {
                    number=mmbase.getDBKey();
                }
                numbers.put(key,new Integer(number));
            }
        }
        log.debug("TransactionResolver - numbers "+numbers);

        // put numbers in the right place
        for (Enumeration e=nnodes.keys();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            v=(Vector)nnodes.get(node);
            for (Enumeration f=v.elements();f.hasMoreElements();) {
                field=(String)f.nextElement();
                log.debug("TransactionResolver - Field "+field);
                tmpfield="_"+field;
                key=node.getStringValue(tmpfield);
                log.debug("TransactionResolver - Key "+key);
                number=((Integer)numbers.get(key)).intValue();
                log.debug("TransactionResolver - Number "+number);
                node.setValue(field,number);
            }
        }

        // Verify resolving stage
        for (Enumeration e=nodes.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            bul=mmbase.getMMObject(node.getName());
            for (Enumeration f=bul.getFields().elements();f.hasMoreElements();) {
                fd=(FieldDefs)f.nextElement();
                dbtype=fd.getDBType();
                if ((dbtype==FieldDefs.TYPE_INTEGER)||
                    (dbtype==FieldDefs.TYPE_NODE)) {
                    field=fd.getDBName();
                    number=node.getIntValue(field);
                    if (number==-1) {
                        tmpfield="_"+field;
                        if (node.getDBState(tmpfield)==0) {
                            key=node.getStringValue(tmpfield);
                            if (key!=null && key.length()>0) {
                                rtn=false;
                            }
                        }
                    }
                }
            }
        }
        return rtn;
    }
}
