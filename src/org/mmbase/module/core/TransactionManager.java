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
	$Id: TransactionManager.java,v 1.8 2000-11-24 12:15:22 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.7  2000/11/24 12:08:38  vpro
	Rico: increased debug
	
	Revision 1.6  2000/11/24 12:07:30  vpro
	Rico: increased debug
	
	Revision 1.5  2000/11/22 13:11:25  vpro
	Rico: added deleteObject support
	
	Revision 1.4  2000/11/08 16:24:13  vpro
	Rico: fixed key bussiness
	
	Revision 1.3  2000/10/13 11:47:26  vpro
	Rico: made it working
	
	Revision 1.2  2000/10/13 11:41:34  vpro
	Rico: made it working
	
	Revision 1.1  2000/08/14 19:19:05  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TransactionManager.java,v 1.8 2000-11-24 12:15:22 vpro Exp $
 */
public class TransactionManager implements TransactionManagerInterface {
	private String	_classname = getClass().getName();
	private boolean _debug=true;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	public static final String EXISTS_NO="no";
	public static final int I_EXISTS_NO=0;
	public static final String EXISTS_YES="yes";
	public static final int I_EXISTS_YES=1;
	public static final String EXISTS_NOLONGER="nolonger";
	public static final int I_EXISTS_NOLONGER=2;

	private TemporaryNodeManagerInterface tmpNodeManager;
	private Object usermanager;
	private MMBase mmbase;
	protected Hashtable transactions=new Hashtable();
	protected TransactionResolver trs;

	public TransactionManager(MMBase mmbase,TemporaryNodeManagerInterface tmpn) {
		this.mmbase=mmbase;
		this.tmpNodeManager=tmpn;
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

	public String addNode(String transactionname,String owner,String tmpnumber) {
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
					debug("addNode(): node not added as it was already in the transaction "+tmpnumber);
				}
			}
		} else {
			debug("Can't add node as it doesn't exist "+tmpnumber);
		}
		return(tmpnumber);
	}

	public String removeNode(String transactionname,String owner,String tmpnumber) {
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


	public String deleteObject(String transactionname,String owner,String tmpnumber) {
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
					debug("addNode(): node not added as it was already in the transaction "+tmpnumber);
				}
			}
			// Mark it as to delete
			node.setValue("_exists",EXISTS_NOLONGER);
		} else {
			debug("Can't add node as it doesn't exist "+tmpnumber);
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
					// if (!debug) transactions.remove(transactionname);
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
		if (nodes==null || nodes.size()==0) {
			debug("performCommits: Empty list of nodes");
			return(false);
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

		if (_debug) debug("performCommits: checking types");
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

		if (_debug) debug("performCommits: checking existence");
		// check if they alreay exist (aka use update vs insert)
		for (i=0;i<nodes.size();i++) {
			node=(MMObjectNode)nodes.elementAt(i);
			tmpstate=node.getDBState("_number");
			if ((tmpstate==FieldDefs.DBSTATE_UNKNOWN || tmpstate==FieldDefs.DBSTATE_VIRTUAL)) {
 				exists=node.getStringValue("_exists");
				if (exists==null) {
					debug("performCommits: exists field does not exist "+node);
				} else if (exists.equals(EXISTS_NO)) {
					nodeexist[i]=I_EXISTS_NO;
				} else if (exists.equals(EXISTS_YES)) {
					nodeexist[i]=I_EXISTS_YES;
				} else if (exists.equals(EXISTS_NOLONGER)) {
					nodeexist[i]=I_EXISTS_NOLONGER;
				} else {
					debug("performCommits: invalid value for _exists "+node);
				}
			}
		}

		if (debug) {
			for (i=0;i<nodes.size();i++) {
				node=(MMObjectNode)nodes.elementAt(i);
				debug("node "+i+" type "+nodetype[i]+" , exist "+nodeexist[i]+" node "+node.getStringValue("_number"));
			}
		}

		if (_debug) debug("performCommits: deleting relations");
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
						if (_debug) debug("node "+i+" delete ");
						if (!debug) {
							// no return information
							bul.removeNode(node);
							res=true;
						} else {
							res=true;
						}
						break;
					default:
						res=false;
						debug("performCommits invalid exists value "+nodeexist[i]);
						break;
				}
				if (res) {
					nodestate[i]=COMMITED;
				} else {
					nodestate[i]=FAILED;
				}
			}
		}

		if (_debug) debug("performCommits: deleting nodes");
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
						if (_debug) debug("node "+i+" delete ");
						if (!debug) {
							// no return information
							bul.removeNode(node);
							res=true;
						} else {
							res=true;
						}
						break;
					default:
						res=false;
						debug("performCommits invalid exists value "+nodeexist[i]);
						break;
				}
				if (res) {
					nodestate[i]=COMMITED;
				} else {
					nodestate[i]=FAILED;
				}
			}
		}

		if (_debug) debug("performCommits: commiting nodes");
		// Then commit all the NODES
		for (i=0;i<nodes.size();i++) {
			if (nodetype[i]==NODE) {
				node=(MMObjectNode)nodes.elementAt(i);
				switch(nodeexist[i]) {
					case I_EXISTS_YES:
						if (!debug) {
							res=node.commit();
						} else {
							res=true;
						}
						if (_debug) debug("node "+i+" commit ");
						break;
					case I_EXISTS_NO:
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
						break;
					case I_EXISTS_NOLONGER:
						break;
					default:
						res=false;
						debug("performCommits invalid exists value "+nodeexist[i]);
						break;
				}
				if (res) {
					nodestate[i]=COMMITED;
				} else {
					nodestate[i]=FAILED;
				}
			}
		}

		if (_debug) debug("performCommits: commiting relations");
		// Then commit all the RELATIONS
		for (i=0;i<nodes.size();i++) {
			if (nodetype[i]==RELATION) {
				node=(MMObjectNode)nodes.elementAt(i);

				switch(nodeexist[i]) {
					case I_EXISTS_YES:
						if (!debug) {
							res=node.commit();
						} else {
							res=true;
						}
						if (_debug) debug("node "+i+" commit ");
						break;
					case I_EXISTS_NO:
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
						break;
					case I_EXISTS_NOLONGER:
						break;
					default:
						res=false;
						debug("performCommits invalid exists value "+nodeexist[i]);
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

		if (_debug) debug("performCommits: removing tmpnodes");
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

}
