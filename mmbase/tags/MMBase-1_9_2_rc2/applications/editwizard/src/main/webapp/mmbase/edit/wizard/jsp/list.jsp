<%@ include file="settings.jsp"%><mm:content type="text/html" expires="0" language="<%=ewconfig.language%>"><mm:cloud method="$loginmethod"  loginpage="login.jsp" jspvar="cloud" sessionname="$loginsessionname"><mm:log jspvar="log"><%@page import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,org.mmbase.util.functions.Parameters,javax.servlet.jsp.JspException"
%><%@ page import="org.w3c.dom.Document"
%><%
    /**
     * list.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id$
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     * @author   Pierre van Rooden
     * @author   Nico Klasens
     * @author   Martijn Houtman
     */

log.trace("list.jsp");


Config.ListConfig listConfig = null; // stores the configuration specific for this list.
Config.SubConfig    top = null;

if (! ewconfig.subObjects.empty()) {
    top  = (Config.SubConfig) ewconfig.subObjects.peek();
    if (! popup) {
        log.debug("This is not a popup");
        if (top instanceof Config.ListConfig) {
            listConfig = (Config.ListConfig) top;
        } else {
            log.debug("not a list on the stack?");
        }

    } else {
        log.debug("this is a popup");
        Stack stack = (Stack) top.popups.get(popupId);
        if (stack == null) {
            log.debug("No configuration found for popup list");
            stack = new Stack();
            top.popups.put(popupId, stack);
            listConfig = null;
        } else {
            if (stack.empty()) {
                log.error("Empty stack?");
            } else {
                listConfig = (Config.ListConfig) stack.peek();
            }
        }
    }
} else {
    log.debug("nothing found on stack");
    if (popup) {
        throw new WizardException("Popup without parent");
    }
}

if (listConfig == null) {
    listConfig = configurator.createList(cloud);
    if (! popup) {
        if (log.isDebugEnabled()) log.trace("putting new list on the stack for list " + listConfig.title);
        ewconfig.subObjects.push(listConfig);
    } else {
        if (log.isDebugEnabled()) log.trace("putting new list in popup map  for list " + listConfig.title);
        Stack stack = (Stack) top.popups.get(popupId);
        stack.push(listConfig);
    }
}

configurator.config(listConfig); // configure the thing, that means, look at the parameters.

if (listConfig.age > -1) {
    // maxlistConfig.age is set. pre-query to find objectnumber
    long daymarker = (new java.util.Date().getTime() / (60*60*24*1000)) - listConfig.age;

    NodeManager mgr = cloud.getNodeManager("daymarks");

    NodeList tmplist = mgr.getList("daycount>="+daymarker, null,null);
    String ageconstraint = "";
    if (tmplist.size()<1) {
        // not found. No objects can be found.
        ageconstraint = "number>99999";
    } else {
        Node n = tmplist.getNode(0);
        ageconstraint = "number>"+n.getStringValue("mark");
    }

    if (listConfig.multilevel) ageconstraint=listConfig.mainObjectName+"."+ageconstraint;

    if (listConfig.constraints == null || listConfig.constraints.equals("")) {
        listConfig.constraints = ageconstraint;
    } else {
        listConfig.constraints = "(" + listConfig.constraints+") AND " + ageconstraint;
    }
}


boolean deletable = false;
boolean linkable = false;
boolean unlinkable = false;
boolean creatable = false;
String deletedescription = "";
String unlinkdescription = "";
String deleteprompt = "";
String unlinkprompt = "";
String createprompt = "";
org.w3c.dom.NodeList titles = null;
if (listConfig.wizard != null) {

    Wizard wiz = null;
    wiz = new Wizard(request, ewconfig.uriResolver, listConfig.wizard, null, cloud);
    deletable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='delete']")!=null);
    linkable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='link']")!=null);
    unlinkable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='unlink']")!=null);
    creatable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='create']")!=null);

    deletedescription = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='delete']/description", null, cloud);
    unlinkdescription = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='unlink']/description", null, cloud);
    deleteprompt = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='delete']/prompt", null, cloud);
    unlinkprompt = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='unlink']/prompt", null, cloud);
    createprompt = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='create']/prompt", null, cloud);
    titles            = Utils.selectNodeList(wiz.getSchema(), "/wizard-schema/title");

}


// fire query
NodeList results = null;

int start = listConfig.start;
int len   = listConfig.pagelength;
int resultsSize;

//// do not list anything if search is forced and no searchvalue given
if (listConfig.search == Config.ListConfig.SEARCH_FORCE && listConfig.searchFields != null && "".equals(listConfig.searchValue)) {
    results = cloud.createNodeList();
    resultsSize = 0;
} else if (listConfig.multilevel) {
    log.trace("this is a multilevel");
    Query query = cloud.createQuery();

    Queries.addPath(query, listConfig.nodePath, listConfig.searchDir); // also possible to specify more than one searchDir
    Queries.addSortOrders(query, listConfig.orderBy, listConfig.directions);
    Queries.addFields(query, listConfig.fields);
    Queries.addStartNodes(query, listConfig.startNodes);
    Queries.addConstraints(query, listConfig.constraints);
    query.setDistinct(listConfig.distinct);

    query.setMaxNumber(len);
    query.setOffset(start );

    results = cloud.getList(query);
    resultsSize = Queries.count(query);
} else {
    log.trace("This is not a multilevel. Getting nodes from type " + listConfig.nodePath);
    NodeManager mgr = cloud.getNodeManager(listConfig.nodePath);
    if (log.isDebugEnabled()) {
       log.trace("directions: " + listConfig.directions);
    }
    NodeQuery q = mgr.createQuery();
    Queries.addConstraints(q, listConfig.constraints);
    Queries.addSortOrders(q, listConfig.orderBy, listConfig.directions);

    q.setMaxNumber(len);
    q.setOffset(start );

    results = mgr.getList(q);
    resultsSize = Queries.count(q);
}


if (log.isDebugEnabled()) {
log.trace("Got " + results.size() + " of " + resultsSize + " results");
}


int maxpages   = listConfig.maxpagecount;

if (start > resultsSize - 1) start = resultsSize - 1;
if (start<0) start=0;
int end = len + start;
if (end > resultsSize) end = resultsSize;

// place all objects
String s = "<list offsetstart=\"" + (start + 1) + "\" offsetend=\"" + (start + results.size()) + "\" count=\"" + results.size() + "\" totalcount=\"" + resultsSize + "\" />";
Document doc = Utils.parseXML(s);

log.trace("Create document");
org.w3c.dom.Node docel = doc.getDocumentElement();

if (titles != null) {
   Document owner = docel.getOwnerDocument();
   for (int i = 0; i < titles.getLength(); i++) {
       docel.appendChild(owner.importNode(titles.item(i),  true));
   }
}


String mainManager=listConfig.mainObjectName;
if (mainManager.charAt(mainManager.length()-1)<='9') mainManager=mainManager.substring(0,mainManager.length()-1);

NodeManager manager=cloud.getNodeManager(mainManager);
if (!manager.mayCreateNode()) creatable=false;

// relationmanager test for searchlists
String roleStr =(String) listConfig.getAttributes().get("relationRole");
String originNodeNr = (String) listConfig.getAttributes().get("relationOriginNode");
String createDir  = (String) listConfig.getAttributes().get("relationCreateDir");
if ( originNodeNr == null || "".equals(originNodeNr)) {
   originNodeNr = (String) listConfig.getAttributes().get("origin");
}
boolean checkRelationRights = originNodeNr != null && !"".equals(originNodeNr) && roleStr != null && !"".equals(roleStr);
Node originNode = null;
RelationManager relationManager = null;
boolean checkDestination = true;
boolean checkSource = true;
if (checkRelationRights) {
    try {
        originNode = cloud.getNode(originNodeNr);
        relationManager = cloud.getRelationManager(roleStr);
        if (createDir != null) {
            checkDestination = !createDir.equals("source"); // destination or both
            checkSource = !createDir.equals("destination"); // source or both
        }
    } catch (NotFoundException nfe) {
        log.error("error:" + nfe);
        checkRelationRights = false;
    }
}

// newform

for (int i=0; i < results.size(); i++) {

    Node item = results.getNode(i);
    org.w3c.dom.Node obj;
    if (listConfig.multilevel) {
        obj = addObject(docel, item.getIntValue(listConfig.mainObjectName+".number"), i+1 + start,
                        mainManager, manager.getGUIName(2));
    } else {
        obj = addObject(docel, item.getNumber(), i+1 + start, mainManager, manager.getGUIName(2));
    }

    for (int j=0; j < listConfig.fieldList.size(); j++) {
        String fieldName = (String)listConfig.fieldList.get(j);

        Field field = null;
        String value = "";
        if (listConfig.multilevel) {
            int period = fieldName.indexOf('.');
            String nmname = fieldName.substring(0, period);
            if (nmname.charAt(period-1)<='9') nmname = nmname.substring(0, period - 1);
            field = cloud.getNodeManager(nmname).getField(fieldName.substring(period + 1));
        } else {
            field = item.getNodeManager().getField(fieldName);
        }
        if (field.getGUIType().equals("eventtime")) {
           // eventtime is formatted lateron with xslt
           value = "" + item.getIntValue(fieldName);
           if (value.equals("-1")) {
             value = "";
           }
        } else {
            Locale locale = new Locale(ewconfig.language);
            Parameters args = item.createParameters("gui");
            args.set("field",    fieldName);
            args.set("language", locale.getLanguage());
            args.set("locale",   locale);
            args.set("response", pageContext.getResponse());
            args.set("request",  pageContext.getRequest());
            value = item.getFunctionValue("gui", args).toString();
            //value = item.getStringValue("gui(" + fieldName + ")");
        }
        addField(obj, field.getGUIName(), fieldName, value, field.getGUIType());
    }
    if (listConfig.multilevel) {
        item = item.getNodeValue(listConfig.mainObjectName);
    }
    if (checkRelationRights) {
        boolean toDestination = checkDestination &&
                                relationManager.mayCreateRelation(originNode, item);
        boolean toSource = checkSource &&
                           relationManager.mayCreateRelation(item, originNode);
        Utils.setAttribute(obj, "maylink", "" + (toSource || toDestination));
    } else {
        Utils.setAttribute(obj, "maylink", "true");
    }
    if (popupId == null) { // not a searchlist
    if (originNodeNr == null) {
        Utils.setAttribute(obj, "mayunlink", "false");
    }  else {
        RelationList rels = SearchUtil.findRelations(item, cloud.getNode(originNodeNr), roleStr, createDir);
        if (rels.size() == 1) {
           Utils.setAttribute(obj, "mayunlink", "" + rels.getRelation(0).mayDelete());
        } else {
           Utils.setAttribute(obj, "mayunlink", "false");
        }

    }
    }

    Utils.setAttribute(obj, "mayedit", "" + item.mayWrite());
    Utils.setAttribute(obj, "maydelete", "" + item.mayDelete());
}

// place page information
int pagecount = new Double(Math.floor(resultsSize / len)).intValue();
if (resultsSize % len>0) pagecount++;
int currentpage = new Double(Math.floor((start / len))).intValue();

org.w3c.dom.Node pages = doc.createElement("pages");
Utils.setAttribute(pages, "count", pagecount+"");
Utils.setAttribute(pages, "currentpage", (currentpage+1) + "");
docel.appendChild(pages);

if (pagecount>maxpages) {
    Utils.setAttribute(pages, "showing", maxpages + "");
}

int pageOffset = 0;
currentpage = start / len;
pageOffset = currentpage - (maxpages / 2);
if (pageOffset < 0) {
  pageOffset = 0;
}
if (pageOffset + maxpages > pagecount) {
    pageOffset = pagecount - maxpages;
    if (pageOffset < 0) {
      pageOffset = 0;
    }
}

for (int i = pageOffset; i<pagecount && i - pageOffset <maxpages; i++) {
    org.w3c.dom.Node pagenode = doc.createElement("page");
    Utils.setAttribute(pagenode, "number", (i+1)+"");
    Utils.setAttribute(pagenode, "start", (i*len)+"");
    Utils.setAttribute(pagenode, "current", (i==currentpage)+"");
    Utils.setAttribute(pagenode, "previous", (i==currentpage-1)+"");
    Utils.setAttribute(pagenode, "next",  (i==currentpage+1)+"");
    pages.appendChild(pagenode);
}

java.util.Map params = listConfig.getAttributes();


params.put("start",      String.valueOf(start));
params.put("referrer",   ewconfig.backPage);
params.put("referrer_encoded", java.net.URLEncoder.encode(ewconfig.backPage));
if (ewconfig.templates != null) params.put("templatedir",  ewconfig.templates);
params.put("len",        String.valueOf(len));
params.put("sessionkey", ewconfig.sessionKey);
params.put("sessionid",  ewconfig.sessionId);
params.put("deletable",  deletable+"");
params.put("unlinkable",  unlinkable +"");
params.put("linkable",  linkable +"");
params.put("creatable",  creatable+"");
params.put("cloud",  cloud);
params.put("popupid",  popupId);

if (roleStr != null) params.put("relationRole",  roleStr);
if (originNodeNr != null) params.put("relationOriginNode",  originNodeNr);
if (createDir != null) params.put("relationCreateDir",  createDir);

if (deletedescription!=null) params.put("deletedescription", deletedescription);
if (unlinkdescription!=null) params.put("unlinkdescription", unlinkdescription);
if (deleteprompt!=null) params.put("deleteprompt", deleteprompt);
if (unlinkprompt!=null) params.put("unlinkprompt", unlinkprompt);
if (createprompt!=null) params.put("createprompt", createprompt);
if (listConfig.title == null) {
    params.put("title", manager.getGUIName(2));
}

params.put("username", cloud.getUser().getIdentifier());
params.put("language", cloud.getLocale().getLanguage());
params.put("ew_context", request.getContextPath());
params.put("ew_path",  new java.net.URL(pageContext.getServletContext().getResource(request.getServletPath()), "."));


log.trace("Doing the transformation for " + listConfig.template);
Utils.transformNode(doc, listConfig.template, ewconfig.uriResolver, out, params, cloud);

if (log.isDebugEnabled()) log.trace("ready: " + ewconfig.subObjects);

%><%!

private org.w3c.dom.Node addObject(org.w3c.dom.Node el, int number, int index, String type, String guitype) {
    org.w3c.dom.Node n = el.getOwnerDocument().createElement("object");
    Utils.setAttribute(n, "number", ""+number);
    Utils.setAttribute(n, "index", ""+index);
    Utils.setAttribute(n, "type", type);
    Utils.setAttribute(n, "guitype", guitype);
    el.appendChild(n);
    return n;

}

private org.w3c.dom.Node addField(org.w3c.dom.Node el, String name, String fieldName, String value, String guitype) {
    org.w3c.dom.Node n = el.getOwnerDocument().createElement("field");
    Utils.setAttribute(n, "name", name);
    Utils.setAttribute(n, "fieldname", fieldName);
    Utils.setAttribute(n, "guitype", guitype);
    Utils.storeText(n, value);
    el.appendChild(n);
    return n;
}
%></mm:log></mm:cloud></mm:content>
