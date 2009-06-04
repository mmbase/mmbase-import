/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.security.Operation;
import org.mmbase.util.PageInfo;
import org.mmbase.util.StringTagger;
import org.mmbase.util.functions.Function;
import org.mmbase.util.logging.*;

/**
 * This class represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types. It also contains some maintenance code - code
 * to create new nodes, en code to query objects belonging to the same manager.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (which is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$

 */
public class BasicNodeManager extends BasicNode implements NodeManager {
    private static final  Logger log = Logging.getLoggerInstance(BasicNodeManager.class);

    /**
     * @javadoc
     */
    private long internalVersion = -1;

    // builder on which the type is based
    protected MMObjectBuilder builder;

    // field types
    protected Map<String, Field> fieldTypes = new HashMap<String, Field>();

    /**
     * Instantiates a new NodeManager (for insert) based on a newly created node which either represents or references a builder.
     * Normally this is a TypeDef node, but subclasses (i.e. BasicRelationManager)
     * may use other nodes, such as nodes from RelDef or TypeRel.
     * The NodeManager that administrates the node itself is requested from the Cloud.
     * The Nodemanager cannot be used for administartion tasks until it is isnerted (committed) in the Cloud.
     * @param node the MMObjectNode to base the NodeManager on.
     * @param Cloud the cloud to which this node belongs
     * @param id the id of the node in the temporary cloud
     */
    BasicNodeManager(MMObjectNode node, BasicCloud cloud, int nodeid) {
        super(node, cloud, nodeid);
        // no initialization - for a new nodes, builder is null.
    }

    /**
     * Instantiates a NodeManager based on a node which either represents or references a builder.
     * Normally this is a TypeDef node, but subclasses (i.e. BasicRelationManager)
     * may use other nodes, such as nodes from RelDef or TypeRel.
     * The NodeManager that administrates the node itself is requested from the Cloud.
     * @param node the MMObjectNode to base the NodeManager on.
     * @param Cloud the cloud to which this node belongs
     */
    BasicNodeManager(MMObjectNode node, BasicCloud cloud) {
        super(node, cloud);
        initManager();
    }

    /**
     * Instantiates a NodeManager based on a builder.
     * The constructor attempts to retrieve an MMObjectNode (from typedef)
     * which represents this builder.
     * @param builder the MMObjectBuilder to base the NodeManager on.
     * @param Cloud the cloud to which this node belongs
     */
    BasicNodeManager(MMObjectBuilder builder, BasicCloud cloud) {
        super(cloud);
        noderef = getNodeForBuilder(builder);
        this.builder = builder;
        TypeDef typeDef =  BasicCloudContext.mmb.getTypeDef();
        if (builder == typeDef) {
            nodeManager = this;
        } else {
            nodeManager = cloud.getBasicNodeManager(typeDef);
        }
        sync();
    }

    /**
     * This method is  only needed to get clearer exception from above constructor in case of problem with builder.
     * @since MMBase-1.8
     */
    private static MMObjectNode getNodeForBuilder(MMObjectBuilder builder) {
        if (builder.isVirtual()) {
            return new org.mmbase.module.core.VirtualNode(BasicCloudContext.mmb.getTypeDef());
        } else {
            MMObjectNode typedefNode = builder.getNode(builder.getNumber());
            if (typedefNode == null) {
                throw new RuntimeException("Could not find typedef node for builder " + builder + " with otype " + builder.getNumber());
            }
            return typedefNode;
        }
    }

    @Override
    protected void setNodeManager(MMObjectNode node) {
        int nodeNumber = node.getNumber();
        if (nodeNumber >= 0 && nodeNumber == node.getBuilder().getNumber()) { // this is the typedef itself
            nodeManager = this;
        } else {
            log.debug("Setting node manager for nodeManager, but no typedef " + node);
            super.setNodeManager(node);
        }
    }

    @Override
    public final boolean isNodeManager() {
        return true;
    }

    @Override
    public final NodeManager toNodeManager() {
        return this;
    }

    /**
     * Initializes the NodeManager: determines the MMObjectBuilder if it was not already known,
     * and fills the fields list.
     */
    protected void initManager() {
        if (builder == null) {
            if(noderef == null) {
                throw new BridgeException("node reference was null, could not continue");
            }
            // look which node we represent, and
            // what the builder is that this
            // node is representing
            TypeDef typedef = (TypeDef) noderef.getBuilder();
            builder = typedef.getBuilder(noderef);
            if(builder == null) {
                // builder = null. this muist mean that this is an inactive builder
                // log warning, then exit.
                // This will mean the nodemanager can be used as a node, but will be treated as a
                // non-committed builder.
                log.warn ("could not find nodemanager for node #" + noderef.getNumber() + " (" + noderef.getGUIIndicator() + ")");
                return;
            }
        }
        sync();
    }


    /**
     * @since MMBase-1.8
     */
    protected static void sync(MMObjectBuilder builder, Map<String, Field> fieldTypes, NodeManager nodeManager) {
        Collection<CoreField> fields = builder.getFields();
        if (fields != null) { // when is it null?
            fieldTypes.clear();
            for (CoreField f : fields) {
                Field ft = new BasicField(f, nodeManager);
                if (f.getStoragePosition() > 0) {
                    fieldTypes.put(ft.getName().toLowerCase(), ft);
                }
            }
        }
    }


    /**
     * Syncs the nodemanger with the builder.
     * Loads the fieldlist from the associated builder if needed.
     * @since MMBase-1.8
     */
    synchronized private void sync() {
        long builderVersion = builder.getInternalVersion();
        if (internalVersion < builderVersion) {
            internalVersion = builderVersion;
            sync(builder, fieldTypes, this);
        }
    }

    /**
     * Returns the fieldlist of this nodemanager after making sure the manager is synced with the builder.
     * @since MMBase-1.8
     */
    protected Map<String, Field> getFieldTypes() {
        sync();
        return fieldTypes;
    }

    /**
     * Structure to temporary contain an MMObjectNode and a {@link BasicCloud#uniqueId()}
     * @since MMBase-1.8
     */
    protected final class NodeAndId {
        final MMObjectNode node;
        final int id;
        NodeAndId(MMObjectNode n, int i) {
            node = n; id = i;
        }
    }


    /**
     * Creates new MMObjectNode for the current node manager.
     * @return MMObjectNode wrapped in a {@link NodeAndId}
     * @since MMBase-1.8
     */
    protected NodeAndId createMMObjectNode() {
        // create object as a temporary node
        int id = BasicCloud.uniqueId();
        {
            String currentObjectContext = BasicCloudContext.tmpObjectManager.createTmpNode(getMMObjectBuilder().getTableName(), cloud.getAccount(), ""+id);
            // if we are in a transaction, add the node to the transaction;
            cloud.add(currentObjectContext);
        }

        MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(cloud.getAccount(), "" + id);
        // odd this MMObjectNode does _not_ have the right builder?!

        // XXX this should somehow be the default value of the owner field!!
        // set the owner to the owner field as indicated by the user
        node.setValue("owner", cloud.getUser().getOwnerField());

        setDefaultsWithCloud(node);
        return new NodeAndId(node, id);
    }

    /**
     * Sometimes default values can only be filled if a cloud is present, hence only in the bridge.
     * This is the counterpart of ${MMObjectBuilder#setDefaults}
     * @since MMBase-1.8.6
     */
    protected void setDefaultsWithCloud(MMObjectNode node) {
        log.debug("Setting default values");
        for (Iterator i = getFields().iterator(); i.hasNext(); ) {
            Field field = (Field) i.next();
            if (field.isVirtual())                         continue;
            if (field.getName().equals(MMObjectBuilder.FIELD_NUMBER))      continue;
            if (field.getName().equals(MMObjectBuilder.FIELD_OWNER))       continue;
            if (field.getName().equals(MMObjectBuilder.FIELD_OBJECT_TYPE)) continue;

            if (node.isNull(field.getName()) || "".equals(node.getStringValue(field.getName()))) { // required field are set to '', which would destroy the default value...
                org.mmbase.datatypes.DataType dt = field.getDataType();
                //log.info("" + field.getName() + " " + dt);
                Object defaultValue = dt.getDefaultValue(getCloud().getLocale(), getCloud(), field);
                if (defaultValue != null) {
                    if (defaultValue instanceof Node) {
                        defaultValue = ((Node) defaultValue).getNumber();
                    }

                    node.setValue(field.getName(), defaultValue);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("" + field.getName() + " is already non null, but " + node.getValue(field.getName()));
                }
            }
        }
    }


    /**
     * BasicNodeManager is garantueed to return BasicNode's. So extendsion must override this, and not {@link #createNode}
     * @since MMBase-1.8
     */
    protected BasicNode createBasicNode() {
        NodeAndId n = createMMObjectNode();
        MMObjectBuilder bul = getMMObjectBuilder();
        if (bul instanceof TypeDef) {
            // I wonder if this is necessary.
            return new BasicNodeManager(n.node, cloud, n.id);
        } else if (bul instanceof TypeRel) {
            return new BasicRelationManager(n.node, cloud, n.id);
        } else {
            return new BasicNode(n.node, cloud, n.id);
        }
    }
    public final Node createNode() {
        return createBasicNode();
    }


    public NodeManager getParent() throws NotFoundException {
        MMObjectBuilder bul = getMMObjectBuilder().getParentBuilder();
        if (bul == null) {
            throw new NotFoundException("Parent of nodemanager " + getName() + "does not exist");
        } else {
            return cloud.getNodeManager(bul.getTableName());
        }
    }

    public NodeManagerList getDescendants() {
        List<MMObjectBuilder> descs = getMMObjectBuilder().getDescendants();
        return new BasicNodeManagerList(descs, cloud);
    }

    public String getName() {
        if (builder != null) {
            return builder.getTableName();
        } else {
            return getStringValue("name");
        }
    }

    public String getProperty(String name) {
        if (builder != null) {
            return builder.getInitParameter(name);
        } else {
            return null;
        }
    }

    public Map<String, String> getProperties() {
        if (builder != null) {
            return Collections.unmodifiableMap(builder.getInitParameters());
        } else {
            return Collections.emptyMap();
        }
    }

    public String getGUIName() {
        return getGUIName(NodeManager.GUI_SINGULAR);
    }

    public String getGUIName(int plurality) {
        return getGUIName(plurality, null);
    }

    public String getGUIName(int plurality, Locale locale) {
        if (locale==null) locale = cloud.getLocale();
        if (builder!=null) {
            if (plurality == NodeManager.GUI_SINGULAR) {
                return builder.getSingularName(locale.getLanguage());
            } else {
                return builder.getPluralName(locale.getLanguage());
            }
        } else {
            return getName();
        }
    }

    public String getDescription() {
        return getDescription(null);
    }

    public String getDescription(Locale locale) {
        if (locale == null) locale = cloud.getLocale();
        if (builder != null) {
            return builder.getDescription(locale.getLanguage());
        } else {
            return "";
        }
    }

    public FieldList getFields() {
        return getFields(NodeManager.ORDER_NONE);
    }

    public FieldList getFields(int order) {
        if (builder != null) {
            return new BasicFieldList(builder.getFields(order), this);
        } else {
            return new BasicFieldList(getFieldTypes().values(), this);
        }
    }

    public Field getField(String fieldName) throws NotFoundException {
        Field f =  getFieldTypes().get(fieldName.toLowerCase());
        if (f == null) throw new NotFoundException("Field '" + fieldName + "' does not exist in NodeManager '" + getName() + "'.(" + getFieldTypes() + ")");
        return f;
    }

    public boolean hasField(String fieldName) {
        return fieldName != null && getFieldTypes().containsKey(fieldName.toLowerCase());
    }


    // javadoc inherited
    public NodeQuery createQuery() {
        return new BasicNodeQuery(this);
    }

    public NodeList getList(NodeQuery query) {
        try {
            if (query == null) query = createQuery();

            boolean checked = cloud.setSecurityConstraint(query);

            boolean useCache = query.getCachePolicy().checkPolicy(query);
            List<MMObjectNode> resultList = builder.getStorageConnector().getNodes(query, useCache);

            if (! checked) {
                resultList = cloud.checkNodes(resultList, query);
            }

            BasicNodeList resultNodeList;
            NodeManager nm = query.getNodeManager();
            resultNodeList = new BasicNodeList(resultList, cloud);
            resultNodeList.setProperty(NodeList.QUERY_PROPERTY, query);

            return resultNodeList;
        } catch (SearchQueryException sqe) {
            throw new BridgeException(sqe);
        }
    }


    public NodeList getList(String constraints, String sorted, String directions) {
//        MMObjectBuilder builder = getMMObjectBuilder();

        // begin of check invalid search command
        /*
        org.mmbase.util.Encode encoder = new org.mmbase.util.Encode("ESCAPE_SINGLE_QUOTE");
        if(orderby != null)    orderby     = encoder.encode(orderby);
        if(directions != null) directions  = encoder.encode(directions);
        if(constraints != null && !cloud.validConstraints(constraints)) {
            throw new BridgeException("invalid contrain:" + constraints);
        }
        */
        // end of check invalid search command


        NodeQuery query = createQuery();
        Queries.addConstraints(query, constraints);
        Queries.addSortOrders(query, sorted, directions);
        NodeList list = getList(query);
        list.setProperty("constraints", constraints);
        list.setProperty("orderby",     sorted);
        list.setProperty("directions",  directions);
        return list;
    }


    public RelationManagerList getAllowedRelations() {
       return getAllowedRelations((NodeManager) null, null, null);
    }

    public RelationManagerList getAllowedRelations(String nodeManager, String role, String direction) {
        if (nodeManager==null) {
            return getAllowedRelations((NodeManager)null, role, direction);
        } else {
            return getAllowedRelations(cloud.getNodeManager(nodeManager), role, direction);
        }
    }

    public RelationManagerList getAllowedRelations(NodeManager nodeManager, String role, String direction) {
        int thisOType = getMMObjectBuilder().getNumber();
        int requestedRole = -1;
        if (role != null) {
            requestedRole = BasicCloudContext.mmb.getRelDef().getNumberByName(role);
            if (requestedRole == -1) {
                throw new NotFoundException("Could not get role '" + role + "'");
            }
        }

        int dir  = ClusterBuilder.getSearchDir(direction);

        Enumeration<MMObjectNode> typerelNodes;
        if (nodeManager != null) {
            int otherOType = nodeManager.getNumber();
            typerelNodes = BasicCloudContext.mmb.getTypeRel().getAllowedRelations(thisOType, otherOType);
        } else {
            typerelNodes = BasicCloudContext.mmb.getTypeRel().getAllowedRelations(thisOType);
        }

        List<MMObjectNode> nodes = new ArrayList<MMObjectNode>();
        while (typerelNodes.hasMoreElements()) {
            MMObjectNode n = typerelNodes.nextElement();
            if ((requestedRole == -1) || (requestedRole == n.getIntValue("rnumber"))) {
                int snumber = n.getIntValue("snumber");
                int dnumber = n.getIntValue("dnumber");
                if (snumber != dnumber) { // if types are equal, no need to check direction, it is always ok then..
                    if (thisOType == dnumber) {
                        if (dir == RelationStep.DIRECTIONS_DESTINATION) {
                            continue;
                        }
                    } else {
                        if (dir == RelationStep.DIRECTIONS_SOURCE) {
                            continue;
                        }
                    }
                }
                nodes.add(n);
            }
        }
        return new BasicRelationManagerList(nodes, cloud);
    }

    public String getInfo(String command) {
        return getInfo(command, null,null);
    }

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){
        MMObjectBuilder builder = getMMObjectBuilder();
        StringTokenizer tokens = new StringTokenizer(command,"-");
        return builder.replace(new PageInfo((HttpServletRequest)req, (HttpServletResponse)resp, getCloud()),tokens);
    }

    public NodeList getList(String command, Map parameters){
        return getList(command,parameters,null,null);
    }

    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp){
        MMObjectBuilder builder = getMMObjectBuilder();
        StringTagger params = new StringTagger("");
        if (parameters != null) {
            for (Iterator entries = parameters.entrySet().iterator(); entries.hasNext(); ) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key=(String) entry.getKey();
                Object o = entry.getValue();
                if (o instanceof Vector) {
                    params.setValues(key,(Vector)o);
                } else {
                    params.setValue(key,""+o);
                }
            }
        }
        try {
            StringTokenizer tokens= new StringTokenizer(command,"-");
            List<String> v = builder.getList(new PageInfo((HttpServletRequest)req, (HttpServletResponse)resp, getCloud()), params, tokens);
            if (v == null) {
                v = new ArrayList<String>();
            }
            int items=1;
            try {
                items=Integer.parseInt(params.Value("ITEMS"));
            } catch (Exception e) {
                log.warn("parameter 'ITEMS' must be a int value, it was :" + params.Value("ITEMS"));
            }
            Vector<String> fieldlist = params.Values("FIELDS");
            Vector<MMObjectNode> res = new Vector<MMObjectNode>(v.size() / items);
            for (int i= 0; i<v.size(); i+=items) {
                MMObjectNode node = new org.mmbase.module.core.VirtualNode(builder);
                for(int j= 0; (j<items) && (j<v.size()); j++) {
                    if ((fieldlist!=null) && (j<fieldlist.size())) {
                        node.setValue(fieldlist.get(j), v.get(i+j));
                    } else {
                        node.setValue("item"+(j+1),v.get(i+j));
                    }
                }
                res.add(node);
            }
            if (res.size() > 0) {
                NodeManager tempNodeManager = new VirtualNodeManager((org.mmbase.module.core.VirtualNode)res.get(0), cloud);
                return new BasicNodeList(res, tempNodeManager);
            }
            return createNodeList();
        } catch (Exception e) {
            throw new BridgeException(e);
        }
    }

    public boolean mayCreateNode() {
        if (builder == null) return false;
        return cloud.check(Operation.CREATE, builder.getNumber());
    }

    MMObjectBuilder getMMObjectBuilder() {
        if (builder == null) {
            throw new IllegalStateException("No functional instantiation exists (yet/any more) for this NodeManager.");
        }
        return builder;
    }

    // overriding behavior of BasicNode

    @Override
    public void commit() {
        super.commit();  // commit the node - the builder should now be loaded by TypeDef
        // rebuild builder reference and fieldlist.
        initManager();
    }

    @Override
    public void delete(boolean deleteRelations) {
        super.delete(deleteRelations);
        builder=null;  // invalidate (builder does not exist any more)
    }

    @Override
    public Collection<Function<?>> getFunctions() {
        return  builder.getFunctions();
    }

    @Override
    protected Function getNodeFunction(String functionName) {
        if (log.isDebugEnabled()) {
            log.debug("Getting function '" + functionName + "' for " + this);
        }

        // it may be a node-function on the type-def node then
        // it may be gui on a typedef node or so.
        Function function = getNode().getFunction(functionName);
        if (function == null || functionName.equals("info") || functionName.equals("getFunctions")) {
            function = builder != null ? builder.getFunction(functionName) : null;
        }
        if (function == null) {
            throw new NotFoundException("Function with name " + functionName + " does not exist in " + builder.getFunctions());
        }
        return function;

    }

    public FieldList createFieldList() {
        return new BasicFieldList(Collections.emptyList(), this);
    }

    public NodeList createNodeList() {
        return new BasicNodeList(Collections.emptyList(), this);
    }

    public RelationList createRelationList() {
        return new BasicRelationList(Collections.emptyList(), this);
    }

}

