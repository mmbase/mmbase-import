<%@ page errorPage="exception.jsp"
%><%@ include file="settings.jsp"%><mm:cloud method="http" jspvar="cloud"><mm:log jspvar="log"><%@page import="org.mmbase.bridge.*,javax.servlet.jsp.JspException"
%><%@ page import="org.w3c.dom.Document"
%><%
    /**
     * list.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: list.jsp,v 1.8 2002-05-17 07:43:50 pierre Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */
    log.trace("list.jsp");

Config.ListConfig listConfig; // stores the configuration specific for this list.

if ("true".equals(request.getParameter("popup"))) {
    log.trace("This is a popup list viewer. Don't  push on Stack.");
    listConfig = configurator.createList();
} else {
    if (ewconfig.subObjects.size() > 0) {
        listConfig = (Config.ListConfig) ewconfig.subObjects.peek();
    } else {
        listConfig = configurator.createList();
        log.trace("List, push on Stack.");
        ewconfig.subObjects.push(listConfig);
    }
}


configurator.config(listConfig); // configure the thing, that means, look to the parameters.

// decide what kind of query: multilevel or single?
boolean multilevel = listConfig.nodePath.indexOf(",") > -1;

List fieldList     = new Vector();
String numberField = null;

StringTokenizer stok = new StringTokenizer(listConfig.fields, ",");
String mainObjectName =null;
while (stok.hasMoreTokens()) {
    String token = stok.nextToken();
    fieldList.add(token);
    int nrpos=token.indexOf(".number");
    if (nrpos > -1) {
        numberField = token;
        mainObjectName = token.substring(0,nrpos);
    }
    if (token.indexOf("number") > -1 && numberField == null) numberField=token;
}

int nodecount=0;

stok = new StringTokenizer(listConfig.nodePath, ",");
nodecount = stok.countTokens();
String lastObjectName=null;

while (stok.hasMoreTokens()) {
    lastObjectName = stok.nextToken();
}

if (lastObjectName == null) {
    throw new JspException("No nodepath (" + listConfig.nodePath + ") was specified on URL, nor could it be found in the session");
}
if (mainObjectName==null) mainObjectName=lastObjectName;

if (numberField==null || listConfig.fields.indexOf(numberField)==-1) {
    // no numberField supplied. Let's add it ourselves and place in in front of the fields list.
    String addfield="number";
    if (multilevel) {
        addfield = mainObjectName+"."+addfield;
        listConfig.fields = addfield + "," + listConfig.fields;
    } else {
        listConfig.fields = "number," + listConfig.fields;
    }
    fieldList.add(0, addfield);
}


int fieldcount = fieldList.size();
// check syntax of params....
if (nodecount==0 || fieldcount==0) {
    String s = "MMCI returned an error. You probably did not supply the right parameters for the MMCI getList routines.<br /><br />";
    s += "Please fill in the following params:<br />";
    s += "wizard, nodepath, fields<br /><br />";
    s += "and optional:<br />";
    s += "startnodes, constraints, orderby, directions, distinct";
    throw new JspException(s + "<br /><br />No valid nodePath or fields are supplied.");
}


// expand query depending on wether or not an age param is given.
if (listConfig.age==99999) listConfig.age=-1;
if (listConfig.age >- 1) {
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

    if (multilevel && mainObjectName!=null) ageconstraint=mainObjectName+"."+ageconstraint;

    if (listConfig.constraints == null || listConfig.constraints.equals("")) {
        listConfig.constraints = ageconstraint;
    } else {
        listConfig.constraints = "(" + listConfig.constraints+") AND " + ageconstraint;
    }
}


boolean deletable = false;
boolean creatable = false;
String deletedescription = "";
String deleteprompt = "";
String title = "editwizard list";

if (ewconfig.wizard != null) {

    log.trace("Create wizard object so that delete/create actions are correctly loaded. No need to store. We'll create another wizard automatically if a button in the list is pressed.");
    Wizard wiz = null;
    wiz = new Wizard(request.getContextPath(), ewconfig.uriResolver, ewconfig.wizard, null, cloud);
    deletable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='delete']")!=null);
    creatable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='create']")!=null);
    deletedescription = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='delete']/description", null);
    deleteprompt      = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='delete']/prompt", null);
    title             = Utils.selectSingleNodeText(wiz.getSchema(), "/wizard-schema/title", null);

}

// fire query
NodeList results;

if (multilevel) {
    log.trace("this is a multilevel");
    results = cloud.getList(listConfig.startNodes, listConfig.nodePath, listConfig.fields, listConfig.constraints,
                            listConfig.orderBy,
                            listConfig.directions, "both",
                            listConfig.distinct);
} else {
    log.trace("This is not a multilevel. Getting nodes from type " + listConfig.nodePath);
    NodeManager mgr = cloud.getNodeManager(listConfig.nodePath);
    log.trace("directions: " + listConfig.directions);
    results = mgr.getList(listConfig.constraints, listConfig.orderBy, listConfig.directions);
}

log.trace("Got " + results.size() + " results");

int start = listConfig.start;
int len        = ewconfig.list_pagelength;
int maxpages   = ewconfig.list_maxpagecount;

if (start>results.size()-1) start = results.size()-1;
if (start<0) start=0;
int end = len+start;
if (end > results.size()) end = results.size();

// place all objects
String s = "<list count=\"" + results.size() + "\" />";
Document doc = Utils.parseXML(s);

log.trace("Create document");

org.w3c.dom.Node docel = doc.getDocumentElement();

String mainManager=mainObjectName;
if (mainManager.charAt(mainManager.length()-1)<='9') mainManager=mainManager.substring(0,mainManager.length()-1);

NodeManager manager=cloud.getNodeManager(mainManager);
if (!manager.mayCreateNode()) creatable=false;

for (int i=start; i< end; i++) {
    Node item = results.getNode(i);
    org.w3c.dom.Node obj = addObject(docel, item.getStringValue((String)fieldList.get(0)), (i+1)+"",
                                     manager.getName());
    for (int j=1; j < fieldList.size(); j++) {
        String fieldname = (String)fieldList.get(j);
        String fieldguiname=fieldname;

        if (multilevel) {
            int period=fieldname.indexOf('.');
            String nmname=fieldname.substring(0,period);
            if (nmname.charAt(period-1)<='9') nmname=nmname.substring(0, period-1);
            fieldguiname=cloud.getNodeManager(nmname).getField(fieldname.substring(period+1)).getGUIName();
        } else {
            fieldguiname=item.getNodeManager().getField(fieldname).getGUIName();
        }
        addField(obj, fieldguiname, item.getStringValue(fieldname));
    }
    if (multilevel) {
        item=item.getNodeValue(mainObjectName);
    }
    Utils.setAttribute(obj, "mayedit", ""+item.mayWrite());
    Utils.setAttribute(obj, "maydelete", ""+item.mayDelete());
}


// place pge information
int pagecount = new Double(Math.floor(results.size() / len)).intValue();
if (results.size() % len>0) pagecount++;
int currentpage = new Double(Math.floor((start / len))).intValue();

org.w3c.dom.Node pages = doc.createElement("pages");
Utils.setAttribute(pages, "count", pagecount+"");
Utils.setAttribute(pages, "currentpage", currentpage+ "");
docel.appendChild(pages);

if (pagecount>maxpages) {
    Utils.setAttribute(pages, "showing", maxpages+"");
}

for (int i=0; i<pagecount && i<maxpages; i++) {
    org.w3c.dom.Node pagenode = doc.createElement("page");
    Utils.setAttribute(pagenode, "start", (i*len)+"");
    Utils.setAttribute(pagenode, "current", (i==currentpage)+"");
    Utils.setAttribute(pagenode, "previous", (i==currentpage-1)+"");
    Utils.setAttribute(pagenode, "next",  (i==currentpage+1)+"");
    pages.appendChild(pagenode);
}
String url = response.encodeURL("list.jsp?instanceName=" + ewconfig.sessionKey);

log.trace("Setting xsl parameters");
java.util.Map params = new java.util.Hashtable();
if (ewconfig.wizard != null) params.put("wizard", ewconfig.wizard);
params.put("start",String.valueOf(start));
params.put("len",String.valueOf(len));
params.put("url", url);
params.put("sessionid", ewconfig.sessionId);
params.put("deletable", deletable+"");
params.put("creatable", creatable+"");

if (deletedescription!=null) params.put("deletedescription", deletedescription);
if (deleteprompt!=null) params.put("deleteprompt", deleteprompt);
//if (settings_ewconfig.get("type") != null) params.put("type", settings_ewconfig.get("type"));
//XXX: new param1
if (title != null) params.put("wizardtitle", title);

params.put("ew_context", request.getContextPath());
params.put("ew_imgdb",   org.mmbase.module.builders.AbstractImages.getImageServletPath(request.getContextPath()));

log.trace("Doing the transformation for " + listConfig.template);
Utils.transformNode(doc, listConfig.template, ewconfig.uriResolver, out, params);

log.trace("ready");

%><%!

private org.w3c.dom.Node addObject(org.w3c.dom.Node el, String number, String index, String type) {
    org.w3c.dom.Node n = el.getOwnerDocument().createElement("object");
    Utils.setAttribute(n, "number", number);
    Utils.setAttribute(n, "index", index);
    Utils.setAttribute(n, "type", type);
    el.appendChild(n);
    return n;

}

private org.w3c.dom.Node addField(org.w3c.dom.Node el, String name, String value) {
    org.w3c.dom.Node n = el.getOwnerDocument().createElement("field");
    Utils.setAttribute(n, "name", name);
    Utils.storeText(n, value);
    el.appendChild(n);
    return n;
}
%></mm:log></mm:cloud>
