/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.RelationStep; // just for the search-constants.
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class handles all communition with mmbase. It uses the bridge to do the transactions and get the information
 * needed for rendering the wizard screens. It also maintains the repository.
 * The work space can connect to MMBase and get data, relations, and lists. It can also
 * store changes, create new objects and relations.
 *
 * The connector can be instantiated without the wizard class, but will usually be called from the Wizard class itself.
 *
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: WorkSpace.java,v 1.1 2005-03-22 08:44:07 pierre Exp $
 *
 */
public class WorkSpace {

    public static final String OBJECT = "object"; // confusing?
    public static final String FIELD = "field";
    public static final String RELATION = "relation";

    // keywords used for attributes
    public static final String ELM_NUMBER = "number";
    public static final String ELM_TYPE = "type";
    public static final String ELM_NAME = "name";
    public static final String ELM_ROLE = "role";
    public static final String ELM_DESTINATION = "destination";
    public static final String ELM_DESTINATIONTYPE = "destinationtype";
    public static final String ELM_SOURCETYPE = "sourcetype";
    public static final String ELM_SEARCHDIR = "searchdir";
    public static final String ELM_CREATEDIR = "createdir";
    public static final String ELM_XPATH = "xpath";
    public static final String ELM_WHERE = "where";
    public static final String ELM_ORDERBY = "orderby";
    public static final String ELM_DIRECTIONS = "directions";
    public static final String ELM_SOURCE = "source";
    public static final String ELM_HREF = "href";
    public static final String ELM_MAYWRITE = "maywrite";
    public static final String ELM_MAYDELETE = "maydelete";
    public static final String ELM_CONTEXT = "context";
    public static final String ELM_SIZE = "size";
    public static final String ELM_ENCODING = "encoding";

    protected final static String[] TYPE_DESCRIPTIONS = {
        "unknown", "string", "integer", "unknown", "byte", "float", "double", "long", "xml", "node", "datetime", "boolean", "list"
    };

    // logging
    private static final Logger log = Logging.getLoggerInstance(WorkSpace.class);

    // Counter for the data ID, to uniquely number nodes in the data xml for reference
    private int dataIDCounter = 1;

    // The cloud used by the wizard
    private Transaction transaction = null;
    private Document repository;
    private Map binaries = new HashMap();

    /**
     * Constructor: Creates the connector
     * @param cloud the cloud to use for this user
     */
    public WorkSpace() {
        repository = null;
    }

    public int getByteSize(org.mmbase.util.SizeOf sizeof) {
        return sizeof.sizeof(transaction) + sizeof.sizeof(repository) + sizeof.sizeof(binaries);
    }

    /**
     * Tags a node with a new data ID (did attribute).
     * The wizard uses the did to identify each datanode.
     */
    private void registerNode(Node node) {
        Utils.setAttribute(node, "did", "d_" + dataIDCounter++);
    }

    private String getTypeDescription(int type) {
       if (type >= 0 && type < TYPE_DESCRIPTIONS.length) {
            return TYPE_DESCRIPTIONS[type];
       } else {
            return TYPE_DESCRIPTIONS[0];
       }
    }

    /**
     * @javadoc
     */
    private org.mmbase.bridge.Node getAssociatedNode(String alias) {
         //if (alias.startsWith("n")) {
         //    alias = "-" + alias.substring(1); // assume negative numbers as temporary node numbers
         //}
         try {
             return transaction.getNode(Integer.parseInt(alias));
         } catch (NumberFormatException nfe) {
             return transaction.getNode(alias);
         }
    }

    /**
     * @javadoc
     */
    private org.mmbase.bridge.Node getAssociatedNode(Node nodeData) {
        return getAssociatedNode(Utils.getAttribute(nodeData,"number"));
     }

    /**
     * @javadoc
     */
    public NodeManager getNodeManager(String objectType) {
        return transaction.getNodeManager(objectType);
    }

    /**
     * Creates a DOM element which contains a Text Node, and adds it to the
     * specified node as a child.
     * Used for creating field and error tags.
     * @param tagname name of the new element
     * @param content content of the new element as a string
     * @param out the element to which to add the new Element.
     * @return the newly created element
     */
    protected Element addContentElement(String tagname, String content, Node parent) {
        Element el = parent.getOwnerDocument().createElement(tagname);
        registerNode(el);
        if (content == null) content="";
        Text tel = parent.getOwnerDocument().createTextNode(content);
        el.appendChild(tel);
        parent.appendChild(el);
        return el;
    }

    /**
     * Retrieves the first child node of an element that is itself an element.
     * If none exist, it returns <code>null</code>.
     * @param item the element to find the first child element of
     * @return the first child element, or <code>null</code>.
     */
    protected Element getFirstElement(Node item) {
        if (item==null) return null;
        item=item.getFirstChild();
        if ((item!=null) && !(item instanceof Element)) {
            item = getNextElement(item);
        };
        return (Element)item;
    }

    /**
     * Retrieves the first child node of an element that is itself an element, and has the specified tagname.
     * If none exist, it returns <code>null</code>.
     * @param item the element to find the first child element of
     * @param tagname the tagname of the element desired
     * @return the first child element, or <code>null</code>.
     */
    protected Element getFirstElement(Node item, String tagname) {
        Element elm = getFirstElement(item);
        if (elm != null && !elm.getTagName().equals(tagname)) {
            elm=getNextElement(elm,tagname);
        }
        return elm;
    }

    /**
     * Retrieves the next sibling of an element that is itself an element.
     * If none exist, it returns <code>null</code>.
     * @param item the element to find the sibling element of
     * @return the sibling element, or <code>null</code>.
     */
    protected Element getNextElement(Node item) {
        if (item==null) return null;
        do {
            item=item.getNextSibling();
        } while ((item!=null) && !(item instanceof Element));
        return (Element)item;
    }

    /**
     * Retrieves the next sibling of an element that is itself an element with a specified tag name.
     * If none exist, it returns <code>null</code>.
     * @param item the element to find the sibling element of
     * @param tagname the tagname of the element desired
     * @return the sibling element, or <code>null</code>.
     */
    protected Element getNextElement(Node item, String tagname) {
        if (item==null) return null;
        do {
            item=item.getNextSibling();
        } while ((item!=null) && (!(item instanceof Element)  || !((Element)item).getTagName().equals(tagname)));
        return (Element)item;
    }

    public String load(Element schema, String dataId, Map params, Cloud cloud) throws WizardException {
        transaction = cloud.createTransaction();
        repository = Utils.parseXML("<data />");
        if (dataId.equals("new")) {
            log.debug("Creating new xml");
            // Get the definition and create a copy of the object-definition.
            Node createAction = Utils.selectSingleNode(schema, "action[@type='create']");
            if (createAction == null) {
                throw new WizardException("You tried to start a create action in the wizard, but no create action was defined in the wizard schema. Please supply a <action type='create' /> section in the wizard.");
            }
            if (log.isDebugEnabled()) {
                log.debug("Creating a new object " + createAction.getNodeName() +
                          " of type " + Utils.getAttribute(createAction, "type"));
            }

            // We have to add the object to the data, so first determine to which parent it belongs.
            Node parent = repository.getDocumentElement();
            // Ask the database to create that object, ultimately to get the new id.
            Node nodeData = createObject(parent, createAction, params);

            if (nodeData == null) {
                throw new WizardException("Could not create new object. Did you forget to add an 'object' subtag?");
            }

            dataId = Utils.getAttribute(nodeData, "number");

            if (log.isDebugEnabled()) {
                log.debug("Created object " + nodeData.getNodeName() +
                          " of type " + Utils.getAttribute(nodeData, "type") +
                          " with id " + dataId);
            }
        } else {
            // load initial object using object number
            log.debug("Loading: " + dataId);
            Node loadAction = Utils.selectSingleNode(schema, "action[@type='load']");
            loadNode(repository.getDocumentElement(), dataId, loadAction);
        }
        return dataId;
    }

    public Document getRepository() {
        return repository;
    }

    public Node getMainObject() {
        return Utils.selectSingleNode(repository, "/data/*");
    }

    /**
     * Returns true if the node with the specified objectnumber can be edited
     * @param objectNumber number of the object to check
     * @throws WizardException if the object cannot be retrieved
     */
    protected boolean mayEditNode(String objectNumber) throws WizardException {
        org.mmbase.bridge.Node node = getAssociatedNode(objectNumber);
        return node != null && node.mayWrite();
    }

    /**
     * Returns true if the node with the specified objectnumber can be deleted
     * @param objectNumber number of the object to check
     * @throws WizardException if the object cannot be retrieved
     */
     protected boolean mayDeleteNode(String objectNumber) {
        org.mmbase.bridge.Node node = getAssociatedNode(objectNumber);
        return node != null && node.mayDelete();
    }

    /**
     * Utility function, determines whether a field is a data field.
     * Data fields are fields mentioned explicitly in the builder configuration file.
     * These can include virtual fields.
     * Fields number, owner, and ottype, and the relation fields snumber,dnumber, rnumber, and dir
     * are excluded; these fields should be handled through the attributes of Element.
     * @param node  the MMBase node that owns the field (or null)
     * @param fieldName name of the field to check
     */
    private boolean isDataField(org.mmbase.bridge.NodeManager nodeManager, String fieldName) {
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
     * This method stores a node's type information, rights, and fields in a DOM node.
     * if a load action specified, it may possibly limit the fields that are stored.
     * Some fields (such as bytefields and the number field) are never stored.
     *
     * @param parent The parent DOM node where the results should be appended to.
     * @param loadAction The node with loadAction data. Has information about what relations should be loaded and what fields should be retrieved.
     * @return The new relations (in the data object), or <code>null</code> if none.
     * @throws WizardException if loading the relations fails
     */
    public void loadNodeData(Node nodeData, org.mmbase.bridge.Node node, Node loadAction) throws WizardException {
        NodeManager nodeManager = node.getNodeManager();
        Utils.setAttribute(nodeData,ELM_TYPE, nodeManager.getName());
        Utils.setAttribute(nodeData,ELM_MAYWRITE, "" + node.mayWrite());
        Utils.setAttribute(nodeData,ELM_MAYDELETE, "" + node.mayDelete());
        // load fields
        Element field = null;
        if (loadAction!=null) {
            field = getFirstElement(loadAction, FIELD);
        }
        if (field == null) {
            for (FieldIterator i = nodeManager.getFields(NodeManager.ORDER_CREATE).fieldIterator(); i.hasNext(); ) {
                Field f = i.nextField();
                String fieldName = f.getName();
                if (isDataField(nodeManager,fieldName)) { // ???
                    Element fel;
                    int type = f.getType();
                    if (type == Field.TYPE_DATETIME || type == Field.TYPE_BOOLEAN) {
                        int intValue = node.getIntValue(fieldName);
                        if (intValue < 0) {
                            fel = addContentElement(FIELD, "", nodeData);
                        } else {
                            fel = addContentElement(FIELD, ""+intValue, nodeData);
                        }
                    } else if (type == Field.TYPE_BYTE) {
                        fel = addContentElement(FIELD, "", nodeData);
                        byte[] bytes = node.getByteValue(fieldName);
                        fel.setAttribute(ELM_SIZE, "" + (bytes != null ? bytes.length : 0));
                    } else {
                        fel = addContentElement(FIELD, node.getStringValue(fieldName), nodeData);
                    }
                    fel.setAttribute(ELM_TYPE, getTypeDescription(type));
                    fel.setAttribute(ELM_NAME, fieldName);
                }
            }
        } else {
            while (field != null) { // select all child tags, should be 'field'
                String fieldName = field.getAttribute(ELM_NAME);
                if ((fieldName == null) || (fieldName.equals(""))) {
                    throw new IllegalArgumentException("name required for field");
                } else if (isDataField(nodeManager,fieldName)) {
                    Element fel;
                    int type = nodeManager.getField(fieldName).getType();
                    if (type == Field.TYPE_BYTE) {
                        fel = addContentElement(FIELD, "", nodeData);
                        byte[] bytes = node.getByteValue(fieldName);
                        fel.setAttribute(ELM_SIZE, "" + (bytes != null ? bytes.length : 0));
                    } else {
                        fel = addContentElement(FIELD, node.getStringValue(fieldName), nodeData);
                    }
                    fel.setAttribute(ELM_TYPE, getTypeDescription(type));
                    fel.setAttribute(ELM_NAME, fieldName);
                } else {
                    throw new IllegalArgumentException("field with name " + fieldName + " does not exist");
                }
                field = getNextElement(field,FIELD);
            }
        }
        // load relations
        Element relationAction = getFirstElement(loadAction, RELATION);
        while (relationAction != null) { // select all child tags, should be 'relation'
            loadRelations(nodeData, node, relationAction);
            relationAction = getNextElement(relationAction, RELATION);
        }
    }

    /**
     * This method retrieves data (objectdata) from mmbase.
     *
     * @param parent Results are appended to this targetNode.
     * @param objectNumber The number of the object to load.
     * @param loadAction The node with loadAction data. Has information about what relations should be loaded and what fields should be retrieved.
     * @return The resulting node with the objectdata.
     * @throws WizardException if loading the object fails
     */
    public Node loadNode(Node parent, String objectNumber, Node loadAction) throws WizardException {
        org.mmbase.bridge.Node node = transaction.getNode(objectNumber);
        return loadNode(parent, node, loadAction);
    }

    /**
     * This method loads a node from MMBase and stores the result in a DOM node.
     *
     * @param parent The parent DOM node where the results should be appended to.
     * @param node The object to load.
     * @param loadAction The node with loadAction data. Has information about what relations should be loaded and what fields should be retrieved.
     * @return The new relations (in the data object), or <code>null</code> if none.
     * @throws WizardException if loading the relations fails
     */
    public Node loadNode(Node parent, org.mmbase.bridge.Node node, Node loadAction) throws WizardException {
        Element nodeData = repository.createElement(OBJECT);
        registerNode(nodeData);
        nodeData.setAttribute(ELM_NUMBER, "" + node.getNumber());
        loadNodeData(nodeData,node, loadAction);
        if (loadAction != null) {
            loadRelations(nodeData,node,loadAction);
        }
        if (parent != null) {
            parent.appendChild(nodeData);
        }
        return nodeData;
    }

    /**
     * This method loads relations from MMBase and stores the result in a DOM node.
     *
     * @param parent The parent DOM node where the results should be appended to.
     * @param objectNumber The number of the object to load.
     * @param loadAction The node with loadAction data. Has information about what relations should be loaded and what fields should be retrieved.
     * @return The new relations (in the data object), or <code>null</code> if none.
     * @throws WizardException if loading the relations fails
     */
    void loadRelations(Node parent, String objectNumber, Node loadAction) throws WizardException {
        org.mmbase.bridge.Node node = transaction.getNode(objectNumber);
        loadRelations(parent, node, loadAction);
    }

    /**
     * This method loads relations from MMBase and stores the result in a DOM node.
     *
     * @param parent The parent DOM node where the results should be appended to.
     * @param node The object to load.
     * @param loadAction The node with loadAction data. Has information about what relations should be loaded and what fields should be retrieved.
     * @return The new relations (in the data object), or <code>null</code> if none.
     * @throws WizardException if loading the relations fails
     */
    void loadRelations(Node parent, org.mmbase.bridge.Node node, Node loadAction) throws WizardException {
        NodeList relationsRequested = Utils.selectNodeList(loadAction, "relation");
        // load relations (automatically loads related objects and 'deep' relations)
        for (int i = 0; i < relationsRequested.getLength(); i++) {
            Node relationRequested = relationsRequested.item(i);

            // determine attributes of the search
            String role = Utils.getAttribute(relationRequested,ELM_ROLE);
            if ("".equals(role)) role=null;
            String destinationType = Utils.getAttribute(relationRequested,ELM_DESTINATIONTYPE);
            if (("".equals(destinationType)) || (destinationType == null)) {
                destinationType = Utils.getAttribute(relationRequested,ELM_DESTINATION);
            }
            if ("".equals(destinationType)) destinationType=null;
            int searchDir = 0;
            String searchDirs = Utils.getAttribute(relationRequested,ELM_SEARCHDIR).toLowerCase();
            if("destination".equals(searchDirs)) {
                searchDir = 1;
            } else if("source".equals(searchDirs)) {
                searchDir = 2;
            }
            // determines whether to restrict what is loaded from the object
            Element subLoadAction = getFirstElement(relationRequested,OBJECT);
            int thisNumber = node.getNumber();
            for (RelationIterator ri = node.getRelations(role,destinationType).relationIterator(); ri.hasNext(); ) {
                Relation relation = ri.nextRelation();
                if (searchDir == 1 && thisNumber != relation.getIntValue("snumber")) continue;
                if (searchDir == 2 && thisNumber != relation.getIntValue("dnumber")) continue;
                Element relationData = repository.createElement(RELATION);
                registerNode(relationData);
                relationData.setAttribute(ELM_NUMBER, "" + relation.getNumber());
                loadNodeData(relationData,relation,loadAction);
                if (role != null) {
                    relationData.setAttribute(ELM_ROLE, role);
                } else {
                    relationData.setAttribute(ELM_ROLE,relation.getRelationManager().getForwardRole());
                }
                relationData.setAttribute(ELM_SOURCE, "" + relation.getIntValue("snumber"));
                relationData.setAttribute(ELM_DESTINATION, "" + relation.getIntValue("dnumber"));

                int otherNumber;
                if (thisNumber == relation.getIntValue("snumber")) {
                    otherNumber = relation.getIntValue("dnumber");
                } else {
                    otherNumber = relation.getIntValue("snumber");
                }
                loadNode(relationData,transaction.getNode(otherNumber), subLoadAction);
                parent.appendChild(relationData);
            }
        }
    }

    /**
     * Handles a getlist call.
     * This method accepts a DOM element, which should have a 'xpath' attribute to indicate the nodemanager(s)
     * to run the list on, and optionally a 'where' that contains the constraints.
     * The result of this call is a slist of bridge nodes.
     * @param loadAction The node with loadAction data (the query).
     */
    public org.mmbase.bridge.NodeList loadListNodes(Node loadAction) {
        String xpath = Utils.getAttribute(loadAction,ELM_XPATH); // get xpath (nodetype);
        String where = Utils.getAttribute(loadAction,ELM_WHERE); // get constraints;
        String orderby = Utils.getAttribute(loadAction,ELM_ORDERBY); // get orderby;
        if ("".equals(orderby)) orderby=null;
        String directions = Utils.getAttribute(loadAction,ELM_DIRECTIONS); // get directions;
        if ("".equals(directions)) directions=null;
        if (xpath.equals("")) {
            throw new IllegalArgumentException("xpath required for query");
        } else {
            if (xpath.indexOf("/*@")!=0) {
                throw new IllegalArgumentException("invalid xpath");
            } else {
                String nodepath = xpath.substring(3);

                if (nodepath.indexOf("/") == -1) {
                    // If there is no '/' seperator, we only get fields from one nodemanager. This is the fastest
                    // way of getting those.
                    return transaction.getNodeManager(xpath.substring(3)).getList(where,orderby,directions);
                } else {
                    // If there are '/' seperators, we need to do a multilevel search. Therefore we first need to
                    // get a list of all the fields (as subnodes) to query.
                    String fields = "";
                    Element field = getFirstElement(loadAction, FIELD);
                    nodepath = nodepath.replace('/', ',');
                    while (field != null) {
                        String fieldName = field.getAttribute(ELM_NAME);
                        if (!fields.equals(""))
                            fields += ",";
                        fields += fieldName;
                        field = getNextElement(field, FIELD);
                    }
                    return transaction.getList("", nodepath, fields, where, orderby, directions, null, true);
                }
            }
        }
    }

    /**
     * Handles a getlist call.
     * This method accepts a DOM element, which should have a 'xpath' attribute to indicate the nodemanager(s)
     * to run the list on, and optionally a 'where' that contains the constraints.
     * The result of this call should be a DOM elements. The node element's children
     * are the nodes from the list.
     * @param parent The parent DOM node where the results should be appended to.
     * @param loadAction The node with loadAction data (the query).
     */
    public void loadList(Node parent, Node loadAction) throws WizardException {
        // get node template
        Element subLoadAction = getFirstElement(loadAction);
        for (NodeIterator i = loadListNodes(loadAction).nodeIterator(); i.hasNext(); ) {
            org.mmbase.bridge.Node node = i.nextNode();
            Element nodeData = parent.getOwnerDocument().createElement(OBJECT);
            nodeData.setAttribute(ELM_NUMBER, "" + node.getNumber());
            registerNode(nodeData);
            parent.appendChild(nodeData);
            loadNodeData(nodeData,node,subLoadAction);
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
    public Node loadNew(Node parent, String objectType) throws WizardException {
        NodeManager nodeManager = transaction.getNodeManager(objectType);
        org.mmbase.bridge.Node node = nodeManager.createNode();
        Element nodeData = repository.createElement(OBJECT);
        nodeData.setAttribute(ELM_NUMBER, "" + node.getNumber());
        registerNode(nodeData);
        if (parent!=null) {
            parent.appendChild(nodeData);
        }
        loadNodeData(nodeData,node,null);
        return nodeData;
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
    public Node loadNewRelation(Node parent, String sourceObjectNumber,String destinationObjectNumber, String roleName,
                                String createDir) throws WizardException {
        int createDirection = Queries.getRelationStepDirection(createDir);
        if (roleName.equals("")) {
            throw new IllegalArgumentException("role required for getrelations");
        } else {
            // if both types are given, use these as a constraint for the Relationmanager
            org.mmbase.bridge.Node source = getAssociatedNode(sourceObjectNumber);
            org.mmbase.bridge.Node destination = getAssociatedNode(destinationObjectNumber);
            if (createDirection == RelationStep.DIRECTIONS_SOURCE) {
                org.mmbase.bridge.Node temp = destination;
                destination = source;
                source = temp;
            }
            RelationManager relationManager = transaction.getRelationManager(source.getNodeManager(),destination.getNodeManager(),roleName);
            Relation relation = relationManager.createRelation(source, destination);

            Element nodeData = repository.createElement(RELATION);
            nodeData.setAttribute(ELM_NUMBER, "" + relation.getNumber());
            nodeData.setAttribute(ELM_DESTINATION, "" + destination.getNumber());
            nodeData.setAttribute(ELM_SOURCE, "" + source.getNumber());
            nodeData.setAttribute(ELM_ROLE,roleName);
            registerNode(nodeData);
            parent.appendChild(nodeData);
            loadNodeData(nodeData,relation,null);
            return nodeData;
        }
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
    private void fillObjectFields(Node nodeData, Map params, Node createAction, int createOrder)  throws WizardException {
        // fill-in (or place) defined fields and their values.
        org.mmbase.bridge.Node node = getAssociatedNode(nodeData);
        NodeList fields = Utils.selectNodeList(createAction, FIELD);
        for (int i = 0; i < fields.getLength(); i++) {
            Node field = fields.item(i);
            String fieldName = Utils.getAttribute(field, ELM_NAME);
            // does this field already exist?
            Node dataField = Utils.selectSingleNode(nodeData, "field[@name='"+fieldName+"']");
            if (dataField == null) {
                // Non-existing field (getNew/getNewRelation always return all fields)
                String type = Utils.getAttribute(createAction, "type");
                throw new WizardException("field " + fieldName + " does not exist in '" + type + "'");
            }
            String value = Utils.getText(field);

            // if you add a list of items, the order of the list may be of import.
            // the variable $pos is used to make that distinction
            params.put("pos",createOrder+"");
            Node root = repository.getDocumentElement();
            if (log.isDebugEnabled()) log.debug("root="+root.toString());
            value = Utils.transformAttribute(root,value,false,params);
            params.remove("pos");
            if (value == null) {
                value = "";
            }
            Utils.storeText(dataField, value, params); // process innerText: check for params
            node.setValue(fieldName, value);
        }
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
    public Node createObject(Node parent, Node createAction, Map params) throws WizardException {
        return createObject(parent, createAction, params, 1);
    }

    /**
     * This method can create a object (or a tree of objects and relations)
     *
     * @param parentDid The did of the node where the results should be appended.
     * @param createAction The careet action.
     * @param params The params to use when creating the objects and relations.
     * @return The resulting object(tree) node.
     * @throws WizardException if the object cannot be created
     */
    public Node createObject(String parentDid, Node createAction, Map params, int createOrder) throws WizardException {
        Node parent = Utils.selectSingleNode(repository, ".//*[@did='" + parentDid + "']");
        return createObject(parent, createAction, params, createOrder);
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
    public Node createObject(Node parent, Node createAction, Map params, int createOrder) throws WizardException {

        if (createAction == null) throw new WizardException("No create action given"); // otherwise NPE in getAttribute

        String nodeName = createAction.getNodeName();

        // check if we maybe should create multiple objects or relations

        if (nodeName.equals("action")) {
            NodeList createActions = Utils.selectNodeList(createAction, "object|relation");
            Node firstNodeData = null;
            for (int i=0; i < createActions.getLength(); i++) {
                firstNodeData = createObject(parent, createActions.item(i), params);
            }
            log.debug("This is an action");  // no relations to add here..
            return firstNodeData;
        }

        String context = (String)params.get("context");
        NodeList relationActions;
        Node nodeData;

        if (nodeName.equals("relation")) {
            // nodeData equals targetParentNode
            nodeData = parent;
            if (nodeData == null) {
                throw new WizardException("Could not find a parent node for relation " + Utils.stringFormatted(createAction));
            }
            relationActions = Utils.selectNodeList(createAction, ".");
        } else if (nodeName.equals("object")) {
            String objectType = Utils.getAttribute(createAction, "type");
            if (objectType.equals("")) throw new WizardException("No 'type' attribute used in " + Utils.stringFormatted(createAction));
            if (log.isDebugEnabled()) log.debug("Create object of type " + objectType);
            // create a new object of the given type
            nodeData = loadNew(parent, objectType);
            if (context != null && !context.equals("")) {
                Utils.setAttribute(nodeData, "context", context);
            }
            fillObjectFields(nodeData,params,createAction,createOrder);
            relationActions = Utils.selectNodeList(createAction, "relation");
        } else {
           throw new WizardException("Can only create with 'action' 'object' or 'relation' nodes");
        }

        // Let's see if we need to create new relations (maybe even with new objects inside...
        Node lastCreatedRelationData = null;

        for (int i = 0; i < relationActions.getLength(); i++) {
            Node relationAction = relationActions.item(i);
            String role = Utils.getAttribute(relationAction, "role", "related");
            String snumber = Utils.getAttribute(nodeData, "number");
            // determine destination (dnumber can be null)
            String dnumber = Utils.getAttribute(relationAction, "destination", null);
            dnumber = Utils.transformAttribute(repository, dnumber, false, params);
            String createDir = Utils.getAttribute(relationAction, ELM_CREATEDIR, "either");

            Node relatedNodeData = null;
            Node relatedNodeAction = Utils.selectSingleNode(relationAction, "object");
            if (dnumber != null) {
                // dnumber is given (direct reference to an existing mmbase node)
                relatedNodeData = loadNode(null,dnumber,null);
            } else {
                if (relatedNodeAction == null) {
                    String destinationType = Utils.getAttribute(relationAction, "destinationtype", "");
                    if (destinationType.equals("")) throw new WizardException("No 'type' attribute used in " + Utils.stringFormatted(relationAction));
                    if (log.isDebugEnabled()) log.debug("Create object of type " + destinationType);
                    // create a new object of the given type
                    relatedNodeData = loadNew(null, destinationType);
                    if (context != null && !context.equals("")) {
                        Utils.setAttribute(relatedNodeData, "context", context);
                    }
                } else {
                    relatedNodeData = createObject(null, relatedNodeAction, params);
                }
                dnumber = Utils.getAttribute(relatedNodeData, "number");
            }
            Node relationData = loadNewRelation(nodeData, snumber, dnumber, role, createDir);
            if (context != null && !context.equals("")) {
                Utils.setAttribute(relationData, "context", context);
            }
            fillObjectFields(relationData,params,relationAction,createOrder);
            lastCreatedRelationData = relationData;
            relationData.appendChild(relatedNodeData);
        }
        if (nodeName.equals("relation")) {
            return lastCreatedRelationData;
        } else {
            return nodeData;
        }
    }

    /**
     * @javadoc
     */
    public String put() {
        org.mmbase.bridge.Node node = getAssociatedNode(getMainObject());
        transaction.commit();
        transaction = null;
        return "" + node.getNumber();
    }

    /**
     * @javadoc
     */
    public void cancel() {
        transaction.cancel();
        transaction = null;
    }

    /**
     * Puts the given value in the right datanode (given by did), depending on the type
     * of the form field.
     * @param did The data id where the value should be stored
     * @param value The value to be stored
     * @param binary is true, the value stored is a binary field and listed in the binary repository
     */
    public void storeValue(String did, String value, boolean binary) throws WizardException {
        String xpath = ".//*[@did='" + did + "']";
        Node fieldNodeData = Utils.selectSingleNode(repository, xpath);
        if (fieldNodeData == null) {
            log.warn("Unable to store value for field with did = " + did + ", value=" + value +
                     "\nxpath was:" + xpath + " on:\n" + repository.getDocumentElement());
            return;
        }
        org.mmbase.bridge.Node node = getAssociatedNode(fieldNodeData.getParentNode());
        String fieldName = Utils.getAttribute(fieldNodeData,ELM_NAME);
        // everything seems to be ok
        if (binary) {
            BinaryObjectMetaData metaData = getBinary(did);
            if (metaData != null) {
                Utils.setAttribute(fieldNodeData, ELM_HREF, did);
                Utils.storeText(fieldNodeData, metaData.name);
            }
        } else { // default behavior: store content as text
            node.setValue(fieldName,value);
            Utils.storeText(fieldNodeData, value);
        }
    }

    public void deleteNode(String did, Node deleteAction) {
        Node nodeData = Utils.selectSingleNode(repository, ".//*[@did='" + did + "']");
        if (nodeData != null) {
            if (deleteAction != null) {
                Node dataObjectNode = Utils.selectSingleNode(nodeData, "object");
                getAssociatedNode(dataObjectNode).delete();
            }
            getAssociatedNode(nodeData).delete();
            nodeData.getParentNode().removeChild(nodeData);
        }
    }

    public void updateRepository(String value) {
        NodeList nodesToUpdate = Utils.selectNodeList(repository, ".//*[@number='" + value + "']");
        if (nodesToUpdate != null) {
            try {
                Node updatedNode = loadNode(null, value, null);
                NodeList updatedFields = Utils.selectNodeList(updatedNode, "./field");
                Map fieldValues = new HashMap();
                for (int j = 0; j < updatedFields.getLength(); j++) {
                    Node fieldNode = updatedFields.item(j);
                    String fieldName = Utils.getAttribute(fieldNode, ELM_NAME);
                    String fieldValue = Utils.getText(fieldNode);
                    fieldValues.put(fieldName, fieldValue);
                }
                for (int i = 0; i < nodesToUpdate.getLength(); i++) {
                    Node nodeData = nodesToUpdate.item(i);
                    NodeList fieldsToUpdate = Utils.selectNodeList(nodeData, "./field");

                    for (int j = 0; j < fieldsToUpdate.getLength(); j++) {
                        Node fieldNode = fieldsToUpdate.item(j);
                        String fieldName = Utils.getAttribute(fieldNode, ELM_NAME);
                        String fieldValue = (String) fieldValues.get(fieldName);
                        Utils.storeText(fieldNode, fieldValue);
                    }
                }
            } catch (Exception e) {
                // node was deleted, remove all nodes from repository, including relations if needed
                // note that any conflicts with lockign shoudd be handled through the
                // transaction mechanism
                for (int i = 0; i < nodesToUpdate.getLength(); i++) {
                    Node nodeData = nodesToUpdate.item(i);
                    Node parentNode = nodeData.getParentNode();
                    if (parentNode instanceof Element && ((Element)parentNode).getTagName().equals("relation")) {
                        nodeData = parentNode;
                    }
                    nodeData.getParentNode().removeChild(nodeData);
                }
            }
        }
    }

    public void swapValues(String did, String otherDid, String fieldPath) {
        // if orderby is only a field name, create an xpath
        log.debug("swap " + did + " and " + otherDid + " on " + fieldPath);
        Node nodeData = Utils.selectSingleNode(repository,".//*[@did='" + did + "']/" + fieldPath);
        Node otherNodeData = Utils.selectSingleNode(repository,".//*[@did='" + otherDid + "']/" + fieldPath);
        if (nodeData != null && otherNodeData != null) {
            org.mmbase.bridge.Node node = getAssociatedNode(nodeData.getParentNode());
            String fieldName = Utils.getAttribute(nodeData,ELM_NAME);
            org.mmbase.bridge.Node otherNode = getAssociatedNode(otherNodeData.getParentNode());

            String dataValue = Utils.getText(nodeData);
            String otherValue = Utils.getText(otherNodeData);

            node.setValue(fieldName, otherValue);
            Utils.storeText(nodeData, otherValue);

            otherNode.setValue(fieldName, dataValue);
            Utils.storeText(otherNodeData, dataValue);
        }
    }

    /**
     * With this method you can store a binary in the wizard.
     *
     * @param did This is the objectID what points to in what field the binary should be stored, once commited.
     * @param bytes This is a bytearray with the data to be stored.
     * @param name This is the name which will be used to show what file is uploaded.
     * @param path The (local) path of the file placed.
     */
    public void setBinary(String did, byte[] bytes, String name, String path) {
        setBinary(did, bytes, name, path, null);
    }

    /**
     * With this method you can store a binary in the wizard.
     * @param type Content-type of the byte (or null). If not null, then the fields 'mimetype',
     *             'size' and 'filename' are filled as well.
     * @since MMBase-1.7.2
     */
    public void setBinary(String did, byte[] bytes, String name, String path, String type) {
        Node binaryField = Utils.selectSingleNode(repository, ".//field[@did='" + did + "']");
        String fieldName = Utils.getAttribute(binaryField,ELM_NAME);
        Node nodeData = binaryField.getParentNode();
        org.mmbase.bridge.Node node = getAssociatedNode(nodeData);
        if (type != null) {
            Node mimetypeField = Utils.selectSingleNode(nodeData, "field[@name='mimetype']");
            if (mimetypeField != null) {
                Utils.storeText(mimetypeField, type);
            }
            node.setValue("mimetype",type);
        }
        int length = bytes.length;
        Node sizeField = Utils.selectSingleNode(nodeData, "field[@name='size']");
        if (sizeField != null && bytes != null) {
            Utils.storeText(sizeField, "" + length);
        }
        node.setValue("size",length);
        Node fileNameField = Utils.selectSingleNode(nodeData, "field[@name='filename']");
        if (fileNameField != null && name != null) {
            Utils.storeText(fileNameField, name);
        }
        node.setValue("filename",name);
        node.setValue(fieldName,bytes);
        binaries.put(did, new BinaryObjectMetaData(length,name,path));
    }

    /**
     * This method allows you to retrieve the data of a temporarily stored binary.
     * @param did The objectID of the binary you want.
     * @return the binary data, if found.
     */
    public BinaryObjectMetaData getBinary(String did) {
        return (BinaryObjectMetaData)binaries.get(did);
    }

    /**
     * @javadoc
     */
    public String getGUIType(Field field) {
        String guiType = field.getGUIType();
        if (guiType.indexOf("/")==-1) {
            if (guiType.equals("field")) {
                guiType = "string/text";
            } else if (guiType.equals("string")) {
                guiType = "string/line";
            } else if (guiType.equals("eventtime")) {
                guiType = "datetime/datetime";
            } else if (guiType.equals("newimage")) {
                guiType = "binary/image";
            } else if (guiType.equals("newfile")) {
                guiType = "binary/file";
            } else {
                String dttype;
                int itype = field.getType();
                switch(itype) {
                case Field.TYPE_INTEGER:
                case Field.TYPE_NODE:
                    dttype = "int";
                    break;
                case Field.TYPE_LONG:
                    dttype="long";
                    break;
                case Field.TYPE_FLOAT:
                    dttype="float";
                    break;
                case Field.TYPE_DOUBLE:
                    dttype="double";
                    break;
                case Field.TYPE_BYTE:
                    dttype="binary";
                    break;
                case Field.TYPE_DATETIME:
                    dttype = "datetime";
                    break;
                case Field.TYPE_BOOLEAN:
                    dttype = "boolean";
                    break;
                default:
                    dttype = "string";
                }
                if (guiType.equals("")) {
                    guiType = dttype + "/" + dttype;
                } else {
                    guiType = dttype + "/" + guiType;
                }
            }
        }
        return guiType;
    }

    /**
     * @javadoc
     */
    public class BinaryObjectMetaData extends Object {
        public int length;
        public String name;
        public String path;

        public BinaryObjectMetaData(int length, String name, String path) {
            this.length = length;
            this.name = name;
            this.path = path;
        }
    }

}
