/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.builders.MultiRelations;
import org.mmbase.util.StringTagger;
import java.util.*;

/**
 * A Cloud is a collection of Nodes (and relations that are also nodes).
 * A Cloud is tied to one or more CLoudContexts (which reside on various VMs).
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicCloud implements Cloud, Cloneable {

    // link to cloud context
    private BasicCloudContext cloudContext = null;

    // link to typedef object for retrieving type info (builders, etc)
    private TypeDef typedef = null;

    // name of the cloud
    protected String name = null;

    // account of the current user (unique)
    // This is a unique number, unrelated to the user context
    // It is meant to uniquely identify this session to MMBase
    // It is NOT used for authorisation!
    protected String account = null;

    // user name of the current user
    protected String userName = null;

    // language
    protected String language = null;

    // description
    // note: in future, this is dependend on language settings!
    protected String description = null;

    // transactions
    protected HashMap transactions = new HashMap();

    // node managers cache
    protected HashMap nodeManagerCache = new HashMap();

    // relation manager cache
    protected HashMap relationManagerCache = new HashMap();

    // parent Cloud, if appropriate
    protected BasicCloud parentCloud=null;
    /**
     *  basic constructor for descendant clouds (i.e. Transaction)
     */
    protected BasicCloud() {
    }

    /**
     *  basic constructor for descendant clouds (i.e. Transaction)
     */
    BasicCloud(String cloudName, BasicCloud cloud) {
        cloudContext=cloud.cloudContext;
        parentCloud=cloud;
        typedef = cloud.typedef;
        language=cloud.language;
        if (cloudName==null) {
            name = cloud.name;
        } else {
            name = cloud.name+"."+cloudName;
        }
        description = cloud.description;
        account= cloud.account;
        userName=cloud.userName;
    }

    /**
     *  Constructor to call from the CloudContext class
     *  (package only, so cannot be reached from a script)
     */
    BasicCloud(String cloudName, CloudContext cloudcontext) {
        cloudContext=(BasicCloudContext)cloudcontext;
        typedef = cloudContext.mmb.getTypeDef();
        language = cloudContext.mmb.getLanguage();

        // normally, we want the cloud to read it's context from an xml file.
        // the current system does not support multiple clouds yet,
        // so as a temporary hack we set default values

        name = cloudName;
        description = cloudName;
    }

	/**
	 * Retrieve the node from the cloud.
	 * Note : this also retrieves temporary (newly created) nodes
	 * @param nodenumber the number of the node
	 * @return the requested node
	 */
	public Node getNode(int nodenumber) {

	    MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(account,""+nodenumber);
	    if (node==null) {
	        return null;
	    } else {
    	    // hack. should be done differently!
	        if (node.getIntValue("number")==-1) {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()), nodenumber);
    	    } else {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()));
    	    }
	    }
	}

	/**
	 * Retrieves the node with the given aliasname.
	 * Note : this also retrieves temporary (newly created) nodes by id, but cannot use aliasses within a transaction
	 * @param aliasname the aliasname of the node
	 * @return the requested node
	 */
	public Node getNodeByAlias(String aliasname) {
	    MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(account,aliasname);
	    if (node==null) {
	        return null;
	    } else {
    	    // hack. should be done differently!
	        if (node.getIntValue("number")==-1) {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()), Integer.parseInt(aliasname));
    	    } else {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()));
    	    }
	    }
	}

 	/**
     * Retrieves all node managers (aka builders) available in this cloud
     * @return an <code>Iterator</code> containing all node managers
     */
    public List getNodeManagers() {
        Vector nodeManagers = new Vector();
        for(Enumeration builders = cloudContext.mmb.getMMObjects(); builders.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)builders.nextElement();
            if (!(bul instanceof org.mmbase.module.builders.MultiRelations)) {
                NodeManager nodeManager=(NodeManager)nodeManagerCache.get(bul.getTableName());
                if (nodeManager==null) {
                    nodeManager=new BasicNodeManager(bul, this);
                    nodeManagerCache.put(bul.getTableName(),nodeManager);
                }
                nodeManagers.add(nodeManager);
            }
        }
       return nodeManagers;
    }

	/**
     * Retrieves a node manager (aka builder)
     * @param nodeManagerName name of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    public NodeManager getNodeManager(String nodeManagerName) {
        // cache quicker, and you don't get 2000 nodetypes when you do a search....
        NodeManager nodeManager=(NodeManager)nodeManagerCache.get(nodeManagerName);
        if (nodeManager==null) {
            MMObjectBuilder bul=cloudContext.mmb.getMMObject(nodeManagerName);
            nodeManager=new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName,nodeManager);
        }
        return nodeManager;
    }

	/**
     * Retrieves a node manager (aka builder)
     * @param nodeManagerId ID of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    NodeManager getNodeManagerById(int nodeManagerId) {
        return getNodeManager(typedef.getValue(nodeManagerId));
    }
 	
 	/**
     * Retrieves a RelationManager.
     * Note that the Relationmanager is very strict - you cannot retrieve a manager with source and destination reversed.
     * @param sourceManagerID number of the NodeManager of the source node
     * @param destinationManagerID number of the NodeManager of the destination node
     * @param roleID number of the role
     * @return the requested RelationManager
     */
    RelationManager getRelationManager(int sourceManagerId, int destinationManagerId, int roleId) {
        // cache. pretty ugly but at least you don't get 1000+ instances of a relationmanager
        RelationManager relManager=(RelationManager)relationManagerCache.get(""+sourceManagerId+"/"+destinationManagerId+"/"+roleId);
        if (relManager==null) {
            // XXX adapt for other dir too!
            Enumeration e =cloudContext.mmb.getTypeRel().search("WHERE snumber="+sourceManagerId+" AND dnumber="+destinationManagerId+" AND rnumber="+roleId);
            if (e.hasMoreElements()) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                relManager = new BasicRelationManager(node,this);
                relationManagerCache.put(""+sourceManagerId+"/"+destinationManagerId+"/"+roleId,relManager);
            }
        }
        return relManager;
    };


 	/**
     * Retrieves a RelationManager
     * @param sourceManagerName name of the NodeManager of the source node
     * @param destinationManagerName name of the NodeManager of the destination node
     * @param roleName name of the role
     * @return the requested RelationManager
     */
    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        // uses getguesed number, maybe have to fix this later
        int r=cloudContext.mmb.getRelDef().getGuessedNumber(roleName);
        int n1=typedef.getIntValue(sourceManagerName);
        int n2=typedef.getIntValue(destinationManagerName);
        return getRelationManager(n1,n2,r);
    };

	/**
     * Creates a node using a specified NodeManager
     * The returned node will not be visible in the cloud until the commit() method is called on this node.
     * @param nodeManagerName name of the NodeManager defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(String nodeManagerName) {
        NodeManager nodeManager = getNodeManager(nodeManagerName);
	    if (nodeManager==null) {
	        return null;
	    } else {
            return nodeManager.createNode();
        }
    }

	/**	
 	 * Create unique number
	 */
	static synchronized int uniqueId() {
		try {
			Thread.sleep(1); // A bit paranoid, but just to be sure that not two threads steal the same millisecond.
		} catch (Exception e) {
		}
		return (int)(java.lang.System.currentTimeMillis() % Integer.MAX_VALUE);		
	}

    /**
     * Creates a non-named transaction on this cloud
     * @return a <code>Transaction</code> on this cloud
     */
    public Transaction createTransaction() {
        return createTransactionByName(null);
    }

    /**
     * Creates a transaction on this cloud.
     * @param name an unique name to use for the transaction
     * @return a <code>Transaction</code> on this cloud
     */
    public Transaction createTransactionByName(String name){
      if (name==null) {
        name="Tran"+uniqueId();
      } else if (transactions.get(name)!=null) {
	        throw new BridgeException("Transaction already exists name = " + name);
      }
      Transaction transaction = new BasicTransaction(name,this);
      transactions.put(name,transaction);
      return transaction;
    }

    /**
     * Creates a transaction on this cloud.
     * @param name the unique name to for the transaction
     * @return the identified <code>Transaction</code>
     */
    public Transaction openTransaction(String name) {
        return (Transaction)transactions.get(name);
    }
	
	/**
     * Retrieves the context for this cloud
     * @return the cloud's context
     */
    public CloudContext getCloudContext() {
        return cloudContext;
    }

  	/**
     * Retrieves the cloud's name (this is an unique identifier).
     * @return the cloud's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the description of the cloud
     * @return return a description of the cloud
     */
    public String getDescription(){
        return description;
    }

  	/**
     * Retrieves the current user accountname (unique)
     * @return the account name
     */
    String getAccount() {
        if (account==null) {
            throw new SecurityException("User not logged on.");
        }
        return account;
    }

  	/**
     * Retrieves the current user name
     * @return the user name
     */
    public String getUserName() {
        if (userName==null) {
            throw new SecurityException("User not logged on.");
        }
        return userName;
    }

    /**
     * Retrieves the current selected language
	 * @return return the language as a <code>String</code>
     */
    public String getLanguage() {
        return language;
    };

    /**
     * Sets the current selected language
	 * @param language the language as a <code>String</code>
     */
    public void setLanguage(String language) {
        this.language=language;
    };
  	
  	/**
     * Logs on a user.
     * This results in an environment (a cloud) in which the user is registered.
	 * @param authenticatorName name of the authentication method to sue
	 * @param parameters parameters for the authentication
	 * @return <code>true</code> if succesful (should throw exception?)
     */
    public boolean logon(String authenticatorName, Object[] parameters) {
        if (account!=null) {
	        throw new SecurityException("Unsupported authentication method");
        } else {
	        throw new BridgeException("Invalidly obtained cloud");
        }
    }
  	
  	/**
     * Logs off a user.
     * Resets the user's context to 'anonymous'
     */
    public void logoff() {
        if (account!=null) {
            userName="anonymous";
        } else {
	        throw new BridgeException("Invalidly obtained cloud");
        }
    }

    /**
    * Copies the cloud and return the clone.
    * @return the copy of this <code>Cloud</code>
    */
    BasicCloud getCopy() {
        BasicCloud cloud=new BasicCloud(null,this);
        cloud.userName="anonymous";
        cloud.account="U"+uniqueId();
        return cloud;
    }  	

	/**
     * Search nodes in a cloud accoridng to a specified filter.
     * @param nodes The numbers of the nodes to start the search with. These have to be a member of the first NodeManager
     *      listed in the nodeManagers parameter. The syntax is a comma-seperated lists of node ids.
     *      Example : '112' or '1,2,14'
     * @param nodeManagers The NodeManager chain. The syntax is a comma-seperated lists of NodeManager names.
     *      The search is formed by following the relations between successive NodeManagers in the list. It is possible to explicitly supply
     *      a RelationManager by placing the name of the manager between two NodeManagers to search.
     *      Example: 'company,people' or 'typedef,authrel,people'.
     * @param fields The fieldnames to return (comma seperated). This can include the name of the NodeManager in case of fieldnames that are used by
     *      more than one manager (i.e number).
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname', 'typedef.number,authrel.creat,people.number'
     * @param where The contraint. this is in essence a SQL where clause, using the NodeManager names from the nodes as tablenames.
     *      Examples: "people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param order the fieldnames on which you want to sort. Identical in syntax to the fields parameter.
     * @param direction A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      The first value in the list is used for teh remainig fields. Default value is <code>'UP'</code>.
     *      Examples: 'UP,DOWN,DOWN'
     * @param distinct <code>True> indicates the records returned need to be distinct. <code>False</code> indicates double values can be returned.
     * @return a <code>List</code> of found (virtual) nodes
     */
    public List search(String nodes, String nodeManagers, String fields, String where, String sorted, String direction, boolean distinct) {
  		StringTagger tagger= new StringTagger(
  		                    "NODES='"+nodes+"' TYPES='"+nodeManagers+"' FIELDS='"+fields+
  		                  "' SORTED='"+sorted+"' DIR='"+direction+"'",
  		                    ' ','=',',','\'');
  		
  		String sdistinct="";
        if (distinct) sdistinct="YES";

        Vector snodes = tagger.Values("NODES");
  		Vector sfields = tagger.Values("FIELDS");
  		Vector tables = tagger.Values("TYPES");
  		Vector orderVec = tagger.Values("SORTED");
  		Vector sdirection =tagger.Values("DIR"); // minstens een : UP
		if (direction==null) {
		    sdirection=new Vector();
		    sdirection.addElement("UP"); // UP == ASC , DOWN =DESC
		}
	  		
  		MultiRelations multirel = (MultiRelations)cloudContext.mmb.getMMObject("multirelations");
  		int nrfields = sfields.size();
  		Vector retval = new Vector();
  		if (nrfields==0) { return retval; }
  		if (where!=null) {
  		    if (where.trim().equals("")) {
  		        where = null;
  		    } else {
  		        where="WHERE "+where;
  		    }
  		}	
  		Vector v = multirel.searchMultiLevelVector(snodes,sfields,sdistinct,tables,where,orderVec,sdirection);
  		if (v!=null) {
  		    NodeManager tempNodeManager=null;
  		    for(Enumeration nodeEnum = v.elements(); nodeEnum.hasMoreElements(); ){
  		        MMObjectNode node = (MMObjectNode)nodeEnum.nextElement();
  		        if (tempNodeManager==null) {
  		            tempNodeManager = new VirtualNodeManager(node,this);
  		        }
                retval.addElement(new BasicNode(node,tempNodeManager));
  		    }
		}
  		return retval;
    }

}
