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
 * @version 13 november 2000
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
    * If true, debug messages are send to the MMBase log
    */
    public boolean debug=false;

    /**
    * Sets debugging on or off
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
    * @see readSearchResults
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
    private Hashtable fields;

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
    * @see create
    */
    public boolean init() {
        database=mmb.getDatabase();

        if (!created()) {
            if (debug) debug("init(): Create "+tableName);
            create();
        }
        if (!tableName.equals("object") && mmb.TypeDef!=null) {
            oType=mmb.TypeDef.getIntValue(tableName);
            if (oType==-1) {
                //mmb.TypeDef.insert("system",tableName,description);
                MMObjectNode node=mmb.TypeDef.getNewNode("system");
                node.setValue("otype",1);
                node.setValue("name",tableName);
                if (description==null) description="not defined in this langauge";
                node.setValue("description",description);
                node.insert("system");
                oType=mmb.TypeDef.getIntValue(tableName);
            }
        } else {
            if(!tableName.equals("typedef")) {
                debug("init(): for tablename("+tableName+") -> can't get to typeDef");
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

    /*
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
            if (n>=0) nodeCache.put(new Integer(n),node);
	    String alias=node.getAlias();
 	    if (alias!=null) createAlias(n,alias);	// add alias, if provided
            return n;
        } catch(Exception e) {
            debug("ERROR INSERT PROBLEM !");
            debug("Error node="+node);
            e.printStackTrace();
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
	if (mmb.OAlias!=null) {
		MMObjectNode node=mmb.OAlias.getNewNode("system");
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
                e.printStackTrace();
            }
        }
        return;
    }

    /**
    * Get a new node, using this builder as its parent. The new node is not a part of the cloud yet, and thus has
    * the value -1 as a number. (Call {@link @insert} to add the node to the cloud).
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
    public void setDefaults(MMObjectNode node) {}

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
        				if( number == node.getIntValue("number")) {
        					nameCache.remove( name );	
        				} 
        			} catch( NumberFormatException e ) {
        				debug("removeNode("+node+"): ERROR: snumber("+sNumber+") from nameCache not valid number!");
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
        int number=node.getIntValue("number");
        if (number!=-1) {
            removeRelations(number);
        }
    }

    /**
    * Remove the relations of a node
    * @param number The number of the node whose relations to remove.
    */
    public void removeRelations(int number) {
        Vector relvector=getRelations_main(number);
        for (Iterator rels=relvector.iterator(); rels.hasNext(); ) {
                // get the relation node
                MMObjectNode node=(MMObjectNode)rels.next();
                // determine the true builder for this node
                // (node.parent is always InsRel, but otype
                //  indicates any derived builders, such as AuthRel)
                MMObjectBuilder bul = mmb.getMMObject(mmb.getTypeDef().getValue(node.getIntValue("otype")));
                // remove the node using this builder
                // circumvent problem in database layers
                bul.removeNode(node);
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
    * Retrieves an object's type. If necessary, the type is added to the cache.
    * @param number The number of the node to search for
    * @return an <code>int</code> value which is the object type (otype) of the node.
    */
    public int getNodeType(int number)
    {
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
            e.printStackTrace();
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
    * @return <code>null</code> if the node does not exist or the key is invalid, or a
    *       <code>MMObjectNode</code> containing the contents of the requested node.
    */
    public MMObjectNode getNode(String key) {
        int nr;
        MMObjectNode node = null;

        if( key == null ) {
            debug("getNode(null): ERROR: for tablename("+tableName+"): key is null!");
            return null;
        }

        try {
            nr=Integer.parseInt(key);
        } catch (Exception e) {
            nr=-1;
        }
        // load the node directy if the number is right
        if (nr>0) {
            node=mmb.getTypeDef().getNode(nr);
        } else {
            //otherwise try to see if it can be retrieved by alias name
            if (mmb.OAlias!=null) {
            	node=mmb.OAlias.getAliasedNode(key);
            }
        }
        return node;
    }

    /**
    * Retrieves a node based on it's number (a unique key).
    * @param number The numbe rof the node to search for
    * @return <code>null</code> if the node does not exist or the key is invalid, or a
    *       <code>MMObjectNode</code> containign the contents of the requested node.
    */
    public synchronized MMObjectNode getNode(int number) {

        // test with counting
        statCount("getnode");
        if (number==-1) {
            debug(" ("+tableName+") nodenumber == -1");
            return null;
        }

        // cache setup
        MultiConnection con;
        Integer integerNumber=new Integer(number);
        MMObjectNode node=(MMObjectNode)nodeCache.get(integerNumber);
        if (node!=null) {
            // lets add a extra asked counter to make a smart cache
            int c=node.getIntValue("CacheCount");
            c++;
            node.setValue("CacheCount",c);
            return node;
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
                debug("getNode(): got a null type table ("+bi+") on node ="+number+", possible non table query blocked !!!");
                return null;
            }

            con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+bul+" WHERE "+mmb.getDatabase().getNumberString()+"="+number);
            if (rs.next()) {
                // create a new object and add it to the result vector
                MMObjectBuilder bu=mmb.getMMObject(bul);
                if (bu==null) debug("getMMObject did not return builder on : "+bul);
                node=new MMObjectNode(bu);
                ResultSetMetaData rd=rs.getMetaData();
                String fieldname;
                String fieldtype;
                for (int i=1;i<=rd.getColumnCount();i++) {
                    fieldname=rd.getColumnName(i);
                    node=database.decodeDBnodeField(node,fieldname,rs,i);
                }
                nodeCache.put(integerNumber,node);
                stmt.close();
                con.close();
                // clear the changed signal
                node.clearChanged();
            } else {
                stmt.close();
                con.close();
                debug("getNode(): Node not found "+number);
                node=null; // not found
            }

            // return the results

            return node;
        } catch (SQLException e) {
            // something went wrong print it to the logs
            e.printStackTrace();
            return null;
        }
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
    * Get nodes from the temporary node space
    * @param key  The (temporary) key to use under which the node is stored
    */
	public MMObjectNode getTmpNode(String key) {
		MMObjectNode node=null;
		node=(MMObjectNode)TemporaryNodes.get(key);
		if (node==null) {
			if (debug) debug("getTmpNode(): node not found "+key);
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
		if (node==null) debug("removeTmpNode): node with "+key+" didn't exists");
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
        if (in==null || in.equals("")) return(new Vector());
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
        //System.out.println(query);
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
            debug("basicSearch(): ERROR in search "+query);
            try {
                if (stmt!=null) stmt.close();
            } catch(Exception g) {}

            try {
                if (con!=null) con.close();
            } catch(Exception g) {}
            //e.printStackTrace();
        }

        return (new Vector()); // Return an empty Vector
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
            e.printStackTrace();
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
        return(searchVector(where,sort).elements());
    }


    /**
    * Enumerate all the objects that match the searchkeys
    * @param where where clause that the objects need to fulfill
    * @param sorted order in which to return the objects
    * @param in lost of node numbers to filter on
    * @return an <code>Enumeration</code> containing all the objects that apply.
    */
    public Enumeration searchIn(String where,String sort,String in) {
        return(searchVectorIn(where,sort,in).elements());
    }


    /**
    * Enumerate all the objects that match the searchkeys
    * @param where where clause that the objects need to fulfill
    * @param in lost of node numbers to filter on
    * @return an <code>Enumeration</code> containing all the objects that apply.
    */
    public Enumeration searchIn(String where,String in) {
        return(searchVectorIn(where,in).elements());
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
        return(searchVector(where,sort,direction).elements());
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
        return(searchVectorIn(where,sort,direction,in).elements());
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
        return(basicSearch(query));
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
        if (in!=null && in.equals("")) return(new Vector());
        String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted;
        return(basicSearch(query));
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
        if (in==null || in.equals("")) return(new Vector());
        String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+")";
        return(basicSearch(query));
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
            return(basicSearch(query));
        } else {
            String query="SELECT * FROM "+getFullTableName()+" "+where+" ORDER BY "+sorted+" DESC";
            return(basicSearch(query));
        }
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
        if (in==null || in.equals("")) return(new Vector());
        if (direction) {
            String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted+" ASC";
            return(basicSearch(query));
        } else {
            String query="SELECT * FROM "+getFullTableName()+" "+QueryConvertor.altaVista2SQL(where,database)+" AND "+mmb.getDatabase().getNumberString()+" in ("+in+") ORDER BY "+sorted+" DESC";
            return(basicSearch(query));
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

                // huge trick to fill the caches does it make sense ?
                number=new Integer(node.getIntValue("number"));
                if (!nodeCache.containsKey(number) || replaceCache) {
                    nodeCache.put(number,node);
                } else {
                    node=(MMObjectNode)nodeCache.get(number);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
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

            return (sv);
        } catch (SQLException e) {
            // something went wrong print it to the logs
            e.printStackTrace();
        }
        return (null);
    }


    /**
    * Build a set command string from a set nodes ( should be moved )
    * @parame nodes Vector containg teh nodes to put in the set
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
        return (result);
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
        fields.remove(fieldname);
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
        if (fields==null) debug("getDBType(): fielddefs are null on object : "+tableName);
        FieldDefs node=(FieldDefs)fields.get(fieldName);
        if (node==null) {
            if (debug) debug("getDBType(): PROBLEM Can't find fielddef on : "+fieldName+" builder="+tableName);
            return -1;
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
        if (fields==null) return 2;
        FieldDefs node=(FieldDefs)fields.get(fieldName);
        if (node==null) return -1;
        return(node.getDBState());
    }

    /**
    * What should a GUI display for this node.
    * Default is the first non system field (first field after owner).
    * Override this to display your own choice (see Images.java).
    */
    public String getGUIIndicator(MMObjectNode node) {

        // do the best we can because this method was not implemeted
        // we get the first field in the object and try to make it
        // to a string we can return

        if (sortedDBLayout.size()>0) {
            String fname=(String)sortedDBLayout.elementAt(2);
            String str = node.getValueAsString( fname );
            if (str.length()>128) {
                return(str.substring(0,128)+"...");
            }
            return str;
        } else {
            return GUIIndicator;
        }
    }

    /**
    * What should a GUI display for this node/field combo.
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
                    if (node.GUISearch==i) sortedEditFields.addElement(node);
                }
            }
        }
        return (sortedEditFields);
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
                    if (node.GUIList==i) sortedListFields.addElement(node);
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
                    if (node.GUIPos==i) sortedFields.addElement(node);
                }
            }
        }
        return (sortedFields);
    }

    /**
    * Returns the next field as defined by its sortorder, according to it's GUIPos property (as set in the builder xml file).
    * Used for moving between fields in an edit-form.
    */
    public FieldDefs getNextField(String currentfield) {
        FieldDefs cdef=getField(currentfield);
        int pos=sortedFields.indexOf(cdef);
        if (pos!=-1  && (pos+1)<sortedFields.size()) {
            return((FieldDefs)sortedFields.elementAt(pos+1));
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
        return(mmb.baseName+"_"+tableName);
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
        //		if (debug) debug("getValue() "+node+" --- "+field);
        Object rtn=null;
        int pos2,pos1=field.indexOf('(');
        String name,function,val;
        if (pos1!=-1) {
            pos2=field.indexOf(')');
            if (pos2!=-1) {
                name=field.substring(pos1+1,pos2);
                function=field.substring(0,pos1);
                debug("function= "+function+", fieldname ="+name);
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

    /**
    * Executes a function on the field of a node, and returns the result.
    * This method is called by the builder's {@link #getValue} method.
    * Derived builders should override this method to provide additional functions.
    * @param node the node whos efields are queries
    * @param field the fieldname that is requested
    * @return the result of the 'function', or null if no valid functions could be determined.
    */
    protected Object executeFunction(MMObjectNode node,String function,String field) {
        Object rtn=null;

        //		System.out.println("Builder ("+tableName+") execute "+function+" on "+field);

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
        } else if (function.equals("month")) {			// month Sep
            int v=node.getIntValue(field);
            rtn=DateStrings.months[DateSupport.getMonthInt(v)];
        } else if (function.equals("weekday")) {		// weekday Sunday
            int v=node.getIntValue(field);
            rtn=DateStrings.longdays[DateSupport.getWeekDayInt(v)];
        } else if (function.equals("shortday")) {		// shortday Sun
            int v=node.getIntValue(field);
            rtn=DateStrings.days[DateSupport.getWeekDayInt(v)];
        } else if (function.equals("day")) {			// day 4
            int v=node.getIntValue(field);
            rtn=""+DateSupport.getDayInt(v);
        } else if (function.equals("year")) {			// year 2001
            int v=node.getIntValue(field);
            rtn=DateSupport.getYear(v);

            // text convertion  functions
        }
        else if (function.equals("wap")) {
            String val=node.getStringValue(field);
            rtn=getWAP(val);
        } else if (function.equals("html")) {
            String val=node.getStringValue(field);
            rtn=getHTML(val);
        } else if (function.equals("shorted")) {
            String val=node.getStringValue(field);
            rtn=getShort(val,32);

        } else {
            System.out.println("Builder ("+tableName+") unknown function '"+function+"'");
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
        InsRel bul=(InsRel)mmb.getMMObject("insrel");
        if (bul==null) debug("getMMObject(): InsRel not yet loaded");
        return(bul.getRelationsVector(src));
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
	* @deprecated This is a utility method for use with a number of SCAN commands. Since it does not actually require knowledge of the nodes,
	* this method should be moved to the {@link #org.mmbase.module.gui.html.scanparser} class.
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
        int i=mmb.TypeDef.getIntValue(type);
        int j=0;
        for (Enumeration e=nodeCache.elements();e.hasMoreElements();) {
            MMObjectNode n=(MMObjectNode)e.nextElement();
            int c=n.getIntValue("CacheCount");
            if (n.getIntValue("otype")==i && c!=-1) j++;
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
            int c=n.getIntValue("CacheCount");
            if (c!=-1) {
                if (!results.equals("")) {
                    results+=","+n.getIntValue("number");
                } else {
                    results+=n.getIntValue("number");
                }
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
    		debug("setDBText(): String contains odd chars");
    		e.printStackTrace();
    	}
    	try {
    		ByteArrayInputStream stream=new ByteArrayInputStream(isochars);
    		stmt.setAsciiStream(i,stream,isochars.length);
    		stream.close();
    	} catch (Exception e) {
    		debug("setDBText(): Can't set ascii stream");
    		e.printStackTrace();
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
    		debug("setDBByte(): Can't set byte stream");
    		e.printStackTrace();
    	}
}
    */

    /**
    * Return the age of the node, determined using the daymarks builder.
    * @param node The node whose age to determine
    * @return the age in days, or 0 if unknown (daymarks builder not present)
    */
    public int getAge(MMObjectNode node) {
        return(((DayMarkers)mmb.getMMObject("daymarks")).getAge(node));
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
    * @param number Number of the changed node as a <code>String</code>
    + @param builder type of the changed node
    + @param ctype command type, not very well documented
    * @return always <code>true</code>
    */
    public boolean nodeRemoteChanged(String number,String builder,String ctype) {
        // overal cache control, this makes sure that the caches
        // provided by mmbase itself (on nodes and relations)
        // are kept in sync is other servers add/change/delete them.
        // System.out.println("MMObjectBuilder -> CHECK REMOTE remove from cache node="+tableName+" nr="+number);
        if (ctype.equals("c") || ctype.equals("d")) {
            try {
                Integer i=new Integer(number);
                if (nodeCache.containsKey(i)) {
                    nodeCache.remove(i);
                }
            } catch (Exception e) {
                debug("nodeRemoteChanged(): Not a number");
            }
        } else if (ctype.equals("r")) {
            try {
                Integer i=new Integer(number);
                MMObjectNode node=(MMObjectNode)nodeCache.get(i);
                if (node!=null) {
                    node.delRelationsCache();
                }
            } catch (Exception e) {}

        }

        // signal all the other objects that have shown interest in changes of nodes of this builder type.
        // System.out.println("DEBUG OBSERVERS="+remoteObservers.size());
        for (Enumeration e=remoteObservers.elements();e.hasMoreElements();) {
            MMBaseObserver o=(MMBaseObserver)e.nextElement();
            o.nodeRemoteChanged(number,builder,ctype);
        }
        return true;
    }

    /**
    * Called when a local node is changed.
    * Should be called by subclasses if they override it.
    * @param number Number of the changed node as a <code>String</code>
    + @param builder type of the changed node
    + @param ctype command type, not very well documented
    * @return always <code>true</code>
    */
    public boolean nodeLocalChanged(String number,String builder,String ctype) {
        // overal cache control, this makes sure that the caches
        // provided by mmbase itself (on nodes and relations)
        // are kept in sync is other servers add/change/delete them.
        // System.out.println("MMObjectBuilder -> CHECK LOCAL remove from cache node="+tableName+" nr="+number);
        if (ctype.equals("r")) {
            try {
                Integer i=new Integer(number);
                MMObjectNode node=(MMObjectNode)nodeCache.get(i);
                if (node!=null) {
                    node.delRelationsCache();
                }
            } catch (Exception e) {}

        }
        // signal all the other objects that have shown interest in changes of nodes of this builder type.
        for (Enumeration e=localObservers.elements();e.hasMoreElements();) {
            MMBaseObserver o=(MMBaseObserver)e.nextElement();
            o.nodeLocalChanged(number,builder,ctype);
        }
        return true;
    }


    /**
    * Called when a local field is changed.
    * @param number Number of the changed node as a <code>String</code>
    + @param builder type of the changed node
    + @param field name of the changed field
    + @param value value it changed to
    * @return always <code>true</code>
    */
    public boolean fieldLocalChanged(String number,String builder,String field,String value) {
        debug("FLC="+number+" BUL="+builder+" FIELD="+field+" value="+value);
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
        debug("getDefaultTeaser(): Generate Teaser,Should be overridden");
        return tnode;
    }

    /**
    * Waits until a node is changed (multicast).
    * @param node the node to wait for
    */
    public boolean waitUntilNodeChanged(MMObjectNode node) {
        return(mmb.mmc.waitUntilNodeChanged(node));
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
        debug("replace(): replace called should be overridden");
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
        if (debug) debug("convertMMNode2SQL(): "+where);
        String result="WHERE "+database.getMMNodeSearch2SQL(where,this);
        if (debug) debug("convertMMNode2SQL(): results : "+result);
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
                                            debug("setDBLayout(): ERROR: 'dbname' not defined (while reading defines?)");
                                    } else
                                        debug("setDBLayout(): ERROR: 'dbstate' not defined (while reading defines?)");
                                } else
                                    debug("setDBLayout(): ERROR: 'guisearch' not defined (while reading defines?)");
                            } else
                                debug("setDBLayout(): ERROR: 'guilist' not defined (while reading defines?)");
                        } else
                            debug("setDBLayout(): ERROR: 'guipos' not defined (while reading defines?)");
                    } else
                        debug("setDBLayout(): ERROR: 'guitype' not defined (while reading defines?)");
                } else
                    debug("setDBLayout(): ERROR: 'guiname' not defined (while reading defines?)");
            } else
                debug("setDBLayout(): ERROR: 'dbname' not defined (while reading defines?)");
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
                if (node.DBPos==i) {
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
    * If this fails, teh value set with {@link #SetDutchSName} is used instead.
    * @returns the 'dutch' short name
    * @deprecated Will be removed soon
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
    * @param fieldname the anme of teh field that was changed
    * @return always <code>true</code>
    */
    public boolean	sendFieldChangeSignal(MMObjectNode node,String fieldname) {
        // we need to find out what the DBState is of this field so we know
        // who to notify of this change
        int state=getDBState(fieldname);
        debug("Changed field="+fieldname+" dbstate="+state);

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

        fieldLocalChanged(""+node.getIntValue("number"),tableName,fieldname,value);
        //mmb.mmc.changedNode(node.getIntValue("number"),tableName,"f");
        return true;
    }

    /**
    * Send a signal to other servers that a new node was created.
    * @param tableName the table in which a node was edited (?)
    * @param number teh number of the new node
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
    * @param node teh node to convert
    * @return the XML <code>String</code>
    */
    public String toXML(MMObjectNode node) {
        String body="<?xml version=\""+version+"\"?>\n";
        body+="<!DOCTYPE mmnode."+tableName+" SYSTEM \""+mmb.getDTDBase()+"/mmnode/"+tableName+".dtd\">\n";
        body+="<"+tableName+">\n";
        body+="<number>"+node.getIntValue("number")+"</number>\n";
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
        return(database.getShortedText(tableName,fieldname,number));
    }

    /**
    * Get binary data of a database blob field. the data is cut if it is to long.
    * @param fieldname name of the field
    * @param number number of the object in the table
    * @return an array of <code>byte</code> containing the contents of a field as text
    */
    public byte[] getShortedByte(String fieldname,int number) {
        return(database.getShortedByte(tableName,fieldname,number));
    }

    /**
    * Get binary data of a database blob field.
    * @param fieldname name of the field
    * @param number number of the object in the table
    * @return an array of <code>byte</code> containing the contents of a field as text
    */
    public byte[] getDBByte(ResultSet rs,int idx) {
        return(database.getDBByte(rs,idx));
    }

    /**
    * Get text from a blob field.
    * @param fieldname name of the field
    * @param number number of the object in the table
    * @return a <code>String</code> containing the contents of a field as text
    */
    public String getDBText(ResultSet rs,int idx) {
        return(database.getDBText(rs,idx));
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
            return(database.created(getFullTableName()));
        } else {
            return(super.created());
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
            number=""+node.getIntValue("number");
            //nameCache.put(name,number);
        }
        //}

        return number;
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

    /**
    * Returns a HTML-version of a string.
    * This replaces a number of tokens with HTML sequences.
    * The output does not match well with the new xhtml standards (ugly html), nor does it replace all tokens.
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

            obj.replace("\r\n\r\n","<p> </p>");
            obj.replace("\n\n","<p> </p>");
            obj.replace("\r\n","<br />");
            obj.replace("\n","<br />");
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
    * Support routine to return shorter strings.
    * Cuts a string to a amximum length if it exceeds the length specified.
    * @param str the string to shorten
    @ param len the maximum length
    @ return the (possibly shortened) string
    */
    public String getShort(String str,int len) {
        if (str.length()>len) {
            return(str.substring(0,(len-3))+"...");
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
            fields.put(name,def);
        }

        FieldDefs def=new FieldDefs("Type","integer",-1,-1,"otype",FieldDefs.TYPE_INTEGER,-1,3);
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
    	properties.put(name,value);
    }

    /**
    * Retrieve a specific property.
    * @param name the name of the property to get
    * @return the value of the property as a <code>String</code>
    */
    public String getInitParameter(String name) {
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
    */
    protected void debug( String msg )
    {
    	System.out.println( classname +":"+ msg );
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

}
