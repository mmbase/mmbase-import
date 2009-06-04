/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.*;

import org.mmbase.security.UserContext;
import org.mmbase.util.functions.Function;

/**
 * A Cloud is a collection of Nodes (and relations that are also nodes).
 * A Cloud is part of a CloudContexts.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id$
 */
public interface Cloud {

    /**
     * This property can contain hints on how to deal with XML fields. Things like 'xml', 'plain',
     * 'wiki'.
     */
    public static final String PROP_XMLMODE     = "org.mmbase.xml-mode";


    /**
     * The cloud itself may have been stored in a user's 'session', using this the key stored in
     * this property.
     */
    public static final String PROP_SESSIONNAME = "org.mmbase.cloud.sessionname";


    /**
     * With the Cloud a ServletRequest can be associated and stored in the 'property.
     *
     * @since MMBase-1.9
     */
    public static final String PROP_REQUEST     = "request";


    /**
     * If you set this property on the cloud to true, validation errors will not be fatal, and nodes
     * can be saved anyways.
     *
     * @since MMBase-1.8.6
     */
    public static final String PROP_IGNOREVALIDATION  = "org.mmbase.cloud.ignore-validation";

    /**
     * Returns the node with the specified number from this cloud. The returned
     * node is a new instance of <code>Node</code> with a reference to this
     * instance of <code>Cloud</code>.
     *
     * @param number                  the number of the requested node
     * @return                        the requested node
     * @throws NotFoundException  if the specified node could not be found
     */
    public Node getNode(int number) throws NotFoundException;

    /**
     * Returns the node with the specified number from this cloud.
     * If the string passed is not a number, the string is assumed to be an alias.
     * The returned node is a new instance of <code>Node</code> with a reference to this
     * instance of <code>Cloud</code>.
     *
     * @param number    a string containing the number or alias of the requested node
     * @return          the requested node
     * @throws NotFoundException  if the specified node could not be found
     */
    public Node getNode(String number) throws NotFoundException;


    /**
     * Returns the node with the specified alias from this cloud. The returned
     * node is a new instance of <code>Node</code> with a reference to this
     * instance of <code>Cloud</code>.
     *
     * @param alias                   the alias of the requested node
     * @return                        the requested node
     * @throws NotFoundException  if the specified node could not be found
     */
    public Node getNodeByAlias(String alias) throws NotFoundException;

    /**
     * Returns the relation with the specified number from this cloud. The returned
     * node is a new instance of <code>Relation</code> with a reference to this
     * instance of <code>Cloud</code>.
     *
     * @param number                  the number of the requested node
     * @return                        the requested node
     * @throws NotFoundException  if the specified node could not be found
     * @throws ClassCastException  if the specified node is not a relation
     * @since  MMBase-1.6
     */
    public Relation getRelation(int number) throws NotFoundException;

    /**
     * Returns the relation with the specified number from this cloud.
     * If the string passed is not a number, the string is assumed to be an alias.
     * The returned node is a new instance of <code>Relation</code> with a reference to this
     * instance of <code>Cloud</code>.
     *
     * @param number    a string containing the number or alias of the requested node
     * @return          the requested node
     * @throws NotFoundException  if the specified node could not be found
     * @throws ClassCastException  if the specified node is not a relation
     * @since  MMBase-1.6
     */
    public Relation getRelation(String number) throws NotFoundException;

    /**
     * Determines whether a node with the specified number exists in this cloud.
     * Note that this method does not determien whether you may actually access (read) this node,
     * use {@link #mayRead(int)} to determine this.
     *
     * @param number    the number of the node
     * @return          true if the node exists
     * @since  MMBase-1.6
     */
    public boolean hasNode(int number);

    /**
     * Determines whether a node with the specified number is available from this cloud.
     * If the string passed is not a number, the string is assumed to be an alias.
     * Note that this method does not determien whether you may actually access (read) this node,
     * use {@link #mayRead(int)} to determine this.
     *
     * @param number a string containing the number or alias of the requested node
     * @return          true if the node exists
     * @since  MMBase-1.6
     */
    public boolean hasNode(String number);

    /**
     * Determines whether a relation with the specified number exists in this cloud.
     * The node returns true if a Node exists and is a relation.
     * Note that this method does not determien whether you may actually access (read) this node,
     * use {@link #mayRead(int)} to determine this.
     *
     * @param number    the number of the node
     * @return          true if the relation exists
     * @since  MMBase-1.6
     */
    public boolean hasRelation(int number);

    /**
     * Determines whether a relation with the specified number is available from this cloud.
     * If the string passed is not a number, the string is assumed to be an alias.
     * The node returns true if a Node exists and is a relation.
     * Note that this method does not determien whether you may actually access (read) this node,
     * use {@link #mayRead(int)} to determine this.
     *
     * @param number a string containing the number or alias of the requested node
     * @return          true if the relation exists
     * @since  MMBase-1.6
     */
    public boolean hasRelation(String number);

    /**
     * Determines whether a node with the specified number is accessible for the user - that is,
     * the user has sufficient rights to read the node.
     * The node must exist - the method throws an exception if it doesn't.
     *
     * @param number  the number of the requested node
     * @return          true if the node is accessible
     * @throws NotFoundException  if the specified node could not be found
     * @since  MMBase-1.6
     */
    public boolean mayRead(int number);

    /**
     * Check whether an action is allowed
     * @param action Action to perform
     * @param parameters parameters passed into this action
     * @return <code>true</code> when allowed
     * @since MMBase-1.9
     */
    public boolean may(org.mmbase.security.Action action, org.mmbase.util.functions.Parameters parameters);

    /**
     * Determines whether a node with the specified number is accessible for the user - that is,
     * the user has sufficient rights to read the node.
     * If the string passed is not a number, the string is assumed to be an alias.
     * The node must exist - the method throws an exception if it doesn't.
     *
     * @param number a string containing the number or alias of the requested node
     * @return          true if the node is accessible
     * @throws NotFoundException  if the specified node could not be found
     * @since  MMBase-1.6
     */
    public boolean mayRead(String number);

    /**
     * Returns all node managers available in this cloud.
     *
     * @return  a <code>NodeManagerList</code> containing all node managers
     *          available in this cloud.
     */
    public NodeManagerList getNodeManagers();

    /**
     * Returns the specified node manager.
     *
     * @param name                the name of the requested node manager
     * @return                    the requested node manager
     * @throws NotFoundException  if the specified node manager could not be found
     */
    public NodeManager getNodeManager(String name) throws NotFoundException;

    /**
     * Returns whether the specified node manager exists.
     *
     * @param name  the name of the requested node manager
     * @return      <code>true</code> if the specified node manager exists
     */
    public boolean hasNodeManager(String name);

    /**
     * Returns the specified node manager.
     *
     * @param nodeManagerId       Unique ID of the NodeManager to retrieve
     * @return                    the requested node manager
     * @throws NotFoundException  if the specified node manager could not be found
     * @since  MMBase-1.6
     */
    public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException;

    /**
     * Returns the specified relation manager.
     *
     * @param relationManagerId       Unique ID of the RelationManager to retrieve
     * @return                    the requested relation manager
     * @throws NotFoundException  if the specified relation manager could not be found
     * @since  MMBase-1.6
     */
    public RelationManager getRelationManager(int relationManagerId) throws NotFoundException;

    /**
     * Returns the specified relation manager.
     *
     * @param sourceManagerName      name of the node manager of the source node
     * @param destinationManagerName name of the node manager of the destination node
     * @param roleName               name of the role
     * @return                       the requested relation manager
     * @throws NotFoundException     if the specified relation manager could not be found
     */
    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException;


    /**
     * Returns the specified relation manager.
     *
     * @param sourceManager          the node manager of the source node
     * @param destinationManager     the node manager of the destination node
     * @param roleName               name of the role
     * @return                       the requested relation manager
     * @throws NotFoundException     if the specified relation manager could not be found
     * @since MMBase-1.7
     */
    public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException;



    /**
     * Returns whether the specified relation manager exists.
     *
     * @param sourceManagerName      name of the node manager of the source node
     * @param destinationManagerName name of the node manager of the destination node
     * @param roleName               name of the role
     * @return                       <code>true</code> if the specified relation manager could be found
     */
    public boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName);


    /**
     * Returns whether the specified relation manager exists.
     *
     * @param sourceManager         name of the node manager of the source node
     * @param destinationManager    name of the node manager of the destination node
     * @param roleName              name of the role
     * @return                      <code>true</code> if the specified relation manager could be found
     * @since MMBase-1.7
     */
    public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName);


    /**
     * Returns whether the specified role exists.
     * @param roleName              name of the role
     * @return                      <code>true</code> if the specified role could  be found
     * @since MMBase-1.7
     */
    public boolean hasRole(String roleName);


    /**
     * Returns the specified relation manager.
     *
     * @param roleName               name of the role
     * @return                       the requested relation manager
     * @throws NotFoundException     if the specified relation manager could not be found
     */
    public RelationManager getRelationManager(String roleName) throws NotFoundException;

    /**
     * Returns whether the specified relation manager exists.
     *
     * @param roleName   name of the role
     * @return           <code>true</code> if the specified relation manager exists
     */
    public boolean hasRelationManager(String roleName);

    /**
     * Returns all relation managers available in this cloud.
     *
     * @return  a <code>RelationManagerList</code> containing all relation
     *          managers available in this cloud
     */
    public RelationManagerList getRelationManagers();

    /**
     * Returns all relation managers available in this cloud that follow the specified filter.
     *
     * @param sourceManagerName the name of the manager for the source of the relation
     * @param destinationManagerName the name of the manager for the destination of the relation
     * @param roleName the rolename
     * @return  a <code>RelationManagerList</code> containing all relation
     *          managers that follow this filter
     * @throws NotFoundException     if one of the specified relation managers or the rolename could not be found
     */
    public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName,  String roleName) throws NotFoundException;

    /**
     * Returns all relation managers available in this cloud that follow the specified filter.
     *
     * @param sourceManager the manager for the source of the relation
     * @param destinationManager the manager for the destination of the relation
     * @param roleName the rolename
     * @return  a <code>RelationManagerList</code> containing all relation
     *          managers that follwo thsi filter
     * @throws NotFoundException     if one of the specified relation managers or the rolename could not be found
     */
    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager,
                String roleName) throws NotFoundException;

    /**
     * Returns the context to which this cloud belongs.
     *
     * @return  a <code>CloudContext</code> to which this cloud belongs
     */
    public CloudContext getCloudContext();

    /**
     * Creates a transaction on this cloud. The returned
     * <code>Transaction</code> will have a generic ID.
     *
     * @return a <code>Transaction</code> on this cloud
     */
    public Transaction createTransaction();

    /**
     * Creates a transaction on this cloud with a specified name.
     *
     * @param name                     an unique name to use for the transaction
     * @return                         a <code>Transaction</code> on this cloud
     * @throws AlreadyExistsException  if a transaction with the specified name allready exists
     */
    public Transaction createTransaction(String name) throws AlreadyExistsException;

    /**
     * Creates a transaction on this cloud with a specified name.
     *
     * @param name                    an unique name to use for the transaction
     * @param overwrite               if <code>true</code>, cancels and replaces
     *                                any existing transaction of this name for the current user
     * @return                         a <code>Transaction</code> on this cloud
     * @throws AlreadyExistsException  if a transaction with the specified name allready
     *                                 exists and overwrite is <code>false</code>
     */
    public Transaction createTransaction(String name, boolean overwrite) throws AlreadyExistsException;

    /**
     * Returnes the transaction with the specified name.
     * If no active transaction exists, a new transaction is created.
     *
     * @param name  the name of the requested transaction
     * @return      the requested transaction
     */
    public Transaction getTransaction(String name);


    /**
     * Returns the name of this cloud. The name of the cloud is the string "mmbase" unless this
     * Cloud is a {@link Transaction}.
     *
     * @return the name of this cloud
     */
    public String getName();

    /**
     * This may return {@link #getName}, but in principable it could have been localized using the
     * value also returned by {@link #getLocale}.
     *
     * @return the description of this cloud
     */
    public String getDescription();

    /**
     * Who is using this cloud.
     *
     * @return the User object describing who is using this cloud now.
     */
    public UserContext getUser();

    /**
     * Returns a list of virtual nodes that are composed by fields of other
     * nodes.
     * Starting at one or more specified nodes traversals are made according to
     * a specified path. One traversal makes up one virtual node. All possible
     * traversals that can be made starting at one or more nodes of the same
     * type and following a specified path are stored in the returned list.
     *
     * Suppose we have defined the following:
     *
     * <pre>
     * - A node manager recordcompany containing a field name.
     * - A node manager artist containing a field name.
     * - A node manager url containing a field description and url.
     * - A relation type related between recordcompany and artist.
     * - A relation type related between artist and url.
     * - A relation type homepagerel between artist and url.
     * </pre>
     *
     * If node 100 has a node manager called recordcompany we can do
     * the following to get a list of the record company's artists and all urls
     * belonging
     * to these artist (including nodes found through the related relation and
     * the homepagerel relation):
     * <pre>
     * getList("100", "recordcompany,artist,url",
     *         "artist.name,description,url", null, null, null, null, true);
     * </pre>
     * This call returns a list of virtual nodes with the fields artist.name,
     * description and url for every valid traversal.
     *
     * <p>
     * If we only want to list homepage urls of the artists we do:
     * <pre>
     * getList("100", "recordcompany,artist,url",
     *         "artist.name,description,homepagerel,url", null, null, null,
     *         null, true);
     * </pre>
     *
     * <p>
     * If we want to list all url's except the the homepage urls we do:
     * <pre>
     * getList("100", "recordcompany,artist,url",
     *         "artist.name,description,related,url", null, null, null, null, true);
     * </pre>
     *
     * <p>
     * If node 200 also has a node manager with name recordcompany we can get
     * the info from their artist together with the info of the artist from the
     * first company by also putting number 200 in the first parameter:
     * <pre>
     * getList("100,200", "recordcompany,artist,url",
     *         "artist.name,description,related,url", null, null, null, null, true);
     * </pre>
     *
     * For more information about the <code>constraints</code> parameter consult
     * {@link NodeManager#getList(String constraints, String orderby, String
     * directions)}.
     *
     * @param startNodes    A comma separated list of node numbers that should
     *                      be used as a starting point for all traversals
     *                      or <code>null</code> if all nodes of the first node
     *                      manager in <code>nodePath</code> should be used.
     * @param nodePath      A comma separated list of node manager names
     *                      which specifies the path that should be followed.
     *                      It is possible to explicitly specify a relation
     *                      manager that should be used to go from one node to
     *                      an other. If no relation manager is specified
     *                      between two nodes, all possible relation managers
     *                      that can be used to go to the next specified node in
     *                      the path are followed.
     * @param fields        A comma separated list of field names that will make
     *                      up the returned virtual
     *                      nodes. A fieldname can be prefixed with the
     *                      original node manager name of the field and a dot
     *                      in cases where more than one node manager in the
     *                      path has a field with the same name.
     * @param constraints   Constraints to prevent nodes from being
     *                      included in the resulting list which would normally
     *                      by included or <code>null</code> if no constraints
     *                      should be applied.
     * @param orderby       A comma separated list of field names on which the
     *                      returned list should be sorted or <code>null</code>
     *                      if the order of the returned virtual nodes doesn't
     *                      matter.
     * @param directions    A comma separated list of values indicating wether
     *                      to sort up (ascending) or down (descending) on the
     *                      corresponding field in the <code>orderby</code>
     *                      parameter or <code>null</code> if sorting on all
     *                      fields should be up.
     *                      The value DOWN (case insensitive) indicates
     *                      that sorting on the corresponding field should be
     *                      down, all other values (including the
     *                      empty value) indicate that sorting on the
     *                      corresponding field should be up.
     *                      If the number of values found in this parameter are
     *                      less than the number of fields in the
     *                      <code>orderby</code> parameter, all fields that
     *                      don't have a corresponding direction value are
     *                      sorted according to the last specified direction
     *                      value.
     * @param searchDir     Determines how directionality affects the search.
     *                      This is a string with the following possible values:<br />
     *                      <code>"both"</code>, which is the default, searches for all
     *                      valid relations through a path, checking full directionality
     *                      of relations where appropriate.
     *                      <code>"destination"</code> searches for only those relations
     *                      in a path where valid relations exist from source to destination,
     *                      in the order of the nodemanagers given in the nodePath.
     *                      <code>"source"</code> searches for only those relations
     *                      in a path where valid relations exist from destination to source,
     *                      in the order of the nodemanagers given in the nodePath.
     *                      <code>"all"</code> searches all existing relations, and does
     *                      not check on directionality.
     *                      A value of <code>null</code> or any other values than those
     *                      listed above are ignored. In that case, search is
     *                      treated as if the default (<code>"both"</code>) was specified.
     * @param distinct      <code>true</code> if nodes who allready exist in
     *                      the list should not be added to the list.
     *                      <code>false</code> if all nodes should be added to
     *                      the list even if a node with exactly the same field
     *                      values is allready present.
     * @return              a list of virtual nodes
     */
    public NodeList getList(String startNodes, String nodePath, String fields,
            String constraints, String orderby, String directions,
            String searchDir, boolean distinct);

    /**
     * Executes a query and returns the result as a Cluster Node List (also if the query is a {@link NodeQuery}).
     * @param query Query to execute
     * @return Cluster Node List
     *
     * @see org.mmbase.storage.search.SearchQuery
     * @since MMBase-1.7
     */
    public NodeList getList(Query query);


    /**
     * Create an empty Query, which can be filled, and used in {@link #getList(Query)}.
     * @return empty Query
     * @since MMBase-1.7
     */
    public Query createQuery();


    /*
     * TODO: Why has there to be a difference between aggregated and non-aggregaged queries?
     * @since MMBase-1.7
     */
    public Query createAggregatedQuery();


    /**
     * Create an empty NodeQuery, which can be filled, and used in {@link NodeManager#getList(NodeQuery)} or
     * {@link #getList(Query)} (but then no 'real' node are returned). The query can be used on NodeManager only when at
     * least one step is added, and {@link NodeQuery#setNodeStep} is called.
     * @return empty NodeQuery
     * @since MMBase-1.7
     */
    public NodeQuery createNodeQuery();


    /**
     * Sets a locale for this <code>Cloud</code> instance.
     * @param locale To which locale it must be set. It can be null, in which case it will be reset to a default.
     *
     * @since MMBase-1.6
     */
    public void setLocale(Locale locale);

   /**
     * Gets the locale assocatied with this <code>Cloud</code> instance.
     * @return Locale of this Cloud instance
     *
     * @since MMBase-1.6
     */
    public Locale getLocale();

    /**
     * Retrieves a property previously set for this cloud. If this Cloud has a 'parent' cloud
     * (ie. this Cloud is a {@link Transaction}), then this will also mirror properties in this
     * parent cloud.
     * @see #setProperty(Object, Object)
     * @param key the key of the property
     * @return the property value
     * @since MMBase-1.8
     */
    public Object getProperty(Object key);

    /**
     * Sets a property for this cloud object.
     * This can be used as a kind of 'environment' variables.
     * @param key the key of the property
     * @param value the property value
     * @since MMBase-1.8
     */
    public void setProperty(Object key, Object value);

    /**
     * Retrieves all properties previously set for this cloud.
     * @return all properties
     * @since MMBase-1.8
     */
    public Map<Object, Object> getProperties();

    /**
     * Returns all Function objects from a function set.
     * Function sets group functions by name, and are configured in the functionset.xml configuration file.
     * In each entry in the returned map, the key is the function name, and the value is a
     * {@link org.mmbase.util.functions.Function} object.
     *
     * @since MMBase-1.8
     * @param setName name of the function set
     * @return a Set of {@link org.mmbase.util.functions.Function} objects.
     * @throws NotFoundException if the function set does not exist
     */
    public Collection<Function<?>> getFunctions(String setName);

    /**
     * Returns a Function object from a function set.
     * Function sets group functions by name, and are configured in the functionset.xml configuration file.
     * The object returned is a {@link org.mmbase.util.functions.Function} object.
     *
     * @since MMBase-1.8
     * @param setName name of the function set
     * @param functionName name of the function
     * @return a {@link org.mmbase.util.functions.Function} object.
     * @throws NotFoundException if the function set or the function do not exist
     */
    public Function<?> getFunction(String setName, String functionName);

    /**
     * Returns a new, empty node list for this cloud
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public NodeList createNodeList();

    /**
     * Returns a new, empty relation list for this cloud
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public RelationList createRelationList();

    /**
     * Returns a new, empty node manager list for this cloud
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public NodeManagerList createNodeManagerList();

    /**
     * Returns a new, empty relation manager list for this cloud
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public RelationManagerList createRelationManagerList();

    /**
     * Contacts the security implementation to find out to which possible contexts are
     * available to the current user.
     *
     * @return A StringList containing the contexts which can be used by teh suer
     * @throws SecurityException   When appropriate rights to perform this are lacking (read rights)
     */
    public StringList getPossibleContexts();



    /**
     * Returns a cloud which is not a Transaction.
     * @return This cloud or a parent cloud if this cloud is a {@link Transaction}
     * @since MMBase-1.9.1
     */
    public Cloud getNonTransactionalCloud();


    /**
     * Shutdown MMBase, if you are allowed to do so.
     * @since MMBase-1.9
     * @throws SecurityException If you are not allowed.
     */
    public void shutdown();

}
