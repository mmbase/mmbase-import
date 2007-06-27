<%@ page errorPage="exception.jsp"%>
<%@ include file="settings.jsp"%>
<mm:locale language="<%=ewconfig.language%>">
<mm:cloud method="delegate" jspvar="cloud">
<mm:log jspvar="log">
<%@page import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,javax.servlet.jsp.JspException"%>
<%@ page import="org.w3c.dom.Document"%>

<% 
String path="";
if(request.getParameter("path") != null){
	path=(String) request.getParameter("path");
	session.setAttribute("path",path);
}
%>

<%
    /**
     * list.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: list.jsp,v 1.9 2006-05-09 15:37:06 igeorgijev Exp $
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

if(request.getParameter("metadata") == null)
{/*if we don't begin a new entity
  (like http://localhost:8080/editwizards/jsp/list.jsp?wizard=image&nodepath=images&searchfields=title&fields=title&search=yes&orderby=title&metadata=yes,
   we have to check the previous state in session*/
   if(session.getAttribute("show_metadata_in_list") != null)
   {//try to restore state of this list from session
      listConfig = (Config.ListConfig) session.getAttribute("metalist_mode");
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
boolean creatable = false;
String deletedescription = "";
String deleteprompt = "";
org.w3c.dom.NodeList titles = null;
if (listConfig.wizard != null) {

    Wizard wiz = null;
    wiz = new Wizard(request.getContextPath(), ewconfig.uriResolver, listConfig.wizard, null, cloud);
    deletable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='delete']")!=null);
    creatable = (Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='create']")!=null);

    deletedescription = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='delete']/description", null, cloud);
    deleteprompt = Utils.selectSingleNodeText(wiz.getSchema(), "/*/action[@type='delete']/prompt", null, cloud);
    titles            = Utils.selectNodeList(wiz.getSchema(), "/wizard-schema/title");

}


// fire query
NodeList results = null;

int start = listConfig.start;
int len   = listConfig.pagelength;
int resultsSize;

//// do not list anything if search is forced and no searchvalue given
if (listConfig.search == listConfig.SEARCH_FORCE && listConfig.searchFields != null && "".equals(listConfig.searchValue)) {
    results = cloud.getCloudContext().createNodeList();
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
log.trace("Got " + results.size() + " results");
}


int maxpages   = listConfig.maxpagecount;

if (start > resultsSize - 1) start = resultsSize - 1;
if (start<0) start=0;
int end = len + start;
if (end > resultsSize) end = resultsSize;

// place all objects
String s = "<list count=\"" + resultsSize + "\" />";
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

session.setAttribute("show_metadata_in_list", "false");
String imageName = "";
String sAltText = "";
String sPathPrefix = "../../education/wizards/";
String sURL = "";
if((request.getParameter("metadata") != null) && (request.getParameter("metadata").equals("yes")) )
{
   //We have to find the proper wizard to make the URL for the returning from Metaeditor
   String[][] arrstrContentMetadataConfig = (String[][]) session.getAttribute("content_metadata_names");
   String sWizard = null;
   String sField  = null;
   for(int f = 0; f < arrstrContentMetadataConfig.length; f++)
   {
      if(arrstrContentMetadataConfig[f][2].equals(manager.getName()))
      {
         sWizard = arrstrContentMetadataConfig[f][1];
         sField  = arrstrContentMetadataConfig[f][3];
      }
   }
   session.setAttribute("show_metadata_in_list", "true");
   session.setAttribute("metalist_url", request.getRequestURI() + "?wizard=" + sWizard + "&nodepath=" + manager.getName() + "&searchfields=" + sField + "&fields=" + sField + "&search=yes&orderby=" + sField);
   session.setAttribute("metalist_mode", listConfig);
}


for (int i=0; i < results.size(); i++)
{
    Node item = results.getNode(i);

    org.w3c.dom.Node obj;
    if (listConfig.multilevel) {
        obj = addObject(docel, item.getIntValue(listConfig.mainObjectName+".number"), i+1 + start,
                        mainManager, manager.getGUIName(2));
    } else {
        obj = addObject(docel, item.getNumber(), i+1 + start, mainManager, manager.getGUIName(2));
    }
    for (int j=0; j < listConfig.fieldList.size(); j++) {
        String fieldname = (String)listConfig.fieldList.get(j);

        Field field = null;
        String value = "";
        if (listConfig.multilevel) {
            int period=fieldname.indexOf('.');
            String nmname=fieldname.substring(0,period);
            if (nmname.charAt(period-1)<='9') nmname=nmname.substring(0, period-1);
            field=cloud.getNodeManager(nmname).getField(fieldname.substring(period+1));
        } else {
            field=item.getNodeManager().getField(fieldname);
        }
        if (field.getGUIType().equals("eventtime")) {
           // eventtime is formatted lateron with xslt
           value = item.getStringValue(fieldname);
           if (value.equals("-1")) {
             value = "";
           }
        } else {
          value = item.getStringValue("gui(" + fieldname + ")");
        }
        addField(obj, field.getGUIName(), value , field.getGUIType());
    }
    if(session.getAttribute("show_metadata_in_list") != null 
         && session.getAttribute("show_metadata_in_list").equals("true"))
    {  //If we are showing the metadata also, we have to add the column with the (i)-icon
      %>
      <mm:node number="<%= "" + item.getNumber()%>">
         <%@include file="/education/wizards/whichimage.jsp"%>
      </mm:node>
      <%
      sURL = "<a href='" + sPathPrefix + "metaedit.jsp?number=" + item.getNumber() + "&path= "+session.getAttribute("path")+"' target='text'><img id='img_" + item.getNumber() + "' src='" + sPathPrefix + imageName + "' border='0' title='" + sAltText + "' alt='" + sAltText + "'></a>";
       addField(obj, "metadata", sURL, "string");
    }
    if (listConfig.multilevel) {
        item=item.getNodeValue(listConfig.mainObjectName);
    }
    Utils.setAttribute(obj, "mayedit",     "" + item.mayWrite());
    Utils.setAttribute(obj, "maydelete",   "" + item.mayWrite());
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

for (int i = 0; i<pagecount && i<maxpages; i++) {
    org.w3c.dom.Node pagenode = doc.createElement("page");
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

if(request.getParameter("forbiddelete") == null) {
   params.put("deletable",  deletable+"");
} else {
   params.put("deletable",  "false");
}

params.put("creatable",  creatable+"");
params.put("cloud",  cloud);
params.put("popupid",  popupId);

if (deletedescription!=null) params.put("deletedescription", deletedescription);
if (deleteprompt!=null) params.put("deleteprompt", deleteprompt);
if (listConfig.title == null) {
    params.put("title", manager.getGUIName(2));
}

params.put("username", cloud.getUser().getIdentifier());
params.put("language", cloud.getLocale().getLanguage());
params.put("ew_context", request.getContextPath());
params.put("ew_path", new File(request.getServletPath()).getParentFile().getParent() + "/");


log.trace("Doing the transformation for " + listConfig.template);
Utils.transformNode(doc, listConfig.template, ewconfig.uriResolver, out, params);

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

private org.w3c.dom.Node addField(org.w3c.dom.Node el, String name, String value, String guitype) {
    org.w3c.dom.Node n = el.getOwnerDocument().createElement("field");
    Utils.setAttribute(n, "name", name);
    Utils.setAttribute(n, "guitype", guitype);
    Utils.storeText(n, value);
    el.appendChild(n);
    return n;
}
%></mm:log></mm:cloud></mm:locale>
