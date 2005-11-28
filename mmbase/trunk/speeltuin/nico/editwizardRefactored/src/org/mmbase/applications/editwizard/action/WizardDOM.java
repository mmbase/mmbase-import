/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.data.*;
import org.mmbase.applications.editwizard.schema.*;
import org.mmbase.applications.editwizard.session.WizardConfig;
import org.mmbase.applications.editwizard.util.XmlUtil;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class WizardDOM {
    
    static final Logger log = Logging.getLoggerInstance(WizardDOM.class);
    
    private WizardConfig config = null;
    
    private String wizardName = null;
    
    private WizardSchema schema = null;
    
    private ObjectData wizardData = null;
    
    private Map attributes = null;
    
    public static WizardDOM getInstance(WizardConfig wizardConfig) {
        WizardDOM wizarddom = new WizardDOM();
        wizarddom.config = wizardConfig;
        wizarddom.wizardName = wizardConfig.getWizardName();
        wizarddom.wizardData = wizardConfig.wizardData;
        wizarddom.attributes = wizardConfig.getAttributes();
        wizarddom.schema = wizardConfig.getWizardSchema();
        return wizarddom;
    }
    
    public Document getDocument() throws WizardException{
        //create document body;
        Document wizardDoc = XmlUtil.parseXML("<wizard instance=\"" + wizardName + "\" />");
        Node wizardNode = wizardDoc.getDocumentElement();
        
        // copy all global wizard nodes.
        SchemaUtils.copyToNode(wizardNode,schema.titles,SchemaKeys.ELEM_TITLE);
        SchemaUtils.copyToNode(wizardNode,schema.descriptions,SchemaKeys.ELEM_DESCRIPTION);
        //TODO: remove subtitle support here because it only allowed under form-schema according to dtd
        
        //add <curform/><prevform/><nextform/> tags
        createCurform(wizardNode);

        Map formSchemaMap = schema.getFormSchemas();
        if (formSchemaMap.size()==0) {
            throw new WizardException(
            "No form-schema was found in the xml. Make sure at least one form-schema node is present.");
        }
        
        // add <form> elements
        List steplist = schema.getSteps();
        for (int i=0;i<steplist.size();i++) {
            String formid = (String)steplist.get(i);
            FormSchemaElm formSchemaElm = schema.getFormSchema(formid);
            if (formid.equals(config.getCurrentFormId())==true) {
                // create current form
                createForm(wizardNode, formSchemaElm, wizardData);
            } else {
                // create other form, only create form and title.
                createOtherForm(wizardNode, formSchemaElm);
            }
        }
        

        // now, resolve optionlist values:
        // - The schema contains the list definitions, from which the values are
        // copied.
        // - Each list may have a query attached, which is performed before the
        // copying.
        NodeList optionListNodes = XmlUtil.selectNodeList(wizardNode, ".//optionlist[@select]");

        for (int i = 0; i < optionListNodes.getLength(); i++) {
            Node optionListNode = optionListNodes.item(i);

            String listname = XmlUtil.getAttribute(optionListNode, "select");
            log.debug("Handling optionlist: " + i + ": " + listname);

            OptionListElm optionListElm = schema.getOptionListByName(listname);
            
            createOptionList(optionListNode,optionListElm,null);

        }

        return wizardDoc;
    }
    
    /**
     * create form which is not current selected form.
     * @param parentNode
     * @param formSchemaElm
     */
    private void createOtherForm(Node parentNode, FormSchemaElm formSchemaElm) {
        // Make prehtml form.
        //create element <form>
        Node formNode = XmlUtil.createAndAppendNode(parentNode,SchemaKeys.ELEM_FORM,null);
        //copy all attributes of the form-schema
        XmlUtil.setAttribute(formNode,SchemaKeys.ATTR_ID,formSchemaElm.getId());
        
        // Add the title, description.
        //TODO: original version handle "title|subtitle|description" 
        //      but actually description is not allowed under form-schema according to schema dtd
        SchemaUtils.copyToNode(formNode,formSchemaElm.title,SchemaKeys.ELEM_TITLE);
        SchemaUtils.copyToNode(formNode,formSchemaElm.subTitle,SchemaKeys.ELEM_SUBTITLE);

    }

    /**
     * create &lt;curform&gt; &lt;preform&gt; &lt;nextform&gt; elements in &lt;wizard&gt; DOM.
     * @param node the node to which these elements will be added.
     * @throws WizardException
     */
    private void createCurform(Node node) throws WizardException {
        List stepList = config.getWizardSchema().getSteps();
        int index= stepList.indexOf(config.getCurrentFormId());
        if (index<0) {
            throw new WizardException("No form-schema marked as "+config.getCurrentFormId()
                    +" was defined in wizard schema file.");
        } 
        //<curform>
        XmlUtil.createAndAppendNode(node,SchemaKeys.ELEM_CURFORM,config.getCurrentFormId());
        String prevFormId = index>0? (String)stepList.get(index-1):"";
        String nextFormId = index+1<stepList.size()? (String)stepList.get(index+1):"";
        //<prevform>
        XmlUtil.createAndAppendNode(node,SchemaKeys.ELEM_PREVFORM,prevFormId);
        //<nextform>
        XmlUtil.createAndAppendNode(node,SchemaKeys.ELEM_NEXTFORM,nextFormId);
    }
    
    /**
     * create &lt;form&gt; element in &lt;wizard&gt; dom.
     * @param parentNode node to be operated in <wizard> DOM object
     * @param formSchemaElm form-schema element
     * @param dataObject object data element
     * @throws WizardException 
     */
    private void createForm(Node parentNode, FormSchemaElm formSchemaElm, ObjectData dataObject) throws WizardException {
        // Make prehtml form.
        //create element <form>
        Node formNode = XmlUtil.createAndAppendNode(parentNode,SchemaKeys.ELEM_FORM,null);
        //copy all attributes of the form-schema
        XmlUtil.setAttribute(formNode,SchemaKeys.ATTR_ID,formSchemaElm.getId());
        
        // Add the title, description.
        //TODO: "title|subtitle|description" be handled in original version
        //       but actually description is not allowed under form-schema according to schema dtd
        SchemaUtils.copyToNode(formNode,formSchemaElm.title,SchemaKeys.ELEM_TITLE);
        SchemaUtils.copyToNode(formNode,formSchemaElm.subTitle,SchemaKeys.ELEM_SUBTITLE);

        List nodeList = new ArrayList();
        nodeList.addAll(formSchemaElm.fields);
        nodeList.addAll(formSchemaElm.fieldSets);
        // check all fields and do the thingies
        createFields(formNode, nodeList, dataObject);
        
        for (int i=0;i<formSchemaElm.lists.size();i++) {
            ListElm listElm = (ListElm)formSchemaElm.lists.get(i);
            createList(formNode, listElm, dataObject);
        }
        
    }
    
    /**
     * create option list in wizard document
     * @param optionListNode node to be operated in &lt;wiard&gt; DOM.
     * @param optionListElm schema element for &lt;optionlist&gt; in &lt;wizard-schema&gt; xml file.
     * @param selectedValue value selected by the field
     * @throws WizardException 
     */
    private void createOptionList(Node fieldNode, OptionListElm optionListElm,
            String selectedValue) throws WizardException {
        if (optionListElm == null) {
            // Not found in definition. Put an error in the list and proceed
            // with the next list.
            log.debug("OptionList is null! Ignore and proceeding with next list.");
            return;
        }

        // create optionlist under field
        Node optionListNode = XmlUtil.createAndAppendNode(fieldNode,SchemaKeys.ELEM_OPTIONLIST,null);

        // Now copy the option items of the optionlist into field.
        List options = this.getOptions(optionListElm);
        if (options==null) {
            // this scenario will never be reached, because optionlist.options is final 
            // and inited as empty ArrayList
            log.warn("options is null, ignore this");
            return;
        }
        if (options.size()==0) {
            log.warn("Options not found in optionlist " + optionListElm.getName());
            return;
        }

        for (int i=0;i<options.size();i++) {
            OptionElm optionElm = (OptionElm)options.get(i);
            Node optionNode = XmlUtil.createAndAppendNode(optionListNode,SchemaKeys.ELEM_OPTION,optionElm.getTextValue());
            XmlUtil.setAttribute(optionNode,SchemaKeys.ATTR_ID,optionElm.getId());
            if (selectedValue!=null && selectedValue.equals(optionElm.getId())) {
                // set selected=true for option which is currently selected
                XmlUtil.setAttribute(optionNode, "selected", "true");
            }
        }
    }
    
    private List getOptions(OptionListElm optionListElm) throws WizardException {
        String select = optionListElm.getSelect();
        if (select!=null) {
            // if defined select attribute to point a globle OptionList, use the globe one
            OptionListElm globeOptionList = this.schema.getOptionListByName(select);
            
            if (globeOptionList==null) {
                // cannot find a globe OptionList according to given OptionList's name
                throw new WizardException("select optionlist[@name='"+select+"'] is not defined in wizard-schema");
            }
            return getOptions(globeOptionList);
        }
        // Test if this list has a query and get the time-out related values.
        QueryElm queryElm = optionListElm.query;
        //TODO: select could not work with query at the same time. so don't use this two at same time
        if (optionListElm.query!=null) {
            if (optionListElm.getOptionContent()==null) {
                //TODO: not defined optioncontent attribute
                throw new WizardException(
                        "not defined optoincontent attribute for optionlist "+optionListElm.getName());
            }
            long currentTime = new Date().getTime();
            //Utils.getAttribute(optionListElm, "query-timeout",String.valueOf(this.m_listQueryTimeOut))
            //TODO: according to Schema DTD, no query-timeout attribute allowed in query element.
            long queryTimeOut = queryElm.getQueryTimeout();
            //TODO: according to schema DTD, no last-executed attribute allowed in query element 
            long lastExecuted = queryElm.getLastExecutedTime();
    
            // Execute the query if it's there and only if it has timed out.
            if ((currentTime - lastExecuted) > queryTimeOut) {
                log.debug("Performing query for optionlist '" + optionListElm.getName() + "'. Cur time "
                        + currentTime + " last executed " + lastExecuted + " timeout "
                        + queryTimeOut + " > " + (currentTime - lastExecuted));
    
                // select all child tags, should be 'query'
                String xpath = queryElm.getXpath(); // get xpath (nodetype);
                if ("".equals(xpath)) {
                    xpath = null;
                }
                if (xpath==null && queryElm.object!=null) {
                    xpath = queryElm.object.getType();
                }
                if (xpath == null) {
                    throw new WizardException("xpath or object/@type is required for query");
                }
                if (xpath.indexOf("/*@")!=0) {
                    throw new WizardException("invalid xpath, allowed xpath should start with '/*@'");
                }
                
                String where = queryElm.getWhere(); // get constraints;
                if (where!=null) {
                    //fill variable's value
                    where = XmlUtil.fillInParams(where, attributes);
                }
                
                String orderby = queryElm.getOrderBy(); // get orderby;
                if ("".equals(orderby)) {
                    orderby = null;
                }
                
                String directions = queryElm.getDirections(); // get directions;
                if ("".equals(directions)) {
                    directions = null;
                }
                
                
                // get object e template
                ObjectElm objectElm = queryElm.object;
                // get a list of all the fields (as subnodes) to query.
                String fields = "";
                if (objectElm!=null) {
                    for (int i=0;i<objectElm.fields.size();i++) {
                        FieldElm fieldElm = (FieldElm)objectElm.fields.get(i);
                        if (fields.length()>0){
                            fields += ",";
                        }
                        fields += fieldElm.getName();
                    }
                }
    
                String nodepath = xpath.substring(3);
                try {
                    Cloud cloud = this.config.getCloud();
                    NodeIterator nodeIterator = null;
    
                    if (nodepath.indexOf("/") == -1) {
                        // If there is no '/' seperator, we only get fields from one nodemanager. This is the fastest
                        // way of getting those.
                        nodeIterator = cloud.getNodeManager(nodepath).getList(where,orderby,directions).nodeIterator();
                    } else {
                        // If there are '/' seperators, we need to do a multilevel search. Therefore we first need to
                        nodeIterator = cloud.getList("", nodepath, fields, where, orderby, directions, null, true).nodeIterator();
                    }
                    // Loop through the queryresult and add the included objects by creating
                    // an option element for each one. The id and content of the option
                    // element are taken from the object by performing the xpaths on the object,
                    // that are given by the list definition.
                    optionListElm.options.clear();
                    String optionIdPath = optionListElm.getOptionid();
                    if (optionIdPath == null || "".equals(optionIdPath)) {
                        optionIdPath = "@number";
                    }
                    
                    String optionContentPath = optionListElm.getOptionContent();
                    for(; nodeIterator.hasNext(); ) {
                        org.mmbase.bridge.Node n=nodeIterator.nextNode();
                        OptionElm optionElm = new OptionElm();
                        String optionId = null;
                        if ("@number".equalsIgnoreCase(optionIdPath)) {
                            optionId = ""+n.getNumber();
                        } else {
                            optionId = n.getStringValue(optionIdPath);
                        }
                        String optionContent = n.getStringValue(optionContentPath);
                        optionElm.setId(optionId);
                        optionElm.setTextValue(optionContent);
                        optionListElm.options.add(optionElm);
                    }
                } catch(RuntimeException e) {
                    log.warn(Logging.stackTrace(e));
                    throw new WizardException("node type " + xpath.substring(3) + " does not exist(" + e.toString() + ")");
               }
            }
        }
        return Collections.unmodifiableList(optionListElm.options);
    }

    /**
     * create form for &lt;wizard&gt; DOM. 
     * @param parentNode
     * @param elementList
     * @throws WizardException 
     */
    private void createFields(Node parentNode, List elementList, BaseData data) throws WizardException {
        
        for (int i=0;i<elementList.size();i++) {
            Object obj = elementList.get(i);
            
            if (obj instanceof FieldElm) {
                FieldElm element = (FieldElm)obj;
                //TODO: the merge constraints should be optimized
                //       we could define object's type in schema files and doing merge immediately after load 
                if (data instanceof ObjectData 
                        && "startwizard".equals(element.getFtype())==false 
                        && "wizard".equals(element.getFtype())==false
                        && "function".equals(element.getFtype())==false) {
                    SchemaUtils.mergeConstraints(element,data.getType(),config.getCloud());
                }
                createField(parentNode,element,data);
                
            } else if (obj  instanceof FieldSetElm) {
                FieldSetElm element = (FieldSetElm) obj;
                // place newfieldset in pre-html form
                Node fieldSetNode = XmlUtil.createAndAppendNode(parentNode, SchemaKeys.ELEM_FIELDSET,null);
                SchemaUtils.copyToNode(fieldSetNode,element.prompt,SchemaKeys.ELEM_PROMPT);
                
                // recursive into <fieldset>
                createFields(fieldSetNode,element.fields,data);
                
            }
        }
    }
    
    /**
     * create &lt;field&gt; node in &lt;wizard&gt; dom.
     * @param parentNode the node where the field element be added to.
     * @param fieldElm the field schema element defined by <wizard-schema> xml file.
     * @param dataContext mmbase cloud data presented as Node.
     * @throws WizardException
     */
    private void createField(Node parentNode, FieldElm fieldElm, BaseData data) throws WizardException {
        String ftype = fieldElm.getFtype();
        //parentNode : form, object
        ObjectData objectData = null;
        RelationData relationData = null;
        // A normal field.
        FieldData fieldData = null;
        String fieldName = fieldElm.getName();
        if (data instanceof RelationData) {
            relationData = (RelationData)data;
            objectData = relationData.getRelatedObject();
        } else {
            objectData = (ObjectData)data;
        }
        if (("startwizard".equals(ftype) || "wizard".equals(ftype)) && 
                objectData.getStatus()==BaseData.STATUS_NEW) {
            // if startwizard and object is new, ignore this command
            return;
        }
        String datafrom = fieldElm.getAttribute(SchemaKeys.ATTR_DATAFROM);
        if (relationData !=null && "object".equals(datafrom)==false) {
            // if relation and not specified datafrom="object", try to load field's value from relation
            fieldData = relationData.findFieldByName(fieldName);
        }
        if (fieldData==null) {
            // if not get relation's field's value, try to find in object
            // if fdatapath="object/field[@name='fieldname']", should use related object's field
            fieldData = objectData.findFieldByName(fieldName);
        }
        
        //create new field node
        Node fieldNode = XmlUtil.createAndAppendNode(parentNode, SchemaKeys.ELEM_FIELD,null);
        //copy all attributes from field element to field node
        SchemaUtils.copyAttributesToNode(fieldNode,fieldElm.getAttributes());
        //copy prompt and description into field node
        SchemaUtils.copyToNode(fieldNode,fieldElm.prompt,SchemaKeys.ELEM_PROMPT);
        SchemaUtils.copyToNode(fieldNode,fieldElm.description,SchemaKeys.ELEM_DESCRIPTION);

        // add prefix and postfix
        String prefix = fieldElm.getPrefix();
        if (prefix!=null && "".equals(prefix)) {
            XmlUtil.createAndAppendNode(fieldNode, SchemaKeys.ELEM_PREFIX,prefix);
        }
        String postfix = fieldElm.getPostfix();
        if (postfix!=null && "".equals(postfix)) {
            XmlUtil.createAndAppendNode(fieldNode, SchemaKeys.ELEM_PREFIX,postfix);
        }
        
        if (fieldData != null) {
            // create normal formfield.
           
            //copy attribute of data from mmbase into wizard DOM
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_DID,fieldData.getDid());
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_NAME,fieldData.getName());
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_NUMBER,fieldData.getMainObject().getNumber());
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_TYPE,fieldData.getType());
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_FID,fieldElm.getFid());
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_FIELDNAME,"field/"+fieldElm.getFid()+"/"+fieldData.getDid());
            
            if (fieldElm.optionList !=null ) {
                createOptionList(fieldNode,fieldElm.optionList,fieldData.getStringValue());
            }
            
            // binary type needs special processing
            // TODO: how to judge binary file.
            if ("binary".equals(fieldElm.getFtype())||"byte".equals(fieldData.getType())) {
                createBinaryData(fieldNode,fieldData);
            }

            // 
            XmlUtil.createAndAppendNode(fieldNode, SchemaKeys.ELEM_VALUE,fieldData.getStringValue());
            
            if (fieldData.getMainObject().isMayWrite() && 
                    "data".equals(fieldElm.getFtype())==false) {
                XmlUtil.setAttribute(fieldNode, SchemaKeys.ATTR_MAYWRITE, "true");
            }

        } else {
            if ("function".equals(ftype)) {
                // create field[@ftype="function"]
                log.debug("Not an data node, setting number attribute, because it cannot be found with fdatapath");

                // set number attribute in field, then you can use it in wizard.xsl
                XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_NUMBER,objectData.getNumber());
                XmlUtil.setAttribute(fieldNode, SchemaKeys.ATTR_MAYWRITE, objectData.isMayWrite()+"");
                //Utils.setAttribute(fieldNode,SchemaKeys.ATTR_FIELDNAME,"field/"+fieldElm.getFid()+"/"+fieldData.getDid());
                
                if (objectData.isMayWrite()==false) {
                    // check rights - if you can't edit, set ftype to data
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_FTYPE,"data");
                }
                // add value, the name of the field
                String value = fieldElm.getName();
                if (value!=null) {
                    XmlUtil.storeText(fieldNode,value);
                }
                
            }
            else if ("startwizard".equals(ftype) || "wizard".equals(ftype)) {
                log.debug("A startwizard!");
                //create field[@ftype="startwizard" or @ftype="wizard"]
                SchemaElement parentElm = fieldElm.getParent();
                if (parentElm instanceof ItemElm) {
                    ItemElm itemElm = (ItemElm)parentElm;
                    SchemaUtils.copyAttributesToNode(fieldNode,itemElm.getAttributes());
                    SchemaUtils.copyAttributesToNode(fieldNode,fieldElm.getAttributes());
                }
                XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_OBJECTNUMBER,objectData.getNumber());
                if (relationData!=null) {
                    // if the object is relation, add startwizard support link
                    // add origin object number (the object which the relation will belong)
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_ORIGIN,
                            relationData.getMainObject().getNumber());
                    // add relation's main object's did
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_DID,
                            relationData.getMainObject().getDid());
                    // add relation's number
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_NUMBER,
                            relationData.getNumber());
                    // add relation's destination
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_DESTINATION,
                            relationData.getDestination());
                    // add relation's source
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_SOURCE,
                            relationData.getSource());
                    // add relation's role
                    XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_ROLE,
                            relationData.getRole());
                }
            }
            else {
                // throw an exception, but ONLY if the datapath
                // was created from a 'name' attribute
                // (only in that case can we be sure that the
                // path is faulty - in otehr cases
                // the path can be valid but point to a related
                // object that is not present)
                String fname = fieldElm.getName();
                
                if (fieldName.startsWith("@")) {
                    
                    String value = null;
                    if (relationData !=null && "object".equals(datafrom)==false) {
                        // if relation and not specified datafrom="object", try to load field's value from relation
                        value = relationData.getAttribute(fieldName.substring(1));
                    }
                    if (value==null) {
                        // if not get relation's field's value, try to find in object
                        // if fdatapath="object/field[@name='fieldname']", should use related object's field
                        value = objectData.getAttribute(fieldName.substring(1));
                    }
                    if (value!=null) {
                        XmlUtil.storeText(fieldNode,value);
                        return;
                    }
                }

                if (fname != null) {
                    throw new WizardException("Perhaps the field with name '" + fname
                            + "' does not exist?");
                }
            }
        }
    }
    
    /**
     * set binarydata
     * @param fieldNode
     * @param fieldData
     */
    private void createBinaryData(Node fieldNode, FieldData fieldData) {
        BinaryData binary = fieldData.getBinaryData();
        if (binary==null) {
            log.warn("binary data is not set to the field "+fieldData.getName());
            return;
        }
        String originalFilePath = binary.getOriginalFilePath();
        if(originalFilePath!=null) {
            Node uploadNode = XmlUtil.createAndAppendNode(fieldNode,SchemaKeys.ELEM_UPLOAD,null);
            // add path element under upload element
            XmlUtil.createAndAppendNode(uploadNode,SchemaKeys.ELEM_PATH,originalFilePath);
            // reset the size attribute's value of field element.
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_SIZE,"0");
            // set name, size, uploaded attributes' value of upload element 
            XmlUtil.setAttribute(uploadNode,SchemaKeys.ATTR_NAME,binary.getOriginalFileName());
            XmlUtil.setAttribute(uploadNode,SchemaKeys.ATTR_SIZE,""+binary.getLength());
            // TODO: what does the "uploaded" attribute mean?
            XmlUtil.setAttribute(uploadNode,SchemaKeys.ATTR_UPLOADED,"true");
        } else {
            //set the size attribute of field
            XmlUtil.setAttribute(fieldNode,SchemaKeys.ATTR_SIZE,""+binary.getLength());
        }
        
    }

    /**
     * create &lt;list&gt; element in &lt;wizard&gt; DOM.
     * 
     * @param parentNode
     *            the node in &lt;wizard&gt; DOM, wich will be operated
     * @param listElm
     *            the &lt;list&gt; element defined in schema xml file.
     * @param dataNode
     * @throws WizardException
     */
    private void createList(Node parentNode, ListElm listElm, 
            ObjectData objectData) throws WizardException {
        
        // copy all attributes from fielddefinition to new pre-html field definition
        log.debug("creating form list");

        // copy all attributes of the <list> element: 
        // maxoccurs minoccurs fdatapath fparentdatapath destination
        // destinationtype hidecommand orderby ordertype role searchdir
        // place newfield in pre-html form
        Node listNode = XmlUtil.addChildElement(parentNode,SchemaKeys.ELEM_LIST,listElm.getAttributes(),null);
        XmlUtil.setAttribute(listNode,SchemaKeys.ATTR_FID,listElm.getFid());
        
        // Add the title, description.  "title|description"
        SchemaUtils.copyToNode(listNode,listElm.title,SchemaKeys.ELEM_TITLE);
        SchemaUtils.copyToNode(listNode,listElm.description,SchemaKeys.ELEM_DESCRIPTION);

        // add other childNode  "|action|command" 
        SchemaUtils.copyToNode(listNode,listElm.actions,true);
        SchemaUtils.copyToNode(listNode,listElm.commands,true);
        
        // expand attribute 'startnodes' for search command
        Node command = XmlUtil.selectSingleNode(listNode, "command[@name='search']");

        if (command != null) {
            expandAttribute(command, "startnodes", null);
        }

        // expand attribute 'objectnumber' en 'origin' for editwizard command
        // command = Utils.selectSingleNode(listNode,
        // "command[@name='startwizard']");
        NodeList commands = XmlUtil.selectNodeList(listNode, "command[@name='startwizard']");
        if (commands != null) {
            for (int i = 0; i < commands.getLength(); i++) {
                command = commands.item(i);
                if (command != null) {
                    expandAttribute(command, "objectnumber", "new");
                    expandAttribute(command, "origin", this.wizardData.getNumber());
                    expandAttribute(command, "wizardname", null);
                }
            }
        }
        

        //TODO: there only support list with role and destination, fdatapath is not support here
        //      althouth we already handle simple fdatapath in list at schema loader.
        //      if we want to improve the way in which we search the list, we should add a new
        //      functionality allow user to defined how to filter the relations.
        List relationDataList = objectData.getRelationDataList(listElm.getRole(),listElm.getDestination());
        List tempstorage = null;
        // set the orderby attribute for all the nodes
        if (relationDataList!=null) {
            tempstorage = new ArrayList(relationDataList.size());
            tempstorage.addAll(relationDataList);
        }
        else {
            tempstorage = new ArrayList();
        }

        // sort list
        String orderby = listElm.getOrderBy();
        if ("".equals(orderby)) {
            orderby = null;
        }
        if (orderby==null && relationDataList.size()>0) {
            RelationData relation = (RelationData)relationDataList.get(0);
            if ("posrel".equals(relation.getType())) {
                orderby="pos";
            }
        }
        String ordertype = listElm.getOrderType();
        if (ordertype==null || ordertype.equals("")){
            ordertype = "string";
        }

        if (orderby != null) {
            Collections.sort(tempstorage, new OrderByComparator(orderby, ordertype));
        }

        // and make form
        int listsize = tempstorage.size();

        // calculate minoccurs and maxoccurs
        int minoccurs = 0;
        try {
            minoccurs = Integer.parseInt(listElm.getMinoccurs());
        } catch (Exception e1) {
            if (log.isDebugEnabled()) log.debug(Logging.stackTrace(e1));
        }
        
        int nrOfItems = relationDataList.size();

        int maxoccurs = -1;
        try {
            maxoccurs = Integer.parseInt(listElm.getMaxoccurs());
        } catch (Exception e) {
            if (log.isDebugEnabled()) log.debug(Logging.stackTrace(e));
        }

        String hiddenCommands = "|" + listElm.getHideCommand() + "|";

        boolean isposrel = false;
        int maxpos = 0;
        for (int dataindex = 0; dataindex < listsize; dataindex++) {
            RelationData relationData = (RelationData) tempstorage.get(dataindex);
            if ("posrel".equals(relationData.getType())) {
                isposrel = true;
                FieldData fieldData = relationData.findFieldByName("pos");
                if (fieldData!=null) {
                    int pos = fieldData.getIntValue();
                    if (pos>maxpos) {
                        maxpos = pos;
                    }
                } else {
                    log.warn("miss 'pos' field in 'posrel' relation node");
                }
            }

            // Select the form item
            if (listElm.item == null) {
                throw new WizardException(
                        "Could not find item in a list of " + wizardName); 
            }
            
            if (relationData.getStatus()==BaseData.STATUS_DELETE) {
                if (log.isDebugEnabled()) {
                    log.debug("the relation("+relationData.getNumber()+")'s status is delete, ignore this relation");
                }
                continue;
            }
            
            Node itemNode = createItem(listNode,listElm,relationData);

            // finally, see if we need to place some commands here
            String did = relationData.getDid();
            if (hiddenCommands.indexOf("|delete-item|") == -1) {
                addSingleCommand(itemNode, "delete-item", did);
            }

            boolean isfirst = dataindex == 0;
            XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_FIRSTITEM,isfirst+"");

            boolean islast = dataindex == (tempstorage.size() - 1);
            XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_LASTITEM, islast+"");
            
            if (orderby != null) {
                if (isfirst==false) {
                    String otherdid = ((RelationData) tempstorage.get(dataindex - 1)).getDid();
                    if ((dataindex > 0) && (hiddenCommands.indexOf("|move-up|") == -1)) {
                        addSingleCommand(itemNode, "move-up", did, otherdid);
                    }
                }
                if (islast==false) {
                    String otherdid = ((RelationData) tempstorage.get(dataindex + 1)).getDid();
                    if (((dataindex + 1) < listsize) && (hiddenCommands.indexOf("|move-down|") == -1)) {
                        addSingleCommand(itemNode, "move-down", did, otherdid);
                    }
                }
            }

        }

        // should the 'save' button be inactive because of this list?
        // works likes this:
        // If the minoccurs or maxoccurs condiditions are not satisfied, in the
        // 'wizard.xml'
        // to the form the 'invalidlist' attribute is filled with the name of
        // the guilty list. By wizard.xsl then this value
        // is copied to the html.
        //
        // validator.js/doValidateForm returns invalid as long as this invalid
        // list attribute of the html form is not an
        // emptry string.
        if (log.isDebugEnabled()) {
            log.debug("minoccurs:" + minoccurs + " maxoccurs: " + maxoccurs + " items: "
                    + nrOfItems);
        }

        if (((nrOfItems > maxoccurs) && (maxoccurs != -1)) 
                || (nrOfItems < minoccurs)) { 
            // form cannot be valid in that case
            
            ((Element) listNode).setAttribute("status", "invalid");

            // which list?
            
            String listTitle = listElm.title.getTextValue();
            if (listTitle==null || listTitle.length()==0) {
                listTitle = "some list";
            }
            XmlUtil.setAttribute(parentNode,SchemaKeys.ATTR_INVALIDLIST,listTitle);
        }
        else {
            ((Element) listNode).setAttribute("status", "valid");
        }

        log.debug("can we place an add-button?");
        
        ActionElm createActionElm = listElm.getActionByType("create");
        ActionElm addActionElm = listElm.getActionByType("add");

        // add command[@type="add-item"] 
        if ((hiddenCommands.indexOf("|add-item|") == -1)
                && ((maxoccurs == -1) || (maxoccurs > nrOfItems))
                && (createActionElm != null || addActionElm != null)) {
            String did = objectData.getDid();
            String otherid = null;
            if (isposrel) {
                otherid = ""+maxpos;
            }
            Node cmdNode = addSingleCommand(listNode, "add-item", did, otherid);
            if (addActionElm != null && addActionElm.prompt.hasValues()) {
                SchemaUtils.copyToNode(cmdNode,addActionElm.prompt,SchemaKeys.ELEM_PROMPT);
            } else if (createActionElm!=null && createActionElm.prompt.hasValues()) {
                SchemaUtils.copyToNode(cmdNode,createActionElm.prompt,SchemaKeys.ELEM_PROMPT);
            } 
            
        }

        log.debug("end");
        
    }
    
    /**
     * create item in wizard document
     * @param parentNode parent node in wizard document under which append item element
     * @param listElm list elements in wizard schema
     * @param relationData relation data
     * @return
     * @throws WizardException
     */
    private Node createItem(Node parentNode, ListElm listElm, RelationData relationData) 
        throws WizardException{
        
        //TODO: only one <item> allowed in <list>  
        ItemElm itemElm = listElm.item;
        
        Node itemNode = SchemaUtils.copyElementToNode(parentNode,itemElm,false);
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_FID,itemElm.getFid());
        
        String title = itemElm.title.getTextValue(); 
        String description = itemElm.description.getTextValue(); 
        
        // TODO: add "title|description" as attributes or add as child elements? 
        //       we add them as attributes, the same as original version
        if (title!=null && "".equals(title.trim())==false) {
            XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_ITEMTITLE,title);
        }
        if (description!=null && "".equals(description.trim())==false) {
            XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_ITEMDESCRIPTION,description);
        }
        // Add the title, description as child elements.
        //SchemaUtils.copyToNode(itemNode,itemElm.title,SchemaKeys.ELEM_TITLE);
        //SchemaUtils.copyToNode(itemNode,itemElm.description,SchemaKeys.ELEM_DESCRIPTION);

        //add attributes from schema
        SchemaUtils.copyAttributesToNode(itemNode,itemElm.getAttributes());

        // Copy all attributes from data to new pre-html field def (mainly
        // needed for the did).
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_DESTINATION,relationData.getDestination());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_SOURCE,relationData.getSource());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_NUMBER,relationData.getNumber());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_MAYWRITE,""+relationData.isMayWrite());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_MAYDELETE,""+relationData.isMayDelete());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_TYPE,relationData.getType());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_ROLE,relationData.getRole());
        XmlUtil.setAttribute(itemNode,SchemaKeys.ATTR_DID,relationData.getDid());
        
        // and now, do the recursive trick! All our fields inside need to be processed.
        List list = new ArrayList();
        list.addAll(itemElm.fields);
        list.addAll(itemElm.fieldSets);
        createFields(itemNode, list, relationData);
        
        for (int i=0;i<itemElm.lists.size();i++) {
            //there are lists under items, recursive into list
            ListElm subListElm = (ListElm)itemElm.lists.get(i);
            createList(itemNode,subListElm,relationData.getRelatedObject());
        }
        return itemNode;
         
    }
    
    /**
     * expand attribute value to node. the attribute value could be defined as template
     * which will be processed by template enginee. If no template available, defaultvalue 
     * will be used as attribute value.   
     * @param node the node in which attribute be append.
     * @param name the attribute name.
     * @param defaultvalue default value
     */
    private void expandAttribute(Node node, String name, String defaultvalue) {
        String template =  XmlUtil.getAttribute(node, name, null);
        String value = null;
        if (template!=null) {
            value = XmlUtil.fillInParams(template, attributes);
        }
        //TODO: no data document available so could not doing this, is this useful?
//        String value = Utils.transformAttribute(
//                this.wizardData.getDocumentElement()
//                ,template, false, attributes);

        if (value == null) {
            value = defaultvalue;
        }

        if (value != null) {
            XmlUtil.setAttribute(node, name, value);
        }
    }

    /**
     * add &lt;command&gt; element into &lt;field&gt; element. 
     * @param fieldNode the &lt;field&gt; node
     * @param commandname the name of the &lt;command&gt; element
     * @param did serial id.
     */
    private Node addSingleCommand(Node fieldNode, String commandname, String did) {
        return addSingleCommand(fieldNode, commandname, did, null);
    }

    /**
     * add &lt;command&gt; element into &lt;field&gt; element. 
     * @param fieldNode the &lt;field&gt; node
     * @param commandname the name of the &lt;command&gt; element
     * @param did serial id.
     * @param otherdid the did of other node's which is used as refered element.
     */
    private Node addSingleCommand(Node fieldNode, String commandname, String did, String otherdid) {
        return addSingleCommand(fieldNode,commandname,did,otherdid,null);
    }

    /**
     * add &lt;command&gt; element into &lt;field&gt; element. 
     * @param fieldNode the &lt;field&gt; node
     * @param commandname the name of the &lt;command&gt; element
     * @param did serial id.
     * @param otherdid the did of other node's which is used as refered element.
     */
    private Node addSingleCommand(Node fieldNode, String commandname, String did, String otherdid, String value) {
        
        if (did == null) {
            did = "";
        }
        
        if (otherdid == null) {
            otherdid = "";
        }
        
        if (value ==null) {
            value = did;
        }

        Element command = fieldNode.getOwnerDocument().createElement("command");
        command.setAttribute("name", commandname);
        command.setAttribute("cmd", "cmd/" + commandname + "/" + XmlUtil.getAttribute(fieldNode, "fid")
                + "/" + did + "/" + otherdid + "/");
        command.setAttribute("value", value);
        fieldNode.appendChild(command);
        return command;
    }

    /**
     * comparator used to sort the relation list
     * @todo javadoc
     * 
     * @author caicai
     * @created 2005-9-30
     * @version $Id: WizardDOM.java,v 1.1 2005-11-28 10:09:27 nklasens Exp $
     */
    static class OrderByComparator implements Comparator {
        
        static Pattern PATTERN_XPATH_ORDERBY = Pattern.compile(
            "((object)/)?(field\\[@name=['\"](\\w*)['\"]\\])");
        
        String datafrom = null;
        String fieldName = null;
        boolean compareAsNumber = false;
        
        OrderByComparator(String orderby, String ordertype) {
            this.fieldName = orderby;
            this.compareAsNumber = ordertype.equals("number");
            Matcher matcher = PATTERN_XPATH_ORDERBY.matcher(orderby);
            if (matcher.find()) {
                String from = matcher.group(2);
                if (from != null && !"".equals(from)) {
                    this.datafrom = from;
                }
                this.fieldName = matcher.group(4);
            }
        }

        public int compare(Object o1, Object o2) {
            BaseData d1 = (BaseData) o1;
            BaseData d2 = (BaseData) o2;
            
            FieldData field1 = null;
            FieldData field2 = null;

            if (fieldName == null) {
                //TODO: temporary sort logic, just give a default sort to the list
                String n1 = d1.getNumber();
                String n2 = d2.getNumber();
                return n1.compareTo(n2);
            }
            field1 = this.getField(d1);
            field2 = this.getField(d2);
            
            String value1 = null;
            if (field1!=null) {
                value1 = field1.getStringValue();
            }
            String value2 = null;
            if (field2!=null) {
                value2 = field2.getStringValue();
            }
            //this means it we want evaludate the value as a number
            if (compareAsNumber) {
                try {
                    return Double.valueOf(value1).compareTo(Double.valueOf(value2));
                } catch (Exception e) {
                    log.error("Invalid field values (" + value1 + "/" + value2 + "):" + e);
                    return 0;
                }
            } else {
                // Determine the orderby values and compare 
                if (value1!=null) {
                    return value1.compareToIgnoreCase(value2);
                } else if (value2!=null) {
                    return 0-value2.compareToIgnoreCase(value1);
                }
                return 0;
            }
        }
        
        /**
         * get field of the node
         * @param node
         * @return
         */
        private FieldData getField(BaseData node) {
            if (node==null) {
                return null;
            }
            RelationData relation = null;
            ObjectData object = null;
            if (node instanceof RelationData) {
                relation = (RelationData)node;
                object = relation.getRelatedObject();
            } else {
                object = (ObjectData)node;
            }
            FieldData field = null;
            if (relation !=null && "object".equals(datafrom)==false) {
                // if relation and not specified datafrom="object", try to load field's value from relation
                field = relation.findFieldByName(fieldName);
            }
            if (field==null) {
                // if not get relation's field's value, try to find in object
                // if fdatapath="object/field[@name='fieldname']", should use related object's field
                field = object.findFieldByName(fieldName);
            }
            return field;
        }
    }

}
