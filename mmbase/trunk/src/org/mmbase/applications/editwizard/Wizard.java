package org.mmbase.applications.editwizard;

import org.mmbase.bridge.Cloud;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import org.mmbase.applications.dove.*;
import org.mmbase.util.logging.*;

/**
 * Title:        EditWizard
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Q42
 * @author Kars Veling
 * @version 1.0
 */
public class Wizard {
    private static Logger log = Logging.getLoggerInstance(Wizard.class.getName());

    public final static short ERROR   = 1;
    public final static short WARNING = 2;
    // Some of these variables are placed public, for debugging reasons.
    public Document preform;

    // turn debugging variable on if you want all kind of debugdata sent to the Logs.
    public boolean debugging = false;

    // the username and password are stored here.
    public String user="";
    public String pass="";
    public boolean loggedIn = false;

    // basepath where all data files reside. Will be set from the jsp files.
    public String path="";

    // schema / session data
    public String name;
    public String objectnumber;
    public String lastCommand;

    // the wizard (file) name. Eg.: samples/jumpers will choose the file $path/samples/jumpers.xml
    public String wizardName;
    public String wizardDataid;

    // stores the current formid
    public String currentformid;

    // expanded filename of the wizard
    public String wizardSchemaFilename;

    // filename of the stylesheet which should be used to make the html form.
    public String wizardStylesheetFilename;

    // public xmldom's: the schema, the data and the originaldata is stored.
    public Document schema;
    public Document data;
    public Document originaldata;

    // not yet committed uploads are stored in there hashmaps
    public HashMap uploads;
    public HashMap uploadnames;
    public HashMap uploadpaths;

    // in the wizards, variables can be used. Values of the variables are stored here.
    public Hashtable variables;

    // the constraints received from mmbase are stored + cached in this xmldom
    public Document constraints;

    // Seconds.
    public long listQueryTimeOut = 60 * 60;

    // the database connector handles communition with mmbase. the instance is stored here.
    public WizardDatabaseConnector dbconn;

    // this boolean tells the jsp that the wizard may be closed, as far as he is concerned.
    public boolean mayBeClosed = false;

    // this list stores all errors and warnings occured
    public Vector errors;



    /**
     * Constructor. Setup initial variables. No connection to mmbase is made yet.
     * Make sure a valid path is supplied.
     * Use initialize() to really startup the wizard and start communicating with mmbase
     *
     * @param apath       the path should point to the data directory of the editwizard. From that dir the wizard schema's and the xsl's will be loaded
     */
    public Wizard(String apath) {
        path = apath;
        uploads = new HashMap();
        uploadnames = new HashMap();
        uploadpaths = new HashMap();
        variables = new Hashtable();
        errors = new Vector();
        constraints = Utils.parseXML("<constraints/>");
    }

    /**
     * This method initializes the Wizard instance. Always use one of these methods to start the wizard.
     *
     * @param       wizardname      the wizardname which the wizard will use all the time. Eg.: samples/jumpers
     * @param       dataid          the dataid (objectnumber) of the main object what is used by the editwizard
     */
    public void initialize(String wizardname, String dataid) throws WizardException, SecurityException{
        initialize(wizardname,dataid,null,null);
    }

    /**
        - Loads the wizard schema
        - Creates a connection to the database.
        - Creates a work document (to contain all data)
        - Loads data (new or existing)
    */
    public void initialize(String wizardname, String dataid, String user, String pass) throws WizardException, SecurityException {
        initialize(wizardname,dataid,user,pass,null);
    }

    /**
        - Loads the wizard schema
        - Creates a connection to the database.
        - Creates a work document (to contain all data)
        - Loads data (new or existing)
    */
    public void initialize(String wizardname, String dataid, Cloud cloud) throws WizardException, SecurityException {
        initialize(wizardname,dataid,null,null,cloud);
    }

    /**
        - Loads the wizard schema
        - Creates a connection to the database.
        - Creates a work document (to contain all data)
        - Loads data (new or existing)
    */
    public void initialize(String wizardname, String dataid, String user, String pass, Cloud cloud) throws WizardException, SecurityException
    {
        wizardName = wizardname;
        wizardDataid = dataid;
        wizardSchemaFilename = path + "/" + wizardName + ".xml";
        wizardStylesheetFilename = path + "/xsl/wizard.xsl";

        // store variables so that these can be used in the wizard schema
        variables.put("wizardname",wizardname);
        if (dataid!=null) variables.put("objectnumber",dataid);

        // load wizard schema
        loadSchema();

        // initialize database connector
        dbconn = new WizardDatabaseConnector();
        dbconn.debugging = this.debugging;
        dbconn.init(path);
        if (cloud!=null) {
            // add username + password to variables
            dbconn.setUserInfo(cloud);
            variables.put("username", cloud.getUser().getIdentifier());
        } else if (user != null){
            dbconn.setUserInfo(user,pass,null,null);
            // add username to variables
            variables.put("username", user);
        }

        // setup original data
        originaldata = Utils.EmptyDocument();

        // If the given dataid=new, we have to create a new object first, given
        // by the object definition in the schema.
        // If dataid equals null, we don't need to do anything. Wizard will not be used to show or save data; just to load schema information.
        if (dataid!=null) {
            if (dataid.equals("new")){
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
                Node newobject = dbconn.createObject(data,parent, objectdef, variables);
                parent.appendChild(newobject);
                dbconn.tagDataNodes(data);
                dataid = Utils.getAttribute(newobject,"number");
                log.debug("Created object " + newobject.getNodeName() + " type " + Utils.getAttribute(newobject,"type") + ", id " + dataid);
            } else {
                // - load data.
                // - tags the datanodes
                try{
                    data = dbconn.load(schema.getDocumentElement(), dataid);
                    if (data==null) {
                        throw new WizardException("The requested object could not be loaded from MMBase. Objectnumber:"+dataid+". Does the object exists and do you have enough rights to load this object.");
                    }
                    // store original data, so that the put routines will know what to save/change/add/delete
                    originaldata.appendChild(originaldata.importNode(data.getDocumentElement().cloneNode(true), true));
                }catch (SecurityException secure){
                    log.warn("Wizard failed to login: " + secure.getMessage());
                    throw secure;
                } catch (WizardException we) {
                    throw we;
                }catch (Exception e){
                    throw new WizardException("Wizard could not be initialized.");
                }
            }
        }

        // initialize a editor session
        if (currentformid == null){currentformid = determineNextForm("first");}
    }

    /**
     * this method processes an incoming request (usually passed on by a jsp code).
     * First, all given values are stored in the current datatree,
     * Second, all given commands are processed sequentially.
     *
     * @param       req     the ServletRequest contains the name-value pairs received through the http connection
     */
    public void processRequest(ServletRequest req) throws WizardException {
        String curform = req.getParameter("curform");
        if (curform != null && !curform.equals("")) currentformid = curform;

        storeValues(req);
        processCommands(req);
    }

    /**
     * This method constructs and writes final form-html to the given out writer.
     *
     * @param       out     The writer where the output (html) should be written to.
     * @param       instancename    in the instancename you can specify what instancename the current instance of a wizard has.
     *                              This instancename is used by the wizard so that it is able to start another wizard in the
     *                              *same* session. The jsp pages and in the html the instancenames are used to keep track of one and another.
    */
    public void writeHtmlForm(Writer out, String instancename) throws WizardException {
        Node datastart = Utils.selectSingleNode(data, "/data/*");

        // Build the preHtml version of the form.
        preform = createPreHtml(schema.getDocumentElement(),currentformid,datastart, instancename);
        Validator.validate(preform, schema);
        Utils.transformNode(preform, this.wizardStylesheetFilename, out);
    }

    /////////////////////////////////////

    /**
     * internal method which is used to store the passed values. this method is called by processRequest.
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
                storeValue(ids[0], ids[1], req.getParameter(name));
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
        Node laststep = Utils.selectSingleNode(schema, "//steps/step[@form-schema='" + currentformid + "']");
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
    public Document createPreHtml(String instancename) throws WizardException {
        Node datastart = Utils.selectSingleNode(data, "/data/*");
        return createPreHtml(schema.getDocumentElement(), "1", datastart, instancename);
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

    public Document createPreHtml(Node wizardSchema, String formid, Node data, String instancename) throws WizardException {
        // intialize preHTML wizard
        Document preHtml = Utils.parseXML("<wizard instance=\""+instancename+"\" />");
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
                    queryresult = dbconn.getList(query);
                    queryresult = Utils.selectSingleNode(queryresult,"/getlist/query");
                }catch (Exception e){
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
            String selectedValue = Utils.selectSingleNode(optionlist, "../value/text()").getNodeValue();
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
        // select all fields on first level
        NodeList fields = Utils.selectNodeList(formdef, "field|list|command");

        // Add the title, description.
        NodeList props = Utils.selectNodeList(formdef, "title");
        Utils.appendNodeList(props, form);

        // process all possible fields
        // - Parse the fdatapath attribute to obtain the corresponding data fields.
        // - Create a form field for each found data field.
        for (int i=0; i<fields.getLength(); i++) {
            Node field = fields.item(i);
            String xpath = Utils.getAttribute(field, "fdatapath", null);

            if (xpath==null) {
                throw new WizardException("A field tag should contain one of the following attributes: fdatapath or field");
            }
            // xpath is found. Let's see howmany 'hits' we have
            NodeList fieldinstances = Utils.selectNodeList(data, xpath);
            if (fieldinstances==null) {
                throw new WizardException("The xpath: "+xpath+" is not valid. Note: this xpath maybe generated from a &lt;field name='fieldname'&gt; tag. Make sure you use simple valid fieldnames use valid xpath syntax.");
            }
            Node fielddatanode = null;
            if (fieldinstances.getLength()>0) fielddatanode = fieldinstances.item(0);

            // let's see what we should do here
            String nodeName = field.getNodeName();

            // A normal field.
            if (nodeName.equals("field")) {
                if (fielddatanode!=null) {
                    // create normal formfield.
                    mergeConstraints(field, fielddatanode);
                    createFormField(form, field, fielddatanode);
                } else {
                    // here we could check if this maybe should be placed anyway, even now there is no
                    // datanode. For now, nothing will do.
                }
            }

            // A list "field". Needs special processing.
            if (nodeName.equals("list")) {
                createFormList(form, field, fieldinstances, data);
            }
        }
    }

    // ----------------- private methods

    // Loading

    /**
     * This method loads the schema using the properties of the wizard. It loads the wizard using #wizardSchemaFilename,
     * resolves the includes, and 'tags' all datanodes. (Places temp. ids in the schema).
     *
     * No params needed.
     */
    private void loadSchema() throws WizardException {
        schema = Utils.loadXMLFile(wizardSchemaFilename);
        if (schema==null) {
            // could not load schema!
            throw new WizardException("Could not load and parse schema xml file. Filename:"+wizardSchemaFilename);
        }

        resolveIncludes(schema.getDocumentElement(), path);

        resolveShortcuts(schema.getDocumentElement(), true);

        log.debug("Schema loaded (and resolved):"+wizardSchemaFilename);

        // tag schema nodes
        NodeList fields = Utils.selectNodeList(schema, "//field|//list|//item");
        Utils.tagNodeList(fields, "fid", "f", 1);
    }

    /**
     * This method resolves the includes (and extends) in a wizard. It searches for include="" attributes, and searches for extends="" attributes.
     * Include means: the file is loaded (uses the path and assumes it references from the basepath param, and the referenced node
     * is placed 'over' the existing node. Attributes are copied also. Any content in the original node is removed.
     * Extends means: same as include, but now, the original content is not thrown away, and the nodes are placed after the included node.
     *
     * This method is a recursive one. Included files are also scanned again for includes.
     *
     * @param       node    The node from where to start searching for include and extends attributes.
     * @param       basepath allows you to give a 'basepath' from where the includes should be found.
     *
     */
    private void resolveIncludes(Node node, String basepath) throws WizardException {
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
                    url = basepath + "/" + url;

                    // Load the external file.
                    Document externalDocument = Utils.loadXMLFile(url);
                    if (externalDocument==null) {
                        throw new WizardException("Could not load and parse included file. Filename:"+url);
                    }

                    // Add a copy of the external part to our schema here, to replace the
                    // referer itself.
                    Node externalPart = null;
                    if (hash == -1){
                        // Load the entire file.
                        externalPart = externalDocument.getDocumentElement();
                    }else if (externalId.startsWith("xpointer(")){
                        // Load only part of the file, using an xpointer.
                        String xpath = externalId.substring(9,externalId.length()-1);
                        externalPart = Utils.selectSingleNode(externalDocument, xpath);
                    }else{
                        // Load only the node with the given id.
                        externalPart = Utils.selectSingleNode(externalDocument, "//node()[@id='" + externalId + "']");
                    }

                        // recurse!
                        resolveIncludes(externalPart, basepath);

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
                String parentname = singlenode.getParentNode().getNodeName();
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
     *      for each given datanode.
    */
    private void createFormList(Node form, Node fieldlist, NodeList datalist, Node parentdatanode) throws WizardException {
        // copy all attributes from fielddefinition to new pre-html field definition
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

        // we need to order datalist using orderby!

        SortedMap sorteddatalist = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int dataindex=0; dataindex<datalist.getLength(); dataindex++) {
            String orderbyvalue = "";
            Node datacontext = datalist.item(dataindex);
            if (orderby!=null) {
                try {
                    // Determine the orderby value and store it.
                    orderbyvalue = Utils.selectSingleNodeText(datacontext, orderby, "");
                } catch (RuntimeException e) {}
                Utils.setAttribute(datacontext, "orderby", orderbyvalue);
            }
            sorteddatalist.put(orderbyvalue,datacontext);
        }

        // Create an item node for each datanode in the datalist.

        Map.Entry[] entrylist = new Map.Entry[sorteddatalist.size()];
        sorteddatalist.entrySet().toArray(entrylist);

        for (int dataindex=0; dataindex<entrylist.length; dataindex++) {

            Map.Entry en=entrylist[dataindex];
            Node datacontext = (Node)en.getValue();
            if (orderby!=null) {
                String orderbyvalue=(String)en.getKey();
                Utils.setAttribute(datacontext, "orderby", orderbyvalue);
            }

            // Determine the display mode of the current datanode.
            String displaymode = Utils.getAttribute(datacontext,"displaymode",defaultdisplaymode);

            // Create the form item which has the same display mode as this datanode.
            Node item = Utils.selectSingleNode(fieldlist, "item[@displaymode='" + displaymode + "']");
            if (item == null){item = Utils.selectSingleNode(fieldlist, "item");}
            Node newitem = item.cloneNode(false);
            newitem = form.getOwnerDocument().importNode(newitem, false);
            newlist.appendChild(newitem);
            // Copy all attributes from data to new pre-html field def (mainly needed for the did).
            Utils.copyAllAttributes(datacontext, newitem);
            Utils.copyAllAttributes(item, newitem);
            // Add the title, description.
            NodeList itemprops = Utils.selectNodeList(item, "title|description");
            Utils.appendNodeList(itemprops, newitem);

            // and now, do the recursive tric! All our fields inside need to be processed.
            createPreHtmlForm(newitem, item, datacontext);

            // finally, see if we need to place some commands here
            if (nrOfItems > minoccurs && hiddenCommands.indexOf("|delete-item|") == -1){
               addSingleCommand(newitem,"delete-item", datacontext);
            }
            if (dataindex > 0 && hiddenCommands.indexOf("|move-up|") == -1){
               addSingleCommand(newitem,"move-up", datacontext,
                                                   (Node)entrylist[dataindex-1].getValue());
            }
            if (dataindex < nrOfItems-1 && hiddenCommands.indexOf("|move-down|") == -1){
               addSingleCommand(newitem,"move-down", datacontext,
                                                   (Node)entrylist[dataindex+1].getValue());
            }
        }

        // can we place an add-button?
        if (hiddenCommands.indexOf("|add-item|") == -1 && (maxoccurs == -1 || maxoccurs > nrOfItems) && (Utils.selectSingleNode(fieldlist, "action[@type='create']")!=null)) {
            String defaultpath=".";
            if (fieldlist.getParentNode().getNodeName().equals("item")) {
                // this is a list in a list.
                defaultpath="object";
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
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method generates a form field node in the pre-html.
     *
     * @param       form    the pre-html form node
     * @param       field   the form definition field node
     * @param       datanode        the current context data node
     */
    private void createFormField(Node form, Node field, Node datanode) throws WizardException {
        // copy all attributes from fielddefinition to new pre-html field definition

        Node newfield = form.getOwnerDocument().createElement("field");

        Utils.copyAllAttributes(field, newfield);
        // place newfield in pre-html form
        form.appendChild(newfield);

        Vector exceptattrs = new Vector();
        exceptattrs.add("fid");
        // copy all attributes from data to new pre-html field def
        if (datanode.getNodeType() != Node.ATTRIBUTE_NODE){
            Utils.copyAllAttributes(datanode, newfield, exceptattrs);
        }

        String ftype = Utils.getAttribute(newfield, "ftype");
        // place html form field name (so that we always know about which datanode and fieldnode we are talking)
        String htmlfieldname = calculateFormName(newfield);
        Utils.setAttribute(newfield, "fieldname", htmlfieldname);

        // place objectnumber as attribute number, if not already was placed there by the copyAllAttributes method.
        if (Utils.getAttribute(datanode, "number",null)==null) {
            Utils.setAttribute(newfield, "number", Utils.getAttribute(datanode.getParentNode(),"number"));
        }

        // resolve special attributes
        if (ftype.equals("startwizard")) {
            String objectnumber = Utils.getAttribute(newfield,"objectnumber","new");
            objectnumber = Utils.transformAttribute(datanode, objectnumber);
            Utils.setAttribute(newfield, "objectnumber", objectnumber);
        }

        // upload type needs special processing
        if (ftype.equals("upload")) {
                addUploadData(newfield);
        }

        NodeList list = Utils.selectNodeList(field, "optionlist|prompt|description|action");
        Utils.appendNodeList(list, newfield);
        // place value
        // by default, theValue is the text of the node.
        String theValue = "";
        try {
            if (datanode.getNodeType() == Node.ATTRIBUTE_NODE){
                theValue = datanode.getNodeValue();
            } else {
                theValue = datanode.getFirstChild().getNodeValue();
            }
        } catch (RuntimeException e) {}
        // if this is a relation, we want the value of the dnumber field
        if (ftype.equals("relation")) {
            theValue = Utils.getAttribute(newfield, "destination");
        }

        if (theValue==null) theValue="";
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
     * Below are specific util-like functions for data manipulation.
     * specific stuff which is used to make sure communication between client and server is consistent
     */

    // dataformat functions.
    //
    //

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
        Puts the given value in the right datanode (given by did), depending on the type
        of the form field. If the operation fails... System.out is notified.

        - text,line: the value is stored as text in the datanode.
        - relation: the value is assumed to be the destination number (dnumber) of the relation.

        @param  did     The data id where the value should be stored
        @param  fid     The wizarddefinition field id what applies to this data
        @param  value   The (String) value what should be stored in the data.
    */
    private void storeValue(String did, String fid, String value) {
        if (debugging) Utils.printXML(Utils.selectSingleNode(schema, ".//*[@fid='" + fid + "']"));
        String ftype = Utils.selectSingleNode(schema, ".//*[@fid='" + fid + "']/@ftype").getNodeValue();
        Node datanode = Utils.selectSingleNode(data, ".//*[@did='" + did + "']");
        boolean ok = false;

        if (datanode == null){
            // Nothing.
        }else if (ftype.equals("text") || ftype.equals("line") || ftype.equals("date") || ftype.equals("html")) {
            Utils.storeText(datanode, value);
            ok = true;
        }else if (ftype.equals("upload")) {
            if (getUpload(did)!=null) {
            Utils.setAttribute(datanode, "href", did);
                Utils.storeText(datanode,"YES");
            }
            ok = true;
        }else if (ftype.equals("enum")) {
            Utils.storeText(datanode, value);
            ok = true;
        }


        if (!ok) {
            log.warn("Unable to store value for field with ftype " + ftype + ". fid=" + fid + ", did=" + did + ", value=" + value +", wizard:"+wizardName);
        }
    }


    /**
     * This method processes the commands sent over http.
     *
     * @param       req     The ServletRequest where the commands (name/value pairs) reside.
     */
    public void processCommands(ServletRequest req) throws WizardException {
        mayBeClosed = false;

        boolean found=false;
        String commandname="";

        Vector errors = new Vector();
        Enumeration list = req.getParameterNames();
        while (list.hasMoreElements()) {
            commandname = (String)list.nextElement();
            if (commandname.indexOf("cmd/")==0 && !commandname.endsWith(".y")) {
                // this is a command.
                String commandvalue = req.getParameter(commandname);
                try{
                    WizardCommand wc = new WizardCommand(commandname, commandvalue);
                    processCommand(wc);
                } catch (WizardException we) {
                    throw we;
                }catch (RuntimeException e){
                    // Have to accumulate the exceptions and report them at the end.
                    java.io.StringWriter w= new StringWriter();
                    e.printStackTrace(new PrintWriter(w));
                    errors.add(new WizardException("* Could not process command:"+commandname + "="+commandvalue+"\n"+w));
                }
            }
        }

        if (errors.size() > 0){
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
        // store this command as the last given command. Jsp's can use the field to see what happened over here.
        lastCommand=cmd.type;
        // processes the given command
        if (cmd.type.equals("delete-item")) {
            // delete item!
            Node datanode = Utils.selectSingleNode(data, ".//*[@did='" + cmd.did + "']");
            if (datanode != null) {
                // Ok. delete.

                // Let op: alle objecten die hierbinnen staan, in een repository geplaatst. Dus,
                // als een relation wordt verwijderd naar een object, worden automatisch de wijzigingen die evt. in
                // dat object gemaakt waren, ongedaan gemaakt.
                Node newrepos = data.createElement("repos");

                NodeList inside_objects = Utils.selectNodeList(datanode, "*");
                Utils.appendNodeList(inside_objects, newrepos);

                //place repos
                datanode.getParentNode().appendChild(newrepos);

                //remove relation and inside objects
                datanode.getParentNode().removeChild(datanode);
            }
        }
        if (cmd.type.equals("move-up") || cmd.type.equals("move-down")) {
            // This is in fact a SWAP action, not really move up or down.
            // The command contains the did's of the nodes that are to be swapped.

            // get fieldname to swap (hack: use title for test)
            Node parentnode=Utils.selectSingleNode(schema, ".//*[@fid='" + cmd.fid + "']");
            String orderby=Utils.getAttribute(parentnode.getParentNode(),"orderby");
            log.info("swap "+cmd.did+" and "+cmd.otherdid+" on "+orderby);

            if (orderby != null) {
                Node datanode = Utils.selectSingleNode(data, ".//*[@did='" + cmd.did + "']/"+orderby);
                if (datanode != null) {
                    // find other datanode
                    Node othernode = Utils.selectSingleNode(data, ".//*[@did='" + cmd.otherdid + "']/"+orderby);

                    // now we gotta swap the value of them nodes.. (must be strings).

                    String datavalue=Utils.getText(datanode);
                    String othervalue=Utils.getText(othernode);
                    Utils.storeText(othernode,datavalue);
                    Utils.storeText(datanode,othervalue);
                }

/*


                // get fieldname to swap (hack: use title for test)
                Node parentnode=Utils.selectSingleNode(schema, ".//*[@fid='" + cmd.fid + "']");
                String orderby=Utils.getAttribute(parentnode.getParentNode(),"orderby");
                log.info("swap "+cmd.did+" and "+cmd.otherdid+" on "+orderby);

                // Is het nodig om een temp node te maken?
                Node tmpnode = datanode.cloneNode(true);

                othernode.getParentNode().insertBefore(tmpnode, othernode);
                datanode.getParentNode().insertBefore(othernode, datanode);
                datanode.getParentNode().removeChild(datanode);
*/
            }
        }
        if (cmd.type.equals("goto-form")) {
            currentformid = cmd.did;
        }
        if (cmd.type.equals("add-item")) {
            if (cmd.value != null && !cmd.value.equals("")){
                StringTokenizer ids = new StringTokenizer(cmd.value,"|");
                while (ids.hasMoreElements()){
                    Node newObject = addListItem(cmd.fid, cmd.did, ids.nextToken());
                    Utils.setAttribute(newObject, "displaymode", "search");
                    // Temporary hack: only images can be shown as summarized.
                    if (Utils.getAttribute(newObject,"type").equals("images")){
                        Utils.setAttribute(newObject, "displaymode", "summarize");
                    }
                }
            }else{
//log.warn("add-item:"+cmd.otherdid+":"+cmd.fid+":"+cmd.did);
                String destinationId = null;
                if (cmd.otherdid != null && !cmd.otherdid.equals("")){
                    destinationId = cmd.otherdid;
                }
                Node newObject = addListItem(cmd.fid, cmd.did, destinationId);
                Utils.setAttribute(newObject, "displaymode", "add");
                // Temporary hack: only images can be shown as summarized.
                if (Utils.getAttribute(newObject,"type").equals("images")){
                    Utils.setAttribute(newObject, "displaymode", "summarize");
                }
            }
        }
        if (cmd.type.equals("cancel")) {
            mayBeClosed = true;
        }
        if (cmd.type.equals("commit")) {
            try {
                Element results = dbconn.firePutCommand(originaldata, data, uploads);
                NodeList errors = Utils.selectNodeList(results,".//error");
                if (errors.getLength() > 0){
                    String errorMessage = "Errors received from MMBase :";
                    for (int i=0; i<errors.getLength(); i++){
                        errorMessage = errorMessage + "\n" + Utils.getText(errors.item(i));
                    }
                    throw new WizardException(errorMessage);
                }

                // find the (new) objectnumber and store it. Just take the firstone found.
                String newnumber=Utils.selectSingleNodeText(results,".//object/@number",null);
                if (newnumber!=null) objectnumber=newnumber;
                mayBeClosed = true;
            } catch (WizardException e) {
                log.error("could not send PUT command!. Wizardname:"+wizardName+"Exception occured: " + e.getMessage());
                throw e;
            }
        }
    }

    /**
     * This method adds a listitem. It is used by the #processCommand method to add new items to a list. (Usually when the
     * add-item command is fired.)
     * Note: this method can only add new relations and their destinations!. For creating new objects, use WizardDatabaseConnector.createObject.
     *
     * @param       listId  the id of the proper list definition node
     * @param       dataId  The did (dataid) of the anchor (parent) where the new node should be created
     * @param       destinationId   The new destination
     */
    private Node addListItem(String listId, String dataId, String destinationId) throws WizardException{
        /**
            This code assumes that you'll always be creating relations.
                cmd.did - the did of the parent to which a new item is added
            cmd.fid - the fid of the list that issued the add command
            cmd.otherid - the dnumber of the to-be-created relation.
        */
        // Determine which list issued the add-item command, so we can get the create code from there.

        Node listnode = Utils.selectSingleNode(schema, ".//list[@fid='" + listId + "']");
        Node objectdef=null;
        // Get the 'item' from this list, with displaymode='add'
        // Get (and create a copy of) the object-definition from the action node within that item.
        objectdef = Utils.selectSingleNode(listnode, "action[@type='create']/relation");
        if (objectdef==null) objectdef = Utils.selectSingleNode(listnode, "item[@displaymode='add']/action/relation");
        objectdef = objectdef.cloneNode(true);
        log.debug("Creating object " + objectdef.getNodeName() + " type " + Utils.getAttribute(objectdef,"type"));
        // Put the value from the command in that object-definition.
        if (destinationId != null){
            Utils.setAttribute(objectdef, "destination", destinationId);
        }
        // We have to add the object to the data, so first determine to which parent it belongs.
        Node parent = Utils.selectSingleNode(data, ".//*[@did='" + dataId + "']");
        // Ask the database to create that object.
//		log.warn("creating:"+Utils.getXML(objectdef));
        Node newobject = dbconn.createObject(data,parent, objectdef, variables);
//		log.warn("created:"+newobject);
        return newobject;
    }

    /**
     * These methods are used to temporarily store and process uploads
     */

    /**
     * With this method you can store an upload binary in the wizard.
     *
     * @param       did     This is the dataid what points to in what field the binary should be stored, once commited.
     * @param       data    This is a bytearray with the data to be stored.
     * @param       name    This is the name which will be used to show what file is uploaded.
     * @param       path    The (local) path of the file placed.
     */
    public void setUpload(String did, byte[] data, String name, String path) {
        uploads.put(did, data);
        uploadnames.put(did, name);
        uploadpaths.put(did, path);
    }

    /**
     * This method allows you to retrieve the data of a temporarily stored upload-binary.
     *
     * @param       did     The dataid of the binary you want.
     * @return     the binary data, if found.
     */
    public byte[] getUpload(String did) {
        return (byte[])uploads.get(did);
    }
    /**
     * With this method you can retrieve the uploadname of a placed binary.
     *
     * @param       did     The dataid of the binary you want.
     * @return     The name as set when #setUpload was used.
     */
    public String getUploadName(String did) {
        return (String)uploadnames.get(did);
    }

    /**
     * With this method you can retrieve the uploadpath of a placed binary.
     *
     * @param       did     The dataid of the binary you want.
     * @return     The path as set when #setUpload was used.
     */
    public String getUploadPath(String did) {
        return (String)uploadpaths.get(did);
    }
    /**
     * This method stores upload data in the data, so that the information can be used by the html.
     * This method is called from the form creating code.
     *
     * @param       fieldnode       the fieldnode where the upload data information should be stored.
     */
    public void addUploadData(Node fieldnode) {
            // add's information about the possible placed uploads in the fieldnode.
        // assumes this field is an upload-field
        String did = Utils.getAttribute(fieldnode, "did", null);
        if (did!=null) {
            byte[] upload = getUpload(did);
            if (upload!=null) {
                // upload
                Node uploadnode = fieldnode.getOwnerDocument().createElement("upload");
                Utils.setAttribute(uploadnode, "uploaded", "true");
                Utils.setAttribute(uploadnode, "size", upload.length+"");
                Utils.setAttribute(uploadnode, "name", getUploadName(did));
                String path=getUploadPath(did);
                Utils.createAndAppendNode(uploadnode, "path", path);
                fieldnode.appendChild(uploadnode);
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

        // dttype?
        String ftype = Utils.getAttribute(fielddef, "ftype", null);
        String dttype = Utils.getAttribute(fielddef, "dttype", null);
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
            // Dove returns the following Non-XML Schema conformant typs:
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

        if (ftype==null) {
            // no ftype defined? Hmm.. Maybe the constraints can help.
            if (dttype.equals("date") || dttype.equals("datetime") || dttype.equals("time")) {
                // make it a date.
                ftype="date";
            } else {
                // no ftype is given in the wizard schema.
                ftype="line";
            }
        }

        // remove illegal combinations
        if (!ftype.equals("date") && !ftype.equals("data") && dttype.equals("date")) {
            // datatype is date, but no valid ftype is given. We'd better adjust.
            ftype = "date";
        }

        // process requiredness
        //
        String dtrequired = Utils.getAttribute(fieldnode, "dtrequired", null);
        if (required.equals("true")) {
            // should be required (according to MMBase)
            dtrequired="true";
        }

        // store new attributes in fielddef
        Utils.setAttribute(fielddef, "ftype", ftype);
        Utils.setAttribute(fielddef, "dttype", dttype);
        if (dtrequired!=null) Utils.setAttribute(fielddef, "dtrequired", dtrequired);
    }

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
                con = dbconn.getConstraints(objecttype);
            } catch (Exception e) {
                e.printStackTrace();
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

    public void addError(short type, String message) {
        String errtype="ERROR";
        switch (type) {
            case WARNING:
                errtype = "WARNING";
                break;
            case ERROR:
                errtype = "ERROR";
                break;
        }
        errors.add(errtype+":"+message);
    }
}
