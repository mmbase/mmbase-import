/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Notfound;
import org.mmbase.bridge.jsp.taglib.editor.Editor;
import org.mmbase.bridge.jsp.taglib.editor.EditTag;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The FieldTag can be used as a child of a 'NodeProvider' tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class FieldTag extends FieldReferrerTag implements FieldProvider, Writer {

    private static final Logger log = Logging.getLoggerInstance(FieldTag.class);

    protected Field  field;
    protected String fieldName;
    protected Attribute name = Attribute.NULL;
    protected Attribute notfound = Attribute.NULL;


    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    public void setNotfound(String i) throws JspTagException {
        notfound = getAttribute(i);
    }


    // NodeProvider Implementation
    /**
     * A fieldprovider also provides a node.
     */

    public Node getNodeVar() throws JspTagException {
        return getNode();
    }


    public Field getFieldVar() {
        return field;
    }

    protected void setFieldVar(String n) throws JspTagException {
        if (n != null) {
            try {
                Node nd = getNode();
                if (nd == null) {
                    throw new JspTagException("No node found (" + findNodeProvider() + ")");
                }
                NodeManager nm = nd.getNodeManager();
                if (nm == null) {
                    throw new RuntimeException("NodeManager for " + n + " is null!");
                }
                field = nm.getField(n);
		/*
                if (!nm.getName().equals("virtual_manager")) {
		} else {
			field = null;
		}
		*/
            } catch (NotFoundException e) {
                log.warn(e);
                field = null;
            }
            fieldName = n;
            if (getReferid() != null) {
                throw new JspTagException ("Could not indicate both 'referid' and 'name' attribute");
            }
        } else {
            if (getReferid() == null) {
                field = getField(); // get from parent.
                getNode();       // be sure to set the nodevar too, though.
                fieldName = field.getName();
            }
        }
    }
    protected void setFieldVar() throws JspTagException {
        setFieldVar((String) name.getValue(this));
    }

    /**
     * Does something with the generated output. This default
     * implementation does nothing, but extending classes could
     * override this function.
     *
     **/
    protected String convert (String s) throws JspTagException { // virtual
        return s;
    }


    /**
     * Method to handle the EditTag if it is present around fields and their nodes.
     * <br /><br />
     * When the FieldTag finds itself inside an EditTag then it will register its
     * contents with the EditTag. The EditTag can provide access to an editor.
     * Not only the field and its nodes will be registered but also the query it
     * originated from. It passes these to the method
     * EditTag#registerField(Query query, int nodenr, String fieldName).
     * @see org.mmbase.bridge.jsp.taglib.editor.EditTag
     *
     * @since MMBase-1.8
     * @todo  EXPERIMENTAL
     */
    protected void handleEditTag() {
    	// See if this FieldTag has a parent EditTag
        Editor editor = (Editor) pageContext.getAttribute(EditTag.KEY, EditTag.SCOPE);
        if (editor == null) {
            if (log.isDebugEnabled()) log.debug("No EditTag as parent. We don't want to edit, i presume.");
        } else {
            Query query = null;
            try {
                query = findNodeProvider().getGeneratingQuery();
            } catch (JspTagException jte) {
                log.error("JspTagException, no GeneratingQuery found : " + jte);
            }

            Node node = null;
            try {
                node = getNodeVar();
            } catch (JspTagException jte) {
                if (log.isDebugEnabled()) log.debug("Node not found in getNodeVar() " + jte);
            }

            if (fieldName == null) {
                if (log.isDebugEnabled()) log.debug("fieldName still null. Image tag? URL tag? Attachment?");
                if (this instanceof ImageTag) {
                    if (log.isDebugEnabled()) log.debug("Image! fieldName = handle");
                    fieldName = "handle";
                }
            }
            if (fieldName.indexOf(".") < 0) {	// No nodemanager? add one
                fieldName = node.getNodeManager().getName() + "." + fieldName;
            }


            int nodenr = node.getIntValue("number");		// nodenr of this field to pass to EditTag
            if (nodenr < 0) {
                java.util.List steps = query.getSteps();
                Step nodeStep = null;
                if (query instanceof NodeQuery) {
                    nodeStep = ((NodeQuery) query).getNodeStep();
            	}
                for (int j = 0; j < steps.size(); j++) {
                    Step step = (Step)steps.get(j);
                    if (step.equals(nodeStep)) {
                        nodenr = node.getIntValue("number");
                    } else {
                        String pref = step.getAlias();
                        if (pref == null) pref = step.getTableName();

                        // check with correct nodemanager
                        String nm = fieldName.substring(0, fieldName.indexOf("."));
                        if (pref.equals(nm)) {
                            nodenr = node.getIntValue(pref + ".number");
                        }


                    }
                }
            }

            // register stuff with EditTag
            if (log.isDebugEnabled()) log.debug("Registering fieldName '" + fieldName + "' with nodenr '" + nodenr + "' and query: " + query);
            editor.registerField(query, nodenr, fieldName);
        }
    }

    public int doStartTag() throws JspException {
        initTag();
        Node node = getNode();
        fieldName = (String) name.getValue(this);
        boolean findValue = true;
        boolean hasField = node != null && fieldName != null && node.getNodeManager().hasField(fieldName);
        if (! hasField) {
            switch(Notfound.get(notfound, this)) {
            case Notfound.LOG:
                findValue = false;
                log.warn("Field '" + fieldName + "' does not exist in " + getNode().getNodeManager().getName());
                break;
            case Notfound.SKIP:
                return SKIP_BODY;
            case Notfound.PROVIDENULL:
                findValue = false;
                break;
            case Notfound.MESSAGE:
                findValue = false;
                try {
                    getPageContext().getOut().write("Field '" + fieldName + "' does not exist in " + getNode().getNodeManager().getName());
                } catch (java.io.IOException ioe) {
                    log.warn(ioe);
                }
                break;
            case Notfound.THROW:
            default:
                if ("log".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.field.nonExistance"))) {
                    log.error("Tried to use non-existing field '" + fieldName + "' of node '"
                                + node.getNumber() + "' from "
                                + getNode().getNodeManager().getName() + "\n"
                                + Logging.stackTrace(5));
                    findValue = false;
                }

                // will cause exception
            }
        }
        Object value = null;
        if (findValue) {
            if (hasField || fieldName == null) setFieldVar(fieldName); // set field and node
            if (log.isDebugEnabled()) {
                log.debug("Field.doStartTag(); '"  + fieldName + "'");
            }

            // found the node now. Now we can decide what must be shown:
            // now also 'node' is availabe;
            if (field == null) { // some function, or 'referid' was used.
                if (getReferid() != null) { // referid
                    value = getObject(getReferid());
                } else {         // function
                    value = node.getValue(fieldName);
                }
            } else {        // a field was found!
                // if direct parent is a Formatter Tag, then communicate
                FormatterTag f = findParentTag(FormatterTag.class, null, false);
                if (f != null && f.wantXML()) {
                    if (log.isDebugEnabled()) log.debug("field " + field.getName() + " is in a formatter tag, creating objects Element. ");
                    f.getGenerator().add(node, field); // add the field
                    f.setCloud(node.getCloud());
                    value = "";
                } else { // do the rest as well.

                    // if a value is really null, should it be past as null or cast?
                    // I am leaning to the latter but it would break backward compatibility.
                    // currently implemented this behavior for DateTime values (new fieldtype)
                    // Maybe better is an attribute on fieldtag that determines this?
                    // I.e. ifempty = "skip|asis|default"
                    // where:
                    //   skip: skips the field tag
                    //   asis: returns null as a value
                    //   default: returns a default value

                    switch(helper.getVartype()) {
                    case WriterHelper.TYPE_NODE:
                        value = node.getNodeValue(fieldName);
                        break;
                    case WriterHelper.TYPE_FIELDVALUE:
                        value = node.getFieldValue(fieldName);
                        break;
                    case WriterHelper.TYPE_FIELD:
                        value = node.getFieldValue(fieldName).getField();
                        break;
                    default:
                        switch(field.getType()) {
                        case Field.TYPE_BINARY:
                            value = node.getByteValue(fieldName);
                            break;
                        case Field.TYPE_INTEGER:
                        case Field.TYPE_NODE:
                            value = node.getIntValue(fieldName);
                            break;
                        case Field.TYPE_DOUBLE:
                            value = node.getDoubleValue(fieldName);
                            break;
                        case Field.TYPE_LONG:
                            value = node.getLongValue(fieldName);
                            break;
                        case Field.TYPE_FLOAT:
                            value = node.getFloatValue(fieldName);
                            break;
                        case Field.TYPE_DATETIME:
                            value = node.getValue(fieldName);
                            if (value != null) {
                                value = node.getDateValue(fieldName);
                            }
                            break;
                        case Field.TYPE_BOOLEAN:
                            value = Boolean.valueOf(node.getBooleanValue(fieldName));
                            break;
                        case Field.TYPE_LIST:
                            value = node.getListValue(fieldName);
                            break;
                        default:
                            value = convert(node.getStringValue(fieldName));
                        }
                    }
                }
            }
            if (log.isDebugEnabled()) log.debug("value of " + fieldName + ": " + value);
        }
        handleEditTag();

        helper.setValue(value);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        log.debug("end of doStartTag");
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     * write the value of the field.
     **/
    public int doEndTag() throws JspTagException {
        log.debug("doEndTag of FieldTag");
        if ((! "".equals(helper.getString()) && getReferid() != null)) {
            throw new JspTagException("Cannot use body in reused field (only the value of the field was stored, because a real 'field' object does not exist in MMBase)");
        }
        helper.doEndTag();
        return super.doEndTag();
    }

    public void doFinally() {
        field = null;
        fieldName = null;
        helper.doFinally();
        super.doFinally();
    }
}
