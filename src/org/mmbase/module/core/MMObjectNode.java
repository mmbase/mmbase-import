/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;

import org.mmbase.module.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.gui.html.*;
import org.mmbase.util.logging.*;

import org.w3c.dom.Document;

/**
 * MMObjectNode is the core of the MMBase system.
 * This class is what its all about, because the instances of this class hold the content we are using.
 * All active Nodes with data and relations are MMObjectNodes and make up the
 * object world that is MMBase (Creating, searching, removing is done by the node's parent,
 * which is a class extended from MMObjectBuilder)
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @author Eduard Witteveen
 * @version $Revision: 1.69 $ $Date: 2002-04-03 11:19:28 $
 */

public class MMObjectNode {
    /**
     * Logger routine
     */
    private static Logger log = Logging.getLoggerInstance(MMObjectNode.class.getName());

    /**
     * Holds the name - value pairs of this node (the node's fields).
     * Most nodes will have a 'number' and an 'otype' field, and fields which will differ by builder.
     * This collection should not be directly queried or changed -
     * use the SetValue and getXXXValue methods instead.
     * @scope private
     */
    public Hashtable values=new Hashtable();

    /**
     * Holds the 'extra' name-value pairs (the node's properties)
     * which are retrieved from the 'properties' table.
     * @scope private
     */
    public Hashtable properties;

    /**
     * Vector whcih stores the key's of the fields that were changed
     * since the last commit.
     * @scope private
     */
    public Vector changed=new Vector();

    /**
     * Pointer to the parent builder that is responsible for this node.
     * @scope private
     */
    public MMObjectBuilder parent;

    /**
     * Used to make fields from multiple nodes (for multilevel for example)
     * possible.
     * This is a 'default' value.
     * XXX: specifying the prefix in the fieldName SHOULD override this field.
     * @scope private
     */
    public String prefix="";

    // Vector  with the related nodes to this node
    Vector relations=null; // possibly filled with insRels

    /**
     * Determines whether this node is virtual.
     * A virtual node is not persistent (that is, not stored in a table).
     * @scope private
     */
    protected boolean virtual=false;

    private static int relation_cache_hits=0;
    private static int relation_cache_miss=0;

    /**
     * Alias name of this node.
     * XXX: nodes can have multiple aliases.
     * @scope private
     */
    protected String alias;

    // object to sync access to properties
    private Object properties_sync=new Object();

    /**
     * Empty constructor added for javadoc
     * @deprecated Unused. Should be removed.
     */
    public MMObjectNode() {
    }

    /**
     * Main constructor.
     * @param parent the node's parent, an instance of the node's builder.
     */
    public MMObjectNode(MMObjectBuilder parent) {
        if (parent!=null) {
            this.parent=parent;
        } else {
            log.error("MMObjectNode-> contructor called with parent=null");
            throw new NullPointerException("contructor called with parent=null");
        }
    }

    /**
     * legacy constructor, useless will be removed soon (daniel)
     */
//    public MMObjectNode(int id,int type, String owner) {
//    }

    /**
     * Tests whether the data in a node is valid (throws an exception if this is not the case).
     * @throws org.mmbase.module.core.InvalidDataException
     *   If the data was unrecoverably invalid (the references did not point to existing objects)
     */
    public void testValidData() throws InvalidDataException {
        parent.testValidData(this);
    };

    /**
     * Commit the node to the database or other storage system.
     * This can only be done on a existing (inserted) node. It will use the
     * changed Vector as its base of what to commit/change.
     * @return <code>true</code> if the commit was succesfull, <code>false</code> is it failed
     */
    public boolean commit() {
        return parent.commit (this);
    }

    /**
     * Insert this node into the database or other storage system.
     * @return the new node key (number field), or -1 if the insert failed
     */
    public int insert(String userName) {
        return parent.insert(userName,this);
    }

    /**
     * Once an insert is done in the editors, this method is called.
     * @param ed Contains the current edit state (editor info). The main function of this object is to pass
     *        'settings' and 'parameters' - value pairs that have been set during the edit process.
     * @return An <code>int</code> value. It's meaning is undefined.
     *        The basic routine returns -1.
     * @deprecated This method doesn't seem to fit here, as it references a gui/html object ({@link org.mmbase.module.gui.html.EditState}),
     *    endangering the separation between content and layout, and has an undefined return value.
     */
    public int insertDone(EditState ed) {
        return parent.insertDone(ed,this);
    }

    /**
     * Check and make last changes before calling {@link #commit} or {@link #insert}.
     * This method is called by the editor. This differs from {@link MMObjectBuilder#preCommit}, which is called by the database system
     * <em>during</em> the call to commit or insert.
     * @param ed Contains the current edit state (editor info). The main function of this object is to pass
     *        'settings' and 'parameters' - value pairs that have been the during the edit process.
     * @return An <code>int</code> value. It's meaning is undefined.
     *        The basic routine returns -1.
     * @deprecated This method doesn't seem to fit here, as it references a gui/html object ({@link org.mmbase.module.gui.html.EditState}),
     *    endangering the separation between content and layout. It also has an undefined return value (as well as a confusing name).
     */
    public int preEdit(EditState ed) {
        return parent.preEdit(ed,this);
    }

    /**
     * Returns the core of this node in a string.
     * Used for debugging.
     * For data exchange use toXML() and getDTD().
     * @return the contents of the node as a string.
     */
    public String toString() {
        String result="";
        try {
            result="prefix='"+prefix+"'";
            Enumeration e=values.keys();
            while (e.hasMoreElements()) {
                String key=(String)e.nextElement();
                int dbtype=getDBType(key);
                String value=""+values.get(key);  // XXX:should be retrieveValue ?
                if (result.equals("")) {
                    result=key+"="+dbtype+":'"+value+"'";
                } else {
                    result+=","+key+"="+dbtype+":'"+value+"'";
                }
            }
        } catch(Exception e) {}
        return result;
    }


    /**
     * Return the node as a string in XML format.
     * Used for data exchange, though, oddly enough, not by application export. (?)
     * @return the contents of the node as a xml-formatted string.
     */
    public String toXML() {
        // call is implemented by its builder so
        // call the builder with this node
        if (parent!=null) {
            return parent.toXML(this);
        } else {
            return null;
        }
    }

    /**
     * Stores a value in the values hashtable.
     *
     * @param fieldName the name of the field to change
     * @param fieldValue the value to assign
     */
    protected void storeValue(String fieldName,Object fieldValue) {
        values.put(fieldName, fieldValue);
    }

    /**
     * Retrieves a value from the values hashtable.
     *
     * @param fieldName the name of the field to change
     * @return the value of the field
     */
    protected Object retrieveValue(String fieldName) {
        return values.get(fieldName);
    }

    /**
     * Determines whether the node is virtual.
     * A virtual node is not persistent (that is, stored in a database table).
     */
    public boolean isVirtual() {
        return virtual;
    }
        

    /**
     *  Sets a key/value pair in the main values of this node.
     *  Note that if this node is a node in cache, the changes are immediately visible to
     *  everyone, even if the changes are not committed.
     *  The fieldName is added to the (public) 'changed' vector to track changes.
     *  @param fieldName the name of the field to change
     *  @param fieldValue the value to assign
     *  @return <code>true</code> When the field was changed, false otherwise.
     */
    public boolean setValue(String fieldName, Object fieldValue) {
        // check the value also when the parent thing is null
	Object originalValue = values.get(fieldName);
        
        // if we have an XML-dbtype field, we always have to store it inside an Element.
        if(parent != null && getDBType(fieldName) == FieldDefs.TYPE_XML && !(fieldValue instanceof Document)) {
            log.debug("im called far too often");
            Document doc = convertStringToXml(fieldName, (String) fieldValue);
            if(doc != null) {
                // store the document inside the field.. much faster...
                fieldValue = doc;
            }
        }
        // put the key/value in the value hashtable
        storeValue(fieldName, fieldValue);
        
        // process the changed value (?)
        if (parent != null) {
	    if(!parent.setValue(this,fieldName, originalValue)) {
	        // setValue of parent returned false, no update needed...
	        return false;
            }
	}
        else log.error("parent was null for node with number" + getNumber());
        setUpdate(fieldName);
        return true;
    }

    /**
     * Sets a key/value pair in the main values of this node. The value to set is of type <code>boolean</code>.
     * Note that if this node is a node in cache, the changes are immediately visible to
     * everyone, even if the changes are not committed.
     * The fieldName is added to the (public) 'changed' vector to track changes.
     * @param fieldName the name of the field to change
     * @param fieldValue the value to assign
     * @return always <code>true</code>
     */
    public boolean setValue(String fieldName,boolean fieldValue) {
        return setValue(fieldName,new Boolean(fieldValue));
    }

    /**
     *  Sets a key/value pair in the main values of this node. The value to set is of type <code>int</code>.
     *  Note that if this node is a node in cache, the changes are immediately visible to
     *  everyone, even if the changes are not committed.
     *  The fieldName is added to the (public) 'changed' vector to track changes.
     *  @param fieldName the name of the field to change
     *  @param fieldValue the value to assign
     *  @return always <code>true</code>
     */
    public boolean setValue(String fieldName,int fieldValue) {
        return setValue(fieldName,new Integer(fieldValue));
    }

    /**
     *  Sets a key/value pair in the main values of this node. The value to set is of type <code>double</code>.
     *  Note that if this node is a node in cache, the changes are immediately visible to
     *  everyone, even if the changes are not committed.
     *  The fieldName is added to the (public) 'changed' vector to track changes.
     *  @param fieldName the name of the field to change
     *  @param fieldValue the value to assign
     *  @return always <code>true</code>
     */
    public boolean setValue(String fieldName,double fieldValue) {
        return setValue(fieldName,new Double(fieldValue));
    }

    /**
     *  Sets a key/value pair in the main values of this node.
     *  The value to set is converted to the indicated type.
     *  Note that if this node is a node in cache, the changes are immediately visible to
     *  everyone, even if the changes are not committed.
     *  The fieldName is added to the (public) 'changed' vector to track changes.
     *  @param fieldName the name of the field to change
     *  @param fieldValue the value to assign
     *  @return <code>false</code> if the value is not of the indicated type, <code>true</code> otherwise
     */
    public boolean setValue(String fieldName, int type, String value)
    // WH: This one will be moved/replaced soon...
    // Testing of db types will be moved to the DB specific classes
    // Called by both versions of FieldEditor.setEditField.
    // MMBaseMultiCast.mergeXMLNode
    // MMImport.parseOneXML
    {
        if (type==FieldDefs.TYPE_UNKNOWN) {
            log.error("MMObjectNode.setValue(): unsupported fieldtype null for field "+fieldName);
            return false;
        }
        switch (type) {
            case FieldDefs.TYPE_XML:
                setValue(fieldName, convertStringToXml(fieldName, value));
                break;
            case FieldDefs.TYPE_STRING:
                setValue( fieldName, value);
                break;
            case FieldDefs.TYPE_INTEGER:
                Integer i;
                try { i = new Integer(value); }
                catch (NumberFormatException e)
                { log.error( e.toString() ); log.error(Logging.stackTrace(e)); return false; }
                setValue( fieldName, i );
                break;
            case FieldDefs.TYPE_FLOAT:
                Float f;
                try { f = new Float(value); }
                catch (NumberFormatException e)
                { log.error( e.toString() ); log.error(Logging.stackTrace(e)); return false; }
                setValue( fieldName, f );
                break;
            case FieldDefs.TYPE_LONG:
                Long l;
                try { l = new Long(value); }
                catch (NumberFormatException e)
                { log.error( e.toString() ); log.error(Logging.stackTrace(e)); return false; }
                setValue( fieldName, l );
                break;
            case FieldDefs.TYPE_DOUBLE:
                Double d;
                try { d = new Double(value); }
                catch (NumberFormatException e)
                { log.error( e.toString() ); log.error(Logging.stackTrace(e)); return false; }
                setValue( fieldName, d );
                break;
            default:
                log.error("unsupported fieldtype: "+type+" for field "+fieldName);
                return false;
        }
        return true;
    }

    // Add the field to update to the changed Vector
    //
    private void setUpdate(String fieldName) {
        // obtain the type of field this is
        int state=getDBState(fieldName);

        // add it to the changed vector so we know that we have to update it
        // on the next commit
        if (!changed.contains(fieldName) && state==FieldDefs.DBSTATE_PERSISTENT) {
            changed.addElement(fieldName);
        }

        // is it a memory only field ? then send a fieldchange
        if (state==0) sendFieldChangeSignal(fieldName);
    }

    /**
     * Retrieve an object's number.
     * In case of a new node that is not committed, this will return -1.
     * @return the number of the node
     */
    public int getNumber() {
        return getIntValue("number");
    }

    /**
     * Retrieve an object's object type.
     * This is a number (an index in the typedef builer), rather than a name.
     * @return the object type number of the node
     */
    public int getOType() {
        return getIntValue("otype");
    }

    /**
     * Get a value of a certain field.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>Object</code>
     */
    public Object getValue(String fieldName) {
        
        // get the value from the values table
        Object o = retrieveValue(prefix+fieldName);

        // routine to check for indirect values
        // this are used for functions for example
        // its implemented per builder so lets give this
        // request to our builder
        if (o==null) return parent.getValue(this,fieldName);

        // return the found object
        return o;
    }

    /**
     * Get a value of a certain field.  The value is returned as a
     * String. Non-string values are automatically converted to
     * String. 'null' is converted to an empty string.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>String</code>
     */
    public String getStringValue(String fieldName) {

        // try to get the value from the values table
        String tmp = "";
        Object o = getValue(fieldName);
        if (o!=null) {
            if (o instanceof byte[]) {
                tmp = new String((byte[])o);
            } 
            else if(o instanceof Document) {
                // 
                tmp = convertXmlToString(fieldName, (Document) o );
            }
            else {
                tmp=""+o;
            }
        }

        // check if the object is shorted, shorted means that
        // because the value can be a large text/blob object its
        // not loaded into each object when its first obtained
        // from the database but that we instead out a text $SHORTED
        // in the field. Only when the field is really used does this
        // get mapped into a real value. this saves speed and memory
        // because every blob/text mapping is a extra request to the
        // database
        if (tmp.indexOf("$SHORTED")==0) {
            if (log.isDebugEnabled()) log.debug("getStringValue(): node="+this+" -- fieldName "+fieldName);
            // obtain the database type so we can check if what
            // kind of object it is. this have be changed for
            // multiple database support.
            int type=getDBType(fieldName);

            log.debug("getStringValue(): fieldName "+fieldName+" has type "+type);
            // check if for known mapped types
            if (type==FieldDefs.TYPE_STRING) {
                MMObjectBuilder bul;

                int number=getNumber();
                // check if its in a multilevel node (than we have no node number and
                // XXX:Not needed, since checking takes place in MultiRelations!
                // Can be dropped
                if (prefix!=null && prefix.length()>0) {
                    String tmptable="";
                    int pos=prefix.indexOf('.');
                    if (pos!=-1) {
                        tmptable=prefix.substring(0,pos);
                    } else {
                        tmptable=prefix;
                    }
//                    number=getNumber();
                    bul=parent.mmb.getMMObject(tmptable);
                    log.debug("getStringValue(): "+tmptable+":"+number+":"+prefix+":"+fieldName);
                } else {
                    bul=parent;
                }

                // call our builder with the convert request this will probably
                // map it to the database we are running.
                String tmp2=bul.getShortedText(fieldName,number);

                // did we get a result then store it in the values for next use
                // and return it.
                // we could in the future also leave it unmapped in the values
                // or make this programmable per builder ?
                if (tmp2!=null) {
                    // store the unmapped value (replacing the $SHORTED text)
                    storeValue(prefix+fieldName,tmp2);
                    // return the found and now unmapped value
                    return tmp2;
                } else {
                    return null;
                }
            }
        }

        // return the found value
        return tmp;
    }

    /**
     * @see getXMLValue
     */
    public Document getXMLValue(String fieldName) {
        Object o = getValue(fieldName);
        
       
        if(getDBType(fieldName)!= FieldDefs.TYPE_XML) {
            throw new RuntimeException("field was not an xml-field, dont know how i need to convert this to and xml-document");
        }        
        if (o == null) {
            log.warn("Got null value in field " + fieldName);
            return null;
        }
        if (!(o instanceof Document)) {
            //do conversion from string to Document thing...
            log.warn("Field " + fieldName + " did not contain a Document, but a " + o.getClass().getName());
            // o = convertStringToXml(fieldName,  getStringValue(fieldName));
            // if(o != null) {
            //    values.put(fieldName, o);
            // }
            return null;
        }
        return (Document) o;
    }

    /**
     * Get a binary value of a certain field.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>byte []</code> (binary/blob field)
     */
    public byte[] getByteValue(String fieldName) {

        // try to get the value from the values table
        // it might be using a prefix to allow multilevel
        // nodes to work (if not duplicate can not be stored)

        // call below also allows for byte[] type of
        // formatting functons.
        Object obj=getValue(fieldName);

        // well same as with strings we only unmap byte values when
        // we really use them since they mean a extra request to the
        // database most of the time.

        // we signal with a empty byte[] that its not obtained yet.
        if (obj instanceof byte[]) {
            // was allready unmapped so return the value
            return (byte[])obj;
        } else {

            byte[] b;
            if (getDBType(fieldName) == FieldDefs.TYPE_BYTE) {
                // call our builder with the convert request this will probably
                // map it to the database we are running.
                b=parent.getShortedByte(fieldName,getNumber());
                if (b == null) {
                    b = new byte[0];
                }
                // we could in the future also leave it unmapped in the values
                // or make this programmable per builder ?
                storeValue(prefix+fieldName,b);
            } else {
                if (getDBType(fieldName) == FieldDefs.TYPE_STRING) {
                    String s = getStringValue(fieldName);
                    b = s.getBytes();
                } else {
                    b = new byte[0];
                }
            }
            // return the unmapped value
            return b;
        }
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an MMObjectNode.
     * If the field contains an Numeric value, the method
     * tries to obtrain the object with that number.
     * If it is a String, the method tries to obtain the object with
     * that alias. The only other possible values are those created by
     * certain virtual fields.
     * All remaining situations return <code>null</code>.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>int</code>
     */
    public MMObjectNode getNodeValue(String fieldName) {
        if (fieldName==null || fieldName.equals("number")) return this;
        MMObjectNode res=null;
        Object i=getValue(fieldName);
        if (i instanceof MMObjectNode) {
            res=(MMObjectNode)i;
        } else if (i instanceof Number) {
            res=parent.getNode(((Number)i).intValue());
        } else if (i!=null) {
            res=parent.getNode(""+i);
        }
        return res;
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an int value. Values of non-int, numeric fields are converted if possible.
     * Booelan fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * If a value is an MMObjectNode, it's numberfield is returned.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>int</code>
     */
    public int getIntValue(String fieldName) {
        Object i=getValue(fieldName);
        int res=-1;
        if (i instanceof MMObjectNode) {
            res=((MMObjectNode)i).getNumber();
        } else if (i instanceof Boolean) {
            res=((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res=((Number)i).intValue();
        } else if (i!=null) {
            try {
              res=Integer.parseInt(""+i);
            } catch (NumberFormatException e) {}
        }
        return res;
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an boolean value.
     * If the actual value is numeric, this call returns <code>true</code>
     * if the value is a positive, non-zero, value. In other words, values '0'
     * and '-1' are concidered <code>false</code>.
     * If the value is a string, this call returns <code>true</code> if
     * the value is "true" or "yes" (case-insensitive).
     * In all other cases (including calling byte fields), <code>false</code>
     * is returned.
     * Note that there is currently no basic MMBase boolean type, but some
     * <code>excecuteFunction</code> calls may return a Boolean result.
     *
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>int</code>
     */
    public boolean getBooleanValue(String fieldName) {
        Object b=getValue(fieldName);
        boolean res=false;
        if (b instanceof Boolean) {
            res=((Boolean)b).booleanValue();
        } else if (b instanceof Number) {
            res=((Number)b).intValue()>0;
        } else if (b instanceof String) {
            // note: we don't use Boolean.valueOf() because that only captures
            // the value "true"
            res= ((String)b).equalsIgnoreCase("true") ||
                 ((String)b).equalsIgnoreCase("yes");
            // Call MMLanguage, and compare to
            // the 'localized' values of true or yes.
            if ((!res) && (parent!=null)) {
                MMLanguage languages = (MMLanguage)Module.getModule("mmlanguage");
                if (languages!=null) {
                    res= ((String)b).equalsIgnoreCase(
                                languages.getFromCoreEnglish("true")) ||
                         ((String)b).equalsIgnoreCase(
                                languages.getFromCoreEnglish("yes"));
                }
            }
        }
        return res;
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an Integer value. Values of non-Integer, numeric fields are converted if possible.
     * Booelan fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>Integer</code>
     */
    public Integer getIntegerValue(String fieldName) {
        Object i=getValue(fieldName);
        int res=-1;
        if (i instanceof Boolean) {
            res=((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res=((Number)i).intValue();
        } else if (i!=null) {
            try {
              res=Integer.parseInt(""+i);
            } catch (NumberFormatException e) {}
        }
        return new Integer(res);
    }

    /**
     * Get a value of a certain field.
     * The value is returned as a long value. Values of non-long, numeric fields are converted if possible.
     * Booelan fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>long</code>
     */
    public long getLongValue(String fieldName) {
        Object i=getValue(fieldName);
        long res =-1;
        if (i instanceof Boolean) {
            res=((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res=((Number)i).longValue();
        } else if (i!=null) {
            try {
              res=Long.parseLong(""+i);
            } catch (NumberFormatException e) {}
        }
        return res;
    }


    /**
     * Get a value of a certain field.
     * The value is returned as a float value. Values of non-float, numeric fields are converted if possible.
     * Booelan fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>float</code>
     */
    public float getFloatValue(String fieldName) {
        Object i=getValue(fieldName);
        float res =-1;
        if (i instanceof Boolean) {
            res=((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res=((Number)i).floatValue();
        } else if (i!=null) {
            try {
              res=Float.parseFloat(""+i);
            } catch (NumberFormatException e) {}
        }
        return res;
    }


    /**
     * Get a value of a certain field.
     * The value is returned as a double value. Values of non-double, numeric fields are converted if possible.
     * Booelan fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>double</code>
     */
    public double getDoubleValue(String fieldName) {
        Object i=getValue(fieldName);
        double res =-1;
        if (i instanceof Boolean) {
            res=((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res=((Number)i).doubleValue();
        } else if (i!=null) {
            try {
              res=Double.parseDouble(""+i);
            } catch (NumberFormatException e) {}
        }
        return res;
    }

    /**
     * Get a value of a certain field and return is in string form (regardless of actual type).
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>String</code>
     * @deprecated use {@link #getStringValue} instead.
     */
    public String getValueAsString(String fieldName) {
    // WH Will remove/replace this one soon
    // Testing of db types will be moved to the DB specific classes
    // Currently used by:
    // Music.getObjects and getObjects2 (last is dead code)
    // FieldEditor.getEditField
    // HTMLBase.getNodeStringValue
    // ObjectSelector.getObjectFields
    // MMObjectBuilder.getGUIIndicator
    // Forums.getObjectField
    // Teasers.doTSearch
        Object o=getValue(fieldName);
        if (o!=null) {
            return ""+o; // getValue(fieldName);
        } else {
            return "";
        }
    }


    /**
     * Returns the DBType of a field.
     * @param fieldName the name of the field who's type to return
     * @return the field's DBType
     */
    public int getDBType(String fieldName) {
        if (prefix!=null && prefix.length()>0) {
            // If the prefix is set use the builder contained therein
            int pos=prefix.indexOf('.');
            if (pos==-1) pos=prefix.length();
            MMObjectBuilder bul=parent.mmb.getMMObject(prefix.substring(0,pos));
            return bul.getDBType(fieldName);
        } else {
            return parent.getDBType(fieldName);
        }
    }


    /**
     * Returns the DBState of a field.
     * @param fieldName the name of the field who's state to return
     * @return the field's DBState
     */
    public int getDBState(String fieldName) {
        if (parent!=null)    {
            return parent.getDBState(fieldName);
        } else {
            return FieldDefs.DBSTATE_UNKNOWN;
        }
    }

    /**
     * Return all the names of fields that were changed.
     * Note that this is a direct reference. Changes (i.e. clearing the vector) will affect the node's status.
     * @param a <code>Vector</code> containing all the fieldNames
     */
    public Vector getChanged() {
        return changed;
    }

    /**
     * Tests whether one of the values of this node was changed since the last commit/insert.
     * @return <code>true</code> if changes have been made, <code>false</code> otherwise
     */
    public boolean isChanged() {
        if (changed.size()>0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clear the 'signal' Vector with the changed keys since last commit/insert.
     * Marks the node as 'unchanged'.
     * Does not affect the values of the fields, nor does it commit the node.
     * @return always <code>true</code>
     */
    public boolean clearChanged() {
        changed=new Vector();
        return true;
    }

    /**
     * Return the values of this node as a hashtable (name-value pair).
     * Note that this is a direct reference. Changes will affect the node.
     * Used by various export routines.
     * @return the values as a <code>Hashtable</code>
     */
    public Hashtable getValues() {
        return values;
    }

    /**
     * Deletes the propertie cache for this node.
     * Forces a reload of the properties on next use.
     */
    public void delPropertiesCache() {
        synchronized(properties_sync) {
            properties=null;
        }
    }

    /**
     * Return a the properties for this node.
     * @return the properties as a <code>Hashtable</code>
     */
    public Hashtable getProperties() {
        synchronized(properties_sync) {
            if (properties==null) {
                properties=new Hashtable();
                MMObjectBuilder bul=parent.mmb.getMMObject("properties");
                Enumeration e=bul.search("parent=="+getNumber());
                while (e.hasMoreElements()) {
                    MMObjectNode pnode=(MMObjectNode)e.nextElement();
                    String key=pnode.getStringValue("key");
                    properties.put(key,pnode);
                }
            }
        }
        return properties;
    }


    /**
     * Returns a specified property of this node.
     * @param key the name of the property to retrieve
     * @return the property object as a <code>MMObjectNode</code>
     */
    public MMObjectNode getProperty(String key) {
        MMObjectNode n;
        synchronized(properties_sync) {
            if (properties==null) {
                getProperties();
            }
            n=(MMObjectNode)properties.get(key);
        }
        if (n!=null) {
            return n;
        } else {
            return null;
        }
    }


    /**
     * Sets a specified property for this node.
     * This method does not commit anything - it merely updates the node's propertylist.
     * @param node the property object as a <code>MMObjectNode</code>
     */
    public void putProperty(MMObjectNode node) {
        synchronized(properties_sync) {
            if (properties==null) {
                getProperties();
            }
            properties.put(node.getStringValue("key"),node);
        }
    }

    /**
     * Return the GUI indicator for this node.
     * The GUI indicator is a string that represents the contents of this node.
     * By default it is the string-representation of the first non-system field of the node.
     * Individual builders can alter this behavior.
     * @return the GUI iddicator as a <code>String</code>
     */
    public String getGUIIndicator() {
        if (parent!=null) {
            return parent.getGUIIndicator(this);
        } else {
            log.error("MMObjectNode -> can't get parent");
            return "problem";
        }
    }

    /**
     * Return the Single name for this node in the currently selected language (accoridng to the configuration).
     * The 'dutch' in the method name is a bit misleading.
     * @return the <code>String</code> value
     */
    public String getDutchSName() {
        if (parent!=null) {
            return parent.getDutchSName();
        } else {
            log.error("MMObjectNode -> can't get parent");
            return "problem";
        }
    }

    /**
     * Return the buildername of this node
     * @return the builder table name
     */
    public String getName() {
        return  parent.getTableName();
    }


    /**
     * Set the parent builder for this node.
     * @param bul the builder
     * @deprecated Unused. Should be removed.
     */
    public void setParent(MMObjectBuilder bul) {
        parent=bul;
    }


    /**
     * Delete the relation cache for this node.
     * This means it will be reloaded from the database/storage on next use.
     */
    public void delRelationsCache() {
        relations=null;
    }

    /**
     * Returns whether this node has relations.
     * This includes unidirection relations which would otherwise not be counted.
     * @return <code>true</code> if any relations exist, <code>false</code> otherwise.
     */
    public boolean hasRelations() {
        // return getRelationCount()>0;
        return parent.mmb.getInsRel().hasRelations(getNumber());
    }

    /**
     * Return all the relations of this node.
     * Use only to delete the relations of a node.
     * Note that this returns the nodes describing the relation - not the nodes 'related to'.
     * @return An <code>Enumeration</code> containing the nodes
     */
    public Enumeration getAllRelations() {
        Vector allrelations=parent.mmb.getInsRel().getAllRelationsVector(getNumber());
        if (allrelations!=null) {
            return allrelations.elements();
        } else {
            return null;
        }
    }

    /**
     * Return the relations of this node.
     * Note that this returns the nodes describing the relation - not the nodes 'related to'.
     * @return An <code>Enumeration</code> containing the nodes
     */
    public Enumeration getRelations() {
        if (relations==null) {
            relations=parent.getRelations_main(getNumber());
            relation_cache_miss++;

        } else {
            relation_cache_hits++;
        }
        if (relations!=null) {
            return relations.elements();
        } else {
            return null;
        }
    }

    /**
     * Remove the relations of the node.
     */
    public void removeRelations() {
        parent.removeRelations(this);
    }

    /**
     * Returns the number of relations of this node.
     * @return An <code>int</code> indicating the number of nodes found
     */
    public int getRelationCount() {
        if (relations==null) {
            relations=parent.getRelations_main(getNumber());
        relation_cache_miss++;
        } else {
        relation_cache_hits++;
    }

        if (relations!=null) {
            return relations.size();
        } else {
            return 0;
        }
    }


    /**
     * Return the relations of this node, filtered on a specified type.
     * Note that this returns the nodes describing the relation - not the nodes 'related to'.
     * @param otype the 'type' of relations to return. The type identifies a relation (InsRel-derived) builder, not a reldef object.
     * @return An <code>Enumeration</code> containing the nodes
     */
    public Enumeration getRelations(int otype) {
        Enumeration e = getRelations();
        Vector result=new Vector();
        if (e!=null) {
            while (e.hasMoreElements()) {
                MMObjectNode tnode=(MMObjectNode)e.nextElement();
                if (tnode.getOType()==otype) {
                    result.addElement(tnode);
                }
            }
        }
        return result.elements();
    }

    /**
     * Return the relations of this node, filtered on a specified type.
     * Note that this returns the nodes describing the relation - not the nodes 'related to'.
     * @param wantedtype the 'type' of relations to return. The type identifies a relation (InsRel-derived) builder, not a reldef object.
     * @return An <code>Enumeration</code> containing the nodes
     */
    public Enumeration getRelations(String wantedtype) {
        int otype=parent.mmb.getTypeDef().getIntValue(wantedtype);
        if (otype!=-1) {
            return getRelations(otype);
        }
        return null;
    }

    /**
     * Return the number of relations of this node, filtered on a specified type.
     * @param wantedtype the 'type' of related nodes (NOT the relations!).
     * @return An <code>int</code> indicating the number of nodes found
     */
    public int getRelationCount(String wantedtype) {
        int count=0;
        int otype=parent.mmb.getTypeDef().getIntValue(wantedtype);
        if (otype!=-1) {
            if (relations==null) {
                relations=parent.mmb.getInsRel().getRelationsVector(getNumber());
            relation_cache_miss++;
            } else {
            relation_cache_hits++;
        }
            if (relations!=null) {
                for(Enumeration e=relations.elements();e.hasMoreElements();) {
                    MMObjectNode tnode=(MMObjectNode)e.nextElement();
                    int snumber=tnode.getIntValue("snumber");
                    int nodetype =0;
                    if (snumber==getNumber()) {
                        nodetype=parent.getNodeType(tnode.getIntValue("dnumber"));
                    } else {
                        nodetype=parent.getNodeType(snumber);
                    }
                    if (nodetype==otype) {
                        count +=1;
                    }
                }
            }
        } else {
            log.warn("getRelationCount is requested with an invalid Builder name (otype "+wantedtype+" does not exist)");
        }
        return count;
    }

    /**
     * Returns the node's age
     * @return the age in days
     */
    public int getAge() {
        return parent.getAge(this);
    }

    /**
     * Returns the node's builder tablename.
     * @return the tablename of the builder as a <code>String</code>
     * @deprecated use getName instead
     */
    public String getTableName() {
        return parent.getTableName();
    }

    /**
     * Sends a field-changed signal.
     * @param fieldName the name of the changed field.
     * @return always <code>true</code>
     */
    public boolean sendFieldChangeSignal(String fieldName) {
        return parent.sendFieldChangeSignal(this,fieldName);
    }

    /**
     * Sets the node's alias.
     * The code only sets a (memory) property, it does not actually add the alias to the database.
     * Does not support multiple aliases.
     */
    public void setAlias(String alias) {
        this.alias=alias;
    }

    /**
     * Returns the node's alias.
     * Does not support multiple aliases.
     * @return the alias as a <code>String</code>
     */
    public String getAlias() {
        return alias;
    }


    /**
     * Get all related nodes. The returned nodes are not the
     * nodes directly attached to this node (the relation nodes) but the nodes
     * attached to the relation nodes of this node.
     * @return a <code>Vector</code> containing <code>MMObjectNode</code>s
     */
    public Vector getRelatedNodes() {
        Vector result = new Vector();
        for (Enumeration e = getRelations(); e.hasMoreElements();) {
            MMObjectNode relNode = (MMObjectNode)e.nextElement();
            int number = relNode.getIntValue("dnumber");
            if (number == getNumber()) {
                number = relNode.getIntValue("snumber");
            }
            MMObjectNode destNode = (MMObjectNode)parent.getNode(number);
            result.addElement(destNode);
        }
        return result;
    }

    /**
     * Get the related nodes of a certain type. The returned nodes are not the
     * nodes directly attached to this node (the relation nodes) but the nodes
     * attached to the relation nodes of this node.
     *
     * @param type the type of objects to be returned
     * @return a <code>Vector</code> containing <code>MMObjectNode</code>s
     */
    public Vector getRelatedNodes(String type) {
        MMObjectBuilder bul=parent.mmb.getMMObject(type);
        if (bul == null) {
            log.error("getRelatedNodes: "+type+" is not a valid builder");
            return null;
        }
        Vector allNodes = getRelatedNodes();
        Vector result = new Vector();
        for (Enumeration e = allNodes.elements(); e.hasMoreElements();) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            if (node.parent.oType==bul.oType) {
                result.addElement(node);
            }
        }
        return result;
    }

    public static int getRelationCacheHits() {
        return relation_cache_hits;
    }

    public static int getRelationCacheMiss() {
        return relation_cache_miss;
    }
    

    /**
     * Convert a String value of a field to a Document
     * @param fieldName The field to be used.
     * @param value     The current value of the field, (can be null)
     * @return A DOM <code>Document</code> or <code>null</code> if there was no value and builder allowed  to be null
     * @throws RuntimeException When value was null and not allowed by builer, and xml failures.
     */
    private Document convertStringToXml(String fieldName, String value) {
        value = value.trim();
        if(value == null || value.length()==0) {
            log.debug("field was empty");
            // may only happen, if the field may be null...
            if(parent.getField(fieldName).getDBNotNull()) {
                throw new RuntimeException("field with name '"+fieldName+"' may not be null");
            }
            return null;
        }                
        if (value.startsWith("<")) { 
            // removing doc-headers if nessecary

            // remove all the <?xml stuff from beginning if there.... 
            //  <?xml version="1.0" encoding="utf-8"?>
            if(value.startsWith("<?xml")) {
                // strip till next ?>
                int stop = value.indexOf("?>");
                if(stop > 0) {
                    value = value.substring(stop + 2).trim();
                    log.debug("removed <?xml part");
                }
                else {
                    throw new RuntimeException("no ending ?> found in xml:\n" + value);
                }
            } else {
                log.debug("no <?xml header found");
            }
            
            // remove all the <!DOCTYPE stuff from beginning if there.... 
            // <!DOCTYPE builder PUBLIC "//MMBase - builder//" "http://www.mmbase.org/dtd/builder.dtd">
            if(value.startsWith("<!DOCTYPE")) {
                // strip till next >
                int stop = value.indexOf(">");
                if(stop > 0) {
                    value = value.substring(stop + 1).trim();
                    log.debug("removed <!DOCTYPE part");
                } else {
                    throw new RuntimeException("no ending > found in xml:\n" + value);
                }                
            } else {
                log.debug("no <!DOCTYPE header found");
            }            
        }
        else {
            // not XML, make it XML, when conversion specified, use it...
            String propertyName = fieldName + ".xmlconversion";
            String conversion = parent.getInitParameter(propertyName);
            if(conversion == null) {                
                conversion = "MMXF_POOR";                
                log.warn("property: '"+propertyName+"' for builder: '"+parent.getTableName()+"' was not set, converting string to xml for field: '" + fieldName + "' using the default: '" + conversion + "'.");
            }
            log.debug("converting the string to something else using conversion: " + conversion);
            value = org.mmbase.util.Encode.decode(conversion, (String) value);
        }        
        
        if (log.isDebugEnabled()) { 
            log.trace("using xml string:\n"+value);
        }    
        // add the header stuff...
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"" + parent.mmb.getEncoding() + "\" ?>";
        String doctype = parent.getField(fieldName).getDBDocType();
        if(doctype != null) {
            xmlHeader += "\n" + doctype;
        }
        value = xmlHeader + "\n" + value;
        
        /////////////////////////////////////////////
        // TODO: RE-USE THE PARSER EVERY TIME !    //
        try {                
            // getXML also uses a documentBuilder, maybe we can speed it up by making it a static member variable,,
            // or ask it from BasicReader ?
            javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            if(doctype != null) {
                log.debug("validating the xmlfield for field with name:" + fieldName + " with doctype: " + doctype);
                dfactory.setValidating(true);
            }
            javax.xml.parsers.DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

            // dont log errors, and try to process as much as possible...
            org.mmbase.util.XMLErrorHandler errorHandler = new org.mmbase.util.XMLErrorHandler(false, org.mmbase.util.XMLErrorHandler.NEVER);
            documentBuilder.setErrorHandler(errorHandler);
            documentBuilder.setEntityResolver(new org.mmbase.util.XMLEntityResolver());
            // ByteArrayInputStream?
            // Yes, in contradiction to what one would think, XML are bytes, rather then characters.
            Document doc = documentBuilder.parse(new java.io.ByteArrayInputStream(value.getBytes(parent.mmb.getEncoding())));
            if(!errorHandler.foundNothing()) {
                throw new RuntimeException("xml for field with name: '"+fieldName+"' invalid:\n"+errorHandler.getMessageBuffer()+"for xml:\n"+value);
            }
            return doc;
        }
        catch(javax.xml.parsers.ParserConfigurationException pce) {
	    String msg = "[sax parser] not well formed xml: "+pce.toString() + " node#"+getNumber()+"\n"+value+"\n" + Logging.stackTrace(pce);
            log.error(msg);
	    throw new RuntimeException(msg);
        }
        catch(org.xml.sax.SAXException se) {
	    String msg = "[sax] not well formed xml: "+se.toString() + "("+se.getMessage()+")" + " node#"+getNumber()+"\n"+value+"\n" + Logging.stackTrace(se);
            log.error(msg);
	    throw new RuntimeException(msg);
        }
        catch(java.io.IOException ioe) {
	    String msg = "[io] not well formed xml: "+ioe.toString() + " node#"+getNumber()+"\n"+value+"\n" + Logging.stackTrace(ioe);
            log.error(msg);
	    throw new RuntimeException(msg);
        }
    }
    
    private String convertXmlToString(String fieldName, Document xml) {
        log.debug("converting from xml to string");
        
        // check for null values
        if(xml == null) {
            log.debug("field was empty");
            // string with null isnt allowed in mmbase...
            return "";            
        }
        
        // check if we are using the right DOC-type for this field....
        String doctype = parent.getField(fieldName).getDBDocType();
        if(doctype != null) {
            // we have a doctype... the doctype of the document has to mach the doctype of the doctype which is needed..
            org.w3c.dom.DocumentType type =  xml.getDoctype();
            String publicId = type.getPublicId();
            if(doctype.indexOf(publicId) == -1) {
                throw new RuntimeException("doctype('"+doctype+"') required by field '"+fieldName+"' and public id was NOT in it : '"+publicId+"'");
            }
            log.warn("doctype check can not completely be trusted");
        }
        /////////////////////////////////////////////
        // TODO: RE-USE THE PARSER EVERY TIME !    //
        try {
            // getXML also uses a documentBuilder, maybe we can speed it up by making it a static member variable,,
            // or ask it from BasicReader ?
        
            //make a string from the XML
            javax.xml.transform.TransformerFactory tfactory = javax.xml.transform.TransformerFactory.newInstance();
            //tfactory.setURIResolver(new org.mmbase.util.xml.URIResolver(new java.io.File("")));
            javax.xml.transform.Transformer serializer = tfactory.newTransformer();
            // for now, we save everything in ident form, this since it makes debugging a little bit more handy            
            serializer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            // store as less as possible, otherthings should be resolved from gui-type
            serializer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            java.io.StringWriter str = new java.io.StringWriter();
            serializer.transform(new javax.xml.transform.dom.DOMSource(xml),  new javax.xml.transform.stream.StreamResult(str));
            if (log.isDebugEnabled()) { 
                log.debug("xml -> string:\n" + str.toString());
            }                                    
            return str.toString();
        }
        catch(javax.xml.transform.TransformerConfigurationException tce) {
            String message = tce.toString() + " " + Logging.stackTrace(tce);
            log.error(message);
            throw new RuntimeException(message);
        }
        catch(javax.xml.transform.TransformerException te) {
            String message = te.toString() + " " + Logging.stackTrace(te);
            log.error(message);
            throw new RuntimeException(message);
        }        
    }    
}
