/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import org.mmbase.bridge.Cloud;
import java.util.*;
import java.io.*;
import org.mmbase.applications.dove.Dove;
import org.mmbase.util.logging.*;
import org.mmbase.bridge.LocalContext;
import org.w3c.dom.*;


/**
 * This class handles all communition with mmbase. It uses the MMBase-Dove code to do the transactions and get the information
 * needed for rendering the wizard screens.
 * The WizardDatabaseConnector can connect to MMBase and get data, relations, constraints, lists. It can also
 * store changes, create new objects and relations.
 *
 * The connector can be instantiated without the wizard class, but will usually be called from the Wizard class itself.
 *
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @author Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: WizardDatabaseConnector.java,v 1.3 2002-02-25 11:53:58 pierre Exp $
 *
 */
public class WizardDatabaseConnector {
    private static Logger log = Logging.getLoggerInstance(WizardDatabaseConnector.class.getName());

    Vector commandlist;
    int didcounter=1;
    private String username = null;
    private String password = null;
    private String logonmethodname = "name/password";
    private String cloudname = "mmbase";
    private Cloud userCloud = null;

    public final static String RELATIONFIELDS_XPATH = "field[not(@name='dnumber') and not(@name='rnumber') and not(@name='snumber')]";

    /**
     * Constructor: Creates the connector. Call #init also.
     */
    public WizardDatabaseConnector(){
        commandlist = new Vector();
    }

    /**
     * Sets the right cloud for the user info.
     *
     * @param       cloud   The cloud from which the userinfo should be set.
     */
    public void setUserInfo(Cloud cloud) {
        userCloud=cloud;
    }

    /**
     * This method sets the user (security) info and the cloudname.
     *
     * @param       user            the username
     * @param       password        the password
     * @param       cloud           The cloudname
     */
    public void setUserInfo(String user, String pass, String logonmethod, String cloud) {
        username = user;
        password = pass;
        if (logonmethod!=null) logonmethodname = logonmethod;
        if (cloud!=null) cloudname = cloud;
    }

    /**
     * This method sets the user (security) info.
     *
     * @param       user            the username
     * @param       password        the password
     */
    public void setUser(String user, String pass) {
        setUserInfo(user,pass,null,null);
    }

    /**
     * Retrieves the current username.
     *
     * @return     The username
     */
    public String getUserName() {
        if (userCloud!=null) return userCloud.getUser().getIdentifier();
        return username;
    }

    /**
     * This method tags the datanodes in the given document. The wizards use the tagged datanodes so that each datanode can be identified.
     *
     * @param       data    The data document which should be tagged.
     */
    public void tagDataNodes(Document data) {
        didcounter = 1;
        tagDataNode(data.getDocumentElement());
    }

    /**
     * This method tags datanodes starting from the current node.
     * A internal counter is used to make sure identifiers are still unique.
     */
    public void tagDataNode(Node node) {
        NodeList nodes = Utils.selectNodeList(node, ".|.//*");
        didcounter = Utils.tagNodeList(nodes, "did", "d", didcounter);
    }

    /**
     * This method loads relations from MMBase and stores the result in the given object node.
     *
     * @param       object          The objectnode where the results should be appended to.
     * @param       objectnumber    The objectnumber of the parentobject from where the relations should originate.
     * @param       loadaction      The node with loadaction data. Has inforation about what relations should be loaded and what fields should be retrieved.
     */
  private void loadRelations(Node object, String objectnumber, Node loadaction) throws Exception {
        // loads relations using the loadaction rules
    Document flatloadaction = Utils.parseXML("<tmp />");
    NodeList tmprels = Utils.selectNodeList(loadaction, "relation");
    for (int i=0; i<tmprels.getLength(); i++) {
        flatloadaction.getDocumentElement().appendChild(flatloadaction.importNode(tmprels.item(i).cloneNode(false), true));
    }

    NodeList flatrels = Utils.selectNodeList(flatloadaction, "/*/relation");
    try {
        // only get relations if a loadrestriction is placed.
            if (flatrels.getLength()>0) getRelations(object, objectnumber, flatrels);
    } catch (Exception e) {
        log.debug("Could not getRelations (loadRelations) object ["+objectnumber+"]. MMBase returned some errors. Sorry for that, though.\n"+e.getMessage());
        throw e;
    }

    // load objects to which relations are pointing
    NodeList loadactionrestrictionfields = Utils.selectNodeList(loadaction, "field");
    NodeList rels = Utils.selectNodeList(object, "relation");
    for (int i=0; i<rels.getLength(); i++) {
        Node rel = rels.item(i);
        String parentobjnumber = Utils.getAttribute(rel.getParentNode(), "number");
        String relatedobject = getOtherRelatedObject(rel, parentobjnumber);
        Node loadedobj = getData(rel, relatedobject, loadactionrestrictionfields);

        // object loaded. Check to see if we need to follow more relations...
        // find corresponding loadaction settings...
        String reldestination=Utils.getAttribute(loadedobj, "type");
        Node deeprels = Utils.selectSingleNode(loadaction, "relation[@destination='"+reldestination+"'][relation/@destination]");

        if (deeprels!=null) {
            // yep. we should carry on loading! (Recurse!)
            loadRelations(loadedobj, relatedobject, deeprels);
        }
    }
  }

  /**
   * This method loads an object and the necessary relations and fields, according to the given schema.
   *
   * @param     schema          The schema carrying all the information needed for loading the proper object and related fields and objects.
   * @param     objectnumber    The objectnumber of the object to start with.
   * @return   The resulting data document.
   */
  public Document load(Node schema, String objectnumber) throws Exception {
    // intialize data xml
    Document data = Utils.parseXML("<data />");

    try {
      // load initial object using object number
      log.debug("Loading: " + objectnumber);
      Node object = getData(data.getDocumentElement(), objectnumber);

      // load relations
      Node loadactionrestrictions = Utils.selectSingleNode(schema, "action[@type='load']");
      if (loadactionrestrictions==null) {
        // no action type="load". Give an emptyone.
        Document tmp = Utils.parseXML("<action type=\"load\" />");
        loadactionrestrictions = tmp.getDocumentElement();
      }
      loadRelations(object, objectnumber, loadactionrestrictions);
    } catch (Exception e) {
        log.error("Could not load object ["+objectnumber+"]. MMBase returned some errors.\n"+e.getMessage());
        throw e;
    }

    tagDataNodes(data);
    return data;
  }

  //////////////////////////////////////////////////////////////////////
  // MMBase API methods
  //////////////////////////////////////////////////////////////////////

  /**
   * This method loads a list from mmbase.
   *
   * Depricated.
   */
  public Node getList(String objecttype) throws Exception {
    // fires getData command and places result in targetNode
    ConnectorCommand cmd = new ConnectorCommandGetList(objecttype);
    fireCommand(cmd);

    if (!cmd.hasError()) {
      // place object in targetNode
        Node result = cmd.responsexml.cloneNode(true);
        return result;
    } else {
      throw new Exception("getList command not succesful, for objecttype " + objecttype);
    }
  }

  /**
   * This method gets constraint information from mmbase about a specific objecttype.
   *
   * @param     objecttype      the objecttype where you want constraint information from.
   * @return   the constraintsnode as received from mmbase (Dove)
   */
  public Node getConstraints(String objecttype) throws Exception {
    // fires getData command and places result in targetNode
    ConnectorCommand cmd = new ConnectorCommandGetConstraints(objecttype);
    fireCommand(cmd);

    if (!cmd.hasError()) {
    // place object in targetNode
    Node result = cmd.responsexml.getFirstChild().cloneNode(true);
    return result;
    } else {
    throw new Exception("getConstraints command not succesful, for objecttype " + objecttype);
    }
  }


  /**
   * This method retrieves a list from mmbase. It uses a query which is sent to mmbase.
   *
   * Depricated.
   */
  public Node getList(Node query) throws Exception {
      // fires getData command and places result in targetNode
      ConnectorCommand cmd = new ConnectorCommandGetList(query);
      fireCommand(cmd);

      if (!cmd.hasError()) {
          // place object in targetNode
          if (log.isDebugEnabled()) log.debug(Utils.getSerializedXML(cmd.responsexml));
          Node result = cmd.responsexml.cloneNode(true);
          return result;
      } else {
          throw new Exception("getList command not succesful");
      }
  }

  /**
   * This method retrieves data (objectdata) from mmbase.
   *
   * @param     targetNode      Results are appended to this targetNode.
   * @param     objectnumber    The number of the object to load.
   * @return   The resulting node with the objectdata.
   */
  public Node getData(Node targetNode, String objectnumber) throws Exception {
    return getData(targetNode, objectnumber, null);
  }

  /**
   * This method retrieves data (objectdata) from mmbase.
   *
   * @param     targetNode      Results are appended to this targetNode.
   * @param     objectnumber    The number of the object to load.
   * @param     restrictions    These restrictions will restrict the load action. So that not too large or too many fields are retrieved.
   * @return   The resulting node with the objectdata.

   */
  public Node getData(Node targetNode, String objectnumber, NodeList restrictions) throws Exception {
    // fires getData command and places result in targetNode
    ConnectorCommandGetData cmd = new ConnectorCommandGetData(objectnumber, restrictions);
    fireCommand(cmd);

    if (!cmd.hasError()) {
      // place object in targetNode
      Node objectnode = targetNode.getOwnerDocument().importNode(Utils.selectSingleNode(cmd.responsexml, "/*/object[@number='" + objectnumber + "']").cloneNode(true),true);
      tagDataNode(objectnode);
      targetNode.appendChild(objectnode);
      return objectnode;
    } else {
      throw new Exception("Could not fire getData command for object " + objectnumber);
    }
  }

public void getRelations(Node targetNode, String objectnumber) throws Exception {
    getRelations(targetNode, objectnumber, null);
}

/**
 * This method gets relation information from mmbase.
 *
 * @param       targetNode      The targetnode where the results should be appended.
 * @param       objectnumber    The objectnumber of the parent object from where the relations originate.
 * @param       loadaction      The loadaction data as defined in the schema. These are used as 'restrictions'.
 *
 */
public void getRelations(Node targetNode, String objectnumber, NodeList loadaction) throws Exception {
    // fires getRelations command and places results targetNode
    ConnectorCommandGetRelations cmd = new ConnectorCommandGetRelations(objectnumber, loadaction);
    fireCommand(cmd);

    if (!cmd.hasError()) {
            NodeList relations = Utils.selectNodeList(cmd.responsexml, "/*/object/relation");
            for (int i=0; i<relations.getLength(); i++) {
                tagDataNode(relations.item(i));
            processIncomingRelation(relations.item(i), objectnumber);
        }

        Utils.appendNodeList(relations, targetNode);
    } else {
        throw new Exception("Could NOT fire getData command for object " + objectnumber);
    }
}

/**
 * This method gets a new temporarily object of the given type.
 *
 * @param       targetNode      The place where the results should be appended.
 * @param       objecttype      The objecttype which should be created.
 * @return     The resulting object node.
 */
  public Node getNew(Node targetNode, String objecttype) throws Exception {
    // fires getNew command and places result in targetNode
    ConnectorCommandGetNew cmd = new ConnectorCommandGetNew(objecttype);
    fireCommand(cmd);

    if (!cmd.hasError()) {
      Node objectnode = targetNode.getOwnerDocument().importNode(Utils.selectSingleNode(cmd.responsexml, "/*/object[@type='"+objecttype+"']").cloneNode(true), true);
      tagDataNode(objectnode);
      targetNode.appendChild(objectnode);
      return objectnode;
    } else {
      throw new Exception("getNew command returned an error. Objecttype="+objecttype);
    }
  }

  /**
   * This method creates a new temporarily relation.
   *
   * For now, Dove does not have a getNewRelation method. A dummy temporarily relation is created locally. Thus: NO communition is made with MMBase.
   *
   * @param     targetNode      The place where the results should be appended.
   * @param     role            The name of the role the new relation should have.
   * @param     sourceobjectnumber      the number of the sourceobject
   * @param     destinationobjectnumber the number of the destination object
   * @return   The resulting relation node.
   */
  public Node getNewRelation(Node targetNode, String role, String sourceobjectnumber, String destinationobjectnumber) throws Exception {
    // fires getNewRelation command and places result in targetNode
    ConnectorCommandGetNewRelation cmd = new ConnectorCommandGetNewRelation(role, sourceobjectnumber, destinationobjectnumber);
    fireCommand(cmd);

    if (!cmd.hasError()) {
      Node objectnode = targetNode.getOwnerDocument().importNode(Utils.selectSingleNode(cmd.responsexml, "/*/relation").cloneNode(true), true);
      tagDataNode(objectnode);
      processIncomingRelation(objectnode, sourceobjectnumber);
      targetNode.appendChild(objectnode);
      return objectnode;
    } else {
      throw new Exception("getNewRelation command returned an error. role="+role + ", source="+sourceobjectnumber+", dest="+destinationobjectnumber);
    }
  }
  ///////////////////////////////////// create Logic methods
  /**
   * This method can create a object (or a tree of objects and relations)
   *
   * this method should be called from the wizard if it needs to create a new
   * combination of fields, objects and relations.
   * see further documentation or the samples for the definition of the action
   *
   * in global: the Action node looks like this: (Note: this function expects the <object/> node,
   * not the action node.
   *
   * <action type="create">
   *   <object type="authors">
   *     <field name="name">Enter name here</field>
   *     <relation role="related" [destination="234" *]>
   *       <object type="locations">
   *         <field name="street">Enter street</field>
   *       </object>
   *     </relation>
   *   </object>
   * </action>
   *
   * *) if dnumber is supplied, no new object is created (shouldn't be included in the relation node either),
   *  but the relation will be created and directly linked to an object with number "dnumber".
   *
   * @param     targetParentNode        The place where the results should be appended.
   * @param     objectDef               The objectdefinition.
   * @param     params                  The params to use when creating the objects and relations.
   * @return   The resulting object(tree) node.
   */
  public Node createObject(Document data, Node targetParentNode, Node objectDef, Hashtable params) throws WizardException {
    String objecttype = Utils.getAttribute(objectDef, "type");
    String nodename = objectDef.getNodeName();

    // no relations to add here..
    NodeList relations = null;
    Node objectnode = null;

    // check if we maybe should create multiple objects or relations
    if (nodename.equals("action")) {
        NodeList objectdefs = Utils.selectNodeList(objectDef, "object|relation");
        Node firstobject=null;
        //for (int i=objectdefs.getLength()-1; i>=0; i--) {
        for (int i=0; i<objectdefs.getLength(); i++) {
            firstobject=createObject(data,targetParentNode, objectdefs.item(i), params);
        }
        return firstobject;
    }

    if (nodename.equals("relation")) {
       // objectnode equals targetParentNode
       objectnode = targetParentNode;
       relations = Utils.selectNodeList(objectDef, ".");
    }

    if (nodename.equals("object")) {
      try {
        // create a new object of the given type
        objectnode = getNew(targetParentNode, objecttype);
      } catch (Exception e) {
        log.error("Could NOT createObject with type:"+objecttype+". Message: "+ e.getMessage());
        throw new WizardException("Could NOT createObject with type:"+objecttype+". Message: "+ e.getMessage());
      }

      // fill-in (or place) defined fields and their values.
      NodeList fields = Utils.selectNodeList(objectDef, "field");
      for (int i=0; i<fields.getLength(); i++) {
        Node field = fields.item(i);
        String fieldname = Utils.getAttribute(field, "name");
        // does this field already exist?
        Node datafield = Utils.selectSingleNode(objectnode, "field[@name='"+fieldname+"']");
        if (datafield!=null) {
          // yep. fill-in
          String value=Utils.getText(field);
          if ((value!=null) && value.startsWith("{")) {
            Node parent=data.getDocumentElement();

            log.info(parent.toString());
            log.info(value.substring(1,value.length()-1));

            value=Utils.selectSingleNodeText(parent,value.substring(1,value.length()-1),"X");
          }
          Utils.storeText(datafield, value, params); // place param values inside if needed
        } else {
          // nope. create. (Or, actually, clone and import node from def and place it in data
          Node newfield = targetParentNode.getOwnerDocument().importNode(field.cloneNode(true), true);
          objectnode.appendChild(newfield);
          Utils.storeText(newfield, Utils.getText(newfield), params); // process innerText: check for params
        }
      }

      relations = Utils.selectNodeList(objectDef, "relation");
    }

    // Let's see if we need to create new relations (maybe even with new objects inside...
    Node lastCreatedRelation = null;

    for (int i=0; i<relations.getLength(); i++) {
        Node relation = relations.item(i);
        String dnumber = Utils.getAttribute(relation, "destination", null);
        dnumber=Utils.transformAttribute(targetParentNode, dnumber, false, params);
        Node inside_object = null;
        Document tempobjectholder=null;
        if (dnumber==null) {
            // no dnumber given! let's just create the object definition inside first.
            Node inside_objectdef = Utils.selectSingleNode(relation, "object");
            if (inside_objectdef==null) {
                // no destination is given AND no object to-be-created-new is placed.
                // so, no destination should be added...
                tempobjectholder = Utils.parseXML("<object number=\"\" type=\"" + Utils.getAttribute(relation, "destinationtype", "") + "\" disposable=\"true\"/>");
                inside_object = tempobjectholder.getDocumentElement();
            } else {
                tempobjectholder = Utils.parseXML("<tmpdata/>");
                inside_object = createObject(data,tempobjectholder.getDocumentElement(), inside_objectdef, params);
                dnumber = Utils.getAttribute(inside_object, "number");
            }
        }

        String role="related";
        String snumber="";
        Node relationnode = null;

        try {
            // create the relation now we can get all needed params
            role = Utils.getAttribute(relation, "role", "related");
            snumber = Utils.getAttribute(objectnode, "number");
            relationnode = getNewRelation(objectnode, role, snumber, dnumber);

            // place pre-defined fields in relation node (eg. pos-fields in posrel relation)
            NodeList flds = Utils.selectNodeList(relation, "field");
            Utils.appendNodeList(flds, relationnode);

            // check if some params should be replaced.
            flds = Utils.selectNodeList(relationnode, "field");
            for (int j=0; j<flds.getLength(); j++) {
                Utils.storeText(flds.item(j), Utils.getText(flds.item(j)), params);
            }
            tagDataNode(relationnode);
            lastCreatedRelation = relationnode;
        } catch (Exception e) {
            log.error("Could NOT create relation in createObject. Role="+role+", snumber="+snumber+", destination="+dnumber);
            return null;
        }

        try {
            // now check if we need to load the inside object...
            if (inside_object==null) {
                // yep. we don't have it yet. Let's load it
                inside_object = getData(relationnode, dnumber);
                // but annotate that thisone is loaded from mmbase. Not a new one
                Utils.setAttribute(inside_object, "already-exists", "true");
            } else {
                // we already have it. Let's copy/clone and place it.
                inside_object = relationnode.getOwnerDocument().importNode(inside_object.cloneNode(true), true);
                relationnode.appendChild(inside_object);
            }
        } catch (Exception e) {
            log.error("Could NOT place inside object in createObject. Message: "+e.getMessage());
            return null;
        }
    }
    if (nodename.equals("relation")) {
        return lastCreatedRelation;
    } else {
        return objectnode;
    }
}

  ///////////////////////////////////// fireCommand methods

  /**
   * This is an internal method which is used to fire a command to connect to mmbase via Dove.
   */
  public Document fireCommand(ConnectorCommand command) throws SecurityException,WizardException {
    Vector tmp = new Vector();
    tmp.add(command);
    return fireCommands(tmp);
  }

  /**
   * This is an internal method which is used to fire commands to connect to mmbase via Dove.
   */
  public Document fireCommands(Vector commands) throws SecurityException,WizardException {
    // send commands away from here... away!

    // first create request xml
    Document req = Utils.parseXML("<request/>");
    Node docel = req.getDocumentElement();

    // Add security tag if necessary.
    if (username != null && !username.equals("anonymous")){
        Element secure = req.createElement("security");
        secure.setAttribute("name",this.username);
        secure.setAttribute("password",this.password);
        secure.setAttribute("method",this.logonmethodname);
        secure.setAttribute("cloud",this.cloudname);
        docel.appendChild(secure);
    }

    Enumeration enum = commands.elements();
    while (enum.hasMoreElements()) {
      ConnectorCommand cmd = (ConnectorCommand)enum.nextElement();
      docel.appendChild(req.importNode(cmd.getCommandXML().getDocumentElement().cloneNode(true), true));
    }

    String res="";
    Element results=null;
    try {
        Document tmp = Utils.EmptyDocument();
        Dove dove = new Dove(tmp);
        if (userCloud!=null) {
            results = dove.executeRequest(req.getDocumentElement(),userCloud,null);
        } else {
            results = dove.executeRequest(req.getDocumentElement(),null,null);
        }
    } catch (Exception e) {
        throw new WizardException("Error while communicating with Dove servlet. "+e.getMessage());
    }
    Document response = Utils.EmptyDocument();
    response.appendChild(response.importNode(results,true));

    Node securityError = Utils.selectSingleNode(response,"/response/security/error");
    if (securityError != null){
        throw new SecurityException("Login incorrect for user '" + this.username + "'.");
    }

    // map response back to each command.
    enum = commands.elements();
    while (enum.hasMoreElements()) {
      ConnectorCommand cmd = (ConnectorCommand)enum.nextElement();

      // find response for this command
      Node resp = Utils.selectSingleNode(response, "/*/"+cmd.name +"[@id]");
      if (resp!=null) {
        // yes we found a response
        cmd.setResponseXML(resp);
      } else {
        log.error("Could NOT store response "+cmd.id+" in a ConnectorCommand");
        log.error(cmd.responsexml);
      }
    }

    return response;
  }

  /**
   * This method can fire a Put command to Dove. It uses #putData to construct the transaction.
   *
   * @param     originalData            The original data object tree.
   * @param     newData                 The new and manipulated data. According to differences between the original and the new data, the transaction is constructed.
   * @param     uploads                 A hashmap with the uploaded binaries.
   * @return   The element containing the results of the put transaction.
   */
  public Element firePutCommand(Document originalData, Document newData, HashMap uploads) throws WizardException {
    Node putcmd =putData(originalData, newData);
    Element results=null;
    try {
        Document tmp = Utils.EmptyDocument();
        Dove dove = new Dove(tmp);
        if (userCloud!=null) {
            results = dove.executeRequest(putcmd.getOwnerDocument().getDocumentElement(),userCloud,uploads);
        } else {
            results = dove.executeRequest(putcmd.getOwnerDocument().getDocumentElement(),null,uploads);
        }
    } catch (Exception e) {
        log.error("Error while communicating with Dove servlet."+e.getMessage());
        throw new WizardException("Error while communicating with Dove servlet. Message:"+e.getMessage());
    }
    return results;
  }

  /**
   * This method constructs a update transaction ready for mmbase.
   * The differences between the original and the new data define the transaction.
   *
   * @param     originalData    The original data.
   * @param     newData         The new data.
   */
  public Node putData(Document originalData, Document newData) throws WizardException {
        Document d = Utils.parseXML("<response/>");

        Document workDoc = Utils.EmptyDocument();
        workDoc.appendChild(workDoc.importNode(newData.getDocumentElement().cloneNode(true), true));

        Node workRoot = workDoc.getDocumentElement();

        // initialize request xml
        Document req = Utils.parseXML("<request><put id=\"put\"><original/><new/></put></request>");

        // Add security tag if necessary.
        if (username != null && !username.equals("anonymous")){
            Element secure = req.createElement("security");
            secure.setAttribute("name",this.username);
            secure.setAttribute("password",this.password);
            secure.setAttribute("method",this.logonmethodname);
            secure.setAttribute("cloud",this.cloudname);
            req.getDocumentElement().insertBefore(secure, req.getDocumentElement().getFirstChild());
        }

        Node reqorig = Utils.selectSingleNode(req, "/request/put/original");
        Node reqnew = Utils.selectSingleNode(req, "/request/put/new");

        // Remove disposable objects (disposable=true)
        // Remove all relations which have NO destination chosen (destination="-");
        NodeList disposables = Utils.selectNodeList(workRoot, ".//*[@disposable or @destination='-']");
        for (int i=0; i<disposables.getLength(); i++) {
            Node disp = disposables.item(i);
            disp.getParentNode().removeChild(disp);
        }

        // serialize original data. Place objects first, relations second
        makeFlat(originalData, reqorig, ".//object", "field");
        makeFlat(originalData, reqorig, ".//relation", "field");

        // serialize new data. Place objects first, relations second
        makeFlat(workRoot, reqnew, ".//object", "field");
        makeFlat(workRoot, reqnew, ".//relation", "field");

        // find all changed or new relations and objects
        NodeList nodes = Utils.selectNodeList(reqnew, ".//relation|.//object[not(@disposable)]");
        for (int i=0; i<nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodename = node.getNodeName();

            String nodenumber = Utils.getAttribute(node, "number", "");
            Node orignode = Utils.selectSingleNode(reqorig, ".//*[@number='"+nodenumber+"' and not(@already-exists)]");
            if (orignode!=null) {
                // we found the original relation. Check to see if destination has changed.
                if (nodename.equals("relation")) {
                    String destination = Utils.getAttribute(node,"destination", "");
                    String olddestination = Utils.getAttribute(orignode,"destination", "");
                    if (!destination.equals(olddestination) && !destination.equals("")) {
                        // ok. it's different
                        Utils.setAttribute(node, "status", "changed");
                        // store original destination also. easier to process later on
                        Utils.setAttribute(node, "olddestination", olddestination);
                        } else {
                        // it's the same (or at least: the destination is the same)
                        // now se check if some inside-fields are changed.
                        boolean valueschanged = checkRelationFieldsChanged(orignode, node);

                        if (valueschanged) {
                            // values in the fields are changed, destination/source are still te same.
                            // let's store that knowledge.
                            Utils.setAttribute(node,"status", "fieldschangedonly");
                        } else {
                            // really nothing changed.
                                // remove relation from both orig as new
                            node.getParentNode().removeChild(node);
                                orignode.getParentNode().removeChild(orignode);
                        }
                    }
                }
                if (nodename.equals("object")) {
                    // object
                    // check if it is changed
                    boolean different = isDifferent(node, orignode);
                    if (!different) {
                        // remove both objects
                        node.getParentNode().removeChild(node);
                        orignode.getParentNode().removeChild(orignode);
                    } else {
                        // check if fields are different?
                        NodeList fields=Utils.selectNodeList(node,"field");
                        for (int j=0; j<fields.getLength(); j++) {
                        Node origfield = Utils.selectSingleNode(orignode, "field[@name='"+Utils.getAttribute(fields.item(j), "name")+"']");
                        if (origfield!=null) {
                            if (!isDifferent(fields.item(j), origfield)) {
                                // the same. let's remove this field also
                                fields.item(j).getParentNode().removeChild(fields.item(j));
                                origfield.getParentNode().removeChild(origfield);
                            }
                        }
                    }
                }
            }
        } else {
            // this is a new relation or object. Remember that

            // but, check first if the may-be-new object has a  "already-exists" attribute. If so,
            // we don't have a new object, no no, this is a later-loaded object which is not added to the
            // original datanode (should be better in later versions, eg. by using a repository).
            String already_exists = Utils.getAttribute(node, "already-exists", "false");
            if (!already_exists.equals("true")) {
                // go ahead. this seems to be a really new one...
                Utils.setAttribute(node, "status", "new");
            } else {
                // remove it from the list.
                node.getParentNode().removeChild(node);
            }
        }
    }

        // find all deleted relations and objects
        NodeList orignodes = Utils.selectNodeList(reqorig, ".//relation|.//object");
        for (int i=0; i<orignodes.getLength(); i++) {
            Node orignode = orignodes.item(i);

            String nodenumber = Utils.getAttribute(orignode, "number", "");
            Node node = Utils.selectSingleNode(reqnew, ".//*[@number='"+nodenumber+"']");
            if (node==null) {
                // item is apparently deleted.
                // place relation node anyway but say that it should be deleted (and make it so more explicit)
                Node newnode = req.createElement(orignode.getNodeName());
                Utils.copyAllAttributes(orignode, newnode);
                Utils.setAttribute(newnode, "status", "delete");
                reqnew.appendChild(newnode);
            }
        }

        // now, do our final calculations:
        //
        //
        // 2. change "changed" relations into a delete + a create command.
        //    and, make sure the create command is in the right format.
        NodeList rels = Utils.selectNodeList(req, "//new/relation[@status='changed']");
        for (int i=0; i<rels.getLength(); i++) {
            Node rel = rels.item(i);
            Node newrel = rel.cloneNode(true);

            // say that old relation should be deleted
            Utils.setAttribute(rel, "destination", Utils.getAttribute(rel, "olddestination", ""));
            rel.getAttributes().removeNamedItem("olddestination");
            Utils.setAttribute(rel, "status", "delete");

            // say that a new relation should be formed
            newrel.getAttributes().removeNamedItem("number");
            newrel.getAttributes().removeNamedItem("olddestination");
            Utils.setAttribute(newrel, "status", "new");
            String role = Utils.getAttribute(newrel, "role", "related");
            Utils.setAttribute(newrel, "role", role);

            // copy inside fields also (except dnumber, snumber and rnumber fields)
            NodeList flds = Utils.selectNodeList(rel, RELATIONFIELDS_XPATH);
            Utils.appendNodeList(flds,rel);

            // store the new rel in the list also
            rel.getParentNode().appendChild(newrel);
        }

        //
        // 3. search for 'fieldschangedonly' fields of the relations. If so, we should make a special command for the Dove
        // servlet.
        //
        rels = Utils.selectNodeList(req, "//new/relation[@status='fieldschangedonly']");
        for (int i=0; i<rels.getLength(); i++) {
            Node rel = rels.item(i);
            String number = Utils.getAttribute(rel,"number","");
            Node origrel = Utils.selectSingleNode(req, "//original/relation[@number='"+number+"']");
            if (!number.equals("") && origrel!=null) {
                // we found the original relation also. Now, we can process these nodes.
                convertRelationIntoObject(origrel);
                convertRelationIntoObject(rel);
            }
        }

        return req.getDocumentElement();
  }

  // Utils

  /**
   * This method returns the objectnumber of the related object, given the 'other' related object.
   *
   * @param     relationnode    The relation which to use.
   * @param     objectnumber    The objectnumber we don't want to get. So, we want to get the OTHER related object.
   * @return   The number of other related object.
   */
  public String getOtherRelatedObject(Node relationnode, String objectnumber) {
    String dnumber="";
    String snumber="";

    try {
        dnumber = Utils.selectSingleNode(relationnode, "field[@name='dnumber']/text()").getNodeValue();
        snumber = Utils.selectSingleNode(relationnode, "field[@name='snumber']/text()").getNodeValue();
    } catch (Exception e) {
        try {
            // sometimes a destination="objnr" is given, sometimes dnumber/snumber is used. This is a workaround.
            return Utils.getAttribute(relationnode, "destination", "");
        } catch (RuntimeException e2) {
                log.error("getOtherRelationObject err:"+e.getMessage());
                return "";
        }
    }
    if (dnumber.equals(objectnumber)) return snumber;
        return dnumber;
  }

  /**
   * If a new relation is made, it will be processed here. Attributes are set so that all other code can easily use the relations.
   *
   * @param     relationnode    the newly created or loaded relationnode.
   * @param     objectnumber    the already known objectnumber.
   */
  public void processIncomingRelation(Node relationnode, String objectnumber) {
    // calculate right destination objectnumber and place it in the relationnode
    String othernumber = getOtherRelatedObject(relationnode, objectnumber);
    if (othernumber.equals("")) return;

    Utils.setAttribute(relationnode, "source", objectnumber);
    Utils.setAttribute(relationnode, "destination", othernumber);
  }

  /**
   * This method makes the object data tree flat, so that Dove can construct a transaction from it.
   *
   * @param     sourcenode              The sourcenode from which should be flattened.
   * @param     targetParentNode        The targetParentNode where the results should be appended.
   * @param     allowChildrenXpath      This xpath defines what children may be copied in te proces and should NOT be flattened.
   */
  public void makeFlat(Node sourcenode, Node targetParentNode, String xpath, String allowedChildrenXpath) {
    NodeList list = Utils.selectNodeList(sourcenode, xpath);
    for (int i=0; i<list.getLength(); i++) {
        Node item = list.item(i);
        Node newnode = targetParentNode.getOwnerDocument().importNode(item, false);
        targetParentNode.appendChild(newnode);
        cloneOneDeep(item, newnode, allowedChildrenXpath);
    }
  }

  /**
   * This method can clone an object node one deep. So, only the first level children will be copied.
   *
   * @param     sourcenode              The sourcenode from where the flattening should start.
   * @param     targetParentNode        The parent on which the results should be appended.
   * @param     allowedChildrenXpath    The xpath describing what children may be copied.
   */
  public void cloneOneDeep(Node sourcenode, Node targetParentNode, String allowedChildrenXpath) {
    NodeList list = Utils.selectNodeList(sourcenode, allowedChildrenXpath);
    Utils.appendNodeList(list, targetParentNode);
  }

  /**
   * This method compares two xml nodes and checks them for identity.
   *
   * @param     node1   The first node to check
   * @param     node2   Compare with thisone.
   * @return   True if they are different, False if they are similar.
   */
  public boolean isDifferent(Node node1, Node node2) {
        // only checks textnodes and childnumbers
    boolean res = false;
    if (node1.getChildNodes().getLength()!=node2.getChildNodes().getLength()) {
        // ander aantal kindjes!
        return true;
    }

    // andere getnodetype?
    if (node1.getNodeType()!=node2.getNodeType()) {
            return true;
    }

    if (node1.getNodeType()==Node.TEXT_NODE) {
            String s1 = node1.getNodeValue();
        String s2 = node2.getNodeValue();
        if (!s1.equals(s2)) return true;
    }


    // check kids
    NodeList kids = node1.getChildNodes();
    NodeList kids2 = node2.getChildNodes();

    for (int i=0; i<kids.getLength(); i++) {
            if (isDifferent(kids.item(i), kids2.item(i))) {
                return true;
        }
    }
    return false;
  }

/**
 * This method checks if relationfields are changed. It does NOT check the source-destination numbers, only the other fields,
 * like the pos field in a posrel relation.
 *
 * @param       origrel         The original relation
 * @param       rel             The new relation
 * @return     True if the relations are different, false if they are the same.
 */
  private boolean checkRelationFieldsChanged(Node origrel, Node rel) {
    NodeList origflds = Utils.selectNodeList(origrel, RELATIONFIELDS_XPATH);
    NodeList newflds = Utils.selectNodeList(rel, RELATIONFIELDS_XPATH);
    Document tmp = Utils.parseXML("<tmp><n1><r/></n1><n2><r/></n2></tmp>");

    Node n1 = Utils.selectSingleNode(tmp, "/tmp/n1/r");
    Node n2 = Utils.selectSingleNode(tmp, "/tmp/n2/r");

    Utils.appendNodeList(origflds,n1);
    Utils.appendNodeList(newflds,n2);

    return isDifferent(n1,n2);
  }

  /**
   * this method converts a <relation /> node into an <object /> node.
   * source/destination attributes are removed, and rnumber, snumber, dnumber fields also.
   * It is mainly used by the putData commands to store extra relation fields values.
   * @param     rel     The relation which should be converted.
    */
  private void convertRelationIntoObject(Node rel) {

    Node obj = rel.getOwnerDocument().createElement("object");

    // copy attributes, except...
    Vector except = new Vector();
    except.add("destination");
    except.add("source");
    except.add("role");
    except.add("status"); // we don't need status anymore also!
    Utils.copyAllAttributes(rel, obj, except);

    // copy fields, except... (uses RELATIONFIELDS_XPATH)
    NodeList flds = Utils.selectNodeList(rel, RELATIONFIELDS_XPATH);
    Utils.appendNodeList(flds, obj);

    // remove rel, place obj instead
    rel.getParentNode().replaceChild(obj, rel);
  }
}
