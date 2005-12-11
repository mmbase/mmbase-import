/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.applications.editwizard.util.XmlUtil;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Node;


public class DataUtils {
    
    private static Pattern XPATH_PATTERN_FIELD = Pattern.compile(
        "((relation|object)(\\[@(\\w+)=['\"](\\w*)['\"]\\])?/?)*(field\\[@(\\w+)=['\"](\\w*)['\"]\\])?");
    // a complex pattern:
    // "[./]*((relation|object)(\\[@(\\w+)=['\"](\\w*)['\"]\\])?/?)*(field\\[@(\\w+)=['\"](\\w*)['\"]\\])?");
    
    private static Pattern XPATH_PATTERN_FDATAPATH = Pattern.compile(
        "((object)/)?field\\[@name=['\"](\\w+)['\"]\\]");

    private static final Logger log = Logging.getLoggerInstance(DataUtils.class);

    private static long dataObjectSerialNo = 0l;

    final static String[] TYPE_DESCRIPTIONS = {
        "unknown", "string", "integer", "unknown", "byte", "float", "double", "long", "xml", "node", "datetime", "boolean", "list"
    };
    
    public static String getTypeDescription(int type) {
        if (type >= 0 && type < TYPE_DESCRIPTIONS.length) {
             return TYPE_DESCRIPTIONS[type];
        } else {
             return TYPE_DESCRIPTIONS[0];
        }
     }

    /**
     * Utility function, determines whether a field is a data field.
     * Data fields are fields mentioned explicitly in the builder configuration file.
     * These can include virtual fields.
     * Fields number, owner, and ottype, and the relation fields snumber,dnumber, rnumber, and dir
     * are excluded; these fields should be handled through the attributes of Element.
     * @param node  the MMBase node that owns the field (or null)
     * @param f The field to check
     */
    public static boolean isDataField(NodeManager nodeManager, Field field) {
        String fieldName = field.getName();
        return (nodeManager.hasField(fieldName)) && // skip temporary fields
               (!"owner".equals(fieldName)) && // skip owner/otype/number fields!
               (!"otype".equals(fieldName)) &&
               (!"number".equals(fieldName)) &&
               (!"snumber".equals(fieldName)) &&
               (!"dnumber".equals(fieldName)) &&
               (!"rnumber".equals(fieldName)) &&
               (!"dir".equals(fieldName));
    }

    /**
     * Utility function, determines whether a field is a editable field.
     * Editable fields are fields whose value could be input by user.
     * @param nodeManager
     * @param field
     * @return
     */
    public static boolean isEditableField(NodeManager nodeManager, Field field) {
        return isDataField(nodeManager, field) && field.getState() == Field.STATE_PERSISTENT;
    }

    /**
     * Utility function, determines whether a field is a data field.
     * Data fields are fields mentioned explicitly in the builder configuration file.
     * These can include virtual fields.
     * Fields number, owner, and ottype, and the relation fields snumber,dnumber, and rnumber
     * are excluded; these fields should be handled through the attributes of Element.
     * @param node  the MMBase node that owns the field
     * @param fname The name of the field to check
     */
    public static boolean isDataField(NodeManager nodeManager, String fieldName) {
        return nodeManager.hasField(fieldName);
    }

    /**
     * Utility function, determines whether a field is a editable field.
     * editable fields are fields whose value could be input by user.
     * @param nodeManager
     * @param fieldName
     * @return
     */
    public static boolean isEditableField(NodeManager nodeManager, String fieldName) {
        return isDataField(nodeManager, fieldName) && nodeManager.getField(fieldName).getState() == Field.STATE_PERSISTENT;
    }
    
    public static BaseData findNodeByXpath(BaseData baseData, String xpath, boolean recursive) {
        // this method is used to add xpath support to POJO, the following is some xpath examples 
        // which should be support by this method. All the samples below come from previous projects
        // 1) fdatapath=".//object[@type='images']"
        // 2) fdatapath="object/relation/object[@type='images']"
        // 3) fdatapath="object/field[@name='start']"
        // 4) fdatapath="field[@name=&apos;menu_type&apos;]"  notic: &apos;--> '
        if (xpath == null) {
            return null;
        }
        BaseData result = null;
        if (xpath.startsWith("..")) {
            if (baseData.getParent()!=null) {
                log.error("could not find parent(..) node because the current node has no parent");
                return null;
            } else if (xpath.startsWith("..//")) {
                //find node in all descendants of the parent, recursive into children's children
                return findNodeByXpath(baseData.getParent(),xpath.substring(4),true);
            } else if (xpath.startsWith("../")) {
                //find node in the parent node
                return findNodeByXpath(baseData.getParent(),xpath.substring(3),false);
            } else {
                //find field in the parent node
                return findNodeByXpath(baseData.getParent(),xpath.substring(2),false);
            }

        } else if (xpath.startsWith(".")) {
            if (xpath.startsWith(".//")) {
                //find node in all descendants of the current node, recursive into children's children
                return findNodeByXpath(baseData,xpath.substring(3),true);
            } else if (xpath.startsWith("./")) {
                //find node in all children of the current node
                return findNodeByXpath(baseData,xpath.substring(2),false);
            } else {
                //find field in the current node
                return findNodeByXpath(baseData,xpath.substring(1),false);
            }
            
        } else if (xpath.startsWith("//")) {
            //find node in all descendants of the root, recursive into children's children
            BaseData root = baseData.getRoot();
            return findNodeByXpath(root,xpath.substring(2),true);
            
        } else if (xpath.startsWith("/")) {
            //find node in all children of the root
            BaseData root = baseData.getRoot();
            return findNodeByXpath(root,xpath.substring(1),false);
            
        } else if (xpath.startsWith("object")) {
            if (baseData instanceof ObjectData) {
                result = null;
                log.error("it is not allowed that find object under object");
            } else {
                RelationData relation = (RelationData)baseData;
                ObjectData object = relation.getRelatedObject();
                if (xpath.startsWith("object//")) {
                    //find node in all descendants of the current node, recursive into children's children
                    result = findNodeByXpath(object,xpath.substring(8),true);
                } else if (xpath.startsWith("object/")) {
                    //find field in related object
                    result = findNodeByXpath(object,xpath.substring(7),recursive);
                } else {
                    //find field in related object
                    result = findNodeByXpath(object,xpath.substring(6),recursive);
                }
            }
        } else if (xpath.startsWith("relation")) {
            if (baseData instanceof RelationData) {
                log.error("it is not allowed that find relation under relation");
                result=null;
            } else {
                ObjectData object = (ObjectData)baseData;
                if (xpath.startsWith("relation//")) {
                    //find node in all descendants of the current node, recursive into children's children
                    result = findNodeByXpath(object,xpath.substring(10),true);
                } else if (xpath.startsWith("relation/")) {
                    //find field in related object
                    result = findNodeByXpath(object,xpath.substring(9),recursive);
                } else {
                    //find field in related object
                    result = findNodeByXpath(object,xpath.substring(8),recursive);
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
                if ("object".equals(node) && baseData instanceof RelationData) {
                    RelationData relation = (RelationData)baseData;
                    ObjectData object = relation.getRelatedObject();
                    if (attrValue!=null && attrValue.equals(object.getAttribute(attrName))) {
                        result = baseData;
                    }  
                } else if ("relation".equals(node) && baseData instanceof ObjectData) {
                    ObjectData object = (ObjectData)baseData;
                    List relationList = object.getRelationDataList();
                    for (int i=0;i<relationList.size();i++) {
                        RelationData relation = (RelationData)relationList.get(i);
                        if (attrValue!=null && attrValue.equals(relation.getAttribute(attrName))) {
                            result = baseData;
                            break;
                        }  
                    }
                } else {
                    if (attrValue!=null && attrValue.equals(baseData.getAttribute(attrName))) {
                        result = baseData;
                    }  
                }
            }
        } else {
            log.error("the xpath "+xpath+" is not point to a node "+baseData.getType());
        }
        if (result==null && recursive) {
            if (baseData instanceof RelationData) {
                RelationData relation = (RelationData)baseData;
                ObjectData object = relation.getRelatedObject();
                result = findNodeByXpath(object,xpath,recursive);
            } else if (baseData instanceof ObjectData) {
                ObjectData object = (ObjectData)baseData;
                List relationList = object.getRelationDataList();
                for (int i=0;i<relationList.size();i++) {
                    RelationData relation = (RelationData)relationList.get(i);
                    result = findNodeByXpath(relation,xpath,recursive);
                    if (result!=null) {
                        break;
                    }
                }
                
            }
        }
        return result;
    }

    /**
     * find field int this node, according to the specified xpath.
     * @param xpath
     * @return
     */
    public static FieldData findFieldByXpath(BaseData baseData,String xpath) {
        if (xpath == null) {
            return null;
        }
        // the following scenario is supported by this method
        // 1)<field fdatapath="field[@name=&apos;pos&apos;]"
        // 2)<field fdatapath=".//object[@type='images']" ftype="image"> this will not support
        // 3)<field fdatapath="object/relation/object[@type='images']" ftype="image"/> this will not support
        // 4)<field name="name" fdatapath="object/field[@name='title']" ftype="data" /> fdatapath will be preceding
        if (xpath.startsWith("field")) {
            // xpath = field[@name='fieldname']
            Pattern pattern = Pattern.compile("field\\[\\@name=['\"](\\w+)['\"]\\]");
            Matcher matcher = pattern.matcher(xpath);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                if ("".equals(fieldName)==false) {
                    return baseData.findFieldByName(fieldName);
                }
            }
            return null;
        }
        Matcher matcher = XPATH_PATTERN_FIELD.matcher(xpath);
        if (matcher.find()) {
            String fieldXPath = matcher.group(6);
            String nodeXPath = null;
            if (matcher.end(1)>0) {
                nodeXPath = xpath.substring(0,matcher.end(1));
                BaseData node = findNodeByXpath(baseData,nodeXPath,false);
                if (node!=null) {
                    if (fieldXPath!=null) {
                        return findFieldByXpath(node,fieldXPath);
                    }
                    if ("images".equals(baseData.getType()) ) {
                        //if image object and not specify field's name, it implies "handler" field
                        return baseData.findFieldByName("handler");
                    }
                    log.error("must specify field's name for object "+baseData.getType());
                }
            }
        } else {
            log.error("xpath "+xpath+" is not a valid path");
        }
        return null;
    }
    
    /**
     * create a new data identify number
     * @return
     */
    public static synchronized String getDataId(){
        String dataId = "d_" + (++dataObjectSerialNo);
        return dataId;
    }

    public static Node getNode(Node parentNode, BaseData baseData) {
        
        if (baseData==null) {
            return null;
        }
        if (baseData instanceof ObjectData) {
            return createNode(parentNode,(ObjectData)baseData);
        }
        if (baseData instanceof RelationData) {
            return createNode(parentNode,(RelationData)baseData);
        }
        return null;
    }
    
    private static Node createNode(Node parentNode, ObjectData object) {
        Node objectNode = XmlUtil.createAndAppendNode(parentNode,"node",null);
        XmlUtil.setAttribute(objectNode,"did",object.getDid());
        XmlUtil.setAttribute(objectNode,"number",object.getNumber());
        XmlUtil.setAttribute(objectNode,"type",object.getType());
        
        createFields(objectNode,object.getFieldDataList());
        
        List relationList = object.getRelationDataList();
        for (int i=0;i<relationList.size();i++) {
            RelationData relationData = (RelationData)relationList.get(i);
            createNode(objectNode,relationData);
        }
        
        return objectNode;
    }
    
    private static Node createNode(Node parentNode, RelationData relation) {
        
        Node relationNode = XmlUtil.createAndAppendNode(parentNode,"relation",null);
        XmlUtil.setAttribute(relationNode,"did",relation.getDid());
        XmlUtil.setAttribute(relationNode,"number",relation.getNumber());
        XmlUtil.setAttribute(relationNode,"type",relation.getType());
        XmlUtil.setAttribute(relationNode,"role",relation.getRole());
        XmlUtil.setAttribute(relationNode,"source",relation.getSource());
        XmlUtil.setAttribute(relationNode,"destination",relation.getDestination());
        
        createFields(relationNode,relation.getFieldDataList());
        
        createNode(relationNode,relation.getRelatedObject());
        
        return relationNode;
    }
    
    private static void createFields(Node parentNode,List fieldList) {
        
        for (int i=0;i<fieldList.size();i++) {
            FieldData field = (FieldData)fieldList.get(i);
            Node fieldNode = XmlUtil.createAndAppendNode(parentNode,"field",null);
            XmlUtil.setAttribute(fieldNode,"did",field.getDid());
            XmlUtil.setAttribute(fieldNode,"name",field.getName());
            XmlUtil.setAttribute(fieldNode,"type",field.getType());
            XmlUtil.setAttribute(fieldNode,"value",field.getStringValue());
        }
    }
    
    /**
     * find object or relation under current node by specified did.
     * @param did
     * @return
     */
    public static BaseData findDataById(BaseData fromNode, String did) {
        if (fromNode.getDid().equals(did)) {
            return fromNode;
        }
        for (Iterator iterator=fromNode.getChildren().iterator();iterator.hasNext();) {
            BaseData child = (BaseData)iterator.next();
            BaseData result = findDataById(child,did);
            if (result!=null) {
                return result;
            }
        }
        return null;
    }

    /**
     * 
     * @param baseData
     * @param fdatapath
     * @return
     */
    public static FieldData getField(BaseData baseData, String fdatapath) {
        Matcher matcher = XPATH_PATTERN_FDATAPATH.matcher(fdatapath);
        String datafrom = null;
        String fieldName = null;
        if (matcher.find()) {
            datafrom = matcher.group(2);
            fieldName = matcher.group(3);
        } else {
            // if not suitable for pattern, fdatapath is fieldname
            fieldName = fdatapath;
        }
        RelationData relation = null;
        ObjectData object = null;
        if (baseData instanceof RelationData) {
            relation = (RelationData)baseData;
            object = relation.getRelatedObject();
        } else if (baseData instanceof ObjectData){
            object = (ObjectData)baseData;
        } else {
            // this scenario should not occur
            log.error("unknow data object's type");
            return null;
        }
        FieldData field = null;
        if ("object".equals(datafrom)==false && relation!=null) {
            // if data is relation and not specified object/, get field from relation
            field = relation.findFieldByName(fieldName);
        }
        if (field==null && object!=null) {
            // get field from object
            field = object.findFieldByName(fieldName);
        }
        return field;
    }
    
    /**
     * 
     * @param baseData
     * @param fdatapath
     * @return
     */
    public static String getFieldValue(BaseData baseData, String fdatapath) {
        Matcher matcher = XPATH_PATTERN_FDATAPATH.matcher(fdatapath);
        String datafrom = null;
        String fieldName = null;
        if (matcher.find()) {
            datafrom = matcher.group(2);
            fieldName = matcher.group(3);
        } else {
            // if not suitable for pattern, fdatapath is fieldname
            fieldName = fdatapath;
        }
        RelationData relation = null;
        ObjectData object = null;
        if (baseData instanceof RelationData) {
            relation = (RelationData)baseData;
            object = relation.getRelatedObject();
        } else if (baseData instanceof ObjectData){
            object = (ObjectData)baseData;
        } else {
            // this scenario should not occur
            log.error("unknow data object's type");
            return null;
        }
        String value = null;
        if ("object".equals(datafrom)==false && relation!=null) {
            // if data is relation and not specified object/, get field from relation
            FieldData field = relation.findFieldByName(fieldName);
            if (field!=null) {
                value = field.getStringValue();
            } else {
                value = relation.getAttribute(fieldName);
            }
        }
        if (value==null && object!=null) {
            // get field from object
            FieldData field = object.findFieldByName(fieldName);
            if (field!=null) {
                value = field.getStringValue();
            } else {
                value = object.getAttribute(fieldName);
            }
        }
        
        return value;
    }
}
