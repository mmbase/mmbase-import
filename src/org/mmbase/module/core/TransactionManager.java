/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.module.corebuilders.*;

/*
	$Id: TransactionManager.java,v 1.1 2000-08-14 19:19:05 rico Exp $

	$Log: not supported by cvs2svn $
*/

/**
 * @author Rico Jansen
 * @version $Id: TransactionManager.java,v 1.1 2000-08-14 19:19:05 rico Exp $
 */
public class TransactionManager implements TransactionManagerInterface {
	private String	_classname = getClass().getName();
	private boolean _debug=true;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private Object usermanager;
	private MMBase mmbase;
	protected Hashtable transactions=new Hashtable();
	protected TransactionResolver trs;

	public TransactionManager(MMBase mmbase) {
		this.mmbase=mmbase;
		trs=new TransactionResolver(mmbase);
		// Probably this is going to be retrieved from mmbase
		// so findUserName can actually do something
		usermanager=new Object();
	}

	public String create(Object user,String transactionname) {
		if (!transactions.containsKey(transactionname)) {
			Vector v=new Vector();
			transactions.put(transactionname,v);
		}
		if (_debug) debug("create transaction for "+transactionname);
		return(transactionname);
	}

	public String addNode(String transactionname,String tmpnumber) {
		MMObjectNode node;
		Vector v;

		v=(Vector)transactions.get(transactionname);
		node=findNode(tmpnumber);
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
					debug("addNode(): node not added as it was already in the transaction "+tmpnumber);
				}
			}
		} else {
			debug("Can't add node as it doesn't exist "+tmpnumber);
		}
		return(tmpnumber);
	}

	public String removeNode(String transactionname,String tmpnumber) {
		MMObjectNode node;
		Vector v;

		v=(Vector)transactions.get(transactionname);
		node=findNode(tmpnumber);
		if (node!=null) {
			if (v!=null) {
				int n;
				n=v.indexOf(node);
				if (n>=0) {
					v.removeElementAt(n);
				} else {
					debug("removeNode(): node is not in transaction "+transactionname+", "+tmpnumber);
				}
			} else {
				debug("removeNode(): transaction doesn't exist "+transactionname);
			}
		} else {
			debug("removeNode(): Can't remove node as it doesn't exist "+tmpnumber);
		}
		return(tmpnumber);
	}

	public Vector getNodes(Object user,String transactionname) {
		Vector rtn;

		rtn=(Vector)transactions.get(transactionname);
		if (rtn==null) {
			debug("getNodes(): can't find transaction "+transactionname);
		}
		return(rtn);
	}

	public String cancel(Object user,String transactionname) {
		Vector v;

		v=(Vector)transactions.get(transactionname);
		if (v!=null) {
			transactions.remove(transactionname);
			if (_debug) debug("Removed transaction "+transactionname+ "\n   "+v);
		} else {
			debug("Can't find transaction "+transactionname);
		}
		return(transactionname);
	}


	public String commit(Object user,String transactionname) {
		return(commit(user,transactionname,false));

	}

	protected String commit(Object user,String transactionname,boolean debug) {
		Vector v;
		String s;

		v=(Vector)transactions.get(transactionname);
		if (v!=null) {
			boolean resolved;
			resolved=trs.resolve(v,debug);
			if (!resolved) {
				debug("Can't resolve transaction "+transactionname);
				debug("Nodes \n"+v);
			} else {
				resolved=performCommits(user,v,debug);
				if (!resolved) {
					debug("Can't commit transaction "+transactionname);
					debug("Nodes \n"+v);
				} else {
					debug("commited "+transactionname);
					if (!debug) transactions.remove(transactionname);
				}
			}
		} else {
			debug("Can't find transaction "+transactionname);
		}
		return(transactionname);
	}

	private final static int UNCOMMITED=0;
	private final static int COMMITED=1;
	private final static int FAILED=2;
	private final static int NODE=3;
	private final static int RELATION=4;

	boolean performCommits(Object user,Vector nodes,boolean debug) {
		MMObjectNode node;
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		boolean okay=false,res=false;
		int[] nodestate=new int[nodes.size()];
		int[] nodetype=new int[nodes.size()];
		boolean[] nodeexist=new boolean[nodes.size()];
		int i;
		String username=findUserName(user);

		// Nodes are uncommited by default
		for (i=0;i<nodes.size();i++) nodestate[i]=UNCOMMITED;

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

		// check if they alreay exist (aka use update vs insert)
		for (i=0;i<nodes.size();i++) {
			node=(MMObjectNode)nodes.elementAt(i);
			if (node.getDBState("_number")==0 && node.getStringValue("_exists")==null) {
				nodeexist[i]=false;
			} else {
				nodeexist[i]=true;
			}
		}

		if (debug) {
			for (i=0;i<nodes.size();i++) {
				node=(MMObjectNode)nodes.elementAt(i);
				debug("node "+i+" type "+nodetype[i]+" , exist "+nodeexist[i]+" node "+node.getStringValue("_number"));
			}
		}

		// First commit all the NODES
		for (i=0;i<nodes.size();i++) {
			if (nodetype[i]==NODE) {
				node=(MMObjectNode)nodes.elementAt(i);
				if (nodeexist[i]) {
					if (!debug) {
						res=node.commit();
					} else {
						res=true;
					}
					if (_debug) debug("node "+i+" commit ");
				} else {
					if (_debug) debug("node "+i+" insert ");
					if (!debug) {
						if (username.length()>1) {
							res=node.insert(username)!=-1;
						} else {
							res=node.insert(node.getStringValue("owner"))!=-1;
						}
					} else {
						res=true;
					}
				}
				if (res) {
					nodestate[i]=COMMITED;
				} else {
					nodestate[i]=FAILED;
				}
			}
		}

		// Then commit all the RELATIONS
		for (i=0;i<nodes.size();i++) {
			if (nodetype[i]==RELATION) {
				node=(MMObjectNode)nodes.elementAt(i);
				if (nodeexist[i]) {
					if (!debug) {
						res=node.commit();
					} else {
						res=true;
					}
					if (_debug) debug("node "+i+" commit ");
				} else {
					if (_debug) debug("node "+i+" insert ");
					if (!debug) {
						if (username.length()>1) {
							res=node.insert(username)!=-1;
						} else {
							res=node.insert(node.getStringValue("owner"))!=-1;
						}
					} else {
						res=true;
					}
				}
				if (res) {
					nodestate[i]=COMMITED;
				} else {
					nodestate[i]=FAILED;
				}
			}
		}
		// check for failures

		// remove temporary nodes from temporary area
		for (i=0;i<nodes.size();i++) {
			if (nodestate[i]==COMMITED) {
				node=(MMObjectNode)nodes.elementAt(i);
				if (_debug) debug("commit "+node);
				bul.removeTmpNode(node.getStringValue("_number"));
			}
		}
		okay=true;
		for (i=0;i<nodes.size();i++) {
			if (nodestate[i]==FAILED) {
				okay=false;
				debug("Failed node "+((MMObjectNode)nodes.elementAt(i)).toString());
			}
		}
		return(okay);
	}

	public String findUserName(Object user) {
		String rtn="";
		return(rtn);
	}

	protected MMObjectNode findNode(String key) {
		MMObjectBuilder bul=mmbase.getMMObject("typedef");
		MMObjectNode node;
		node=bul.getTmpNode(key);
		// fallback to normal nodes
		if (node==null) {
			bul.getNode(key);
		}
		return(node);
	}
}
