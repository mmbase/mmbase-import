/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.action;

import java.util.*;

import javax.servlet.ServletRequest;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.data.*;
import org.mmbase.applications.editwizard.schema.*;
import org.mmbase.applications.editwizard.session.WizardConfig;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;


public class WizardWorkspace {

    private static final Logger log = Logging.getLoggerInstance(WizardWorkspace.class);
    
    /**
     * load the wizard data
     * @return
     * @throws WizardException
     */
    public boolean doLoad(WizardConfig wizardConfig, Cloud cloud) throws WizardException {
        WizardSchema wizardSchema = wizardConfig.getWizardSchema();
        
        WizardCloudConnector connector = WizardCloudConnector.getInstance(cloud);
        if ("new".equals(wizardConfig.objectNumber)==false) {
            ActionElm actionElm = wizardSchema.getActionByType("load");
            //int objectNumber = Integer.parseInt(wizardConfig.objectNumber);
            ObjectElm objectElm = new ObjectElm();
            if (actionElm!=null) {
                objectElm.fields.addAll(actionElm.fields);
                objectElm.relations.addAll(actionElm.relations);
            }
            wizardConfig.wizardData = connector.load(objectElm,wizardConfig.objectNumber);
        } else {
            ActionElm actionElm = wizardSchema.getActionByType("create");
            ObjectElm objectElm = null;
            if (actionElm!=null) {
                objectElm = actionElm.object;
            }
            wizardConfig.wizardData = connector.create(objectElm,wizardConfig.getAttributes());
        }

        return true;
    }
    
    /**
     * get document
     * @return
     * @throws WizardException
     */
    public Document getDocument(WizardConfig wizardConfig, Cloud cloud) throws WizardException {
        WizardDOM wizarddom = WizardDOM.getInstance(wizardConfig, cloud);
        Document doc = wizarddom.getDocument();
        Validator.validate(doc,wizardConfig.getWizardSchema());
        return doc;
    }
    
    /**
     * get data from request and save to wizardconfig
     * @param request
     * @return
     * @throws WizardException
     */
    public boolean saveWizardData(ServletRequest request, WizardConfig wizardConfig, String timezone) throws WizardException {
        
        String formEncoding = request.getCharacterEncoding();
        boolean hasEncoding = formEncoding != null;
        if (!hasEncoding) {
            log.debug("Request did not mention encoding, supposing UTF-8, as JSP's are");
            formEncoding = "UTF-8";
        } else {
            log.debug("found encoding in the request: " + formEncoding);
        }

        Enumeration enu = request.getParameterNames();
        log.debug("Synchronizing editor data, using the request");
        while(enu.hasMoreElements()) {
            String name = (String)enu.nextElement();

            if (name.startsWith("internal_")) {
                log.debug("Ignoring parameter " + name);
            } else {
                log.debug("Processing parameter " + name);

                String[] ids = processFormName(name);
                if (log.isDebugEnabled()) {
                    log.debug("found ids: " + ((ids == null) ? "null" : (" " + java.util.Arrays.asList(ids))));
                }
                if (ids != null) {
                    String result = null;
                    if (!hasEncoding) {
                        try {
                           result = new String(request.getParameter(name).getBytes("ISO-8859-1"), formEncoding);
                           if (log.isDebugEnabled()) {
                               log.debug("Found in post '" + request.getParameter(name) +  "' -> '" + result + "'");
                           }
                        } catch (java.io.UnsupportedEncodingException e) {
                            log.warn(e.toString());
                            result = request.getParameter(name);
                        }
                    } else { // the request encoding was known, so, I think we can suppose that the Parameter value was interpreted correctly.
                        result = request.getParameter(name);
                    }
                    
                    if ("date".equals(result)) {
                        result = buildDate(request, name, timezone);
                    }
                    if ("time".equals(result)) {
                        result = buildTime(request, name, timezone);
                    }
                    if ("datetime".equals(result)) {
                        result = buildDatetime(request, name, timezone);
                    }
                    if ("duration".equals(result)) {
                        result = buildDuration(request, name, timezone);
                    }

                    storeValue(wizardConfig, ids[0], ids[1], result);
                }
            }
        }
        
        
        return true;
    }
    
    /**
     * This method de-encodes a html field-name (@see #calculateFormName) and returns an Array with the decoded values.
     * @return     The array with id's. First id in the array is the data-id (did), which indicates what datanode is pointed to,
     *              second id is the fid (field-id) which points to the proper fieldnode in the wizarddefinition.
     */
    private String[] processFormName(String formName) {
        String[] res = { "", "" };

        boolean isafield = (formName.indexOf("field/") > -1);
        int nr1 = formName.indexOf("/") + 1;
        int nr2 = formName.indexOf("/", nr1) + 1;

        if ((nr1 < 1) || (nr2 < 1) || !isafield) {
            // not good. no 2 slashes found
            return null;
        }

        String fid = formName.substring(nr1, nr2 - 1);
        String did = formName.substring(nr2);
        res[0] = did;
        res[1] = fid;

        return res;
    }
    
    /**
     * get date parameter's value from request
     * @param req
     * @param name
     * @return
     */
    private String buildDate(ServletRequest req, String name, String timezone) {
        try {
            int day = Integer.parseInt(req.getParameter("internal_" + name + "_day"));
            int month = Integer.parseInt(req.getParameter("internal_" + name + "_month"));
            int year = Integer.parseInt(req.getParameter("internal_" + name + "_year"));

            Calendar cal = getCalendar(timezone);
            cal.set(year, month - 1, day, 0, 0, 0);
            return "" + cal.getTimeInMillis() / 1000;
        } catch (RuntimeException e) { //NumberFormat NullPointer
            log.debug("Failed to parse date for " + name + " " + e.getMessage());
            return "";
        }
    }

    private String buildTime(ServletRequest req, String name, String timezone) {
        try {
            int hours = Integer.parseInt(req.getParameter("internal_" + name + "_hours"));
            int minutes = Integer.parseInt(req.getParameter("internal_" + name + "_minutes"));

            Calendar cal = getCalendar(timezone);
            cal.set(1970, 0, 1, hours, minutes, 0);
            return "" + cal.getTimeInMillis() / 1000;
        } catch (RuntimeException e) { //NumberFormat NullPointer
            log.debug("Failed to parse time for " + name + " "
                    + e.getMessage());
            return "";
        }
    }

    
    /**
     * get the datatime paramter's value from request. the value passed by some form fields
     * @param req
     * @param name
     * @return
     */
    private String buildDatetime(ServletRequest req, String name, String timezone) {
        try {
            int day = Integer.parseInt(req.getParameter("internal_" + name + "_day"));
            int month = Integer.parseInt(req.getParameter("internal_" + name + "_month"));
            int year = Integer.parseInt(req.getParameter("internal_" + name + "_year"));
            int hours = Integer.parseInt(req.getParameter("internal_" + name + "_hours"));
            int minutes = Integer.parseInt(req.getParameter("internal_" + name + "_minutes"));

            Calendar cal = getCalendar(timezone);
            cal.set(year, month - 1, day, hours, minutes, 0);
            return "" + cal.getTimeInMillis() / 1000;
        } catch (RuntimeException e) { //NumberFormat NullPointer
            log.debug("Failed to parse datetime for " + name + " "
                    + e.getMessage());
            return "";
        }
    }

    /**
     * get the duration parameter's value form request
     * @param req
     * @param name
     * @return
     */
    private String buildDuration(ServletRequest req, String name, String timezone) {
        try {
            int hours = Integer.parseInt(req.getParameter("internal_" + name + "_hours"));
            int minutes = Integer.parseInt(req.getParameter("internal_" + name + "_minutes"));
            int seconds = Integer.parseInt(req.getParameter("internal_" + name + "_seconds"));

            Calendar cal = getCalendar(timezone);
            cal.set(1970, 0, 1, hours, minutes, seconds);
            return "" + cal.getTimeInMillis() / 1000;
        } catch (RuntimeException e) { //NumberFormat NullPointer
            log.debug("Failed to parse duration for " + name + " " + e.getMessage());
            return "";
        }
    }

    /**
     * @return Calendar with timezone parameter
     */
    private Calendar getCalendar(String timezone) {
        if (timezone != null) {
            TimeZone tz = TimeZone.getTimeZone(timezone);
            if (tz.getID().equals(timezone)) {
                return Calendar.getInstance(tz);
            }
            else {
                return Calendar.getInstance();
            }
        }
        else {
            return Calendar.getInstance();
        }
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
    private void storeValue(WizardConfig wizardConfig, String did, String fid, String value) throws WizardException {
        if (log.isDebugEnabled()) {
            log.debug("String value " + value + " in " + did + " for  field " + fid);
        }

        SchemaElement element = wizardConfig.getWizardSchema().findElementByFid(fid);
        if (element instanceof FieldElm == false) {
            throw new WizardException("No field with fid=" + fid + " could be found in schema");
        }
        FieldElm fieldElm = (FieldElm)element;
        String dttype = fieldElm.getDttype();
//        element.getAttribute(SchemaKeys.ATTR_DTTYPE);
        
        FieldData fieldData = wizardConfig.wizardData.findFieldById(did);

        if (fieldData == null) {
            String msg = "Unable to store value for field with dttype " + dttype + ". fid=" + fid
                    + ", did=" + did + ", value=" + value + ", wizard:" + wizardConfig.getWizardName();
            log.warn(msg);
            return;
        }

        // everything seems to be ok
        if ("binary".equals(dttype)) {
            //TODO: save binary data
            // binaries are stored differently
//            if (getBinary(did) != null) {
//                Utils.setAttribute(datanode, "href", did);
//                Utils.storeText(datanode, getBinaryName(did));
//            }
        } else { // default behavior: store content as text
            fieldData.setValue(value);
        }
    }

    public boolean doSave(WizardConfig wizardConfig, Cloud cloud) throws WizardException {
        
        WizardCloudConnector connector = WizardCloudConnector.getInstance(cloud);
        connector.save(wizardConfig.wizardData);
        
        return true;
    }
    
    /**
     * add item into wizard. only suitable for action[@type="search"]
     * @param fid
     * @param did
     * @param curmaxpos
     * @param numberList
     * @return
     * @throws WizardException
     */
    public boolean doAddItem(WizardConfig wizardConfig, Cloud cloud, String fid, String did, List numberList) throws WizardException {
        
        WizardSchema schema = wizardConfig.getWizardSchema();
        SchemaElement element = schema.findElementByFid(fid);
        BaseData baseData = DataUtils.findDataById(wizardConfig.wizardData, did);
        
        if (baseData instanceof ObjectData == false) {
            throw new WizardException("add-item command is only suitable for object");
        } 
        if (element instanceof ListElm == false) {
            throw new WizardException("add-item command is only suitable for list");
        }
        ObjectData source = (ObjectData)baseData;
        ListElm listElm = (ListElm)element;
        RelationElm relationElm = null;
        if (numberList.size()>0) {
            // this scenario is adding items by select nodes
            ActionElm actionElm = listElm.getActionByType("add");
            if (actionElm!=null && actionElm.relations.size()>0) {
                relationElm = (RelationElm)actionElm.relations.get(0);
            }
        } else {
            // this scenario is adding items by create new
            ActionElm actionElm = listElm.getActionByType("create");
            if (actionElm!=null && actionElm.relations.size()>0) {
                relationElm = (RelationElm)actionElm.relations.get(0);
            }
            numberList.add("new");
        }
        if (relationElm==null) {
            //if not defined action[@type="add"]
            relationElm = new RelationElm();
            String role = listElm.getRole();
            if (role!=null) {
                relationElm.setAttribute(SchemaKeys.ATTR_ROLE,role);
            }
            String destination = listElm.getDestination();
            if (destination!=null) {
                relationElm.setAttribute(SchemaKeys.ATTR_DESTINATION,destination);
            }
            String searchdir = listElm.getSearchDir();
            if (searchdir!=null) {
                relationElm.setAttribute(SchemaKeys.ATTR_SEARCHDIR,searchdir);
            }
        }
        // this is the scenario add items by select nodes.
        List relationList = createRelations(cloud, source, relationElm, numberList, wizardConfig.getAttributes());
        int maxpos = Integer.MIN_VALUE;
        for (int i=0;i<relationList.size();i++) {
            RelationData relation = (RelationData)relationList.get(i);
            if ("posrel".equals(relation.getType())) {
                // if relation's type is posrel, set position number
                if (maxpos<0) {
                    //if maxpos is not inited as current max position number, find max number first
                    List relList = source.getRelationDataList(listElm.getRole(),listElm.getDestination());
                    for (int j = 0; j < relList.size(); j++) {
                        RelationData rel = (RelationData)relList.get(j);
                        int pos = rel.findFieldByName("pos").getIntValue();
                        if (pos>maxpos) {
                            maxpos=pos;
                        }
                    }
                    if (maxpos<0) {
                        maxpos=0;
                    }
                    
                }
                FieldData pos = relation.findFieldByName("pos");
                pos.setValue(String.valueOf(++maxpos));
            }
        }
        source.addRelationData(relationList);
        return true;
    }

    /**
     * create relations by specify destination numbers.
     */
    public List createRelations(Cloud cloud, ObjectData source, RelationElm relationElm, List numberList, Map params)
            throws WizardException {
        
        WizardCloudConnector connector = WizardCloudConnector.getInstance(cloud);
        
        List relationList = new ArrayList();
        for (int i=0;i<numberList.size();i++) {
            String number = (String)numberList.get(i);
            ObjectData relatedObject = null;
            if ("new".equals(number)){
                if (relationElm.object!=null) {
                    relatedObject = connector.loadNew(relationElm.object,params);
                } else {
                    relatedObject = connector.loadNew(relationElm.getDestination());
                }
            } else {
                relatedObject = connector.loadNode(relationElm.object,number);
            }
            RelationData relationData = connector.loadNewRelation(source, relatedObject, relationElm.getRole(),
                    relationElm.getCreateDir());
            relationList.add(relationData);
        }
        return relationList;
    }

    
    /**
     * delete specified item from list in wizard
     * @param fid
     * @param did
     * @return
     * @throws WizardException
     */
    public boolean doDeleteItem(WizardConfig wizardConfig, String fid, String did) throws WizardException{
        WizardSchema schema = wizardConfig.getWizardSchema();
        SchemaElement element = schema.findElementByFid(fid);
        BaseData baseData = DataUtils.findDataById(wizardConfig.wizardData,did);
        
        if (baseData instanceof RelationData == false) {
            throw new WizardException("add-item command is only suitable for relation");
        } 
        if (element !=null)
        {   //element is ItemElm, get its parent--> ListElm
            element = element.getParent();
        }
        if (element instanceof ListElm == false) {
            throw new WizardException("add-item command is only suitable for list");
        }
        RelationData relation = (RelationData)baseData;
        ListElm listElm = (ListElm)element;
        ActionElm actionElm = listElm.getActionByType("delete");
        boolean deleteRelatedObject = false;
        if (actionElm!=null && actionElm.object!=null) {
            deleteRelatedObject = true;
        }
        if (relation.getStatus()==BaseData.STATUS_NEW) {
            relation.getMainObject().removeRelationData(relation.getDid());
        } else {
            relation.setStatus(BaseData.STATUS_DELETE);
        }
        //TODO: if "actin[@type="delete"]/object" exists, recursive to delete object
        // but how to commit to mmbase if the relation is not loaded in system?
        if (deleteRelatedObject==true) {
            relation.getRelatedObject().setStatus(BaseData.STATUS_DELETE);
        }
        
        return true;
    }
    
    /**
     * update the item's data in the list
     * @param numberList 
     * @param did 
     * @param fid 
     * @return
     * @throws WizardException 
     */
    public boolean doUpdateItem(WizardConfig wizardConfig, Cloud cloud, String fid, String did, String number, 
            String origin, String newNumber) throws WizardException{
        //TODO: the update-item is only defined for the scenario that 
        // update the list in main object's wizard after commit a startwizard for change?
        WizardSchema schema = wizardConfig.getWizardSchema();
        SchemaElement element = schema.findElementByFid(fid);
        BaseData baseData = DataUtils.findDataById(wizardConfig.wizardData, did);
        
        if (baseData instanceof ObjectData == false) {
            throw new WizardException("update-item command is only suitable for object");
        } 
        if (element instanceof ListElm == false) {
            throw new WizardException("update-item command is only suitable for list");
        }
        //load related object as item's data
        ObjectData mainObject = (ObjectData)baseData;
        if (mainObject.getNumber().equals(origin)==false) {
            //TODO: the data is not the one need updated.
            throw new WizardException("the object find by did is not the proper object should be updated");
        }
        ListElm listElm = (ListElm)element;
        RelationData relationData = findRelation(listElm,mainObject,number);
        if (relationData == null || relationData.getRelatedObject().getStatus()==BaseData.STATUS_NEW) {
            //update only apply for loaded relations
            throw new WizardException("update-item command is only suitable for loaded related object");
        }
        if ( relationData.getStatus()==BaseData.STATUS_DELETE) {
            log.warn("ignore update-item command because the relation you specified has been deleted");
            return false;
        }
        
        ObjectElm objectElm = null;
        // find schema element which indicate what should be load
        ActionElm actionElm = listElm.getActionByType("load");
        if (actionElm!=null && actionElm.relations.size()>0) {
            // if the load action is defined under list. use the setting.
            objectElm = actionElm.object;
        } else {
            // if action[@type="load"] is not defined under list, 
            // try to find action under wizard-sche
            actionElm = wizardConfig.getWizardSchema().getActionByType("load");
            if (actionElm != null) {
                // if find action[@type="load"] try to find object under relations
                RelationElm relationElm = actionElm.getRelation(listElm.getRole(), 
                        listElm.getDestination());
                if (relationElm != null) {
                    objectElm = relationElm.object;
                }
            }
        }
        WizardCloudConnector connector = WizardCloudConnector.getInstance(cloud);
        // reload the object data and add it to relation
        ObjectData objectData = connector.load(objectElm,newNumber);
        relationData.setRelatedObject(objectData);
        return true;
    }
    
    private RelationData findRelation(ListElm listElm, ObjectData objectData, String number) {
        String role = listElm.getRole();
        String destination = listElm.getDestination();
        List relationList = objectData.getRelationDataList(role,destination);
        for (int i=0;i<relationList.size();i++) {
            RelationData relationData = (RelationData)relationList.get(i);
            if (number.equals(relationData.getRelatedObject().getNumber())) {
                return relationData;
            }
        }
        return null;
    }
    
    /**
     * move the current item
     * @param fid
     * @param did
     * @param otherid change with the item specified by this id
     * @return
     * @throws WizardException
     */
    public boolean doMoveItem(WizardConfig wizardConfig, String fid, String did, String otherid) throws WizardException{
        WizardSchema schema = wizardConfig.getWizardSchema();
        SchemaElement element = schema.findElementByFid(fid);
        BaseData baseData = DataUtils.findDataById(wizardConfig.wizardData,did);
        BaseData otherData = DataUtils.findDataById(wizardConfig.wizardData,otherid);
        
        if ((baseData instanceof RelationData ==false) || 
                (otherData instanceof RelationData == false)) {
            throw new WizardException("this operation is only suitable for relation");
        }
        if (element !=null)
        {   //element is ItemElm, get its parent--> ListElm
            element = element.getParent();
        }
        if (element instanceof ListElm == false) {
            throw new WizardException("add-item command is only suitable for list");
        }
        ListElm listElm = (ListElm)element; 
        
        //swap fields' value between 2 data objects.
        RelationData fromRelation = (RelationData)baseData;
        RelationData toRelation = (RelationData)otherData;
        
        String orderby = listElm.getOrderBy();
        if (orderby==null) {
            if ("posrel".equals(fromRelation.getType())) {
                // if relation's type is posrel, default orderby attribute's value is "pos"
                orderby = "pos";
            } else {
                // no orderby
                log.warn("not specify a valid orderby attribute for list, could not moveitem");
                return false;
            }
        }
        
        FieldData fromField = DataUtils.getField(fromRelation, orderby);
        FieldData toField = DataUtils.getField(toRelation, orderby);
        if (fromField==null || toField==null) {
            log.warn("cannot find field according to given orderby attribute");
        }
        else {
            String fromValue = fromField.getStringValue();
            String toValue = toField.getStringValue();
            fromField.setValue(toValue);
            toField.setValue(fromValue);
        }
        return true;
    }

}
