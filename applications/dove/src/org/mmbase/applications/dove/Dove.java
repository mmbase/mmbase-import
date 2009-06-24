/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.dove;

import java.util.*;
import java.util.regex.Pattern;
import org.w3c.dom.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.*;
import org.mmbase.module.builders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.util.Casting;
import org.mmbase.util.Encode;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.util.logging.*;

/**
 * This class handles Remote Procedure Calls described using a DOM model.
 * The original data is received in xml, likely through a specialized servlet such
 * as the DoveServlet (but this is not required. it may, for instance, be
 * possible to use this class in conjunction with SOAP or even outside a servlet
 * context).
 * The call should result in a new DOM tree (see the EditWizard API for details).
 * <br />
 * XXX: Currently, this class performs some type of validation on the xml received.
 * If errors occur, they are included in the response, at the place where they
 * occur. This means that the &lt;error &gt; tag can occur anywhere in the
 * response, which is not very neat.
 * <br />
 * Errors have three types:<br />
 * parser : the xml given is invalid or does not follow the grammar.
 *          This likely means there is a bug in the client code.<br />
 * server : the code invoked is either not yet implemented or another,
 *          server-related, error occurred (such as no memory, bad configuration,
 *          etc.).
 *          Server errors entirely fail a request.<br />
 * client : The data requested could not be retrieved or values specified
 *          were invalid. I.e. a requested node does not exist (any more), or a
 *          <code>put</code> failed due to locking or invalid data.<br />
 * <br />
 * This is ONLY for debugging purposes!
 * XML validation should occur at the parser and be captured early.
 * If we reach consensus that we must always validate, the xml-checking can be left
 * out, as we can then assumes a correct model. This will optimize processing.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.5
 * @version $Id: Dove.java,v 1.78.2.11 2009-01-13 11:07:33 michiel Exp $
 */

public class Dove extends AbstractDove {

    private static final Logger log = Logging.getLoggerInstance(Dove.class);


    /**
     *@since MMBase-1.8.1
     */
    private static final UtilReader properties = new UtilReader("dove.xml");
    private static final String PROP_CHANGES = "changes";
    private static final String CHANGES_IGNORE = "ignore";
    private static final String CHANGES_WARN   = "warn";
    private static final String CHANGES_EXCEPTION   = "exception";

    /**
     * Constructor
     * @param doc the Document that is constructed. This should only be used to
     *            construct new DOM elements. New (child) nodes should not be added
     *            to this document, but to the out element.
     */
    public Dove(Document doc) {
        super(doc);
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
    private boolean isDataField(NodeManager nodeManager, Field f) {
        String fname = f.getName();
        return (nodeManager.hasField(fname)) && // skip temporary fields
            (!"owner".equals(fname)) &&
            (!"otype".equals(fname)) && // skip owner/otype/number fields!
            (!"number".equals(fname)) &&
            (!"snumber".equals(fname)) &&
            (!"dnumber".equals(fname)) &&
            (!"rnumber".equals(fname)) &&
            (!"dir".equals(fname));
    }

    /**
     * Utility function, determines whether a field is a data field.
     * Data fields are fields mentioned explicitly in the builder configuration file.
     * These can include virtual fields.
     * Fields number, owner, and ottype, and the relation fields snumber,dnumber, and rnumber
     * are not excluded; these fields should be handled through the attributes of Element.
     * @param node  the MMBase node that owns the field
     * @param fname The name of the field to check
     */
    private boolean isDataField(NodeManager nodeManager, String fname) {
        return nodeManager.hasField(fname);
    }

    private boolean isEditableField(NodeManager nodeManager, String fname) {
        return isDataField(nodeManager, fname) && ! nodeManager.getField(fname).isReadOnly();
    }



    /**
     * @since MMBase-1.8.4
     */
    protected Element addField(Element out, NodeManager nm, Field f, org.mmbase.bridge.Node node) {
        Element fel;
        DataType dataType = f.getDataType();
        String fname = f.getName();
        if (dataType instanceof BinaryDataType) {
            log.debug("Adding '" + fname + "'");
            fel = addContentElement(FIELD, "", out);

            long byteLength = 0;
            if (nm.hasField("filesize")) {
                byteLength = node.getIntValue("filesize");
            } else if (nm.hasField("size")) {
                byteLength = node.getIntValue("size");
            } else {
                byteLength = node.isNull(fname) ? 0L : node.getSize(fname);
            }
            fel.setAttribute(ELM_SIZE, "" + byteLength);
        } else if (dataType instanceof DateTimeDataType ||
                   dataType instanceof IntegerDataType ||
                   dataType instanceof LongDataType
                               ) {
            log.debug("Adding '" + fname + "'");
            // have to convert ourselves because bridge will use user-defined formatting
            fel = addContentElement(FIELD, node.isNull(fname) ? null : "" + node.getLongValue(fname), out);
        } else {
            log.debug("Adding '" + fname + "' ->" + node.getStringValue(fname));
            fel = addContentElement(FIELD, node.isNull(fname) ? null : node.getStringValue(fname), out);
        }
        fel.setAttribute(ELM_TYPE, dataType.getBaseTypeIdentifier());
        fel.setAttribute(ELM_NAME, fname);
        fel.setAttribute("nodemanager", nm.getName());
        Iterator i = getOptionList(dataType, node.getCloud(), node, f);
        if (i != null) {
            Element ol = addContentElement("optionlist", "", out);
            ol.setAttribute("name", "_" + node.getNodeManager().getName() + "_" + f.getName());
            while (i.hasNext()) {
                Map.Entry  entry = (Map.Entry) i.next();
                Object key = entry.getKey();
                if (dataType instanceof BooleanDataType) { // These damn wizards use integer for
                                                           // booleans. Don't ask me why. It's stupid.
                    key =  "" + Casting.toInt(key);
                }
                Element o = addContentElement("option", Casting.toString(entry.getValue()), ol);
                o.setAttribute("id", Casting.toString(key));
            }
        }
        return fel;
    }

    private Iterator  getOptionList(DataType dataType, Cloud cloud, org.mmbase.bridge.Node node, Field f) {
        Iterator i = dataType.getEnumerationValues(cloud.getLocale(), cloud, node, f);
        if (i != null) return i;

        long min = Long.MIN_VALUE;
        long max = Long.MAX_VALUE;
        if (dataType instanceof IntegerDataType) {
            IntegerDataType idt = (IntegerDataType) dataType;
            min = idt.getMin() + (idt.isMinInclusive() ? 0 : 1);
            max = idt.getMax() - (idt.isMaxInclusive() ? 0 : 1);
        }


        if (dataType instanceof LongDataType) {
            LongDataType ldt = (LongDataType) dataType;
            min = ldt.getMin() + (ldt.isMinInclusive() ? 0 : 1);
            max = ldt.getMax() - (ldt.isMaxInclusive() ? 0 : 1);
        }
        if ((double) max - min < 200.0) {
            final long end = max;
            final long start = min;
            return new Iterator() {
                long value = start;
                public boolean hasNext() {
                    return value <= end;
                }
                public Object next() {
                    Long v = new Long(value++);
                    return new org.mmbase.util.Entry(v, v);
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return null;

    }
    /**
     * Handles a node storing its content in a DOM element.
     * This method accepts a object to store, as well as a DOM element, which
     * may contain as it child nodes elements describing the fields to retrieve.
     * The tagname of these elements should be 'field'.
     * Each element should have a 'name' attribute that identifies the field.
     * The result of this call should be a list of DOM elements describing each field,
     * which are appended to the out element.
     * If the 'in' node has no field elements, all fields are returned, with the
     * exception of some system-specific fields.
     * <br />
     * @todo Currently, the Dove does not return values for binary (byte) fields - a binary field is
     * always returned empty. This is done for optimization of the editwizards, which would otherwise
     * eat a lot of memory, but that DO need a reference to a bytefield.
     * Future versions of Dove should handle a better mechanism for handling binary fields.
     *
     * @param in the element that described the <code>getdata</code> call.
     *           The childnodes should describe the nodes to retrieve.
     * @param out the element that described the <code>getdata</code> result.
     *           Retrieved nodes should be added as childs to this element.
     * @param node The node to store in out.
     */
    public void getDataNode(Element in, Element out, org.mmbase.bridge.Node node) {
        NodeManager nm = node.getNodeManager();
        out.setAttribute(ELM_TYPE, nm.getName());
        out.setAttribute(ELM_MAYWRITE, "" + node.mayWrite());
        out.setAttribute(ELM_MAYDELETE, "" + node.mayDelete());
        // load fields
        Element field = getFirstElement(in, FIELD);
        if (field == null) {
            for (FieldIterator i = nm.getFields(NodeManager.ORDER_CREATE).fieldIterator(); i.hasNext(); ) {
                Field f = i.nextField();
                String fname = f.getName();
                if (isDataField(nm,f)) {
                    addField(out, nm, f, node);
                }
            }
        } else {
            while (field != null) { // select all child tags, should be 'field'
                String fname = field.getAttribute(ELM_NAME);
                if ((fname == null) || (fname.equals(""))) {
                    Element err = addContentElement(ERROR, "name required for field",out);
                    err.setAttribute(ELM_TYPE, IS_PARSER);
                } else if (isDataField(nm,fname)) {
                    Field f = nm.getField(fname);
                    addField(out, nm, f, node);
                } else {
                    Element err = addContentElement(ERROR, "field with name " + fname + " does not exist", out);
                    err.setAttribute(ELM_TYPE, IS_PARSER);
                }
                field = getNextElement(field, FIELD);
            }
        }
        // load relations
        Element relation = getFirstElement(in, RELATION);
        while (relation != null) { // select all child tags, should be 'relation'
            addRelationNodes(relation, out, node);
            relation = getNextElement(relation, RELATION);
        }
    }

    /**
     * Handles a node by retrieving its data and storing its content in a
     * DOM element.
     * This method accepts a DOM element (describing a node), which may contain as it
     * child nodes elements describing the fields to retrieve.
     * The tagname of these elements should be 'field'.
     * Each element should have a 'name' attribute that identifies the field.
     * The result of this call should be a list of DOM elements describing each field,
     * which are appended to the out element.
     * If the 'in' node has no field elements, all fields are returned, with the
     * exception of some system-specific fields.
     * <br />
     * Note also that if no name was given for a field, a NullPointer exception is thrown.
     *
     * @param in the element that described the <code>getdata</code> call.
     *           The childnodes should describe the nodes to retrieve.
     * @param out the element that described the <code>getdata</code> result.
     *           Retrieved nodes should be added as childs to this element.
     * @param cloud the cloud to work on
     */
    public void getDataNode(Element in, Element out, Cloud cloud) {
        String alias = in.getAttribute(ELM_NUMBER);
        org.mmbase.bridge.Node nd = cloud.getNode(alias);
        getDataNode(in,out,nd);
    }

    /**
     * Retrieves the relations of a node and adds its content to a DOM element.
     * This method accepts a DOM element (describing a relation), which may contain as it
     * child nodes elements describing the fields as well as the related object to retrieve.
     *
     * @param relation the element that described the relation
     *           The childnodes (if present) should describe the fields to retrieve, and possibly
     *           a description of the related object.
     * @param out the element that describes the source node.
     *           Retrieved relations should be added as childs to this element.
     * @param nd the MMBase node that shoudl eb sued to query the relations
     */
    protected void addRelationNodes(Element relation, Element out, org.mmbase.bridge.Node nd) {
        int thisNumber=nd.getNumber();
        String role=relation.getAttribute(ELM_ROLE);
        if ("".equals(role)) role=null;
        String destinationType = relation.getAttribute(ELM_DESTINATIONTYPE);
        if (("".equals(destinationType)) || (destinationType==null)) {
            destinationType = relation.getAttribute(ELM_DESTINATION);
        }
        if ("".equals(destinationType)) destinationType=null;
        int searchDir=0;
        String searchDirs = relation.getAttribute(ELM_SEARCHDIR).toLowerCase();
        if("destination".equals(searchDirs)) {
            searchDir = 1;
        } else if("source".equals(searchDirs)) {
            searchDir = 2;
        }

        // determines whether to load the object (and possible restrictions)
        Element objectDef = getFirstElement(relation, OBJECT);
        for (RelationIterator i = nd.getRelations(role, destinationType).relationIterator(); i.hasNext(); ) {
            Relation nrel = i.nextRelation();
            if (searchDir ==1) {
                if (thisNumber!=nrel.getIntValue("snumber")) continue;
            }
            if (searchDir==2) {
                if (thisNumber!=nrel.getIntValue("dnumber")) continue;
            }
            Element data=doc.createElement(RELATION);
            if (role!=null) {
                data.setAttribute(ELM_ROLE, role);
            } else {
                data.setAttribute(ELM_ROLE,nrel.getRelationManager().getForwardRole());
            }
            data.setAttribute(ELM_SOURCE,      "" + nrel.getIntValue("snumber"));
            data.setAttribute(ELM_DESTINATION, "" + nrel.getIntValue("dnumber"));

            int otherNumber;
            if (thisNumber == nrel.getIntValue("snumber")) {
                otherNumber = nrel.getIntValue("dnumber");
            } else {
                otherNumber = nrel.getIntValue("snumber");
            }
            data.setAttribute(ELM_NUMBER, ""+nrel.getNumber());
            out.appendChild(data);
            getDataNode(relation, data, nrel);
            if (objectDef != null) {
                Element nodeData = doc.createElement(OBJECT);
                nodeData.setAttribute(ELM_NUMBER, ""+otherNumber);
                data.appendChild(nodeData);
                getDataNode(objectDef, nodeData,nd.getCloud().getNode(otherNumber));
                nodeData.setAttribute(ELM_SEARCHTYPE, destinationType);
            }
        }
    }

    /**
     * Handles a node by retrieving its relations and storing its content in a
     * DOM element.
     * This method accepts a DOM element (describing a node), which may contain as it
     * child nodes elements describing the relations to retrieve.
     * The tagname of these elements should be 'relation'.
     * If the 'in' node has no relation elements, all relations are returned.
     *
     * @param in the element that described the <code>getdata</code> call.
     *           The childnodes should describe the nodes to retrieve.
     * @param out the element that described the <code>getdata</code> result.
     *           Retrieved nodes should be added as childs to this element.
     * @param cloud the cloud to work on
     */
    public void getRelationsNodes(Element in, Element out, Cloud cloud) {
        String alias = in.getAttribute(ELM_NUMBER);
        org.mmbase.bridge.Node nd = cloud.getNode(alias);
        NodeManager nm=nd.getNodeManager();
        out.setAttribute(ELM_TYPE,nm.getName());

        Element relation=getFirstElement(in, RELATION);
        if (relation==null) {
            int thisNumber=nd.getNumber();
            for (RelationIterator i=nd.getRelations().relationIterator(); i.hasNext(); ) {
                Relation nrel=i.nextRelation();
                Element data=doc.createElement(RELATION);
                data.setAttribute(ELM_NUMBER, ""+nrel.getNumber());
                data.setAttribute(ELM_ROLE, nrel.getRelationManager().getForwardRole());
                if (thisNumber==nrel.getIntValue("snumber")) {
                    data.setAttribute(ELM_SOURCE, ""+nrel.getIntValue("snumber"));
                    data.setAttribute(ELM_DESTINATION, ""+nrel.getIntValue("dnumber"));
                } else {
                    data.setAttribute(ELM_SOURCE, ""+nrel.getIntValue("dnumber"));
                    data.setAttribute(ELM_DESTINATION, ""+nrel.getIntValue("snumber"));
                }
                out.appendChild(data);
                getDataNode(null, data, nrel);
            }
        } else {
            while (relation!=null) { // select all child tags, should be 'relation'
                addRelationNodes(relation,out,nd);
                relation=getNextElement(relation,RELATION);
            }
        }

    }

    /**
     * Handles a getdata call, by obtaining data for each node specified.
     * This method accepts a DOM element, which should contain as it
     * child nodes elements describing the nodes to retrieve.
     * The tagname of these elements should be 'object'.
     * Each element should have an 'number' attribute that described the number
     * or alias of the node to retrieve.
     * The result of this call should be a list of DOM elements describing each node,
     * which are appended to the out element.
     *
     * @param in the element that described the <code>getdata</code> call.
     *           The childnodes should describe the nodes to retrieve.
     * @param out the element that described the <code>getdata</code> result.
     *           Retrieved nodes should be added as childs to this element.
     * @param cloud the cloud to work on
     */
    public void getData(Element in, Element out, Cloud cloud) {
        Element node=getFirstElement(in);
        while (node!=null) { // select all child tags, should be 'object'
            if (node.getTagName().equals(OBJECT)) {
                String number = node.getAttribute(ELM_NUMBER); // check id;
                if (number.equals("")) {
                    Element err = addContentElement(ERROR,"number required for node",out);
                    err.setAttribute(ELM_TYPE, IS_PARSER);
                } else {
                    Element data=doc.createElement(OBJECT);
                    data.setAttribute(ELM_NUMBER, number);
                    out.appendChild(data);
                    getDataNode(node,data,cloud);
                }
            } else {
                Element err = addContentElement(ERROR,"Unknown subtag in getdata: "+node.getTagName(),out);
                err.setAttribute(ELM_TYPE, IS_PARSER);
            }
            node=getNextElement(node);
        }
    }

    /**
     * Handles a getnew call, by obtaining data for the new node of a specified type.
     * This method accepts a DOM element, which should contain as it's attribute
     * the type of the node to return.
     * The result of this call should be DOM element describing a new node,
     * which is appended to the out element.
     *
     * @param in the element that described the <code>getnew</code> call.
     * @param out the element that described the <code>getnew</code> result.
     *           The new node should be added as a child to this element.
     * @param cloud the cloud to work on
     */
    public void getNew(Element in, Element out, Cloud cloud) {
        String nodemanagername = in.getAttribute(ELM_TYPE); // check type;
        if (nodemanagername.equals("")) {
            Element err = addContentElement(ERROR,"type required for getnew",out);
            err.setAttribute(ELM_TYPE, IS_PARSER);
        } else {
            out.setAttribute(ELM_TYPE, nodemanagername);
            NodeManager nm = cloud.getNodeManager(nodemanagername);
            org.mmbase.bridge.Node n = nm.createNode();
            try {
                Element data=doc.createElement(OBJECT);
                int number=java.lang.Math.abs(n.getNumber());
                data.setAttribute(ELM_NUMBER, "n"+number);
                out.appendChild(data);
                getDataNode(null, data, n);
            } finally {
                n.cancel();  // have to cancel node ! It will only be really made in the putNewNode function
            }
        }
    }

    /**
     * Handles a getnewrelation call, by obtaining data for the new node of a specified type.
     * This method accepts a DOM element, which should contain as it's attribute
     * the role of the relation to return.
     * The result of this call should be DOM element describing a new node,
     * which is appended to the out element.
     *
     * @param in the element that described the <code>getnew</code> call.
     * @param out the element that described the <code>getnew</code> result.
     *           The new node should be added as a child to this element.
     * @param cloud the cloud to work on
     */
    public void getNewRelation(Element in, Element out, Cloud cloud) {
        String rolename = in.getAttribute(ELM_ROLE); // check role;
        String destination = in.getAttribute(ELM_DESTINATION); // check destination;
        String source = in.getAttribute(ELM_SOURCE); // check source;
        String destinationType = in.getAttribute(ELM_DESTINATIONTYPE); // check destination type;
        String sourceType = in.getAttribute(ELM_SOURCETYPE); // check source type;

        int createDir = Queries.getRelationStepDirection(in.getAttribute(ELM_CREATEDIR));

        if (rolename.equals("")) {
            Element err = addContentElement(ERROR,"role required for getrelations",out);
            err.setAttribute(ELM_TYPE, IS_PARSER);
        } else {
                out.setAttribute(ELM_ROLE,rolename);
                out.setAttribute(ELM_DESTINATION, destination);
                out.setAttribute(ELM_SOURCE, source);
                // if both types are given, use these as a constraint for the Relationmanager
                RelationManager nm;
                if (destinationType.equals("") || sourceType.equals("") ) {
                    nm = cloud.getRelationManager(rolename);
                } else {
                    nm = cloud.getRelationManager(sourceType,destinationType,rolename);
                }
                org.mmbase.bridge.Node n = nm.createNode();
                try {
                    Element data=doc.createElement(RELATION);
                    int number=java.lang.Math.abs(n.getNumber());
                    data.setAttribute(ELM_NUMBER, "n" + number);
                    if (createDir == RelationStep.DIRECTIONS_SOURCE) {
                        log.debug("Creating relation in the INVERSE direction");
                        data.setAttribute(ELM_DESTINATION, source);
                        data.setAttribute(ELM_SOURCE, destination);
                    } else {
                        log.debug("Creating relation in the NORMAL direction");
                        data.setAttribute(ELM_DESTINATION, destination);
                        data.setAttribute(ELM_SOURCE, source);
                    }
                    data.setAttribute(ELM_ROLE,rolename);
                    out.appendChild(data);
                    getDataNode(null, data, n);
                } finally  {
                    n.cancel();  // have to cancel node !
                }

        }
    }

    /**
     * Handles a getrelations call, by obtaining relations for each node specified.
     * This method accepts a DOM element, which should contain as it
     * child nodes elements describing the nodes to retrieve the relations from.
     * The tagname of these elements should be 'object'.
     * Each element should have an 'number' attribute that described the number
     * or alias of the node to retrieve.
     * The result of this call should be a list of DOM elements with a data element for
     * each node,which are appended to the out element. The node element's children
     * are the relations.
     *
     * @param in the element that described the <code>getrelations</code> call.
     *           The childnodes should describe the nodes to retrieve the relations from.
     * @param out the element that described the <code>getrelations</code> result.
     *           Retrieved nodes and their relations should be added as childs to this element.
     * @param cloud the cloud to work on
     */
    public void getRelations(Element in, Element out, Cloud cloud) {
        Element node = getFirstElement(in);
        while (node != null) { // select all child tags, should be 'object'
            if (node.getTagName().equals(OBJECT)) {
                String number = node.getAttribute(ELM_NUMBER); // check id;
                if (number.equals("")) {
                    Element err = addContentElement(ERROR,"number required for node", out);
                    err.setAttribute(ELM_TYPE, IS_PARSER);
                } else {
                    Element data = doc.createElement(OBJECT);
                    data.setAttribute(ELM_NUMBER, number);
                    out.appendChild(data);
                    getRelationsNodes(node,data,cloud);
                }
            } else {
                Element err = addContentElement(ERROR,"Unknown subtag in getrelations: " + node.getTagName(), out);
                err.setAttribute(ELM_TYPE, IS_PARSER);
            }
            node = getNextElement(node);
        }
    }

    /**
     * Handles a getconstraints call.
     *
     * @param in the element that described the <code>getconstraints</code> call.
     *           The childnodes should describe the node types to retrieve the constraints of.
     * @param out the element that described the <code>getconstraints</code> result.
     *           Retrieved constraints should be added as childs to this element.
     * @param cloud the cloud to work on
     */
    public void getConstraints(Element in, Element out, Cloud cloud) {
        String nodeManagerName = in.getAttribute(ELM_TYPE); // check type;
        if (nodeManagerName.equals("")) {
            Element err = addContentElement(ERROR,"type required for getconstraints",out);
            err.setAttribute(ELM_TYPE, IS_PARSER);
        } else {
            out.setAttribute(ELM_TYPE,nodeManagerName);
            NodeManager nm =cloud.getNodeManager(nodeManagerName);

            Locale locale = cloud.getLocale();
            String lang= in.getAttribute(ELM_LANG);
            if (!"".equals(lang)) {
                out.setAttribute(ELM_LANG,lang);
                locale = new Locale(lang);
            }

            // singular name
            Element elm=addContentElement(SINGULARNAME,nm.getGUIName(NodeManager.GUI_SINGULAR,locale),out);
            if (lang!=null) elm.setAttribute(ELM_LANG,lang);

            // plural name
            elm=addContentElement(PLURALNAME,nm.getGUIName(NodeManager.GUI_PLURAL,locale),out);
            if (lang!=null) elm.setAttribute(ELM_LANG,lang);

            // description
            elm=addContentElement(DESCRIPTION,nm.getDescription(locale),out);
            if (lang!=null) elm.setAttribute(ELM_LANG,lang);

            // parent
            try {
                NodeManager nmparent = nm.getParent();
                Element parent = doc.createElement(PARENT);
                out.appendChild(parent);
                parent.setAttribute(ELM_TYPE,nmparent.getName());
            } catch (NotFoundException nfe) {
                log.debug("no parent available");
            }

            // descendants
            NodeManagerList nmdesclist = nm.getDescendants();
            if (nmdesclist.size()>0) {
                Element descendants = doc.createElement(DESCENDANTS);
                out.appendChild(descendants);
                for (NodeManagerIterator i = nmdesclist.nodeManagerIterator(); i.hasNext();) {
                    Element descendant=doc.createElement(DESCENDANT);
                    descendants.appendChild(descendant);
                    descendant.setAttribute(ELM_TYPE,i.nextNodeManager().getName());
                }
            }

            // fields
            Element fields=doc.createElement(FIELDS);
            out.appendChild(fields);
            for(FieldIterator i = nm.getFields(NodeManager.ORDER_CREATE).fieldIterator(); i.hasNext(); ) {
                Field fielddef = i.nextField();
                String fname = fielddef.getName();
                // Filter out the owner/otype/number/CacheCount fields and
                // the virtual fields.
                if (isDataField(nm, fielddef)) {
                    Element field=doc.createElement(FIELD);
                    field.setAttribute(ELM_NAME,fname);
                    fields.appendChild(field);
                    // guiname (XXX:language is ignored)
                    elm = addContentElement(GUINAME, fielddef.getGUIName(locale),field);
                    elm = addContentElement(DESCRIPTION, fielddef.getDescription(locale),field);
                    if (lang != null) elm.setAttribute(ELM_LANG, lang);
                    // guitype
                    DataType dataType = fielddef.getDataType();
                    // The written type will be the first non-anonymous origin (or self).
                    String baseType = dataType.getBaseTypeIdentifier();

                    DataType specializationDataType = dataType;
                    String specialization = specializationDataType.getName();
                    while (specialization == null || "".equals(specialization)) {
                        specializationDataType = specializationDataType.getOrigin();
                        if (specializationDataType == null) break;
                        specialization = specializationDataType.getName();
                    }
                    log.debug("Found " + specialization);

                    if (getOptionList(dataType, nm.getCloud(), null, fielddef) != null) {
                        specialization = "enum";
                    } else if (dataType instanceof StringDataType) {
                        if (((StringDataType) dataType).getPattern().matcher("a\na").matches()) {
                            specialization = "text";
                        } else {
                            specialization = "line";
                        }
                    } else if (specialization.equals("eventtime")) {
                        baseType = "datetime";
                        specialization = "datetime";
                    } else if (specialization.equals("newimage")) {
                        specialization = "image";
                    } else if (specialization.equals("newfile")) {
                        specialization = "file";
                    } else {
                        // backward compatibility: NODE and XML are passed as int and string
                        // TODO: should change ?
                        if (dataType instanceof NodeDataType) {
                            baseType = "int";
                        } else if (dataType instanceof XmlDataType) {
                            baseType = "string";
                        } else if (dataType instanceof BinaryDataType) {
                            Pattern p = ((BinaryDataType) dataType).getValidMimeTypes();
                            if (p.matcher("image/someimageformat").matches() &&
                                ! p.matcher("application/nonimageformat").matches()) {
                                specialization = "image";
                            } else {
                                specialization = "file";
                            }
                        }
                    }
                    String guiType = baseType + "/" + specialization;

                    addContentElement(GUITYPE, guiType, field);
                    int maxLength = fielddef.getMaxLength();
                    if (maxLength>0) {
                        addContentElement(MAXLENGTH, "" + maxLength, field);
                    }

                    if (fielddef.isRequired()) {
                        addContentElement(REQUIRED, IS_TRUE, field);
                    } else {
                        addContentElement(REQUIRED, IS_FALSE, field);
                    }
                }
            }
            // relations
            Element relations = doc.createElement(RELATIONS);
            out.appendChild(relations);
            // XXX: getAllowedRelations is not yet supported by the MMCI
            // This code commented out

        }
    }

    /**
     * Handles a getlist call.
     * This method accepts a DOM element, which should contain as it
     * child nodes elements describing the queries to retrieve the nodes with.
     * The tagname of these elements should be 'query'.
     * Each element should have a 'xpath' attribute to indicate the nodemanager to run the list on,
     * and optionally a 'where' that contains the constraints.
     * The result of this call should be a list of DOM elements with a data element for
     * each query, which are appended to the out element. The node element's children
     * are the nodes from the list.
     * @param in the element that described the <code>getlist</code> call.
     *           The childnodes should describe the queries to run.
     * @param out the element that described the <code>getlist</code> result.
     *           Retrieved nodes should be added as childs to this element.
     * @param cloud the cloud to work on
     */
    public void getList(Element in, Element out, Cloud cloud) {
        Element query = getFirstElement(in);
        while (query != null) { // select all child tags, should be 'query'
            if (query.getTagName().equals(QUERY)) {
                String xpath = query.getAttribute(ELM_XPATH); // get xpath (nodetype);
                String where = query.getAttribute(ELM_WHERE); // get constraints;
                String orderby = query.getAttribute(ELM_ORDERBY); // get orderby;
                if ("".equals(orderby)) orderby=null;
                String directions = query.getAttribute(ELM_DIRECTIONS); // get directions;
                if ("".equals(directions)) directions=null;
                if (xpath.equals("")) {
                    Element err = addContentElement(ERROR,"xpath required for query",out);
                    err.setAttribute(ELM_TYPE, IS_PARSER);
                } else {
                    Element querydata=doc.createElement(QUERY);
                    querydata.setAttribute(ELM_XPATH, xpath);
                    if (!where.equals("")) {
                        querydata.setAttribute(ELM_WHERE, where);
                    }
                    if (orderby!=null) {
                        querydata.setAttribute(ELM_ORDERBY, orderby);
                    }
                    if (directions != null) {
                        querydata.setAttribute(ELM_DIRECTIONS, directions);
                    }
                    out.appendChild(querydata);

                    // get node template
                    Element node = getFirstElement(query);

                    if (xpath.indexOf("/*@") != 0) {
                        Element err = addContentElement(ERROR,"invalid xpath",out);
                        err.setAttribute(ELM_TYPE, IS_CLIENT);
                    } else {
                        String nodepath = xpath.substring(3);
                        NodeIterator i = null;

                        if (nodepath.indexOf("/") == -1) {
                            // If there is no '/' seperator, we only get fields from one nodemanager. This is the fastest
                            // way of getting those.
                            i = cloud.getNodeManager(nodepath).getList(where, orderby, directions).nodeIterator();
                        } else {
                            // If there are '/' seperators, we need to do a multilevel search. Therefore we first need to
                            // get a list of all the fields (as subnodes) to query.
                            String fields = "";
                            Element field = getFirstElement(node, FIELD);
                            nodepath = nodepath.replace('/', ',');
                            while (field != null) {
                                String fname = field.getAttribute(ELM_NAME);
                                if (!fields.equals(""))
                                    fields += ",";
                                fields += fname;
                                field = getNextElement(field, FIELD);
                            }
                            i = cloud.getList("", nodepath, fields, where, orderby, directions, null, true).nodeIterator();
                        }

                        for(; i.hasNext(); ) {
                            org.mmbase.bridge.Node n = i.nextNode();
                            Element data = doc.createElement(OBJECT);
                            data.setAttribute(ELM_NUMBER, ""+n.getNumber());
                            querydata.appendChild(data);
                            getDataNode(node,data,n);
                        }
                    }
                }
            } else {
                Element err = addContentElement(ERROR,"Unknown subtag in getlist: "+query.getTagName(),out);
                err.setAttribute(ELM_TYPE, IS_PARSER);
            }
            query=getNextElement(query);
        }
    }

    /**
     * Handles a put call.
     * This method accepts a DOM element, which should contain as it
     * child nodes elements describing the original and the new cloud.
     * @param in the element that described the <code>put</code> call.
     *           The childnodes should describe the old and the new cloud.
     * @param out the element that described the <code>put</code> result.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @param repository Repository that contains the blobs
     */
    public void put(Element in, Element out, Cloud cloud, Map repository) {
        // first collect all new and original nodes
        Map originalNodes     = new HashMap();
        Map newNodes          = new HashMap();
        Map originalRelations = new HashMap();
        Map newRelations      = new HashMap();

        // get all the needed info from the xml stream
        Element query = getFirstElement(in);
        while (query!=null) { // select child tags, should be 'original' or 'new'
            if (query.getTagName().equals(ORIGINAL) || query.getTagName().equals(NEW)) {
                boolean isOriginal = query.getTagName().equals(ORIGINAL);
                Element node = getFirstElement(query);

                while (node!=null) { // select all child tags, should be 'object'
                    if (node.getTagName().equals(OBJECT) || node.getTagName().equals(RELATION)) {
                        boolean isRelation = node.getTagName().equals(RELATION);

                        // store the values of one node
                        Map values = new HashMap();
                        if (isRelation) {
                            if (isOriginal) {
                                originalRelations.put(node.getAttribute(ELM_NUMBER), values);
                            } else {
                                newRelations.put(node.getAttribute(ELM_NUMBER), values);
                            }
                        } else {
                            if (isOriginal) {
                                originalNodes.put(node.getAttribute(ELM_NUMBER), values);
                            } else {
                                newNodes.put(node.getAttribute(ELM_NUMBER),values);
                            }
                        }
                        if (! isOriginal) {
                            values.put("_status",node.getAttribute(ELM_STATUS));
                        }

                        String context = node.getAttribute(ELM_CONTEXT);
                        if (context != null && !context.equals("")) {
                            values.put("_context", context);
                        }

                        if (isRelation) {
                            String role = node.getAttribute(ELM_ROLE);
                            if (role!=null) values.put("_role",role);

                            String source = node.getAttribute(ELM_SOURCE);
                            if (source!=null) values.put("_source",source);

                            String destination = node.getAttribute(ELM_DESTINATION);
                            if (destination!=null) values.put("_destination", destination);
                        } else {
                            String type = node.getAttribute(ELM_TYPE);
                            if (type!=null) values.put("_otype", type);
                        }
                        Element field = getFirstElement(node);
                        while (field != null) { // select all child tags, should be 'fields'
                            if (field.getTagName().equals(FIELD)) {
                                String fieldname = field.getAttribute(ELM_NAME);
                                String href = field.getAttribute(ELM_HREF);
                                String encoding = field.getAttribute(ELM_ENCODING);
                                if (!href.equals("")) {
                                    // binary data.
                                    Object repval = repository.get(href);
                                    if (repval != null) {
                                        values.put(fieldname, repval);
                                        // also retrieve and set filename
                                        if(field.getFirstChild() != null) {
                                            values.put("filename", field.getFirstChild().getNodeValue());
                                        }
                                    }
                                } else if (!encoding.equals("")) {
                                    if (encoding.toLowerCase().equals("base64")) {
                                        values.put(fieldname, new Encode("BASE64").decodeBytes(field.getFirstChild().getNodeValue()));
                                    }
                                } else {
                                    if(field.getFirstChild() == null) {
                                        values.put(fieldname, "");
                                    } else {
                                        values.put(fieldname, field.getFirstChild().getNodeValue());
                                    }
                                }
                            }
                            field = getNextElement(field);
                        }
                    }
                    node = getNextElement(node);
                }
            }
            query = getNextElement(query);
        }
        // are there new nodes to handle ?
        if (newNodes.size() > 0 || newRelations.size() > 0) {
            Transaction trans = cloud.createTransaction();
            Map addedNodes     = new HashMap ();
            Map addedRelations = new HashMap();
            if (mergeClouds(originalNodes, newNodes, originalRelations, newRelations, addedNodes, addedRelations, out, trans) ) {
                trans.commit();
                // retrieve all numbers and reset them to the right value
                // This is possible, as the nodes themselves contain this info after the
                // transaction
                //
                for (Iterator i = addedNodes.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry me=(Map.Entry)i.next();
                    org.mmbase.bridge.Node n = (org.mmbase.bridge.Node)me.getKey();
                    Element oe = (Element)me.getValue();
                    oe.setAttribute(ELM_NUMBER, n.getStringValue("number"));
                }
                // retrieve all numbers, snumbers, dnumbers and reset them to the right value
                for (Iterator i = addedRelations.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry me=(Map.Entry)i.next();
                    org.mmbase.bridge.Node n = (org.mmbase.bridge.Node)me.getKey();
                    Element re = (Element)me.getValue();
                    re.setAttribute(ELM_NUMBER, n.getStringValue("number"));
                    re.setAttribute(ELM_SOURCE, "" + n.getIntValue("snumber"));
                    re.setAttribute(ELM_DESTINATION, "" + n.getIntValue("dnumber"));
                }
            } else {
                trans.cancel();
            }
        }
    }


    /**
     * Converts the value of a field to a node reference (either a temporary id or an object alias or number).
     * Used for determing values of the snumber and dnumber relation fields.
     * @param name the name of the field
     * @param values a Map with field values
     * @param aliases a Map with mappings from XML aliases to node reference values
     * @return the node reference value
     */
    protected String getNodeReferenceFromValue(String name, Map values, Map aliases) {
        String result=null;
        String value=(String)values.get(name);
        Object tmp=aliases.get(value);
        if (tmp==null) {
            // oke its not a temporary alias, return the original value
            result=value;
        } else {
            result=""+tmp;
        }
        return result;
    }

    /**
     * Fills the fields of the specified node with the supplied values.
     * The node's XML element is upadeytd with the affected field elements.
     * This method does not validate whether a node's values have changed since the
     * transaction started.
     * @param alias the node alias in the put tree
     * @param node the node to fill
     * @param objectelement the XML element to fill with the changed data for feedback
     * @param values a Map with new node values
     * @return true if succesful, false if an error ocurred
     */
    protected boolean fillFields(String alias, org.mmbase.bridge.Node node, Element objectelement, Map values) {
        return fillFields(alias, node, objectelement, values, null);
    }

    /**
     * Fills the fields of the specified node with the supplied values.
     * The node's XML element is updated with the affected field elements.
     * This method validates whether a node's values have changed since the
     * transaction started, and fails if this was teh acse.
     * @param alias the node alias in the put tree
     * @param node the node to fill
     * @param out the XML element to fill with the changed data for feedback
     * @param values a Map with new node values
     * @param originalValues a Map with the original values of the node, needed for checking.
     *        if <code>null</code>, no checking takes place
     * @return true if succesful, false if an error ocurred
     */
    protected boolean fillFields(String alias, org.mmbase.bridge.Node node, Element out, Map values, Map originalValues) {
        node.getCloud().setProperty(Cloud.PROP_XMLMODE, "flat");
        for (Iterator i = values.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry me = (Map.Entry)i.next();
            String key = (String)me.getKey();
            if (isEditableField(node.getNodeManager(),key)) {
                Object value = me.getValue();
                DataType dt = node.getNodeManager().getField(key).getDataType();
                String changes = (String) properties.getProperties().get(PROP_CHANGES);
                if ((! CHANGES_IGNORE.equals(changes)) &&
                    (originalValues != null) &&
                    (!(value instanceof byte[])) && (!"binary".equals(dt.getBaseTypeIdentifier())) ) { // XXX: currently, we do not validate on byte fields
                    String originalValue = (String) originalValues.get(key);
                    String  mmbaseValue;
                    if (dt instanceof DateTimeDataType ||
                        dt instanceof LongDataType ||
                        dt instanceof IntegerDataType) {
                        // have to convert ourselves because bridge will use user-defined formatting
                        mmbaseValue = node.isNull(key) ? "" : "" + node.getLongValue(key);
                    } else {
                        mmbaseValue = node.isNull(key) ? null : node.getStringValue(key);
                    }
                    if (mmbaseValue == null) {
                        if ("".equals(originalValue)) {
                            // XML cannot make difference between NULL and empty.
                            originalValue = null;
                        }
                        if ("".equals(value)) {
                            value = null;
                        }
                    }
                    if ((originalValue != null ) && !originalValue.equals(mmbaseValue)) {
                        String message = "Node was changed in the cloud, node number : " + alias + " field name '" + key + "' value found: '" + mmbaseValue + "' value expected '" + originalValue + "' changes: " + changes;
                        // give error node was changed in cloud
                        if (CHANGES_WARN.equals(changes)) {
                            log.warn(message);
                        } else {
                            log.debug(message);
                            Element err = addContentElement(ERROR, message, out);
                            err.setAttribute(ELM_TYPE, IS_SERVER);
                            return false;
                        }
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("Setting field " + key + " to '" + value + "'");
                }
                if (value instanceof byte[]) {
                    node.setValue(key, value);
                } else {
                    node.setStringValue(key, value != null ? Casting.toString(value) : null);
                }
                Element fieldElement = doc.createElement(FIELD);
                fieldElement.setAttribute(ELM_NAME, key);
                if (!(value instanceof byte[])) {
                    Text tel = doc.createTextNode(value == null ? "" : value.toString());
                    fieldElement.appendChild(tel);
                }
                out.appendChild(fieldElement);
            }
        }
        return true;
    }

    /**
     * Creates a new node.
     * @param alias the node alias in the put tree
     * @param values a Map with new node values
     * @param aliases a Map with mappings from XML aliases to node reference values
     * @param addedNodes a Map used to keep associations between xml elements and newly created nodes,
     *                   needed for resolving new node numbers
     * @param out the element that describes 'new' cloud.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @return true if succesful, false if an error ocurred
     */
    protected boolean putNewNode(String alias, Map values, Map aliases, Map addedNodes, Element out, Cloud cloud) {
        String type = (String) values.get("_otype");

        NodeManager nm = cloud.getNodeManager(type);
        org.mmbase.bridge.Node newnode = nm.createNode();
        Element objectelement = doc.createElement(OBJECT);
        objectelement.setAttribute(ELM_TYPE, type);
        fillFields(alias, newnode, objectelement, values);

        String context = (String) values.get("_context");
        if (context!=null) {
            newnode.setContext(context);
        }
        newnode.commit();
        int number = newnode.getNumber();
        if (log.isDebugEnabled()) {
            log.debug("Created new node " + number);
        }
        aliases.put(alias,new Integer(number));
        objectelement.setAttribute(ELM_NUMBER,""+number);
        objectelement.setAttribute(ELM_OLDNUMBER, alias);
        // keep in transaction for later update...
        addedNodes.put(newnode,objectelement);
        // add node to response
        out.appendChild(objectelement);

        return true;
    }

    /**
     * Creates a new relation.
     * @param alias the node alias in the put tree
     * @param values a Map with new node values
     * @param aliases a Map with mappings from XML aliases to node reference values
     * @param addedRelations a Map used to keep associations between xml elements and newly created relations,
     *                   needed for resolving new node numbers and references
     * @param out the element that describes 'new' cloud.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @return true if succesful, false if an error ocurred
     */
    protected boolean putNewRelation(String alias, Map values, Map aliases, Map addedRelations, Element out, Cloud cloud) {
        String role=(String)values.get("_role");

        RelationManager relman=cloud.getRelationManager(role);
        String sourcenumber=getNodeReferenceFromValue("_source",values, aliases);
        String destinationnumber=getNodeReferenceFromValue("_destination",values, aliases);
        if (log.isDebugEnabled()) log.debug("Creating a relations between node " + sourcenumber + " and " + destinationnumber);
        Relation newnode=relman.createRelation(cloud.getNode(sourcenumber), cloud.getNode(destinationnumber));
        Element relationelement=doc.createElement(RELATION);
        // note that source and destination may be switched (internally) when you
        // commit a bi-directional relation, if the order of the two differs in typerel
        relationelement.setAttribute(ELM_SOURCE,""+sourcenumber);
        relationelement.setAttribute(ELM_DESTINATION,""+destinationnumber);
        relationelement.setAttribute(ELM_ROLE,role);
        fillFields(alias,newnode,relationelement,values);

        String context = (String) values.get("_context");
        if (context != null) {
            newnode.setContext(context);
        }
        newnode.commit();
        int number = newnode.getNumber();
        if (log.isServiceEnabled()) log.service("Created new relation " + number);
        aliases.put(alias, new Integer(number));
        // keep in transaction for later update...
        addedRelations.put(newnode,relationelement);
        // add node to response
        relationelement.setAttribute(ELM_NUMBER,""+number); // result in transaction ???
        out.appendChild(relationelement);
        return true;

    }


    /**
     * Deletes a node.
     * @param alias the node alias in the put tree
     * @param originalValues a Map with the original values of the node
     * @param out the element that describes 'new' cloud.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @return true if succesful, false if an error ocurred
     */
    protected boolean putDeleteNode(String alias, Map originalValues, Element out, Cloud cloud) {
        // check if this org node is also found in
        // mmbase cloud and if its still the same
        // also check if its the same type
        String type = (String)originalValues.get("_otype");

        NodeManager nm = cloud.getNodeManager(type);

        org.mmbase.bridge.Node mmbaseNode = cloud.getNode(alias);
        if (mmbaseNode.getNodeManager().getName().equals(nm.getName()) ) {
            mmbaseNode.delete(true);
            return true;
        } else {
            // give error its the wrong type
            Element err = addContentElement(ERROR,"Node not same type as in the cloud, node number : "+alias+", cloud type="+mmbaseNode.getNodeManager().getName()+", expected="+nm.getName()+")",out);
            err.setAttribute(ELM_TYPE, IS_SERVER);
            return false;
        }

     }

    /**
     * Deletes a relation.
     * @param alias the node alias in the put tree
     * @param originalValues a Map with the original values of the relation
     * @param out the element that describes 'new' cloud.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @return true if succesful, false if an error ocurred
     */
    protected boolean putDeleteRelation(String alias, Map originalValues, Element out, Cloud cloud) {
        String role = (String)originalValues.get("_role");

        RelationManager relman = cloud.getRelationManager(role);

        org.mmbase.bridge.Node mmbaseNode = cloud.getNode(alias);
        // check if they are the same type
        if (mmbaseNode.getNodeManager().getName().equals(relman.getName()) ) {
            mmbaseNode.delete(true);
            return true;
        } else {
            Element err = addContentElement(ERROR,"Node not same type as in the cloud, node number : "+alias+", cloud type="+mmbaseNode.getNodeManager().getName()+", expected="+relman.getName()+")",out);
            err.setAttribute(ELM_TYPE, IS_SERVER);
            return false;
        }
     }

    /**
     * Changes a relation
     * @param alias the node alias in the put tree
     * @param values a Map with new node values
     * @param originalValues a Map with original node values, used for validation
     * @param aliases a Map with mappings from XML aliases to node reference values
     * @param out the element that describes 'new' cloud.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @return true if succesful, false if an error ocurred
     */
    protected boolean putChangeRelation(String alias, Map values, Map originalValues, Map aliases, Element out, Cloud cloud) {
        String role = (String)values.get("_role");

        RelationManager relman = cloud.getRelationManager(role);
        String sourcenumber=getNodeReferenceFromValue("_source",values, aliases);
        String destinationnumber=getNodeReferenceFromValue("_destination",values, aliases);

        if (log.isDebugEnabled()) log.debug("Changing a relation between node " + sourcenumber + " and " + destinationnumber);

        org.mmbase.bridge.Node mmbaseNode = cloud.getNode(alias);
        // check if they are the same type
        if (mmbaseNode.getNodeManager().getName().equals(relman.getName()) ) {
            // create new node for response
            Element relationElement = doc.createElement(RELATION);
            relationElement.setAttribute(ELM_SOURCE, "" + sourcenumber);
            relationElement.setAttribute(ELM_DESTINATION, "" + destinationnumber);
            relationElement.setAttribute(ELM_ROLE, role);
            if (!fillFields(alias, mmbaseNode, relationElement, values, originalValues)) {
                out.appendChild(relationElement);
                return false;
            }
            mmbaseNode.commit();
            // add node to response
            out.appendChild(relationElement);
            return true;
        } else {
            // give error its the wrong type
            Element err = addContentElement(ERROR, "Node not same type as in the cloud, node number : " + alias + ", cloud type=" + mmbaseNode.getNodeManager().getName()+", expected=" + relman.getName() + ")", out);
            err.setAttribute(ELM_TYPE, IS_SERVER);
            return false;
        }
    }

    /**
     * Changes a node.
     * @param alias the node alias in the put tree
     * @param values a Map with new node values
     * @param originalValues a Map with original node values, used for validation
     * @param aliases a Map with mappings from XML aliases to node reference values
     * @param out the element that describes 'new' cloud.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     * @return true if succesful, false if an error ocurred
     */
    protected boolean putChangeNode(String alias, Map values, Map originalValues, Map aliases, Element out, Cloud cloud) {
        // now check if this org node is also found in
        // mmbase cloud and if its still the same
        // also check if its the same type
        String type = (String)values.get("_otype");

        NodeManager nm = cloud.getNodeManager(type);

        org.mmbase.bridge.Node mmbaseNode = cloud.getNode(alias);
        // check if they are the same type
        if (mmbaseNode.getNodeManager().getName().equals(nm.getName()) ) {
            // create new node for response
            Element objectElement = doc.createElement(OBJECT);
            objectElement.setAttribute(ELM_TYPE, type);
            objectElement.setAttribute(ELM_NUMBER, alias);
            if (!fillFields(alias, mmbaseNode, objectElement, values, originalValues)) {
                out.appendChild(objectElement);
                return false;
            }
            mmbaseNode.commit();
            // add node to response
            out.appendChild(objectElement);
            return true;
        } else {
            // give error its the wrong type
            Element err = addContentElement(ERROR,"Node not same type as in the cloud, node number : " + alias + ", cloud type=" + mmbaseNode.getNodeManager().getName()+", expected=" + nm.getName() + ")", out);
            err.setAttribute(ELM_TYPE, IS_SERVER);
            return false;
        }

    }

    /**
     * Performs the put within a transaction.
     * @param originalNodes the nodes in the original cloud, used in validation
     * @param newNodes the nodes in the new cloud
     * @param originalRelations the relations in the original cloud, used in validation
     * @param newRelations the relations in the new cloud
     * @param addedNodes a Map used to keep associations between xml elements and newly created nodes,
     *                   needed for resolving new node numbers and references
     * @param addedRelations a Map used to keep associations between xml elements and newly created relations,
     *                   needed for resolving new node numbers and references
     * @param out the element that described the <code>put</code> result.
     *           The result of the put (an error or the resulting cloud) should be added
     *           as childs to this element.
     * @param cloud the cloud to work on
     */
    protected boolean mergeClouds(Map originalNodes, Map newNodes, Map originalRelations, Map newRelations,
                                  Map addedNodes, Map addedRelations, Element out, Cloud cloud) {
        Map aliases = new HashMap(); // hash from alias names to real names

        // create new tag and add it to response
        Element newElement = doc.createElement(NEW);
        out.appendChild(newElement);

        // lets enum all the newNodes
        for (Iterator i = newNodes.entrySet().iterator(); i.hasNext(); ) {
            // handle the several cases we have when we have
            // a new node (merge, really new)
            Map.Entry me = (Map.Entry)i.next();
            String alias = (String)me.getKey();
            Map values = (Map)me.getValue();
            String status = (String)values.get("_status");

            // is it a new node if so create one and remember its alias
            if (status != null && status.equals("new")) {
                if (!putNewNode(alias, values, aliases, addedNodes, newElement, cloud)) return false;
            } else if (status != null && status.equals("delete")) {
                // to check if they send a original
                // XXX: no original is not an error ???
                Map originalValues = (Map) originalNodes.get(alias);
                if (originalValues!=null) {
                    if (!putDeleteNode(alias, originalValues, newElement, cloud)) return false;;
                }
            } else if (status == null || status.equals("") || status.equals("change")) {
                // check if they send a original
                Map originalValues = (Map )originalNodes.get(alias);
                if (originalValues != null) {
                    if(!putChangeNode(alias, values, originalValues, aliases, newElement, cloud)) return false;
                } else {
                    // give error not a org. node
                    Element err = addContentElement(ERROR,"Node not defined in original tag, node number : " + alias, out);
                    err.setAttribute(ELM_TYPE, IS_SERVER);
                    return false;
                }
            } else {
                // give error not a org. node
                Element err = addContentElement(ERROR,"Invalid status '" + status + "' for node : "+alias,out);
                err.setAttribute(ELM_TYPE, IS_SERVER);
                return false;
            }
        }

        // now handle all the relations
        for (Iterator i = newRelations.entrySet().iterator(); i.hasNext(); ) {
            // handle the several cases we have when we have
            // a new node (merge, really new)
            Map.Entry me = (Map.Entry)i.next();
            String alias = (String)me.getKey();
            Map values = (Map)me.getValue();
            String status = (String)values.get("_status");

            // is it a new node if so create one and remember its alias
            if (status != null && status.equals("new")) {
                if (!putNewRelation(alias, values, aliases, addedRelations, newElement, cloud)) return false;
            } else if (status != null && status.equals("delete")) {
                Map originalValues = (Map)originalRelations.get(alias);
                if (originalValues != null) {
                    if(!putDeleteRelation(alias, originalValues, newElement, cloud)) return false;;
                } // no error ???
            } else if (status == null || status.equals("") || status.equals("change")) {
                // check if we have an original allready existing relation
                Map originalValues = (Map) originalRelations.get(alias);
                if (originalValues != null) {
                    if(!putChangeRelation(alias, values, originalValues, aliases, newElement, cloud)) return false;
                } else {
                    Element err = addContentElement(ERROR, "Relation not found between original values, node number: " + alias, out);
                    err.setAttribute(ELM_TYPE, IS_SERVER);
                    return false;
                }
            } else {
                // give error not a org. node
                Element err = addContentElement(ERROR, "Invalid status " + status + " for (relation) node : " + alias, out);
                err.setAttribute(ELM_TYPE, IS_SERVER);
                return false;
            }
        }
        return true;
    }

    /**
     * Handles a request running one or more RPCs.
     * This method accepts a root DOM element, which should contain as it
     * child nodes elements describing the call(s) to perform.
     * Each element is an xml element with as the tagname the name of the
     * call to run. Valid calls are getdata, getrelations, getlist, getconstraints,
     * getnew, and put.
     * The calls are redirected to the appropriate encapsulating method in
     * this class. The end result of each call should be a DOM tree, built
     * using the given parameters, which is then appended to the out element.
     * <br />
     * XXX:This method runs all commands in order of entrance. It does
     * not currently check whether all child nodes refer to the same call.
     * This means that it is possible to mix 'getdata' and 'getrelations' calls
     * in one request. Ina dditon, it ignores calls it does not know how to handle.
     * as described above, this should be handled by the xml parser.
     * Note also that if no id was given for a call, a NullPointer exception is thrown.
     *
     * @param in the element that described the request (or input).
     *           The childnodes should describe the calls to perform.
     * @param out the element that described the response (or return value).
     *           Results of calls should be added as childs to this element.
     * @param cloud the cloud to work on - if null, a cloud will be created by the Dove class
     * @param repository Repository that contains the blobs
     */
    public void doRequest(Element in, Element out, Cloud cloud, Map repository) {
        // set repository for blobs
        if (repository == null) repository = new HashMap();

        Element command = getFirstElement(in);
        if ((cloud == null)) {
            // make new cloud if not specified,
            cloud = checkSecurity(command,out);
            if(cloud == null) {
                return; // Athentication failed, so stop
            }
            command = getNextElement(command);
        }
        // skip security if cloud was given - or throw exception???
        if (SECURITY.equals(command.getTagName())) {
            command = getNextElement(command);
        }

        while (command!=null) {
            String cmd = command.getTagName();
            Element data = doc.createElement(cmd);
            if (command.hasAttribute(ELM_ID)) {
                data.setAttribute(ELM_ID, command.getAttribute(ELM_ID));
            }
            out.appendChild(data);
            if (cmd.equals(GETDATA)) {
                getData(command, data, cloud);
            } else if (cmd.equals(GETNEW)) {
                getNew(command, data, cloud);
            } else if (cmd.equals(GETNEWRELATION)) {
                getNewRelation(command, data, cloud);
            } else if (cmd.equals(GETRELATIONS)) {
                getRelations(command, data, cloud);
            } else if (cmd.equals(GETCONSTRAINTS)) {
                getConstraints(command, data, cloud);
            } else if (cmd.equals(GETLIST)) {
                getList(command, data, cloud);
            } else if (cmd.equals(PUT)) {
                put(command, data, cloud, repository);
            } else {
                Element err = addContentElement(ERROR, "Unknown command: " + cmd, data);
                err.setAttribute(ELM_TYPE, IS_PARSER);
            }
            command = getNextElement(command);
        }
    }

    /**
     * checkSecurity tries to authenticate the user that performs the request
     * @return true if the user is authenticated, false otherwise
     */
    private Cloud checkSecurity(Element command, Element out) {
        Element data = doc.createElement(SECURITY);
        out.appendChild(data);

        if (command!=null && command.getTagName().equals(SECURITY)) {
            String userName = command.getAttribute(SECURITY_NAME); // retrieve name

                String password = command.getAttribute(SECURITY_PASSWORD); // retrieve password
                String methodName = command.getAttribute(SECURITY_METHOD ); // retrieve method name
                if ((methodName == null) || (methodName.equals(""))) methodName="name/password";
                String cloudName = command.getAttribute(SECURITY_CLOUD); // retrieve cloud name
                if ((cloudName == null) || (cloudName.equals(""))) cloudName="mmbase";
                Map user = new HashMap();
                if ((userName!=null) && (!userName.equals(""))) {
                    user.put("username", userName);
                    user.put("password", password);
                }
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud(cloudName, methodName, user);
                return cloud;
        } else {
            log.warn("Authentication error  (security info missing)");
            Element err = addContentElement(ERROR, "Authentication error (security info missing).", data);
            err.setAttribute(ELM_TYPE, IS_CLIENT);
        }
        return null;
    }
}

