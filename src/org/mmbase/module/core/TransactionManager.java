/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.security.*;

/**
 * @author Rico Jansen
 * @version $Id: TransactionManager.java,v 1.25 2003-11-19 13:03:10 pierre Exp $
 */
public class TransactionManager implements TransactionManagerInterface {

    private static Logger log = Logging.getLoggerInstance(TransactionManager.class.getName());

    public static final String EXISTS_NO="no";
    public static final int I_EXISTS_NO=0;
    public static final String EXISTS_YES="yes";
    public static final int I_EXISTS_YES=1;
    public static final String EXISTS_NOLONGER="nolonger";
    public static final int I_EXISTS_NOLONGER=2;

    private TemporaryNodeManagerInterface tmpNodeManager;
    private MMBaseCop  mmbaseCop = null;
    private MMBase mmbase;
    protected Hashtable transactions=new Hashtable();
    protected TransactionResolver transactionResolver;

    public TransactionManager(MMBase mmbase,TemporaryNodeManagerInterface tmpn) {
        this.mmbase=mmbase;
        this.tmpNodeManager=tmpn;
        transactionResolver=new TransactionResolver(mmbase);

        mmbaseCop = mmbase.getMMBaseCop();

    }

    public String create(Object user,String transactionname)
        throws TransactionManagerException {
        if (!transactions.containsKey(transactionname)) {
            Vector v=new Vector();
            transactions.put(transactionname,v);
        } else {
            throw new TransactionManagerException("transaction already exists");
        }
        if (log.isDebugEnabled()) {
            log.debug("create transaction for "+transactionname);
        }
        return transactionname;
    }

    public String addNode(String transactionname,String owner,String tmpnumber)
        throws TransactionManagerException {
        MMObjectNode node;
        Vector v;

        v=(Vector)transactions.get(transactionname);
        node=tmpNodeManager.getNode(owner,tmpnumber);
        if (node!=null) {
            if (v==null) {
                v=new Vector();
                v.addElement(node);
                transactions.put(transactionname,v);
            } else {
                int n;
                n=v.indexOf(node);
                if (n==-1) {
                    v.addElement(node);
                } else {
                    throw new TransactionManagerException(
                        "node not added as it was already in the transaction");
                }
            }
        } else {
            throw new TransactionManagerException(
                        "System error: Can't add node as it doesn't exist ");
        }
        return tmpnumber;
    }

    public String removeNode(String transactionname,String owner,String tmpnumber)
        throws TransactionManagerException {
        MMObjectNode node;
        Vector v;

        v=(Vector)transactions.get(transactionname);
        node=tmpNodeManager.getNode(owner,tmpnumber);
        if (node!=null) {
            if (v!=null) {
                int n;
                n=v.indexOf(node);
                if (n>=0) {
                    v.removeElementAt(n);
                } else {
                    throw new TransactionManagerException(
                        "node is not in transaction ");
                }
            } else {
                throw new TransactionManagerException(
                        "transaction doesn't exist ");
            }
        } else {
            throw new TransactionManagerException(
                        "System error: node doesn't exist ");
        }
        return tmpnumber;
    }


    public String deleteObject(String transactionname,String owner,String tmpnumber)
        throws TransactionManagerException    {
        MMObjectNode node;
        Vector v;

        v=(Vector)transactions.get(transactionname);
        node=tmpNodeManager.getNode(owner,tmpnumber);
        if (node!=null) {
            if (v!=null) {
                int n;
                n=v.indexOf(node);
                if (n>=0) {
                    // Mark it as to delete
                    node.setValue("_exists",EXISTS_NOLONGER);
                 } else {
                    throw new TransactionManagerException(
                        "node is not in transaction "+transactionname+" ,node "+tmpnumber);
                }
            } else {
                throw new TransactionManagerException(
                        "transaction doesn't exist "+transactionname);
            }
        } else {
            throw new TransactionManagerException(
                        "node doesn't exist "+tmpnumber);
        }
        return tmpnumber;
    }

    public Vector getNodes(Object user,String transactionname) {
        Vector rtn;

        rtn=(Vector)transactions.get(transactionname);
        if (rtn==null) {
            log.warn("getNodes(): can't find transaction "+transactionname);
        }
        return rtn;
    }

    public String cancel(Object user,String transactionname)
            throws TransactionManagerException {
        Vector v=(Vector)transactions.get(transactionname);
        if (v!=null) {
            MMObjectNode node;
            // remove nodes from the temporary node cache
            MMObjectBuilder bul=mmbase.getTypeDef();
            for (int i=0;i<v.size();i++) {
                node=(MMObjectNode)v.elementAt(i);
                bul.removeTmpNode(node.getStringValue("_number"));
            }
            transactions.remove(transactionname);
            if (log.isDebugEnabled()) {
                log.debug("Removed transaction "+transactionname+ "\n   "+v);
            }
        } else {
            throw new TransactionManagerException("transaction unknown");
        }
        return transactionname;
    }


    public String commit(Object user,String transactionname)
        throws TransactionManagerException {
        return commit(user,transactionname,false);
    }

    protected String commit(Object user,String transactionname,boolean debug)
            throws TransactionManagerException {
        Vector v=(Vector)transactions.get(transactionname);
        if (v!=null) {
            try {
                boolean resolved;
                resolved=transactionResolver.resolve(v,debug);
                if (!resolved) {
                    log.error("Can't resolve transaction "+transactionname);
                    log.error("Nodes \n"+v);
                    throw new TransactionManagerException("Can't resolve transaction "+transactionname);
                } else {
                    resolved=performCommits(user,v,debug);
                    if (!resolved) {
                        log.error("Can't commit transaction "+transactionname);
                        log.error("Nodes \n"+v);
                        throw new TransactionManagerException("Can't commit transaction "+transactionname);
                    } else {
                        if (debug) log.debug("commited "+transactionname);
                    }
                }
            } finally {
                // remove nodes from the temporary node cache
                MMObjectNode node;
                MMObjectBuilder bul=mmbase.getTypeDef();
                for (int i=0;i<v.size();i++) {
                    node=(MMObjectNode)v.elementAt(i);
                    bul.removeTmpNode(node.getStringValue("_number"));
                }
                transactions.remove(transactionname);
            }
        } else {
            throw new TransactionManagerException("transaction unknown");
        }
        return transactionname;
    }

    private final static int UNCOMMITED=0;
    private final static int COMMITED=1;
    private final static int FAILED=2;
    private final static int NODE=3;
    private final static int RELATION=4;

    boolean performCommits(Object user,Vector nodes,boolean debug) {
        if (nodes==null || nodes.size()==0) {
            log.warn("performCommits: Empty list of nodes");
            return true;
        }

        MMObjectNode node;
        MMObjectBuilder bul=mmbase.getMMObject("typedef");
        boolean okay=false;
        int[] nodestate=new int[nodes.size()];
        int[] nodetype=new int[nodes.size()];
        int[] nodeexist=new int[nodes.size()];
        int i,tmpstate;
        String username=findUserName(user),exists;

        if (log.isDebugEnabled()) {
            log.debug("performCommits: checking types and existence");
        }

        for (i=0;i<nodes.size();i++) {
            node=(MMObjectNode)nodes.elementAt(i);
            // Nodes are uncommited by default
            nodestate[i]=UNCOMMITED;

            //check type (relation or normal node)
            if (node.parent instanceof InsRel) {
                nodetype[i]=RELATION;
            } else {
                nodetype[i]=NODE;
            }
            // check if the node already exists
            // note: check on _number seems stupid, no errors?
            // not really needed anyway
            tmpstate=node.getDBState("_number");
            if ((tmpstate==FieldDefs.DBSTATE_UNKNOWN || tmpstate==FieldDefs.DBSTATE_VIRTUAL)) {
                 exists=node.getStringValue("_exists");
                if (exists==null) {
                    // should throw an exception, as it breaks the code furtheron...
                    log.error("performCommits: exists field does not exist "+node);
                } else if (exists.equals(EXISTS_NO)) {
                    nodeexist[i]=I_EXISTS_NO;
                } else if (exists.equals(EXISTS_YES)) {
                    nodeexist[i]=I_EXISTS_YES;
                } else if (exists.equals(EXISTS_NOLONGER)) {
                    nodeexist[i]=I_EXISTS_NOLONGER;
                } else {
                    // should throw an exception, as it breaks the code furtheron...
                    log.error("performCommits: invalid value for _exists "+node);
                }
            }
            if (debug) {
                log.debug("node "+i+" type "+nodetype[i]+" , exist "+nodeexist[i]+" node "+node.getStringValue("_number"));
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: commiting nodes");
        }
        // First commit all the NODES
        for (i=0;i<nodes.size();i++) {
            if (nodetype[i]==NODE) {
                node=(MMObjectNode)nodes.elementAt(i);
                switch(nodeexist[i]) {
                    case I_EXISTS_YES:
                        if (!debug) {
                            // use safe commit, which locks the node cache
                            boolean commitOK;
                            if (user instanceof UserContext) {
                                commitOK = node.commit((UserContext)user);
                            } else {
                                commitOK = node.parent.safeCommit(node);
                            }
                            if (commitOK) {
                                nodestate[i]=COMMITED;
                            } else {
                                nodestate[i]=FAILED;
                            }
                        } else {
                            nodestate[i]=COMMITED;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("node "+i+" commit ");
                        }
                        break;
                    case I_EXISTS_NO:
                        if (log.isDebugEnabled()) {
                            log.debug("node "+i+" insert ");
                        }
                        if (!debug) {
                            int insertOK;
                            if (user instanceof UserContext) {
                                insertOK = node.insert((UserContext)user);
                            } else {
                                insertOK = node.parent.safeInsert(node, username);
                            }
                            if (insertOK > 0) {
                                nodestate[i] = COMMITED;
                            } else {
                                nodestate[i] = FAILED;
                                String message = "When this failed, it is possible that the creation of an insrel went right, which leads to a database inconsistency..  stop now.. (transaction 2.0: [rollback?])";
                                log.error(message);
                                throw new RuntimeException(message);
                            }
                        } else {
                            nodestate[i]=COMMITED;
                        }
                        break;
                    case I_EXISTS_NOLONGER:
                        break;
                    default:
                        nodestate[i]=FAILED;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;

                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: commiting relations");
        }
        // Then commit all the RELATIONS
        for (i=0;i<nodes.size();i++) {
            if (nodetype[i]==RELATION) {
                node=(MMObjectNode)nodes.elementAt(i);

                switch(nodeexist[i]) {
                    case I_EXISTS_YES:
                        if (!debug) {
                            boolean commitOK;
                            if (user instanceof UserContext) {
                                commitOK = node.commit((UserContext)user);
                            } else {
                                commitOK = node.parent.safeCommit(node);
                            }
                            if (commitOK) {
                                nodestate[i]=COMMITED;
                            } else {
                                nodestate[i]=FAILED;
                            }
                        } else {
                            nodestate[i]=COMMITED;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("node "+i+" commit ");
                        }
                        break;
                    case I_EXISTS_NO:
                        if (log.isDebugEnabled()) {
                            log.debug("node "+i+" insert ");
                        }
                        if (!debug) {
                            int insertOK;
                            if (user instanceof UserContext) {
                                insertOK = node.insert((UserContext)user);
                            } else {
                                insertOK = node.parent.safeInsert(node, username);
                            }
                            if (insertOK > 0) {
                                nodestate[i] = COMMITED;
                            } else {
                                nodestate[i] = FAILED;
                                String message = "relation failed(transaction 2.0: [rollback?])";
                                log.error(message);
                            }
                        } else {
                            nodestate[i]=COMMITED;
                        }
                        break;
                    case I_EXISTS_NOLONGER:
                        break;
                    default:
                        nodestate[i]=FAILED;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: deleting relations");
        }
        // Then commit all the RELATIONS that must be deleted
        for (i=0;i<nodes.size();i++) {
            if (nodetype[i]==RELATION) {
                node=(MMObjectNode)nodes.elementAt(i);
                switch(nodeexist[i]) {
                    case I_EXISTS_YES:
                        break;
                    case I_EXISTS_NO:
                        break;
                    case I_EXISTS_NOLONGER:
                        if (log.isDebugEnabled()) {
                            log.debug("node "+i+" delete ");
                        }
                        if (!debug) {
                            // no return information
                            if (user instanceof UserContext) {
                                node.remove((UserContext)user);
                            } else {
                                node.parent.removeNode(node);
                            }
                            nodestate[i]=COMMITED;
                        } else {
                            nodestate[i]=COMMITED;
                        }
                        break;
                    default:
                        nodestate[i]=FAILED;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: deleting nodes");
        }
        // Then commit all the NODES that must be deleted
        for (i=0;i<nodes.size();i++) {
            if (nodetype[i]==NODE) {
                node=(MMObjectNode)nodes.elementAt(i);
                switch(nodeexist[i]) {
                    case I_EXISTS_YES:
                        break;
                    case I_EXISTS_NO:
                        break;
                    case I_EXISTS_NOLONGER:
                        if (log.isDebugEnabled()) {
                            log.debug("node "+i+" delete ");
                        }
                        if (!debug) {
                            // no return information
                            if (user instanceof UserContext) {
                                node.remove((UserContext)user);
                            } else {
                                node.parent.removeNode(node);
                            }
                            nodestate[i]=COMMITED;
                        } else {
                            nodestate[i]=COMMITED;
                        }
                        break;
                    default:
                        nodestate[i]=FAILED;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
            }
        }

        // check for failures
        okay=true;
        for (i=0;i<nodes.size();i++) {
            if (nodestate[i]==FAILED) {
                okay=false;
                log.error("Failed node "+((MMObjectNode)nodes.elementAt(i)).toString());
            }
        }
        return okay;
    }

    public String findUserName(Object user) {
        if (user instanceof UserContext) {
            return ((UserContext)user).getIdentifier();
        } else {
            return "";
        }
    }

}
