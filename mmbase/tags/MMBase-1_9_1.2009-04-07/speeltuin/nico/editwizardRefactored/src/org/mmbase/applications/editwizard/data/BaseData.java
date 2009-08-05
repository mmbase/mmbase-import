/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;

import java.util.*;

/**
 * this object is the abstract object for data object
 * @todo javadoc
 *
 * @author caicai
 * @created 2005-10-11
 * @version $Id: BaseData.java,v 1.3 2006-02-02 12:18:33 pierre Exp $
 */
public abstract class BaseData {

    public static final int STATUS_NEW        = 4;
    public static final int STATUS_DELETE     = 3;
    public static final int STATUS_CHANGE     = 2;
    public static final int STATUS_LOAD       = 1;
    public static final int STATUS_NOT_INIT   = 0;
    public static final int STATUS_NOT_LOADED = -1;

    public static final String ELEM_OBJECT      = "object";
    public static final String ELEM_RELATION    = "relation";
    public static final String ELEM_FIELD       = "field";

    public static final String ATTR_NAME        = "name";
    public static final String ATTR_NUMBER      = "number";
    public static final String ATTR_TYPE        = "type";
    public static final String ATTR_MAYDELETE   = "maydelete";
    public static final String ATTR_MAYWRITE    = "maywrite";
    public static final String ATTR_DID         = "did";
    public static final String ATTR_DESTINATION = "destination";
    public static final String ATTR_SOURCE      = "source";
    public static final String ATTR_ROLE        = "role";

    // store the attributes of this node
    private final Map attributes = new HashMap();

    //object unique id number
//    private final String did  = DataUtils.getDataId();

    protected BaseData parent = null;

    // child elements' list, used within this class
    // should not be changed by other class directly
    protected final List children = new ArrayList();

    // status
    private int status = BaseData.STATUS_NOT_INIT;

    // used to store fielddata
    private final List fieldDataList = new ArrayList();
    // used to manage field.name-->fielddata mapping,
    // only be used within this class as a index for fast search.
    private final Map fieldNameIndex = new HashMap();
    // used to manage field.id-->fielddata mapping
    // only be used within this class as a index for fast search.
    private final Map fieldDidIndex = new HashMap();

    public BaseData() {
        this.setAttribute(ATTR_DID,DataUtils.getDataId());
    }

    /**
     * add field data into object
     */
    public void addField(FieldData fieldData) {
        this.fieldDataList.add(fieldData);
        this.fieldNameIndex.put(fieldData.getName(),fieldData);
        this.fieldDidIndex.put(fieldData.getDid(),fieldData);
        fieldData.setMainObject(this);
    }

    /**
     *
     * @param fieldDataList
     */
    public void addFields(List fieldDataList) {
        if (fieldDataList==null) {
            return;
        }
        for (int i=0;i<fieldDataList.size();i++){
            addField((FieldData)fieldDataList.get(i));
        }
    }

    /**
     * get the fielddata by specify name
     * @param fieldName
     * @return
     */
    public FieldData findFieldByName(String fieldName) {
        FieldData fieldData = (FieldData)this.fieldNameIndex.get(fieldName);
        if (fieldData!=null && this.fieldDataList.contains(fieldData) == false) {
            this.fieldNameIndex.remove(fieldName);
            this.fieldDidIndex.remove(fieldData.getDid());
            fieldData = null;
        }
        return fieldData;
    }

    /**
     * get list of the fields
     * @return Returns the dataFields in a List.
     */
    public List getFieldDataList() {
        return Collections.unmodifiableList(fieldDataList);
    }

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return this.getAttribute(ATTR_NUMBER);
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return this.getAttribute(ATTR_TYPE);
    }

    /**
     * @return Returns the mayDeletable.
     */
    public boolean isMayDelete() {
        return "true".equals(this.getAttribute(ATTR_MAYDELETE));
    }

    /**
     * @return Returns the mayWritable.
     */
    public boolean isMayWrite() {
        return "true".equals(this.getAttribute(ATTR_MAYWRITE));
    }

    /**
     * @param mayDeletable The mayDeletable to set.
     */
    public void setMayDelete(boolean mayDelete) {
        this.setAttribute(ATTR_MAYDELETE,""+mayDelete);
    }

    /**
     * @param mayWritable The mayWritable to set.
     */
    public void setMayWrite(boolean mayWrite) {
        this.setAttribute(ATTR_MAYWRITE,""+mayWrite);
    }

    /**
     * get object's number.
     * @param number The number to set.
     */
    public void setNumber(String number) {
        this.setAttribute(ATTR_NUMBER,""+number);
    }

    /**
     * get object's type (note type)
     * @param type The type to set.
     */
    public void setType(String type) {
        this.setAttribute(ATTR_TYPE,type);
    }


    /**
     * get object's unique identify number
     * @return Returns the id.
     */
    public String getDid() {
        return this.getAttribute(ATTR_DID);
    }

    /**
     * find field by specified field's did
     * @return
     */
    public FieldData findFieldById (String did) {
        FieldData fieldData = (FieldData)this.fieldDidIndex.get(did);
        if (fieldData!=null && this.fieldDataList.contains(fieldData)==false) {
            this.fieldDidIndex.remove(did);
            this.fieldNameIndex.remove(fieldData.getName());
            fieldData = null;
        }
        if (fieldData!=null) {
            return fieldData;
        }
        for (Iterator iterator=this.children.iterator();iterator.hasNext();) {
            BaseData child = (BaseData)iterator.next();
            fieldData = child.findFieldById(did);
            if (fieldData!=null) {
                return fieldData;
            }
        }
        return null;
    }

    /**
     * set status of current data
     * @param status The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * get status of current data
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * set attribute to data
     * @param attrName
     * @param attrValue
     */
    public void setAttribute(String attrName, String attrValue) {
        this.attributes.put(attrName,attrValue);
    }

    /**
     * get attribute to data
     * @param attrName
     * @return
     */
    public String getAttribute(String attrName) {
        return (String)this.attributes.get(attrName);
    }

    /**
     * clear attributes of the data
     *
     */
    public void clearAttributes() {
        this.attributes.clear();
    }

    /**
     * get root node of the tree in which current node located
     * @return
     */
    public BaseData getRoot() {
        if (this.parent==null) {
            return this;
        }
        return this.parent.getRoot();
    }

    /**
     * @return Returns the parent.
     */
    public BaseData getParent() {
        if (parent!=null && parent.getChildren().contains(this)==false) {
            // this scenario means that already removed current node from parent but not update
            // this parent attribute's value
            parent = null;
        }
        return parent;
    }

    /**
     * get list of all children nodes.
     * @return Returns the childrenList.
     */
    public List getChildren() {
        return children;
    }

    /**
     * add node into current node as child
     * @param child
     */
    public void addChild(BaseData child) {
        this.children.add(child);
        child.parent = this;
    }

    /**
     * remove child from current node
     * @param child
     */
    public void removeChild(BaseData child) {
        this.children.remove(child);
        child.parent = null;
    }

}
