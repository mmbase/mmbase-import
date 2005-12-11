/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the
 * Open Source Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 * 
 */
package org.mmbase.applications.editwizard.schema;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.util.XmlUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.URIResolver;
import org.w3c.dom.*;

public class WizardSchemaLoader {

    //this pattern is used for resolveXpath(FieldElm) to match fdatapath="field[@name='fieldname']"
    private static Pattern PATTERN_XPATH_FIELDNAME = Pattern.compile("(object/)?field\\[\\@name=['\"](\\w+)['\"]\\]");
    //this pattern is used for resolveXpath(ListElm) to match fdatapath="relation[object/@type='destination']"
    private static Pattern PATTERN_XPATH_RELATION = Pattern.compile("relation\\[(object/)?@(\\w+)=['\"](\\w+)['\"]\\]");
    
    private static final Logger log = Logging.getLoggerInstance(WizardSchemaLoader.class);

    private static WizardSchemaCache wizardSchemaCache = new WizardSchemaCache();

    private URIResolver uriResolver = null;
    
    private WizardSchemaLoader() {
        // use getInstance
    }

    public static WizardSchemaLoader getInstance(URIResolver resolver) {
        WizardSchemaLoader wizardSchemaLoader = new WizardSchemaLoader();
        wizardSchemaLoader.uriResolver = resolver;
        return wizardSchemaLoader;
    }

    public WizardSchema getSchema(String wizardName) throws WizardException {

        URL wizardSchemaFile;
        try {
            wizardSchemaFile = uriResolver.resolveToURL(wizardName + ".xml", null);
        }
        catch (Exception e) {
            throw new WizardException(e);
        }
        if (wizardSchemaFile == null) {
            throw new WizardException("Could not resolve wizard " + wizardName + ".xml  with "
                    + uriResolver);
        }

        WizardSchema wizardSchema = wizardSchemaCache.getWizardSchema(wizardSchemaFile);

        if (wizardSchema == null) {
            wizardSchema = loadSchema(wizardSchemaFile);
            log.debug("Schema loaded (and resolved): " + wizardSchemaFile);
        }
        else {
            log.debug("Schema found in cache: " + wizardSchemaFile);
        }

        // tag schema nodes
        tagSchemaNodes(wizardSchema);
        log.debug("Tag schema nodes : " + wizardSchemaFile);

        return wizardSchema;
    }

    /**
     * tag schema's elements with identify number
     * @throws WizardException 
     */
    public void tagSchemaNodes(WizardSchema wizardSchema) throws WizardException {
        // tag schema nodes
        int count = 0;
        count = tagNodes(wizardSchema.getElementFidIndex(),wizardSchema.getActions().values(),count);
        count = tagNodes(wizardSchema.getElementFidIndex(),wizardSchema.getFormSchemas().values(),count);
        count = tagNodes(wizardSchema.getElementFidIndex(),wizardSchema.getOptionLists().values(),count);
    }
    
    /**
     * use f_x tag fid of field/list/item 
     * @param obj the node need to be tagged
     * @param number serial number
     * @return updated serial number
     * @throws WizardException 
     */
    private int tagNode(Map nodeFidIndex, Object obj, int number) throws WizardException {
        if (obj == null) {
            return number;
        }
        if (obj instanceof FieldElm) {
            FieldElm element = (FieldElm)obj;
            //mark field
            String tagvalue = "f_"+number++; 
            element.setFid(tagvalue);
            nodeFidIndex.put(tagvalue,element);
            resolveXpath(element);
            
        } else if (obj instanceof ItemElm) {
            ItemElm element = (ItemElm)obj;
            //mark item
            String tagvalue = "f_"+number++; 
            element.setFid(tagvalue);
            nodeFidIndex.put(tagvalue,element);

            //reversely tag into fieldset/field
            number = tagNodes(nodeFidIndex,element.fields,number);
            number = tagNodes(nodeFidIndex,element.lists,number);
            number = tagNodes(nodeFidIndex,element.fieldSets,number);
            
        } else if (obj instanceof ListElm) {
            ListElm element = (ListElm)obj;
            //mark list
            String tagvalue = "f_"+number++; 
            element.setFid(tagvalue);
            nodeFidIndex.put(tagvalue,element);

            //reversely tag into list/item
            number = tagNode(nodeFidIndex,element.item,number);
            resolveXpath(element);
            
        } else if (obj instanceof ActionElm) {
            ActionElm element = (ActionElm)obj;
            //reversely tag into action/field & action/relation
            number = tagNodes(nodeFidIndex,element.fields,number);
            number = tagNodes(nodeFidIndex,element.relations,number);
            
        } else if (obj instanceof FieldSetElm) {
            FieldSetElm element = (FieldSetElm)obj;
            //reversely tag into fieldset/field
            number = tagNodes(nodeFidIndex,element.fields,number);
            
        } else if (obj instanceof FormSchemaElm) {
            FormSchemaElm element = (FormSchemaElm)obj;
            //reversely tag into form-schema/field form-schema/list form-schema/fieldset
            number = tagNodes(nodeFidIndex,element.fields,number);
            number = tagNodes(nodeFidIndex,element.lists,number);
            number = tagNodes(nodeFidIndex,element.fieldSets,number);

        } else if (obj instanceof ObjectElm) {
            ObjectElm element = (ObjectElm)obj;
            //reversely tag into object/field object/relation
            number = tagNodes(nodeFidIndex,element.fields,number);
            number = tagNodes(nodeFidIndex,element.relations,number);
            
        } else if (obj instanceof RelationElm) {
            RelationElm element = (RelationElm)obj;
            //reversely tag into object/field object/relation
            number = tagNodes(nodeFidIndex,element.fields,number);
            number = tagNode(nodeFidIndex,element.object,number);
            
        } else if (obj instanceof OptionListElm) {
            OptionListElm element = (OptionListElm)obj;
            //process optionlist
            this.resolveXpath(element);
            
        } else {
            log.warn("This type object ("+obj.getClass()+") should not be tagged");
        }
        return number;
    }

    /**
     * use f_x tag fid attributes of nodes
     * @param nodeList the nodes need to be tagged
     * @param startNumber serial number
     * @return updated serial number
     * @throws WizardException 
     */
    private int tagNodes(Map nodeFidIndex, Collection nodeList,int number) throws WizardException {
        if (nodeList==null) {
            return number;
        }
        /*
         * the following element could be found with these xpath
         * field    [ action/field  form-schema/field  fieldset/field
         *            object/field  relation/field  item/field ]
         * list     [ form-schema/list  item/list ]
         * item     [ list/item ]
         * object   [ relation/object ]
         * relation [ action/relation object/relation ]
         * fieldset [ form-schema/fieldset item/fieldset]
         */
        for (Iterator iterator=nodeList.iterator();iterator.hasNext();){
        //for (int i=0;i<nodeList.size();i++) {
            Object obj = iterator.next();//nodeList.get(i);
            number  = tagNode(nodeFidIndex,obj,number);
        }
        return number;
    }

    /**
     * This method loads the schema using the properties of the wizard. It loads the wizard using
     * #wizardSchemaFilename, resolves the includes, and 'tags' all datanodes. (Places temp. ids in
     * the schema).
     * 
     */
    private WizardSchema loadSchema(URL wizardSchemaFile) throws WizardException {
        Document schemaDocument = XmlUtil.loadXMLFile(wizardSchemaFile);
        log.debug("Schema loaded : " + wizardSchemaFile);

        resolveIncludes(schemaDocument.getDocumentElement());
        log.debug("Resolved files included in Schema : " + wizardSchemaFile);

        resolveShortcuts(schemaDocument.getDocumentElement(), true);
        log.debug("Resolved shortcuts included in Schema : " + wizardSchemaFile);

        WizardSchema wizardSchema = SchemaUtils.getWizardSchema(schemaDocument);
        return wizardSchema;
    }

    /**
     * This method resolves the includes (and extends) in a wizard. It searches for include=""
     * attributes, and searches for extends="" attributes. Include means: the file is loaded (uses
     * the path and assumes it references from the basepath param, and the referenced node is placed
     * 'over' the existing node. Attributes are copied also. Any content in the original node is
     * removed. Extends means: same as include, but now, the original content is not thrown away,
     * and the nodes are placed after the included node. <br />
     * Note: this does not work with teh wizard-schema, and only ADDS objects when overriding (it
     * does not replace them)
     * 
     * This method is a recursive one. Included files are also scanned again for includes.
     * 
     * @param node
     *            The node from where to start searching for include and extends attributes.
     * @returns A list of included files.
     * 
     */
    private List resolveIncludes(Node node) throws WizardException {
        List result = new ArrayList();

        // Resolve references to elements in other wizards. This can be by
        // inclusion
        // or extension.
        NodeList externalReferences = XmlUtil.selectNodeList(node, "//*[@include or @extends]");
        Document targetdoc = node.getOwnerDocument();

        if (externalReferences != null) {
            for (int i = 0; i < externalReferences.getLength(); i++) {
                Node referer = externalReferences.item(i);
                boolean inherits = !XmlUtil.getAttribute(referer, "extends", "").equals("");
                String includeUrl = XmlUtil.getAttribute(referer, "include");

                if (inherits) {
                    includeUrl = XmlUtil.getAttribute(referer, "extends");
                }

                try {
                    // Resolve the filename and form-schema id.
                    String url = includeUrl;
                    String externalId = "not applicable";
                    int hash = includeUrl.indexOf('#');

                    if (hash != -1) {
                        url = includeUrl.substring(0, includeUrl.indexOf('#'));
                        externalId = includeUrl.substring(includeUrl.indexOf('#') + 1);
                    }

                    URL file;
                    try {
                        file = this.uriResolver.resolveToURL(url, null);
                    }
                    catch (Exception e) {
                        throw new WizardException(e);
                    }
                    result.add(file);

                    // Load the external file.
                    Document externalDocument = XmlUtil.loadXMLFile(file);

                    if (externalDocument == null) {
                        throw new WizardException(
                                "Could not load and parse included file. Filename:" + file);
                    }

                    // Add a copy of the external part to our schema here, to
                    // replace the
                    // referer itself.
                    Node externalPart = null;

                    if (hash == -1) {
                        // Load the entire file.
                        externalPart = externalDocument.getDocumentElement();
                    }
                    else
                        if (externalId.startsWith("xpointer(")) {
                            // Load only part of the file, using an xpointer.
                            String xpath = externalId.substring(9, externalId.length() - 1);
                            externalPart = XmlUtil.selectSingleNode(externalDocument, xpath);
                        }
                        else {
                            // Load only the node with the given id.
                            externalPart = XmlUtil.selectSingleNode(externalDocument,
                                    "//node()[@id='" + externalId + "']");
                        }

                    // recurse!
                    result.addAll(resolveIncludes(externalPart));

                    // place loaded external part in parent...
                    Node parent = referer.getParentNode();
                    externalPart = parent.insertBefore(targetdoc.importNode(externalPart, true),
                            referer);

                    // If the old node had some attributes, copy them to the
                    // included one.
                    XmlUtil.copyAllAttributes(referer, externalPart);

                    //
                    if (inherits) {
                        NodeList overriders = XmlUtil.selectNodeList(referer, "node()");

                        for (int k = 0; k < overriders.getLength(); k++) {
                            // 'inherit' old nodes. Do not clone, as we are
                            // essentially 'moving' these
                            externalPart.appendChild(overriders.item(k));
                        }
                    }

                    // Remove the refering node.
                    parent.removeChild(referer);
                }
                catch (RuntimeException e) {
                    log.error(Logging.stackTrace(e));
                    throw new WizardException("Error resolving external part '" + includeUrl + "'");
                }
            }
        }

        return result;
    }

    /**
     * Resolves shortcuts placed in the schema. eg.: if a user just entered <field name="firstname" />
     * it will be replaced by <field fdatapath="field[@name='firstname']" />
     * 
     * later, other simplifying code could be placed here, so that for more simple fdatapath's more
     * simple commands can be used. (maybe we should avoid using xpath in total for normal use of
     * the editwizards?)
     * 
     * @param schemanode
     *            The schemanode from were to start searching
     * @param recurse
     *            Set to true if you want to let the process search in-depth through the entire
     *            tree, false if you just want it to search the first-level children
     */
    private void resolveShortcuts(Node schemaNode, boolean recurse) {
        String xpath;

        if (recurse) {
            xpath = ".//field|.//list";
        }
        else {
            xpath = "field|list";
        }

        NodeList children = XmlUtil.selectNodeList(schemaNode, xpath);

        if (children == null) {
            throw new RuntimeException("could not perform xpath:" + xpath + " for schemanode:\n"
                    + schemaNode);
        }

    }
    
    /**
     * convert <field fdatapath="field[@name='firstname']" /> into <field name="firstname"/>
     * @param fieldElm
     * @throws WizardException
     */
    private void resolveXpath(FieldElm fieldElm) throws WizardException{
        // transforms <field fdatapath="field[@name='firstname']" /> into <field name="firstname"/>
        // some thing should be convert <field fdatapath=".//object[@type='images']" --> <field name="handle"
        String ftype = fieldElm.getFtype();
        if ("startwizard".equals(ftype)==true) {
            return;
        }
        String fdatapath = fieldElm.getFdatapath();
        String fieldName = fieldElm.getName();
        if (fdatapath == null || "".equals(fdatapath)) {
            if (fieldName == null || "".equals(fieldName)) {
                throw new WizardException("name attribute of field element is required in schema file");
            } else {
                return;
            }
        }
        Matcher matcher = PATTERN_XPATH_FIELDNAME.matcher(fdatapath);
        if (matcher.find()) {
            String datafrom = matcher.group(1);
            if (fieldElm.getParent() instanceof ItemElm) {
                if (datafrom!=null) {
                    // get field of object
                    fieldElm.setAttribute(SchemaKeys.ATTR_DATAFROM,"object");
                } else {
                    // default is get field of relation
                    fieldElm.setAttribute(SchemaKeys.ATTR_DATAFROM,"relation");
                }
            }
            fieldName = matcher.group(2);   
            fieldElm.setAttribute(SchemaKeys.ATTR_NAME,fieldName);
            return;
        }
        if (".//object[@type='images'".equals(fdatapath)==false) {
            fieldElm.setAttribute(SchemaKeys.ATTR_NAME,"handle");
            return;
        }
        throw new WizardException("fdatapath attribute's value "+fdatapath+" specified in field element is not valid");
    }
    
    /**
     * convert <field fdatapath="field[@name='firstname']" /> into <field name="firstname"/>
     * @param listElm
     * @throws WizardException
     */
    private void resolveXpath(ListElm listElm) throws WizardException{
        // transforms <list fdatapath="relation[object/@type='audioparts']" /> into <list destination="xxx" role="xxx"/>
        // the following is the scenario this method support, all the scenario is collection from previous projects
        // 1) <list fdatapath="relation[object/@type='audioparts']" --> <list destination="audioparts" 
        String fdatapath = listElm.getFDataPath();
        if (fdatapath==null) {
            return;
        }
        Matcher matcher = PATTERN_XPATH_RELATION.matcher(fdatapath);
        if (matcher.find()) {
            String datafrom = matcher.group(1);
            String attrName = matcher.group(2);
            String attrValue = matcher.group(3);
            if (datafrom!=null && "type".equals(attrName) && listElm.getDestination()!=null) {
                //if defined object/@type='typename' set list@destination="typename"
                listElm.setAttribute(SchemaKeys.ATTR_DESTINATION,attrValue);
            }
            return;
        }
        
        throw new WizardException("fdatapath attribute's value "+fdatapath+" specified in field element is not valid");
    }
    
    /**
     * resolve optionlist's attributes such as optionid and optioncontent
     * convert xpath into fieldname
     * <optionlist name="role" 
     *   optionid="field[@name='role.role']" --> optionid="role.role"
     *   optioncontent="field[@name='role.name']" --> optioncontent="role.name"
     * @param optionListElm
     */
    private void resolveXpath(OptionListElm optionListElm) {
        String optionid = optionListElm.getOptionid();
        if (optionid!=null) {
            optionid = SchemaUtils.getFieldNameByXpath(optionid);
            optionListElm.setAttribute(SchemaKeys.ATTR_OPTIONID,optionid);
        }
        String optioncontent = optionListElm.getOptionContent();
        if (optioncontent!=null) {
            optioncontent = SchemaUtils.getFieldNameByXpath(optioncontent);
            optionListElm.setAttribute(SchemaKeys.ATTR_OPTIONCONTENT,optioncontent);
        }
    }
}
