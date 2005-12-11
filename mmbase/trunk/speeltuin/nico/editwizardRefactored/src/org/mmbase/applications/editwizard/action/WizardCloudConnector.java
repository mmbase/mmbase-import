/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.applications.editwizard.action;


import java.util.*;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.data.*;
import org.mmbase.applications.editwizard.schema.*;
import org.mmbase.applications.editwizard.util.XmlUtil;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class WizardCloudConnector {

    // logging
    private static final Logger log = Logging.getLoggerInstance(WizardCloudConnector.class);

    private Cloud cloud = null;

    /**
     * get the instance by provide cloud instance.
     * @param cloud the could instance.
     * @return 
     */
    public static WizardCloudConnector getInstance(Cloud cloud) {
        WizardCloudConnector connector = new WizardCloudConnector();
        connector.cloud = cloud;
        return connector;
    }

    /**
     * provide private constructor. the instance of this class should be 
     * create by use singletone method "getInstance".
     *
     */
    private WizardCloudConnector() {
        // use getInstance
    }
    

    /**
     * Retrieves an MMBase node, comment by yu,
     * @param fieldElmList
     * @param relationElmList
     * @param objectNumber
     * @return
     * @exception
     */
//    public ObjectData load(ObjectElm objectElm, int objectNumber) {
//        Node node = cloud.getNode(objectNumber);
//        if (node==null) {
//            //TODO: should add Exception here
//            throw new RuntimeException("object which number is "+objectNumber+" is not found!");
//        }
//        return loadNode(objectElm,node);
//    }
    
    /**
     * Retrieves an MMBase node with the oalias name;
     * @param objectElm
     * @param objectNumber
     * @return
     */
    public ObjectData load(ObjectElm objectElm,String objectNumber){
    	Node node = null;
    	try {
			int intObjectNumber = Integer.parseInt(objectNumber);
			node = cloud.getNode(intObjectNumber);
		} catch (NumberFormatException e) {
			// maybe use the oalias name
			node = cloud.getNode(objectNumber);
		}
    	if(node==null){
    		throw new RuntimeException("object which oalias is "+objectNumber+" is not found!");   		
    	}
    	return loadNode(objectElm,node);    	
    }

    /**
     * Retrieves an MMBase node
     * @param fieldElmList
     * @param relationElmList
     * @param objectNumber
     * @return
     * @exception
     */
    public ObjectData loadNode(ObjectElm objectElm, String objectNumber) {
        //get node by object number
        Node node = cloud.getNode(objectNumber);
        if (node==null) {
            //TODO: should add Exception here
            throw new RuntimeException("object which number is "+objectNumber+" is not found!");
        }
        return loadNode(objectElm,node);
    }
    
    /**
     * get object data from node
     * @param objectElm
     * @param node
     * @return
     */
    private ObjectData loadNode(ObjectElm objectElm, Node node) {

        List fieldElmList = null;
        List relationElmList = null;
        if (objectElm!=null) {
            fieldElmList = objectElm.fields;
            relationElmList = objectElm.relations;
        }
        //get object data
        return loadNode(fieldElmList,relationElmList,node);
    }
    
    /**
     * Retrieves an MMBase node
     * @param fieldElmList
     * @param relationElmList
     * @param node
     * @return
     * @exception
     */
    private ObjectData loadNode(List fieldElmList, List relationElmList, Node node) {
            
        //create object data
        ObjectData objData = new ObjectData();
        loadProperties(objData,node);
        
        //fill object with field data
        loadFields(objData, fieldElmList, node);
        
        if (relationElmList!=null && relationElmList.size()>0) {
            //defined relation elements, load relation recursively.
            List relationDataList = getRelationDataList(relationElmList,node);
            if (relationDataList!=null) {
                objData.addRelationData(relationDataList);
            }
        }
        objData.setStatus(BaseData.STATUS_LOAD);
        return objData;
    }
    
    /**
     * fill the data into objects or relations.
     * @param data
     * @param node
     */
    private void loadProperties(BaseData data, Node node) {
        
        NodeManager nm = node.getNodeManager();
        //TODO: remember to do something before commit new node
        data.setNumber(getNumber(node));
        data.setType(nm.getName());
        data.setMayDelete(node.mayDelete());
        data.setMayWrite(node.mayWrite());
        
    }
    
    /**
     * get data from node and fill them into fields' data.
     * @param fieldElmList
     * @param node
     * @return
     */
    private void loadFields(BaseData baseData, List fieldElmList, Node node) {
        NodeManager nm = node.getNodeManager();
        //TODO: should add code here
        if (fieldElmList==null || fieldElmList.size()==0) {
            //the names of fields are not specified. get all fields
            for (FieldIterator i = nm.getFields(NodeManager.ORDER_CREATE).fieldIterator(); i.hasNext(); ) {
                Field field = i.nextField();
                String fieldName = field.getName();
                if (DataUtils.isDataField(nm,fieldName)) {
                    FieldData fieldData = getFieldData(node, field);
                    baseData.addField(fieldData);
                }
            }
        } else {
            for (int i=0;i<fieldElmList.size();i++) {
                // select all child tags, should be 'field'
                FieldElm fieldElm = (FieldElm)fieldElmList.get(i);
                String fieldName = fieldElm.getName();
                if ((fieldName == null) || (fieldName.equals(""))) {
                    //TODO: should throw exception here
                    throw new RuntimeException("name is required for field");
                } else if (DataUtils.isDataField(nm,fieldName)==false) {
                    //TODO: should throw exception here
                    throw new RuntimeException("field with name " + fieldName + " does not exist");
                } else {
                    //add field data
                    Field field = nm.getField(fieldName);
                    FieldData fieldData = getFieldData(node, field);
                    baseData.addField(fieldData);
                }
            }
        }
    }

    /**
     * @param node
     * @param field
     * @param fieldName
     * @return
     */
    private FieldData getFieldData(Node node, Field field) {
        int fieldType = field.getType();
        String fieldName = field.getName();
        FieldData fieldData = new FieldData();
        fieldData.setName(fieldName);
        fieldData.setType(DataUtils.getTypeDescription(fieldType));
        switch (fieldType) {
            case Field.TYPE_BINARY:
                //binary field
                //TODO: should binary file be cached?
                byte[] bytes = node.getByteValue(fieldName);
                fieldData.loadValue(bytes);
                break;
            case Field.TYPE_DATETIME :
                //date field
                //TODO: 
                Date date = node.getDateValue(fieldName);
                fieldData.loadValue(date);
                break;
            default:
                fieldData.loadValue(node.getStringValue(fieldName));
        }
        return fieldData;
    }
    
    /**
     * get relation data list.
     * @param relationElmList relation filter
     * @param node
     * @return
     */
    private List getRelationDataList(List relationElmList, Node node) {
        if (relationElmList==null || relationElmList.size()==0) {
            //TODO: should add support for all relation query? (I don't think so)
            return new ArrayList(); // empty list means: no relation related with this node was retrived.
        }
        //create index map for relation schema elements.
        List relationList = new ArrayList();
        for (int i=0;i<relationElmList.size();i++) {
            RelationElm relationElm = (RelationElm)relationElmList.get(i);
            String destinationType = relationElm.getDestination();
            if (destinationType == null || "".equals(destinationType)) {
                //TODO: should throw exception because destination is required attribute.
                continue;
            }
            
            relationList.addAll(loadRelations(relationElm,node));
        }
        return relationList;
    }
    
    /**
     * get relation list with specified role and destination type of the node
     * @param relationElm the element specify role and destination type
     * @param node
     * @return
     */
    private List loadRelations(RelationElm relationElm, Node node) {
        List relationList = new ArrayList();
        // determine attributes of the search
        String role = relationElm.getRole();
        if ("".equals(role)) {
            role=null;
        }
        String destination = relationElm.getDestination();
        if ("".equals(destination)) {
            destination=null;
        }
        int searchDir = 0;
        String searchDirs = relationElm.getSearchDir();
        if("destination".equalsIgnoreCase(searchDirs)) {
            searchDir = 1;
        } else if("source".equalsIgnoreCase(searchDirs)) {
            searchDir = 2;
        }
        // determines whether to restrict what is loaded from the object
        int thisNumber = node.getNumber();
        for (RelationIterator ri = node.getRelations(role,destination).relationIterator(); ri.hasNext(); ) {
            Relation relation = ri.nextRelation();
            if (searchDir == 1 && thisNumber != relation.getIntValue("snumber")) continue;
            if (searchDir == 2 && thisNumber != relation.getIntValue("dnumber")) continue;
            RelationData relationData = new RelationData();
            relationData.setNumber(getNumber(relation));
            // load relation's attributes
            loadProperties(relationData,relation);
            // load fields of relation
            loadFields(relationData, relationElm.fields,relation);
            if (role != null) {
                relationData.setRole(role);
            } else {
                relationData.setRole(relation.getRelationManager().getForwardRole());
            }
            relationData.setSource("" + relation.getIntValue("snumber"));
            relationData.setDestination("" + relation.getIntValue("dnumber"));
            relationData.setStatus(BaseData.STATUS_LOAD);
            
            //get related object's number
            String otherNumber;
            if (thisNumber == relation.getIntValue("snumber")) {
                otherNumber = relation.getStringValue("dnumber");
            } else {
                otherNumber = relation.getStringValue("snumber");
            }
            // get related object's data
            ObjectData relatedNode = load(relationElm.object,otherNumber);
            relationData.setRelatedObject(relatedNode);
            relationList.add(relationData);
        }
        return relationList;
    }
    
    /**
     * This method can create a object (or a tree of objects and relations)
     *
     * @param parent The place where the results should be appended.
     * @param createAction The craete action.
     * @param params The params to use when creating the objects and relations.
     * @return The resulting object(tree) node.
     * @throws WizardException if the object cannot be created
     */
    public ObjectData create(ObjectElm objectElm, Map params) throws WizardException {
        return loadNew(objectElm, params);
    }

    /**
     * This method can create a object (or a tree of objects and relations)
     *
     * this method should be called from the wizard if it needs to create a new
     * combination of fields, objects and relations.
     * See further documentation or the samples for the definition of the action
     *
     * in global: the Action node looks like this: (Note: this function expects the &lt;object/&gt; node,
     * not the action node.
     * <pre>
     * &lt;action type="create"&gt;
     *   &lt;object type="authors"&gt;
     *     &lt;field name="name"&gt;Enter name here&lt;/field&gt;
     *     &lt;relation role="related" [destination="234" *]&gt;
     *       &lt;object type="locations"&gt;
     *         &lt;field name="street"&gt;Enter street&lt;/field&gt;
     *       &lt;/object&gt;
     *     &lt;/relation&gt;
     *   &lt;/object&gt;
     * &lt;/action&gt;
     * </pre>
     * *) if dnumber is supplied, no new object is created (shouldn't be included in the relation node either),
     *  but the relation will be created and directly linked to an object with number "dnumber".
     *
     * @param parent The place where the results should be appended.
     * @param createAction The create action.
     * @param params The params to use when creating the objects and relations.
     * @param createOrder ordernr under which this object is added (i.e. when added to a list)
     *                     The first ordernr in a list is 1
     * @return The resulting object(tree) node.
     * @throws WizardException if the object cannot be created
     */
    public ObjectData loadNew(ObjectElm objectElm, Map params) throws WizardException {

        // node name
        String objectType = objectElm.getType();

        if (objectType.equals("")) {
            throw new WizardException("No 'type' attribute used in object under action element");
        }
        if (log.isDebugEnabled()) log.debug("Create object of type " + objectType);

        // create a new object of the given type
        ObjectData newObject = loadNew(objectType);
        if (newObject != null) {
            //fill default value of fields.
            fillFields(newObject,objectElm.fields,params);

            // Let's see if we need to create new relations (maybe even with new objects inside...
            for (int i = 0; i < objectElm.relations.size(); i++) {
                RelationElm relationElm = (RelationElm)objectElm.relations.get(i);
                RelationData relationData = createRelation(newObject, relationElm, params);
                newObject.addRelationData(relationData);
            }
            return newObject;
        }
        else {
            throw new WizardException("Failed to load new object");
        }
    }
    
    /**
     * create new related object and new relation
     * @param sourceObject
     * @param relationElm
     * @param params
     * @return
     * @throws WizardException
     */
    private RelationData createRelation(ObjectData sourceObject, RelationElm relationElm, Map params) throws WizardException {
        String role = relationElm.getRole();
        if (role==null) {
            role = "related";
        }
        // determine destination (dnumber can be null)
        String createDir = relationElm.getCreateDir();
        if (createDir==null) {
            createDir = "either";
        }
        
        ObjectElm relatedObjectElm = relationElm.object;
        ObjectData relatedObject = null;
        if (relationElm.object == null) {
            String destinationType = relationElm.getDestination();
            if (destinationType==null || destinationType.equals("")) {
                throw new WizardException("No 'type' attribute used in object under create action");
            }
            if (log.isDebugEnabled()) {
                log.debug("Create object of type " + destinationType);
            }
            // create a new object of the given type
            relatedObject = loadNew(destinationType);
        } else {
            relatedObject = create(relatedObjectElm, params);
        }
        RelationData relationData = loadNewRelation(sourceObject, relatedObject, role, createDir);
        if (relationData != null) {
            //fill default value of fields.
            fillFields(relationData,relationElm.fields,params);
            relationData.setRelatedObject(relatedObject);
            return relationData;
        }
        else {
            throw new WizardException("Failed to load new relationData");
        }
    }

    /**
     * This method gets a new temporarily object of the given type.
     *
     * @param parent The place where the results should be appended.
     * @param objecttype The objecttype which should be created.
     * @return The resulting object node.
     * @throws WizardException if the node could not be created
     */
    public ObjectData loadNew(String objectType) {
        NodeManager nodeManager = cloud.getNodeManager(objectType);
        Node node = nodeManager.createNode();
        ObjectData objectData = null;
        try {
            objectData = loadNode(null,node);
        } 
        finally {
            node.cancel();
        }
        objectData.setStatus(BaseData.STATUS_NEW);
        return objectData;
    }

    /**
     * This method creates a new temporarily relation.
     *
     * @param parent The place where the results should be appended.
     * @param sourceObjectNumber the number of the sourceobject
     * @param destinationObjectNumber the number of the destination object
     * @param roleName The name of the role the new relation should have.
     * @param createDir Direction of the relation to create
     * @return The resulting relation node.
     * @throws WizardException if the relation could not be created
     */
    public RelationData loadNewRelation(ObjectData source, ObjectData destination, 
            String roleName, String createDir) throws WizardException {
        // if both types are given, use these as a constraint for the Relationmanager
        if (roleName==null || "".equals(roleName)) {
            throw new IllegalArgumentException("role required for getrelations");
        }
        RelationData relationData = new RelationData();
        int createDirection = Queries.getRelationStepDirection(createDir);
        String destinationType = destination.getType();
        if (destinationType==null) {
            destinationType = "";
        }
        String sourceType = source.getType();
        if (sourceType==null) {
            sourceType = "";
        }

        //TODO: should we swap the type when action[@type="add" && @createdir="source"]?
        if (createDirection == RelationStep.DIRECTIONS_SOURCE) {
            String swapType = destinationType;
            destinationType = sourceType;
            sourceType = swapType;
        } 
        try {
            // if both types are given, use these as a constraint for the Relationmanager
            RelationManager nm;
            if (destinationType.equals("") || sourceType.equals("") ) {
                nm =cloud.getRelationManager(roleName);
            } else {
                nm =cloud.getRelationManager(sourceType,destinationType,roleName);
            }
            Node node = nm.createNode();
            try {
                loadProperties(relationData,node);
                
                if (createDirection == RelationStep.DIRECTIONS_SOURCE) {
                    log.debug("Creating relation in the INVERSE direction");
                    relationData.setDestination(source.getNumber());
                    relationData.setSource(destination.getNumber());
                } else {
                    log.debug("Creating relation in the NORMAL direction");
                    relationData.setDestination(destination.getNumber());
                    relationData.setSource(source.getNumber());
                }
                relationData.setRole(roleName);
                loadFields(relationData,null,node);
            } finally  {
                node.cancel();  // have to cancel node !
            }
            relationData.setRelatedObject(destination);
            relationData.setStatus(BaseData.STATUS_NEW);
        } catch (RuntimeException e) {
            if (destinationType.equals("") || sourceType.equals("") ) {
                throw new WizardException("role ("+roleName+") does not exist");
            } else {
                throw new WizardException("relation ("+sourceType+"-"+roleName+"->"+destinationType+") constraint does not exist");
            }
        }
        return relationData;
    }
    
    /**
     * Adds or replaces values specified for fields in the wizard to a recently created node.
     * @param parent The place where the results should be appended.
     * @param loadAction The load action.
     * @param nodeData The new object
     * @param params The parameters to use when creating the objects and relations.
     * @param createOrder ordernr under which this object is added (i.e. when added to a list)
     *                     The first ordernr in a list is 1
     */
    private void fillFields(BaseData objectData, List fieldElmList, 
            Map params)  throws WizardException {
        // fill-in (or place) defined fields and their values.
        for (int i = 0; i < fieldElmList.size(); i++) {
            FieldElm fieldElm = (FieldElm)fieldElmList.get(i);
            String fieldName = fieldElm.getName();
            // does this field already exist?
            FieldData fieldData = objectData.findFieldByName(fieldName);
            if (fieldData == null) {
                // Non-existing field (getNew/getNewRelation always return all fields)
                throw new WizardException("field " + fieldName + " does not exist in '" 
                        + fieldElm.getParent().getAttribute(SchemaKeys.ATTR_TYPE) + "'");
            }
            String value = fieldElm.getDefaultValue();
            // if you add a list of items, the order of the list may be of import.
            // the variable $pos is used to make that distinction
            //TODO: how to set pos value?
//            params.put("pos",createOrder+"");
//            Node root = repository.getDocumentElement();
//            if (log.isDebugEnabled()) log.debug("root="+root.toString());
//            value = Utils.transformAttribute(root,value,false,params);
//            params.remove("pos");
            if (value == null) {
                value = "";
            }
            fieldData.setValue(XmlUtil.fillInParams(value, params)); // process innerText: check for params
            //TODO: need set to mmbase?
//            node.setValue(fieldName, value);
        }
    }
    
    /**
     * delete node in mmbase
     * @param objectNumber object 
     * @throws WizardException 
     */
    public void delete(int objectNumber) throws WizardException {
        try {
            Node node = cloud.getNode(objectNumber);
            node.delete(true);
            node.commit();
        } catch (NotFoundException e) {
            //TODO: handle exception: be deleted by other session
            throw new WizardException("the node which number is "+objectNumber+" doesn't exists");
        }
    }
    
    /**
     * Passes changes to make to MMBase
     * @param
     * @throws WizardException 
     */
    public void save(ObjectData objectData) throws WizardException {
        Transaction trans = cloud.createTransaction();
        try {
            putObject(objectData);
            trans.commit();
            log.debug("success in saving data to mmbase success, confirm the transaction");
        } catch (Exception e) {
            log.error("error in save data to mmbase, cancel the transaction");
            trans.cancel();
            if (e instanceof WizardException) {
                throw (WizardException)e;
            }
            //TODO: handle exception
            throw new WizardException(e.getMessage());
        }
    }
    
    /**
     * put object data into mmbase
     * @param objectData
     * @throws WizardException
     */
    private void putObject(ObjectData objectData) throws WizardException {
        switch (objectData.getStatus()) {
            case BaseData.STATUS_NEW:
                putNew(objectData);
                objectData.setStatus(BaseData.STATUS_LOAD);
                break;
            case BaseData.STATUS_DELETE:
                putDelete(objectData);
                break;
            case BaseData.STATUS_CHANGE :
                //TODO: the object be changed must be load first, so just recogenize load status
            case BaseData.STATUS_LOAD :
                //TODO: save object data to system. should we distinguish load/change status here?
                putChange(objectData);
                objectData.setStatus(BaseData.STATUS_LOAD);
                break;
            default:
                throw new WizardException("unknown status of the object data");
        }
        putRelations(objectData);
    }
    
    /**
     * put new object into mmbase
     * @param objectData
     * @return
     * @throws WizardException
     */
    private int putNew(ObjectData objectData) throws WizardException {
        try {
            NodeManager nm = cloud.getNodeManager(objectData.getType());
            Node newNode = nm.createNode();
            fillFields(newNode,objectData);
            try {
                //TODO: should this will be perform by mmbase automatically?
                newNode.setContext(cloud.getUser().getIdentifier());
                newNode.commit();
                
                int number = newNode.getNumber();
                // use object's permanent number to replace temporary number
                loadProperties(objectData,newNode);
                // update object's fields' value
//                loadFields(objectData,null,newNode);
//                objectData.setNumber(number+"");
                if (log.isServiceEnabled()) log.service("Created new node " + number);
                return number;
            } catch (RuntimeException e) {
                // give error
                //TODO: handle exception here
                throw new WizardException("Cloud can not insert this object, alias number : "+objectData.getType() + "(" + e.toString() + ")");
            }
        } catch (BridgeException e) {
            // give error this cloud doesn't support this type
            // TODO: handle exception here
            throw new WizardException(Logging.stackTrace(e));
        }
    }
    
    /**
     * delete node from mmbase.
     * @param deleteNode
     * @return
     * @throws WizardException 
     */
    private void putDelete(ObjectData deleteNode) throws WizardException {
        // check if this original node is also found in
        // mmbase cloud and if its still the same
        // also check if its the same type
        String type = deleteNode.getType();
        String number = deleteNode.getNumber();
        NodeManager nm = null;
        try {
            nm = cloud.getNodeManager(type);
        } catch(RuntimeException e) {
            // give error can't find builder of that type
            //TODO: message should be "Cloud does not support type : "+type + "(" + e.toString() + ")"
            throw new RuntimeException("Cloud does not support type : " + type 
                    + "(" + e.toString() + ")");
        }
        try {
            Node mmbaseNode = cloud.getNode(deleteNode.getNumber());
            if (mmbaseNode.getNodeManager().getName().equals(nm.getName()) ) {
                mmbaseNode.delete(true);
                mmbaseNode.commit();
                return;
            } else {
                // give error its the wrong type
                //TODO: message should be "Node not same type as in the cloud, node number : "+alias+", cloud type="+mmbaseNode.getNodeManager().getName()+", expected="+nm.getName()+")"
                throw new RuntimeException("Node not same type as in the cloud, node number : "
                        + number + ", cloud type=" + mmbaseNode.getNodeManager().getName()
                        + ", expected=" + nm.getName() + ")");
            }
        } catch(RuntimeException e) {
            // give error node not found
            //TODO: message should be "Node not in the cloud (anymore?), node number : "+alias + "(" + e.toString() + ")"
            throw new WizardException("Node not in the cloud (anymore?), node number : "
                    + deleteNode.getNumber() + "(" + e.toString() + ")");
        }
    }
    
    /**
     * put changed data of the object into mmbase
     * @param objectData
     * @throws WizardException
     */
    private void putChange(ObjectData objectData) throws WizardException{
        // now check if this org node is also found in
        // mmbase cloud and if its still the same
        // also check if its the same type
        try {
            NodeManager nm = cloud.getNodeManager(objectData.getType());
            try {
                Node mmbaseNode = cloud.getNode(objectData.getNumber());
                // check if they are the same type
                if (mmbaseNode.getNodeManager().getName().equals(nm.getName()) ) {
                    fillFields(mmbaseNode,objectData);
                    mmbaseNode.commit();
                    return;
                } else {
                    // give error its the wrong type
                    // TODO: handle exception
                    throw new WizardException("Node not same type as in the cloud, node number : "
                            + objectData.getNumber() + ", cloud type="
                            + mmbaseNode.getNodeManager().getName() + ", expected=" + nm.getName()
                            + ")");
                }

            } catch(RuntimeException e) {
                // give error node not found
                // TODO: handle exception
                log.error(Logging.stackTrace(e));
                throw new WizardException("Node not in the cloud (any more), node number : "
                        + objectData.getNumber() + "(" + e.toString() + ")");
            }
        } catch(RuntimeException e) {
            // give error can't find builder of that type
            //TODO: handle exception
            throw new WizardException("Cloud does not support type : " + objectData.getType() + "("
                    + e.toString() + ")");
        }
    }
    
    /**
     * put relation data of the object into mmbase
     * @param relationDataList
     * @return
     * @throws WizardException 
     */
    private void putRelations(ObjectData source) throws WizardException {
        if (source==null) {
            log.warn("try to save relations of null objects");
            return;
        }
        List relationList = new ArrayList();
        for (Iterator iterator = source.getRelationDataList().iterator();iterator.hasNext();) {
            RelationData relationData = (RelationData)iterator.next();
            putObject(relationData.getRelatedObject());
            switch (relationData.getStatus()) {
                case BaseData.STATUS_NEW:
                    putNewRelation(source, relationData);
                    relationData.setStatus(BaseData.STATUS_LOAD);
                    relationList.add(relationData);
                    break;
                case BaseData.STATUS_DELETE:
                    putDeleteRelation(relationData);
                    break;
                case BaseData.STATUS_CHANGE :
                    //TODO: some relation chould be change. for example: posrel's position
                case BaseData.STATUS_LOAD :
                    //TODO: Not change, ignore
                    putChangeRelation(relationData);
                    relationData.setStatus(BaseData.STATUS_LOAD);
                    relationList.add(relationData);
                    break;
                default:
                    throw new WizardException("unknown status of the object data");
            }
        }
        source.clearRelations();
        source.addRelationData(relationList);
    }
    
    /**
     * put new relation into mmbase
     * @param source
     * @param relationData
     * @throws WizardException
     */
    private void putNewRelation(ObjectData source, RelationData relationData) throws WizardException {
        ObjectData destination = relationData.getRelatedObject();
        try {
            RelationManager relman=cloud.getRelationManager(relationData.getRole());
            String sourcenumber = source.getNumber();
            String destinationnumber = destination.getNumber();
            if (log.isDebugEnabled()) {
                log.debug("Creating a relations between node " + sourcenumber + " and "
                        + destinationnumber);
            }
            Relation newnode = relman.createRelation(cloud.getNode(sourcenumber), cloud
                    .getNode(destinationnumber));
            // note that source and destination may be switched (internally) when you
            // commit a bi-directional relation, if the order of the two differs in typerel
            try {
                fillFields(newnode,relationData);
                //
                newnode.commit();
                int number = newnode.getNumber();
                if (log.isServiceEnabled()) {
                    log.service("Created new relation " + number);
                }
                relationData.setNumber(getNumber(newnode));
            } catch (RuntimeException e) {
                // give error
                // TODO: handle exception
                throw new WizardException("Cloud can not insert this object, alias number : "
                        + relationData.getNumber() + "(" + e.toString() + ")");
            }
        } catch (RuntimeException e) {
            // give error can't find builder of that type
            // TODO: handle exception
            throw new WizardException("Error. Does the cloud support role :"
                    + relationData.getRole() + "?:" + e.getMessage());
        }
    }

    /**
     * delete node
     * @param relationData
     * @param deleteRelatedNode
     * @throws WizardException
     */
    private void putDeleteRelation(RelationData relationData) throws WizardException{
        String role = relationData.getRole();
        try {
            RelationManager relman = cloud.getRelationManager(role);
            try {
                Node mmbaseNode = cloud.getNode(relationData.getNumber());
                // check if they are the same type
                if (mmbaseNode.getNodeManager().getName().equals(relman.getName())) {
                    mmbaseNode.delete(true);
                    return;
                } else {
                    // TODO: type is not correct. exception should replace by log.warning
                    throw new WizardException("Node not same type as in the cloud, node number : "
                            + relationData.getNumber() + ", cloud type="
                            + mmbaseNode.getNodeManager().getName() + ", expected="
                            + relman.getName() + ")");
                }
            } catch (RuntimeException e) {
                //TODO: cannot find. exception should replace by log.warning
                throw new WizardException(
                        "Relation not in the cloud (anymore?), relation number : "
                                + relationData.getNumber());
            }
        } catch (RuntimeException e) {
            // give error can't find builder of that type
            throw new WizardException("Cloud does not support role : "+role);
        }
    }

    /**
     * put changed relation data into mmbase
     * @param relationData
     * @throws WizardException 
     */
    private void putChangeRelation(RelationData relationData) throws WizardException {
        // now check if this org node is also found in
        // mmbase cloud and if its still the same
        // also check if its the same type
        try {
            NodeManager nm = cloud.getNodeManager(relationData.getType());
            try {
                Node mmbaseNode = cloud.getNode(relationData.getNumber());
                // check if they are the same type
                if (mmbaseNode.getNodeManager().getName().equals(nm.getName()) ) {
                    fillFields(mmbaseNode,relationData);
                    mmbaseNode.commit();
                    return;
                } else {
                    // give error its the wrong type
                    // TODO: handle exception
                    throw new WizardException("Node not same type as in the cloud, node number : "
                            + relationData.getNumber() + ", cloud type="
                            + mmbaseNode.getNodeManager().getName() + ", expected=" + nm.getName()
                            + ")");
                }

            } catch(RuntimeException e) {
                // give error node not found
                // TODO: handle exception
                log.error(Logging.stackTrace(e));
                throw new WizardException("Node not in the cloud (any more), node number : "
                        + relationData.getNumber() + "(" + e.toString() + ")");
            }
        } catch(RuntimeException e) {
            // give error can't find builder of that type
            //TODO: handle exception
            throw new WizardException("Cloud does not support type : " + relationData.getType() + "("
                    + e.toString() + ")");
        }
    }

    /**
     * fill node's field data
     * @param node
     * @param newData
     */
    private void fillFields(Node node, BaseData newData) {
        NodeManager nm = node.getNodeManager();
        List newFieldDataList = newData.getFieldDataList();
        for (Iterator iterator = newFieldDataList.iterator(); iterator.hasNext(); ) {
            FieldData fieldData = (FieldData)iterator.next();
            String fieldName = fieldData.getName();
            Field field = nm.getField(fieldName);
            
            if (newData.getStatus() == BaseData.STATUS_NEW) {
                //TODO: if status is new 
            } else if (!fieldData.isChanged()) {
                //TODO: if the value is not changed
                // we can determined whether binary field be changed by binaryversion 
                continue;
            } else if (newData.getStatus() != BaseData.STATUS_LOAD  && "binary".equals(field.getDataType().getName())==false) {
                //TODO: if the node's value had been changed by someone else.
                // XXX: currently, we do not validate on byte fields
                String originalValue = fieldData.getOldValue();
                String mmbaseValue;
                if (field.getType() ==  Field.TYPE_DATETIME) {
                    mmbaseValue = "" + node.getDateValue(fieldName).getTime();
                } else {
                    mmbaseValue = node.getStringValue(fieldName);
                }
                if ((originalValue != null) && !originalValue.equals(mmbaseValue)) {
                    //TODO: give error node was changed in cloud
                    throw new RuntimeException("Node was changed in the cloud, node number : "
                            + node.getNumber() + " field name " + fieldName + " value found: "
                            + mmbaseValue + "value expected: " + originalValue);
                }
            }
            if (DataUtils.isEditableField(node.getNodeManager(),field)) {
                //TODO: how to judge the type?
                if ("binary".equals(field.getDataType().getGUIName())){
                    BinaryData binaryData = fieldData.getBinaryData();
                    node.setValue(fieldName,binaryData.getData());
                } else {
                    node.setStringValue(fieldName,fieldData.getStringValue());
                }
                
            }
        }
    }
    
    /**
     * get object number, if the number is temporary, convert negative number to "n"+abs(number)
     * @param node
     * @return
     */
    private String getNumber(Node node){
        int number = node.getNumber();
        if (number<0) {
            // temporary object's number is negative
            return "n"+Math.abs(number);
        }
        return ""+number;
    }
    
}
