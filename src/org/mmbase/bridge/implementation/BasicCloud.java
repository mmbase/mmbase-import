/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.cache.*;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.util.StringTagger;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * @javadoc
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: BasicCloud.java,v 1.65 2002-09-03 12:39:56 eduard Exp $
 */
public class BasicCloud implements Cloud, Cloneable {
    private static Logger log = Logging.getLoggerInstance(BasicCloud.class.getName());

    // lastRequestId
    // used to generate a temporary ID number
    // The Id starts at - 2 and is decremented each time a new id is asked
    // until Integer.MIN_VALUE is reached (after which counting starts again at -2).
    private static int lastRequestId = -2;

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

    // MMBaseCop
    MMBaseCop mmbaseCop = null;

    // User context
    protected BasicUser userContext = null;


    private int multilevel_cachesize=300;

    private MultilevelCacheHandler multilevel_cache;

    private Locale locale;
    
    /**
     *  basic constructor for descendant clouds (i.e. Transaction)
     */
    BasicCloud(String cloudName, BasicCloud cloud) {
        cloudContext=cloud.cloudContext;
        parentCloud=cloud;
        typedef = cloud.typedef;
        locale = cloud.locale;
        if (cloudName==null) {
            name = cloud.name;
        } else {
            name = cloud.name+"."+cloudName;
        }
        description = cloud.description;
        mmbaseCop = cloud.mmbaseCop;

        userContext = cloud.userContext;
        account= cloud.account;

        // start multilevel cache
        MultilevelCacheHandler.setMMBase(this.cloudContext.mmb);
        multilevel_cache = MultilevelCacheHandler.getCache();
    }

    /**
     */
    BasicCloud(String name, String application, Map loginInfo, CloudContext cloudContext) {
        // get the cloudcontext and mmbase root...
        this.cloudContext=(BasicCloudContext)cloudContext;
        MMBase mmb = this.cloudContext.mmb;

        // do authentication.....
        mmbaseCop = mmb.getMMBaseCop();

        if (mmbaseCop == null) {
            String message;
            message = "Couldn't find the MMBaseCop.";
            log.error(message);
            throw new BridgeException(message);
        }
        org.mmbase.security.UserContext uc = mmbaseCop.getAuthentication().login(application, loginInfo, null);
        if (uc == null) {
            String message;
            message = "Login invalid (login-module: " + application + ")";
            log.error(message);
            throw new BridgeException(message);
        }
        userContext = new BasicUser(mmbaseCop, uc);
        // end authentication...

        // other settings of the cloud...
        typedef = mmb.getTypeDef();
        locale = new Locale(mmb.getLanguage(), "");

        // normally, we want the cloud to read it's context from an xml file.
        // the current system does not support multiple clouds yet,
        // so as a temporary hack we set default values
        this.name = name;
        description = name;

        // generate an unique id for this instance...
        account="U"+uniqueId();

        // start multilevel cache
        MultilevelCacheHandler.setMMBase(mmb);
        multilevel_cache = MultilevelCacheHandler.getCache();
    }

    // Makes a node otr Relation object based on an MMObjectNode
    Node makeNode(MMObjectNode node, String nodenumber) {
        NodeManager nm = getNodeManager(node.parent.getTableName());
        int nodenr=node.getNumber();
        if (nodenr==-1) {
            int nodeid = Integer.parseInt(nodenumber);
            if (node.parent instanceof InsRel) {
                return new BasicRelation(node, nm, nodeid);
            } else {
                return new BasicNode(node, nm, nodeid);
            }
        } else {
            this.verify(Operation.READ,nodenr);
            if (node.parent instanceof InsRel) {
                return new BasicRelation(node, nm);
            } else {
                return new BasicNode(node, nm);
            }
        }
    }

    public Node getNode(String nodenumber) {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(account,nodenumber);
        } catch (RuntimeException e) {
            String message;
            message = "Something went wrong while getting node with number " + nodenumber + " (does it exist?)\n" + Logging.stackTrace(e);
            log.error(message);
            throw new NotFoundException(message);
        }
        if (node==null) {
            String message;
            message = "Node with number " + nodenumber + " does not exist.";
            log.error(message);
            throw new NotFoundException(message);
        } else {
            return makeNode(node,nodenumber);
        }
    }

    public Node getNode(int nodenumber) {
        return getNode(""+nodenumber);
    }

    public Node getNodeByAlias(String aliasname) {
        return getNode(aliasname);
    }

    public NodeManagerList getNodeManagers() {
        Vector nodeManagers = new Vector();
        for(Enumeration builders = cloudContext.mmb.getMMObjects(); builders.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)builders.nextElement();
            if(!bul.isVirtual() && check(Operation.READ, bul.oType)) {
                nodeManagers.add(bul.getTableName());
            }
        }
        return new BasicNodeManagerList(nodeManagers,this);
    }

    public NodeManager getNodeManager(String nodeManagerName) {
        // cache quicker, and you don't get 2000 nodetypes when you do a search....
        BasicNodeManager nodeManager=(BasicNodeManager)nodeManagerCache.get(nodeManagerName);
        MMObjectBuilder bul = cloudContext.mmb.getMMObject(nodeManagerName);
        // always look if builder exists, since otherwise
        if (bul == null) {
            String message;
            message = "Node manager with name " + nodeManagerName + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        }
        if (nodeManager==null) {
            // not found in cache
            nodeManager=new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName,nodeManager);
        }
        else if (nodeManager.getMMObjectBuilder() != bul) {
            // cache differs
            nodeManagerCache.remove(nodeManagerName);
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
    NodeManager getNodeManager(int nodeManagerId) {
        return getNodeManager(typedef.getValue(nodeManagerId));
    }

    /**
     * Retrieves a RelationManager.
     * Note that you can retrieve a manager with source and destination reversed.
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
            Enumeration e =cloudContext.mmb.getTypeRel().search(
                  "WHERE snumber="+sourceManagerId+" AND dnumber="+destinationManagerId+" AND rnumber="+roleId);
            if (!e.hasMoreElements()) {
              e =cloudContext.mmb.getTypeRel().search(
                  "WHERE dnumber="+sourceManagerId+" AND snumber="+destinationManagerId+" AND rnumber="+roleId);
            }
            if (e.hasMoreElements()) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                relManager = new BasicRelationManager(node,this);
                relationManagerCache.put(""+sourceManagerId+"/"+destinationManagerId+"/"+roleId,relManager);
            }
        }
        return relManager;
    }

    /**
     * Retrieves a flexible (type-independent) RelationManager.
     * Note that the relation manager retrieved with this method does not contain
     * type info, which means that some data, such as source- and destination type,
     * cannot be retrieved.
     * @param roleID number of the role
     * @return the requested RelationManager
     */
    RelationManager getRelationManager(int roleId) {
        // cache. pretty ugly but at least you don't get 1000+ instances of a relationmanager
        RelationManager relManager=(RelationManager)relationManagerCache.get(""+roleId);
        if (relManager==null) {
            MMObjectNode n =cloudContext.mmb.getRelDef().getNode(roleId);
            if (n!=null) {
                relManager = new BasicRelationManager(n,this);
                relationManagerCache.put(""+roleId,relManager);
            }
        }
        return relManager;
    }

    public RelationManagerList getRelationManagers() {
        Vector v= new Vector();
        for(Enumeration e =cloudContext.mmb.getTypeRel().search("");e.hasMoreElements();) {
            v.add((MMObjectNode)e.nextElement());
        }
        return new BasicRelationManagerList(v,this);
    }

    public RelationManager getRelationManager(String sourceManagerName,
        String destinationManagerName, String roleName) {
        // uses getguesed number, maybe have to fix this later
        int r=cloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r==-1) {
            String message;
            message = "Role " + roleName + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        }
        int n1=typedef.getIntValue(sourceManagerName);
        if (n1==-1) {
            String message;
            message = "Source type " + sourceManagerName + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        }
        int n2=typedef.getIntValue(destinationManagerName);
        if (n2==-1) {
            String message;
            message = "Destination type " + destinationManagerName
                      + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        }
        RelationManager rm=getRelationManager(n1,n2,r);
        if (rm==null) {
            String message;
            message = "Relation manager from " + sourceManagerName + " to "
                      + destinationManagerName + " as " + roleName
                      + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        } else {
            return rm;
        }
    }

    public RelationManager getRelationManager(String roleName) {
        int r=cloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r==-1) {
            String message;
            message = "Role " + roleName + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        }
        RelationManager rm=getRelationManager(r);
        if (rm==null) {
            String message;
            message = "Relation manager for " + roleName + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        } else {
            return rm;
        }
    }

    /**
     * Create unique temporary node number.
     * The Id starts at - 2 and is decremented each time a new id is asked
     * until Integer.MINVALUE is reached (after which counting starts again at -2).
     * @todo This may be a temporary solution. It may be desirable to immediately reserve a
     * number at the database layer, so resolving (by the transaction) will not be needed.
     * However, this needs some changes in the TemporaryNodeManager and the classes that make use of this.
     *
     * @return the temporary id as an integer
     */
    static synchronized int uniqueId() {
        if (lastRequestId>Integer.MIN_VALUE) {
            lastRequestId = lastRequestId-1;
        } else {
            lastRequestId = -2;
        }
        return lastRequestId;
    }

    /**
     * Test if a node id is a temporay id.
     * @param id the id (node numebr) to test
     * @return true if the id is a temporary id
     * @since MMBase-1.5
     */
    static boolean isTemporaryId(int id) {
        return id < -1;
    }

    public Transaction createTransaction() {
        return createTransaction(null,false);
    }

    public Transaction createTransaction(String name){
        return createTransaction(name,false);
    }

    public Transaction createTransaction(String name, boolean overwrite) {
        if (name==null) {
            name="Tran"+uniqueId();
          } else {
              Transaction oldtransaction=(Transaction)transactions.get(name);
              if (oldtransaction!=null) {
                  if (overwrite) {
                      oldtransaction.cancel();
                  } else {
                      String message;
                      message = "Transaction with name " + name
                                + "already exists.";
                      log.error(message);
                      throw new BridgeException(message);
                  }
              }
        }
        Transaction transaction = new BasicTransaction(name,this);
        transactions.put(name,transaction);
        return transaction;
    }

    public Transaction getTransaction(String name) {
        Transaction tran=(Transaction)transactions.get(name);
        if (tran==null) {
            tran = createTransaction(name,false);
        }
        return tran;
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

    public User getUser() {
        return userContext;
    }

    /**
     * Retrieves the current user accountname (unique)
     * @return the account name
     */
    String getAccount() {
        return account;
    }

    /**
    * Checks access rights.
    * @param operation the operation to check (READ, WRITE, CREATE, OWN)
    * @param nodeID the node on which to check the operation
    * @return <code>true</code> if access is granted, <code>false</code> otherwise
    */
    boolean check(Operation operation, int nodeID) {
        return mmbaseCop.getAuthorization().check(userContext.getUserContext(),nodeID,operation);
    }

    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (READ, WRITE, CREATE, OWN)
    * @param nodeID the node on which to check the operation
    */
    void verify(Operation operation, int nodeID) {
        mmbaseCop.getAuthorization().verify(userContext.getUserContext(),nodeID,operation);
    }

    /**
    * Checks access rights.
    * @param operation the operation to check (CREATE, CHANGE_RELATION)
    * @param nodeID the node on which to check the operation
    * @param srcNodeID the source node for this relation
    * @param dstNodeID the destination node for this relation
    * @return <code>true</code> if access is granted, <code>false</code> otherwise
    */
    boolean check(Operation operation, int nodeID, int srcNodeID, int dstNodeID) {
        return mmbaseCop.getAuthorization().check(userContext.getUserContext(),nodeID,srcNodeID,dstNodeID,operation);
    }

    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (CREATE, CHANGE_RELATION)
    * @param nodeID the node on which to check the operation
    * @param srcNodeID the source node for this relation
    * @param dstNodeID the destination node for this relation
    */
    void verify(Operation operation, int nodeID, int srcNodeID, int dstNodeID) {
        mmbaseCop.getAuthorization().verify(userContext.getUserContext(),nodeID,srcNodeID,dstNodeID,operation);
    }

    /**
    * initializes access rights for a newly created node
    * @param nodeID the node to init
    */
    void createSecurityInfo(int nodeID) {
        mmbaseCop.getAuthorization().create(userContext.getUserContext(),nodeID);
    }

    /**
    * removes access rights for a removed node
    * @param nodeID the node to init
    */
    void removeSecurityInfo(int nodeID) {
        mmbaseCop.getAuthorization().remove(userContext.getUserContext(),nodeID);
    }

    /**
    * updates access rights for a changed node
    * @param nodeID the node to init
    */
    void updateSecurityInfo(int nodeID) {
        mmbaseCop.getAuthorization().update(userContext.getUserContext(),nodeID);
    }

    /**
     * Converts a constraint by turning all 'quoted' fields into
     * database supported fields.
     * XXX: todo: escape characters for '[' and ']'.
     */
    private String convertClausePartToDBS(String constraints) {
        // obtain dbs for fieldname checks
        MMJdbc2NodeInterface dbs=cloudContext.mmb.getDatabase();
        String result="";
        int posa=constraints.indexOf('[');
        while (posa>-1) {
            int posb=constraints.indexOf(']',posa);
            if (posb==-1) {
                posa=-1;
            } else {
                String fieldname=constraints.substring(posa+1,posb);
                int posc=fieldname.indexOf('.',posa);
                if (posc==-1) {
                    fieldname=dbs.getAllowedField(fieldname);
                } else {
                    fieldname= fieldname.substring(0,posc+1)+
                        dbs.getAllowedField(fieldname.substring(posc+1));
                }
                result+=constraints.substring(0,posa)+fieldname;
                constraints=constraints.substring(posb+1);
                posa=constraints.indexOf('[');
            }
        }
        result=result+constraints;
        return result;
    }

    /**
     * Converts a constraint by turning all 'quoted' fields into
     * database supported fields.
     * XXX: todo: escape characters for '[' and ']'.
     */
    String convertClauseToDBS(String constraints) {
        if (constraints.startsWith("MMNODE")) return constraints;
        if (constraints.startsWith("ALTA")) return constraints.substring(5);
        String result="";
        int posa=constraints.indexOf('\'');
        while (posa>-1) {
            int posb=constraints.indexOf('\'',1);
            if (posb==-1) {
                posa=-1;
            } else {
                String part=constraints.substring(0,posa);
                result+=convertClausePartToDBS(part)+constraints.substring(posa,posb+1);
                constraints=constraints.substring(posb+1);
                posa=constraints.indexOf('\'');
            }
        }
        result+=convertClausePartToDBS(constraints);
        if (!constraints.startsWith("WHERE ")) result="WHERE "+result;
        return result;
    }

    public NodeList getList(String startNodes, String nodePath, String fields,
            String constraints, String orderby, String directions,
            String searchDir, boolean distinct) {

        // begin of check invalid search command
        org.mmbase.util.Encode encoder = new org.mmbase.util.Encode("ESCAPE_SINGLE_QUOTE");        
        // if(startNodes != null) startNodes = encoder.encode(startNodes);
        // if(nodePath != null) nodePath = encoder.encode(nodePath);
        // if(fields != null) fields = encoder.encode(fields);
        if(orderby != null) orderby  = encoder.encode(orderby);
        if(directions != null) directions  = encoder.encode(directions);
        if(searchDir != null) searchDir  = encoder.encode(searchDir);
        if(constraints != null && !validConstraints(constraints)) {
            throw new BridgeException("invalid contrain:" + constraints);
        }
        // end of check invalid search command
        
        String sdistinct="";
        int search = ClusterBuilder.SEARCH_BOTH;
        String pars ="";

        if (startNodes!=null) {
            pars+=" NODES='"+startNodes+"'";
        }
        if (nodePath != null && (!nodePath.trim().equals(""))) {
            pars+=" TYPES='"+nodePath+"'";
        } else {
            String message;
            message = "No nodePath specified.";
            log.error(message);
            throw new BridgeException(message);
        }

        if (fields == null) fields = "";
        pars += " FIELDS='"+fields+"'";

        if (orderby!=null) {
            pars+=" SORTED='"+orderby+"'";
        }
        if (directions!=null) {
          pars+=" DIR='"+directions+"'";
        }

        if (constraints!=null) {
          pars+=" WHERE='"+constraints.replace(' ','_')+"'";
        }

        if (distinct) {
            sdistinct="YES";
            pars+=" DISTINCT='YES'";
        }

        StringTagger tagger= new StringTagger(pars,' ','=',',','\'');
        if (searchDir!=null) {
            searchDir = searchDir.toUpperCase();
            if ("DESTINATION".equals(searchDir)) {
                search = ClusterBuilder.SEARCH_DESTINATION;
            } else if ("SOURCE".equals(searchDir)) {
                search = ClusterBuilder.SEARCH_SOURCE;
            } else if ("BOTH".equals(searchDir)) {
                search = ClusterBuilder.SEARCH_BOTH;
            } else if ("ALL".equals(searchDir)) {
                search = ClusterBuilder.SEARCH_ALL;
            } else if ("EITHER".equals(searchDir)) {
                search = ClusterBuilder.SEARCH_EITHER;
            }
        }

        Vector snodes = tagger.Values("NODES");
        Vector sfields = tagger.Values("FIELDS");
        Vector tables = tagger.Values("TYPES");
        Vector orderVec = tagger.Values("SORTED");
        Vector sdirection =tagger.Values("DIR"); // minstens een : UP
        if (sdirection==null) {
            sdirection=new Vector();
            sdirection.addElement("UP"); // UP == ASC , DOWN =DESC
        }
        ClusterBuilder clusters = cloudContext.mmb.getClusterBuilder();
        if (constraints!=null) {
            if (constraints.trim().equals("")) {
                constraints = null;
            } else {
                constraints=convertClauseToDBS(constraints);
            }
        }

        Integer hash=null; // result hash for cache
        Vector resultlist=null; // result vector
        // check multilevel cache if needed
        if (multilevel_cache.isActive()) {
            hash=multilevel_cache.calcHashMultiLevel(tagger);
            resultlist=(Vector)multilevel_cache.get(hash);
        }
        // if unavailable, obtain from database
        if (resultlist==null) {
            log.debug("result list is null, getting from database");
            resultlist = clusters.searchMultiLevelVector(snodes,sfields,sdistinct,tables,constraints,orderVec,sdirection,search);
        }
        // store result in cache if needed
        if (multilevel_cache.isActive() && resultlist!=null) {
            multilevel_cache.put(hash,resultlist,tables,tagger);
            resultlist=(Vector)resultlist.clone();
        }
        if (resultlist!=null) {
            // get authorization for this call only
            Authorization auth=mmbaseCop.getAuthorization();
            for (int i=resultlist.size()-1; i>=0; i--) {
                boolean check=true;
                MMObjectNode node=(MMObjectNode)resultlist.get(i);
                for (int j=0; check && (j<tables.size()); j++) {
                    int nodenr = node.getIntValue(tables.get(j)+".number");
                    if (nodenr!=-1) {
                        check=auth.check(userContext.getUserContext(),nodenr,Operation.READ);
                    }
                }
                if (!check) resultlist.remove(i);
            }
            NodeManager tempNodeManager = null;
            if (resultlist.size()>0) {
                tempNodeManager = new VirtualNodeManager((MMObjectNode)resultlist.get(0),this);
            }
            return new BasicNodeList(resultlist,this,tempNodeManager);
        } else {
            String message;
            message = "Parameters are invalid :" + pars + " - " + constraints;
            log.error(message);
            throw new BridgeException(message);
        }
    }

    /**
     * set the Context of the current Node
     */
    void setContext(int nodeNumber, String context) {
        mmbaseCop.getAuthorization().setContext(userContext.getUserContext(), nodeNumber, context);
    }

    /**
     * get the Context of the current Node
     */
    String getContext(int nodeNumber) {
        return mmbaseCop.getAuthorization().getContext(userContext.getUserContext(), nodeNumber);
    }

    /**
     * get the Contextes which can be set to this specific node
     */
    StringList getPossibleContexts(int nodeNumber) {
        return new BasicStringList(mmbaseCop.getAuthorization().getPossibleContexts(userContext.getUserContext(), nodeNumber));
    }

    public void setLocale(Locale l) {
        if (l == null) {
            locale = new Locale(cloudContext.mmb.getLanguage(), "");
        } else {
            locale = l;
        }
    }
    public Locale getLocale() {
        return locale;
    }

    /** returns false, when escaping wasnt closed, or when a ";" was found outside a escaped part (to prefent spoofing) */
    boolean validConstraints(String contraints) {
        // first remove all the escaped "'" ('' occurences) chars...                      
        String remaining = contraints;
        while(remaining.indexOf("''") != -1) {
            int start = remaining.indexOf("''");
            int stop = start + 2;
            if(stop < remaining.length()) {
                String begin = remaining.substring(0, start);
                String end = remaining.substring(stop);
                remaining = begin + end;
            }
            else {
                remaining = remaining.substring(0, start);
            }
        }        
        // assume we are not escaping... and search the string..
        // Keep in mind that at this point, the remaining string could contain different information 
        // than the original string. This doesnt matter for the next sequence...
        // but it is important to realize!
        while(remaining.length() > 0) {
	    if(remaining.indexOf('\'') != -1) {
		// we still contain a "'"
		int start = remaining.indexOf('\'');

		// escaping started, but no stop
		if(start == remaining.length())  {
		    log.warn("reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:" + contraints);
		    return false;
		}
            
		String notEscaped = remaining.substring(0, start);
		if(notEscaped.indexOf(';') != -1) {
		    log.warn("found a ';' outside the constraints(you should sql-escape the search query inside the jsp-page?)\noriginal:" + contraints + "\nnot excaped:" + notEscaped);
		    return false;
		}
                        
		int stop = remaining.substring(start + 1).indexOf('\'');
		if(stop < 0) {
		    log.warn("reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:" + contraints + "\nlast escaping:" + remaining.substring(start + 1));
		    return false;
		}
		// we added one to to start, thus also add this one to stop...
		stop = start + stop + 1;
            
		// when the last character was the stop of our escaping
		if(stop == remaining.length())  {
		    return true;
		}
            
		// cut the escaped part from the string, and continue with resting sting...
		remaining = remaining.substring(stop + 1);
	    }
	    else{
		if(remaining.indexOf(';')!= -1) {
		    log.warn("found a ';' inside our contrain:" + contraints);
		    return false;
		}
		return true;
	    }
        }
        return true;
    }
}
