/*
 * MMObjectBuilder.java
 *
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 *
 */
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import java.net.URLEncoder;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.ParseException;
import org.mmbase.module.database.*;
import org.mmbase.module.gui.html.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.support.*;
import org.mmbase.module.database.MultiConnection;

import org.mmbase.util.logging.*;

/**
 * This class is the base class for all builders.
 * It offers a list of routines which are useful in maintaining the nodes in the MMBase
 * object cloud.
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
 * @author Johan Verelst
 * @version $Revision: 1.115 $ $Date: 2002-02-14 12:32:33 $
 */
public class MMObjectBuilder extends MMTable {

    // Max size of the object type cache
    public final static int OBJ2TYPE_MAX_SIZE=20000;

    // Max size of the node cache
    public final static int NODE_MAX_SIZE=1024*4;

    // Default size of the temporary node cache
    public final static int TEMPNODE_DEFAULT_SIZE=1024;

    /**
     * The cache that contains the last X types of all requested objects
     * X is currently set to 20000.
     * The hashtable is created using the init_obj2type() method, which
     * seems strange - as other caches are instantiated during variable declaration.
     */
    public static LRUHashtable obj2type;

    /**
     * The cache that contains the X last requested nodes
     * X is currently set to 4096
     */
    public static LRUHashtable nodeCache = new LRUHashtable(NODE_MAX_SIZE);

    /**
     * Collection for temporary nodes,
     * Used by the Temporarynodemanager when working with transactions
     * The default size is 1024.
     */
    public static Hashtable TemporaryNodes=new Hashtable(TEMPNODE_DEFAULT_SIZE);

    /**
     * The class used to store and retrieve data in the database that is currently in use.
     */
    public static MMJdbc2NodeInterface database = null;

    // unused. hitlisted ???
    static String currentPreCache=null;
    private static Hashtable fieldDefCache=new Hashtable(40);
    // unused

    /**
     * Determines whether the cache is locked.
     * A locked cache can be read, and nodes can be removed from it (allowing it to
     * clean invalid nodes), but nodes cannot be added.
     * Needed for committing nodes from transactions.
     */
    private static int cacheLocked=0;

    /**
     * Logger routine
     */
    private static Logger log = Logging.getLoggerInstance(MMObjectBuilder.class.getName());

    /**
     * If true, debug messages are send to the MMBase log
     * @deprecated : use Logger routines instead
     */
   public boolean debug=false;

    /**
     * Sets debugging on or off
     * @deprecated : use Logger routines instead
     */
    public void setDebug(boolean state) { debug=state; }

    /**
     * The current builder's object type
     * Retrieved from the TypeDef builder.
     */
    public int oType=0;

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
     */
    public Vector sortedDBLayout = null;

    /**
     * The default search age for this builder.
     * Used for intializing editor search forms (see HtmlBase)
     * Default value is 31. Can be changed with the &lt;searchage&gt; tag in the xml builder file.
     */
    public String searchAge="31";

    /**
     * The classname as specified in the builder xml file
     * mainly for use in export
     */
    public String className="onbekend";
    /**
     * Detemines whether the cache need be refreshed?
     * Seems useless, as this value is never changed (always true)
     * @see #readSearchResults
     */
    public boolean replaceCache=true;

    /**
     * Determines whether changes to this builder need be broadcasted to other known mmbase servers.
     * This setting also governs whether the cache for relation builders is emptied when a relation changes.
     * Actual broadcasting (and cache emptying) is initiated in the 'database' object, when
     * changes are commited to the database.
     * By default, all builders broadcast their changes, with the exception of the TypeDef builder.
     */
    public boolean broadcastChanges=true;

    /**
     * Contains builder fields in order of appearance in search forms
     */
    Vector sortedEditFields = null;
    /**
     * Contains builder fields in order of appearance in list forms
     */
    Vector sortedListFields = null;
    /**
     * Contains builder fields in order of appearance in input forms
     */
    Vector sortedFields = null;

    /**
     *  Maintainer information for builder registration
     *  Set with &lt;builder maintainer="mmbase.org" version="0"&gt; in the xml builder file
     */
    String maintainer="mmbase.org";

    /**
     * Default output when no data is available to determine a node's GUI description
     */
    String GUIIndicator="no info";

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

    // Unused
    Statistics statbul;
    // Unused
    Hashtable nameCache=new Hashtable();

    /**
     * Full filename (path + buildername + ".xml") where we loaded the builder from
     * It is relative from the '/builders/' subdir
     */
    String xmlPath = "";

    // contains the builder's field definitions
    protected Hashtable fields;

    // actual classname
    private String classname = getClass().getName();

    // Version information for builder registration
    // Set with &lt;builder maintainer="mmbase.org" version="0"&gt; in the xml builder file
    private int version=0;

    // Dutch builder description (?)
    private String dutchSName="onbekend";

    // ???
    private Vector qlist=new Vector();

    /**
     * determines whether builders are created using xml, accessible through {@link #getXMLConfig}
     */
    private boolean isXmlConfig=false;

    /**
     * Determines whether a builder is virtual (data is not stored in a database).
     */
    protected boolean virtual=false;

    // Properties of a specific Builder.
    // Specified in the xml builder file with the <properties> tag.
    // The use of properties is determined by builder
    private Hashtable properties = null;

    /**
     * Constructor.
     * Derived builders should provide their own constructors, rather than use this one.
     */
    public MMObjectBuilder() {}

    /**
     * Initializes this builder
     * The property 'mmb' needs to be set for the builder before this method can be called.
     * The method retrieves data from the TypeDef builder, or adds data to thet builder if the
     * current builder si not yet registrered.
     * @return Always true.
     * @see #create
     */
    public boolean init() {
        database=mmb.getDatabase();

        if (!created()) {
            log.info("init(): Create "+tableName);
            create();
        }
        if (!tableName.equals("object") && mmb.getTypeDef()!=null) {
            oType=mmb.getTypeDef().getIntValue(tableName);
            if (oType==-1) {
                //mmb.getTypeDef().insert("system",tableName,description);
                MMObjectNode node=mmb.getTypeDef().getNewNode("system");
                node.setValue("otype",1);
                node.setValue("name",tableName);
                if (description==null) description="not defined in this langauge";
                node.setValue("description",description);
                node.insert("system");
                oType=mmb.getTypeDef().getIntValue(tableName);
            }
        } else {
            if(!tableName.equals("typedef")) {
                log.warn("init(): for tablename("+tableName+") -> can't get to typeDef");
            }
        }
        // hack to override the hard  fields by database (bootstrap)
        // Hashtable tmp=initFields();
        // if (tmp.size()>0) fields=tmp;
        //if (fieldDefCache==null) initAllFields();
        if (obj2type==null) init_obj2type(); // RICO switched ON
        //if (fields==null) initFields(true);
        return true;
    }

    /**
     * Creates a new builder table in the current database.
     */
    public boolean create() {
        return database.create(this);
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
     *		The basic routine does not create any nodes this way and always fails.
     */
    public int insert(int oType,String owner) {
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
        // test with counting
        statCount("insert");

        try {
            int n;
            n=database.insert(this,owner,node);
            if (n>=0) safeCache(new Integer(n),node);
            String alias=node.getAlias();
            if (alias!=null) createAlias(n,alias);	// add alias, if provided
            return n;
        } catch(Exception e) {
            log.error("ERROR INSERT PROBLEM ! Error node="+node);
            log.error(Logging.stackTrace(e));
            return -1;
        }
    }

    /**
     * Once a insert is done in the editor this method is called.
     * @param ed Contains the current edit state (editor info). The main function of this object is to pass
     *		'settings' and 'parameters' - value pairs that have been the during the edit process.
     * @param node The node thatw as inserted
     * @return An <code>int</code> value. It's meaning is undefined.
     *		The basic routine returns -1.
     * @deprecated This method doesn't seem to fit here, as it references a gui/html object ({@link org.mmbase.module.gui.html.EditState}),
     *	endangering the separation between content and layout, and has an undefined return value.
     */
    public int insertDone(EditState ed, MMObjectNode node) {
        return -1;
    }

    /**
     * Check and make last changes before calling {@link #commit} or {@link #insert}.
     * This method is called by the editor. This differs from {@link #preCommit}, which is called by the database system
     * <em>during</em> the call to commit or insert.
     * @param ed Contains the current edit state (editor info). The main function of this object is to pass
     *		'settings' and 'parameters' - value pairs that have been the during the edit process.
     * @param node The node that was inserted
     * @return An <code>int</code> value. It's meaning is undefined.
     *		The basic routine returns -1.
     * @deprecated This method doesn't seem to fit here, as it references a gui/html object ({@link org.mmbase.module.gui.html.EditState}),
     *	endangering the separation between content and layout. It also has an undefined return value.
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
     * @return The committed node.
     */
    public boolean commit(MMObjectNode node) {
        return database.commit(this,node);
    }

    /**
     *  Creates an alias for a node, provided the OAlias builder is loaded.
     *  @param number the to-be-aliased node's unique number
     *  @param alias the aliasname to associate with the object
     */
    public void createAlias(int number,String alias) {
        if (mmb.getOAlias()!=null) {
                MMObjectNode node=mmb.getOAlias().getNewNode("system");
                node.setValue("name",alias);
                node.setValue("destination",number);
                node.insert("system");
        }
    }

    /**
     * Creates a cache for storing types and objects.
     * The cache can contain a maximum of OBJ2TYPE_MAX_SIZE elements.
     * Note that this should possibly be moved to the variable declaration part (like nodecache)?
     */
    public synchronized void init_obj2type() {

        if (obj2type!=null) return;
        obj2type=new LRUHashtable(OBJ2TYPE_MAX_SIZE);

        // This doesn't do anything...
        if (false) {
            // do the query on the database
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT "+mmb.getDatabase().getNumberString()+","+mmb.getDatabase().getOTypeString()+" FROM "+mmb.baseName+"_object;");
                while(rs.next() && (obj2type.size()<OBJ2TYPE_MAX_SIZE)) {
                    obj2type.put(new Integer(rs.getInt(1)),new Integer(rs.getInt(2)));
                }
                stmt.close();
                con.close();

            } catch (SQLException e) {
                log.error(Logging.stackTrace(e));
            }
        }
        return;
    }

    /**
     * Get a new node, using this builder as its parent. The new node is not a part of the cloud yet, and thus has
     * the value -1 as a number. (Call {@link #insert} to add the node to the cloud).
     * @param owner The administrator creating the new node.
     * @return A newly initialized <code>MMObjectNode</code>.
     */
    public MMObjectNode getNewNode(String owner) {
        MMObjectNode node=new MMObjectNode(this);
        node.setValue("number",-1);
        node.setValue("owner",owner);
        node.setValue("otype",oType);
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
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        /*
        // check if node.name in nameCache, remove that also.
        // --------------------------------------------------
        String name = node.getStringValue("name");
        if( name != null && !name.equals("")) {
            String sNumber = (String)nameCache.get(name);
            try {
                int number = Integer.parseInt( sNumber );
                if( number == node.getNumber()) {
                    nameCache.remove( name );
                }
            } catch( NumberFormatException e ) {
                log.debug("removeNode("+node+"): ERROR: snumber("+sNumber+") from nameCache not valid number!");
            }
        }
        */

        // removes the node FROM THIS BUILDER
        // seems not a very logical call, as node.parent is the node's actual builder,
        // which may - possibly - be very different from the current builder

        database.removeNode(this,node);
    }

    /**
     * Remove the relations of a node.
     * @param node The node whose relations to remove.
     */
    public void removeRelations(MMObjectNode node) {
        Vector relsv=getRelations_main(node.getNumber());
        if (relsv!=null) {
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
     * Is this byuilder virtual?
     * A virtual builder represents nodes that are not stored or retrieved directly
     * from the database, but are created as needed.
     * @return <code>true</code> if the builder is virtual.
     */
    public boolean isVirtual() {
        return virtual;
    }

    /**
     * Stores a node in the cache provided the cache is not locked.
     */
    public void safeCache(Integer n, MMObjectNode node) {
        synchronized(nodeCache) {
            if(cacheLocked==0) {
                nodeCache.put(n,node);
            }
        }
    }

    /**
     * Locks the node cache during the commit of a node.
     * This prevents the cache from gaining an invalid state
     * during the commit.
     */
    public boolean safeCommit(MMObjectNode node) {
        boolean res=false;
        try {
            synchronized(nodeCache) {
                cacheLocked++;
            }
            nodeCache.remove(new Integer(node.getNumber()));
            res=node.commit();
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
    public int safeInsert(MMObjectNode node, String username) {
        int res=-1;
        try {
            synchronized(nodeCache) {
                cacheLocked++;
            }
            // determine valid username
            if ((username==null) || (username.length()<=1)) {
                username=node.getStringValue("owner");
            }
            res=node.insert(username);
            if (res > -1) {
                nodeCache.put(new Integer(res),node);
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
     * @param number The number of the node to search for
     * @return an <code>int</code> value which is the object type (otype) of the node.
     */
    public int getNodeType(int number) {
      int otype=-1;
      try {
        // first try our mega cache for the convert
        if (obj2type!=null) {
                Integer tmpv=(Integer)obj2type.get(new Integer(number));
                if (tmpv!=null) otype=tmpv.intValue();
        }
        if (otype==-1 || otype==0) {
                // first get the otype to select the correct builder
                MultiConnection con=mmb.getConnection();
                Statement stmt2=con.createStatement();
                ResultSet rs=stmt2.executeQuery("SELECT "+mmb.getDatabase().getOTypeString()+" FROM "+mmb.baseName+"_object WHERE "+mmb.getDatabase().getNumberString()+"="+number);
                if (rs.next()) {
                    otype=rs.getInt(1);
                    // hack hack need a better way
                    if (otype!=0) {
                        if (obj2type!=null) obj2type.put(new Integer(number),new Integer(otype));
                    }
                }
                stmt2.close();
                con.close();
        }
      } catch (SQLException e) {
            // something went wrong print it to the logs
            log.error(Logging.stackTrace(e));
            return -1;
      };
      return otype;
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
        int nr;
        MMObjectNode node = null;
        if( key == null ) {
            log.error("getNode(null): ERROR: for tablename("+tableName+"): key is null!");
            return null;
        }
        try {
            nr=Integer.parseInt(key);
        } catch (Exception e) {
            nr=-1;
        }
        // is not a number, try top obtain the number from the alias builder
        if ((nr<0) && (mmb.getOAlias()!=null)) {
            nr=mmb.getOAlias().getNumber(key);
        }
        // load the node if the number is right
        if (nr>0) {
            node=getNode(nr,usecache);
        }
        return node;
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
        return getNode(key,true);
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
     * @param number The number of the node to search for
     * @param usecache If true, the node is retrieved from the node cache if possible.
     * @return <code>null</code> if the node does not exist or the key is invalid, or a
     *       <code>MMObjectNode</code> containign the contents of the requested node.
     */
    public synchronized MMObjectNode getNode(int number, boolean usecache) {
        // test with counting
        statCount("getnode");
        if (number==-1) {
            log.warn(" ("+tableName+") nodenumber == -1");
            return null;
        }
        MMObjectNode node=null;
        Integer integerNumber=new Integer(number);
        // try cache if indicated to do so
        if (usecache) {
            node=(MMObjectNode)nodeCache.get(integerNumber);
            if (node!=null) {
                return node;
            }
        }
        // do the query on the database
        try {
            String bul="typedef";

            // retrieve node's objecttype
            int bi=getNodeType(number);
            if (bi!=0) {
                bul=mmb.getTypeDef().getValue(bi);
             }
            if (bul==null) {
                log.error("getNode(): got a null type table ("+bi+") on node ="+number+", possible non table query blocked !!!");
                return null;
            }

            MultiConnection con =null;
            Statement stmt = null;

            try {
                con=mmb.getConnection();
                stmt=con.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+bul+" WHERE "+mmb.getDatabase().getNumberString()+"="+number);
                if (rs.next()) {
                    // create a new object and add it to the result vector
                    MMObjectBuilder bu=mmb.getMMObject(bul);
                    if (bu==null) {
                        log.error("getMMObject did not return builder on : "+bul);
                        return null;
                    }
                    node=new MMObjectNode(bu);
                    ResultSetMetaData rd=rs.getMetaData();
                    String fieldname;
                    String fieldtype;
                    for (int i=1;i<=rd.getColumnCount();i++) {
                        fieldname=rd.getColumnName(i);
                        node=database.decodeDBnodeField(node,fieldname,rs,i);
                    }
                    // store in cache if indicated to do so
                    if (usecache) {
                        safeCache(integerNumber,node);
                    }
                    // clear the changed signal
                    node.clearChanged();
                } else {
                    log.warn("getNode(): Node not found "+number);
                    return null; // not found
                }
            } finally {
                mmb.closeConnection(con,stmt);
            }
            // return the results
            return node;
        } catch (SQLException e) {
            // something went wrong print it to the logs
            log.error(Logging.stackTrace(e));
            return null;
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
        return getNode(number,true);
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
        checkAddTmpField("_number");
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
        checkAddTmpField("_number");
        node.setValue("_number",key);
        TemporaryNodes.put(key,node);
    }

    /**
     * Defines a virtual field to use for temporary nodes
     * @param field the anme of the temporary field
     * @return true if the field was added, false if it already existed.
     */
    public boolean checkAddTmpField(String field) {
        boolean rtn=false;
        if (getDBState(field)==FieldDefs.DBSTATE_UNKNOWN) {
            FieldDefs fd=new FieldDefs(field,"string",-1,-1,field,FieldDefs.TYPE_STRING,-1,FieldDefs.DBSTATE_VIRTUAL);
            fd.setParent(this);
            log.debug("checkAddTmpField(): adding tmp field "+field);
            addField(fd);
            rtn=true;
        }
        return rtn;
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
     * Count all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return the number of an <code>Enumeration</code> containing all the objects that apply.
     */
    public int count(String where) {
        if (where==null) where="";
        if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            where=QueryConvertor.altaVista2SQL(where,database);
        }
        String query="SELECT Count(*) FROM "+getFullTableName()+" "+where;
        return basicCount(query);
    }

    /**
     * Executes a search (sql query) on the current database
     * and returns the nodes that result from the search as a Vector.
     * If the query is null, gives no results, or results in an error, an empty enumeration is returned.
     * @param query The SQL query
     * @return A Vector which contains all nodes that were found
     */
    private int basicCount(String query) {
        statCount("count");
        int nodecount=-1;
        MultiConnection con=null;
        Statement stmt=null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            if (rs.next()) {
                nodecount= rs.getInt(1);
            }
            stmt.close();
            con.close();
            // return the results
        } catch (Exception e) {
            // something went wrong print it to the logs
            log.error("basicSearch(): ERROR in search "+query);
            mmb.closeConnection(con,stmt);
        }
        return nodecount;
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration search(String where) {
        return searchVector(where).elements();
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return a vector containing all the objects that apply.
     * @deprecated Use search() instead
     */
    public Vector searchVector(String where) {
        // do the query on the database
        if (where==null) where="";
        if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            //where=QueryConvertor.altaVista2SQL(where);
            where=QueryConvertor.altaVista2SQL(where,database);
        }
        String query="SELECT * FROM "+getFullTableName()+" "+where;
        return basicSearch(query);
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *		returning a set of object numbers.
     * @return a vector containing all the objects that apply.
     */
    public Vector searchVectorIn(String in) {
        // do the query on the database
        if (in==null || in.equals("")) return new Vector();
        String query="SELECT * FROM "+getFullTableName()+" where "+mmb.getDatabase().getNumberString()+" in ("+in+")";
        return basicSearch(query);
    }

    /**
     * Executes a search (sql query) on the current database
     * and returns the nodes that result from the search as a Vector.
     * If the query is null, gives no results, or results in an error, an empty enumeration is returned.
     * @param query The SQL query
     * @return A Vector which contains all nodes that were found
     */
    private Vector basicSearch(String query) {
        // test with counting
        statCount("search");

        MultiConnection con=null;
        Statement stmt=null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            Vector results=readSearchResults(rs);
            stmt.close();
            con.close();
            // return the results
            return results;
        } catch (Exception e) {
            // something went wrong print it to the logs
            log.error("basicSearch(): ERROR in search "+query);
            mmb.closeConnection(con,stmt);
        }

        return new Vector(); // Return an empty Vector
    }

    /**
     * Rewturns a Vector containing all the objects that match the searchkeys. Only returns the object numbers.
     * @param where scan expression that the objects need to fulfill
     * @return a <code>Vector</code> containing all the object numbers that apply, <code>null</code> if en error occurred.
     */
    public Vector searchNumbers(String where) {
        // do the query on the database
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT "+mmb.getDatabase().getNumberString()+" FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database));
            Vector results=new Vector();
            Integer number;
            String tmp;
            while(rs.next()) {
                results.addElement(new Integer(rs.getInt(1)));
            }
            stmt.close();
            con.close();
            return results;
        } catch (SQLException e) {
            // something went wrong print it to the logs
            log.error(Logging.stackTrace(e));
            return null;
        }
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration search(String where,String sort) {
        return searchVector(where,sort).elements();
    }


    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in lost of node numbers to filter on
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration searchIn(String where,String sort,String in) {
        return searchVectorIn(where,sort,in).elements();
    }


    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param in lost of node numbers to filter on
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration searchIn(String where,String in) {
        return searchVectorIn(where,in).elements();
    }


    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *		Only applies if a sorted order is given.
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration search(String where,String sort,boolean direction) {
        return searchVector(where,sort,direction).elements();
    }


    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in lost of node numbers to filter on
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *		Only applies if a sorted order is given.
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration searchIn(String where,String sort,boolean direction,String in) {
        return searchVectorIn(where,sort,direction,in).elements();
    }


    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @return a vector containing all the objects that apply.
     */
    public Vector searchVector(String where,String sorted) {
        // do the query on the database
        if (where==null) {
            where="";
        } else if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            //where=QueryConvertor.altaVista2SQL(where);
            where=QueryConvertor.altaVista2SQL(where,database);
        }

        // temp mapper hack only works in single order fields
        sorted=mmb.getDatabase().getAllowedField(sorted);
        String query="SELECT * FROM "+getFullTableName()+" "+where+" ORDER BY "+sorted;
        return basicSearch(query);
    }


    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *		returning a set of object numbers.
     * @return a vector containing all the objects that apply.
     */
    public Vector searchVectorIn(String where,String sorted,String in) {
        // temp mapper hack only works in single order fields
        sorted=mmb.getDatabase().getAllowedField(sorted);
        // do the query on the database
        if (in!=null && in.equals("")) return new Vector();
        String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted;
        return basicSearch(query);
    }

    /*
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *		returning a set of object numbers.
     * @return a vector containing all the objects that apply.
     */
    public Vector searchVectorIn(String where,String in) {
        // do the query on the database
        if (in==null || in.equals("")) return new Vector();
        String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+")";
        return basicSearch(query);
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *		Only applies if a sorted order is given.
     * @return a vector containing all the objects that apply.
     */
    public Vector searchVector(String where,String sorted,boolean direction) {
        // do the query on the database
        if (where==null) {
            where="";
        } else if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            //where=QueryConvertor.altaVista2SQL(where);
            where=QueryConvertor.altaVista2SQL(where,database);
        }
        // temp mapper hack only works in single order fields
        sorted=mmb.getDatabase().getAllowedField(sorted);
        if (direction) {
            String query="SELECT * FROM "+getFullTableName()+" "+where+" ORDER BY "+sorted+" ASC";
            return basicSearch(query);
        } else {
            String query="SELECT * FROM "+getFullTableName()+" "+where+" ORDER BY "+sorted+" DESC";
            return basicSearch(query);
        }
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
     */
    public Vector searchVector(String where, String sorted, String directions) {
        if (where==null) {
            where="";
        } else if (where.indexOf("MMNODE")!=-1) {
            where=convertMMNode2SQL(where);
        } else {
            //where=QueryConvertor.altaVista2SQL(where);
            where=QueryConvertor.altaVista2SQL(where,database);
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
        String query = "SELECT * FROM " + getFullTableName() + " " + where
                       + " ORDER BY " + orderBy;
        return basicSearch(query);
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param in either a set of object numbers (in comma-separated string format), or a sub query
     *		returning a set of object numbers.
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *		Only applies if a sorted order is given.
     * @return a vector containing all the objects that apply.
     */
    public Vector searchVectorIn(String where,String sorted,boolean direction,String in) {
        // temp mapper hack only works in single order fields
        sorted=mmb.getDatabase().getAllowedField(sorted);
        // do the query on the database
        if (in==null || in.equals("")) return new Vector();
        if (direction) {
            String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted+" ASC";
            return basicSearch(query);
        } else {
            String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted+" DESC";
            return basicSearch(query);
        }
    }

    /**
     * Enumerate all the objects that match the where clause
     * This method is slightly faster than search(), since it does not try to 'parse'
     * the where clause.
     * @param where where clause (SQL-syntax) that the objects need to fulfill
     * @return an <code>Enumeration</code> containing all the objects that apply.
     */
    public Enumeration searchWithWhere(String where) {
        // do the query on the database
        String query="SELECT * FROM "+getFullTableName()+" where "+where;
        Vector results=basicSearch(query);
        if (results!=null) {
            return results.elements();
        } else {
            return null;
        }
    }


    /**
     * Store the nodes in the resultset, obtained from a builder, in a vector.
     * The nodes retrieved are added to the cache.
     * @param rs The resultset containing the nodes
     * @return The vector which is to hold the data
     */
    private Vector readSearchResults(ResultSet rs) {
        MMObjectNode node=null;
        Vector results=new Vector();
        Integer number;
        String tmp;
        try {
            while(rs.next()) {
                // create a new object and add it to the result vector
                node=new MMObjectNode(this);
                ResultSetMetaData rd=rs.getMetaData();
                String fieldname;
                String fieldtype;
                for (int i=1;i<=rd.getColumnCount();i++) {
                    fieldname=rd.getColumnName(i);
                    //fieldtype=rd.getColumnTypeName(i);
                    node=database.decodeDBnodeField(node,fieldname,rs,i);
                }
                // clear the changed signal
                node.clearChanged(); // huh ?
                results.addElement(node);

                if (oType==node.getOType()) {
                    // huge trick to fill the caches does it make sense ?
                    number=new Integer(node.getNumber());
                    if (!nodeCache.containsKey(number) || replaceCache) {
                        safeCache(number,node);
                    } else {
                        node=(MMObjectNode)nodeCache.get(number);
                    }
                }
            }
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return results;
    }


    /**
     * Store the nodes in the resultset, obtained from a builder, in a sorted vector.
     * (Called by nl.vpro.mmbase.module.search.TeaserSearcher.createShopResult ?)
     * The nodes retrieved are added to the cache.
     * @param rs The resultset containing the nodes
     * @return The SortedVector which holds the data
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
                    node=database.decodeDBnodeField(node,fieldname,rs,index);
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
     * @parame nodes Vector containg the nodes to put in the set
     * @param fieldName fieldname whsoe values should be put in the set
     * @return a comma-seperated list of values, as a <code>String</code>
     */
    public String buildSet(Vector nodes, String fieldName) {
        String result = "(";
        Enumeration enum = nodes.elements();
        MMObjectNode node;

        while (enum.hasMoreElements()) {
            node = (MMObjectNode)enum.nextElement();

            if(enum.hasMoreElements()) {
                result += node.getValue(fieldName) + ", ";
            } else {
                result += node.getValue(fieldName);
            }

        }
        result += ")";
        return result;
    }

    /**
     * Return a list of field definitions of this table.
     * @return a <code>Vector</code> with the tables fields (FieldDefs)
     */
    public Vector getFields() {
        Vector	results=new Vector();
        FieldDefs node;
        for (Enumeration e=fields.elements();e.hasMoreElements();) {
            node=(FieldDefs)e.nextElement();
            results.addElement(node);
        }
        return results;
    }


    /**
     * Return a list of field names of this table.
     * @return a <code>Vector</code> with the tables field anmes (String)
     */
    public Vector getFieldNames() {
        Vector	results=new Vector();
        FieldDefs node;
        for (Enumeration e=fields.elements();e.hasMoreElements();) {
            node=(FieldDefs)e.nextElement();
            results.addElement(node.getDBName());
        }
        return results;
    }

    /**
     * Return a field's definition
     * @param the requested field's name
     * @return a <code>FieldDefs</code> belonging with the indicated field
     */
    public FieldDefs getField(String fieldName) {
        FieldDefs node=(FieldDefs)fields.get(fieldName);
        return node;
    }


    /**
     * Add a field to this builder.
     * This does not affect the builder config file, nor the table used.
     * @param def the field definiton to add
     */
    public void addField(FieldDefs def) {
        fields.put(def.getDBName(),def);
        sortedEditFields = null;
        sortedListFields = null;
        sortedFields = null;
        sortedDBLayout=new Vector();
        setDBLayout_xml(fields);
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


        sortedEditFields = null;
        sortedListFields = null;
        sortedFields = null;
        sortedDBLayout=new Vector();
        setDBLayout_xml(fields);
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
     * TYPE_UNKNOWN
     * @param the requested field's name
     * @return the field's type.
     */
    public int getDBType(String fieldName) {
        if (fields==null) {
            log.error("getDBType(): fielddefs are null on object : "+tableName);
            return FieldDefs.TYPE_UNKNOWN;
        }
        FieldDefs node=(FieldDefs)fields.get(fieldName);
        if (node==null) {
            // log warning, except for virtual builders
            if (!virtual) log.warn("getDBType(): Can't find fielddef on : "+fieldName+" builder="+tableName);
            return FieldDefs.TYPE_UNKNOWN;
        }
        return node.getDBType();
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
        if (fields==null) return FieldDefs.DBSTATE_PERSISTENT;
        FieldDefs node=(FieldDefs)fields.get(fieldName);
        if (node==null) return FieldDefs.DBSTATE_UNKNOWN;
        return node.getDBState();
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

        if (sortedDBLayout.size()>0) {
            String fname=(String)sortedDBLayout.elementAt(2);
            String str = node.getStringValue( fname );
            if (str.length()>128) {
                return str.substring(0,128)+"...";
            }
            return str;
        } else {
            return GUIIndicator;
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
    public String getGUIIndicator(String field,MMObjectNode node) {
        return null;
    }

    /**
     * Get the field definitions for the editor, sorted according to it's GUISearch property (as set in the builder xml file).
     * Used for creating search-forms.
     * @return a vector with FieldDefs
     */
    public Vector getEditFields() {
        // hack hack
        if (sortedEditFields == null) {
            sortedEditFields=new Vector();
            FieldDefs node;
            for (int i=1;i<20;i++) {
                for (Enumeration e=fields.elements();e.hasMoreElements();) {
                    node=(FieldDefs)e.nextElement();
                    if (node.getGUISearch()==i) sortedEditFields.addElement(node);
                }
            }
        }
        return sortedEditFields;
    }

    /**
     * Get the field definitions for the editor, sorted accoring to it's GUIList property (as set in the builder xml file).
     * Used for creating list-forms (tables).
     * @return a vector with FieldDefs
     */
    public Vector getSortedListFields() {
        // hack hack
        if (sortedListFields == null) {
            sortedListFields = new Vector();
            FieldDefs node;
            for (int i=1;i<20;i++) {
                for (Enumeration e=fields.elements();e.hasMoreElements();) {
                    node=(FieldDefs)e.nextElement();
                    if (node.getGUIList()==i) sortedListFields.addElement(node);
                }
            }
        }
        return (sortedListFields);
    }


    /**
     * Get the field definitions for the editor, sorted according to it's GUIPos property (as set in the builder xml file).
     * Used for creating edit-forms.
     * @return a vector with FieldDefs
     */
    public Vector getSortedFields() {
        // hack hack
        if (sortedFields == null) {
            sortedFields = new Vector();
            FieldDefs node;
            for (int i=1;i<20;i++) {
                for (Enumeration e=fields.elements();e.hasMoreElements();) {
                    node=(FieldDefs)e.nextElement();
                    if (node.getGUIPos()==i) sortedFields.addElement(node);
                }
            }
        }
        return sortedFields;
    }

    /**
     * Returns the next field as defined by its sortorder, according to it's GUIPos property (as set in the builder xml file).
     * Used for moving between fields in an edit-form.
     */
    public FieldDefs getNextField(String currentfield) {
        FieldDefs cdef=getField(currentfield);
        int pos=sortedFields.indexOf(cdef);
        if (pos!=-1  && (pos+1)<sortedFields.size()) {
            return (FieldDefs)sortedFields.elementAt(pos+1);
        }
        return null;
    }

    /**
     * Retrieve the table name (without the clouds' base name)
     * @return a <code>String</code> containing the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Retrieve the full table name (including the clouds' base name)
     * @return a <code>String</code> containing the full table name
     */
    public String getFullTableName() {
        return mmb.baseName+"_"+tableName;
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
    public Object getValue(MMObjectNode node,String field) {
        Object rtn=null;
        int pos2,pos1=field.indexOf('(');
        String name,function,val;
        if (pos1!=-1) {
            pos2=field.lastIndexOf(')');
            if (pos2!=-1) {
                name=field.substring(pos1+1,pos2);
                function=field.substring(0,pos1);
                log.debug("function= "+function+", fieldname ="+name);
                rtn=executeFunction(node,function,name);
            }
        }
        // Old code
        if (field.indexOf("short_")==0) {
            val=node.getStringValue(field.substring(6));
            val=getShort(val,34);
            rtn=val;
        }  else if (field.indexOf("html_")==0) {
            val=node.getStringValue(field.substring(5));
            val=getHTML(val);
            rtn=val;
        } else if (field.indexOf("wap_")==0) {
            val=node.getStringValue(field.substring(4));
            val=getWAP(val);
            rtn=val;
        }
        // end old
        return rtn;
    }

    protected Vector getFunctionParameters(String fields) {
        int commapos=0;
        int nested=0;
        Vector v= new Vector();
        int i;
        log.debug("Fields="+fields);
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
     * @param node the node whos efields are queries
     * @param field the fieldname that is requested
     * @return the result of the 'function', or null if no valid functions could be determined.
     */
    protected Object executeFunction(MMObjectNode node,String function,String field) {
        Object rtn=null;
        // time functions
        if(function.equals("date")) {					// date
            int v=node.getIntValue(field);
            rtn=DateSupport.date2string(v);
        } else if (function.equals("time")) {			// time hh:mm
            int v=node.getIntValue(field);
            rtn=DateSupport.getTime(v);
        } else if (function.equals("timesec")) {		// timesec hh:mm:ss
            int v=node.getIntValue(field);
            rtn=DateSupport.getTimeSec(v);
        } else if (function.equals("longmonth")) {		// longmonth September
            int v=node.getIntValue(field);
            rtn=DateStrings.longmonths[DateSupport.getMonthInt(v)];
        } else if (function.equals("monthnumber")) {
            int v=node.getIntValue(field);
            rtn=""+(DateSupport.getMonthInt(v)+1);
        } else if (function.equals("month")) {			// month Sep
            int v=node.getIntValue(field);
            rtn=DateStrings.Dutch_months[DateSupport.getMonthInt(v)];
        } else if (function.equals("weekday")) {		// weekday Sunday
            int v=node.getIntValue(field);
            rtn=DateStrings.Dutch_longdays[DateSupport.getWeekDayInt(v)];
        } else if (function.equals("shortday")) {		// shortday Sun
            int v=node.getIntValue(field);
            rtn=DateStrings.Dutch_days[DateSupport.getWeekDayInt(v)];
        } else if (function.equals("day")) {			// day 4
            int v=node.getIntValue(field);
            rtn=""+DateSupport.getDayInt(v);
        } else if (function.equals("shortyear")) {			// year 01
            int v=node.getIntValue(field);
            rtn=(DateSupport.getYear(v)).substring(2);
        } else if (function.equals("year")) {			// year 2001
            int v=node.getIntValue(field);
            rtn=DateSupport.getYear(v);
        } else if (function.equals("thisdaycurtime")) {			//
            int curtime=node.getIntValue(field);
            // gives us the next full day based on time (00:00)
            int days=curtime/(3600*24);
                rtn=""+((days*(3600*24))-3600);
            // text convertion  functions
        }

                // functions that do not require a field
                // These are more or like pseudo fields, like the age of a node.
                // node.getAge("age()"); will work.

                else if (function.equals("age")) {
                        Integer val = new Integer(node.getAge());
                        rtn = val.toString();
                }
                // text convertion  functions
        else if (function.equals("wap")) {
            String val=node.getStringValue(field);
            rtn=getWAP(val);
        } else if (function.equals("html")) {
            String val=node.getStringValue(field);
            rtn=getHTML(val);
        } else if (function.equals("shorted")) {
            String val=node.getStringValue(field);
            rtn=getShort(val,32);
        } else if (function.equals("uppercase")) {
            String val=node.getStringValue(field);
            rtn=val.toUpperCase();
        } else if (function.equals("lowercase")) {
            String val=node.getStringValue(field);
            rtn=val.toLowerCase();
        } else if (function.equals("hostname")) {
            String val=node.getStringValue(field);
            rtn=hostname_function(val);
        } else if (function.equals("urlencode")) {
            String val=node.getStringValue(field);
            rtn=getURLEncode(val);
        } else if (function.startsWith("wrap_")) {
            String val=node.getStringValue(field);
            try {
                int wrappos=Integer.parseInt(function.substring(5));
                rtn=wrap(val,wrappos);
            } catch(Exception e) {}
        } else if (function.equals("wrap")) {
            Vector v=getFunctionParameters(field);
            try {
                String val=node.getStringValue((String)v.get(0));
                int wrappos=Integer.parseInt((String)v.get(1));
                rtn=wrap(val,wrappos);
            } catch(Exception e) {}
        } else if (function.equals("substring")) {
            Vector v=getFunctionParameters(field);
            try {
                String val=node.getStringValue((String)v.get(0));
                int len=Integer.parseInt((String)v.get(1));
                if (v.size()>2) {
                        String filler=(String)v.get(2);
                        rtn=substring(val,len,filler);
                } else {
                        rtn=substring(val,len,null);
                }
            } catch(Exception e) {}
        } else if (function.equals("currency_euro")) {
             double val=node.getDoubleValue(field);
                         NumberFormat nf = NumberFormat.getNumberInstance (Locale.GERMANY);
                         rtn=""+nf.format(val);
        } else if (function.equals("gui")) {
            String val=null;
            if (field.equals("")) {
                val=getGUIIndicator(node);
            } else {
                val=getGUIIndicator(field,node);
            }
            if (val==null) {
                val=node.getStringValue(field);
            }
            rtn=val;
        } else if (function.equals("smartpath")) {
            Vector v=getFunctionParameters(field);
            try {
                String documentRoot = (String)v.get(0);
                String path = (String)v.get(1);
                String version = (String)v.get(2);
                if (version != null) {
                    if (version.equals("")) {
                        version = null;
                    }
                }
                rtn = getSmartPath(documentRoot, path, "" + node.getNumber(), version);
            } catch(Exception e) {
                log.error("Evaluating smartpath for "+node.getNumber()+" went wrong");
            }
        } else {
            log.warn("Builder ("+tableName+") unknown function '"+function+"'");
        }

        return rtn;
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
        if (bul==null) {
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
            return null;
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
     * set text array in database
     */
    /*
    public void setDBText(int i, PreparedStatement stmt,String body) {
        byte[] isochars=null;
        try {
                isochars=body.getBytes("ISO-8859-1");
        } catch (Exception e) {
                log.debug("setDBText(): String contains odd chars");
                log.error(Logging.stackTrace(e));
        }
        try {
                ByteArrayInputStream stream=new ByteArrayInputStream(isochars);
                stmt.setAsciiStream(i,stream,isochars.length);
                stream.close();
        } catch (Exception e) {
                log.error("setDBText(): Can't set ascii stream");
                log.error(Logging.stackTrace(e));
        }
}
    */


    /**
    * set byte array in database
    */
    /*
    public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
        try {
                ByteArrayInputStream stream=new ByteArrayInputStream(bytes);
                stmt.setBinaryStream(i,stream,bytes.length);
                stream.close();
        } catch (Exception e) {
                log.error("setDBByte(): Can't set byte stream");
                log.error(Logging.stackTrace(e));
        }
}
    */

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
            o.nodeRemoteChanged(machine,number,builder,ctype);
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
        for (Enumeration e=localObservers.elements();e.hasMoreElements();) {
            MMBaseObserver o=(MMBaseObserver)e.nextElement();
            o.nodeLocalChanged(machine,number,builder,ctype);
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
        throw new ParseException(classname +" should override the getList method (you've probably made a typo)");
    }


    /**
     * Obtains a string value by performing the provided command.
     * This method is SCAN related and may fail if called outside the context of the SCAN servlet.
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
     * Converts an MMNODE expression to SQL.
     * MMNODE expressions are resolved by the database support classes.
     * This means that some database-specific expressions can easier be converted.
     * @param where the MMNODE expression
     * @return the SQL clause as a <code>String</code>
     */
    public String convertMMNode2SQL(String where) {
        log.debug("convertMMNode2SQL(): "+where);
        String result="WHERE "+database.getMMNodeSearch2SQL(where,this);
        log.debug("convertMMNode2SQL(): results : "+result);
        return result;
    }

    /**
     * Set the MMBase object.
     * @param m the MMBase object to set as owner of this builder
     */
    public void setMMBase(MMBase m) {
        this.mmb=m;
    }

    /**
     * Stores the fieldnames of a table in a vector, based on the current fields definition.
     * The fields 'otype' and 'owner' become the first and second fieldnames.
     * @param vec A vector with builder-creation commands, in the form 'dbname,guiname,guitype,guipos,guilist,guisearch,dbstate,dbname'
     */
    public void setDBLayout(Vector vec) {
        sortedDBLayout=new Vector();
        sortedDBLayout.addElement("otype");
        sortedDBLayout.addElement("owner");
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            StringTokenizer tok = new StringTokenizer((String)e.nextElement(),",\n\r");
            if(tok.hasMoreTokens()) {
                String dbtype=tok.nextToken();
                if(tok.hasMoreTokens()) {
                    String guiname=tok.nextToken();
                    if(tok.hasMoreTokens()) {
                        String guitype=tok.nextToken();
                        if(tok.hasMoreTokens()) {
                            String guipos=tok.nextToken();
                            if(tok.hasMoreTokens()) {
                                String guilist=tok.nextToken();
                                if(tok.hasMoreTokens()) {
                                    String guisearch=tok.nextToken();
                                    if(tok.hasMoreTokens()) {
                                        String dbstate=tok.nextToken();
                                        if(tok.hasMoreTokens()) {
                                            String dbname=tok.nextToken();
                                            if (!dbname.equals("number") && !dbname.equals("owner")) {
                                                sortedDBLayout.addElement(dbname);
                                            }
                                        } else
                                            log.error("setDBLayout(): 'dbname' not defined (while reading defines?)");
                                    } else
                                        log.error("setDBLayout(): 'dbstate' not defined (while reading defines?)");
                                } else
                                    log.error("setDBLayout(): 'guisearch' not defined (while reading defines?)");
                            } else
                                log.error("setDBLayout(): 'guilist' not defined (while reading defines?)");
                        } else
                            log.error("setDBLayout(): 'guipos' not defined (while reading defines?)");
                    } else
                        log.error("setDBLayout(): 'guitype' not defined (while reading defines?)");
                } else
                    log.error("setDBLayout(): 'guiname' not defined (while reading defines?)");
            } else
                log.error("setDBLayout(): 'dbname' not defined (while reading defines?)");
        }
    }


    /**
     * Stores the fieldnames of a table in a vector, based on the current fields definition.
     * The fields 'otype' and 'owner' become the first and second fieldnames.
     * @param fields A list of the builder's FieldDefs
     */
    public void setDBLayout_xml(Hashtable fields) {
        sortedDBLayout=new Vector();
        sortedDBLayout.addElement("otype");
        sortedDBLayout.addElement("owner");

        FieldDefs node;
        for (int i=1;i<20;i++) {
            for (Enumeration e=fields.elements();e.hasMoreElements();) {
                node=(FieldDefs)e.nextElement();
                if (node.getDBPos()==i) {
                    String name=node.getDBName();
                    if (name!=null && !name.equals("number") && !name.equals("otype") && !name.equals("owner")) {
                        sortedDBLayout.addElement(name);
                    }
                }
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
     * Get descriptions of the builder
     * @return  a <code>Hashtable</code> containing the descriptions
     */
    public Hashtable getDescriptions() {
        return descriptions;
    }

    /**
     * Sets Dutch Short name.
     * @see #getDutchSName
     * @deprecated Will be removed soon
     */
    public void setDutchSName(String d) {
        this.dutchSName=d;
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
     * Gets Dutch Short name.
     * Actually returns the builders short name in wither the 'current langauge', or default langauge 'us', whichever is available.
     * If this fails, the value set with {@link #setDutchSName} is used instead.
     * @returns the 'dutch' short name
     * @deprecated use {@link #getSingularName} instead.
     */
    public String getDutchSName() {
        if (singularNames!=null) {
            String tmp=(String)singularNames.get(mmb.getLanguage());
            if (tmp==null) {
               tmp=(String)singularNames.get("us");
            }
            return tmp;
        }
        return dutchSName;
    }

    /**
     * Gets short name of the builder, using the specified language.
     * @param lang The language requested
     * @returns the short name in that language, or <code>null</code> if it is not avaialble
     */
    public String getSingularName(String lang) {
        if (singularNames==null) return null;
        return (String)singularNames.get(lang);
    }

    /**
     * Gets short name of the builder in the current default language.
     * If the current language is not available, the "us" version is returned instead.
     * @returns the short name in either the default language or in "us"
     */
    public String getSingularName() {
        if (singularNames==null) return null;
        String tmp=getSingularName(mmb.getLanguage());
        if (tmp==null) tmp=getSingularName("us");
        return tmp;
    }

    /**
     * Gets long name of the builder, using the specified language.
     * @param lang The language requested
     * @returns the long name in that language, or <code>null</code> if it is not avaialble
     */
    public String getPluralName(String lang) {
        if (pluralNames==null) return null;
        return (String)pluralNames.get(lang);
    }

    /**
     * Gets long name of the builder in the current default language.
     * If the current language is not available, the "us" version is returned instead.
     * @returns the long name in either the default language or in "us"
     */
    public String getPluralName() {
        if (pluralNames==null)  return null;
        String tmp=getPluralName(mmb.getLanguage());
        if (tmp==null) tmp=getPluralName("us");
        return tmp;
    }

    /**
     * Set classname of the builder
     */
    public void setClassName(String d) {
        this.className=d;
    }

    /**
     * Returns the classname of this builder
     */
    public String getClassName() {
        return className;
    }

    /**
     * Send a signal to other servers that a field was changed.
     * @param node the node the field was changed in
     * @param fieldname the name of the field that was changed
     * @return always <code>true</code>
     */
    public boolean	sendFieldChangeSignal(MMObjectNode node,String fieldname) {
        // we need to find out what the DBState is of this field so we know
        // who to notify of this change
        int state=getDBState(fieldname);
        log.debug("Changed field="+fieldname+" dbstate="+state);

        // still a large hack need to figure out remote changes
        if (state==0) {}
        // convert the field to a string

        int type=getDBType(fieldname);
        String value="";
        if (type==FieldDefs.TYPE_INTEGER) {
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
     */
    public String toXML(MMObjectNode node) {
        String body="<?xml version=\""+version+"\"?>\n";
        body+="<!DOCTYPE mmnode."+tableName+" SYSTEM \""+mmb.getDTDBase()+"/mmnode/"+tableName+".dtd\">\n";
        body+="<"+tableName+">\n";
        body+="<number>"+node.getNumber()+"</number>\n";
        for (Enumeration e=sortedDBLayout.elements();e.hasMoreElements();) {
            String key=(String)e.nextElement();
            int type=node.getDBType(key);
            if (type==FieldDefs.TYPE_INTEGER) {
                body+="<"+key+">"+node.getIntValue(key)+"</"+key+">\n";
            } else if (type==FieldDefs.TYPE_STRING) {
                body+="<"+key+">"+node.getStringValue(key)+"</"+key+">\n";
            } else if (type==FieldDefs.TYPE_BYTE) {
                body+="<"+key+">"+node.getByteValue(key)+"</"+key+">\n";
            } else {
                body+="<"+key+">"+node.getStringValue(key)+"</"+key+">\n";
            }
        }
        body+="</"+tableName+">\n";
        return body;
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
     * Get text from a blob field. the text is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return a <code>String</code> containing the contents of a field as text
     */
    public String getShortedText(String fieldname,int number) {
        return database.getShortedText(tableName,fieldname,number);
    }

    /**
     * Get binary data of a database blob field. the data is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return an array of <code>byte</code> containing the contents of a field as text
     */
    public byte[] getShortedByte(String fieldname,int number) {
        return database.getShortedByte(tableName,fieldname,number);
    }

    /**
     * Get binary data of a database blob field.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return an array of <code>byte</code> containing the contents of a field as text
     */
    public byte[] getDBByte(ResultSet rs,int idx) {
        return database.getDBByte(rs,idx);
    }

    /**
     * Get text from a blob field.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return a <code>String</code> containing the contents of a field as text
     */
    public String getDBText(ResultSet rs,int idx) {
        return database.getDBText(rs,idx);
    }


    /**
     * Maintains statistics. Doesn't work. Needs the statistics builder.
     * @param type the name of the type of action to keep the stats for.
     */
    private void statCount(String type) {
        if (1==1) return; // problems with shadow nodes

        if (statbul==null) statbul=(Statistics)mmb.getMMObject("statistics");
        if (statbul!=null) {
            if (statbul!=this && mmb.getMMObject("sshadow")!=this) {
                String name=mmb.getMachineName()+"_"+type;
                String nr=statbul.getAliasNumber(name);
                if (nr!=null) {
                    statbul.setCount(nr,1);
                } else {
                    MMObjectNode node=statbul.getNewNode("system");
                    node.setValue("name",name);
                    node.setValue("description","");
                    node.setValue("count",1);
                    node.setValue("timeslices",144);
                    node.setValue("timeinterval",600);
                    node.setValue("timesync",0);
                    node.setValue("data","");
                    node.setValue("start",0);
                    node.setValue("timeslice",0);
                    statbul.insert("system",node);
                }
            }
        }
    }

    /**
     * Tests whether a builder table is created.
     * XXX Should be moved to MMTable.
     * @return <code>true</code> if the table exists, <code>false</code> otherwise
     */
    public boolean created() {
        if (database!=null) {
            return database.created(getFullTableName());
        } else {
            return super.created();
        }
    }

    /**
     * Returns the number of the node with the specified name.
     * Tests whether a builder table is created.
      * Should be moved to MMTable.
     * @return <code>true</code> if the table exists, <code>false</code> otherwise
     */
    public String getNumberFromName(String name) {
        String number = null;

        //String number=(String)nameCache.get(name);
        //if (number!=null) {
        //	return number;
        //} else {
        Enumeration e=search("name=='"+name+"'");
        if (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            number=""+node.getNumber();
            //nameCache.put(name,number);
        }
        //}

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
     *	<code>false</code> if original value was set back into the field.
     */
    public boolean setValue(MMObjectNode node,String fieldname, Object originalValue) {
        return setValue(node,fieldname);
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
     * Provides a way to simulate xml files as configuration.
     * @deprecated will be removed
     */
    public Hashtable getXMLSetup() {
        return null;
    }

    // Default replacements for method getHTML()
    private final static String DEFAULT_ALINEA = "<br />&nbsp;<br />";
    private final static String DEFAULT_EOL = "<br />";
    
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
            obj.replace("<","&lt;");
            obj.replace(">","&gt;");
            obj.replace("$","&#36;");

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
                // log.debug("Returning URLEncoded string: "+rtn);
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
     *		This data is retrieved from an outside source (such as an xml file), and thus
     *		may be incorrect.
     */
    public void setXMLValues(Vector xmlfields) {
        //sortedEditFields = null;
        //sortedListFields = null;
        //sortedFields = null;
        //sortedDBLayout=new Vector();

        fields=new Hashtable();

        Enumeration enum = xmlfields.elements();
        while (enum.hasMoreElements()) {
            FieldDefs def=(FieldDefs)enum.nextElement();
            String name=(String)def.getDBName();
            def.setParent(this);
            fields.put(name,def);
        }

        FieldDefs def=new FieldDefs("Type","integer",-1,-1,"otype",FieldDefs.TYPE_INTEGER,-1,3);
        def.setParent(this);
        fields.put("otype",def);
        setDBLayout_xml(fields);
    }

    /**
     * Sets whether configuration is based on xml files.
     * @deprecated will be removed
     */
    public void setXmlConfig(boolean state) {
        isXmlConfig=state;
    }

    /**
     * Retrieves whether configuration is based on xml files.
     * @deprecated will be removed
     */
    public boolean isXMLConfig() {
        return isXmlConfig;
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
     * Set all builder properties
     * Changed properties will not be saved.
     * @param properties the properties to set
     */
    void setInitParameters(Hashtable properties) {
        this.properties=properties;
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
        if (properties==null) properties=new Hashtable();
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
     * Debugging routine,sends message to log
     * @param msg the message to log
     * @deprecated, use new logging system instead
     */
    protected void debug( String msg )
    {
        log.debug( classname +":"+ msg );
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
     * hostname, parses the hostname from a url, so http://www.somesite.com/index.html
     * becomed www.somesite.com
     */
    public String hostname_function(String url) {
        if (url.startsWith("http://")) {
                url=url.substring(7);
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
}
