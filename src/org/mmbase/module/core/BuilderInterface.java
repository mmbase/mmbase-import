/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;

import java.util.*;
import java.sql.ResultSet;
import org.mmbase.util.*;
import org.mmbase.module.ParseException;
import org.mmbase.module.corebuilders.FieldDefs;

/**
 * @author Daniel Ockeloen
 * @author Rob Vermeulen
 */
public interface BuilderInterface {

	/**
	 * Commit node to the database
	 * @param node node to commit
	 * @return true if node is committed
	 * @return false if node cannot be committed
	 */
	public boolean commit(MMObjectNode node);


	/**
	 * Create and get new node
	 * @param owner the creator of this node
	 */
	public MMObjectNode getNewNode(String owner);

	
	/**
	 * Remove node
	 * @param node node to remove
	 */
	public void removeNode(MMObjectNode node);


	/**
	 * Remove all relations belonging to a certain node
	 * @param node node from which all relations have to be removed
	 */
	public void removeRelations(MMObjectNode node);


	/**
	 * Get node
	 * @param number objectnumber of node to get
	 * @return the node with given object number
	 */
	public MMObjectNode getNode(int number);


	/**
	 * Get node
	 * @param number objectnumber of node to get
	 * @return the node with given object number
	 */
	public MMObjectNode getNode(String number) ;


	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration search(String where); 

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVector(String where);

	/**
	* Enumerate all the objects that are within this set
	*/
	public Vector searchVectorIn(String in);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchNumbers(String where);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration search(String where,String sort);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchIn(String where,String sort,String in);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchIn(String where,String in);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration search(String where,String sort,boolean direction); 

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchIn(String where,String sort,boolean direction,String in);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVector(String where,String sorted);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVectorIn(String where,String sorted,String in);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVector(String where,String sorted,boolean direction);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchVectorIn(String where,String sorted,boolean direction,String in);

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Enumeration searchWithWhere(String where);


	/**
	* read the result into a sorted vector
	* (Called by nl.vpro.mmbase.module.search.TeaserSearcher.createShopResult)
	*/
	public SortedVector readSearchResults(ResultSet rs, SortedVector sv);


	/**
	* build a set command string from a set nodes ( should be moved )
	*/
	public String buildSet(Vector nodes, String fieldName);

	/**
	* return all fielddefs of this objecttype
	*/
	public Vector getFields();


	/**
	* return the fieldnames of this objecttype
	*/
	public Vector getFieldNames();

	/**
	* return the fielddefs of a fieldname
	*/
	public FieldDefs getField(String fieldName);


	/**
	* return the database type of the objecttype
	*/
	public String getDBType(String fieldName);

	/**
	* return the database state of the objecttype
	*/
	public int getDBState(String fieldName) ;

	/**
	* what should a gui display when asked for this node/field combo
	* Default is the first non system field (first field after owner)
	* override this to display your own choice (see Images.java)
	*/
	public String getGUIIndicator(MMObjectNode node) ;

	/**
	* what should a gui display when asked for this node/field combo
	*/
	public String getGUIIndicator(String field,MMObjectNode node) ;

	/**
	* get the fielddefs but sorted
	*/
	public Vector getEditFields();

	/**
	* get the fielddefs but sorted
	*/
	public Vector getSortedListFields() ;


	/**
	* get the fielddefs but sorted
	*/
	public Vector getSortedFields() ;

	/**
	* returns the next field as defined by its fielddefs
	*/
	public FieldDefs getNextField(String currentfield) ;

	/**
	* return table name
	*/
	public String getTableName() ;

	/**
	* return the full table name
	*/
	public String getFullTableName() ;
	
	/**
	* should be overriden if you want to define derived fields in a object
	*/	
	public Object getValue(MMObjectNode node,String field) ;

	// called main to prevent override by insrel;
	public Vector getRelations_main(int src) ;

	/**
	* return the default url of this object (should be redone)
	*/	
	public String getDefaultUrl(int src) ;

	
	/**
	* return the number of nodes in the cache of one objecttype
	*/
	public int getCacheSize() ;


	/**
	* return the number of nodes in the cache of one objecttype
	*/
	public int getCacheSize(String type) ;

	/**
	* get the number of the nodes cached (will be removed)
	*/
	public String getCacheNumbers() ;

	/**
	* delete the nodes cache
	*/
	public void deleteNodeCache() ;

	/**
	* get the next DB key
	*/
	public int getDBKey() ;



	/**
	* return the age in days of the node
	*/
	public int getAge(MMObjectNode node); 

	/**
	* return the name of this mmserver
	*/
	public String getMachineName();

	/**
	* called when a remote node is changed, should be called by subclasses
	* if they override it
	*/
	public boolean nodeRemoteChanged(String number,String builder,String ctype) ;

	/**
	* called when a local node is changed, should be called by subclasses
	* if they override it
	*/
	public boolean nodeLocalChanged(String number,String builder,String ctype); 


	/**
	* called then a local field is changed
	*/
	public boolean fieldLocalChanged(String number,String builder,String field,String value) ;

	/**
	* add object to the remote change list of this object
	*/
	public boolean addRemoteObserver(MMBaseObserver obs); 

	/**
	* add object to the local change list of this object
	*/
	public boolean addLocalObserver(MMBaseObserver obs); 

	/**
	*  used to create a default teaser by any builder (will be removed?)
	*/
	public MMObjectNode getDefaultTeaser(MMObjectNode node,MMObjectNode tnode); 
	
	/**
	* waits until a node is changed (multicast)
	*/
	public boolean waitUntilNodeChanged(MMObjectNode node); 

	/**
	* getList all for frontend code
	*/
	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws ParseException; 


	/**
	* replace all for frontend code
	*/
	public String replace(scanpage sp, StringTokenizer tok);

	/**
	* set debug state
	*/	
	public void setDebug(boolean state) ;


	public MMObjectNode getAliasedNode(String key); 

	/**
	* convert mmnode2sql still new should replace the old mapper soon
	*/	
	public String convertMMNode2SQL(String where); 


	/**
	* set the MMBase object
	*/
	public void setMMBase(MMBase m); 

	/**
	* set DBLayout
	* needs to be replaced soon if i know how
	*/
	public void setDBLayout(Vector vec); 


	/**
	* set DBLayout
	* needs to be replaced soon if i know how
	*/
	public void setDBLayout_xml(Hashtable fields); 

	/**
	* set tablename of the builder
	*/
	public void setTableName(String tableName); 

	/**
	* set description of the builder
	*/
	public void setDescription(String e); 

	/**
	* get description of the builder
	*/
	public String getDescription(); 


	/**
	* set search Age
	*/
	public void setSearchAge(String age); 


	/**
	* get search Age
	*/
	public String getSearchAge(); 

	/**
	 * set classname of the builder
	 * @param the classname of the builder
	 */
	public void setClassName(String d); 

	/**
	 * get the classname of the builder
	 * @return classname of this builder
	 */
	public String getClassName(); 

	/**
	* send a signal to other servers of this fieldchange
	*/
	public boolean	sendFieldChangeSignal(MMObjectNode node,String fieldname) ;

	public boolean signalNewObject(String tableName,int number); 


	public String toXML(MMObjectNode node); 

	public void setSingularNames(Hashtable names);

	public void setPluralNames(Hashtable names);
		
	/**
	* get text from blob
	*/
	public String getShortedText(String fieldname,int number); 

	/**
	* get byte of a database blob
	*/
	public byte[] getShortedByte(String fieldname,int number); 


	/**
	* get byte of a database blob
	*/
	public byte[] getDBByte(ResultSet rs,int idx); 

	/**
	* get text of a database blob
	*/
	public String getDBText(ResultSet rs,int idx); 

	public boolean created(); 
	
	public String getNumberFromName(String name); 

	public boolean setValue(MMObjectNode node,String fieldname); 


	/**
	* this call will be removed once the new xml configs work
	* it provides a way to simulate the xml files (like url.xm).
	*/
	public Hashtable getXMLSetup();

	public void setXMLValues(Vector xmlfields);

	public void setXmlConfig(boolean state);

	public boolean isXMLConfig();
}
