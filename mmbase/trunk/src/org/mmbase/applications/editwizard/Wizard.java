/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.*;
import java.io.Writer;
import java.io.File;
import javax.servlet.ServletRequest;
import org.w3c.dom.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.*;
import javax.xml.transform.TransformerFactory;
import org.mmbase.util.xml.URIResolver;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: Wizard.java,v 1.46 2002-07-08 15:21:12 pierre Exp $
 *
 */
public class Wizard {
    // logging
    private static Logger log = Logging.getLoggerInstance(Wizard.class.getName());

    // Some of these variables are placed public, for debugging reasons.
    private Document preform;

    /**
     * The cloud used to connect to MMBase
     */
    private Cloud cloud;

    // This object will be used the revolve URI's, for example those of XSL's and XML's.
    private URIResolver uriResolver = null;

    private String context = null;

    // schema / session data
    private String name;
    private String objectNumber;

    // the wizard (file) name. Eg.: samples/jumpers will choose the file $path/samples/jumpers.xml
    private String wizardName;

    /**
     * @javadoc
     */
    private String dataId;

    // stores the current formid
    private String currentFormId;

    // filename of the stylesheet which should be used to make the html form.
    private File wizardStylesheetFile;

    private String sessionId;
    private String sessionKey="editwizard";
    private String referrer="";

    /**
     * public xmldom's: the schema, the data and the originaldata is stored.
     *
     * @scope private
     */
    private  Document schema;
    private  Document data;
    private  Document originalData;

    // not yet committed uploads are stored in there hashmaps
    private Map binaries;
    private Map binaryNames;
    private Map binaryPaths;

    // in the wizards, variables can be used. Values of the variables are stored here.
    private Map variables;

    // the constraints received from mmbase are stored + cached in this xmldom
    private Document constraints;

    // Seconds.
    private long listQueryTimeOut = 60 * 60;

    // the database connector handles communition with mmbase. the instance is stored here.
    private WizardDatabaseConnector databaseConnector;

    /**
     * This boolean tells the jsp that the wizard may be closed, as far as he is concerned.
     */
    private boolean mayBeClosed = false;

    /**
     * This boolean tells the jsp that a new (sub) wizard should be started
     */
    private boolean startWizard=false;

    /**
     * The command to use dor starting a new (sub) wizard
     * Only set when startwizard is true
     */
    private WizardCommand startWizardCmd=null;

    /**
     * This boolean tells the jsp that the wizard was committed, and changes may have been made
     */
    private boolean committed = false;

    /**
     * This list stores all errors and warnings occured
     *
     */
    private List errors;

    /**
     * Constructor. Setup initial variables. No connection to mmbase is made yet.
     * Make sure a valid path is supplied.
     * Use initialize() to really startup the wizard and start communicating with mmbase
     *
     * @param uri  With the help of this URIResolverthe wizard schema's and the xsl's will be loaded
     */
    protected Wizard(String c, URIResolver uri) throws WizardException {
        context = c;
        uriResolver = uri;
        binaries = new HashMap();
        binaryNames = new HashMap();
        binaryPaths = new HashMap();
        variables = new Hashtable();
        errors = new Vector();
        constraints = Utils.parseXML("<constraints/>");
    }

    /**
     * Constructor. Setup initial variables. No connection to mmbase is made yet.
     * Make sure a valid path is supplied.
     * Use initialize() to really startup the wizard and start communicating with mmbase
     *
     * @param uri  With the help of this URIResolverthe wizard schema's and the xsl's will be loaded
     */
    public Wizard(String context, URIResolver uri, String wizardname, String dataid, Cloud cloud)  throws WizardException, SecurityException {
        this(context, uri);
        initialize(wizardname, dataid, cloud);
    }

    /**
     * Creates a connection to MMBase using a {@link WizardDatabaseConnector}.
     * Also loads the wizard schema, and creates a work document using {@link #loadWizard()}.
     *
     * @param wizardname the wizardname which the wizard will use. Eg.: samples/jumpers
     * @param dataid the dataid (objectNumber) of the main object what is used by the editwizard
     */
    public void initialize(String wizardname, String dataid, Cloud cloud) throws WizardException, SecurityException {
        // initialize database connector
        databaseConnector = new WizardDatabaseConnector();
        databaseConnector.setUserInfo(cloud);
        // set cloud
        this.cloud=cloud;
        // add username to variables
        variables.put("username", cloud.getUser().getIdentifier());
        // actually load the wizard
        loadWizard(wizardname, dataid);
    }

    public void setSessionId(String s) {
        sessionId = s;
    }

    public void setSessionKey(String s) {
        sessionKey = s;
    }

    public void setReferrer(String s) {
        referrer = s;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public String getDataId() {
        return dataId;
    }

    public Document getData() {
        return data;
    }
    public Document getSchema() {
        return schema;
    }

    public Document getPreform() {
        return preform;
    }
    public boolean error() {
        return errors.size() > 0;
    }
    public Iterator getErrors() {
        return errors.iterator();
    }
    public String getErrorString() {
        String str = "";
        Iterator iter = getErrors();
        while (iter.hasNext()) {
            str += (String)iter.next() + "\n";
        }
        return str;
    }

    /**
     * Returns whether the wizard may be closed
     */
    public boolean committed() {
        return committed;
    }

    /**
     * Returns whether the wizard may be closed
     */
    public boolean mayBeClosed() {
        return mayBeClosed;
    }

    /**
     * Returns whether a sub wizard should be started
     */
    public boolean startWizard() {
        return startWizard;
    }

    /**
     * Returns the subwizard start command
     */
    public WizardCommand getStartWizardCommand() {
        return startWizardCmd;
    }

    /**
     * Loads the wizard schema, and a work document, and fills it with initial data.
     *
     * @param wizardname the wizardname which the wizard will use. Eg.: samples/jumpers
     * @param dataid the dataid (objectNumber) of the main object what is used by the editwizard
     */
    protected void loadWizard(String wizardname, String di) throws WizardException, SecurityException {

        if (wizardname == null) throw new WizardException("Wizardname may not be null");
        // if (di         == null) throw new WizardException("ObjectNumber may not be null");
        wizardName = wizardname;
        dataId = di;
        File wizardSchemaFile = uriResolver.resolveToFile(wizardName + ".xml");
        wizardStylesheetFile = uriResolver.resolveToFile("xsl/wizard.xsl");

        // store variables so that these can be used in the wizard schema
        variables.put("wizardname", wizardname);
        if (dataId != null) variables.put("objectnumber", dataId);
        // TODO: dataId == null only means that this wizard object is being abused in list.jsp
        //       should not allow this kind of hackery.

        // load wizard schema
        loadSchema(wizardSchemaFile);    // expanded filename of the wizard

        // setup original data
        originalData = Utils.emptyDocument();

        // If the given dataid=new, we have to create a new object first, given
        // by the object definition in the schema.
        // If dataid equals null, we don't need to do anything. Wizard will not be used to show or save data; just to load schema information.
        if (dataId != null) {
            if (dataId.equals("new")){
                log.debug("Creating new xml");
                // Get the definition and create a copy of the object-definition.
                Node objectdef = Utils.selectSingleNode(schema, "./wizard-schema/action[@type='create']");
                if (objectdef==null) {
                    throw new WizardException("You tried to start a create action in the wizard, but no create action was defined in the wizard schema. Please supply a <action type='create' /> section in the wizard.");
                }
                objectdef = objectdef.cloneNode(true);
                log.debug("Going to creating a new object " + objectdef.getNodeName() + " type " + Utils.getAttribute(objectdef,"type"));
                // We have to add the object to the data, so first determine to which parent it belongs.
                data = Utils.parseXML("<data />");
                Node parent = data.getDocumentElement();
                // Ask the database to create that object, ultimately to get the new id.
                Node newobject = databaseConnector.createObject(data, parent, objectdef, variables);
                parent.appendChild(newobject);
                databaseConnector.tagDataNodes(data);
                dataId = Utils.getAttribute(newobject,"number");
                if (log.isDebugEnabled()) {
                    log.debug("Created object " + newobject.getNodeName() + " type " + Utils.getAttribute(newobject,"type") + ", id " + dataId);
                }
            } else {
                // - load data.
                // - tags the datanodes
                try{
                    data = databaseConnector.load(schema.getDocumentElement(), dataId);
                    if (data==null) {
                        throw new WizardException("The requested object could not be loaded from MMBase. ObjectNumber:" + dataId + ". Does the object exists and do you have enough rights to load this object.");
                    }
                    // store original data, so that the put routines will know what to save/change/add/delete
                    originalData.appendChild(originalData.importNode(data.getDocumentElement().cloneNode(true), true));
                } catch (org.mmbase.security.SecurityException secure){
                    log.warn("Wizard failed to login: " + secure.getMessage());
                    throw secure;
                } catch (Exception e){
                    throw new WizardException("Wizard could not be initialized. (" + e.toString() + ")");
                }
            }
        }

        // initialize a editor session
        if (currentFormId == null){currentFormId = determineNextForm("first");}
    }

    /**
     * Processes an incoming request (usually passed on by a jsp code).
     * First, all given values are stored in the current datatree,
     * Second, all given commands are processed sequentially.
     *
     * @param req the ServletRequest contains the name-value pairs received through the http connection
     */
    public void processRequest(ServletRequest req) throws WizardException {
        String curform = req.getParameter("curform");
        if (curform != null && !curform.equals("")) currentFormId = curform;

        storeValues(req);
        processCommands(req);
    }

    /**
     * Constructs and writes final form-html to the given out writer.
     * You can specify an instancename, so that the wizard is able to start another wizard in the
     * <em>same</em> session. The jsp pages and in the html the instancenames are used to keep track
     * of one and another.
     *
     * @param out The writer where the output (html) should be written to.
     * @param instancename name of the current instance
     */
    public void writeHtmlForm(Writer out, String instanceName) throws WizardException {
        log.debug("writeHtmlForm for " + instanceName);
        Node datastart = Utils.selectSingleNode(data, "/data/*");
        // Build the preHtml version of the form.
        preform = createPreHtml(schema.getDocumentElement(), currentFormId, datastart, instanceName);
        Validator.validate(preform, schema);
        Map params = new HashMap();
        params.put("ew_context", context);
        // params.put("ew_imgdb",   org.mmbase.module.builders.AbstractImages.getImageServletPath(context));
        params.put("sessionid", sessionId);
        params.put("sessionkey", sessionKey);
        params.put("referrer", referrer);
        try {
            Utils.transformNode(preform, wizardStylesheetFile, uriResolver, out, params);
        } catch (javax.xml.transform.TransformerException e) {
            throw new WizardException(e.toString() + ":" + Logging.stackTrace(e));
        }
    }

    /////////////////////////////////////

    /**
     * Internal method which is used to store the passed values. this method is called by processRequest.
     *
     * @see #processRequest
     */
    private void storeValues(ServletRequest req) throws WizardException {
        Document doc = Utils.parseXML("<request/>");
        Enumeration list = req.getParameterNames();
        log.debug("Synchronizing editor data, using the request");
        while (list.hasMoreElements()) {
            String name = (String)list.nextElement();
            String[] ids = processFormName(name);
            if (ids!=null) {
                String formEncoding = req.getCharacterEncoding();
                if (log.isDebugEnabled()) log.debug("found encoding in the request: " + formEncoding);
                String result;
                if (formEncoding == null) {
                   log.debug("request did not mention coding");
                   // The form encoding was not known, so probable the local was used or ISO-8859-1
                   // lets make sure it is right:
                   try {
                      if (cloud != null) {
                         log.debug("Cloud found, supposing parameter in " + cloud.getCloudContext().getDefaultCharacterEncoding());
                         result = new String(req.getParameter(name).getBytes(),
                                             cloud.getCloudContext().getDefaultCharacterEncoding());
                         } else { // no cloud? I don't know how to get default char encoding then.
                            // suppose it utf-8
                            log.debug("No cloud found, supposing parameter in UTF-8" + req.getParameter(name));
                            result = new String(req.getParameter(name).getBytes(), "UTF-8");
                         }
                   } catch (java.io.UnsupportedEncodingException e) {
                       log.warn(e.toString());
                       result = req.getParameter(name);
                   }
                } else { // the request encoding was known, so, I think we can suppose that the Parameter value was interpreted correctly.
                   result = req.getParameter(name);
                }
                storeValue(ids[0], ids[1], result);
            }
        }
    }

    /**
     * This method is used to determine what form is the sequential next, previous, first etc.
     * You can use the parameter to indicate what you want to know:
     *
     * @param direction     indicates what you wanna know. Possibilities:
     * <code>
     * - first (default)
     * - last
     * - previous
     * - next
     * </code>
    */
    public String determineNextForm(String direction){
        String stepDirection = direction;
        if (stepDirection == null){stepDirection = "first";}
        // Determine if there are steps defined.
        // If so, use the step elements to determine previous and next forms.
        // If not, use the form-schema elements to determine previous and next forms.

        // Assume that steps are defined for the moment.
        String nextformid = "FORM_NOT_FOUND";
        Node laststep = Utils.selectSingleNode(schema, "//steps/step[@form-schema='" + currentFormId + "']");
        Node nextstep = null;
        // If the last step doesn't exist, get the first step.
        // If the last step exists, determine the next step.
        if (laststep == null || stepDirection.equals("first")){
            nextstep = Utils.selectSingleNode(schema, "//steps/step");
            nextformid = Utils.getAttribute(nextstep, "form-schema");
        }else{
            if (stepDirection.equals("previous")){
                nextstep = Utils.selectSingleNode(laststep, "./preceding-sibling::step");
            }else if (stepDirection.equals("last")){
                nextstep = Utils.selectSingleNode(laststep, "../step[position()=last()]");
            }else{
                nextstep = Utils.selectSingleNode(laststep, "./following-sibling::step");
            }
            if (nextstep == null){
                nextformid = "WIZARD_OUT_OF_BOUNDS";
            }else{
                nextformid = Utils.getAttribute(nextstep,"form-schema");
            }
        }
        return nextformid;
    }

    /**
     * This method generates the pre-html. See the full-spec method for more details.
     *
     * @see #createPreHtml
     */
    public Document createPreHtml(String instanceName) throws WizardException {
        Node datastart = Utils.selectSingleNode(data, "/data/*");
        return createPreHtml(schema.getDocumentElement(), "1", datastart, instanceName);
    }

    /**
     * This method generated the pre-html.
     *
     * Pre-html is a temporarily datatree (xml of course) which is used to generate the final html from.
     * Is uses the wizardschema, the current formid, the current datatree and the instancename to generate the pre-html.
     *
     * Mainly, the pre-html contains all data needed to make a nice htmlform. The XSL's use the pre-html to generate html (thats why pre-html)
     *
     * @param       wizardSchema    the main node of the schema to be used. Includes should already be resolved.
     * @param       formid          The id of the current form
     * @param       data            The main node of the data tree to be used
     * @param       instancename    The instancename of this wizard
     */

    public Document createPreHtml(Node wizardSchema, String formid, Node data, String instanceName) throws WizardException {
        if (log.isDebugEnabled()) log.debug("Create preHTML of " + instanceName);
        // intialize preHTML wizard
        Document preHtml = Utils.parseXML("<wizard instance=\""+instanceName+"\" />");
        Node wizardnode = preHtml.getDocumentElement();

        // copy all global wizard nodes.
        NodeList globals = Utils.selectNodeList(wizardSchema, "title|description");
        Utils.appendNodeList(globals, wizardnode);

        // find the current step and, if appliccable, the next and previous ones.
        Utils.createAndAppendNode(wizardnode, "curform", formid);



        Node step = Utils.selectSingleNode(wizardSchema, "./steps/step[@form-schema='" + formid + "']");
        if (step!=null) {
            // Yes. we have step information. Let's add info about that.
            String otherformid = "";
            Node prevstep = Utils.selectSingleNode(step, "./preceding-sibling::step[1]");
            if (prevstep!=null) otherformid = Utils.getAttribute(prevstep,"form-schema");
            Utils.createAndAppendNode(wizardnode, "prevform", otherformid);

            otherformid = "";
            Node nextstep = Utils.selectSingleNode(step, "./following-sibling::step[1]");
            if (nextstep!=null) otherformid = Utils.getAttribute(nextstep,"form-schema");
            Utils.createAndAppendNode(wizardnode, "nextform", otherformid);
        }

        // process all forms
        NodeList formlist = Utils.selectNodeList(schema, "/*/form-schema");

        if (formlist.getLength()==0) {
            throw new WizardException("No form-schema was found in the xml. Make sure at least one form-schema node is present.");
        }
        for (int f=0; f<formlist.getLength(); f++) {
            Node form = formlist.item(f);

            // Make prehtml form.
            Node prehtmlform = preHtml.createElement("form");
            Utils.copyAllAttributes(form, prehtmlform);
            wizardnode.appendChild(prehtmlform);

            // Add the title, description.
            NodeList props = Utils.selectNodeList(form, "title");
            Utils.appendNodeList(props, prehtmlform);

            // check all fields and do the thingies
            createPreHtmlForm(prehtmlform, form, data);
        }

        // now, resolve optionlist values:
        // - The schema contains the list definitions, from which the values are copied.
        // - Each list may have a query attached, which is performed before the copying.
        NodeList optionlists = Utils.selectNodeList(wizardnode, ".//optionlist[@select]");
        for (int i=0; i<optionlists.getLength(); i++) {
            Node optionlist = optionlists.item(i);

            String listname = Utils.getAttribute(optionlist,"select");
            log.debug("Handling optionlist: " + i + ": " + listname);
            Node list = Utils.selectSingleNode(wizardSchema, "/*/lists/optionlist[@name='" + listname + "']");
            if (list == null){
                // Not found in definition. Put an error in the list and proceed with the
                // next list.
                log.debug("Not found! Proceeding with next list.");
                Element option = list.getOwnerDocument().createElement("option");
                option.setAttribute("id","-");
                Utils.storeText(option,"Error: optionlist '" + listname + "' not found");
                optionlist.appendChild(option);
                continue;
            }

            // Test if this list has a query and get the time-out related values.
            Node query = Utils.selectSingleNode(list,"query");
            long currentTime = new Date().getTime();
            long queryTimeOut = 1000 * Long.parseLong(Utils.getAttribute(list,"query-timeout",String.valueOf(this.listQueryTimeOut)));
            long lastExecuted = currentTime - queryTimeOut - 1;
            if (query != null){
                String lastExecutedString = Utils.getAttribute(query,"last-executed","never");
                if (!lastExecutedString.equals("never")){
                    lastExecuted = Long.parseLong(lastExecutedString);
                }
            }

            // Execute the query if it's there and only if it has timed out.
            if (query != null && (currentTime - lastExecuted) > queryTimeOut){
                log.debug("Performing query for optionlist '" + listname + "'. Cur time " + currentTime + " last executed " + lastExecuted + " timeout " + queryTimeOut + " > " + (currentTime - lastExecuted));
                Node queryresult = null;
                try{
                    queryresult = databaseConnector.getList(query);
                    queryresult = Utils.selectSingleNode(queryresult,"/getlist/query");
                } catch (Exception e){
                    // Bad luck, tell the user and try the next list.
                    log.debug("Error during query, proceeding with next list: " + e.toString());
                    Element option = list.getOwnerDocument().createElement("option");
                    option.setAttribute("id","-");
                    Utils.storeText(option,"Error: query for '" + listname + "' failed");
                    optionlist.appendChild(option);
                    continue;
                }

                // Remind the current time.
                Utils.setAttribute(query,"last-executed",String.valueOf(currentTime));
                // Remove any already existing options.
                NodeList olditems = Utils.selectNodeList(list,"option");
                for (int itemindex=0; itemindex<olditems.getLength(); itemindex++){
                    list.removeChild(olditems.item(itemindex));
                }
                // Loop through the queryresult and add the included objects by creating
                // an option element for each one. The id and content of the option
                // element are taken from the object by performing the xpaths on the object,
                // that are given by the list definition.
                NodeList items = Utils.selectNodeList(queryresult, "*");
                String idPath = Utils.getAttribute(list,"optionid");
                String contentPath = Utils.getAttribute(list,"optioncontent");
                for (int itemindex=0; itemindex<items.getLength(); itemindex++){
                    Node item = items.item(itemindex);
                    String optionId = Utils.transformAttribute(item,idPath,true);
                    String optionContent = Utils.transformAttribute(item,contentPath,true);
                    Element option = list.getOwnerDocument().createElement("option");
                    option.setAttribute("id",optionId);
                    Utils.storeText(option,optionContent);
                    list.appendChild(option);
                }
            }

            // Now copy the items of the list definition to the preHTML list.
            NodeList items = Utils.selectNodeList(list, "option");
            Utils.appendNodeList(items, optionlist);

            // set selected=true for option which is currently selected
            String selectedValue = Utils.selectSingleNodeText(optionlist, "../value/text()", ""); //.getNodeValue();
            log.debug("Trying to preselect the list at value: "+ selectedValue);
            Node selectedoption = Utils.selectSingleNode(optionlist, "option[@id='" + selectedValue + "']");
            if (selectedoption!=null) {
                // found! Let's set it selected.
                Utils.setAttribute(selectedoption, "selected", "true");
            }
        }

        // Okee, we are ready. Let's return what we've been working on so hard.
        return preHtml;
    }

    /**
     * This method is used by the #createPreHtml method to generate a pre-html form.
     *
     * @param       form    The node of the pre-html form which is to be generated
     * @param       formdef the node of the wizardschema form definition
     * @param       data    Points to the datacontext node which should be used for this form.
     */
    public void createPreHtmlForm(Node form, Node formdef, Node data) throws WizardException {
        if (log.isDebugEnabled()) log.debug("Creating preHTMLForm for form:" + form + " / formdef:" + formdef + " / data:" + data);
        // select all fields on first level
        NodeList fields = Utils.selectNodeList(formdef, "fieldset|field|list|command");

        // process all possible fields
        // - Parse the fdatapath attribute to obtain the corresponding data fields.
        // - Create a form field for each found data field.
        for (int i=0; i<fields.getLength(); i++) {
            Node field = fields.item(i);
            // let's see what we should do here
            String nodeName = field.getNodeName();
            // a field set
            if (nodeName.equals("fieldset")) {
                Node newfieldset = form.getOwnerDocument().createElement("fieldset");
                Utils.copyAllAttributes(field, newfieldset);
                NodeList itemprops = Utils.selectNodeList(field, "prompt");
                Utils.appendNodeList(itemprops, newfieldset);
                // place newfieldset in pre-html form
                form.appendChild(newfieldset);
                createPreHtmlForm(newfieldset, field, data);
            } else {

            String xpath = Utils.getAttribute(field, "fdatapath", null);

            if (xpath == null) {
                throw new WizardException("A field tag should contain one of the following attributes: fdatapath or name");
            }
            // xpath is found. Let's see howmany 'hits' we have
            NodeList fieldinstances = Utils.selectNodeList(data, xpath);
            if (fieldinstances==null) {
                throw new WizardException("The xpath: " + xpath + " is not valid. Note: this xpath maybe generated from a &lt;field name='fieldname'&gt; tag. Make sure you use simple valid fieldnames use valid xpath syntax.");
            }
            Node fieldDataNode = null;
            if (fieldinstances.getLength() > 0) fieldDataNode = fieldinstances.item(0);

            // A normal field.
            if (nodeName.equals("field")) {
                if (fieldDataNode != null) {
                    // create normal formfield.
                    mergeConstraints(field, fieldDataNode);
                    createFormField(form, field, fieldDataNode);
                } else {
                    String ftype = Utils.getAttribute(field, "ftype");
                    if("function".equals(ftype)) {
                        log.debug("Not an data node, setting number attribute, because it cannot be found with fdatapath");
                        //set number attribute in field
                        Utils.setAttribute(field, "number",  Utils.selectSingleNodeText(data, "object/@number", null));
                        createFormField(form, field, fieldDataNode);
                    }
                }

            }
            // A list "field". Needs special processing.
            if (nodeName.equals("list")) {
                createFormList(form, field, fieldinstances, data);
            }

            }
        }
    }

    /**
     * This method loads the schema using the properties of the wizard. It loads the wizard using #wizardSchemaFilename,
     * resolves the includes, and 'tags' all datanodes. (Places temp. ids in the schema).
     *
     */
    private void loadSchema(File wizardSchemaFile) throws WizardException {
        schema = Utils.loadXMLFile(wizardSchemaFile);

        resolveIncludes(schema.getDocumentElement());
        resolveShortcuts(schema.getDocumentElement(), true);

        log.debug("Schema loaded (and resolved): " + wizardSchemaFile);

        // tag schema nodes
        NodeList fields = Utils.selectNodeList(schema, "//field|//list|//item");
        Utils.tagNodeList(fields, "fid", "f", 1);
    }

    /**
     * This method resolves the includes (and extends) in a wizard. It searches for include="" attributes, and searches for extends="" attributes.
     * Include means: the file is loaded (uses the path and assumes it references from the basepath param, and the referenced node
     * is placed 'over' the existing node. Attributes are copied also. Any content in the original node is removed.
     * Extends means: same as include, but now, the original content is not thrown away, and the nodes are placed after the included node.<br />
     * Note: this does not work with teh wizard-schema, and only ADDS objects when overriding (it does not replace them)
     *
     * This method is a recursive one. Included files are also scanned again for includes.
     *
     * @param       node    The node from where to start searching for include and extends attributes.
     *
     */
    private void resolveIncludes(Node node) throws WizardException {
        // Resolve references to elements in other wizards. This can be by inclusion
        // or extension.
        NodeList externalReferences = Utils.selectNodeList(node, "//*[@include or @extends]");
        Document targetdoc = node.getOwnerDocument();
        if (externalReferences != null){
            for (int i=0; i<externalReferences.getLength(); i++){
                Node referer = externalReferences.item(i);
                boolean inherits = !Utils.getAttribute(referer, "extends", "").equals("");
                String includeUrl = Utils.getAttribute(referer, "include");
                if (inherits) includeUrl = Utils.getAttribute(referer, "extends");
                try {
                    // Resolve the filename and form-schema id.
                    String url = includeUrl;
                    String externalId = "not applicable";
                    int hash = includeUrl.indexOf('#');
                    if (hash != -1){
                        url = includeUrl.substring(0, includeUrl.indexOf('#'));
                        externalId = includeUrl.substring(includeUrl.indexOf('#') + 1);
                    }
                    File file = uriResolver.resolveToFile(url);

                    // Load the external file.
                    Document externalDocument = Utils.loadXMLFile(file);
                    if (externalDocument==null) {
                        throw new WizardException("Could not load and parse included file. Filename:" + file);
                    }

                    // Add a copy of the external part to our schema here, to replace the
                    // referer itself.
                    Node externalPart = null;
                    if (hash == -1){
                        // Load the entire file.
                        externalPart = externalDocument.getDocumentElement();
                    } else if (externalId.startsWith("xpointer(")){
                        // Load only part of the file, using an xpointer.
                        String xpath = externalId.substring(9,externalId.length()-1);
                        externalPart = Utils.selectSingleNode(externalDocument, xpath);
                    } else {
                        // Load only the node with the given id.
                        externalPart = Utils.selectSingleNode(externalDocument, "//node()[@id='" + externalId + "']");
                    }

                    // recurse!
                    resolveIncludes(externalPart);

                    // place loaded external part in parent...
                    Node parent = referer.getParentNode();
                    externalPart = parent.insertBefore(targetdoc.importNode(externalPart, true),referer);
                    // If the old node had some attributes, copy them to the included one.
                    Utils.copyAllAttributes(referer,externalPart);
                    //
                    if (inherits){
                        NodeList overriders = Utils.selectNodeList(referer, "node()");
                        for (int k=0; k<overriders.getLength(); k++){
                            externalPart.appendChild(overriders.item(k).cloneNode(true));
                        }
                    }
                    // Remove the refering node.
                    parent.removeChild(referer);
                } catch (RuntimeException e){
                    log.error(Logging.stackTrace(e));
                    throw new WizardException("Error resolving external part '" + includeUrl + "'");
                }
            }
        }
    }

    /**
     * Resolves shortcuts placed in the schema.
     * eg.: if a user just entered <field name="firstname" /> it will be replaced by <field fdatapath="field[@name='firstname']" />
     *
     * later, other simplifying code could be placed here, so that for more simple fdatapath's more simple commands can be used.
     * (maybe we should avoid using xpath in total for normal use of the editwizards?)
     *
     * @param   schemanode  The schemanode from were to start searching
     * @param   recurse     Set to true if you want to let the process search in-depth through the entire tree, false if you just want it to search the first-level children
     */
    private void resolveShortcuts(Node schemanode, boolean recurse) {
        String xpath;
        if (recurse) xpath=".//field|.//list"; else xpath="field|list";
        NodeList children = Utils.selectNodeList(schemanode, xpath );
        Node node;
        for (int i=0; i<children.getLength(); i++) {
            resolveShortcut(children.item(i));
        }

        // if no <steps /> node exist, a default node is created with all form schema's in found order
        if (Utils.selectSingleNode(schemanode, "steps")==null) {
            Node stepsnode = schemanode.getOwnerDocument().createElement("steps");
            NodeList forms = Utils.selectNodeList(schemanode, "form-schema");
            for (int i=0; i<forms.getLength(); i++) {
                Node formstep = schemanode.getOwnerDocument().createElement("step");
                String formid = Utils.getAttribute(forms.item(i), "id", null);
                if (formid==null) {
                    formid="tempformid_"+i;
                    Utils.setAttribute(forms.item(i), "id", formid);
                }
                Utils.setAttribute(formstep, "form-schema", formid);
                stepsnode.appendChild(formstep);
            }
            schemanode.appendChild(stepsnode);
        }
    }

    /**
     * resolves possible shortcut for this given single node. (@see #resolveShortcuts for more information)
     *
     * @param   node    The node to resolve
     */
    private void resolveShortcut(Node singlenode) {
        // transforms <field name="firstname"/> into <field fdatapath="field[@name='firstname']" />
        String nodeName = singlenode.getNodeName();
        if (nodeName.equals("field")) {
            // field nodes
            String name = Utils.getAttribute(singlenode, "name", null);
            String fdatapath = Utils.getAttribute(singlenode, "fdatapath", null);
            if (name!=null && fdatapath==null) {
                // normal field or a field inside a list node?
                Node parentNode=singlenode.getParentNode();
                String parentname = parentNode.getNodeName();
                // skip fieldset
                if (parentname.equals("fieldset")) {
                    parentname = parentNode.getParentNode().getNodeName();
                }
                if (parentname.equals("item")) {
                    fdatapath = "object/field[@name='"+name+"']";
                } else {
                    fdatapath = "field[@name='"+name+"']";
                }

                Utils.setAttribute(singlenode, "fdatapath", fdatapath);
            }
        } else if (nodeName.equals("list")) {
            // List nodes
            String role = Utils.getAttribute(singlenode, "role", "insrel");
            String destination = Utils.getAttribute(singlenode, "destination", null);
            String fdatapath = Utils.getAttribute(singlenode, "fdatapath", null);
            if (destination!=null && fdatapath==null) {
                // convert shortcut in list field to full fdatapath
                fdatapath = "relation[@role='"+role+"' and object/@type='"+destination+"']";

                // normal list or a list inside a list?
                String parentname = singlenode.getParentNode().getNodeName();
                if (parentname.equals("item")) {
                        fdatapath="object/"+fdatapath;
                }

                Utils.setAttribute(singlenode, "fdatapath", fdatapath);
            }
        }
    }

    /**
     * 	Creates a form item (each of which may consist of several single form fields)
     *  for each given datanode.
     */
    private void createFormList(Node form, Node fieldlist, NodeList datalist, Node parentdatanode) throws WizardException {
        // copy all attributes from fielddefinition to new pre-html field definition
        log.debug("creating form list");
        Node newlist = fieldlist.cloneNode(false);
        newlist = form.getOwnerDocument().importNode(newlist, false);
        Utils.copyAllAttributes(fieldlist, newlist);

        // Add the title, description.
        NodeList props = Utils.selectNodeList(fieldlist, "title|description|action|command");
        Utils.appendNodeList(props, newlist);

        String hiddenCommands = "|" + Utils.getAttribute(fieldlist,"hidecommand") + "|";

        // place newfield in pre-html form
        form.appendChild(newlist);

        // calculate minoccurs and maxoccurs
        int minoccurs = Integer.parseInt(Utils.getAttribute(fieldlist, "minoccurs", "0"));
        int nrOfItems = datalist.getLength();

        int maxoccurs = -1;
        String maxstr = Utils.getAttribute(fieldlist, "maxoccurs", "*");
        if (!maxstr.equals("*")) maxoccurs = Integer.parseInt(maxstr);

        String defaultdisplaymode = Utils.getAttribute(newlist,"defaultdisplaymode","edit");

        String orderby = Utils.getAttribute(fieldlist, "orderby", null);
        if ((orderby!=null) && (orderby.indexOf("@")==-1)) {
            orderby="object/field[@name='"+orderby+"']";
        }
        String ordertype = Utils.getAttribute(fieldlist, "ordertype", "string");

        // set the orderby attribute for all the nodes
        List tempstorage=new ArrayList(datalist.getLength());
        for (int dataIndex=0; dataIndex<datalist.getLength(); dataIndex++) {
            Element datacontext = (Element)datalist.item(dataIndex);
            if (orderby!=null) {
                String orderByValue = Utils.selectSingleNodeText(datacontext, orderby, "");
                // make sure of type
                if (ordertype.equals("number")) {
                    double orderDbl;
                    try {
                        orderDbl=Double.parseDouble(orderByValue);
                    } catch (Exception e) {
                        log.error("fieldvalue "+orderByValue+" is not numeric");
                        orderDbl=-1;
                    }
                    orderByValue=""+orderDbl;
                }
                // sets orderby
                datacontext.setAttribute("orderby",orderByValue);
            }
            // clears firstitem
            datacontext.setAttribute("firstitem","false");
            // clears lastitem
            datacontext.setAttribute("lastitem","false");
            tempstorage.add(datacontext);
        }
        // sort list
        if (orderby!=null) {
            Collections.sort(tempstorage,new OrderByComparator(ordertype));
        }

        // and make form
        int listsize=tempstorage.size();
        for (int dataindex=0; dataindex<listsize; dataindex++) {
            Element datacontext = (Element)tempstorage.get(dataindex);

            // Determine the display mode of the current datanode.
            String displaymode = Utils.getAttribute(datacontext,"displaymode",defaultdisplaymode);

            // Create the form item which has the same display mode as this datanode.
            Node item = Utils.selectSingleNode(fieldlist, "item[@displaymode='" + displaymode + "']");
            if (item == null) {
                item = Utils.selectSingleNode(fieldlist, "item");
                if (item == null) {
                    throw new WizardException ("Could not find item in a list of " + wizardName);
                }
                if (log.isDebugEnabled()) log.debug("found an item " + item.toString());
            }
            Node newitem = item.cloneNode(false);
            newitem = form.getOwnerDocument().importNode(newitem, false);
            newlist.appendChild(newitem);
            // Copy all attributes from data to new pre-html field def (mainly needed for the did).
            Utils.copyAllAttributes(datacontext, newitem);
            Utils.copyAllAttributes(item, newitem);
            // Add the title, description.
            NodeList itemprops = Utils.selectNodeList(item, "title|description");
            Utils.appendNodeList(itemprops, newitem);

            // and now, do the recursive trick! All our fields inside need to be processed.
            createPreHtmlForm(newitem, item, datacontext);

            // finally, see if we need to place some commands here
            if (/* nrOfItems > minoccurs && you should be able to replace!*/  hiddenCommands.indexOf("|delete-item|") == -1) {
                addSingleCommand(newitem,"delete-item", datacontext);
            }

            if (orderby!=null) {
                if (dataindex > 0 && hiddenCommands.indexOf("|move-up|") == -1){
                    addSingleCommand(newitem,"move-up", datacontext,
                                                   (Node)tempstorage.get(dataindex-1));
                }
                if ((dataindex+1) < listsize && hiddenCommands.indexOf("|move-down|") == -1){
                    addSingleCommand(newitem,"move-down", datacontext,
                                                       (Node)tempstorage.get(dataindex+1));
                }
            }
            if (dataindex==0) {
                datacontext.setAttribute("firstitem","true");
            }
            if (dataindex==tempstorage.size()-1) {
                datacontext.setAttribute("lastitem","true");
            }
        }

        // should the 'save' button be inactive because of this list?

        // works likes this:
        //  If the minoccurs or maxoccurs condiditions are not satisfied, in the 'wizard.xml'
        //  to the form the 'invalidlist' attribute is filled with the name of the guilty list. By wizard.xsl then this value
        //  is copied to the html.
        //
        //  validator.js/doValidateForm returns invalid as long as this invalid list attribute of the html form is not an
        // emptry string.
        if (log.isDebugEnabled()) log.debug("minoccurs:" + minoccurs + " maxoccurs: " + maxoccurs + " items: " + nrOfItems);
        if ((nrOfItems > maxoccurs && maxoccurs != -1 )|| ( nrOfItems < minoccurs) ) { // form cannot be valid in that case
            // which list?
            String listTitle = Utils.selectSingleNodeText(fieldlist, "title", "some list");
            ((Element) form).setAttribute("invalidlist", listTitle);
        }



        log.debug("can we place an add-button?");
        if (hiddenCommands.indexOf("|add-item|") == -1 && (maxoccurs == -1 || maxoccurs > nrOfItems) && (Utils.selectSingleNode(fieldlist, "action[@type='create']")!=null)) {
            String defaultpath=".";
            if (fieldlist.getParentNode().getNodeName().equals("item")) {
                // this is a list in a list.
                defaultpath = "object";
            }
            String fparentdatapath = Utils.getAttribute(fieldlist, "fparentdatapath", defaultpath);
            Node chosenparent = Utils.selectSingleNode(parentdatanode, fparentdatapath);

            // try to find out what datanode is the parent of inserts...
            if (datalist.getLength()>0 && fparentdatapath.equals(".")) {
                // we have an example and no fparentdatapath was given. So, create a 'brother'
                addSingleCommand(newlist, "add-item", datalist.item(0).getParentNode());
            } else {
                // no living examples exist. Use the chosenparent
                addSingleCommand(newlist, "add-item", chosenparent);
            }
        }
        log.debug("end");
    }

    /**
     * This method generates a form field node in the pre-html.
     *
     * @param       form        the pre-html form node
     * @param       field       the form definition field node
     * @param       datanode     the current context data node. It might be 'null' if the field already contains the 'number' attribute.
     */
    private void createFormField(Node form, Node field, Node datanode) throws WizardException {
        if (log.isDebugEnabled()) log.debug("Creating form field for " + field);
        // copy all attributes from fielddefinition to new pre-html field definition

        Node newfield = form.getOwnerDocument().createElement("field");

        Utils.copyAllAttributes(field, newfield);
        // place newfield in pre-html form
        form.appendChild(newfield);

        List exceptattrs = new Vector();
        exceptattrs.add("fid");
        // copy all attributes from data to new pre-html field def
        if (datanode != null && datanode.getNodeType() != Node.ATTRIBUTE_NODE){
            Utils.copyAllAttributes(datanode, newfield, exceptattrs);
        }

        String ftype = Utils.getAttribute(newfield, "ftype");
        String dttype = Utils.getAttribute(newfield, "dttype");
        // place html form field name (so that we always know about which datanode and fieldnode we are talking)
        String htmlfieldname = calculateFormName(newfield);
        Utils.setAttribute(newfield, "fieldname", htmlfieldname);

        // place objectNumber as attribute number, if not already was placed there by the copyAllAttributes method.
        if (datanode != null && Utils.getAttribute(datanode, "number", null) == null) {
            Utils.setAttribute(newfield, "number", Utils.getAttribute(datanode.getParentNode(),"number"));
        }

        // resolve special attributes
        if (ftype.equals("startwizard")) {
            String objectNumber = Utils.getAttribute(newfield,"objectnumber", null);
            // if no objectnumber is found, assign teh numbe rof teh currebnt field.
            // exception is when teh direct parent is a form.
            // in that acse, wee are editting teh current object, so indtead asisgn new
            // note: this latter does not take into account fieldsets!
            if (objectNumber==null) {
                if (form.getNodeName().equals("form")) {
                    objectNumber="new";
                } else {
                    objectNumber=Utils.getAttribute(newfield, "number", "new");
                }
            }
            objectNumber = Utils.transformAttribute(datanode, objectNumber);
            Utils.setAttribute(newfield, "objectnumber", objectNumber);
        }

        // binary type needs special processing
        if (dttype.equals("binary")) {
            addBinaryData(newfield);
        }

        NodeList list = Utils.selectNodeList(field, "optionlist|prompt|description|action");
        Utils.appendNodeList(list, newfield);
        // place value
        // by default, theValue is the text of the node.
        String theValue = "";
        try {
            if (datanode == null) {
                if (ftype.equals("function")) {
                    theValue = Utils.getAttribute(field, "name");
                    log.debug("Found a function field " + theValue);
                } else {
                    log.debug("Probably a new node");
                    throw new WizardException("No datanode given for field " + theValue + " and ftype does not equal 'function' (but " + ftype + ")");
                }
            } else if (datanode.getNodeType() == Node.ATTRIBUTE_NODE){
                theValue = datanode.getNodeValue();
            } else {
                theValue = datanode.getFirstChild().getNodeValue();
            }
        } catch (RuntimeException e) {
            log.error(Logging.stackTrace(e));
        }
        // if this is a relation, we want the value of the dnumber field
        if (ftype.equals("relation")) {
            theValue = Utils.getAttribute(newfield, "destination");
        }


        if (theValue == null) theValue="";
        Node value = form.getOwnerDocument().createElement("value");
        Utils.storeText(value, theValue);
        newfield.appendChild(value);
    }

    private void addSingleCommand(Node field, String commandname, Node datanode){
        addSingleCommand(field, commandname, datanode, null);
    }

    private void addSingleCommand(Node field, String commandname, Node datanode, Node otherdatanode){
        String otherdid="";
        if (otherdatanode!=null) otherdid = Utils.getAttribute(otherdatanode, "did");
        Element command = field.getOwnerDocument().createElement("command");
        command.setAttribute("name",commandname);
        command.setAttribute("cmd","cmd/" + commandname + "/" + Utils.getAttribute(field, "fid") + "/" + Utils.getAttribute(datanode, "did") + "/" + otherdid + "/");
        command.setAttribute("value",Utils.getAttribute(datanode, "did"));
        field.appendChild(command);
    }

    /**
     * Returns the proper form-name (for <input name="xx" />).
     *
     * @param       preHtmlFormField        This is the prehtml node where the field data should be
     * @return     A string with the proper html field-name.
     */
    private String calculateFormName(Node preHtmlFormField) {
        try {
            String fid = Utils.getAttribute(preHtmlFormField, "fid");
            String did = Utils.getAttribute(preHtmlFormField, "did");
            return "field/"+fid+"/"+did;
        } catch (RuntimeException e) {
            return "field/fid_or_did_missed_a_tag";
        }
    }

    /**
     * This method de-encodes a html field-name (@see #calculateFormName) and returns an Array with the decoded values.
     * @return     The array with id's. First id in the array is the data-id (did), which indicates what datanode is pointed to,
     *              second id is the fid (field-id) which points to the proper fieldnode in the wizarddefinition.
     */
    private String[] processFormName(String formName) {
        String[] res = {"", ""};

        boolean isafield = (formName.indexOf("field/")>-1);
        int nr1 = formName.indexOf("/")+1;
        int nr2 = formName.indexOf("/", nr1)+1;
        if (nr1<1 || nr2<1 || !isafield) {
            // not good. no 2 slashes found
            return null;
        }

        String fid = formName.substring(nr1, nr2-1);
        String did = formName.substring(nr2);
        res[0] = did;
        res[1] = fid;
        return res;
    }

    /**
     * Puts the given value in the right datanode (given by did), depending on the type
     * of the form field.
     *
     * - text,line: the value is stored as text in the datanode.
     * - relation: the value is assumed to be the destination number (dnumber) of the relation.
     *
     * @param  did     The data id where the value should be stored
     * @param  fid     The wizarddefinition field id what applies to this data
     * @param  value   The (String) value what should be stored in the data.
     */
    private void storeValue(String did, String fid, String value) throws WizardException {
        if (log.isDebugEnabled()) {
            log.debug("String value " + value + " in " + did + " for field " + fid);
            log.debug(Utils.getSerializedXML(Utils.selectSingleNode(schema, ".//*[@fid='" + fid + "']")));
        }
        Node dttypeNode = Utils.selectSingleNode(schema, ".//*[@fid='" + fid + "']/@dttype");
        if (dttypeNode == null) {
            throw new WizardException("No node with fid=" + fid + " could be found");
        }
        String dttype = dttypeNode.getNodeValue();
        Node datanode = Utils.selectSingleNode(data, ".//*[@did='" + did + "']");
        boolean ok = false;

        if (datanode == null){
            log.debug("Node datanode found!");
            // Nothing.
        } else if (dttype.equals("binary")) {
            // binaries are stored differently
            if (getBinary(did)!=null) {
                Utils.setAttribute(datanode, "href", did);
                Utils.storeText(datanode,getBinaryName(did));
            }
            ok = true;
        } else {  // default behavior: store content as text
            Utils.storeText(datanode, value);
            ok = true;
        }
        if (!ok) {
            log.warn("Unable to store value for field with dttype " + dttype + ". fid=" + fid + ", did=" + did + ", value=" + value +", wizard:"+wizardName);
        }
    }


    /**
     * This method processes the commands sent over http.
     *
     * @param       req     The ServletRequest where the commands (name/value pairs) reside.
     */
    public void processCommands(ServletRequest req) throws WizardException {

        log.debug("processing commands");
        mayBeClosed = false;
        startWizard=false;
        startWizardCmd=null;

        boolean found=false;
        String commandname="";

        List  errors = new Vector();
        Enumeration list = req.getParameterNames();
        while (list.hasMoreElements()) {
            commandname = (String)list.nextElement();
            if (log.isDebugEnabled()) log.debug("found a command " + commandname);
            if (commandname.indexOf("cmd/")==0 && !commandname.endsWith(".y")) {
                // this is a command.
                String commandvalue = req.getParameter(commandname);
                try{
                    WizardCommand wc = new WizardCommand(commandname, commandvalue);
                    processCommand(wc);
                } catch (WizardException we) {
                    throw we;
                } catch (RuntimeException e){
                    // Have to accumulate the exceptions and report them at the end.
                    String errormsg=Logging.stackTrace(e);
                    log.error(errormsg);
                    errors.add(new WizardException("* Could not process command:"+commandname + "="+commandvalue+"\n"+errormsg));
                }
            }
        }

        if (errors.size() > 0) {
            String errorMessage = "Errors during command processing:";
            for (int i=0; i<errors.size(); i++){
                errorMessage = errorMessage + "\n" + ((Exception)errors.get(i)).toString();
            }
            throw new WizardException(errorMessage);
        }
    }

    /**
     * This method is usually called by #processCommands and processes one command.
     * @param       cmd     The command to be processed.
     * Possible wizardcommands are:
     * - delete-item
     * - add-item
     * - move-up
     * - move-down
     * - goto-form
     * - cancel
     * - commit
     *
     */
    public void processCommand(WizardCommand cmd) throws WizardException {
        // processes the given command
        switch (cmd.getType()) {
        case WizardCommand.DELETE_ITEM : {
            // delete item!
            // The command parameters is the did of the node to delete.
            // note that a fid parameter is expected in the command syntax but ignored
            String did = cmd.getDid();
            Node datanode = Utils.selectSingleNode(data, ".//*[@did='" + did + "']");
            if (datanode != null) {
                // Step one: determine what should eb deleted
                // if an <action name="delete"> exists, and it has an <object> child,
                // the object of the relatiosn should be deleted along with the relation
                // if there is no delete action defined, or object is not a child,
                // only the relation is deleted
                String fid  = cmd.getFid();
                Node itemnode=Utils.selectSingleNode(schema, ".//*[@fid='" + fid + "']");
                Node listnode=itemnode.getParentNode();
                Node objectdef=Utils.selectSingleNode(listnode, "action[@type='delete']/object");

                Node dataobjectnode = datanode;
                if (objectdef!=null) {
                    dataobjectnode = Utils.selectSingleNode(datanode, "object");
                }
                // all child objects of the object to be deleted are added to a repository.
                // these objects are not accessed for editing purposes any more,
                // but do continue to exist in the tree
                // This prevents the included objects from being deleted (deletion of
                // ojects is detected by comparing objects that exist in the original data tree with those
                // in the result data tree)

                Node newrepos = data.createElement("repos");
                NodeList inside_objects = Utils.selectNodeList(dataobjectnode, "object|relation");
                Utils.appendNodeList(inside_objects, newrepos);
                //place repos
                datanode.getParentNode().appendChild(newrepos);
                //remove relation and inside objects
                datanode.getParentNode().removeChild(datanode);
            }
            break;
        }
        case WizardCommand.UPDATE_ITEM : {
            // update an item - replaces all fields of the item with updated values
            // retrieved from MMbase
            // The command parameters is a value indicating the number of the node(s) to update.
            String value = cmd.getValue();
            NodeList nodesToUpdate = Utils.selectNodeList(data, ".//*[@number='" + value + "']");
            NodeList originalNodesToUpdate = Utils.selectNodeList(originalData, ".//*[@number='" + value + "']");
            if ((nodesToUpdate != null) || (originalNodesToUpdate != null)) {
                Node updatedNode=null;
                try {
                    updatedNode = databaseConnector.getDataRaw(value,null);
                } catch (Exception e) {
                    break;
                }
                NodeList updatedFields = Utils.selectNodeList(updatedNode, "./field");

                Map fieldvalues=new HashMap();
                for (int j=0; j<updatedFields.getLength(); j++) {
                    Node fieldnode = updatedFields.item(j);
                    String fieldname=Utils.getAttribute(fieldnode,"name");
                    String fieldvalue=Utils.getText(fieldnode);
                    fieldvalues.put(fieldname,fieldvalue);
                }

                NodeList inside_objects = Utils.selectNodeList(updatedNode, "*");
                for (int i=0; i<nodesToUpdate.getLength(); i++) {
                    Node datanode = nodesToUpdate.item(i);
                    NodeList fieldsToUpdate = Utils.selectNodeList(datanode, "./field");
                    for (int j=0; j<fieldsToUpdate.getLength(); j++) {
                        Node fieldnode = fieldsToUpdate.item(j);
                        String fieldname=Utils.getAttribute(fieldnode,"name");
                        String fieldvalue=(String)fieldvalues.get(fieldname);
                        Utils.storeText(fieldnode,fieldvalue);
                    }
                }

                for (int i=0; i<originalNodesToUpdate.getLength(); i++) {
                    Node datanode = originalNodesToUpdate.item(i);
                    NodeList fieldsToUpdate = Utils.selectNodeList(datanode, "./field");
                    for (int j=0; j<fieldsToUpdate.getLength(); j++) {
                        Node fieldnode = fieldsToUpdate.item(j);
                        String fieldname=Utils.getAttribute(fieldnode,"name");
                        String fieldvalue=(String)fieldvalues.get(fieldname);
                        Utils.storeText(fieldnode,fieldvalue);
                    }
                }
            }
            break;
        }
        case WizardCommand.MOVE_UP: ;
        case WizardCommand.MOVE_DOWN: {
            // This is in fact a SWAP action (swapping the order-by fieldname), not really move up or down.
            // The command parameters are the fid of the list in which the item falls (determines order),
            // and the did's of the nodes that are to be swapped.
            String fid  = cmd.getFid();
            String did      = cmd.getDid();
            String otherdid = cmd.getParameter(2);

            // Step one: get the fieldname to swap
            // this fieldname is determined by checking the 'orderby' attribute in a list
            // If there is no orderby attribute, you can't swap (there is no order defined),
            // so nothing happens.
            Node parentnode=Utils.selectSingleNode(schema, ".//*[@fid='" + fid + "']");
            String orderby=Utils.getAttribute(parentnode.getParentNode(),"orderby");

            // step 2: select the nodes and their fieldfs (provide dthey have them)
            // and swap the values.
            // when the list is sorted again the order of the nodes will be changed
            if (orderby != null) {
                // if orderby is only a field name, create an xpath
                if (orderby.indexOf('@')==-1) {
                    orderby="object/field[@name='"+orderby+"']";
                }

                log.debug("swap "+did+" and "+otherdid+" on "+orderby);
                Node datanode = Utils.selectSingleNode(data, ".//*[@did='" + did + "']/"+orderby);
                if (datanode != null) {
                    // find other datanode
                    Node othernode = Utils.selectSingleNode(data, ".//*[@did='" + otherdid + "']/"+orderby);
                    // now we gotta swap the value of them nodes.. (must be strings).
                    if (othernode !=null) {
                        String datavalue=Utils.getText(datanode);
                        String othervalue=Utils.getText(othernode);
                        Utils.storeText(othernode,datavalue);
                        Utils.storeText(datanode,othervalue);
                    }
                }
            }
            break;
        }
        case WizardCommand.START_WIZARD: {
            // this involves a redirect and is handled by the jsp pages
            startWizard=true;
            startWizardCmd=cmd;
            break;
        }
        case WizardCommand.GOTO_FORM: {
            // The command parameters is the did of the form to jump to.
            // note that a fid parameter is expected in the command syntax but ignored
            currentFormId = cmd.getDid();
            break;
        }
        case WizardCommand.ADD_ITEM : {
            // The command parameters are the fid of the list in which the item need be added,
            // the did of the object under which it should be added (the parent node),
            // and a second id, indicating the object id to add.
            // The second id can be passed either as a paremeter (the 'otherdid' parameter), in
            // which case it involves a newly created item, OR as a value, in which case it is an
            // enumerated list of did's, the result of a search.
            //
            String fid = cmd.getFid();
            String did = cmd.getDid();
            String value = cmd.getValue();

            if (log.isDebugEnabled()) log.debug("Adding item fid: " + fid + " did: " + did + " value: "  + value);
            if (value != null && !value.equals("")){
                log.debug("no value");
                int createorder=1;
                StringTokenizer ids = new StringTokenizer(value,"|");
                while (ids.hasMoreElements()){
                    Node newObject = addListItem(fid, did, ids.nextToken(),false,createorder);
                    createorder++;
                }
            } else {
                String otherdid = cmd.getParameter(2);
                if (otherdid.equals("")) otherdid=null;
                Node newObject = addListItem(fid, did, otherdid, true,1);
            }
            break;
        }
        case WizardCommand.CANCEL : {
            // This command takes no parameters.
            mayBeClosed = true;
            break;
        }
        case WizardCommand.COMMIT : {
            log.service("Committing wizard");
            // This command takes no parameters.
            try {
                if (log.isDebugEnabled()) {
                    log.debug("orig: " +     Utils.stringFormatted(originalData));
                    log.debug("new orig: " + Utils.stringFormatted(data));
                }
                Element results = databaseConnector.put(originalData, data, binaries);
                NodeList errors = Utils.selectNodeList(results,".//error");
                if (errors.getLength() > 0){
                    String errorMessage = "Errors received from MMBase :";
                    for (int i=0; i<errors.getLength(); i++){
                        errorMessage = errorMessage + "\n" + Utils.getText(errors.item(i));
                    }
                    throw new WizardException(errorMessage);
                }
                // find the (new) objectNumber and store it. Just take the first one found.
                String newnumber=Utils.selectSingleNodeText(results,".//object/@number", null);
                if (newnumber != null) objectNumber=newnumber;
                committed = true;
                mayBeClosed = true;
            } catch (WizardException e) {
                log.error("could not send PUT command!. Wizardname:" + wizardName + "Exception occured: " + e.getMessage());
                throw e;
            }
            break;
        }
        }
    }

    /**
     * This method adds a listitem. It is used by the #processCommand method to add new items to a list. (Usually when the
     * add-item command is fired.)
     * Note: this method can only add new relations and their destinations!.
     * For creating new objects, use WizardDatabaseConnector.createObject.
     *
     * @param listId  the id of the proper list definition node, the list that issued the add command
     * @param dataId  The did (dataid) of the anchor (parent) where the new node should be created
     * @param destinationId   The new destination
     * @param createorder ordernr under which this item is added ()i.e. when adding more than one item to a
     *                    list using one add-item command). The first ordernr in a list is 1
     */
    private Node addListItem(String listId, String dataId, String destinationId, boolean isCreate, int createorder) throws WizardException{

        log.debug("Adding list item");
        // Determine which list issued the add-item command, so we can get the create code from there.
        Node listnode = Utils.selectSingleNode(schema, ".//list[@fid='" + listId + "']");
        Node objectdef=null;
        // Get the 'item' from this list, with displaymode='add'
        // Get (and create a copy of) the object-definition from the action node within that item.
        // action=add is for search command
        if (!isCreate) {
            objectdef = Utils.selectSingleNode(listnode, "action[@type='add']/relation");
        }
        // action=create is for create command
        // (this should be an 'else', but is supported for 'search' for old xsls)
        if (objectdef==null)  {
            objectdef = Utils.selectSingleNode(listnode, "action[@type='create']/relation");
        }
        // deprecated code below!, supported for old xsls
        if (objectdef==null) {
            objectdef = Utils.selectSingleNode(listnode, "item[@displaymode='add']/action/relation");
        }

        if (objectdef == null) { // still null?
            throw new WizardException("Could not find action (add or create) to add a item to list with id " + listId);
        }


        objectdef = objectdef.cloneNode(true);

        if (log.isDebugEnabled()) log.debug("Creating object " + objectdef.getNodeName() + " type " + Utils.getAttribute(objectdef,"type"));
        // Put the value from the command in that object-definition.
        if (destinationId != null){
            Utils.setAttribute(objectdef, "destination", destinationId);
        }
        // We have to add the object to the data, so first determine to which parent it belongs.
        Node parent = Utils.selectSingleNode(data, ".//*[@did='" + dataId + "']");
        // Ask the database to create that object.
        Node newObject = databaseConnector.createObject(data, parent, objectdef, variables, createorder);

        // Temporary hack: only images can be shown as summarized.
        if (Utils.getAttribute(newObject,"type").equals("images")){
            Utils.setAttribute(newObject, "displaymode", "summarize");
        } else {
            if (isCreate) {
                Utils.setAttribute(newObject, "displaymode", "add");
            } else {
                Utils.setAttribute(newObject, "displaymode", "search");
            }
        }

        return newObject;
    }

    /**
     * With this method you can store a binary in the wizard.
     *
     * @param       did     This is the dataid what points to in what field the binary should be stored, once commited.
     * @param       data    This is a bytearray with the data to be stored.
     * @param       name    This is the name which will be used to show what file is uploaded.
     * @param       path    The (local) path of the file placed.
     */
    public void setBinary(String did, byte[] data, String name, String path) {
        binaries.put(did, data);
        binaryNames.put(did, name);
        binaryPaths.put(did, path);
    }

    /**
     * This method allows you to retrieve the data of a temporarily stored binary.
     *
     * @param       did     The dataid of the binary you want.
     * @return     the binary data, if found.
     */
    public byte[] getBinary(String did) {
        return (byte[])binaries.get(did);
    }

    /**
     * With this method you can retrieve the binaryname of a placed binary.
     *
     * @param       did     The dataid of the binary you want.
     * @return     The name as set when #setBinary was used.
     */
    public String getBinaryName(String did) {
        return (String) binaryNames.get(did);
    }

    /**
     * With this method you can retrieve the binarypath of a placed binary.
     *
     * @param       did     The dataid of the binary you want.
     * @return     The path as set when #setBinary was used.
     */
    public String getBinaryPath(String did) {
        return (String) binaryPaths.get(did);
    }

    /**
     * This method stores binary data in the data, so that the information can be used by the html.
     * This method is called from the form creating code.
     *
     * @param       fieldnode       the fieldnode where the binary data information should be stored.
     */
    public void addBinaryData(Node fieldnode) {
        // add's information about the possible placed binaries in the fieldnode.
        // assumes this field is an binary-field
        String did = Utils.getAttribute(fieldnode, "did", null);
        if (did!=null) {
            byte[] binary = getBinary(did);
            if (binary!=null) {
                // upload
                Node binarynode = fieldnode.getOwnerDocument().createElement("upload");
                Utils.setAttribute(binarynode, "uploaded", "true");
                Utils.setAttribute(binarynode, "size", binary.length+"");
                Utils.setAttribute(binarynode, "name", getBinaryName(did));
                String path=getBinaryPath(did);
                Utils.createAndAppendNode(binarynode, "path", path);
                fieldnode.appendChild(binarynode);
            }
        }
    }

    /**
     * This method is used to merge MMBase specific constraints with the values placed in the Wizard definition.
     * For now, it checks requiredness and datatypes.
     *
     * It connects to the Dove, gets the constraints (from cache or not) and merges the values.
     *
     * @param       fielddef        the fielddefinition as placed in the wizardschema (==definition)
     * @param       fieldnode       The fieldnode points to the datanode. (This is needed to find out what datatype this field is about).
     */
    public void mergeConstraints(Node fielddef, Node fieldnode) {
        // load all constraints + merge them with the settings in the schema definition
        //
        String objecttype = Utils.getAttribute(fieldnode.getParentNode(), "type", null);
        String fieldname =  Utils.getAttribute(fieldnode, "name", null);

        if (objecttype==null || fieldname==null) {
            log.debug("wizard.mergeConstraints: objecttype or fieldname could not be retrieved for this field. Field:");
            log.debug(Utils.getXML(fieldnode));
            return;
        }

        Node con = getConstraints(objecttype, fieldname);
        if (con==null) return; // no constraints found. so forget it.

        // merge the constraints from mmbase with the constraints placed in the wizard.

        // first, only 'guitype' and 'required' values.
        String xmlSchemaType=null;
        String guitype = Utils.selectSingleNodeText(con, "guitype", "string/line");
        int pos=guitype.indexOf("/");
        if (pos!=-1) {
            xmlSchemaType=guitype.substring(0,pos);
            guitype=guitype.substring(pos+1);
        }
        String required = Utils.selectSingleNodeText(con, "required", "false");
        String guiname = Utils.selectSingleNodeText(con, "guiname", "");
        String maxlength = Utils.selectSingleNodeText(con, "maxlength", "-1");

        // dttype?
        String ftype = Utils.getAttribute(fielddef, "ftype", null);
        String dttype = Utils.getAttribute(fielddef, "dttype", null);
        Node prompt = Utils.selectSingleNode(fielddef, "prompt");
        if (dttype==null) {
            // import xmlSchemaType (dttype)
            // note :
            // Dove currently returns the following XML Schema base types (and their possible constraints):
            // - string (minLength, maxLength)
            // - float
            // - double
            // - date
            // - time
            //
            // Dove returns the following XML Schema derived types:
            // - int
            // - long
            //
            // Dove returns the following Non-XML Schema conformant types:
            // - binary (minLength, maxLength)
            // - datetime
            //
            // The 'binary' type can be defined as :
            //  <xsd:simpleType name="binary">
            //      <xsd:restriction base='anyURI' />
            //  </xsd:simpleType>
            //
            // The 'datetime' type can be defined as :
            //  <xsd:simpleType name="datetime">
            //      <xsd:restriction base='dateTime' />
            //  </xsd:simpleType>
            //
            // Finally, Dove may send other, non-standard, types, depending on the formation of guitype in the builder xml.
            //
            dttype = xmlSchemaType;
        }

        if (ftype==null) {
            // import guitype or ftype
            // this is a qualifier, not a real type
            //
            ftype=guitype;
        }

        // backward compatibility.
        // switch old 'upload' to 'binary'
        // The old format used the following convention:
        //  ftype="upload" + dttype="image"  ->  upload an image
        //  ftype="upload" + dttype="upload"  -> upload a file
        //  ftype="image"  -> display an image
        // The new format usesd 'binary' a s a dftytype,a nd 'image' or 'file' as a ftype,
        // as follows:
        //  ftype="image" + dttype="binary"  ->  upload an image
        //  ftype="file" + dttype="binary"  -> upload a file
        //  ftype="image" + dttype="data" -> display an image
        //  ftype="file" + dttype="data" -> display a link to a file
        // code below changes old format wizards to the new format.
        if ("upload".equals(ftype)) {
            if ("image".equals(dttype)) {
                ftype="image";
                dttype="binary";
            } else {
                ftype="file";
                dttype="binary";
            }
        } else if ("image".equals(ftype)) {
            // check if dttype is binary, else set to data
            if (!"binary".equals(dttype)) dttype="data";
        }

        // in the old format, ftype was date, while dttype was date,datetime, or time
        // In the new format, this is reversed (dttype contains the base datatype,
        // ftype the format in which to enter it)
        if (dttype.equals("date") || dttype.equals("time")) {
            ftype=dttype;
            dttype="datetime";
        }

        // in the old format, 'html' could also be assigned to dttype
        // in the new format this is an ftype (the dttype is string)
        if (dttype.equals("html")){
            ftype="html";
            dttype="string";
        }

        // add guiname as prompt
        if (prompt==null) {
            Utils.createAndAppendNode(fielddef, "prompt", guiname);
        }

        // process requiredness
        //
        String dtrequired = Utils.getAttribute(fielddef, "dtrequired", null);
        if (required.equals("true")) {
            // should be required (according to MMBase)
            dtrequired="true";
        }

        // fix for old format type 'wizard'
        if (ftype.equals("wizard")) {
            ftype="startwizard";
        }

        // store new attributes in fielddef
        Utils.setAttribute(fielddef, "ftype", ftype);
        Utils.setAttribute(fielddef, "dttype", dttype);
        if (dtrequired!=null) {
            Utils.setAttribute(fielddef, "dtrequired", dtrequired);
        }

        // process min/maxlength for strings
        if (dttype.equals("string") || dttype.equals("html")) {
            String dtminlength = Utils.getAttribute(fielddef, "dtminlength", null);
            if (dtminlength==null) {
                // manually set minlength if required is true
                if ("true".equals(dtrequired)) {
                    Utils.setAttribute(fielddef, "dtminlength", "1");
                }
            }
            String dtmaxlength = Utils.getAttribute(fielddef, "dtmaxlength", null);
            if (dtmaxlength==null) {
                int maxlen=-1;
                try {
                    maxlen=Integer.parseInt(maxlength);
                } catch(NumberFormatException e) {}
                // manually set maxlength if given
                // ignore sizes smaller than 1 and larger than 255
                if ((maxlen>0) && (maxlen<256)) {
                    Utils.setAttribute(fielddef, "dtmaxlength", ""+maxlen);
                }
            }

        }

    }

    /**
     * This method gets the MMBase constraints.
     *
     * @param       objecttype      The name of the object, eg. images, jumpers, urls, news
     */
    public Node getConstraints(String objecttype) {
        return getConstraints(objecttype, null);
    }

    /**
     * This method gets the MMBase constraints. It also handles the internal constraints cache.
     *
     * @param       objecttype      The name of the object, eg. images, jumpers, urls, news
     * @param       fieldname       The name of the field, eg. title, body, start
     */
    public Node getConstraints(String objecttype, String fieldname) {
        // check if constraints are in repository, if so, return thatone,
        // otherwise, retrieve+store+and return the contraints received from the Dove.
        Node con = Utils.selectSingleNode(constraints, "/*/getconstraints[@type='"+objecttype+"']");

        if (con==null) {
            // objecttype not in repository. Load from MMBase.
            try {
                con = databaseConnector.getConstraints(objecttype);
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
                return null;
            }

            if (con==null) {
                // something is wrong.
                log.debug("wizard.getConstraints: Could not retrieve MMBase constraints for objecttype:"+objecttype);
                return null;
            }

            // store in repository.
            con = constraints.importNode(con.cloneNode(true), true);
            constraints.getDocumentElement().appendChild(con);
        }

        // no fieldname supplied? return total info node..
        if (fieldname==null) return con;

        // find field declaration
        Node fieldcon = Utils.selectSingleNode(con, "fields/field[@name='"+fieldname+"']");
        return fieldcon;
    }

    class OrderByComparator implements Comparator {

        boolean compareByNumber=false;

        OrderByComparator(String ordertype) {
            compareByNumber=ordertype.equals("number");
        }

        public int compare(Object o1, Object o2) {
            Element n1=(Element)o1;
            Element n2=(Element)o2;
            // Determine the orderby values and compare
            // store it??
            String order1 = n1.getAttribute("orderby");
            String order2= n2.getAttribute("orderby");
            if (compareByNumber) {
                    try {
                    double orderdbl1=Double.parseDouble(order1);
                    double orderdbl2=Double.parseDouble(order2);
                    if (orderdbl1==orderdbl2) {
                        return 0;
                    } else if (orderdbl1>orderdbl2) {
                        return 1;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    log.error("Invalid field values ("+order1+"/"+order2+"):"+e);
                    return 0;
                }
            } else {
                return order1.compareToIgnoreCase(order2);
            }
        }
    }
}
