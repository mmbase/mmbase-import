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
 * @version $Id: TransactionManager.java,v 1.19 2001-08-24 07:31:34 pierre Exp $
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
        Vector v;

        v=(Vector)transactions.get(transactionname);
        if (v!=null) {
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
        Vector v;
        String s;

        v=(Vector)transactions.get(transactionname);
        if (v!=null) {
            boolean resolved;
            resolved=transactionResolver.resolve(v,debug);
            if (!resolved) {
                log.warn("Can't resolve transaction "+transactionname);
                log.warn("Nodes \n"+v);
            } else {
                resolved=performCommits(user,v,debug);
                if (!resolved) {
                    log.warn("Can't commit transaction "+transactionname);
                    log.warn("Nodes \n"+v);
                } else {
                    if (debug) log.debug("commited "+transactionname);
                    // if (!debug) transactions.remove(transactionname);
                }
            }
            transactions.remove(transactionname);
        } else {
            log.warn("Can't find transaction "+transactionname);
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
            return false;
        }

        MMObjectNode node;
        MMObjectBuilder bul=mmbase.getMMObject("typedef");
        boolean okay=false,res=false;
        int[] nodestate=new int[nodes.size()];
        int[] nodetype=new int[nodes.size()];
        int[] nodeexist=new int[nodes.size()];
        int i,tmpstate;
        String username=findUserName(user),exists;


        // Nodes are uncommited by default
        for (i=0;i<nodes.size();i++) nodestate[i]=UNCOMMITED;

        if (log.isDebugEnabled()) {
            log.debug("performCommits: checking types");
        }
        // check for type (relation or normal node)
        for (i=0;i<nodes.size();i++) {
            node=(MMObjectNode)nodes.elementAt(i);
            // This should be easier
            if (mmbase.getMMObject(node.getName()) instanceof InsRel) {
                nodetype[i]=RELATION;
            } else {
                nodetype[i]=NODE;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: checking existence");
        }
        // check if they alreay exist (aka use update vs insert)
        for (i=0;i<nodes.size();i++) {
            node=(MMObjectNode)nodes.elementAt(i);
            tmpstate=node.getDBState("_number");
            if ((tmpstate==FieldDefs.DBSTATE_UNKNOWN || tmpstate==FieldDefs.DBSTATE_VIRTUAL)) {
                 exists=node.getStringValue("_exists");
                if (exists==null) {
                    log.warn("performCommits: exists field does not exist "+node);
                } else if (exists.equals(EXISTS_NO)) {
                    nodeexist[i]=I_EXISTS_NO;
                } else if (exists.equals(EXISTS_YES)) {
                    nodeexist[i]=I_EXISTS_YES;
                } else if (exists.equals(EXISTS_NOLONGER)) {
                    nodeexist[i]=I_EXISTS_NOLONGER;
                } else {
                    log.warn("performCommits: invalid value for _exists "+node);
                }
            }
        }

        if (debug) {
            for (i=0;i<nodes.size();i++) {
                node=(MMObjectNode)nodes.elementAt(i);
                log.debug("node "+i+" type "+nodetype[i]+" , exist "+nodeexist[i]+" node "+node.getStringValue("_number"));
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: deleting relations");
        }
        // First commit all the RELATIONS that must be deleted
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
                            node.parent.removeNode(node);
                            if (user instanceof UserContext) {
                                mmbaseCop.getAuthorization().remove((UserContext)user, node.getNumber());
                            }
                            res=true;
                        } else {
                            res=true;
                        }
                        break;
                    default:
                        res=false;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
                if (res) {
                    nodestate[i]=COMMITED;
                } else {
                    nodestate[i]=FAILED;
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
                            node.parent.removeNode(node);
                            if (user instanceof UserContext) {
                                mmbaseCop.getAuthorization().remove((UserContext)user,node.getNumber());
                            }
                            res=true;
                        } else {
                            res=true;
                        }
                        break;
                    default:
                        res=false;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
                if (res) {
                    nodestate[i]=COMMITED;
                } else {
                    nodestate[i]=FAILED;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("performCommits: commiting nodes");
        }
        // Then commit all the NODES
        for (i=0;i<nodes.size();i++) {
            if (nodetype[i]==NODE) {
                node=(MMObjectNode)nodes.elementAt(i);
                switch(nodeexist[i]) {
                    case I_EXISTS_YES:
                        if (!debug) {
                            res=node.commit();
                            if (user instanceof UserContext) {
                                mmbaseCop.getAuthorization().update((UserContext)user,node.getNumber());
                            }
                        } else {
                            res=true;
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
                            if (username.length()>1) {
                                res=node.insert(username)!=-1;
                            } else {
                                res=node.insert(node.getStringValue("owner"))!=-1;
                            }
                            if (user instanceof UserContext) {
                                mmbaseCop.getAuthorization().create((UserContext)user,node.getNumber());
                            }
                        } else {
                            res=true;
                        }
                        break;
                    case I_EXISTS_NOLONGER:
                        break;
                    default:
                        res=false;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
                if (res) {
                    nodestate[i]=COMMITED;
                } else {
                    nodestate[i]=FAILED;
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
                            res=node.commit();
                            if (user instanceof UserContext) {
                                mmbaseCop.getAuthorization().update((UserContext)user,node.getNumber());
                            }
                        } else {
                            res=true;
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
                            if (username.length()>1) {
                                res=node.insert(username)!=-1;
                            } else {
                                res=node.insert(node.getStringValue("owner"))!=-1;
                            }
                            if (user instanceof UserContext) {
                                mmbaseCop.getAuthorization().create((UserContext)user,node.getNumber());
                            }
                        } else {
                            res=true;
                        }
                        break;
                    case I_EXISTS_NOLONGER:
                        break;
                    default:
                        res=false;
                        log.warn("performCommits invalid exists value "+nodeexist[i]);
                        break;
                }
                if (res) {
                    nodestate[i]=COMMITED;
                } else {
                    nodestate[i]=FAILED;
                }
            }
        }
        // check for failures

        if (log.isDebugEnabled())  {
            log.debug("performCommits: removing tmpnodes");
        }
        // remove temporary nodes from temporary area
        for (i=0;i<nodes.size();i++) {
            if (nodestate[i]==COMMITED) {
                node=(MMObjectNode)nodes.elementAt(i);
                if (log.isDebugEnabled()) {
                    log.debug("commit "+node);
                }
                bul.removeTmpNode(node.getStringValue("_number"));
            }
        }
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
        return "";
    }

}
