/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.action;

import java.util.HashMap;
import java.util.Map;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.schema.ActionElm;
import org.mmbase.applications.editwizard.schema.WizardSchema;
import org.mmbase.applications.editwizard.session.ListConfig;
import org.mmbase.applications.editwizard.util.XmlUtil;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;


/**
 * @todo javadoc
 * 
 * @author caicai
 * @created 2005-8-11
 * @version $Id: ListWorkspace.java,v 1.2 2005-12-11 11:51:04 nklasens Exp $
 */
public class ListWorkspace{
    
    public static final String ELEM_OBJECT = "object";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_INDEX = "index";
    public static final String ATTR_GUITYPE = "guitype";
    public static final String ATTR_FIELDNAME = "fieldname";
    public static final String ATTR_NAME = "name";
    public static final String ELEM_FIELD = "field";
    public static final String ELEM_PAGES = "pages";
    public static final String ATTR_COUNT = "count";
    public static final String ATTR_CURRENTPAGE = "currentpage";
    public static final String ATTR_SHOWING = "showing";
    public static final String ATTR_NEXT = "next";
    public static final String ATTR_PREVIOUS = "previous";
    public static final String ATTR_CURRENT = "current";
    public static final String ATTR_START = "start";
    public static final String ATTR_NUMBER = "number";
    public static final String ELEM_TITLE = "title";
    
    private static final Logger log = Logging.getLoggerInstance(ListWorkspace.class);
    
    private ListConfig listConfig = null;

    private SearchData listData = null;
    private boolean deletable = false;
    private boolean creatable = false;
    private String deletedescription = null;
    private String deleteprompt = null;
    private String createprompt = null;
    private String title = null;

    /**
     * hidden construction method
     */
    public ListWorkspace(ListConfig config) {
        listConfig = config;
    }
    
    public void doSearch(Cloud cloud) {
        if (listConfig.getAge() > -1) {
            // maxlistConfig.getAge() is set. pre-query to find objectnumber
            long daymarker = (new java.util.Date().getTime() / (60 * 60 * 24 * 1000))
                    - listConfig.getAge();

            NodeManager mgr = cloud.getNodeManager("daymarks");

            NodeList tmplist = mgr.getList("daycount>=" + daymarker, null, null);
            String ageconstraint = "";
            if (tmplist.size() < 1) {
                // not found. No objects can be found.
                ageconstraint = "number>99999";
            }
            else {
                Node n = tmplist.getNode(0);
                ageconstraint = "number>" + n.getStringValue("mark");
            }

            if (listConfig.isMultilevel())
                ageconstraint = listConfig.getMainObjectName() + "." + ageconstraint;

            if (listConfig.getConstraints() == null || listConfig.getConstraints().equals("")) {
                listConfig.setConstraints(ageconstraint);
            }
            else {
                listConfig.setConstraints("(" + listConfig.getConstraints() + ") AND " + ageconstraint);
            }
        }

        if (listConfig.getWizardName()!=null) {
            WizardSchema schema = listConfig.getWizardSchema();
            ActionElm deleteAction = schema.getActionByType("delete");
            deletable = deleteAction!=null; 
            if (deletable) {
                deletedescription = deleteAction.description.getTextValue();
                deleteprompt = deleteAction.prompt.getTextValue();
            }
            ActionElm createAction = schema.getActionByType("create"); 
            creatable = createAction!=null;
            if (creatable) {
                createprompt = createAction.prompt.getTextValue();
            }
        }

        NodeManager manager = cloud.getNodeManager(listConfig.getMainObjectName());
        if (!manager.mayCreateNode())
            creatable = false;
        
        if (listConfig.getTitle() == null) {
            title = manager.getGUIName(2);
        }
        
        listData = search(listConfig, cloud);
    }
    
    public Document getDocument(Cloud cloud) throws WizardException {
        doSearch(cloud);

        WizardSchema schema = listConfig.getWizardSchema();
        int start = listData.getStart();
        int resultSize = listData.getResultSize();
        int totalResultSize = listData.getTotalResultsSize();

        // place all objects
        String s = "<list offsetstart=\"" + (start + 1) + "\" offsetend=\"" + (start + resultSize)
                + "\" count=\"" + resultSize + "\" totalcount=\"" + totalResultSize + "\" />";
        Document listDoc = XmlUtil.parseXML(s);
        log.trace("Create document");
        org.w3c.dom.Node rootElement = listDoc.getDocumentElement();

        if (schema!=null) {
            DOMUtils.copyToNode(rootElement,schema.titles,ELEM_TITLE);
        }
        
        String mainManager = listConfig.getMainObjectName();

        NodeManager manager = cloud.getNodeManager(mainManager);

        for (int i = 0; i < listData.getResultSize(); i++) {
            Node item = listData.getResultNode(i);
            org.w3c.dom.Node obj;
            if (listConfig.isMultilevel()) {
                obj = addObject(rootElement, item
                        .getIntValue(listConfig.getMainObjectName() + ".number"), i + 1 + start,
                        mainManager, manager.getGUIName(2));
            }
            else {
                obj = addObject(rootElement, item.getNumber(), i + 1 + start, mainManager, manager
                        .getGUIName(2));
            }
            for (int j = 0; j < listConfig.getFieldList().size(); j++) {
                String fieldname = (String) listConfig.getFieldList().get(j);

                Field field = null;
                String value = "";
                if (listConfig.isMultilevel()) {
                    int period = fieldname.indexOf('.');
                    String nmname = fieldname.substring(0, period);
                    if (nmname.charAt(period - 1) <= '9')
                        nmname = nmname.substring(0, period - 1);
                    field = cloud.getNodeManager(nmname).getField(fieldname.substring(period + 1));
                }
                else {
                    field = item.getNodeManager().getField(fieldname);
                }
                if ("eventtime".equals(field.getDataType().getName())) {
                    // eventtime is formatted lateron with xslt
                    value = "" + item.getIntValue(fieldname);
                    if (value.equals("-1")) {
                        value = "";
                    }
                }
                else {

                    value = item.getStringValue("gui(" + fieldname + ")");
                }
                addField(obj, field.getGUIName(), fieldname, value, field.getDataType().getName());
            }
            if (listConfig.isMultilevel()) {
                item = item.getNodeValue(listConfig.getMainObjectName());
            }
            XmlUtil.setAttribute(obj, "mayedit", "" + item.mayWrite());
            XmlUtil.setAttribute(obj, "maydelete", "" + item.mayDelete());
        }

        // place page information
        int pagecount = listData.getPageCount();
        int currentpage = listData.getCurrentPage();
        int pageOffset = listData.getPageOffset();

        int maxpagessize = listConfig.getMaxpagecount();

        org.w3c.dom.Node pages = listDoc.createElement(ELEM_PAGES);
        XmlUtil.setAttribute(pages, ATTR_COUNT, pagecount + "");
        XmlUtil.setAttribute(pages, ATTR_CURRENTPAGE, (currentpage + 1) + "");
        rootElement.appendChild(pages);

        if (pagecount > maxpagessize) {
            XmlUtil.setAttribute(pages, ATTR_SHOWING, maxpagessize + "");
        }

        for (int i = pageOffset; i < pagecount && i - pageOffset < maxpagessize; i++) {
            org.w3c.dom.Node pagenode = listDoc.createElement("page");
            XmlUtil.setAttribute(pagenode, ATTR_NUMBER, (i + 1) + "");
            XmlUtil.setAttribute(pagenode, ATTR_START, (i * maxpagessize) + "");
            XmlUtil.setAttribute(pagenode, ATTR_CURRENT, (i == currentpage) + "");
            XmlUtil.setAttribute(pagenode, ATTR_PREVIOUS, (i == currentpage - 1) + "");
            XmlUtil.setAttribute(pagenode, ATTR_NEXT, (i == currentpage + 1) + "");
            pages.appendChild(pagenode);
        }
        
        return listDoc;
        //clear the parameters
    }
    
    private static org.w3c.dom.Node addField(org.w3c.dom.Node objNode, String name, String fieldName, String value,
            String guitype) {
        org.w3c.dom.Node fieldNode = objNode.getOwnerDocument().createElement(ELEM_FIELD);
        XmlUtil.setAttribute(fieldNode, ATTR_NAME, name);
        XmlUtil.setAttribute(fieldNode, ATTR_FIELDNAME, fieldName);
        XmlUtil.setAttribute(fieldNode, ATTR_GUITYPE, guitype);
        XmlUtil.storeText(fieldNode, value);
        objNode.appendChild(fieldNode);
        return fieldNode;
    }

    private static org.w3c.dom.Node addObject(org.w3c.dom.Node listNode, int number, int index, String type, String guitype) {
        org.w3c.dom.Node objNode = listNode.getOwnerDocument().createElement(ELEM_OBJECT);
        XmlUtil.setAttribute(objNode, ATTR_NUMBER, "" + number);
        XmlUtil.setAttribute(objNode, ATTR_INDEX, "" + index);
        XmlUtil.setAttribute(objNode, ATTR_TYPE, type);
        XmlUtil.setAttribute(objNode, ATTR_GUITYPE, guitype);
        listNode.appendChild(objNode);
        return objNode;

    }

    public Map getParams() {
        Map params = new HashMap();
        params.putAll(listConfig.getAttributes());

        params.put("start",      listData.getStart()+"");
        params.put("deletable",  deletable+"");
        params.put("creatable",  creatable+"");
        params.put("len", listConfig.getMaxpagecount()+"");
        params.put("deletable", deletable+"");
        params.put("creatable",  creatable+"");

        if (deletedescription != null)
            params.put("deletedescription", deletedescription);
        if (deleteprompt != null)
            params.put("deleteprompt", deleteprompt);
        if (createprompt != null)
            params.put("createprompt", createprompt);
        if (listConfig.getTitle() == null) {
            params.put("title", title);
        }
        return params;
    }

    /**
     * create list
     * @param listConfig
     * @return
     */
    public SearchData search(ListConfig listConfig, Cloud cloud) {
        
        SearchData listData = new SearchData();
        
        // fire query
        NodeList results = null;

        int start = listConfig.getStart();
        int len = listConfig.getPagelength();
        int totalResultsSize;
        
        // // do not list anything if search is forced and no searchvalue given
        if (listConfig.getSearch() == ListConfig.SEARCH_FORCE 
                && listConfig.getSearchFields() != null
                && "".equals(listConfig.getSearchValue())) {
            results = cloud.getCloudContext().createNodeList();
            totalResultsSize = 0;
        }
        else
            if (listConfig.isMultilevel()) {
                log.trace("this is a multilevel");
                Query query = cloud.createQuery();

                // also possible to specify more than one searchDir
                Queries.addPath(query, listConfig.getNodePath(), listConfig.getSearchDir()); 
                Queries.addSortOrders(query, listConfig.getOrderBy(), listConfig.getDirections());
                Queries.addFields(query, listConfig.getFields());
                Queries.addStartNodes(query, listConfig.getStartNodes());
                Queries.addConstraints(query, listConfig.getConstraints());
                query.setDistinct(listConfig.isDistinct());

                query.setMaxNumber(len);
                query.setOffset(start);

                results = cloud.getList(query);
                totalResultsSize = Queries.count(query);
            }
            else {
                log.trace("This is not a multilevel. Getting nodes from type "
                        + listConfig.getNodePath());
                NodeManager mgr = cloud.getNodeManager(listConfig.getNodePath());
                if (log.isDebugEnabled()) {
                    log.trace("directions: " + listConfig.getDirections());
                }
                NodeQuery q = mgr.createQuery();
                Queries.addConstraints(q, listConfig.getConstraints());
                Queries.addSortOrders(q, listConfig.getOrderBy(), listConfig.getDirections());

                q.setMaxNumber(len);
                q.setOffset(start);

                results = mgr.getList(q);
                totalResultsSize = Queries.count(q);
            }

        if (log.isDebugEnabled()) {
            log.trace("Got " + results.size() + " of " + totalResultsSize + " results");
        }
        
        listData.initStart = listConfig.getStart();
        listData.totalResultsSize = totalResultsSize;
        listData.pageMaxSize = len;
        listData.listResults = results;
        
        return listData;
        
    }

}
