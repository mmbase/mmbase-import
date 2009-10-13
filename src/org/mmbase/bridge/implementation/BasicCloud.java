/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.security.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation of Cloud. It wraps a.o. the core's ClusterBuilder and Typedef functionalities.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class BasicCloud implements Cloud, Cloneable, Comparable, SizeMeasurable, Serializable  {

    private static final long serialVersionUID = 1;

    private static long counter = 0;

    private static final Logger log = Logging.getLoggerInstance(BasicCloud.class);

    // lastRequestId
    // used to generate a temporary ID number
    // The Id starts at - 2 and is decremented each time a new id is asked
    // until Integer.MIN_VALUE is reached (after which counting starts again at -2).
    private static int lastRequestId = Integer.MIN_VALUE;


    protected final long count = ++counter;

    // link to cloud context
    private CloudContext cloudContext = null;

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

    // all transactions started by this cloud object (with createTransaction)
    protected Map transactions = new HashMap();

    // node managers cache
    protected Map nodeManagerCache = new HashMap();

    MMBaseCop mmbaseCop = null;

    protected UserContext userContext = null;

    private HashMap properties = new HashMap();

    private Locale locale;

    public int getByteSize() {
        return getByteSize(new SizeOf());
    }

    public int getByteSize(SizeOf sizeof) {
        return sizeof.sizeof(transactions) + sizeof.sizeof(nodeManagerCache);
    }

    /**
     *  basic constructor for descendant clouds (i.e. Transaction)
     * @param cloudName name of cloud
     * @param cloud parent cloud
     */
    BasicCloud(String cloudName, BasicCloud cloud) {
        cloudContext = cloud.cloudContext;
        locale = cloud.locale;
        name = cloudName;
        description = cloud.description;
        mmbaseCop = cloud.mmbaseCop;

        userContext = cloud.userContext;
        account = cloud.account;
    }

    /**
     * @param name name of cloud
     * @param authenticationType authentication type
     * @param loginInfo Map with login credentials
     * @param cloudContext cloudContext of cloud
     * @throws NotFoundException If MMBase not yet started, or shutting down.
     * @throws BridgeException   No security could be obtained.
     * @throws SecurityException  Could not perform login
     */
    BasicCloud(String name, String authenticationType, Map loginInfo, CloudContext cloudContext) {
        // get the cloudcontext and mmbase root...
        this.cloudContext = cloudContext;
        init();
        userContext = mmbaseCop.getAuthentication().login(authenticationType, loginInfo, null);
        if (userContext == null) {
            log.debug("Login failed");
            throw new java.lang.SecurityException("Login invalid (login-module: " + authenticationType + ")");
        }
        // end authentication...

        if (userContext.getAuthenticationType() == null) {
            log.warn("Security implementation did not set 'authentication type' in the user object.");
        }

        // normally, we want the cloud to read it's context from an xml file.
        // the current system does not support multiple clouds yet,
        // so as a temporary hack we set default values
        this.name = name;
        description = name;
    }

    /**
     * @param name name of cloud
     * @param authenticationType authentication type
     * @param loginInfo Map with login credentials
     * @param cloudContext cloudContext of cloud
     * @throws NotFoundException If MMBase not yet started, or shutting down.
     * @throws BridgeException   No security could be obtained.
     * @throws SecurityException  Could not perform login
     */
    BasicCloud(String name, UserContext user, CloudContext cloudContext) {
        // get the cloudcontext and mmbase root...
        this.cloudContext = cloudContext;
        init();
        userContext = user;
        if (userContext == null) {
            throw new java.lang.SecurityException("Login invalid: did not supply user object");
        }

        if (userContext.getAuthenticationType() == null) {
            log.warn("Security implementation did not set 'authentication type' in the user object.");
        }

        this.name = name;
        description = name;

    }

    private final void init() {
        log.debug("Initing " + this);
        MMBase mmb = BasicCloudContext.mmb;

        if (! mmb.getState()) {
            throw new NotFoundException("MMBase not yet, or not successfully initialized (check mmbase log)");
        }

        if (mmb.isShutdown()) {
            throw new NotFoundException("MMBase is shutting down.");
        }

        log.debug("Doing authentication");
        mmbaseCop = mmb.getMMBaseCop();

        if (mmbaseCop == null) {
            throw new BridgeException("Couldn't find the MMBaseCop. Perhaps your MMBase did not start up correctly; check application server and mmbase logs ");
        }
        log.debug("Setting up cloud object");
        locale = mmb.getLocale();

        // generate an unique id for this instance...
        account = "U" + uniqueId();
    }

    protected Authorization getAuthorization() {
        if (mmbaseCop == null) {
            synchronized(this) {

            }

        }
        return mmbaseCop.getAuthorization();
    }

    // Makes a node or Relation object based on an MMObjectNode
    BasicNode makeNode(MMObjectNode node, String nodeNumber) {
        int nodenr = node.getNumber();
        MMObjectBuilder parent = node.getBuilder();
        if (nodenr == -1) {
            int nodeid = Integer.parseInt(nodeNumber);
            if (parent instanceof TypeDef) {
                return new BasicNodeManager(node, this, nodeid);
            } else if (parent instanceof RelDef || parent instanceof TypeRel) {
                return new BasicRelationManager(node, this, nodeid);
            } else if (parent instanceof InsRel) {
                return new BasicRelation(node, this, nodeid);
            } else {
                return new BasicNode(node, this, nodeid);
            }
        } else {
            this.verify(Operation.READ, nodenr);
            if (parent instanceof TypeDef) {
                return new BasicNodeManager(node, this);
            } else if (parent instanceof RelDef || parent instanceof TypeRel) {
                return new BasicRelationManager(node, this);
            } else if (parent instanceof InsRel) {
                return new BasicRelation(node, this);
            } else {
                return new BasicNode(node, this);
            }
        }
    }

    public Node getNode(String nodeNumber) throws NotFoundException {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(getAccount(), nodeNumber);
        } catch (RuntimeException e) {
            throw new NotFoundException("Something went wrong while getting node with number '" + nodeNumber + "': " + e.getMessage() + " by cloud with account " + getAccount(), e);
        }
        if (node == null) {
            throw new NotFoundException("Node with number '" + nodeNumber + "' does not exist.");
        } else {
            BasicNode n  = makeNode(node, nodeNumber);
            add(n);
            return n;
        }
    }

    public final Node getNode(int nodeNumber) throws NotFoundException {
        return getNode("" + nodeNumber);
    }

    public final Node getNodeByAlias(String aliasname) throws NotFoundException {
        return getNode(aliasname);
    }

    public final Relation getRelation(int nodeNumber) throws NotFoundException {
        return getRelation("" + nodeNumber);
    }

    public final Relation getRelation(String nodeNumber) throws NotFoundException {
        return (Relation)getNode(nodeNumber);
    }

    public boolean hasNode(int nodeNumber) {
        return hasNode("" + nodeNumber, false);
    }

    public boolean hasNode(String nodeNumber) {
        return nodeNumber != null && hasNode(nodeNumber, false);
    }

    // check if anode exists.
    // if isrelation is true, the method returns false if the node is not a relation
    private boolean hasNode(String nodeNumber, boolean isrelation) {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(getAccount(), nodeNumber);
        } catch (Throwable e) {
            return false; // error - node inaccessible or does not exist
        }
        if (node == null) {
            return false; // node does not exist
        } else {
            if (isrelation && !(node.getBuilder() instanceof InsRel)) {
                return false;
            }
            return true;
        }
    }

    public boolean hasRelation(int nodeNumber) {
        return hasNode("" + nodeNumber, true);
    }

    public boolean hasRelation(String nodeNumber) {
        return hasNode(nodeNumber, true);
    }

    public NodeManagerList getNodeManagers() {
        List nodeManagers = new ArrayList();
        for (Iterator builders = BasicCloudContext.mmb.getBuilders().iterator(); builders.hasNext();) {
            MMObjectBuilder bul = (MMObjectBuilder)builders.next();
            if (!bul.isVirtual() && check(Operation.READ, bul.getNumber())) {
                nodeManagers.add(bul.getTableName());
            }
        }
        return new BasicNodeManagerList(nodeManagers, this);
    }

    /**
     * @since MMBase-1.8
     */
    BasicNodeManager getBasicNodeManager(MMObjectBuilder bul) throws NotFoundException {
        String nodeManagerName = bul.getTableName();
        // cache quicker, and you don't get 2000 nodetypes when you do a search....
        BasicNodeManager nodeManager = (BasicNodeManager)nodeManagerCache.get(nodeManagerName);
        if (nodeManager == null) {
            // not found in cache
            nodeManager = new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName, nodeManager);
        } else if (nodeManager.getMMObjectBuilder() != bul) {
            // cache differs
            nodeManagerCache.remove(nodeManagerName);
            nodeManager = new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName, nodeManager);
        }
        return nodeManager;
    }

    BasicNodeManager getBasicNodeManager(String nodeManagerName) throws NotFoundException {
        MMObjectBuilder bul = BasicCloudContext.mmb.getMMObject(nodeManagerName);
        // always look if builder exists, since otherwise
        if (bul == null) {
            throw new NotFoundException("Node manager with name '" + nodeManagerName + "' does not exist.");
        }
        return getBasicNodeManager(bul);

    }

    public final NodeManager getNodeManager(String nodeManagerName) throws NotFoundException {
        return getBasicNodeManager(nodeManagerName);
    }

    public boolean hasNodeManager(String nodeManagerName) {
        return BasicCloudContext.mmb.getMMObject(nodeManagerName) != null;
    }

    /**
     * Retrieves a node manager
     * @param nodeManagerId ID of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     * @throws NotFoundException node manager not found
     */
    public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException {
        TypeDef typedef = BasicCloudContext.mmb.getTypeDef();
        return getNodeManager(typedef.getValue(nodeManagerId));
    }

    /**
     * Retrieves a RelationManager.
     * Note that you can retrieve a manager with source and destination reversed.
     * @param sourceManagerId number of the NodeManager of the source node
     * @param destinationManagerId number of the NodeManager of the destination node
     * @param roleId number of the role
     * @return the requested RelationManager
     */
    RelationManager getRelationManager(int sourceManagerId, int destinationManagerId, int roleId) {
        Set set = BasicCloudContext.mmb.getTypeRel().getAllowedRelations(sourceManagerId, destinationManagerId, roleId);
        if (set.size() > 0) {
            Iterator i = set.iterator();
            MMObjectNode typeRel = (MMObjectNode) i.next();
            if (set.size() > 1 && (sourceManagerId != -1 || destinationManagerId != -1)) {
                int quality =
                    (typeRel.getIntValue("snumber") == sourceManagerId ? 1 : 0) +
                    (typeRel.getIntValue("dnumber") == destinationManagerId ? 1 : 0);

                while(i.hasNext()) {
                    MMObjectNode candidate = (MMObjectNode) i.next();
                    int candidateQuality =
                        (candidate.getIntValue("snumber") == sourceManagerId ? 1 : 0) +
                        (candidate.getIntValue("dnumber") == destinationManagerId ? 1 : 0);
                    if (candidateQuality > quality) {
                        typeRel = candidate;
                        quality = candidateQuality;
                    }
                }
            }

            return new BasicRelationManager(typeRel, this);
        } else {
            log.error("Relation " + sourceManagerId + "/" + destinationManagerId + "/" + roleId + " does not exist", new Exception());
            return null; // calling method throws exception
        }
    }

    public RelationManager getRelationManager(int number) throws NotFoundException {
        MMObjectNode n = BasicCloudContext.mmb.getTypeDef().getNode(number);
        if (n == null) {
            throw new NotFoundException("Relation manager with number " + number + " does not exist.");
        }
        if ((n.getBuilder() instanceof RelDef) || (n.getBuilder() instanceof TypeRel)) {
            return new BasicRelationManager(n, this);
        } else {
            throw new NotFoundException("Node with number " + number + " is not a relation manager.");
        }
    }

    public RelationManagerList getRelationManagers() {
        List v = BasicCloudContext.mmb.getTypeRel().getNodes();
        return new BasicRelationManagerList(v, this);
    }

    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) {
            throw new NotFoundException("Role '" + roleName + "' does not exist.");
        }
        // other settings of the cloud...
        TypeDef typedef = BasicCloudContext.mmb.getTypeDef();

        int n1 = typedef.getIntValue(sourceManagerName);
        if (n1 == -1) {
            throw new NotFoundException("Source type '" + sourceManagerName + "' does not exist.");
        }
        int n2 = typedef.getIntValue(destinationManagerName);
        if (n2 == -1) {
            throw new NotFoundException("Destination type '" + destinationManagerName + "' does not exist.");
        }
        RelationManager rm = getRelationManager(n1, n2, r);
        if (rm == null) {
            throw new NotFoundException("Relation manager from '" + sourceManagerName + "' to '" + destinationManagerName + "' as '" + roleName + "' does not exist.");
        } else {
            return rm;
        }
    }

    public RelationManager getRelationManager(NodeManager source, NodeManager destination, String roleName) throws NotFoundException {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) {
            throw new NotFoundException("Role '" + roleName + "' does not exist.");
        }
        RelationManager rm = getRelationManager(source.getNumber(), destination.getNumber(), r);
        if (rm == null) {
            throw new NotFoundException("Relation manager from '" + source + "' to '" + destination + "' as '" + roleName + "' does not exist.");
        } else {
            return rm;
        }

    }

    public boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1)  return false;
        TypeDef typedef = BasicCloudContext.mmb.getTypeDef();
        int n1 = typedef.getIntValue(sourceManagerName);
        if (n1 == -1) return false;
        int n2 = typedef.getIntValue(destinationManagerName);
        if (n2 == -1) return false;

        return BasicCloudContext.mmb.getTypeRel().contains(n1, n2, r);
        // return getRelationManager(n1, n2, r) != null;
    }

    public boolean hasRole(String roleName) {
        return BasicCloudContext.mmb.getRelDef().getNumberByName(roleName) != -1;
    }

    public boolean  hasRelationManager(NodeManager source, NodeManager destination, String roleName) {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) return false;
        return BasicCloudContext.mmb.getTypeRel().contains(source.getNumber(), destination.getNumber(), r);
        // return getRelationManager(source.getNumber(), destination.getNumber(), r) != null;
    }

    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) {
            throw new NotFoundException("Role '" + roleName + "' does not exist.");
        }
        return getRelationManager(r);
    }

    public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        NodeManager n1 = null;
        if (sourceManagerName != null)
            n1 = getNodeManager(sourceManagerName);
        NodeManager n2 = null;
        if (destinationManagerName != null)
            n2 = getNodeManager(destinationManagerName);
        return getRelationManagers(n1, n2, roleName);
    }

    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException {
        if (sourceManager != null) {
            return sourceManager.getAllowedRelations(destinationManager, roleName, null);
        } else if (destinationManager != null) {
            return destinationManager.getAllowedRelations(sourceManager, roleName, null);
        } else if (roleName != null) {
            int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
            if (r == -1) {
                throw new NotFoundException("Role '" + roleName + "' does not exist.");
            }
            Vector v = BasicCloudContext.mmb.getTypeRel().searchVector("rnumber==" + r);
            return new BasicRelationManagerList(v, this);
        } else {
            return getRelationManagers();
        }
    }

    public boolean hasRelationManager(String roleName) {
        return BasicCloudContext.mmb.getRelDef().getNumberByName(roleName) != -1;
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
        if (lastRequestId > Integer.MIN_VALUE) {
            lastRequestId--;
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
        return createTransaction(null, false);
    }

    public Transaction createTransaction(String name) throws AlreadyExistsException {
        return createTransaction(name, false);
    }

    public Transaction createTransaction(String name, boolean overwrite) throws AlreadyExistsException {
        if (name == null) {
            name = "Tran" + uniqueId();
        } else {
            Transaction oldtransaction = (Transaction)transactions.get(name);
            if (oldtransaction != null) {
                if (overwrite) {
                    oldtransaction.cancel();
                } else {
                    throw new AlreadyExistsException("Transaction with name " + name + "already exists.");
                }
            }
        }
        Transaction transaction = new BasicTransaction(name, this);
        transactions.put(name, transaction);
        return transaction;
    }

    public Transaction getTransaction(String name) {
        Transaction tran = (Transaction)transactions.get(name);
        if (tran == null) {
            return createTransaction(name, false);
        } else {
            log.debug("Using existing transaction " + tran);
            return tran;
        }
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UserContext getUser() {
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
        return mmbaseCop != null && mmbaseCop.getAuthorization().check(userContext, nodeID, operation);
    }

    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (READ, WRITE, CREATE, OWN)
    * @param nodeID the node on which to check the operation
    */
    void verify(Operation operation, int nodeID) {
        while (mmbaseCop == null) {
            synchronized(this) {
                if (mmbaseCop == null) {
                    throw new org.mmbase.security.SecurityException("No MMBaseCop in " + this); // mmbase cop can be null if deserialization not yet ready.
                }
            }
        }
        mmbaseCop.getAuthorization().verify(userContext, nodeID, operation);
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
        return mmbaseCop != null && mmbaseCop.getAuthorization().check(userContext, nodeID, srcNodeID, dstNodeID, operation);
    }

    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (CREATE, CHANGE_RELATION)
    * @param nodeID the node on which to check the operation
    * @param srcNodeID the source node for this relation
    * @param dstNodeID the destination node for this relation
    */
    void verify(Operation operation, int nodeID, int srcNodeID, int dstNodeID) {
        mmbaseCop.getAuthorization().verify(userContext, nodeID, srcNodeID, dstNodeID, operation);
    }

    // javadoc inherited
    public NodeList getList(Query query) {
        log.debug("get List");
        NodeList result;
        if (query.isAggregating()) { // should this perhaps be a seperate method? --> Then also 'isAggregating' not needed any more
            result = getResultNodeList(query);
        } else {
            result =  getSecureList(query);
        }
        if (query instanceof NodeQuery) {
            NodeQuery nq = (NodeQuery) query;
            String pref = nq.getNodeStep().getAlias();
            if (pref == null) pref = nq.getNodeStep().getTableName();
            result.setProperty(NodeList.NODESTEP_PROPERTY, pref);
        }
        return result;
    }

    /*
    // javadoc inherited
    public NodeList getList(NodeQuery query) {
        return getSecureNodes(query);
    }
    */


    /**
     * Aggregating query result.
     * @param query query to execute
     * @return list of nodes
     * @since MMBase-1.7
     */
    protected NodeList getResultNodeList(Query query) {
        log.debug("Resultnode list");
        try {

            boolean checked = setSecurityConstraint(query);

            if (! checked) {
                log.warn("Query " + query + " could not be completely modified by security: Aggregated result might be wrong");
            }
            ResultBuilder resultBuilder = new ResultBuilder(BasicCloudContext.mmb, query);
            List resultList = resultBuilder.getResult();
            query.markUsed();
            NodeManager tempNodeManager = new VirtualNodeManager(query, this);
            NodeList resultNodeList = new BasicNodeList(resultList, tempNodeManager);
            resultNodeList.setProperty(NodeList.QUERY_PROPERTY, query);
            return resultNodeList;
        } catch (SearchQueryException sqe) {
            throw new BridgeException(sqe);
        }
    }

    /**
     * Result with all Cluster - MMObjectNodes, with cache. Security is not considered here (the
     * query is executed thoughtlessly). The security check is done in getSecureNodes, which calls
     * this one.
     * @param query query to execute
     * @return list of cluster nodes
     * @since MMBase-1.7
     */
    protected List    getClusterNodes(Query query) {
        ClusterBuilder clusterBuilder = BasicCloudContext.mmb.getClusterBuilder();
        List resultList = clusterBuilder.getClusterNodes(query);
        query.markUsed();
        return resultList;
    }

    /**
     * @param query add security constaint to this query
     * @return is query secure
     * @since MMBase-1.7
     */
    boolean setSecurityConstraint(Query query) {
        if (mmbaseCop == null) return false;
        Authorization auth = mmbaseCop.getAuthorization();
        if (query instanceof BasicQuery) {  // query should alway be 'BasicQuery' but if not, for some on-fore-seen reason..
            BasicQuery bquery = (BasicQuery) query;
            if (bquery.isSecure()) { // already set, and secure
                return true;
            } else {
                if (bquery.queryCheck == null) { // not set already, do it now.
                    Authorization.QueryCheck check = auth.check(userContext, query, Operation.READ);
                    if (log.isDebugEnabled()) {
                        log.debug("FOUND security check " + check + " FOR " + query);
                    }
                    bquery.setSecurityConstraint(check);
                }
                return bquery.isSecure();
            }
        } else {
            // should not happen
            if (query != null) {
                log.warn("Don't know how to set a security constraint on a " + query.getClass().getName());
            } else {
                log.warn("Don't know how to set a security constraint on NULL");
            }
        }
        return false;
    }

    public StringList getPossibleContexts() {
        if (mmbaseCop == null) {
            throw new NotFoundException("MMBase not yet, or not successfully initialized (check mmbase log)");
        }
        return new BasicStringList(mmbaseCop.getAuthorization().getPossibleContexts(getUser()));
    }

    void   checkNodes(BasicNodeList resultNodeList, Query query) {
        Authorization auth = getAuthorization();
        resultNodeList.autoConvert = false; // make sure no conversion to Node happen, until we are ready.

        if (log.isDebugEnabled()) {
            log.trace(resultNodeList);
        }

        log.debug("Starting read-check");
        // resultNodeList is now a BasicNodeList; read restriction should only be applied now
        // assumed it though, that it contain _only_ MMObjectNodes..

        // get authorization for this call only

        List steps = query.getSteps();
        Step nodeStep = null;
        if (query instanceof NodeQuery) {
            nodeStep = ((NodeQuery) query).getNodeStep();
        }
        log.debug("Creating iterator");
        ListIterator li = resultNodeList.listIterator();
        while (li.hasNext()) {
            log.debug("next");
            Object o = li.next();
            if (log.isDebugEnabled()) {
                log.debug(o.getClass().getName());
            }
            MMObjectNode node = (MMObjectNode)o;
            boolean mayRead = true;
            for (int j = 0; mayRead && (j < steps.size()); ++j) {
                Step step = (Step) steps.get(j);
                int nodenr;
                if (step.equals(nodeStep)) {
                    nodenr = node.getIntValue("number");
                } else {
                    String pref = step.getAlias();
                    if (pref == null) {
                        pref = step.getTableName();
                    }
                    nodenr = node.getIntValue(pref + ".number");
                }
                if (nodenr != -1) {
                    mayRead = auth.check(userContext, nodenr, Operation.READ);
                }
            }

            if (!mayRead) {
                li.remove();
            }
        }
        resultNodeList.autoConvert = true;


    }


    /**
     * Result with Cluster Nodes (checked security)
     * @param query query to execute
     * @return lisr of cluster nodes
     * @since MMBase-1.7
     */
    protected NodeList getSecureList(Query query) {

        boolean checked = setSecurityConstraint(query);

        List resultList = getClusterNodes(query);

        if (log.isDebugEnabled()) {
            log.debug("Creating NodeList of size " + resultList.size());
        }

        // create resultNodeList
        NodeManager  tempNodeManager = new VirtualNodeManager(query, this);

        BasicNodeList resultNodeList = new BasicNodeList(resultList, tempNodeManager);
        resultNodeList.setProperty(NodeList.QUERY_PROPERTY, query);

        if (! checked) {
            checkNodes(resultNodeList, query);
        }

        return resultNodeList;
    }

    //javadoc inherited
    public NodeList getList(
        String startNodes,
        String nodePath,
        String fields,
        String constraints,
        String orderby,
        String directions,
        String searchDir,
        boolean distinct) {

        if ((nodePath==null) || nodePath.equals("")) throw new BridgeException("Node path cannot be empty - list at least one nodemanager.");
        Query query = Queries.createQuery(this, startNodes, nodePath, fields, constraints, orderby, directions, searchDir, distinct);
        return getList(query);
    }


    public void setLocale(Locale l) {
        if (l == null) {
            locale = new Locale(BasicCloudContext.mmb.getLanguage(), "");
        } else {
            locale = l;
        }
    }
    public Locale getLocale() {
        return locale;
    }

    public boolean mayRead(int nodeNumber) {
        return mayRead(nodeNumber + "");
    }

    public boolean mayRead(String nodeNumber) {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(getAccount(), nodeNumber);
        } catch (RuntimeException e) {
            throw new NotFoundException("Something went wrong while getting node with number '" + nodeNumber + "': " + e.getMessage(), e);
        }
        if (node == null) {
            throw new NotFoundException("Node with number '" + nodeNumber + "' does not exist.");
        } else {
            int nodenr = node.getNumber();
            if (nodenr == -1) {
                return true; // temporary node
            } else {
                return check(Operation.READ, node.getNumber()); // check read access
            }
        }
    }

    // javadoc inherited
    public Query createQuery() {
        return new BasicQuery(this);
    }

    public NodeQuery createNodeQuery() {
        return new BasicNodeQuery(this);
    }


    public Query createAggregatedQuery() {
        return new BasicQuery(this, true);
    }




    /**
     * Based on multi-level query. Returns however 'normal' nodes based on the last step. This is a
     * protected function, which is used in the implemetnedion of getRelatedNodes, getRelations of
     * NodeManager
     *
     * Before it executes the query, the fields of the query are checked. All and only fields of the
     * 'last' NodeManager and the relation should be queried.  If fields are present already, but
     * not like this, an exception is thrown. If not fields are present, the rights fields are added
     * first (if the query is still unused, otherwise trhows Exception).
     * @param query query to execute
     * @return list of normal nodes
     *
     * @throws BridgeException If wrong fields in query or could not be added.
     *
     * @since MMBase-1.7
     */
    protected NodeList getLastStepList(Query query) {
        return null;
    }


    /**
     * Compares this cloud to the passed object.
     * Returns 0 if they are equal, -1 if the object passed is a Cloud and larger than this cloud,
     * and +1 if the object passed is a Cloud and smaller than this cloud.
     * @todo There is no specific order in which clouds are ordered at this moment.
     *       Currently, all clouds within one CloudContext are treated as being equal.
     * @param o the object to compare it with
     * @return compare number
     */
    public int compareTo(Object o) {
        int h1 = ((Cloud)o).getCloudContext().hashCode();
        int h2 = cloudContext.hashCode();

        // mm: why not simply return h2 - h1?

        if (h1 > h2) {
            return -1;
        } else if (h1 < h2) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares this cloud to the passed object, and returns true if they are equal.
     * @todo Add checks for multiple clouds in the same cloudcontext
     *       Currently, all clouds within one CloudContext are treated as being equal.
     * @param o the object to compare it with
     * @return is equal
     */
    public boolean equals(Object o) {
        // XXX: Currently, all clouds (i.e. transactions/user clouds) within a CloudContext
        // are treated as the 'same' cloud. This may change in future implementations
        if (o instanceof Cloud) {
            Cloud oc = (Cloud) o;
            return cloudContext.equals(oc.getCloudContext()) && userContext.equals(oc.getUser());
        } else {
            return false;
        }
    }

    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }

    public Map getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public Collection getFunctions(String setName) {
        FunctionSet set = FunctionSets.getFunctionSet(setName);
        if (set == null) {
            throw new NotFoundException("Functionset with name " + setName + "does not exist.");
        }
        // get functions
        return set.getFunctions();
    }

    public Function getFunction(String setName, String functionName) {
        FunctionSet set = FunctionSets.getFunctionSet(setName);
        if (set == null) {
            throw new NotFoundException("Functionset with name '" + setName + "' does not exist.");
        }
        Function fun = set.getFunction(functionName);
        if (fun == null) {
            throw new NotFoundException("Function with name '" + functionName + "' does not exist in function set with name '"+ setName + "'.");
        }
        return fun;
    }

    public NodeList createNodeList() {
        return new BasicNodeList(Collections.EMPTY_LIST, this);
    }

    public RelationList createRelationList() {
        return new BasicRelationList(Collections.EMPTY_LIST, this);
    }

    public NodeManagerList createNodeManagerList() {
        return new BasicNodeManagerList(Collections.EMPTY_LIST, this);
    }

    public RelationManagerList createRelationManagerList() {
        return new BasicRelationManagerList(Collections.EMPTY_LIST, this);
    }

    /**
     * Checks wether the current transaction contains the given node.
     */
    boolean contains(MMObjectNode node) {
        return false;
    }


    /**
     * Ignored by basic cloud. See {@link BasicTransaction#add(String)}.
     */
    void add(String currentObjectContext) {
    }
    /**
     * Ignored by basic cloud. See {@link BasicTransaction#add(String)}.
     */
    int  add(BasicNode node) {
        return node.getNumber();
    }

    /**
     * Ignored by basic cloud. See {@link BasicTransaction#remove(String)}.
     */
    void remove(String currentObjectContext) {
    }

    protected void _readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        userContext = (UserContext)in.readObject();
        cloudContext = LocalContext.getCloudContext();
        description = name;
        properties = (HashMap) in.readObject();
        locale     = (Locale) in.readObject();
        log.info("Reading " + this);
        org.mmbase.util.ThreadPools.jobsExecutor.execute(new BasicCloudStarter());
        transactions = new HashMap();
        nodeManagerCache = new HashMap();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        _readObject(in);
    }


    protected void _writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeObject(userContext);
        HashMap props = new HashMap();
        Iterator i = properties.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if ((key instanceof Serializable) && (value instanceof Serializable)) {
                props.put(key, value);
            }
        }
        out.writeObject(props);
        out.writeObject(locale);
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        _writeObject(out);
    }

    class BasicCloudStarter implements Runnable {
        public void run() {
            synchronized(BasicCloud.this) {
                LocalContext.getCloudContext().assertUp();
                BasicCloud.this.init();
                if (BasicCloud.this.userContext == null) {
                    throw new java.lang.SecurityException("Login invalid: did not supply user object");
                }
                if (BasicCloud.this.userContext.getAuthenticationType() == null) {
                    log.warn("Security implementation did not set 'authentication type' in the user object.");
                }
                log.service("Deserialized " + BasicCloud.this);
                BasicCloud.this.notifyAll();
            }
        }
    }


    protected void finalize() throws Throwable {
        log.debug("Finalizing cloud of " + getUser() + " with id " + getAccount() + " transactions: " + transactions.keySet());
        super.finalize();
    }

    public String toString() {
        UserContext uc = getUser();
        return  "BasicCloud " + count +  "'" + getName() + "' of " + (uc != null ? uc.getIdentifier() : "NO USER YET") + " @" + Integer.toHexString(hashCode());
    }

    /*
    public Cloud getNonTransactionalCloud() {
        return this;
    }
    */

}
