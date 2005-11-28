/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.action;

import java.util.List;

import org.mmbase.applications.editwizard.schema.SchemaKeys;
import org.mmbase.applications.editwizard.schema.WizardSchema;
import org.mmbase.applications.editwizard.util.XmlUtil;
import org.w3c.dom.*;

/**
 * This class validates pre-html documents for the EditWizard.
 * It is mainly used to register invalid form and fields.
 *
 * @javadoc
 * @author Kars Veling
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: Validator.java,v 1.1 2005-11-28 10:09:27 nklasens Exp $
 */

public class Validator {

    /**
     * Validates the given prehtml-xml. Returns true if everything is valid, false if errors are found.
     * Detailed information about the errors are placed as subnodes of the invalid fields.
     * @param Document prehtml the prehtml which should be validated
     * @param Document schema the wizardschema which is needed to validate at all
     * @return True is the form is valid, false if it is invalid
     */
    public static boolean validate(Document prehtml, WizardSchema schema) {
        int errorcount = 0;

        // iterate through forms and validate each one of them.
        NodeList forms = XmlUtil.selectNodeList(prehtml, "/*/form");
        for (int i=0; i<forms.getLength(); i++) {

            Node form = forms.item(i);
            errorcount+=validateForm(form);
        }

        // now, add an overview of all steps and validation-information
        createStepsOverview(prehtml, schema);

        // return true if no errors were found.
        return errorcount==0;
    }

    /**
     * This method can validate one form. It returns the number of invalid fields in the form.
     *
     * @param form The form to be validated.
     * @return The number of invalid fields found.
     */
    public static int validateForm(Node form) {
        int errorcount=0;
        NodeList fields = XmlUtil.selectNodeList(form, "field[@ftype]");
        for (int i=0; i<fields.getLength(); i++) {
            if (!validateField(fields.item(i))) errorcount++;
        }
        return errorcount;
    }

    /**
     * This method validates one field.
     *
     * @param field the field which should be validated.
     * @return true is the field is valid, false if it is invalid.
     */
    public static boolean validateField(Node field) {
        String ftype = XmlUtil.getAttribute(field,"ftype","");
        String dttype = XmlUtil.getAttribute(field, "dttype","string");

        if (ftype.equals("data") || ftype.equals("startwizard")) return true; // ok.
        String value = XmlUtil.getText(XmlUtil.selectSingleNode(field,"value"),"");

        // clean up old validation info if exists
        Node val = XmlUtil.selectSingleNode(field, "validator");
        if (val!=null) {
            field.removeChild(val);
        }

        val = field.getOwnerDocument().createElement("validator");
        field.appendChild(val);
        XmlUtil.setAttribute(val,"valid", "true");
        int valueLength = value.length();
        boolean required = XmlUtil.getAttribute(field,"dtrequired", "false").equals("true");
        if (required && valueLength==0) {
            addValidationError(val, 1, "Value is required.");
        } else {
            if ("string".equals(dttype)) {
                int dtminlength = 0;
                try {
                    dtminlength = Integer.parseInt(XmlUtil.getAttribute(field,"dtminlength", "0"));
                } catch (Exception e) {
                    // don't mind that
                }
                if (valueLength<dtminlength) {
                    addValidationError(val, 1, "Entered text is too short.");
                } else {
                    int dtmaxlength = 640000;
                    try {
                        dtmaxlength = Integer.parseInt(XmlUtil.getAttribute(field,"dtmaxlength","640000"));
                    } catch (Exception e){
                        // don't mind that again
                    }
                    if (valueLength>dtmaxlength) {
                        addValidationError(val, 1, "Entered text is too long.");
                    }
                }
            }
            if ("int".equals(dttype) && valueLength!=0) {
                // int
                int nr=0;
                boolean isint=false;
                try {
                    nr = Integer.parseInt(value);
                    isint=true;
                } catch (Exception e) {
                    addValidationError(val, 1, "Entered value is not a valid integer number.");
                }
    
                if (isint) {
                    // check min and max bounds
                    int dtmin = Integer.parseInt(XmlUtil.getAttribute(field,"dtmin","0"));
                    int dtmax = Integer.parseInt(XmlUtil.getAttribute(field,"dtmax","999999999"));
                    if (nr<dtmin || nr>dtmax) {
                        addValidationError(val, 1, "Integer value is too small or too large.");
                    }
                }
            }
            if ("date".equals(dttype)) {
                // date
                // TODO
            }
            if ("enum".equals(dttype)) {
                // enum
                // TODO
            }
        }
        return XmlUtil.getAttribute(val,"valid").equals("true");
    }

    /**
     * This method adds a validation error to the form.
     *
     * @param       validationnode  this node was validated
     * @param       errortype       what kind of error was it?
     * @param       errormsg        what errormessage should be placed.
     */
    public static void addValidationError(Node validationnode, int errortype, String errormsg) {
        XmlUtil.setAttribute(validationnode, "valid", "false");
        Node msg = validationnode.getOwnerDocument().createElement("error");
        XmlUtil.setAttribute(msg,"type",errortype+"");
        XmlUtil.storeText(msg, errormsg);
    }

    /**
     * Constructs an overview-node in the pre-html where all steps are described.
     * Also, information about valid and non-valid form-schema's are placed in the steps-overview.
     *
     * The node looks like this:
     * <pre>
     * &lt;steps-validator valid="false" allowsave="false" allowcancel="true"&gt;
     *      &lt;step form-schema="schema-id" valid="true" /&gt;
     *      &lt;step form-schema="schema-id" valid="false" /&gt;
     * &lt;/steps-validator&gt;
     * </pre>
     * THis method creates a steps-overview which can be used by the html to show what forms are valid and what are not.
     *
     * @param prehtml the prehtml data node
     * @param schema the original wizardschema
     */
    public static void createStepsOverview(Document prehtml, WizardSchema schema) {

        // remove a "steps-validator" node if it exists.
        Node overview = XmlUtil.selectSingleNode(prehtml, "/*/steps-overview");
        if (overview!=null) prehtml.getDocumentElement().removeChild(overview);

        // create new overview node
        overview = prehtml.createElement(SchemaKeys.ELEM_STEPSVALIDATOR);
        XmlUtil.setAttribute(overview, SchemaKeys.ATTR_ALLOWCANCEL, "true");

        int invalidforms = 0;

        // iterate through all defined steps
        List steps = schema.getSteps();
        for (int i=0; i<steps.size(); i++) {
            String schemaid = (String)steps.get(i);
            if ("".equals(schemaid)==false) {
                // find the referred form and check if the form is valid.
                Node form = XmlUtil.selectSingleNode(prehtml, "/*/form[@id='"+schemaid+"']");
                if (form!=null) {
                    Node validationerror = XmlUtil.selectSingleNode(form, ".//field/validator[@valid='false']");

                    boolean valid = (validationerror == null);
                        // copy step information to overview node and store validation results.
                    Node newstep = XmlUtil.createAndAppendNode(overview,SchemaKeys.ELEM_STEP,null);
                    XmlUtil.setAttribute(newstep,SchemaKeys.ATTR_FORMSCHEMA,schemaid);
                    overview.appendChild(newstep);
                    XmlUtil.setAttribute(newstep, SchemaKeys.ATTR_VALID, valid+"");
                    if (!valid) invalidforms++;
                }
            }
        }

        // store global information about validationresults
        XmlUtil.setAttribute(overview, SchemaKeys.ATTR_ALLOWSAVE, (invalidforms==0) + "");
        XmlUtil.setAttribute(overview,SchemaKeys.ATTR_VALID, (invalidforms==0) + "");

        // store new overview node in the prehtml.
        prehtml.getDocumentElement().appendChild(overview);
    }

}
