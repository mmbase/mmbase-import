/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.io.File;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

import org.mmbase.cache.Cache;
import org.mmbase.cache.NodeListCache;
import org.mmbase.cache.AggregatedResultCache;
import org.mmbase.cache.QueryResultCache;

import org.mmbase.module.ParseException;
import org.mmbase.module.builders.DayMarkers;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;

import org.mmbase.module.gui.html.EditState;  //argh

import org.mmbase.storage.StorageManagerFactory;
import org.mmbase.storage.StorageException;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class is the base class for all builders.
 * It offers a list of routines which are useful in maintaining the nodes in the MMBase
 * object cloud.
 * <br />
 * Builders are the core of the MMBase system. They create, delete and search the MMObjectNodes.
 * Most manipulations concern nodes of that builders type. However, a number of retrieval routines extend
 * beyond a builders scope and work on the cloud in general, allowing some ease in retrieval of nodes.
 * The basic routines in this class can be extended to handle more specific demands for nodes.
 * Most of these 'extended builders' will be stored in mmbase.org.builders or mmbase.org.corebuilders.
 * Examples include relation builders or builders for handling binary data such as images.
 * The various builders are registered by the 'TypeDef' builder class (one of the core builders, itself
 * an extension of this class).
 *
 * @author Daniel Ockeloen
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Eduard Witteveen
 * @author Johannes Verelst
 * @author Rob van Maris
 * @author Michiel Meeuwissen
 * @version $Id: MMObjectBuilder.java,v 1.1 2008-02-15 14:07:22 nklasens Exp $
 */
public class MMObjectBuilder extends MMTable {

    /** Max size of the object type cache */
    public final static int OBJ2TYPE_MAX_SIZE = 20000;

    /** Default size of the temporary node cache */
    public final static int TEMPNODE_DEFAULT_SIZE = 1024;

    /** Default replacements for method getHTML() */
    public final static String DEFAULT_ALINEA = "<br />&#160;<br />";
    public final static String DEFAULT_EOL = "<br />";


    /**
     * Parameters for the GUI function
     * @since MMBase-1.7
     */

    public final static Parameter[] GUI_PARAMETERS = {
        new Parameter("field",    String.class),
        Parameter.LANGUAGE, // should add Locale
        new Parameter("session",  String.class),
        Parameter.RESPONSE,
        Parameter.REQUEST
    //       field, language, session, response, request) Returns a (XHTML) gui representation of the node (if field is '') or of a certain field. It can take into consideration a http session variable name with loging information and a language");

    };

    /**
     * Parameters for the age function
     * @since MMBase-1.7
     */

    public final static Parameter[] AGE_PARAMETERS = {};

    /**
     * The cache that contains the last X types of all requested objects
     * @since 1.7
     */
    private static Cache typeCache;

    /**
     * Results of getNodes
     * @since 1.7
     */
    protected static NodeListCache listCache = NodeListCache.getCache();

    static {
        typeCache = new Cache(OBJ2TYPE_MAX_SIZE) {
            public String getName()        { return "TypeCache"; }
            public String getDescription() { return "Cache for node types";}
        };
        typeCache.putCache();
    }

    /**
     * The cache that contains the X last requested nodes
     * @scope protected
     */
    public static org.mmbase.cache.NodeCache nodeCache = org.mmbase.cache.NodeCache.getCache();

    /**
     * Collection for temporary nodes,
     * Used by the Temporarynodemanager when working with transactions
     * The default size is 1024.
     * @rename to Map temporaryNodes
     * @scope  protected
     */
    public static Hashtable TemporaryNodes = new Hashtable(TEMPNODE_DEFAULT_SIZE);

    /**
     * The class used to store and retrieve data in the database that is currently in use.
     * @deprecated use MMBase.getMMBase().getDatabase() or mmb.getDatabase() instead
     */
    public static MMJdbc2NodeInterface database = null;

    /**
     * Determines whether the cache is locked.
     * A locked cache can be read, and nodes can be removed from it (allowing it to
     * clean invalid nodes), but nodes cannot be added.
     * Needed for committing nodes from transactions.
     */
    private static int cacheLocked=0;

    private static final Logger log = Logging.getLoggerInstance(MMObjectBuilder.class);

    /**
     * The current builder's object type
     * Retrieved from the TypeDef builder.
     * @scope private, use getObjectType()
     */
    public int oType=-1;

    /**
     * Description of the builder in the currently selected language
     * Not that the first time the builder is created, this value is what is stored in the TypeDef table.
     */
    public String description="Base Object";

    /**
     * Descriptions of the builder per language
     * Can be set with the &lt;descriptions&gt; tag in the xml builder file.
     */
    public Hashtable descriptions;

    /**
     * Contains the list of fieldnames as they used in the database.
     * The list (which is based on input from the xml builder file)
     * should be sorted on the order of fields as they are defined in the tabel.
     * The first two fields are 'otype' and 'owner'.
     * The field 'number' (the actual first field of a database table record) is not included in this collection.
     * @deprecated this vector should not be used - if the order of the fields is an issue, use getFields(sortorder).
     */
    public Vector sortedDBLayout = null;

    /**
     * The default search age for this builder.
     * Used for intializing editor search forms (see HtmlBase)
     * Default value is 31. Can be changed with the &lt;searchage&gt; tag in the xml builder file.
     */
    public String searchAge="31";

    /**
     * Determines whether the cache need be refreshed.
     * Seems useless, as this value is never changed (always true)
     * @see #processSearchResults
     */
    public static final boolean REPLACE_CACHE = true;

    /**
     * Determines whether changes to this builder need be broadcasted to other known mmbase servers.
     * This setting also governs whether the cache for relation builders is emptied when a relation changes.
     * Actual broadcasting (and cache emptying) is initiated in the 'database' object, when
     * changes are commited to the database.
     * By default, all builders broadcast their changes, with the exception of the TypeDef builder.
     */
    public boolean broadcastChanges = true;

    /**
     *  Maintainer information for builder registration
     *  Set with &lt;builder maintainer="mmbase.org" version="0"&gt; in the xml builder file
     */
    String maintainer = "mmbase.org";

    /**
     * Default output when no data is available to determine a node's GUI description
     */
    static String GUI_INDICATOR = "no info";

    /** Collections of (GUI) names (singular) for the builder's objects, divided by language
     */
    Hashtable singularNames;

    /** Collections of (GUI) names (plural) for the builder's objects, divided by language
     */
    Hashtable pluralNames;

    /** List of remote observers, which are notified when a node of this type changes
     */
    Vector remoteObservers = new Vector();

    /** List of local observers, which are notified when a node of this type changes
     */
    Vector localObservers = new Vector();

    /**
     * Full filename (path + buildername + ".xml") where we loaded the builder from
     * It is relative from the '/builders/' subdir
     */
    String xmlPath = "";

    // contains the builder's field definitions
    protected Hashtable fields;


    /**
     * Reference to the builders that this builder extends.
     * @since MMBase-1.6.2 (parentBuilder in 1.6.0)
     */
    private Stack ancestors = new Stack();

    /**
     * Version information for builder registration
     * Set with &lt;builder maintainer="mmbase.org" version="0"&gt; in the xml
     * builder file
     */
    private int version=0;

    /**
     * Determines whether a builder is virtual (data is not stored in a database).
     */
    protected boolean virtual=false;

    /**
     * Contains lists of builder fields in specified order
     * (ORDER_CREATE, ORDER_EDIT, ORDER_LIST, ORDER_SEARCH)
     */
    private HashMap sortedFieldLists = new HashMap();

    /** Properties of a specific Builder.
     * Specified in the xml builder file with the <properties> tag.
     * The use of properties is determined by builder
     */
    private Hashtable properties = null;

    /**
     * Whenever a list should always return the correct types of nodes
     * old behaviour is not...
     * This is needed, when you want for example use the following code:
     * <pre>
     * MMObjectNode node = MMObjectBuilder.getNode(123);
     * Enumeration relations = node.getRelations("posrel");
     * while(enumeration.hasNext()) {
     *   MMObjectNode posrel = (MMObjectNode) enumeration.getElement();
     *   int pos = posrel.getIntValue("pos");
     * }
     * </pre>
     * When the return of correct node types is the following code has to be used..
     * <pre>
     * MMObjectNode node = MMObjectBuilder.getNode(123);
     * Enumeration relations = node.getRelations("posrel");
     * while(enumeration.hasNext()) {
     *   MMObjectNode posrel = (MMObjectNode) enumeration.getElement();
     *   // next lines is needed when the return of correct nodes is not true
     *   posrel = posrel.parent.getNode(posrel.getNumber());
     *   // when the line above is skipped, the value of pos will always be -1
     *   int pos = posrel.getIntValue("pos");
     * }
     * </pre>
     * Maybe this should be fixed in some otherway,.. but when we want to use the inheritance  you
     * _really_ need this thing turned into true.
     */
    private static boolean CORRECT_NODE_TYPES = true;

    /**
     * Maximum number of nodes to return on a query (-1 means no limit, and is also the default)
     */
    private int maxNodesFromQuery = -1;

    /**
     * Max length of a query, informix = 32.0000 so we assume a bit less for other databases
     */
    private static final int MAX_QUERY_SIZE = 20000;

    /**
     * The string that can be used inside the builder.xml as property,
     * to define the maximum number of nodes to return.
     */
    private static String  MAX_NODES_FROM_QUERY_PROPERTY = "max-nodes-from-query";

    /**
     * Constructor.
     * Derived builders should provide their own constructors, rather than use this one.
     */
    public MMObjectBuilder() {}

    private void initAncestors() {
        if (! ancestors.empty()) {
            ((MMObjectBuilder) ancestors.peek()).init();
        }
    }


    /**
     * Initializes this builder
     * The property 'mmb' needs to be set for the builder before this method can be called.
     * The method retrieves data from the TypeDef builder, or adds data to that builder if the
     * current builder is not yet registered.
     * @return true if init was completed, false if uncompleted.
     * @see #create
     */
    public boolean init() {
        synchronized(mmb) { // synchronized on mmb because can only init builder if mmb is inited completely

            // skip initialisation if oType has been set (happend at end of init)
            // note that init can be called twice
            if (oType != -1) return true;

            log.debug("Init of builder " + getTableName());

            // XXX: deprecated
            database = mmb.getDatabase();

            // first make sure parent builder is initalized
            initAncestors();


            if (!created()) {
                log.info("Creating table for builder " + tableName);
                if (!create() ) {
                    // can't create buildertable.
                    // Throw an exception
                    throw new BuilderConfigurationException("Cannot create table for "+getTableName()+".");
                };
            }
            TypeDef typeDef = mmb.getTypeDef();
            // only deteremine otype if typedef is available,
            // or this is typedef itself (have to start somewhere)
            if (((typeDef != null)  && (typeDef.getObjectType()!=-1)) || (this == typeDef)) {
                oType = typeDef.getIntValue(tableName);
                if (oType == -1) { // no object type number defined yet
                    if (log.isDebugEnabled()) log.debug("Creating typedef entry for " + tableName);
                    MMObjectNode node = typeDef.getNewNode("system");
                    node.setValue("name", tableName);

                    // This sucks:
                    if (description == null) description = "not defined in this language";

                    node.setValue("description", description);

                    oType = mmb.getDatabase().getDBKey();
                    log.debug("Got key " + oType);
                    node.setValue("number", oType);
                    // for typedef, set otype explictly, as it wasn't set in getNewNode()
                    if (this == typeDef) {
                        node.setValue("otype", oType);
                    }
                    log.debug("Inserting the new typedef node");
                    node.insert("system");
                    // for typedef, call it's parents init again, as otype is only now set
                    if (this == typeDef) {
                        initAncestors();
                    }
                }
            } else {
                // warn if typedef was not created
                // except for the 'object' and 'typedef' basic builders
                if(!tableName.equals("typedef") && !tableName.equals("object")) {
                    log.warn("init(): for tablename(" + tableName + ") -> can't get to typeDef");
                    return false;
                }
            }

            // add temporary fields
            checkAddTmpField("_number");
            checkAddTmpField("_exists");

            // get property dof maximum number of queries..
            String property = getInitParameter(MAX_NODES_FROM_QUERY_PROPERTY);
            if(property != null) {
                try {
                    maxNodesFromQuery = Integer.parseInt(property);
                    log.debug(getTableName() + " returns no more than " + maxNodesFromQuery + " records from a query.");
                } catch(NumberFormatException nfe) {
                    log.warn("property:" + MAX_NODES_FROM_QUERY_PROPERTY + " contained an invalid integer value:'" + property +"'(" + nfe + ")");
                }
            }
        }
        return true;
    }


    /**
     * Creates a new builder table in the current database.
     */
    public boolean create() {
        log.debug(tableName);
        return mmb.getDatabase().create(this);
    }

    /**
     * Drops the builder table from the current database
     * @deprecated use {@link #delete()}
     */
    public boolean drop() {
        delete();
        return true;
    }

    /**
     * Removes the builder from the storage.
     */
    public void delete() {
        log.service("trying to drop table of builder: '"+tableName+"' with database class: '"+mmb.getDatabase().getClass().getName()+"'");
        StorageManagerFactory factory = mmb.getStorageManagerFactory();
        if (factory!=null) {
            try {
                factory.getStorageManager().delete(this);
            } catch (StorageException se) {
                throw new RuntimeException(se);
            }
        } else {
            if(size() > 0) throw new RuntimeException("cannot drop a builder, that still contains nodes");
            if (!mmb.getDatabase().drop(this))  throw new RuntimeException("cannot drop a builder");
        }
    }

    /**
     * Tests whether the data in a node is valid (throws an exception if this is not the case).
     * @param node The node whose data to check
     * @throws org.mmbase.module.core.InvalidDataException
     *   If the data was unrecoverably invalid (the references did not point to existing objects)
     */
    public void testValidData(MMObjectNode node) throws InvalidDataException {
        return;
    };

    /**
     * Insert a new, empty, object of a certain type.
     * @param oType The type of object to create
     * @param owner The administrator creating the node
     * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
     *        The basic routine does not create any nodes this way and always fails.
     */
    public int insert(int oType, String owner) {
        return -1;
    }

    /**
     * Insert a new object (content provided) in the cloud, including an entry for the object alias (if provided).
     * This method indirectly calls {@link #preCommit}.
     * @param owner The administrator creating the node
     * @param node The object to insert. The object need be of the same type as the current builder.
     * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
     */
    public int insert(String owner, MMObjectNode node) {
        try {
            int n;
            n = mmb.getDatabase().insert(this,owner,node);
            // it is in the database now, all caches can allready be invalidated, this makes sure
            // that imediate 'select' after 'insert' will be correct'.
            QueryResultCache.invalidateAll(this);

            if (n>=0) safeCache(new Integer(n),node);
            String alias = node.getAlias();
            if (alias != null) createAlias(n,alias);    // add alias, if provided
            return n;
        } catch(RuntimeException e) {
            // do we really wanna catch our exceptions here?
            // the only purpose now to catch them here, is to log
            // failures of inserts..
            String msg = "Failure(" + e + ") inserting node:\n" + node + "\n" + Logging.stackTrace(e);
            log.error(msg);
            throw e;
        }
    }

    /**
     * Once a insert is done in the editor this method is called.
     * @param ed Contains the current edit state (editor info). The main function of this object is to pass
     *        'settings' and 'parameters' - value pairs that have been the during the edit process.
     * @param node The node thatw as inserted
     * @return An <code>int</code> value. It's meaning is undefined.
     *        The basic routine returns -1.
     * @deprecated This method doesn't seem to fit here, as it references a gui/html object ({@link org.mmbase.module.gui.html.EditState}),
     *    endangering the separation between content and layout, and has an undefined return value.
     */
    public int insertDone(EditState ed, MMObjectNode node) {
        return -1;
    }

    /**
     * Check and make last changes before calling {@link #commit} or {@link #insert}.
     * This method is called by the editor. This differs from {@link #preCommit}, which is called by the database system
     * <em>during</em> the call to commit or insert.
     * @param ed Contains the current edit state (editor info). The main function of this object is to pass
     *        'settings' and 'parameters' - value pairs that have been the during the edit process.
     * @param node The node that was inserted
     * @return An <code>int</code> value. It's meaning is undefined.
     *        The basic routine returns -1.
     * @deprecated This method doesn't seem to fit here, as it references a gui/html object ({@link org.mmbase.module.gui.html.EditState}),
     *    endangering the separation between content and layout. It also has an undefined return value.
     */
    public int preEdit(EditState ed, MMObjectNode node) {
        return -1;
    }

    /**
     * This method is called before an actual write to the database is performed.
     * It is called from within the database routines, unlike {@link #preEdit}, which is called by the editor.
     * That is, preCommit is enforced, while preEdit is not (depending on the editor used).
     * @param node The node to be committed.
     * @return the node to be committed (possibly after changes have been made).
     */
    public MMObjectNode preCommit(MMObjectNode node) {
        return node;
    }

    /**
     * Commit changes to this node to the database. This method indirectly calls {@link #preCommit}.
     * Use only to commit changes - for adding node, use {@link #insert}.
     * @param node The node to be committed
     * @return true if commit successful
     */
    public boolean commit(MMObjectNode node) {
        boolean result = mmb.getDatabase().commit(this, node);
        // change is in database, caches can be invalidated immediately
        QueryResultCache.invalidateAll(this);
        return result;
    }

    /**
     *  Creates an alias for a node, provided the OAlias builder is loaded.
     *  @param number the to-be-aliased node's unique number
     *  @param alias the aliasname to associate with the object
     *  @return if the alias could be created
     */
    public boolean createAlias(int number, String alias) {
        if (mmb.getOAlias() != null) {
            if (getNode(alias) != null ) {  // this alias already exists! Don't add a new one!
                return false;
            }
            mmb.getOAlias().createAlias(alias, number);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the builder that this builder extends.
     *
     * @since MMBase-1.6
     * @return the extended (parent) builder, or null if not available
     */
    public MMObjectBuilder getParentBuilder() {
        if (ancestors.empty()) return null;
        return (MMObjectBuilder) ancestors.peek();
    }
    /**
     * Gives the list of parent-builders.
     *
     * @since MMBase-1.6.2

     */
    protected List  getAncestors() {
        return ancestors;
    }

    /**
     * Creates list of descendant-builders.
     *
     * @since MMBase-1.6.2
     */
    public List getDescendants() {
        ArrayList result = new ArrayList();
        Enumeration e = mmb.getMMObjects();
        while(e.hasMoreElements()) {
            MMObjectBuilder builder = (MMObjectBuilder) e.nextElement();
            if (builder.isExtensionOf(this)) {
                result.add(builder);
            }
        }
        return result;
    }


    /**
     * Sets the builder that this builder extends, and registers it in the database layer.
     * @param parent the extended (parent) builder, or null if not available
     *
     * @since MMBase-1.6
     */
    public void setParentBuilder(MMObjectBuilder parent) throws StorageException {
        // mmb.getDatabase().registerParentBuilder(parent, this);
        ancestors.addAll(parent.getAncestors());
        ancestors.push(parent);
    }

    /**
     * Checks wether this builder is an extension of the argument builder
     *
     * @since MMBase-1.6.2
     */
    public boolean isExtensionOf(MMObjectBuilder o) {
        return ancestors.contains(o);
    }

    /**
     * Get a new node, using this builder as its parent. The new node is not a part of the cloud
     * yet, and thus has the value -1 as a number. (Call {@link #insert} to add the node to the
     * cloud).  This method is also called inside database operations, so it may not do new database
     * operations itself (that might cause dead-locks).
     * @param owner The administrator creating the new node.
     * @return A newly initialized <code>MMObjectNode</code>.
     */
    public MMObjectNode getNewNode(String owner) {
        MMObjectNode node = new MMObjectNode(this);
        node.setValue("number", -1);
        node.setValue("owner", owner);
        node.setValue("otype", oType);
        setDefaults(node);
        return node;
    }

    /**
     * Sets defaults for a node. Fields "number", "owner" and "otype" are not set by this method.
     * @param node The node to set the defaults of.
     */
    public void setDefaults(MMObjectNode node) {
    }

    /**
     * In setDefault you could want to generate unique values for fields (if the field is 'unique').
     * @since MMBase-1.7
     */
    protected String setUniqueValue(MMObjectNode node, String field, String baseValue) {
        int seq = 0;
        boolean found = false;
        String value = baseValue;
        try {
            while (! found) {
                NodeSearchQuery query = new NodeSearchQuery(this);
                value = baseValue + seq;
                BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(query.getField(getField(field)), value);
                query.setConstraint(constraint);
                if (getNodes(query).size() == 0) {
                    found = true;
                    break;
                }
                seq++;
            }
        } catch (SearchQueryException e) {
            value =   baseValue + System.currentTimeMillis();
        }
        node.setValue(field,  value);
        return value;
    }

    /**
     * In setDefault you could want to generate unique values for fields (if the field is 'unique').
     * @since MMBase-1.7
     */

    protected int setUniqueValue(MMObjectNode node, String field, int offset) {
        int seq = offset;
        boolean found = false;
        try {
            while (! found) {
                NodeSearchQuery query = new NodeSearchQuery(this);
                BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(query.getField(getField(field)), new Integer(seq));
                query.setConstraint(constraint);
                if (getNodes(query).size() == 0) {
                    found = true;
                    break;
                }
                seq++;
            }
        } catch (SearchQueryException e) {
            seq =  (int) System.currentTimeMillis() / 1000;
        }
        node.setValue(field,  seq);
        return seq;
    }


    /**
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        if (oType != node.getOType()) {
            // fixed comment's below..??
            // prevent from making database inconsistent(say remove nodes from inactive builder)
            // the builder we are in is not the actual builder!!
            // ? why not an node.remove()
            throw new RuntimeException("Builder with name:" + getTableName() + "(" + oType + ") is not the actual builder.");
        }

        removeSyncNodes(node);

        // removes the node FROM THIS BUILDER
        // seems not a very logical call, as node.parent is the node's actual builder,
        // which may - possibly - be very different from the current builder
        mmb.getDatabase().removeNode(this, node);

        // change is in database, caches can be invalidated immediately
        QueryResultCache.invalidateAll(this);
    }      

    /**
     * Removes the syncnodes to this node. This is logical, but also needed to maintain database
     * integrety.
     *
     * @since MMBase-1.7
     */
    protected void removeSyncNodes(MMObjectNode node) {
        try {
            MMObjectBuilder syncnodes = mmb.getBuilder("syncnodes");
            NodeSearchQuery query = new NodeSearchQuery(syncnodes);
            Object numericalValue = new Integer(node.getNumber());
            BasicStepField field = query.getField(syncnodes.getField("localnumber"));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(field,
                                                                                 numericalValue);
            query.setConstraint(constraint);
            Iterator syncs = syncnodes.getNodes(query).iterator();
            while (syncs.hasNext()) {
                MMObjectNode syncnode = (MMObjectNode) syncs.next();
                syncnode.parent.removeNode(syncnode);
                log.service("Removed syncnode " + syncnode);
            }
        } catch (SearchQueryException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Remove the relations of a node.
     * @param node The node whose relations to remove.
     */
    public void removeRelations(MMObjectNode node) {
        Vector relsv=getRelations_main(node.getNumber());
        if (relsv != null) {
            for(Enumeration rels=relsv.elements(); rels.hasMoreElements(); ) {
                // get the relation node
                MMObjectNode relnode=(MMObjectNode)rels.nextElement();
                // determine the true builder for this node
                // (node.parent is always InsRel, but otype
                //  indicates any derived builders, such as AuthRel)
                MMObjectBuilder bul = mmb.getMMObject(mmb.getTypeDef().getValue(relnode.getOType()));
                // remove the node using this builder
                // circumvent problem in database layers
                bul.removeNode(relnode);
            }
        }
    }

    /**
     * Is this node cached at this moment?
     * @param number The number of the node to check.
     * @return <code>true</code> if the node is in the cache, <code>false</code> otherwise.
     */
    public boolean isNodeCached(int number) {
        return nodeCache.containsKey(new Integer(number));
    }

    /**
     * Determinw ehether this builder is virtual.
     * A virtual builder represents nodes that are not stored or retrieved directly
     * from the database, but are created as needed.
     * @return <code>true</code> if the builder is virtual.
     */
    public boolean isVirtual() {
        return virtual;
    }

    /**
     * Returns the objecttype (otype).
     * This is similar to the otype field value of objects of teh builder,
     * and the number of the bilder's object in the typedef builder.
     * In other words: getNode(getObjectType()) returns this builder's
     * objectnode.
     * @return the objecttype
     */
    public int getObjectType() {
        return oType;
    }

    /**
     * Stores a node in the cache provided the cache is not locked.
     */
    public void safeCache(Integer n, MMObjectNode node) {
        synchronized(nodeCache) {
            if(cacheLocked == 0) {
                nodeCache.put(n, node);
            }
        }
    }

    /**
     * Locks the node cache during the commit of a node.
     * This prevents the cache from gaining an invalid state
     * during the commit.
     */
    public boolean safeCommit(MMObjectNode node) {
        boolean res = false;
        try {
            synchronized(nodeCache) {
                cacheLocked++;
            }
            nodeCache.remove(new Integer(node.getNumber()));
            res = node.commit();
            if (res) {
                nodeCache.put(new Integer(node.getNumber()),node);
            };
        } finally {
            synchronized(nodeCache) {
                cacheLocked--;
            }
        }
        return res;
    }

    /**
     * Locks the node cache during the insert of a node.
     * This prevents the cache from adding the node, which
     * means that the next time the node is read it is 'refreshed'
     * from the database
     */
    public int safeInsert(MMObjectNode node, String userName) {
        int res=-1;
        try {
            synchronized(nodeCache) {
                cacheLocked++;
            }
            // determine valid username
            if ((userName == null) || (userName.length() <= 1 )) { // may not have owner of 1 char??
                userName = node.getStringValue("owner");
                log.info("Found username " + (userName == null ? "NULL" : userName));
            }
            res = node.insert(userName);
            if (res > -1) {
                nodeCache.put(new Integer(res), node);
            }
        } finally {
            synchronized(nodeCache) {
                cacheLocked--;
            }
        }
        return res;
    }

    /**
     * Retrieves an object's type. If necessary, the type is added to the cache.
     * @todo when something goes wrong, the method currently catches the exception and returns -1.
     *       It should actually throw a NotFoundException instead.
     * @sql uses sql statements. will be removed once the new storage layer is in use
     * @param number The number of the node to search for
     * @return an <code>int</code> value which is the object type (otype) of the node.
     */
    public int getNodeType(int number) {
        // assertment
        if(number < 0 ) throw new RuntimeException("node number was invalid(" + number + ")" );
        // check the cache
        Integer numberValue = new Integer(number);
        Integer otypeValue = (Integer)typeCache.get(numberValue);
        if (otypeValue != null) {
            return otypeValue.intValue();
        }
        // check whether to use the factory
        if (mmb.getStorageManagerFactory()!=null) {
            try {
                int otype = mmb.getStorageManager().getNodeType(number);
                typeCache.put(numberValue, new Integer(otype));
                return otype;
            } catch(StorageException se) {
                // throw new NotFoundException(se);
                log.error(Logging.stackTrace(se));
                return -1;
            }
        } else {
            int otype=-1;
            MultiConnection con = null;
            Statement stmt2 = null;
            try {
                if (otype==-1 || otype==0) {
                    // first get the otype to select the correct builder
                    con   = mmb.getConnection();
                    stmt2 = con.createStatement();
                    String sql = "SELECT " + mmb.getDatabase().getOTypeString() + " FROM " + mmb.baseName + "_object WHERE " + mmb.getDatabase().getNumberString() + "=" + number;
                    ResultSet rs = stmt2.executeQuery(sql);
                    try {
                        if (rs.next()) {
                            otype = rs.getInt(1);
                            // hack hack need a better way
                            if (otype != 0) {
                                typeCache.put(numberValue,new Integer(otype));
                            }
                        } else {
                            // throw new NotFoundException(msg);
                            log.error("Could not find the otype for node " + number + " (no records) using following query:" + sql);
                            return -1;
                        }
                    } finally {
                        rs.close();
                    }
                 }
            } catch (SQLException e) {
                // throw new NotFoundException(e);
                log.error(Logging.stackTrace(e));
                return -1;
            } finally {
                mmb.closeConnection(con,stmt2);
            }
            return otype;
        }
   }

    /**
     * Retrieves a node based on a unique key. The key is either an entry from the OAlias table
     * or the string-form of an integer value (the number field of an object node).
     * @param key The value to search for
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     * @deprecated Use {@link #getNode(java.lang.String)} instead.
     */
    public MMObjectNode getAliasedNode(String key) {
        return getNode(key);
    }

    /**
     * Convert virtual nodes to real nodes based on their otype
     *
     * Normally a multirelations-search will return virtual nodes. These nodes
     * will only contain values which where specified in the field-vector.
     * This method will make real nodes of those virtual nodes.
     *
     * @param List containing virtual nodes
     * @return List containing real nodes, directly from this Builders
     * @since MMBase-1.6.2
     */
    protected List getNodes(Collection virtuals)  {
        List            result  = new ArrayList();

        List virtualNumbers = new ArrayList();

        try {
            int numbersSize = 0;
            NodeSearchQuery query = new NodeSearchQuery(this);
            BasicStep       step  = (BasicStep) query.getSteps().get(0);

        Iterator        i       = virtuals.iterator();
        while(i.hasNext()) {
            MMObjectNode node    = (MMObjectNode) i.next();
            Integer number = new Integer(node.getNumber());

            if (!virtualNumbers.contains(number)) {
                virtualNumbers.add(number);

                // check if this node is already in cache
                if(nodeCache.containsKey(number)) {
                    result.add(nodeCache.get(number));
                    // else seek it with a search on builder in db
                } else {
                    numbersSize +=  ("," + number).length();
                    step.addNode(number.intValue());
                }

                if(numbersSize > MAX_QUERY_SIZE) {
                    result.addAll(getRawNodes(query));
                    query = new NodeSearchQuery(this);
                    step  = (BasicStep) query.getSteps().get(0);
                    numbersSize  = 0;
                }
            }
        }

        // now that we have a comma seperated string of numbers, we can
        // the search with a where-clause containing this list
            if(numbersSize > 0) {
                result.addAll(getRawNodes(query));
            } // else everything from cache

        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage() + Logging.stackTrace(sqe));
        }
        // check that we didnt loose any nodes
        if(virtualNumbers.size() != result.size()) {
            log.error("We lost a few nodes during conversion from virtualsnodes("+virtualNumbers.size()+") to realnodes("+result.size()+")");
            StringBuffer vNumbers = new StringBuffer();
            for (int j = 0; j < virtualNumbers.size(); j++) {
                vNumbers.append(virtualNumbers.get(j)).append(" ");
            }
            log.error("Virtual node numbers: " + vNumbers.toString());
            StringBuffer rNumbers = new StringBuffer();
            for (int j = 0; j < result.size(); j++) {
                int resultNumber = ((MMObjectNode) result.get(j)).getIntValue("number");
                rNumbers.append(resultNumber).append(" ");
            }
            log.error("Real node numbers: " + rNumbers.toString());
        }
        return result;
    }


    /**
     * Retrieves a node based on a unique key. The key is either an entry from the OAlias table
     * or the string-form of an integer value (the number field of an object node).
     * Note that the OAlias builder needs to be active for the alias to be used
     * (otherwise using an alias is concidered invalid).
     * @param key The value to search for
     * @param usecache If true, the node is retrieved from the node cache if possible.
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     */
    public MMObjectNode getNode(String key, boolean usecache) {
        if( key == null ) {
            log.error("getNode(null) for builder '" + tableName + "': key is null!");
            // who is doing that?
            log.info(Logging.stackTrace(6));
            return null;
        }
        int nr =-1;
        // first look if we have a number...
        try {
            nr = Integer.parseInt(key);
        } catch (Exception e) {}
        if (nr!=-1) {
            // key passed was a number.
            // return node with this number
            return getNode(nr, usecache);
        } else {
            // key passed was an alias
            // return node with this alias
            log.debug("Getting node by alias");
            if (mmb.getOAlias() != null) {
                return mmb.getOAlias().getAliasedNode(key);
            } else {
                return null;
            }
        }
    }

    /**
     * Retrieves a node based on a unique key. The key is either an entry from the OAlias table
     * or the string-form of an integer value (the number field of an object node).
     * Retrieves a node from the node cache if possible.
     * @param key The value to search for
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     */
    public MMObjectNode getNode(String key) {
        return getNode(key, true);
    }

    /**
     * Retrieves a node based on a unique key. The key is either an entry from the OAlias table
     * or the string-form of an integer value (the number field of an object node).
     * Retrieves the node from directly the database, not using the node cache.
     * @param key The value to search for
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     */
    public MMObjectNode getHardNode(String key) {
        return getNode(key,false);
    }

    /**
     * Retrieves a node based on it's number (a unique key).
     * @todo when something goes wrong, the method currently catches the exception and returns null.
     *       It should actually throw a NotFoundException instead.
     * @sql uses sql statements. will be removed once the new storage layer is in use
     * @param number The number of the node to search for
     * @param useCache If true, the node is retrieved from the node cache if possible.
     * @return <code>null</code> if the node does not exist, the key is invalid,or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     * @throws RuntimeException If the node does not exist (not always true!)
     */
    public synchronized MMObjectNode getNode(int number, boolean useCache) {
        if (number ==- 1) {
            log.warn(" (" + tableName + ") nodenumber == -1");
            return null;
        }
        MMObjectNode node = null;
        Integer numberValue = new Integer(number);
        // try cache if indicated to do so
        if (useCache) {
            node = (MMObjectNode)nodeCache.get(numberValue);
            if (node != null) {
                return node;
            }
        }
        // retrieve node's objecttype
        MMObjectBuilder builder = this;
        int nodeType = getNodeType(number);
        // test otype.
        // XXX todo: getNodeType() should throw a NotFound exception.
        if (nodeType < 0) {
            String msg = "The nodetype of node #" + number + " could not be found (nodetype # " + nodeType + ")";
            throw new RuntimeException(msg);
        }
        // if the type is not for the current buidler, determine the real builder
        if (nodeType != oType) {
            String builderName = mmb.getTypeDef().getValue(nodeType);
            if (builderName == null) {
                log.error("The nodetype name of node #" + number + " could not be found (nodetype # " + nodeType + "), taking 'object'");
                builderName = "object";
                
                //return null; Used to return null in MMBase < 1.7.0, but that gives troubles, e.g. that the result not gets cached.
            }
            builder = mmb.getBuilder(builderName);
            if (builder == null) {
                log.warn("Node #" + number + "'s builder " + builderName + "(" + nodeType + ") is not loaded, taking 'object'.");
                builder = mmb.getBuilder("object");                
                //return null; Used to return null in MMBase < 1.7.0, but that gives troubles, e.g. that the result not gets cached.
            }
        }
        // use storage factory if present
        if (mmb.getStorageManagerFactory() != null) {
            try {
                node = mmb.getStorageManager().getNode(builder, number);
                // store in cache if indicated to do so
                if (useCache) {
                    safeCache(numberValue, node);
                }
                return node;
            } catch(StorageException se) {
                // throw new NotFoundException(se);
                log.error(Logging.stackTrace(se));
                return null;
            }
        } else {


            MultiConnection con = null;
            Statement stmt = null;

            try {

                //NodeSearchQuery query = new NodeSearchQuery(this);
                //BasicFieldValueConstraint constraint = new BasiceFieldValueConstraint(
                //List = mmb.getDatabase().getNodes(query, this);
                // do the query on the database
                con = mmb.getConnection();
                stmt = con.createStatement();
                String query = "SELECT " + builder.getNonByteArrayFields() +" FROM " + builder.getFullTableName() + " WHERE "+mmb.getDatabase().getNumberString()+"="+number;

                ResultSet rs = stmt.executeQuery(query);
                try {
                    if (rs.next()) {
                        node = new MMObjectNode(builder);
                        ResultSetMetaData rd = rs.getMetaData();
                        String fieldname;
                        for (int i = 1; i<= rd.getColumnCount(); i++) {
                            fieldname = mmb.getDatabase().getDisallowedField(rd.getColumnName(i));
                            node = mmb.getDatabase().decodeDBnodeField(node, fieldname, rs, i);
                        }
                        // store in cache if indicated to do so
                        if (useCache) {
                            safeCache(numberValue, node);
                        }
                        // clear the changed signal
                        node.clearChanged();
                    } else {
                        // throw new NotFoundException(msg);
                        log.warn("Node #" + number + " could not be found(nodetype: " + builder.getTableName() + "(" + nodeType + "))");
                        return null; // not found
                    }
                } finally {
                    rs.close();
                }
                // return the results
                return node;
            } catch (SQLException e) {
                // something went wrong print it to the logs
                String msg = "The node #" + number + " could not be retrieved : " + e + "\n" + Logging.stackTrace(e);
                log.error(msg);
                // do we need to throw an exception in this situation, of continue running?
                // throw new NotFoundException(e);
                return null;
            } finally {
                mmb.closeConnection(con,stmt);
            }

        }
    }

    /**
     * Retrieves a node based on it's number (a unique key), retrieving the node
     * from the node cache if possible.
     * @param number The number of the node to search for
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *       <code>MMObjectNode</code> containign the contents of the requested node.
     */
    public MMObjectNode getNode(int number) {
        return getNode(number, true);
    }

    /**
     * Retrieves a node based on it's number (a unique key), directly from
     * the database, not using the node cache.
     * @param number The number of the node to search for
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *  <code>MMObjectNode</code> containign the contents of the requested node.
     */
    public MMObjectNode getHardNode(int number) {
        return getNode(number,false);
    }

    /**
     * Create a new temporary node and put it in the temporary _exist
     * node space
     */
    public MMObjectNode getNewTmpNode(String owner,String key) {
        MMObjectNode node=null;
        node=getNewNode(owner);
        node.setValue("_number",key);
        TemporaryNodes.put(key,node);
        return node;
    }

    /**
     * Put a Node in the temporary node list
     * @param key  The (temporary) key under which to store the node
     * @param node The node to store
     */
    public void putTmpNode(String key, MMObjectNode node) {
        node.setValue("_number",key);
        TemporaryNodes.put(key,node);
    }

    /**
     * Defines a virtual field to use for temporary nodes. If the given field-name does not start
     * with underscore ('_'), wich it usually does, then the field does also get a 'dbpos' (1000) as if it
     * was actually present in the builder's XML as a virtual field (this is accompanied with a log
     * message).
     *
     * Normally this is used to add 'tmp' fields like _number, _exists and _snumber which are system
     * fields which are normally invisible.
     *
     * @param field the name of the temporary field
     * @return true if the field was added, false if it already existed.
     */
    public boolean checkAddTmpField(String field) {
        if (getDBState(field) == FieldDefs.DBSTATE_UNKNOWN) { // means that field is not yet defined.
            FieldDefs fd = new FieldDefs(field, "string", -1, -1, field,FieldDefs.TYPE_STRING, -1, FieldDefs.DBSTATE_VIRTUAL);
            if (! field.startsWith("_")) {
                fd.setDBPos(1000);
                log.service("Added a virtual field '" + field + "' to builder '" + getTableName() + "' because it was not defined in the builder's XML, but the implementation requires it to exist.");
            } else {
                log.debug("Adding tmp (virtual) field '" + field + "' to builder '" + getTableName() + "'");
            }

            fd.setParent(this);

            addField(fd);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get nodes from the temporary node space
     * @param key  The (temporary) key to use under which the node is stored
     */
    public MMObjectNode getTmpNode(String key) {
        MMObjectNode node=null;
        node=(MMObjectNode)TemporaryNodes.get(key);
        if (node==null) {
            log.debug("getTmpNode(): node not found "+key);
        }
        return node;
    }

    /**
     * Remove a node from the temporary node space
     * @param key  The (temporary) key under which the node is stored
     */
    public void removeTmpNode(String key) {
        MMObjectNode node;
        node=(MMObjectNode)TemporaryNodes.remove(key);
        if (node==null) log.warn("removeTmpNode): node with "+key+" didn't exists");
    }

    /**
     * Counts number of nodes matching a specified constraint.
     *
     * @param where The constraint, can be a SQL where-clause, a MMNODE
     *        expression or an altavista-formatted expression.
     * @return The number of nodes, or -1 when failing to retrieve the data.
     * @deprecated Use {@link #count(NodeSearchQuery) count(NodeSearchQuery)}
     *             instead.
     */
    public int count(String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        int result = -1;
        NodeSearchQuery query = getSearchQuery(where);
        try {
            result = count(query);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Counts number of nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * @param query The query.
     * @return The number of nodes.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws SearchQueryException when failing to retrieve the data.
     * @since MMBase-1.7
     */
    public int count(NodeSearchQuery query) throws SearchQueryException {
        // Test if nodetype corresponds to builder.
        if (query.getBuilder() != this) {
            throw new IllegalArgumentException(
            "Wrong builder for query on '" + query.getBuilder().getTableName()
            + "'-table: " + this.getTableName());
        }

        // Wrap in modifiable query, replace fields by one count field.
        ModifiableQuery modifiedQuery = new ModifiableQuery(query);
        Step step = (Step) query.getSteps().get(0);
        FieldDefs numberFieldDefs = getField("number");
        BasicAggregatedField field = new BasicAggregatedField(step, numberFieldDefs, AggregatedField.AGGREGATION_TYPE_COUNT);
        List newFields = new ArrayList(1);
        newFields.add(field);
        modifiedQuery.setFields(newFields);


        AggregatedResultCache cache = AggregatedResultCache.getCache();

        List results = (List) cache.get(modifiedQuery);
        if (results == null) {
            // Execute query, return result.
            results = mmb.getDatabase().getNodes(modifiedQuery, new ResultBuilder(mmb, modifiedQuery));
            cache.put(modifiedQuery, results);
        }
        ResultNode result = (ResultNode) results.get(0);
        return result.getIntValue("number");
    }


    /**
     * Enumerate all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration search(String where) {
        return searchVector(where).elements();
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration search(String where,String sort) {
        return searchVector(where, sort).elements();
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *        Only applies if a sorted order is given.
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration search(String where,String sort,boolean direction) {
        return searchVector(where,sort,direction).elements();
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVector(String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        Vector result = new Vector();
        NodeSearchQuery query = getSearchQuery(where);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where       where clause that the objects need to fulfill
     * @param sorted      a comma separated list of field names on wich the
     *                    returned list should be sorted
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVector(String where,String sorted) {
        return searchVector(where, sorted, true);
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *        Only applies if a sorted order is given.
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVector(String where,String sorted,boolean direction) {
        String directions = (direction? "UP": "DOWN");
        return searchVector(where, sorted, directions);
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys in
     * a given order.
     *
     * @param where       where clause that the objects need to fulfill
     * @param sorted      a comma separated list of field names on wich the
     *                    returned list should be sorted
     * @param directions  A comma separated list of the values indicating wether
     *                    to sort up (ascending) or down (descending) on the
     *                    corresponding field in the <code>sorted</code>
     *                    parameter or <code>null</code> if sorting on all
     *                    fields should be up.
     *                    The value DOWN (case insensitive) indicates
     *                    that sorting on the corresponding field should be
     *                    down, all other values (including the
     *                    empty value) indicate that sorting on the
     *                    corresponding field should be up.
     *                    If the number of values found in this parameter are
     *                    less than the number of fields in the
     *                    <code>sorted</code> parameter, all fields that
     *                    don't have a corresponding direction value are
     *                    sorted according to the last specified direction
     *                    value.
     * @return            a vector containing all the objects that apply in the
     *                    requested order
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVector(String where, String sorted, String directions) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        Vector result = new Vector();
        NodeSearchQuery query = getSearchQuery(where, sorted, directions);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * As searchVector. Differences are:
     * - Throws exception on SQL errors
     * - returns List rather then Vector.
     * @since MMBase-1.6
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */

    public List searchList(String where) throws SQLException {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are wrapped
        //   inside an SQLException.
        NodeSearchQuery query = getSearchQuery(where);
        try {
            return getNodes(query);
        } catch (SearchQueryException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * As searchVector
     * But
     * - throws Exception on error
     * - returns List
     *
     * @param where Constraint, represented by scan MMNODE expression,
     *        AltaVista format or SQL "where"-clause.
     * @param sorted Comma-separated list of names of fields to sort on.
     * @param directions Comma-separated list of sorting directions ("UP"
     *        or "DOWN") of the fields to sort on.
     * @since MMBase-1.6
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */

    public List searchList(String where, String sorted, String directions)
    throws SQLException {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are wrapped
        //   inside an SQLException.
        NodeSearchQuery query = getSearchQuery(where, sorted, directions);
        try {
            return getNodes(query);
        } catch (SearchQueryException e) {
            if (log.isDebugEnabled()) {
                log.debug(e + Logging.stackTrace(e));
            }
            throw new SQLException(e.toString());
        }
    }

    /**
     * Parses arguments of searchVector and searchList
     * @since MMBase-1.6
     * @sql
     * @deprecated Use <code>getSearchQuery(String)</code> instead.
     * @deprecated-now This method no longer serves a purpose and is called
     *                  from nowhere.
     */
    protected String getQuery(String where) {
        if (where == null) where="";
        if (where.indexOf("MMNODE") != -1) {
            where=convertMMNode2SQL(where);
        } else {
            //where=QueryConvertor.altaVista2SQL(where);
            where = QueryConvertor.altaVista2SQL(where, mmb.getDatabase());
        }
        return "SELECT * FROM " + getFullTableName() + " " + where;
    }

    /**
     * Creates search query that retrieves nodes matching a specified
     * constraint.
     *
     * @param where The constraint, can be a SQL where-clause, a MMNODE
     *        expression, an altavista-formatted expression, empty or
     *        <code>null</code>.
     * @return The query.
     * @since MMBase-1.7
     */
    // package visibility!
    NodeSearchQuery getSearchQuery(String where) {
        NodeSearchQuery query;

        if (where != null && where.startsWith("MMNODE ")) {
            // MMNODE expression.
            query = convertMMNodeSearch2Query(where);
        } else {
            query = new NodeSearchQuery(this);
            QueryConvertor.setConstraint(query, where);
        }

        return query;
    }

    /**
     * Creates search query that retrieves a sorted list of nodes,
     * matching a specified constraint.
     *
     * @param where The constraint, can be a SQL where-clause, a MMNODE
     *        expression or an altavista-formatted expression.
     * @param sorted Comma-separated list of names of fields to sort on.
     * @param directions Comma-separated list of sorting directions ("UP"
     *        or "DOWN") of the fields to sort on.
     *        If the number of sorting directions is less than the number of
     *        fields to sort on, the last specified direction is applied to
     *        the remaining fields.
     * @since MMBase-1.7
     */
    // package visibility!
    NodeSearchQuery getSearchQuery(String where, String sorted, String directions) {
        NodeSearchQuery query = getSearchQuery(where);
        if (directions == null) {
            directions = "";
        }
        StringTokenizer sortedTokenizer = new StringTokenizer(sorted, ",");
        StringTokenizer directionsTokenizer
        = new StringTokenizer(directions, ",");

        String direction = "UP";
        while (sortedTokenizer.hasMoreElements()) {
            String fieldName = sortedTokenizer.nextToken().trim();
            FieldDefs fieldDefs = getField(fieldName);
            if (fieldDefs == null) {
                throw new IllegalArgumentException(
                "Not a known field of builder " + getTableName()
                + ": '" + fieldName + "'");
            }
            StepField field = query.getField(fieldDefs);
            BasicSortOrder sortOrder = query.addSortOrder(field);
            if (directionsTokenizer.hasMoreElements()) {
                direction = directionsTokenizer.nextToken().trim();
            }
            if (direction.equalsIgnoreCase("DOWN")) {
                sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
            } else {
                sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
            }
        }
        return query;
    }

    /**
     * Adds nodenumbers to be included to query retrieving nodes.
     *
     * @param query The query.
     * @param nodeNumbers Comma-separated list of nodenumbers.
     * @since MMBase-1.7
     */
    // package access!
    void addNodesToQuery(NodeSearchQuery query, String nodeNumbers) {
        BasicStep step = (BasicStep) query.getSteps().get(0);
        StringTokenizer st = new StringTokenizer(nodeNumbers, ",");
        while (st.hasMoreTokens()) {
            String str = st.nextToken().trim();
            int nodeNumber = Integer.parseInt(str);
            step.addNode(nodeNumber);
        }
    }

    /**
     * Executes a search (sql query) on the current database
     * and returns the nodes that result from the search as a Vector.
     * If the query is null, gives no results, or results in an error, an empty enumeration is returned.
     * @param query The SQL query
     * @return A Vector which contains all nodes that were found
     * @deprecated Use {@link getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    private Vector basicSearch(String query) {
        // In order to support this method:
        // - The result is converted to a vector.
        Vector result = new Vector();
        try {
            List nodes = getList(query);
            result.addAll(nodes);
        } catch (Exception e) {
            // something went wrong print it to the logs
            log.error("basicSearch(): ERROR in search " + query + " : " + Logging.stackTrace(e));
        }
        return result;
    }

    /**
     * As basicSearch
     * But:
     * - Throws exception on error
     * - Returns List
     * @since MMBase-1.6
     * @deprecated Use {@link getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */

    private List getList(String query) {
        MultiConnection con=null;
        Statement stmt=null;
        Vector results = new Vector();
        if (log.isDebugEnabled()) {
            log.debug("query: " + query);
        }
        try {
            con = mmb.getConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            try {
                for (int counter = 0; rs.next(); counter++) {
                    // check if we are allowed to do this iteration...
                    if(maxNodesFromQuery != -1 && counter >= maxNodesFromQuery) {
                        // to much nodes found...
                        String msg = "Maximum number of nodes protection, the query generated to much nodes, please define a query that is more specific(maximum:"+maxNodesFromQuery+" on builder:"+getTableName()+")";
                        log.warn(msg);
                        break;
                    }

                    // create the node from the record-set
                    MMObjectNode node = new MMObjectNode(this);
                    ResultSetMetaData rd = rs.getMetaData();
                    for (int i=1; i<=rd.getColumnCount(); i++) {
                        String fieldname = rd.getColumnName(i);
                        // node = mmb.getDatabase().decodeDBnodeField(node, fieldname, rs, i);
                        mmb.getDatabase().decodeDBnodeField(node, fieldname, rs, i);
                    }
                    results.add(node);
                }
            } finally {
                rs.close();
            }
        } catch(java.sql.SQLException e) {
            log.error(Logging.stackTrace(e));
        } finally {
            mmb.closeConnection(con,stmt);
        }


        processSearchResults(results);
        return results;
    }

    /**
     * Returns nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * Cache is used, but not filled (because this function is used to calculate subresults)
     *
     * @param query The query.
     * @return The nodes.
     * @throws IllegalArgumentException When the nodetype specified
     *         by the query is not the nodetype corresponding to this builder.
     * @since MMBase-1.7
     */

    protected List getRawNodes(NodeSearchQuery query)  throws SearchQueryException {
        // Test if nodetype corresponds to builder.
        if (query.getBuilder() != this) {
            throw new IllegalArgumentException("Wrong builder for query on '" + query.getBuilder().getTableName() + "'-table: " + this.getTableName());
        }

        List results = (List) listCache.get(query);

        // if unavailable, obtain from database
        if (results == null) {
            log.debug("result list is null, getting from database");
            results = mmb.getDatabase().getNodes(query, this);

        }
        return results;

    }

    /**
     * Returns nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * @param query The query.
     * @return The nodes.
     * @throws IllegalArgumentException When the nodetype specified
     *         by the query is not the nodetype corresponding to this builder.
     * @since MMBase-1.7
     */
    public List getNodes(NodeSearchQuery query) throws SearchQueryException {

        List results = (List) listCache.get(query);
        if (results == null) {
            results = getRawNodes(query);
            // TODO (later): implement maximum set by maxNodesFromQuery?
            // Perform necessary postprocessing.
            processSearchResults(results);
            listCache.put(query, results);
        }

        return results;
    }

     /**
     * Returns a Vector containing all the objects that match the searchkeys. Only returns the object numbers.
     * @param where scan expression that the objects need to fulfill
     * @return a <code>Vector</code> containing all the object numbers that apply, <code>null</code> if en error occurred.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchNumbers(String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        Vector results = new Vector();
        NodeSearchQuery query = getSearchQuery(where);

        // Wrap in modifiable query, replace fields by just the "number"-field.
        ModifiableQuery modifiedQuery = new ModifiableQuery(query);
        Step step = (Step) query.getSteps().get(0);
        FieldDefs numberFieldDefs = getField("number");
        StepField field = query.getField(numberFieldDefs);
        List newFields = new ArrayList(1);
        newFields.add(field);
        modifiedQuery.setFields(newFields);

        try {
            List resultNodes = mmb.getDatabase().getNodes(modifiedQuery,
                new ResultBuilder(mmb, modifiedQuery));

            // Extract the numbers from the result.
            Iterator iResultNodes = resultNodes.iterator();
            while (iResultNodes.hasNext()) {
                ResultNode resultNode = (ResultNode) iResultNodes.next();
                results.add(resultNode.getIntegerValue("number"));
            }
        } catch (SearchQueryException e) {
            log.error(e);
            results = null;
        }
        return results;
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in lost of node numbers to filter on
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration searchIn(String where,String sort,String in) {
        return searchVectorIn(where,sort,in).elements();
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param in lost of node numbers to filter on
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration searchIn(String where,String in) {
        return searchVectorIn(where,in).elements();
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in lost of node numbers to filter on
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *        Only applies if a sorted order is given.
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration searchIn(String where,String sort,boolean direction,String in) {
        return searchVectorIn(where,sort,direction,in).elements();
    }


    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *        returning a set of object numbers.
     * @return a vector containing all the objects that apply.
     * @sql
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVectorIn(String in) {
        if (in != null && in.length() > 5 && in.substring(0, 5).equalsIgnoreCase("SELECT")) {
            // Nodenumbers specified as query:
            // do the query on the database
            // TODO RvM: phase this out, subquery should not be supported.
            String query = "SELECT * FROM "+getFullTableName()+" where "+mmb.getDatabase().getNumberString()+" in ("+in+")";
            return basicSearch(query);
        }

        // In order to support this method:
        // - The result is converted to a Vector.
        // - Exceptions of type SearchQueryException are caught.
        Vector result = new Vector();
        NodeSearchQuery query = new NodeSearchQuery(this);
        addNodesToQuery(query, in);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *        returning a set of object numbers.
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     * @sql
     */
    public Vector searchVectorIn(String where, String in) {
        if (in != null && in.length() > 5 && in.substring(0, 5).equalsIgnoreCase("SELECT")) {
            // Nodenumbers specified as query:
            // do the query on the database
            // TODO RvM: phase this out, subquery should not be supported.
            // do the query on the database
            String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,mmb.getDatabase())+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+")";
            return basicSearch(query);
        }

        // In order to support this method:
        // - The result is converted to a Vector.
        // - Exceptions of type SearchQueryException are caught.
        Vector result = new Vector();
        NodeSearchQuery query = getSearchQuery(where);
        addNodesToQuery(query, in);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *        returning a set of object numbers.
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVectorIn(String where,String sorted,String in) {
        return searchVectorIn(where, sorted, true, in);
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *        returning a set of object numbers.
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *        Only applies if a sorted order is given.
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     * @sql
     */
    public Vector searchVectorIn(String where,String sorted,boolean direction,String in) {

        if (in != null && in.length() > 5 && in.substring(0, 5).equalsIgnoreCase("SELECT")) {
            // Nodenumbers specified as query:
            // do the query on the database
            // TODO RvM: phase this out, subquery should not be supported.
            // temp mapper hack only works in single order fields
            sorted=mmb.getDatabase().getAllowedField(sorted);
            // do the query on the database
            if (direction) {
                String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,mmb.getDatabase())+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted+" ASC";
                return basicSearch(query);
            } else {
                String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,mmb.getDatabase())+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted+" DESC";
                return basicSearch(query);
            }
        }

        // In order to support this method:
        // - The result is converted to a Vector.
        // - Exceptions of type SearchQueryException are caught.
        Vector result = new Vector();
        String directions = (direction? "UP": "DOWN");
        NodeSearchQuery query = getSearchQuery(where, sorted, directions);
        addNodesToQuery(query, in);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Parses arguments of searchVector and searchList
     *
     * @since MMBase-1.6
     * @sql
     * @deprecated Use <code>getSearchQuery(String,String,String)</code>
     *             instead - specifying direction "UP" or
     *             "DOWN" as appropriate.
     * @deprecated-now This method no longer serves a purpose and is called
     *                 from nowhere.
     */

    protected String getQuery(String where, String sorted, boolean direction) {
        if (where==null) {
            where="";
        } else if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            where=QueryConvertor.altaVista2SQL(where,mmb.getDatabase());
        }
        // temp mapper hack only works in single order fields
        sorted=mmb.getDatabase().getAllowedField(sorted);
        String query;
        if (direction) {
            query="SELECT * FROM "+getFullTableName()+" "+where+" ORDER BY "+sorted+" ASC";

        } else {
            query="SELECT * FROM "+getFullTableName()+" "+where+" ORDER BY "+sorted+" DESC";
        }
        return query;
    }

    /**
     * Parses arguments of searchVector and searchList
     *
     * @since MMBase-1.6
     * @sql
     * @deprecated Use <code>getSearchQuery(String,String,String)</code>
     *             instead.
     * @deprecated-now This method no longer serves a purpose and is called
     *                 from nowhere.
     */
    protected String getQuery(String where, String sorted, String directions) {
        if (where==null) {
            where="";
        } else if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            where=QueryConvertor.altaVista2SQL(where,mmb.getDatabase());
        }
        if (directions == null) {
            directions = "";
        }
        StringTokenizer sortedTokenizer;
        StringTokenizer directionsTokenizer;
        sortedTokenizer = new StringTokenizer(sorted, ",");
        directionsTokenizer = new StringTokenizer(directions, ",");
        String orderBy = "";
        String lastDirection = " ASC";
        while (sortedTokenizer.hasMoreElements()) {
            String field = sortedTokenizer.nextToken();
            orderBy += mmb.getDatabase().getAllowedField(field);
            if (directionsTokenizer.hasMoreElements()) {
                String direction = directionsTokenizer.nextToken();
                if ("DOWN".equalsIgnoreCase(direction)) {
                    lastDirection = " DESC";
                } else {
                    lastDirection = " ASC";
                }
            }
            orderBy += lastDirection;
            if (sortedTokenizer.hasMoreElements()) {
                orderBy += ", ";
            }
        }
        return "SELECT * FROM " + getFullTableName() + " " + where + " ORDER BY " + orderBy;
    }

    /**
     * Enumerate all the objects that match the where clause
     * This method is slightly faster than search(), since it does not try to 'parse'
     * the where clause.
     * @param where SQL WHERE-clause without the leading "WHERE ".
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     *             The performance gain is negligible and does not justify
     *             another method.
     */
    public Enumeration searchWithWhere(String where) {
        return search("WHERE " + where);
    }

    /**
     * Performs some necessary postprocessing on nodes retrieved from a
     * search query.
     * This consists of the following actions:
     * <ul>
     * <li>Stores retrieved nodes in the {@link #nodeCache nodecache}, or
     * <li>Replaces nodes by cached nodes (if cached node available, and
     *     {@link #REPLACE_CACHE REPACE_CACHE} is set to false).
     * <li>Replace partially retrieved nodes in the result by complete nodes.
     *     Nodes are partially retrieved when their type is a inheriting type
     *     of this builder's type, having additional fields. For these nodes
     *     additional queries are performed to retrieve the complete nodes.
     * <li>Removes nodes with invalid node number from the result.
     * </ul>
     *
     * @param results The nodes. After returning, partially retrieved nodes
     *        in the result are replaced <em>in place</em> by complete nodes.
     */
    public void processSearchResults(List results) {
        Map convert = new HashMap();
        int convertCount = 0;
        int convertedCount = 0;
        int cacheGetCount = 0;
        int cachePutCount = 0;

        ListIterator iResults = results.listIterator();
        while (iResults.hasNext()) {
            MMObjectNode node = (MMObjectNode) iResults.next();

            Integer number = new Integer(node.getNumber());
            if(number.intValue() < 0) {
                // never happend to me, and never should!
                log.error("invalid node found, node number was invalid:" + node.getNumber()+", database invalid?");
                // dont know what to do with this node,...
                // remove it from the results, continue to the next one!
                iResults.remove();
                continue;
            }

            boolean fromCache = false;
            // only active when builder loaded (oType != -1)
            // maybe we got the wrong node typeback, if so
            // try to retrieve the correct node from the cache first
            if(oType != -1 && oType != node.getOType()){
                // try to retrieve the correct node from the
                // nodecache
                MMObjectNode cachedNode = (MMObjectNode) nodeCache.get(number);
                if(cachedNode != null) {
                    node = cachedNode;
                    iResults.set(node);
                    fromCache = true;
                    cacheGetCount ++;
                } else {
                    // add this node to the list of nodes that still need to
                    // be converted..
                    // we dont request the builder here, for this we need the
                    // typedef table, which could generate an additional query..
                    Integer otype = new Integer(node.getOType());
                    Set nodes = (Set) convert.get(otype);
                    // create an new entry for the type, if not yet there...
                    if (nodes == null) {
                        nodes = new HashSet();
                        convert.put(otype, nodes);
                    }
                    nodes.add(node);
                    convertCount ++;
                }
            } else if (oType == node.getOType()) {
                MMObjectNode oldNode = (MMObjectNode)nodeCache.get(number);
                // when we want to use cache also for new found nodes
                // and cache may not be replaced, use the one from the
                // cache..
                if(!REPLACE_CACHE && oldNode != null) {
                    node = oldNode;
                    iResults.set(node);
                    fromCache = true;
                    cacheGetCount++;
                }
            } else {
                // skipping everything, our builder hasnt been started yet...
            }

            // we can add the node to the cache _if_
            // it was not from cache already, and it
            // is of the correct type..
            if(!fromCache && oType == node.getOType()) {
                // can someone tell me what this has to do?
                // clear the changed signal
                node.clearChanged(); // huh?
                safeCache(number, node);
                cachePutCount++;
            }
        }

        if(CORRECT_NODE_TYPES && convert.size() > 0){
            // retieve the nodes from the builders....
            // and put them into one big hashmap (integer/node)
            // after that replace all the nodes in result, that
            // were invalid.
            Map convertedNodes = new HashMap();

            // process all the different types (builders)
            Iterator types = convert.entrySet().iterator();
            while(types.hasNext()){
                Map.Entry typeEntry = (Map.Entry) types.next();
                int otype = ((Integer)typeEntry.getKey()).intValue();
                Set nodes = (Set) typeEntry.getValue();
                MMObjectNode typedefNode;
                try {
                    typedefNode = getNode(otype);
                } catch (Exception e) {
                    log.error("Exception during conversion of nodelist to right types.  Nodes (" + nodes + ") of current type " + otype + " will be skipped. Probably the database is inconsistent. Message: " + e.getMessage());
                    
                    continue;
                }
                if(typedefNode == null) {
                    // builder not known in typedef?
                    // skip this builder and process to next one..
                    // TODO: research: add incorrect node to node's cache?
                    log.error("Could not find typedef node #" + otype);
                    continue;
                }
                MMObjectBuilder builder = mmb.getBuilder(typedefNode.getStringValue("name"));
                if(builder == null) {
                    // could not find the builder that was in typedef..
                    // maybe it is not active?
                    // TODO: research: add incorrect node's to node cache?
                    log.error("Could not find builder with name:" + typedefNode.getStringValue("name") + " refered by node #" + typedefNode.getNumber()+", is it active?");
                    continue;
                }
                Iterator converted = builder.getNodes(nodes).iterator();
                
                while(converted.hasNext()) {
                    MMObjectNode current = (MMObjectNode) converted.next();
                    convertedNodes.put(new Integer(current.getNumber()), current);
                }
            }

            // insert all the corrected nodes that were found into the list..
            for(int i = 0; i < results.size(); i++) {
                MMObjectNode current = (MMObjectNode) results.get(i);
                Integer number = new Integer(current.getNumber());
                if(convertedNodes.containsKey(number)) {
                    // converting the node...
                    results.set(i,  convertedNodes.get(number));
                    convertedCount ++;
                }
                current = (MMObjectNode) results.get(i);
                if(current.getNumber() < 0) {
                    // never happened to me, and never should!
                    throw new RuntimeException("invalid node found, node number was invalid:" + current.getNumber());
                }
            }
        } else if(convert.size() != 0) {
            log.warn("we still need to convert " + convertCount + " of the " + results.size() + " nodes"
                     + "(number of different types:"+ convert.size()  +")");
        }
        if(log.isDebugEnabled()) {
            log.debug("retrieved " + results.size() +
                      " nodes, converted " + convertedCount +
                      " of the " + convertCount +
                      " invalid nodes(" + convert.size() +
                      " types, " + cacheGetCount +
                      " from cache, " + cachePutCount + " to cache)");
        }
    }

    /**
     * Store the nodes in the resultset, obtained from a builder, in a sorted vector.
     * (Called by nl.vpro.mmbase.module.search.TeaserSearcher.createShopResult ?)
     * The nodes retrieved are added to the cache.
     * @vpro replace with a way to sort nodes.
     * @param rs The resultset containing the nodes
     * @return The SortedVector which holds the data
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public SortedVector readSearchResults(ResultSet rs, SortedVector sv) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            MMObjectNode node;

            while(rs.next()) {
                node = new MMObjectNode(this);
                for (int index = 1; index <= numberOfColumns; index++) {
                    //String type=rsmd.getColumnTypeName(index);
                    String fieldname=rsmd.getColumnName(index);
                    node=mmb.getDatabase().decodeDBnodeField(node,fieldname,rs,index);
                }
                sv.addUniqueSorted(node);
            }

            return sv;
        } catch (SQLException e) {
            // something went wrong print it to the logs
            log.error(Logging.stackTrace(e));
        }
        return null;
    }


    /**
     * Build a set command string from a set nodes ( should be moved )
     * @param nodes Vector containg the nodes to put in the set
     * @param fieldName fieldname whsoe values should be put in the set
     * @return a comma-seperated list of values, as a <code>String</code>
     */
    public String buildSet(Vector nodes, String fieldName) {
        StringBuffer result = new StringBuffer("(");
        Enumeration enumeration = nodes.elements();
        MMObjectNode node;

        while (enumeration.hasMoreElements()) {
            node = (MMObjectNode)enumeration.nextElement();

            if(enumeration.hasMoreElements()) {
                result.append(node.getValue(fieldName)).append(", ");
            } else {
                result.append(node.getValue(fieldName));
            }

        }
        result.append(')');
        return result.toString();
    }

    /**
     * Return a copy of the list  of field definitions of this table.
     * @return A new  <code>Vector</code> with the tables fields (FieldDefs)
     */
    public Vector getFields() {
        return new Vector(fields.values());
    }


    /**
     * Return a list of field names of this table.
     * @return a new <code>Vector</code> with the tables field anmes (String)
     */
    public Vector getFieldNames() {
        return new Vector(fields.keySet());
    }

    /**
     * Return a field's definition
     * @param the requested field's name
     * @return a <code>FieldDefs</code> belonging with the indicated field
     */
    public FieldDefs getField(String fieldName) {
        FieldDefs fielddefs = (FieldDefs) fields.get(fieldName);
        return fielddefs;
    }

    /**
     * Clears all field list caches, and recalculates the database field list.
     */
    protected void updateFields() {
        sortedFieldLists = new HashMap();
        // note: sortedDBLayout is deprectated
        // sortedDBLayout=new Vector();
        setDBLayout_xml(fields);
        // log.service("currently fields: " + fields);
    }

    /**
     * Add a field to this builder.
     * This does not affect the builder config file, nor the table used.
     * @param def the field definiton to add
     */
    public void addField(FieldDefs def) {
        fields.put(def.getDBName(),def);
        updateFields();
    }


    /**
     * Remove a field from this builder.
     * This does not affect the builder config file, nor the table used.
     * @param fieldname the name of the field to remove
     */
    public void removeField(String fieldname) {
        FieldDefs def=getField(fieldname);
        int dbpos=def.getDBPos();
        fields.remove(fieldname);
        // move them all up one place
        for (Enumeration e=fields.elements();e.hasMoreElements();) {
            def=(FieldDefs)e.nextElement();
            int curpos=def.getDBPos();
            if (curpos>=dbpos) def.setDBPos(curpos-1);
        }
        updateFields();
    }


    /**
     * Return a field's database type. The returned value is one of the following values
     * declared in FieldDefs:
     * TYPE_STRING,
     * TYPE_INTEGER,
     * TYPE_BYTE,
     * TYPE_FLOAT,
     * TYPE_DOUBLE,
     * TYPE_LONG,
     * TYPE_NODE,
     * TYPE_UNKNOWN
     * @param the requested field's name
     * @return the field's type.
     */
    public int getDBType(String fieldName) {
        if (fields == null) {
            log.error("getDBType(): fielddefs are null on object : "+tableName);
            return FieldDefs.TYPE_UNKNOWN;
        }
        FieldDefs fieldDefs = getField(fieldName);
        if (fieldDefs == null) {
            //perhaps prefixed with own tableName[0-9]? (allowed since MMBase-1.7)
            int dot = fieldName.indexOf('.');
            if (dot > 0) {
                if (fieldName.startsWith(tableName)) {
                    if (tableName.length() <= dot  ||
                        Character.isDigit(fieldName.charAt(dot - 1))) {
                        fieldName = fieldName.substring(dot + 1);
                        fieldDefs = getField(fieldName);
                    }
                }
            }
        }

        if (fieldDefs == null) {

            // log warning, except for virtual builders
            if (!virtual) { // should getDBType not be overridden in Virtual Builder then?
                log.warn("getDBType(): Can't find fielddef on field '"+fieldName+"' of builder "+tableName);
                log.debug(Logging.stackTrace(new Throwable()));
            }
            return FieldDefs.TYPE_UNKNOWN;
        }
        return fieldDefs.getDBType();
    }

    /**
     * Return a field's database state. The returned value is one of the following values
     * declared in FieldDefs:
     * DBSTATE_VIRTUAL,
     * DBSTATE_PERSISTENT,
     * DBSTATE_SYSTEM,
     * DBSTATE_UNKNOWN
     * @param the requested field's name
     * @return the field's type.
     */
    public int getDBState(String fieldName) {
        if (fields == null) return FieldDefs.DBSTATE_UNKNOWN;
        FieldDefs field = getField(fieldName);
        if (field == null) return FieldDefs.DBSTATE_UNKNOWN;
        return field.getDBState();
    }

    /**
     * What should a GUI display for this node.
     * Default is the first non system field (first field after owner).
     * Override this to display your own choice (see Images.java).
     * @param node The node to display
     * @return the display of the node as a <code>String</code>
     */
    public String getGUIIndicator(MMObjectNode node) {
        // do the best we can because this method was not implemeted
        // we get the first field in the object and try to make it
        // to a string we can return
        List list = getFields(FieldDefs.ORDER_LIST);
        if (list.size() > 0) {
            String fname = ((FieldDefs) list.get(0)).getDBName();
            String str = node.getStringValue( fname );
            if (str.length() > 128) {
                return str.substring(0, 128) + "...";
            }
            return str;
        } else {
            return GUI_INDICATOR;
        }
    }


    /**
     * What should a GUI display for this node/field combo.
     * Default is null (indicating to display the field as is)
     * Override this to display your own choice.
     * @param node The node to display
     * @param field the name field of the field to display
     * @return the display of the node's field as a <code>String</code>, null if not specified
     */
    public String getGUIIndicator(String field, MMObjectNode node) {
        FieldDefs fieldDef = getField(field);
        if (fieldDef.getDBType() == FieldDefs.TYPE_NODE && ! field.equals("number")) {
            MMObjectNode otherNode = node.getNodeValue(field);
            if (otherNode == null || otherNode == MMObjectNode.VALUE_NULL) {
                return "NULL";
            } else {
                return otherNode.parent.getGUIIndicator(otherNode);
            }
        } else {
            return null;
        }
    }


    /**
     * The GUIIndicator can depend on the locale. Override this function
     * @since MMBase-1.6
     */
    protected String getLocaleGUIIndicator(Locale locale, String field, MMObjectNode node) {
        return getGUIIndicator(field, node);
    }


    /**
     * The GUIIndicator can depend on the locale. Override this function
     * @since MMBase-1.6
     */
    protected String getLocaleGUIIndicator(Locale locale, MMObjectNode node) {
        return getGUIIndicator(node);
    }


    /**
     * Gets the field definitions for the editor, sorted according
     * to the specified order, and excluding the fields that have
     * not been assigned a valid position (valid is >= 0).
     * This method makes an explicit sort (it does not use a cached list).
     *
     * @param sortorder One of the sortorders defined in
     *        {@link org.mmbase.module.corebuilders.FieldDefs FieldDefs}
     * @return The ordered list of field definitions.
     */
    public List getFields(int sortorder) {
        List orderedFields = (List)sortedFieldLists.get(new Integer(sortorder));
        if (orderedFields==null) {
            orderedFields = new Vector();
            for (Iterator i=fields.values().iterator(); i.hasNext();) {
                FieldDefs node=(FieldDefs)i.next();
                // include only fields which have been assigned a valid position
                if (((sortorder==FieldDefs.ORDER_CREATE) && (node.getDBPos()>-1)) ||
                    ((sortorder==FieldDefs.ORDER_EDIT) && (node.getGUIPos()>-1)) ||
                    ((sortorder==FieldDefs.ORDER_SEARCH) && (node.getGUISearch()>-1)) ||
                    ((sortorder==FieldDefs.ORDER_LIST) && (node.getGUIList()>-1))
                    ) orderedFields.add(node);
            }
            FieldDefs.sort(orderedFields,sortorder);
            sortedFieldLists.put(new Integer(sortorder),orderedFields);
        }
        return orderedFields;
    }

    /**
     * Get the field definitions for the editor, sorted according to it's GUISearch property (as set in the builder xml file).
     * Used for creating search-forms.
     * @deprecated use getFields() with sortorder ORDER_SEARCH
     * @return a vector with FieldDefs
     */
    public Vector getEditFields() {
        return (Vector)getFields(FieldDefs.ORDER_SEARCH);
    }

    /**
     * Get the field definitions for the editor, sorted accoring to it's GUIList property (as set in the builder xml file).
     * Used for creating list-forms (tables).
     * @deprecated use getFields() with sortorder ORDER_LIST
     * @return a vector with FieldDefs
     */
    public Vector getSortedListFields() {
        return (Vector)getFields(FieldDefs.ORDER_LIST);
    }

    /**
     * Get the field definitions for the editor, sorted according to it's GUIPos property (as set in the builder xml file).
     * Used for creating edit-forms.
     * @deprecated use getFields() with sortorder ORDER_EDIT
     * @return a vector with FieldDefs
     */
    public Vector getSortedFields() {
        return (Vector)getFields(FieldDefs.ORDER_EDIT);
    }

    /**
     * Returns the next field as defined by its sortorder, according to the specified order.
     */
    public FieldDefs getNextField(String currentfield, int sortorder) {
        FieldDefs cdef=getField(currentfield);
        List sortedFields=getFields(sortorder);
        int pos=sortedFields.indexOf(cdef);
        if (pos!=-1  && (pos+1)<sortedFields.size()) {
            return (FieldDefs)sortedFields.get(pos+1);
        }
        return null;
    }

    /**
     * Returns the next field as defined by its sortorder, according to it's GUIPos property (as set in the builder xml file).
     * Used for moving between fields in an edit-form.
     * @deprecated use getNextField() with sortorder ORDER_EDIT
     */
    public FieldDefs getNextField(String currentfield) {
        return getNextField(currentfield,FieldDefs.ORDER_EDIT);
    }

    /**
     * Provides additional functionality when obtaining field values.
     * This method is called whenever a Node of the builder's type fails at evaluating a getValue() request
     * (generally when a fieldname is supplied that doesn't exist).
     * It allows the system to add 'functions' to be included with a field name, such as 'html(body)' or 'time(lastmodified)'.
     * This method will parse the fieldname, determining functions and calling the {@link #executeFunction} method to handle it.
     * Functions in fieldnames can be given in the format 'functionname(fieldname)'. An old format allows 'functionname_fieldname' instead,
     * though this only applies to the text functions 'short', 'html', and 'wap'.
     * Functions can be nested, i.e. 'html(shorted(body))'.
     * Derived builders should override this method only if they want to provide virtual fieldnames. To provide addiitonal functions,
     * override {@link #executeFunction} instead.
     * @param node the node whos efields are queries
     * @param field the fieldname that is requested
     * @return the result of the 'function', or null if no valid functions could be determined.
     */
    public Object getValue(MMObjectNode node, String field) {
        Object rtn = getObjectValue(node, field);

        // Old code
        if (field.indexOf("short_")==0) {
            String val=node.getStringValue(field.substring(6));
            val=getShort(val,34);
            rtn=val;
        }  else if (field.indexOf("html_")==0) {
            String val=node.getStringValue(field.substring(5));
            val=getHTML(val);
            rtn=val;
        } else if (field.indexOf("wap_")==0) {
            String val=node.getStringValue(field.substring(4));
            val=getWAP(val);
            rtn=val;
        }
        // end old
        return rtn;
    }
    /**
     * Like getValue, but without the 'old' code (short_ html_ etc). This is for
     * protected use, when you are sure this is not used, and you can
     * avoid the overhead.
     *
     * @since MMBase-1.6
     * @see #getValue
     */

    protected Object getObjectValue(MMObjectNode node, String field) {
        Object rtn = null;
        int pos1 = field.indexOf('(');
        if (pos1 != -1) {
            int pos2 = field.lastIndexOf(')');
            if (pos2 != -1) {
                String name     = field.substring(pos1 + 1, pos2);
                String function = field.substring(0, pos1);
                if (log.isDebugEnabled()) {
                    log.debug("function = '" + function + "', fieldname = '" + name + "'");
                }
                List a = new ArrayList(); a.add(name);
                rtn = getFunctionValue(node, function, a);

            }
        }
        return rtn;
    }

    /**
     * Parses string containing function parameters.
     * The parameters must be separated by ',' or ';' and may be functions
     * themselves (i.e. a functionname, followed by a parameter list between
     * parenthesis).
     *
     * @param fields The string, containing function parameters.
     * @return List of function parameters (may be functions themselves).
     * @deprecated use executeFunction(node, function, list)
     */
    protected Vector getFunctionParameters(String fields) {
        int commapos=0;
        int nested  =0;
        Vector v= new Vector();
        int i;
        if (log.isDebugEnabled()) log.debug("Fields=" + fields);
        for(i = 0; i<fields.length(); i++) {
            if ((fields.charAt(i)==',') || (fields.charAt(i)==';')){
                if(nested==0) {
                    v.add(fields.substring(commapos,i).trim());
                    commapos=i+1;
                }
            }
            if (fields.charAt(i)=='(') {
                nested++;
            }
            if (fields.charAt(i)==')') {
                nested--;
            }
        }
        if (i>0) {
            v.add(fields.substring(commapos).trim());
        }
        return v;
    }

    /**
     * Executes a 'function' on a MMObjectNode. The function is
     * identified by a string, and its arguments are passed by a List.
     *
     * The function 'info' should exist, and this will return a Map
     * with descriptions of the possible functions.
     *
     * Override executeFunction in your extension if you want to add functions.
     *
     * @param node The node on which the function must be executed
     * @param function The string identifying the funcion
     * @param arguments The list with function argument or null (which means 'no arguments')
     *
     * @see #executeFunction
     * @since MMBase-1.6
     */
    // package because called from MMObjectNode
    final Object getFunctionValue(MMObjectNode node, String function, List arguments) {

        Object rtn = null;
        if (arguments == null) arguments = new ArrayList();
        // for backwards compatibility (calling with string function with more then one argument)
        if (arguments.size() == 1 && arguments.get(0) instanceof String) {
            String arg = (String) arguments.get(0);
            rtn =  executeFunction(node, function, arg);
            if (rtn != null) return rtn;
            arguments = getFunctionParameters(arg);
        }
        return executeFunction(node, function, arguments);

    }

    /**
     * perhaps we need something like this
     * @since MMBase-1.7
     */
    public Parameter[] getParameterDefinition(String function) {
    	//keesj: why not this.getClass()?
        return org.mmbase.util.functions.NodeFunction.getParametersByReflection(MMObjectBuilder.class, function);
    }



    /**
     * Executes a function on the field of a node, and returns the result.
     * This method is called by the builder's {@link #getValue} method.
     * Derived builders should override this method to provide additional functions.
    *
     * @since MMBase-1.6
     * @throws IllegalArgumentException if the argument List does not
     * fit the function
     * @see #executeFunction
     */

    protected Object executeFunction(MMObjectNode node, String function, List arguments) {
        if (log.isDebugEnabled()) {
            log.debug("Executing function " + function + " on node " + node.getNumber() + " with argument " + arguments);
        }

        if (function.equals("info")) {
            Map info = new HashMap();
            info.put("wrap", "(string, length) Wraps a string (for use in HTML)");
            info.put("gui",  "" + Arrays.asList(GUI_PARAMETERS) + "Returns a (XHTML) gui representation of the node (if field is '') or of a certain field. It can take into consideration a http session variable name with loging information and a language");
            // language is only implemented in TypeDef now, session in AbstractServletBuilder
            // if needed on more place, then it can be generalized to here.

            info.put("html",  "(field), XHTML escape the field");
            info.put("substring", "(string, length, fill)");
            info.put("date", "deprecated, use time-tag");
            info.put("time", "deprecated, use time-tag");
            info.put("timesec", "deprecated, use time-tag");
            info.put("longmonth", "deprecated, use time-tag");
            info.put("monthnumber", "deprecated, use time-tag");
            info.put("month", "deprecated, use time-tag");
            info.put("weekday", "deprecated, use time-tag");
            info.put("shortday", "deprecated, use time-tag");
            info.put("day", "deprecated, use time-tag");
            info.put("shortyear", "deprecated, use time-tag");
            info.put("year", "deprecated, use time-tag");
            info.put("thisdaycurtime", "deprecated, use time-tag");
            info.put("age", "Returns the age of this object in days");
            info.put("wap", "(string)");
            info.put("shorted", "(string) Truncated version of string");
            info.put("uppercase", "(string)");
            info.put("lowercase", "(string)");
            info.put("hostname", "");
            info.put("urlencode", "");
            info.put("wrap_<lengh>", "deprecated");
            info.put("currency_euro", "");
            info.put("info", "(functionname) Returns information about a certain 'function'. Or a map of all function if no arguments.");
            if (arguments == null || arguments.size() == 0) {
                return info;
            } else {
                return info.get(arguments.get(0));
            }
        } else if (function.equals("wrap")) {
            if (arguments.size() < 2) throw new IllegalArgumentException("wrap function needs 2 arguments (currenty:" + arguments.size() + " : "  + arguments + ")");
            try {
                String val  = node.getStringValue((String)arguments.get(0));
                int wrappos = Integer.parseInt((String)arguments.get(1));
                return wrap(val, wrappos);
            } catch(Exception e) {}
        } else if (function.equals("substring")) {
            if (arguments.size() < 2) throw new IllegalArgumentException("substring function needs 2 or 3 arguments (currenty:" + arguments.size() + " : "  + arguments + ")");
            try {
                String val = node.getStringValue((String)arguments.get(0));
                int len    = Integer.parseInt((String)arguments.get(1));
                if (arguments.size() > 2) {
                    String filler = (String)arguments.get(2);
                    return substring(val, len, filler);
                } else {
                    return substring(val, len, null);
                }
            } catch(Exception e) {
                log.debug(Logging.stackTrace(e));
                return e.toString();
            }
        } else if (function.equals("smartpath")) {
            try {
                String documentRoot = (String) arguments.get(0);
                String path         = (String) arguments.get(1);
                String version      = (String) arguments.get(2);
                if (version != null) {
                    if (version.equals("")) {
                        version = null;
                    }
                }
                return getSmartPath(documentRoot, path, "" + node.getNumber(), version);
            } catch(Exception e) {
                log.error("Evaluating smartpath for "+node.getNumber()+" went wrong " + e.toString());
            }
        } else if (function.equals("gui")) {
            if (log.isDebugEnabled()) log.debug("GUI of builder with " + arguments);
            if (arguments == null || arguments.size() == 0) {
                return getGUIIndicator(node);
            } else {
                String rtn;
                String field = (String) arguments.get(0);
                Locale locale = null;
                if (arguments.size() < 2) { // support for login info not needed
                    rtn = getGUIIndicator(field, node);
                } else {
                    String language = (String) arguments.get(1);
                    if (language == null) language = mmb.getLanguage();
                    locale = new Locale(language, "");
                    if (null == field || "".equals(field)) {
                        rtn = getLocaleGUIIndicator(locale, node);
                    } else {
                        rtn = getLocaleGUIIndicator(locale, field, node);
                    }
                }

                if (rtn == null) {
                    FieldDefs fdef = getField(field);
                    if (fdef != null && "eventtime".equals(fdef.getGUIType())) { // do something reasonable for this
                        if (locale == null) locale = new Locale(mmb.getLanguage(), "");
                        Date date = new Date(node.getLongValue(field) * 1000);
                        rtn = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, locale).format(date);
                        Calendar calendar = new GregorianCalendar(locale);
                        calendar.setTime(date);
                        if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
                            java.text.DateFormat df = new java.text.SimpleDateFormat(" G", locale);
                            rtn += df.format(date);
                        }
                    } else {
                        rtn = node.getStringValue(field);
                    }
                }
                return rtn;
            }
        }

        String field;
        if (arguments == null || arguments.size() == 0) {
            field = "";
        } else {
            field = (String) arguments.get(0);
        }

        // time functions
        if(function.equals("date")) {                    // date
            int v=node.getIntValue(field);
            return DateSupport.date2string(v);
        } else if (function.equals("time")) {            // time hh:mm
            int v=node.getIntValue(field);
            return DateSupport.getTime(v);
        } else if (function.equals("timesec")) {        // timesec hh:mm:ss
            int v=node.getIntValue(field);
            return DateSupport.getTimeSec(v);
        } else if (function.equals("longmonth")) {        // longmonth September
            int v=node.getIntValue(field);
            return DateStrings.longmonths[DateSupport.getMonthInt(v)];
        } else if (function.equals("monthnumber")) {
            int v=node.getIntValue(field);
            return ""+(DateSupport.getMonthInt(v)+1);
        } else if (function.equals("month")) {            // month Sep
            int v=node.getIntValue(field);
            return DateStrings.Dutch_months[DateSupport.getMonthInt(v)];
        } else if (function.equals("weekday")) {        // weekday Sunday
            int v=node.getIntValue(field);
            return DateStrings.Dutch_longdays[DateSupport.getWeekDayInt(v)];
        } else if (function.equals("shortday")) {        // shortday Sun
            int v=node.getIntValue(field);
            return DateStrings.Dutch_days[DateSupport.getWeekDayInt(v)];
        } else if (function.equals("day")) {            // day 4
            int v=node.getIntValue(field);
            return ""+DateSupport.getDayInt(v);
        } else if (function.equals("shortyear")) {            // year 01
            int v=node.getIntValue(field);
            return (DateSupport.getYear(v)).substring(2);
        } else if (function.equals("year")) {            // year 2001
            int v=node.getIntValue(field);
            return DateSupport.getYear(v);
        } else if (function.equals("thisdaycurtime")) {            //
            int curtime=node.getIntValue(field);
            // gives us the next full day based on time (00:00)
            int days=curtime/(3600*24);
            return ""+((days*(3600*24))-3600);
        } else if (function.equals("age")) {
            Integer val = new Integer(node.getAge());
            return val.toString();
        } else if (function.equals("wap")) {
            String val=node.getStringValue(field);
            return getWAP(val);
        } else if (function.equals("html")) {
            String val=node.getStringValue(field);
            return getHTML(val);
        } else if (function.equals("shorted")) {
            String val=node.getStringValue(field);
            return getShort(val,32);
        } else if (function.equals("uppercase")) {
            String val=node.getStringValue(field);
            return val.toUpperCase();
        } else if (function.equals("lowercase")) {
            String val=node.getStringValue(field);
            return val.toLowerCase();
        } else if (function.equals("hostname")) {
            String val=node.getStringValue(field);
            return hostname_function(val);
        } else if (function.equals("urlencode")) {
            String val=node.getStringValue(field);
            return getURLEncode(val);
        } else if (function.startsWith("wrap_")) {
            String val=node.getStringValue(field);
            try {
                int wrappos=Integer.parseInt(function.substring(5));
                return wrap(val,wrappos);
            } catch(Exception e) {}
        } else if (function.equals("currency_euro")) {
             double val = node.getDoubleValue(field);
             NumberFormat nf = NumberFormat.getNumberInstance (Locale.GERMANY);
             return  "" + nf.format(val);
        } else {
            StringBuffer arg = new StringBuffer(field);
             if (arguments != null) {
                 for (int i = 1; i < arguments.size(); i++) {
                     if (arg.length() > 0) arg.append(',');
                     arg.append(arguments.get(i));
                 }
             }
             return executeFunction(node, function, arg.toString());
        }
        return null;
    }

    /**
     * Executes a function on the field of a node, and returns the result.
     * This method is called by the builder's {@link #getValue} method.
     * Derived builders should override this method to provide additional functions.
     *
     * current functions are:<br />
     * on dates: date, time, timesec, longmonth, month, monthnumber, weekday, shortday, day, yearhort year<br />
     * on text:  wap, html, shorted, uppercase, lowercase <br />
     * on node:  age() <br />
     * on numbers: wrap_&lt;int&gt;, currency_euro <br />
     *
     * @param node the node whose fields are queries
     * @param field the fieldname that is requested
     * @return the result of the 'function', or null if no valid functions could be determined.
     * @deprecated use executeFunction(MMObjectNode, String, List)
     */
    protected Object executeFunction(MMObjectNode node, String function, String field) {
        if (log.isDebugEnabled()) {
            log.debug("Executing function " + function + " on node " + node.getNumber() + " with argument " + field);
        }
        return null;
    }

    /**
     * Returns all relations of a node.
     * This returns the relation objects, not the objects related to.
     * Note that the relations returned are always of builder type 'InsRel', even if they are really from a derived builser such as AuthRel.
     * @param src the number of the node to obtain the relations from
     * @return a <code>Vector</code> with InsRel nodes
     */
    public Vector getRelations_main(int src) {
        InsRel bul=mmb.getInsRel();
        if (bul == null) {
            log.error("getMMObject(): InsRel not yet loaded");
            return null;
        }
        return bul.getRelationsVector(src);
    }

    /**
     * Return the default url of this object.
     * The basic value returned is <code>null</code>.
     * @param src the number of the node to obtain the url from
     * @return the basic url as a <code>String</code>, or <code>null</code> if unknown.
     */
    public String getDefaultUrl(int src) {
        return null;
    }

    /**
     * Returns the path to use for TREEPART, TREEFILE, LEAFPART and LEAFFILE.
     * The system searches in a provided base path for a filename that matches the supplied number/alias of
     * a node (possibly extended with a version number). See the documentation on the TREEPART SCAN command for more info.
     * @param documentRoot the root of the path to search
     * @param path the subpath of the path to search
     * @param nodeNumber the numbve ror alias of the node to filter on
     * @param version the version number (or <code>null</code> if not applicable) to filter on
     * @return the found path as a <code>String</code>, or <code>null</code> if not found
     * This method should be added to the bridge so jsp can make use of it.
     * This method can be overriden to make an even smarter search possible.
     */
    public String getSmartPath(String documentRoot, String path, String nodeNumber, String version) {
        File dir = new File(documentRoot+path);
        if (version!=null) nodeNumber+="."+version;
        String[] matches = dir.list( new SPartFileFilter( nodeNumber ));
        if ((matches == null) || (matches.length <= 0))
        {
            return null;
        }
        return path + matches[0] + File.separator;
    }

    /**
     * Gets the number of nodes currently in the cache.
     * @return the number of nodes in the cache
     */
    public int getCacheSize() {
        return nodeCache.size();
    }

    /**
     * Return the number of nodes in the cache of one objecttype.
     * @param type the object type to count
     * @return the number of nodes of that type in the cache
     */
    public int getCacheSize(String type) {
        int i=mmb.getTypeDef().getIntValue(type);
        int j=0;
        for (Enumeration e=nodeCache.elements();e.hasMoreElements();) {
            MMObjectNode n=(MMObjectNode)e.nextElement();
            if (n.getOType()==i) j++;
        }
        return j;
    }

    /**
     * Get the numbers of the nodes cached (will be removed).
     */
    public String getCacheNumbers() {
        String results="";
        for (Enumeration e=nodeCache.elements();e.hasMoreElements();) {
            MMObjectNode n=(MMObjectNode)e.nextElement();
            if (!results.equals("")) {
                results+=","+n.getNumber();
            } else {
                results+=n.getNumber();
            }
        }
        return results;
    }

    /**
     * Delete the nodes cache.
     */
    public void deleteNodeCache() {
        nodeCache.clear();
    }

    /**
     * Get the next database key (unique index for an object).
     * @return an <code>int</code> value that is the next available key for an object.
     */
    public int getDBKey() {
        return mmb.getDBKey();
    }

    /**
     * Return the age of the node, determined using the daymarks builder.
     * @param node The node whose age to determine
     * @return the age in days, or 0 if unknown (daymarks builder not present)
     */
    public int getAge(MMObjectNode node) {
        return ((DayMarkers)mmb.getMMObject("daymarks")).getAge(node);
    }

    /**
     * Get the name of this mmserver from the MMBase Root
     * @return a <code>String</code> which is the server's name
     */
    public String getMachineName() {
        return mmb.getMachineName();
    }

    /**
     * Called when a remote node is changed.
     * Should be called by subclasses if they override it.
     * @param machine Name of the machine that changed the node.
     * @param number Number of the changed node as a <code>String</code>
     * @param builder type of the changed node
     * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return always <code>true</code>
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        // overal cache control, this makes sure that the caches
        // provided by mmbase itself (on nodes and relations)
        // are kept in sync is other servers add/change/delete them.
        if (ctype.equals("c") || ctype.equals("d")) {
            try {
                Integer i=new Integer(number);
                if (nodeCache.containsKey(i)) {
                    nodeCache.remove(i);
                }
            } catch (Exception e) {
                log.error("Not a number");
                log.error(Logging.stackTrace(e));
            }
        } else if (ctype.equals("r")) {
            try {
                Integer i=new Integer(number);
                MMObjectNode node=(MMObjectNode)nodeCache.get(i);
                if (node!=null) {
                    node.delRelationsCache();
                }
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }

        // signal all the other objects that have shown interest in changes of nodes of this builder type.
        for (Enumeration e=remoteObservers.elements();e.hasMoreElements();) {
            MMBaseObserver o=(MMBaseObserver)e.nextElement();
            if (o != this) {
                o.nodeRemoteChanged(machine,number,builder,ctype);
            } else {
                log.warn(getClass().getName()  + " " + toString() + " observes itself");
            }
        }

        MMObjectBuilder bul = mmb.getBuilder(builder);
        MMObjectBuilder pb = getParentBuilder();
        if(pb != null) { // && (pb.equals(bul) || pb.isExtensionOf(bul))) {
            log.debug("Builder "+tableName+" sending signal to builder "+pb.tableName+" (changed node is of type "+builder+")");
            pb.nodeRemoteChanged(machine, number, builder, ctype);
        }

        return true;
    }

    /**
     * Called when a local node is changed.
     * Should be called by subclasses if they override it.
     * @param machine Name of the machine that changed the node.
     * @param number Number of the changed node as a <code>String</code>
     * @param builder type of the changed node
     * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return always <code>true</code>
     */

    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        // overal cache control, this makes sure that the caches
        // provided by mmbase itself (on nodes and relations)
        // are kept in sync is other servers add/change/delete them.
        if (ctype.equals("d")) {
            try {
                Integer i=new Integer(number);
                if (nodeCache.containsKey(i)) {
                    nodeCache.remove(i);
                }
            } catch (Exception e) {
                log.error("Not a number");
                log.error(Logging.stackTrace(e));
            }
        } else
        if (ctype.equals("r")) {
            try {
                Integer i=new Integer(number);
                MMObjectNode node=(MMObjectNode)nodeCache.get(i);
                if (node!=null) {
                    node.delRelationsCache();
                }
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
            }

        }
        // signal all the other objects that have shown interest in changes of nodes of this builder type.
        for (Enumeration e = localObservers.elements();e.hasMoreElements();) {
            MMBaseObserver o = (MMBaseObserver)e.nextElement();
            if (o != this) {
                o.nodeLocalChanged(machine,number,builder,ctype);
            } else {
                log.warn(getClass().getName()  + " " + toString() + " observes itself");
            }
        }

        MMObjectBuilder bul = mmb.getBuilder(builder);
        MMObjectBuilder pb = getParentBuilder();
        if(pb != null) { // && (pb.equals(bul) || pb.isExtensionOf(bul))) {
            log.debug("Builder "+tableName+" sending signal to builder "+pb.tableName+" (changed node is of type "+builder+")");
            pb.nodeLocalChanged(machine, number, builder, ctype);
        }

        return true;
    }


    /**
     * Called when a local field is changed.
     * @param number Number of the changed node as a <code>String</code>
     * @param builder type of the changed node
     * @param field name of the changed field
     * @param value value it changed to
     * @return always <code>true</code>
     */
    public boolean fieldLocalChanged(String number,String builder,String field,String value) {
        log.debug("FLC="+number+" BUL="+builder+" FIELD="+field+" value="+value);
        return true;
    }

    /**
     * Adds a remote observer to this builder.
     * The observer is notified whenever an object of this builder is changed, added, or removed.
     * @return always <code>true</code>
     */
    public boolean addRemoteObserver(MMBaseObserver obs) {
        if (!remoteObservers.contains(obs)) {
            remoteObservers.addElement(obs);
        }
        return true;
    }

    /**
     * Adds a local observer to this builder.
     * The observer is notified whenever an object of this builder is changed, added, or removed.
     * @return always <code>true</code>
     */
    public boolean addLocalObserver(MMBaseObserver obs) {
        if (!localObservers.contains(obs)) {
            localObservers.addElement(obs);
        }
        return true;
    }

    /**
     *  Used to create a default teaser by any builder
     *  @deprecated Will be removed?
     */
    public MMObjectNode getDefaultTeaser(MMObjectNode node,MMObjectNode tnode) {
        log.warn("getDefaultTeaser(): Generate Teaser,Should be overridden");
        return tnode;
    }

    /**
     * Waits until a node is changed (multicast).
     * @param node the node to wait for
     */
    public boolean waitUntilNodeChanged(MMObjectNode node) {
        return mmb.mmc.waitUntilNodeChanged(node);
    }

    /**
     * Obtains a list of string values by performing the provided command and parameters.
     * This method is SCAN related and may fail if called outside the context of the SCAN servlet.
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param tagger a Hashtable of parameters (name-value pairs) for the command
     * @param tok a list of strings that describe the (sub)command to execute
     * @return a <code>Vector</code> containing the result values as a <code>String</code>
     */
    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws ParseException {
        throw new ParseException(getClass().getName() +" should override the getList method (you've probably made a typo)");
    }


    /**
     * Obtains a string value by performing the provided command.
     * The command can be called:
     * <ul>
     *   <li>by SCAN : $MOD-MMBASE-BUILDER-[buildername]-[command]</li>
     *   <li>in jsp : cloud.getNodeManager(buildername).getInfo(command);</li>
     * </lu>
     * This method is SCAN related and some commands may fail if called outside the context of the SCAN servlet.
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param tok a list of strings that describe the (sub)command to execute
     * @return the result value as a <code>String</code>
     */
    public String replace(scanpage sp, StringTokenizer tok) {
        log.warn("replace(): replace called should be overridden");
        return "";
    }

    /**
     * The hook that passes all form related pages to the correct handler.
     * This method is SCAN related and may fail if called outside the context of the SCAN servlet.
     * The methood is currentkly called by the MMEDIT module, whenever a 'PRC-CMD-BUILDER-...' command
     * is encountered in the list of commands to be processed.
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param command a list of strings that describe the (sub)command to execute (the portion after ' PRC-CMD-BUILDER')
     * @param cmds the commands (PRC-CMD) that are iurrently being processed, including the current command.
     * @param vars variables (PRC-VAR) thatw ere set to be used during processing. the variable 'EDITSTATE' accesses the
     *       {@link org.mmbase.module.gui.html.EditState} object (if applicable).
     * @return the result value as a <code>String</code>
     */
    public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
        return false;
    }

    /**
     * Converts an MMNODE expression to an SQL expression. Returns the
     * result as an SQL where-clause (including the leading "WHERE ").
     * <p>
     * The syntax of an MMNODE expression is defined as follows:
     * <ul>
     * <li><em>MMNODE expression</em>: "MMNODE fieldexpressions"
     * <li><em>fieldexpressions</em> is one field expression, or several
     *     field expressions combined with logical operators
     * <li><em>field expression</em>: "fieldXXvalue"
     * <li><em>field</em> is a fieldname (may be prefixed as in
           "prefix.fieldname")
     * <li><em>XX</em> is a 2 letter comparison operator: "==" (equal),
     *     "=E" (equal), "=N" (not equal), "=G" (greater than),
     *     "=g" (greater than or equal), "=S" (less than),
     *     "=s" (less than or equal).
     * <li><em>value</em> is a value. The form "*value*" is used to
     *     represent any string containing "value" when comparing for equality.
     * <li><em>logical operator</em> is "+" (AND) or "-" (AND NOT).
     * </ul>
     * MMNODE expressions are resolved by the database support classes.
     * This means that some database-specific expressions can easier be converted.
     *
     * @param where the MMNODE expression
     * @return The SQL expression.
     */
    public String convertMMNode2SQL(String where) {
        log.debug("convertMMNode2SQL(): "+where);
        String result="WHERE "+mmb.getDatabase().getMMNodeSearch2SQL(where,this);
        log.debug("convertMMNode2SQL(): results : "+result);
        return result;
    }

    /**
     * Creates query based on an MMNODE expression.
     *
     * @param expr The MMNODE expression.
     * @return The query.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @since MMBase-1.7
     */
     // package visibility
    NodeSearchQuery convertMMNodeSearch2Query(String expr) {
        NodeSearchQuery query = new NodeSearchQuery(this);
        BasicCompositeConstraint constraints
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        String logicalOperator = null;

        // Strip leading string "MMNODE " from expression, parse
        // fieldexpressions and logical operators.
        // (legacy: eol characters '\n' and '\r' are interpreted as "AND NOT")
        StringTokenizer tokenizer
            = new StringTokenizer(expr.substring(7), "+-\n\r", true);
        while (tokenizer.hasMoreTokens()) {
            String fieldExpression = tokenizer.nextToken();

            // Remove prefix if present (example episodes.title==).
            int pos = fieldExpression.indexOf('.');
            if (pos != -1) {
                fieldExpression = fieldExpression.substring(pos + 1);
            }

            // Break up field expression in fieldname, comparison operator
            // and value.
            pos = fieldExpression.indexOf('=');
            if (pos != -1 && fieldExpression.length() > pos + 2) {
                String fieldName = fieldExpression.substring(0, pos);
                char comparison = fieldExpression.charAt(pos + 1);
                String value = fieldExpression.substring(pos + 2);

                // Add corresponding constraint to constraints.
                FieldDefs fieldDefs = getField(fieldName);
                if (fieldDefs == null) {
                    throw new IllegalArgumentException(
                        "Invalid MMNODE expression: " + expr);
                }
                StepField field = query.getField(fieldDefs);
                BasicConstraint constraint
                    = parseFieldPart(field, comparison, value);
                constraints.addChild(constraint);

                // Set to inverse if preceded by a logical operator that is
                // not equal to "+".
                if (logicalOperator != null && !logicalOperator.equals("+")) {
                    constraint.setInverse(true);
                }
            } else {
                // Invalid expression.
                throw new IllegalArgumentException(
                    "Invalid MMNODE expression: " + expr);
            }

            // Read next logical operator.
            if (tokenizer.hasMoreTokens()) {
                logicalOperator = tokenizer.nextToken();
            }
        }

        List childs = constraints.getChilds();
        if (childs.size() == 1) {
            query.setConstraint((FieldValueConstraint) childs.get(0));
        } else if (childs.size() > 1) {
            query.setConstraint(constraints);
        }
        return query;
    }

    /**
     * Creates a {@link org.mmbase.storage.search.FieldCompareConstraint
     * FieldCompareConstraint}, based on parts of a field expression in a
     * MMNODE expression.
     *
     * @param fieldName The field name.
     * @param comparison The second character of the comparison operator.
     * @param strValue The value to compare with, represented as
     *        <code>String<code>.
     * @return The constraint.
     * @since MMBase-1.7
     */
    // package visibility!
    BasicFieldValueConstraint parseFieldPart(
            StepField field, char comparison, String strValue) {

        Object value = strValue;

        // For numberical fields, convert string representation to Double.
        if (field.getType() != FieldDefs.TYPE_STRING &&
            field.getType() != FieldDefs.TYPE_XML &&
            field.getType() != FieldDefs.TYPE_UNKNOWN) {
                // backwards comp fix. This is needed for the scan editors.
                int length = strValue.length();
                if (strValue.charAt(0) == '*' && strValue.charAt(length - 1) == '*') {
                        strValue = strValue.substring(1, length - 1);
                }

                value = Double.valueOf(strValue);
        }

        BasicFieldValueConstraint constraint =
            new BasicFieldValueConstraint(field, value);

        switch (comparison) {
            case '=':
            case 'E':
                // EQUAL (string field)
                if (field.getType() == FieldDefs.TYPE_STRING ||
                    field.getType() == FieldDefs.TYPE_XML) {
                    // Strip first and last character of value, when
                    // equal to '*'.
                    String str = (String) value;
                    int length = str.length();
                    if (str.charAt(0) == '*' && str.charAt(length - 1) == '*') {
                        value = str.substring(1, length - 1);
                    }

                    // Convert to LIKE comparison with wildchard characters
                    // before and after (legacy).
                    constraint.setValue('%' + (String) value + '%');
                    constraint.setCaseSensitive(false);
                    constraint.setOperator(FieldCompareConstraint.LIKE);

                // EQUAL (numerical field)
                } else {
                    constraint.setOperator(FieldCompareConstraint.EQUAL);
                }
                break;

            case 'N':
                constraint.setOperator(FieldCompareConstraint.NOT_EQUAL);
                break;

            case 'G':
                constraint.setOperator(FieldCompareConstraint.GREATER);
                break;

            case 'g':
                constraint.setOperator(FieldCompareConstraint.GREATER_EQUAL);
                break;

            case 'S':
                constraint.setOperator(FieldCompareConstraint.LESS);
                break;

            case 's':
                constraint.setOperator(FieldCompareConstraint.LESS_EQUAL);
                break;

            default:
                throw new IllegalArgumentException(
                    "Invalid comparison character: '" + comparison + "'");
        }
        return constraint;
    }

    /**
     * Set the MMBase object, and retrieve the database lasyer.
     * @param m the MMBase object to set as owner of this builder
     */
    public void setMMBase(MMBase m) {
        mmb = m;
    }

    /**
     * Return the MMBase object
     * @since 1.7
     */
    public MMBase getMMBase() {
        return mmb;
    }

    /**
     * Stores the fieldnames of a table in a vector, based on the current fields definition.
     * The fields 'otype' and 'owner' become the first and second fieldnames.
     * @deprecated sortedDBLayout should not be used any more. use the getFields(sortorder) method instead
     * @param fields A list of the builder's FieldDefs
     */
    public void setDBLayout_xml(Hashtable fields) {
        sortedDBLayout=new Vector();
        sortedDBLayout.addElement("otype");
        sortedDBLayout.addElement("owner");

        FieldDefs node;

        List orderedfields=getFields(FieldDefs.ORDER_CREATE);
        for (Iterator i=orderedfields.iterator();i.hasNext();) {
            node=(FieldDefs)i.next();
            String name=node.getDBName();
            if (name!=null && !name.equals("number") && !name.equals("otype") && !name.equals("owner")) {
                if(sortedDBLayout.contains(name)) {
                    log.fatal("Adding the field " + name + " to sortedDBLayout again. This is very wrong. Skipping");
                    continue;
                }
                sortedDBLayout.add(name);
            }
        }
    }

    /**
     * Set tablename of the builder. Should be used to initialize a MMTable object before calling init().
     * @param the name of the table
     */
    public void setTableName(String tableName) {
        this.tableName=tableName;
    }

    /**
     * Set description of the builder
     * @param the description text
     */
    public void setDescription(String e) {
        this.description=e;
    }

    /**
     * Set descriptions of the builder
     * @param a <code>Hashtable</code> containing the descriptions
     */
    public void setDescriptions(Hashtable e) {
        this.descriptions=e;
    }

    /**
     * Get description of the builder
     * @return the description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets description of the builder, using the specified language.
     * @param lang The language requested
     * @return the descriptions in that language, or <code>null</code> if it is not avaialble
     */
    public String getDescription(String lang) {
        if (descriptions==null) return null;
        String retval =(String)descriptions.get(lang);
        if (retval == null){
            return getDescription();
        }
    return retval;
    }

    /**
     * Get descriptions of the builder
     * @return  a <code>Hashtable</code> containing the descriptions
     */
    public Hashtable getDescriptions() {
        return descriptions;
    }

    /**
     * Sets search Age.
     * @param age the search age as a <code>String</code>
     */
    public void setSearchAge(String age) {
        this.searchAge=age;
    }

    /**
     * Gets search Age
     * @return the search age as a <code>String</code>
     */
    public String getSearchAge() {
        return searchAge;
    }

    /**
     * Gets short name of the builder, using the specified language.
     * @param lang The language requested
     * @return the short name in that language, or <code>null</code> if it is not available
     */
    public String getSingularName(String lang) {
    String tmp =null;
        if (singularNames !=null) {
            tmp = (String)singularNames.get(lang);
            if (tmp==null) tmp = (String)singularNames.get(mmb.getLanguage());
            if (tmp==null) tmp = (String)singularNames.get("en");
    }
        if (tmp==null) tmp = tableName;
        return tmp;
    }

    /**
     * Gets short name of the builder in the current default language.
     * If the current language is not available, the "en" version is returned instead.
     * If that name is not available, the internal builder name (table name) is returned.
     * @return the short name in either the default language or in "en"
     */
    public String getSingularName() {
        return getSingularName(mmb.getLanguage());
    }

    /**
     * Gets long name of the builder, using the specified language.
     * @param lang The language requested
     * @return the long name in that language, or <code>null</code> if it is not available
     */
    public String getPluralName(String lang) {
        String tmp = null;
        if (pluralNames !=null){
        tmp= (String)pluralNames.get(lang);
            if (tmp==null) tmp = (String)pluralNames.get(mmb.getLanguage());
            if (tmp==null) tmp = (String)pluralNames.get("en");
            if (tmp==null) tmp = getSingularName(lang);
    }
        if (tmp==null) tmp = tableName;
        return tmp;
    }

    /**
     * Gets long name of the builder in the current default language.
     * If the current language is not available, the "en" version is returned instead.
     * If that name is not available, the singular name is returned.
     * @return the long name in either the default language or in "en"
     */
    public String getPluralName() {
        return getPluralName(mmb.getLanguage());
    }

    /**
     * Returns the classname of this builder
     * @deprecated don't use
     */
    public String getClassName() {
        return this.getClass().getName();
    }

    /**
     * Send a signal to other servers that a field was changed.
     * @param node the node the field was changed in
     * @param fieldname the name of the field that was changed
     * @return always <code>true</code>
     */
    public boolean    sendFieldChangeSignal(MMObjectNode node,String fieldname) {
        // we need to find out what the DBState is of this field so we know
        // who to notify of this change
        int state=getDBState(fieldname);
        log.debug("Changed field="+fieldname+" dbstate="+state);

        // still a large hack need to figure out remote changes
        if (state==0) {}
        // convert the field to a string

        int type=getDBType(fieldname);
        String value="";
        if ((type==FieldDefs.TYPE_INTEGER) || (type==FieldDefs.TYPE_NODE)) {
            value=""+node.getIntValue(fieldname);
        } else if (type==FieldDefs.TYPE_STRING) {
            value=node.getStringValue(fieldname);
        } else {
            // should be mapped to the builder
        }

        fieldLocalChanged(""+node.getNumber(),tableName,fieldname,value);
        //mmb.mmc.changedNode(node.getNumber(),tableName,"f");
        return true;
    }

    /**
     * Send a signal to other servers that a new node was created.
     * @param tableName the table in which a node was edited (?)
     * @param number the number of the new node
     * @return always <code>true</code>
     */
    public boolean signalNewObject(String tableName,int number) {
        if (mmb.mmc!=null) {
            mmb.mmc.changedNode(number,tableName,"n");
        }
        return true;
    }


    /**
     * Converts a node to XML.
     * This routine does not take into account invalid charaters (such as &ft;, &lt;, &amp;) in a datafield.
     * @param node the node to convert
     * @return the XML <code>String</code>
     * @todo   This generates ad-hoc system id's and public id's.  Don't know what, why or how this is used.
     */
    public String toXML(MMObjectNode node) {
        StringBuffer body = new StringBuffer("<?xml version=\"" + version + "\"?>\n");
        body.append("<!DOCTYPE mmnode.").append(tableName).append(" SYSTEM \"").append(mmb.getDTDBase()).append("/mmnode/").append(tableName).append(".dtd\">\n");
        body.append("<" + tableName + ">\n");
        body.append("<number>" + node.getNumber() + "</number>\n");
        for (Enumeration e = sortedDBLayout.elements(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            int type = node.getDBType(key);
            body.append('<').append(key).append('>');
            if ((type == FieldDefs.TYPE_INTEGER)|| (type == FieldDefs.TYPE_NODE)) {
                body.append(node.getIntValue(key));
            } else if (type==FieldDefs.TYPE_BYTE) {
                body.append(node.getByteValue(key));
            } else {
                body.append(node.getStringValue(key));
            }
            body.append("</").append(key).append(">\n");
        }
        body.append("</").append(tableName).append(">\n");
        return body.toString();
    }

    /**
     * Sets a list of singular names (language - value pairs)
     */
    public void setSingularNames(Hashtable names) {
        singularNames=names;
    }

    /**
     * Gets a list of singular names (language - value pairs)
     */
    public Hashtable getSingularNames() {
        return singularNames;
    }

    /**
     * Sets a list of plural names (language - value pairs)
     */
    public void setPluralNames(Hashtable names) {
        pluralNames=names;
    }

    /**
     * Gets a list of plural names (language - value pairs)
     */
    public Hashtable getPluralNames() {
        return pluralNames;
    }

    /**
     * Get text from a blob field.
     * The text is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return a <code>String</code> containing the contents of a field as text
     */
    public String getShortedText(String fieldname,int number) {
        return mmb.getDatabase().getShortedText(tableName,fieldname,number);
    }

    /**
     * Get binary data of a database blob field.
     * The data is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return an array of <code>byte</code> containing the contents of a field as text
     */
    public byte[] getShortedByte(String fieldname, int number) {
        return mmb.getDatabase().getShortedByte(tableName, fieldname, number);
    }

    /**
     * Get binary data of a database blob field.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return an array of <code>byte</code> containing the contents of a field as text
     */
    public byte[] getDBByte(ResultSet rs,int idx) {
        return mmb.getDatabase().getDBByte(rs,idx);
    }

    /**
     * Get text from a blob field.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return a <code>String</code> containing the contents of a field as text
     */
    public String getDBText(ResultSet rs,int idx) {
        return mmb.getDatabase().getDBText(rs,idx);
    }

    /**
     * Returns the number of the node with the specified name.
     * Tests whether a builder table is created.
      * Should be moved to MMTable.
     * @return <code>true</code> if the table exists, <code>false</code> otherwise
     */
    public String getNumberFromName(String name) {
        String number = null;
        Enumeration e = search("name=='"+name+"'");
        if (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            number=""+node.getNumber();
        }
        return number;
    }


    /**
     *  Sets a key/value pair in the main values of this node.
     *  Note that if this node is a node in cache, the changes are immediately visible to
     *  everyone, even if the changes are not committed.
     *  The fieldname is added to the (public) 'changed' vector to track changes.
     *  @param fieldname the name of the field to change
     *  @param fieldValue the value to assign
     *  @param originalValue the value which was original in the field
     *  @return <code>true</code> When an update is required(when changed),
     *    <code>false</code> if original value was set back into the field.
     */
    public boolean setValue(MMObjectNode node,String fieldName, Object originalValue) {
        return setValue(node,fieldName);
    }

    /**
     * Provides additional functionality when setting field values.
     * This method is called whenever a Node of the builder's type tries to change a value.
     * It allows the system to add functionality such as checking valid data.
     * Derived builders should override this method if they want to add functionality.
     * @param node the node whose fields are changed
     * @param field the fieldname that is changed
     * @return <code>true</code> if the call was handled.
     */
    public boolean setValue(MMObjectNode node,String fieldname) {
        return true;
    }

    /**
     * Returns a HTML-version of a string.
     * This replaces a number of tokens with HTML sequences.
     * The default output does not match well with the new xhtml standards (ugly html), nor does it replace all tokens.
     *
     * Default replacements can be overridden by setting the builder properties in your <builder>.xml:
     *
     * html.alinea
     * html.endofline
     *
     * Example:
     * <properties>
     *   <property name="html.alinea"> &lt;br /&gt; &lt;br /&gt;</property>
     *   <property name="html.endofline"> &lt;br /&gt; </property>
     * </properties>
     *
     * @param body text to convert
     * @return the convert text
     */

    protected String getHTML(String body) {
        String rtn="";
        if (body!=null) {
            StringObject obj=new StringObject(body);
            // escape ampersand first
            obj.replace("&", "&amp;");

            obj.replace("<","&lt;");
            obj.replace(">","&gt;");
            // escape dollar-sign (prevent SCAN code to be run)
            obj.replace("$","&#36;");
            // unquote ampersand and quotes (see escapeXML method)
            obj.replace("\"", "&quot;");
            obj.replace("'", "&#39;");

            String alinea=getInitParameter("html.alinea");
            String endofline=getInitParameter("html.endofline");

            if (alinea!=null) {
                obj.replace("\r\n\r\n",alinea);
                obj.replace("\n\n",alinea);
            } else {
                obj.replace("\r\n\r\n", DEFAULT_ALINEA);
                obj.replace("\n\n", DEFAULT_ALINEA);
            }

            if (endofline!=null) {
                obj.replace("\r\n",endofline);
                obj.replace("\n",endofline);
            } else {
                obj.replace("\r\n", DEFAULT_EOL);
                obj.replace("\n", DEFAULT_EOL);
            }

            rtn=obj.toString();
        }
        return rtn;
    }

    /**
     * Returns a WAP-version of a string.
     * This replaces a number of tokens with WAP sequences.
     * @param body text to convert
     * @return the convert text
     */
    protected String getWAP( String body ) {
        String result = "";
        if( body != null ) {
            StringObject obj=new StringObject(body);
            obj.replace("\"","&#34;");
            obj.replace("&","&#38;#38;");
            obj.replace("'","&#39;");
            obj.replace("<","&#38;#60;");
            obj.replace(">","&#62;");
            result = obj.toString();
        }
        return result;
    }

    /**
     * Returns a URLEncoded-version (MIME x-www-form-urlencoded) of a string.
     * This version uses the java.net.URLEncoder class to encode it.
     * @param body text to convert
     * @return the URLEncoded text
     */
    protected String getURLEncode(String body) {
        String rtn="";
        if (body!=null) {
            rtn = URLEncoder.encode(body);
        }
        return rtn;
    }

    /**
     * Support routine to return shorter strings.
     * Cuts a string to a amximum length if it exceeds the length specified.
     * @param str the string to shorten
     * @param len the maximum length
     * @return the (possibly shortened) string
     */
    public String getShort(String str,int len) {
        if (str.length()>len) {
            return str.substring(0,(len-3))+"...";
        } else {
            return str;
        }
    }

    /**
     * Stores fields information of this table.
     * Asside from the fields supplied by the caller, a field 'otype' is added.
     * This method calls {@link #setDBLayout_xml} to create a fieldnames list.
     * @param xmlfields A Vector with fields as they appear in the current table.
     *        This data is retrieved from an outside source (such as an xml file), and thus
     *        may be incorrect.
     */
    public void setXMLValues(Vector xmlfields) {
        fields = new Hashtable();

        Enumeration enumeration = xmlfields.elements();
        while (enumeration.hasMoreElements()) {
            FieldDefs def=(FieldDefs)enumeration.nextElement();
            String name=(String)def.getDBName();
            def.setParent(this);
            fields.put(name,def);
        }

        // should be TYPE_NODE ???
        if (fields.get("otype") == null) {
            // if not defined in XML (legacy?)
            // It does currently not work if otype is actually defined in object.xml (as a NODE field)
            FieldDefs def=new FieldDefs("Type","integer",-1,-1,"otype",FieldDefs.TYPE_INTEGER,-1,3);
            // here, we should set the DBPos to 2 and adapt those of the others fields
            def.setDBPos(2);
            // required field
            def.setDBNotNull(true);
            enumeration = xmlfields.elements();
            while (enumeration.hasMoreElements()) {
                FieldDefs field=(FieldDefs)enumeration.nextElement();
                int pos=field.getDBPos();
                if (pos>1) field.setDBPos(pos+1);
            }
            def.setParent(this);
            fields.put("otype",def);
        }
        updateFields();
    }

    /**
     * Sets the subpath of the builder's xml configuration file.
     */
    public void setXMLPath(String m) {
         xmlPath = m;
    }

    /**
     * Retrieves the subpath of the builder's xml configuration file.
     * Needed for builders that reside in subdirectories in the builder configuration file directory.
     */
    public String getXMLPath() {
         return xmlPath;
    }

    /**
     * Gets the file that contains the configuration of this builder
     * @return the builders configuration File object
     */
    public File getConfigFile() {
        // what is the location of our builder?
        return new File(MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator + getXMLPath() + File.separator + getTableName() + ".xml");
    }

    /**
     * Set all builder properties
     * Changed properties will not be saved.
     * @param properties the properties to set
     */
    void setInitParameters(Hashtable properties) {
        this.properties = properties;
    }

    /**
     * Get all builder properties
     * @return a <code>Hashtable</code> containing the current properties
     */
    public Hashtable getInitParameters() {
        return properties;
    }

    /**
     * Set a single builder property
     * The propertie will not be saved.
     * @param name name of the property
     * @param value value of the property
     */
    public void setInitParameter(String name, String value) {
        if (properties == null) properties = new Hashtable();
        properties.put(name,value);
    }

    /**
     * Retrieve a specific property.
     * @param name the name of the property to get
     * @return the value of the property as a <code>String</code>
     */
    public String getInitParameter(String name) {
        if (properties==null)
            return null;
        else
            return (String)properties.get(name);
    }

    /**
     * Sets the version of this builder
     * @param i the version number
     */
    public void setVersion(int i) {
        version=i;
    }

    /**
     * Retrieves the version of this builder
     * @return the version number
     */
    public int getVersion() {
        return version;
    }

    /**
     * Retrieves the maintainer of this builder
     * @return the name of the maintainer
     */
    public String getMaintainer() {
        return maintainer;
    }

    /**
     * Sets the maintainer of this builder
     * @param m the name of the maintainer
     */
    public void setMaintainer(String m) {
        maintainer=m;
    }


    /**
     * hostname, parses the hostname from a url, so http://www.mmbase.org/bug
     * becomed www.mmbase.org
     * @deprecated Has nothing to do with mmbase nodes. Should be in org.mmbase.util
     */
    public String hostname_function(String url) {
        if (url.startsWith("http://")) {
            url=url.substring(7);
        }
        if (url.startsWith("https://")) {
            url=url.substring(8);
        }
        int pos=url.indexOf("/");
        if (pos!=-1) {
            url=url.substring(0,pos);
        }
        return url;
    }

    /**
     * Wraps a string.
     * Inserts newlines (\n) into a string at periodic intervals, to simulate wrapping.
     * This also removes whitespace to the start of a line.
     * @param text the text to wrap
     * @param width the maximum width to wrap at
     * @return the wrapped tekst
     */
    public String wrap(String text,int width) {
        StringTokenizer tok;
        String word;
        StringBuffer dst=new StringBuffer();
        int pos;

        tok=new StringTokenizer(text," \n\r",true);
        pos=0;
        while(tok.hasMoreTokens()) {
            word=tok.nextToken();
            if (word.equals("\n")) {
                pos=0;
            } else if (word.equals(" ")) {
                if (pos==0) {
                    word="";
                } else {
                    pos++;
                    if (pos>=width) {
                        word="\n";
                        pos=0;
                    }
                }
            } else {
                pos+=word.length();
                if (pos>=width) {
                    dst.append("\n");
                    pos=word.length();
                }
            }
            dst.append(word);
        }
        return dst.toString();
    }

    /**
     * Gets a substring.
     * @param value the string to get a substring of
     * @param len the length of the substring
     * @param filler if not null, this field is used as a trailing tekst
     * of the created substring.
     * @return the substring
     */
    private String substring(String value,int len,String filler) {
        if (filler==null) {
            if (value.length()>len) {
                return value.substring(0,len);
            } else {
                return value;
            }
        } else {
            int len2=filler.length();
            if ((value.length()+len2)>len) {
                return value.substring(0,(len-len2))+filler;
            } else {
                return value;
            }
        }
    }


 /**
     * This method returns all fields of the builder that have a FieldDefs.DBSTATE_PERSISTENT or a FieldDefs.DBSTATE_SYSTEM DBState ecluding fields  that have a DBType FieldDefs.TYPE_BYTE
     * @param builderName the name of the builder
     * @return a String containing the fields in the database separated by a comma
     * @since  MMBase-1.6.2
     *
     **/
    private String getNonByteArrayFields(){
        StringBuffer sb = new StringBuffer();
        Iterator fieldIter = getFields(FieldDefs.ORDER_CREATE).iterator();

        boolean first = true;

        while(fieldIter.hasNext()){
            FieldDefs def = (FieldDefs)fieldIter.next();
            if (def.getDBType() != FieldDefs.TYPE_BYTE && (def.getDBState() == FieldDefs.DBSTATE_PERSISTENT || def.getDBState() == FieldDefs.DBSTATE_SYSTEM)){
                if (! first) {
                    sb.append(",");
                }

                sb.append(getFullTableName() + "." + mmb.getDatabase().getAllowedField(def.getDBName()));
                first = false;
            }
        }
        return sb.toString();
    }

    /**
     * Implmenting a sensible toString is usefull for debugging.
     *
     * @since MMBase-1.6.2
     */

    public String toString() {
        return getSingularName();
    }
    /**
     * Equals must be implemented because of the list of MMObjectBuilder which is used for ancestors
     *
     * @since MMBase-1.6.2
     */
    public boolean equals(Object o) {
        if (o instanceof MMObjectBuilder) {
            MMObjectBuilder b = (MMObjectBuilder) o;
            return tableName.equals(b.tableName);
        }
        return false;
    }


    /**
     * Implements for MMObjectNode
     * @since MMBase-1.6.2
     */

    public String toString(MMObjectNode n) {
        return n.defaultToString();
    }

    /**
     * Implements equals for nodes (this is in MMObjectBuilder because you cannot override MMObjectNode)
     *
     * @since MMBase-1.6.2
     */

    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        return o1.defaultEquals(o2);
    }

    /**
     * Implements for MMObjectNode
     * @since MMBase-1.6.2
     */

    public int hashCode(MMObjectNode o) {
        return 127 * o.getNumber();
    }


}


