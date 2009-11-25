/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.mmbase.applications.vprowizards.spring.*;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.DateTime;
import org.mmbase.applications.vprowizards.spring.util.HTMLFilterUtils;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.springframework.web.multipart.MultipartFile;

/**
 * this is a template class for the 'real' node actions
 * 
 * @author ebunders
 * 
 */
public abstract class AbstractNodeAction extends Action {

    private Map<String, String> fields = new HashMap<String, String>();
    // private Map<String, DateTime> dateFields = new HashMap<String, DateTime>();
    private Map<String, DateTime> dateFields = MapUtils.lazyMap(new HashMap<String, DateTime>(), FactoryUtils
            .instantiateFactory(DateTime.class));
    private String id = null;
    private MultipartFile file = null;
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(AbstractNodeAction.class);

    /**
     *This property can contain a set of comma-separated fields that will be filtered by
     * {@link HTMLFilterUtils#filter(String)}
     */
    private String htmlFields = "";

    private Node node;

    private boolean nodeChanged = false;

    private ResultContainer resultContainer;

    /**
     * This final method calls the different template methods, that are either abstract or have a default
     * implementation.
     * <ul>
     * <li>{@link #getNode(Transaction, HttpServletRequest)} is called to obtain a node instance
     * <li>{@link #shouldProcess(Node)} is called to determine if the operation should proceed (if this depends on some
     * specific criterium.)
     * <li>{@link #processNode(Node, ResultContainer)} is called after the default fields are set (fields collection,
     * datefields collection and file field).
     * <li>{@link #createCacheFlushHints()} is called to return the appropriate cacheflush hints. Use
     * {@link #hasChanged()} to find out if the current node has been updated. Use
     * {@link #addCachFlushHint(CacheFlushHint)} to add hints
     * </ul>
     * When the id is set on this action, the node is added to the idmap.
     * 
     * @throws
     * @throws XNIException
     */
    @Override
    public final void process(ResultContainer resultContainer) {
        this.resultContainer = resultContainer;
    
        // get the node
        node = createNode(resultContainer.getTransaction(), resultContainer.getIdMap(), resultContainer.getRequest());
    
        if (hasErrors()) {
            return;
        }
    
        if (node == null && isNodeNullIllegal()) {
            throw new IllegalStateException(
                    "No node has been provided, and no error has been set. Either of these should happen*");
        }
    
        // now check if no illegal fields are set for this node type
        if (node != null) {
            checkBasicFields();
        }
    
        // no error and no overridden 'proceed' flag? Set the values and
        // call the post processing callback method.
        if (!hasErrors() && shouldProcess(node)) {
            if (node != null) {
                log.debug("setting the field values on the node");
                setBasicFields();
                if (node.getNodeManager().hasField("handle")) {
                    setHandlerField();
                }
            }
    
            if (resultContainer.hasFieldErrors()) {
                return;
            }
    
            if (node != null) {
                if (getId() != null) {
                    resultContainer.getIdMap().put(getId(), node);
                    log.debug("node [" + node + "] is registered under id " + getId());
                }
                processNode(resultContainer.getTransaction());
            }
            
            //now check for html fields, and filter them if need be.
            if(!StringUtils.isBlank(htmlFields)){
                String[] htmlFieldArray = htmlFields.split("\\s*,\\s*");
                for(String fieldName : htmlFieldArray){
                    checkField(fieldName);
                }
                //if this created an error, nonexisting fields are set.
                if (resultContainer.hasFieldErrors()) {
                    return;
                }
                //filter the field values.
                for(String fieldName: htmlFieldArray){
                    try {
                        log.debug("HTML filtering field " + fieldName );
                        node.setStringValue(fieldName,HTMLFilterUtils.filter(node.getStringValue(fieldName)));
                    } catch (Exception e) {
                        String msg = "Something went wrong filtering field "+fieldName+" with the HTML filter: "+e.getMessage();
                        log.error(msg, e);
                        throw new RuntimeException(msg, e);
                    }
                }
            }
    
            // even with a null node it can be necessary to create a cache flush hint
            // for instance when a node was deleted.
            createCacheFlushHints();
        }
    }

    public void setHtmlField(String htmlField) {
        if(!StringUtils.isBlank(htmlFields)){
            htmlFields = htmlFields + ",";
        }
        htmlFields = htmlFields + htmlField;
    }

    public Map<String, DateTime> getDateFields() {
        return dateFields;
    }

    public void setDateFields(Map<String, DateTime> dateFields) {
        this.dateFields = dateFields;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        log.info("** id is set to " + id);
        this.id = id;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }

    /**
     * This template method determines if it is an error when the node is null. Override this for concrete actions that
     * actually don't need a node always.
     * 
     * @return true by default.
     */
    protected boolean isNodeNullIllegal() {
        return true;
    }

    /**
     * this template method is called before any changes are made to the node to edit. if it returns false, no more
     * action will be taken. Use this if you want to use some kind of switch.
     * 
     * @param node
     *            the current node.
     * @return true if there is no reason not to process this node
     */
    protected boolean shouldProcess(Node node) {
        return true;
    }

    /**
     * This template method is called to obtain the node for this action. it is the responsibility of the implementation
     * of this method to set a global error (using addGlobalError) when something goes wrong. In that case the action
     * stops.
     * 
     * @param transaction
     *            the mmmbase transaction
     * @return the node this action should handle or null if an error occurs
     * @param request
     * 
     */
    abstract protected Node createNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request);

    /**
     * This template method is called after the values that have been injected in this action in the fields and
     * datefields collections, as well as the file field have been set on the node. The default implementation does
     * nothing
     * 
     * @param node
     * @param resultContainer
     */
    protected void processNode(Transaction transaction) {
    }

    protected final Locale getLocale() {
        return resultContainer.getLocale();
    }

    /**
     * @return the node that is created for this node action.
     */
    protected final Node getNode() {
        return node;
    }

    /**
     * call this method to set the changed flag on this node
     */
    protected final void setChanged() {
        nodeChanged = true;
    }

    protected final boolean hasChanged() {
        return nodeChanged;
    }

    /**
     * Creates a field error for this action
     * 
     * @param field
     * @param key
     * @param placeholderValues
     */
    protected final void addFieldError(String field, String key, String[] placeholderValues) {
        resultContainer.addFieldError(new FieldError(field, key, placeholderValues, getLocale()));
    }

    /**
     * Create a field error for this action, using a key without place holder values
     * 
     * @param field
     * @param key
     */
    protected final void addFieldError(String field, String key) {
        resultContainer.addFieldError(new FieldError(field, key, getLocale()));
    }

    /**
     * Creates a field error for this action, where there is some sort of error when setting the field. This version
     * does not take the (offending) field value but the error message. This method uses it's own error message key.
     * 
     * @param field
     * @param message
     */
    protected final void addFieldErrorTypeMessage(String field, String message) {
        resultContainer.addFieldError(new FieldError(field, "error.field.message", new String[] { field, message },
                getLocale()));
    }

    /**
     * Creates a field error for this action, where the value set on some field is invalid. This method uses it's own
     * error message key.
     * 
     * @param field
     * @param value
     */
    protected final void addFieldErrorTypeValue(String field, String value) {
        resultContainer.addFieldError(new FieldError(field, "error.field.value", new String[] { field, value },
                getLocale()));
    }

    /**
     * Creates a global error for this action.
     * 
     * @param key
     * @param placeholderValues
     */
    protected final void addGlobalError(String key, String[] placeholderValues) {
        resultContainer.addGlobalError(new GlobalError(key, placeholderValues, getLocale()));
    }

    /**
     * Creates a global error for this action.
     * 
     * @param key
     */
    protected final void addGlobalError(String key) {
        resultContainer.addGlobalError(new GlobalError(key, getLocale()));
    }

    protected final void addCachFlushHint(CacheFlushHint hint) {
        resultContainer.addCacheFlushHint(hint);
    }

    /**
     * This template method is called when the node to edit is changed. custom cachflush hints can be set here. This
     * class dous not know if the current node is updated or created, so it dousn't know what kind of events to create.
     * 
     */
    protected abstract void createCacheFlushHints();

    /**
     * Check if a relation is possible from the given source to the given destination with the given relation manager.
     * 
     * @param relationManager
     * @return
     */
    protected final boolean checkTypeRel(RelationManager relationManager, Node sourceNode, Node destinationNode) {
        NodeManager typerelManager = relationManager.getCloud().getNodeManager("typerel");
        String constraints = String.format("snumber=%s AND dnumber=%s AND rnumber=%s", ""
                + sourceNode.getNodeManager().getNumber(), "" + destinationNode.getNodeManager().getNumber(), ""
                + relationManager.getNumber());
        NodeList nl = typerelManager.getList(constraints, null, null);
        if (nl.size() == 0) {
            log.warn(String.format(
                    "could not find typerel record with these constraints: '%s', where snumber type is %s"
                            + " and dnumber type is %s and rnumber type is %s", constraints, sourceNode
                            .getNodeManager().getName(), destinationNode.getNodeManager().getName(), relationManager
                            .getName()));
            addGlobalError("error.create.relation.typerel", new String[] { sourceNode.getNodeManager().getName(),
                    destinationNode.getNodeManager().getName(), relationManager.getName() });
            return false;
        }
        return true;
    }

    /**
     * can the current owner create a node of this type? set global error when fail.
     * 
     * @param nodeManager
     * @return true when allowed.
     */
    protected final boolean mayWrite(NodeManager nodeManager) {
        if (nodeManager == null) {
            throw new NullPointerException("argument nodeManager is null");
        }
        boolean mayWrite = nodeManager.mayWrite();
        if (!mayWrite) {
            addGlobalError("error.authorization.write", new String[] { nodeManager.getName() });
        }
        return mayWrite;
    }

    /**
     * can the current owner create a node of this type? set global error when fail.
     * 
     * @param nodeManager
     * @return true when allowed.
     */
    protected final boolean mayCreate(NodeManager nodeManager) {
        if (nodeManager == null) {
            throw new NullPointerException("argument nodeManager is null");
        }
        boolean mayCreate = nodeManager.mayCreateNode();
        if (!mayCreate) {
            addGlobalError("error.authorization.create", new String[] { nodeManager.getName() });
        }
        return mayCreate;
    }

    /**
     * can the current owner delete this node? set global error when fail.
     * 
     * @param nodeManager
     * @return true when allowed.
     */
    protected final boolean mayDelete(Node node) {
        if (node == null) {
            throw new NullPointerException("argument node is null");
        }
        boolean mayDelete = node.mayDelete();
        if (!mayDelete) {
            addGlobalError("error.authorization.delete", new String[] { node.getNumber() + "",
                    node.getNodeManager().getName() });
        }
        return mayDelete;
    }

    protected final boolean hasErrors() {
        return (resultContainer.hasGlobalErrors() || resultContainer.hasFieldErrors());
    }

    /**
     * Check if all the fields set for this node action actually exist in the nodemanager.
     */
    private void checkBasicFields() {
        for (String field : fields.keySet()) {
            checkField(field);
        }
    }

    private void checkField(String field) {
        if (!node.getNodeManager().hasField(field)) {
            log.warn(String.format(
                    "You try to set field '%s' on a node of type '%s', but the nodetype does not have this field",
                    field, node.getNodeManager().getName()));
            addGlobalError("error.field.unknown", new String[] { field, this.getClass().getName(),
                    node.getNodeManager().getName() });
        }
    }

    /**
     * Set the fields, dateFields and file on the given node. Errors are created when fields are not part of the present
     * nodes manager. TODO: use datatypes to validate the input. TODO: a builder can have more than one binary field
     * with another name than 'handle'
     */
    private final void setBasicFields() {
        NodeManager nm = node.getNodeManager();
        if (fields.isEmpty()) {
            log.debug("** no fields set for this createnode action");
        }
        for (String field : fields.keySet()) {

            // the existence of fields is already checked in 'checkFields()' so we don't have
            // to set errors when non existent fields are set
            if (nm.hasField(field)) {
                if (!node.getStringValue(field).equals(fields.get(field))) {
                    setChanged();
                    log.debug(String.format("setting value '%s' on field '%s' of node of type '%s'", fields.get(field),
                            field, node.getNodeManager().getName()));
                    node.setStringValue(field, fields.get(field));
                } else {
                    log.debug(String.format(
                            "not setting value '%s' on field '%s' of node of type '%s': value is same as current",
                            fields.get(field), field, node.getNodeManager().getName()));

                }
            }
        }

        for (String field : dateFields.keySet()) {
            try {
                if (!nm.hasField(field)) {
                    addGlobalError("error.field.unknown",
                            new String[] { field, this.getClass().getName(), nm.getName() });
                }
                if (dateFields.get(field).getDateInSeconds() != node.getIntValue(field)) {
                    node.setDateValue(field, dateFields.get(field).getParsedDate());
                    setChanged();
                }
            } catch (ParseException e) {
                addFieldErrorTypeMessage(field, e.getMessage());
            }
        }

        // set the file field
        if (getFile() != null && nm.getField("handle") == null) {
            addGlobalError("error.field.unknown", new String[] { "handle", this.getClass().getName(), nm.getName() });
        } else {
            setHandlerField();
        }

    }

    /**
     * This method sets the handle field of a given node if there is a file upload inside the current action. It also
     * tries to set a number of other fields on the node (if they exist):<br>
     * <ul>
     * <li>filename : the original file name (not the path)</li>
     * <li>filesize</li>
     * <li>size</li>
     * <li>mimetype</li>
     * </ul>
     * Current limitations are:
     * <ul>
     * <li>you can only set one file for an action
     * <li>the field name (handle) is hardcoded.
     * <ul>
     * 
     */
    private final void setHandlerField() {
        boolean changed = false;
        try {
            MultipartFile file = getFile();
            if (getFile() != null && !getFile().isEmpty()) {
                NodeManager nodeManager = node.getNodeManager();

                node.setByteValue("handle", file.getBytes());
                changed = true;

                // see if we can set a derived filename
                if (nodeManager.hasField("filename")) {

                    String fileName = file.getOriginalFilename();
                    int pos1 = fileName.lastIndexOf("/");
                    int pos2 = fileName.lastIndexOf("\\");
                    int pos = pos1 > pos2 ? pos1 : pos2;
                    if (pos > 0) {
                        fileName = fileName.substring(pos + 1);
                        if ("".equals(fileName)) {
                            fileName = file.getOriginalFilename();
                        }
                    }
                    node.setStringValue("filename", fileName);
                }

                // see if we can set the mimetype
                if (nodeManager.hasField("mimetype")) {
                    String mimetype = file.getContentType();
                    node.setStringValue("mimetype", mimetype);
                }

                // the file size
                long filesize = file.getSize();
                if (nodeManager.hasField("filesize")) {
                    node.setLongValue("filesize", filesize);
                }
                if (nodeManager.hasField("size")) {
                    node.setLongValue("size", filesize);
                }

            }
        } catch (IOException e) {
            addFieldErrorTypeMessage("file", "" + e);
        }
        if (changed) {
            setChanged();
        }
    }

    protected final void addNodeParam(String name){
        resultContainer.addParamToReturnURL(name, getNode());
    }
    
    protected final void addParam(String name, String value){
        resultContainer.addParamToReturnURL(name, value);
    }
}
