/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import javax.servlet.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.security.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.legacy.*;


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
 * @version $Id: BasicNodeManager.java,v 1.59 2003-07-25 14:10:30 michiel Exp $
 */
public class BasicNodeManager extends BasicNode implements NodeManager, Comparable {
    private static Logger log = Logging.getLoggerInstance(BasicNodeManager.class);

    // builder on which the type is based
    protected MMObjectBuilder builder;

    // field types
    protected Map fieldTypes = new Hashtable();


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
    BasicNodeManager(MMObjectNode node, Cloud cloud, int nodeid) {
        super(node,cloud, nodeid);
        // no initialization - for a new node, builder is null.
    }

    /**
     * Instantiates a NodeManager based on a node which either represents or references a builder.
     * Normally this is a TypeDef node, but subclasses (i.e. BasicRelationManager)
     * may use other nodes, such as nodes from RelDef or TypeRel.
     * The NodeManager that administrates the node itself is requested from the Cloud.
     * @param node the MMObjectNode to base the NodeManager on.
     * @param Cloud the cloud to which this node belongs
     */
    BasicNodeManager(MMObjectNode node, Cloud cloud) {
        super(node,cloud);
        initManager();
    }

    /**
     * Instantiates a NodeManager based on a builder.
     * The constructor attempts to retrieve an MMObjectNode (from typedef)
     * which represents this builder.
     * @param builder the MMObjectBuidler to base the NodeManager on.
     * @param Cloud the cloud to which this node belongs
     */
    BasicNodeManager(MMObjectBuilder builder, BasicCloud cloud) {
        super(builder.isVirtual() ? new VirtualNode(BasicCloudContext.mmb.getTypeDef()) : builder.getNode(builder.oType),cloud);
        this.builder=builder;
        initManager();
    }

    /**
     * Initializes the NodeManager: determines the MMObjectBuilder if it was not already known,
     * and fills the fields list.
     */
    protected void initManager() {
        if (builder == null) {
            if(noderef == null) {
                String msg = "node reference was null, could not continue";
                log.error(msg);
                throw new BridgeException(msg);
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
                log.warn ("could not find nodemanager for node #" + noderef.getNumber() + "("+noderef.getStringValue("gui()")+")");
                return;
            }
        }
    // clear the list of fields..
    // why is this needed?
        List fields = builder.getFields();
        if (fields != null) {
            fieldTypes.clear();
            for(Iterator i = fields.iterator(); i.hasNext();){
                FieldDefs f = (FieldDefs) i.next();
                Field ft = new BasicField(f,this);
                fieldTypes.put(ft.getName(),ft);
            }
        }
    }

    public Node createNode() {
        // create object as a temporary node
        int id = BasicCloud.uniqueId();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.createTmpNode(getMMObjectBuilder().getTableName(), cloud.getAccount(), ""+id);
        // if we are in a transaction, add the node to the transaction;
        if (cloud instanceof BasicTransaction) {
            ((BasicTransaction)cloud).add(currentObjectContext);
        }
        MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(cloud.getAccount(), ""+id);

        // set the owner to the owner field as indicated by the user
        node.setValue("owner",((BasicUser)cloud.getUser()).getUserContext().getOwnerField());

        if (getMMObjectBuilder() instanceof TypeDef) {
            return new BasicNodeManager(node, getCloud(), id);
        } else if (getMMObjectBuilder() instanceof RelDef || getMMObjectBuilder() instanceof TypeRel) {
            return new BasicRelationManager(node, getCloud(), id);
        } else if (getMMObjectBuilder() instanceof InsRel) {
            return new BasicRelation(node, getCloud() /*this*/, id);
        } else {
            return new BasicNode(node, getCloud() /*this*/, id);
        }
    }

    public NodeManager getParent() throws NotFoundException {
        MMObjectBuilder bul = getMMObjectBuilder().getParentBuilder();
        if (bul==null) {
            throw new NotFoundException("Parent of nodemanager "+getName()+"does not exist");
        } else {
            return cloud.getNodeManager(bul.getTableName());
        }
    }

    public String getName() {
        if (builder!=null) {
            return builder.getTableName();
        } else {
            return getStringValue("name");
        }
    }

    public String getGUIName() {
        return getGUIName(1);
    }

    public String getGUIName(int plurality) {
        if (builder!=null) {
            if (plurality==1) {
                return builder.getSingularName(cloud.getLocale().getLanguage());
            } else {
                 return builder.getPluralName(cloud.getLocale().getLanguage());
            }
        }
        return getName();
    }

    public String getDescription() {
        if (builder!=null) {
            return builder.getDescription(cloud.getLocale().getLanguage());
        } 
        return "";
    }

    public FieldList getFields() {
        return new BasicFieldList(fieldTypes.values(),this);
    }

    public FieldList getFields(int order) {
        if (builder!=null) {
            return new BasicFieldList(builder.getFields(order),this);
        }
        return getFields();
    }

    public Field getField(String fieldName) throws NotFoundException {
        Field f = (Field)fieldTypes.get(fieldName);
        if (f == null) throw new NotFoundException("Field " + fieldName + " does not exist.");
        return f;
    }

    public boolean hasField(String fieldName) {
        return fieldTypes.get(fieldName)!=null;
    }

    /**
     *
     * @since MMBase-1.7
     */
    protected NodeList getList(NodeSearchQuery query) {
        List resultList;
        try {
            resultList = builder.getNodes(query); // result with all MMObjectNodes (without security)
        } catch (SearchQueryException sqe) {
            throw new BridgeException(sqe);
        }

        log.debug("checking read rights");
        BasicNodeList list = new BasicNodeList(resultList, this); // also makes a copy
        list.autoConvert = false;

        ListIterator i = list.listIterator();
        while (i.hasNext()) {
            if (!cloud.check(Operation.READ, ((MMObjectNode)i.next()).getNumber())) {
                i.remove();
            }
        }
        list.setProperty("query", query);
        list.autoConvert = true;
        return list;

    }

    public NodeList getList(NodeQuery query) {
        query.markUsed();
        return getList( (NodeSearchQuery)((BasicNodeQuery) query).getQuery());

    }


    // javadoc inherited
    public NodeQuery createQuery() {
        return new BasicNodeQuery(this);
    }



    public NodeList getList(String constraints, String sorted, String directions) {
        MMObjectBuilder builder = getMMObjectBuilder();

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


        NodeSearchQuery query = new NodeSearchQuery(builder);
        if (constraints != null) {
            query.setConstraint(new ConstraintParser(query).toConstraint(constraints));
        }


        // following code was copied from MMObjectBuilder.setSearchQuery (bit ugly)
        if (directions == null) {
            directions = "";
        }
        
        if (sorted != null) {
            StringTokenizer sortedTokenizer = new StringTokenizer(sorted, ",");
            StringTokenizer directionsTokenizer = new StringTokenizer(directions, ",");
            
            String direction = "UP";
            while (sortedTokenizer.hasMoreElements()) {
                String fieldName = sortedTokenizer.nextToken().trim();
                FieldDefs fieldDefs = builder.getField(fieldName);
                if (fieldDefs == null) {
                    throw new IllegalArgumentException("Not a known field of builder " + builder.getTableName() + ": '" + fieldName + "'");
                }
                StepField field = query.getField(fieldDefs);
                BasicSortOrder sortOrder = query.addSortOrder(field);
                if (directionsTokenizer.hasMoreElements()) {
                    direction = directionsTokenizer.nextToken().trim();
                }
                if (direction.equalsIgnoreCase("DOWN")) {
                    sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
                } else {
                    sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
                }
            }
        }

        NodeList list = getList(query);
        list.setProperty("constraints", constraints);
        list.setProperty("orderby",     sorted);
        list.setProperty("directions",  directions);
        return list;

    }

    public RelationManagerList getAllowedRelations() {
       return getAllowedRelations((NodeManager)null,null,null);
    }

    public RelationManagerList getAllowedRelations(String nodeManager, String role, String direction) {
        if (nodeManager==null) {
            return getAllowedRelations((NodeManager)null,role,direction);
        } else {
            return getAllowedRelations(cloud.getNodeManager(nodeManager),role,direction);
        }
    }

    public RelationManagerList getAllowedRelations(NodeManager nodeManager, String role, String direction) {
        int thisOType= getMMObjectBuilder().oType;
        int requestedRole=-1;
        if (role!=null) {
            requestedRole = mmb.getRelDef().getNumberByName(role);
            if (requestedRole == -1) {
                throw new NotFoundException("Could not get role '" + role + "'");
            }
        }

        int dir  = ClusterBuilder.getSearchDir(direction);

        Enumeration typerelNodes;
        if (nodeManager != null) {
            int otherOType = nodeManager.getNumber();
            typerelNodes=mmb.getTypeRel().getAllowedRelations(thisOType,otherOType);
        } else {
            typerelNodes=mmb.getTypeRel().getAllowedRelations(thisOType);
        }
        List nodes = new ArrayList();
        while (typerelNodes.hasMoreElements()) {
            MMObjectNode n= (MMObjectNode)typerelNodes.nextElement();
            if ((requestedRole==-1) || (requestedRole==n.getIntValue("rnumber"))) {
                if (n.getIntValue("snumber") != n.getIntValue("dnumber")) { // if types are equal, no need to check direction, it is always ok then..
                    if (thisOType== n.getIntValue("dnumber")) {
                        if (dir == ClusterBuilder.SEARCH_DESTINATION) {
                            continue;
                        }
                    } else {
                        if (dir == ClusterBuilder.SEARCH_SOURCE) {
                            continue;
                        }
                    }
                }
                nodes.add(n);
            }
        }
        return new BasicRelationManagerList(nodes,cloud);
    }

    public String getInfo(String command) {
        return getInfo(command, null,null);
    }

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){
        MMObjectBuilder builder=getMMObjectBuilder();
        StringTokenizer tokens= new StringTokenizer(command,"-");
        return builder.replace(BasicCloudContext.getScanPage(req, resp),tokens);
    }

    public NodeList getList(String command, Map parameters){
        return getList(command,parameters,null,null);
    }

    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp){
        MMObjectBuilder builder=getMMObjectBuilder();
        StringTagger params= new StringTagger("");
        if (parameters!=null) {
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
            Vector v=builder.getList(BasicCloudContext.getScanPage(req, resp),params,tokens);
            if (v==null) { v=new Vector(); }
            int items=1;
            try {
                items=Integer.parseInt(params.Value("ITEMS"));
            } catch (Exception e) {
                log.warn("parameter 'ITEMS' must be a int value, it was :" + params.Value("ITEMS"));
            }
            Vector fieldlist=params.Values("FIELDS");
            Vector res=new Vector(v.size() / items);
            for(int i= 0; i<v.size(); i+=items) {
                MMObjectNode node = new MMObjectNode(builder);
                for(int j= 0; (j<items) && (j<v.size()); j++) {
                    if ((fieldlist!=null) && (j<fieldlist.size())) {
                        node.setValue((String)fieldlist.get(j),v.get(i+j));
                    } else {
                        node.setValue("item"+(j+1),v.get(i+j));
                    }
                }
                res.add(node);
            }
            if (res.size()>0) {
                NodeManager tempNodeManager = new VirtualNodeManager((MMObjectNode)res.get(0),cloud);
                return new BasicNodeList(res,tempNodeManager);
            }
            return new BasicNodeList();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
            throw new BridgeException(e);
        }
    }

    public boolean mayCreateNode() {
        if (builder==null) return false;
        return cloud.check(Operation.CREATE, builder.oType);
    }

    MMObjectBuilder getMMObjectBuilder() {
        if (builder==null) {
            throw new IllegalStateException("No functional instantiation exists (yet/any more) for this NodeManager.");
        }
        return builder;
    }

    // overriding behavior of BasicNode

    public void commit() {
        super.commit();  // commit the node - the builder should now be loaded by TypeDef/ObjectTypes
        // rebuild builder reference and fieldlist.
        initManager();
    }

    public void delete(boolean deleteRelations) {
        super.delete(deleteRelations);
        builder=null;  // invalidate (builder does not exist any more)
    }

}
