/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * this object is the abstract object for data object
 * @todo javadoc
 * 
 * @author caicai
 * @created 2005-10-11
 * @version $Id: BaseData.java,v 1.1 2005-11-28 10:09:29 nklasens Exp $
 */
public abstract class BaseData {

    public static final int STATUS_NEW      = 4;
    public static final int STATUS_DELETE   = 3;
    public static final int STATUS_CHANGE   = 2;
    public static final int STATUS_LOAD     = 1;
    public static final int STATUS_NOT_INIT = 0;
    
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
     * get list of fields' name
     * @deprecated not used in project
     * @param name The name to set.
     */
    public List getFieldNameList() {
        List nameList = new ArrayList();
        nameList.addAll(this.fieldNameIndex.keySet());
        return Collections.unmodifiableList(nameList);
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
     * according to xpath, find node under current node.
     * @param xpath
     * @param recursive
     * @return
     * @throws WizardException 
     */
    /*
    public BaseData findNode(String xpath, boolean recursive) {
        // this method is used to add xpath support to POJO, the following is some xpath examples 
        // which should be support by this method. All the samples below come from previous projects
        // 1) fdatapath=".//object[@type='images']"
        // 2) fdatapath="object/relation/object[@type='images']"
        // 3) fdatapath="object/field[@name='start']"
        // 4) fdatapath="field[@name=&apos;menu_type&apos;]"  notic: &apos;--> '
        //TODO: I think this method may cause performance issue
        if (xpath == null) {
            return null;
        }
        BaseData field = null;
        if (xpath.startsWith("..")) {
            if (this.parent!=null) {
                log.error("could not find parent(..) node because the current node has no parent");
                return null;
            } else if (xpath.startsWith("..//")) {
                //find node in all descendants of the parent, recursive into children's children
                return this.parent.findNode(xpath.substring(4),true);
            } else if (xpath.startsWith("../")) {
                //find node in the parent node
                return this.parent.findNode(xpath.substring(3),false);
            } else {
                //find field in the parent node
                return this.parent.findNode(xpath.substring(2),false);
            }

        } else if (xpath.startsWith(".")) {
            if (xpath.startsWith(".//")) {
                //find node in all descendants of the current node, recursive into children's children
                return this.findNode(xpath.substring(3),true);
            } else if (xpath.startsWith("./")) {
                //find node in all children of the current node
                return this.findNode(xpath.substring(2),false);
            } else {
                //find field in the current node
                return this.findNode(xpath.substring(1),false);
            }
            
        } else if (xpath.startsWith("//")) {
            //find node in all descendants of the root, recursive into children's children
            BaseData root = this.getRoot();
            return root.findNode(xpath.substring(2),true);
            
        } else if (xpath.startsWith("/")) {
            //find node in all children of the root
            BaseData root = this.getRoot();
            return root.findNode(xpath.substring(1),false);
            
        } else if (xpath.startsWith("object")) {
            if (this instanceof ObjectData) {
                field = null;
                log.error("it is not allowed that find object under object");
            } else {
                RelationData relation = (RelationData)this;
                ObjectData object = relation.getRelatedObject();
                if (xpath.startsWith("object//")) {
                    //find node in all descendants of the current node, recursive into children's children
                    field = object.findNode(xpath.substring(8),true);
                } else if (xpath.startsWith("object/")) {
                    //find field in related object
                    field = object.findNode(xpath.substring(7),recursive);
                } else {
                    //find field in related object
                    field = object.findNode(xpath.substring(6),recursive);
                }
            }
        } else if (xpath.startsWith("relation")) {
            if (this instanceof RelationData) {
                log.error("it is not allowed that find relation under relation");
                field=null;
            } else {
                ObjectData object = (ObjectData)this;
                if (xpath.startsWith("relation//")) {
                    //find node in all descendants of the current node, recursive into children's children
                    field = object.findNode(xpath.substring(10),true);
                } else if (xpath.startsWith("relation/")) {
                    //find field in related object
                    field = object.findNode(xpath.substring(9),recursive);
                } else {
                    //find field in related object
                    field = object.findNode(xpath.substring(8),recursive);
                }
            }
        } else if (xpath.startsWith("[")) {
            Pattern pattern = Pattern.compile("\\[((object|relation)/)?@(\\w+)=['\"](\\w+)['\"]\\]");
            Matcher matcher = pattern.matcher(xpath);
            xpath = xpath.substring(matcher.end());
            if (matcher.find()) {
                String node = matcher.group(2);
                String attrName = matcher.group(3);
                String attrValue = matcher.group(4);
                if ("object".equals(node) && this instanceof RelationData) {
                    RelationData relation = (RelationData)this;
                    ObjectData object = relation.getRelatedObject();
                    if (attrValue!=null && attrValue.equals(object.getAttribute(attrName))) {
                        field = this;
                    }  
                } else if ("relation".equals(node) && this instanceof ObjectData) {
                    ObjectData object = (ObjectData)this;
                    List relationList = object.getRelationDataList();
                    for (int i=0;i<relationList.size();i++) {
                        RelationData relation = (RelationData)relationList.get(i);
                        if (attrValue!=null && attrValue.equals(relation.getAttribute(attrName))) {
                            field = this;
                            break;
                        }  
                    }
                } else {
                    if (attrValue!=null && attrValue.equals(this.getAttribute(attrName))) {
                        field = this;
                    }  
                }
            }
        } else {
            log.error("the xpath "+xpath+" is not point to a node "+this.getType());
        }
        if (field==null && recursive) {
            if (this instanceof RelationData) {
                RelationData relation = (RelationData)this;
                ObjectData object = relation.getRelatedObject();
                field = object.findNode(xpath,recursive);
            } else if (this instanceof ObjectData) {
                ObjectData object = (ObjectData)this;
                List relationList = object.getRelationDataList();
                for (int i=0;i<relationList.size();i++) {
                    RelationData relationData = (RelationData)relationList.get(i);
                    field = relationData.findNode(xpath,recursive);
                    if (field!=null) {
                        break;
                    }
                }
                
            }
        }
        return field;
    }
    */
    /**
     * find field int this node, according to the specified xpath.
     * @param xpath
     * @return
     */
    /*
    public FieldData findField(String xpath) {
        if (xpath == null) {
            return null;
        }
        if (xpath.startsWith("field")) {
            Pattern pattern = Pattern.compile("field\\[\\@name=['\"](\\w+)['\"]\\]");
            Matcher matcher = pattern.matcher(xpath);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                if ("".equals(fieldName)==false) {
                    return this.findFieldByName(fieldName);
                }
            }
            return null;
        }
        Pattern pattern = Pattern.compile(
                "[./]*((relation|object)(\\[@(\\w+)=['\"](\\w*)['\"]\\])?/?)*(field\\[@(\\w+)=['\"](\\w*)['\"]\\])?");
        Matcher matcher = pattern.matcher(xpath);
        if (matcher.find()) {
            String fieldXPath = matcher.group(6);
            String nodeXPath = null;
            if (matcher.end(1)>0) {
                nodeXPath = xpath.substring(0,matcher.end(1));
                BaseData node = this.findNode(nodeXPath,false);
                if (node!=null) {
                    if (fieldXPath!=null) {
                        return node.findField(fieldXPath);
                    }
                    if ("images".equals(this.getType()) ) {
                        //if image object and not specify field's name, it implies "handler" field
                        return this.findFieldByName("handler");
                    }
                    log.error("must specify field's name for object "+this.getType());
                }
            }
        } else {
            log.error("xpath "+xpath+" is not a valid path");
        }
        return null;
    }
    */
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
