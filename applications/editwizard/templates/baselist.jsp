<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"
><%@ page import="org.mmbase.bridge.*"
%><%@ page import="java.util.*"
%><%@ page import="org.w3c.dom.Document"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.SecurityException"
%><%
    response.addHeader("Cache-Control","no-cache");
    response.addHeader("Pragma","no-cache");
    response.addHeader("Expires","0");

    String instancename = getParam(null, request,"instancename","editwizard.", false, null);

    boolean fromsession = false;

    boolean popup = getBoolean(getParam(null, request, "popup", "false", false, null));
    if (popup) {
        // this is a popup list viewer. Generate a instancename.
        instancename += new Date().getTime()+".";
    }

    // check if we want to get our values from session....
    if (getParam(null, request,"nodepath",null,false,null)==null) {
        // No nodepath supplied!
        // let's try to get it from cache...
        fromsession = true;
    }

    // params:
    //

    // startnodes, nodepath, fields, contraints, orderby, direction, searchdir, distinct
    // (params of the Cloud.getList method)
    //
    // Assumes that the FIRST field in 'fields' contains the objectnumber of the object the editwizard should use to edit
    // All other fields are used to show in the list as column fields or concatenation fields

    // wizard	name of the wizard to use if a create or edit command is fired
    String wizard = getParam(instancename, request, "wizard", null, fromsession, session);
    String template = getParam(instancename, request, "template", null, fromsession, session);

    // make sure the path starts with a /
    if (template!=null && template.indexOf("/")!=0) template = "/"+template;

    // XXX: new param
    String title  = getParam(instancename, request, "title", null, fromsession, session);

    String startnodes  = getParam(instancename, request, "startnodes", "", fromsession, session);
    String nodepath    = getParam(instancename, request, "nodepath", "", fromsession, session);
    String fields      = getParam(instancename, request, "fields", "", fromsession, session);
    String constraints = getParam(instancename, request, "constraints", null, fromsession, session);
    String orderby     = getParam(instancename, request, "orderby", null, fromsession, session);
    String directions  = getParam(instancename, request, "directions", null, fromsession, session);
    String type        = getParam(instancename, request, "type", null, fromsession, session);
    int age            = Integer.parseInt(getParam(instancename, request, "age", "-1", fromsession, session));
    boolean distinct   = getBoolean(getParam(instancename, request, "distinct", "true", fromsession, session));
    int start          = Integer.parseInt(getParam(instancename, request, "start", "0", fromsession, session));
    int len            = Integer.parseInt(getParam(instancename, request, "len", settings_default_list_pagelength+"", fromsession, session));
    int maxpages       = Integer.parseInt(getParam(instancename, request, "maxpages", settings_list_maxpagecount+"", fromsession, session));

    // decide what kind of query: multilevel or single?
    boolean multilevel = false;
    if (nodepath.indexOf(",")>-1) multilevel = true;
    String originalconstraints = constraints;

    StringTokenizer stok = new StringTokenizer(fields, ",");
    Vector fieldlist = new Vector();
    String token;
    String numberfield=null;
  
    while (stok.hasMoreTokens()) {
		token = stok.nextToken();
		fieldlist.add(token);
		if (token.indexOf(".number")>-1) {
			numberfield = token;
		}
		if (token.indexOf("number")>-1 && numberfield==null) numberfield=token;
	}


    int nodecount=0;

    stok = new StringTokenizer(nodepath, ",");
    nodecount = stok.countTokens();
    String lastobjectname=null;

	while (stok.hasMoreTokens()) {
	   lastobjectname = stok.nextToken();
    }



    if (lastobjectname==null) {
        // redirect to default document of current directory.
        response.sendRedirect("");
        return;
    }




    if (numberfield==null || fields.indexOf(numberfield)==-1) {
        // no numberfield supplied. Let's add it ourselves and place in in front of the fields list.
        String addfield="number";
        if (multilevel) {
            addfield = lastobjectname+"."+addfield;
            fields = addfield + "," + fields;
        } else {
            fields = "number," + fields;
        }
        fieldlist.insertElementAt(addfield,0);
    }

    int fieldcount = fieldlist.size();
    // check syntax of params....
    if (nodecount==0 || fieldcount==0) {
        out.println(showError());
        out.println("<br /><br />No valid nodepath or fields are supplied.");
        return;
    }


    // expand query depending on wether or not an age param is given.

    String ageconstraint = "";
    if (age==99999) age=-1;
    if (age>-1) {
        // maxage is set. pre-query to find objectnumber
        long daymarker = (new Date().getTime() / (60*60*24*1000)) - age;

        NodeManager mgr = cloud.getNodeManager("daymarks");

        NodeList tmplist = mgr.getList("daycount>="+daymarker, null,null);
        if (tmplist.size()<1) {
            // not found. No objects can be found.
            ageconstraint = "number>99999";
        } else {
            Node n = tmplist.getNode(0);
            ageconstraint = "number>"+n.getStringValue("mark");
        }

        if (multilevel && lastobjectname!=null) ageconstraint=lastobjectname+"."+ageconstraint;

        if (constraints==null || constraints.equals("")) constraints = ageconstraint;
        else constraints = "("+constraints+") AND "+ageconstraint;
    }


    boolean deletable = false;
    boolean creatable = false;
    String deletedescription = "";
    String deleteprompt = "";


    if (wizard!=null) {
        // create wizard object so that delete/create actions are correctly loaded. No need to store. We'll create another wizard automatically if a button in the list is pressed.
        Wizard wiz=null;
        wiz = new Wizard(settings_basedir, wizard, null, cloud);
        deletable = (Utils.selectSingleNode(wiz.schema, "/*/action[@type='delete']")!=null);
        creatable = (Utils.selectSingleNode(wiz.schema, "/*/action[@type='create']")!=null);

        deletedescription = Utils.selectSingleNodeText(wiz.schema, "/*/action[@type='delete']/description", null);
        deleteprompt      = Utils.selectSingleNodeText(wiz.schema, "/*/action[@type='delete']/prompt", null);
    }

    // fire query
    NodeList results;

        if (multilevel) {
            results = cloud.getList(startnodes, nodepath, fields, constraints, orderby, directions, "both", distinct);
        } else {
            NodeManager mgr = cloud.getNodeManager(nodepath);
            results = mgr.getList(constraints, orderby, directions);
        }


    if (start>results.size()-1) start = results.size()-1;
    if (start<0) start=0;
    int end=len+start;
    if (end>results.size()) end=results.size();

    // place all objects
    String s = "<list count=\"" + results.size() + "\" />";
    Document doc = Utils.parseXML(s);

    org.w3c.dom.Node docel = doc.getDocumentElement();

    String mainmanager=lastobjectname;
    if (multilevel) {
        if (mainmanager.charAt(mainmanager.length()-1)<='9') mainmanager=mainmanager.substring(0,mainmanager.length()-1);
    }

    if (start>=end) {
        org.w3c.dom.Node obj = addObject(docel, null,null,cloud.getNodeManager(mainmanager).getName());

    } else
    for (int i=start; i<end; i++) {
        Node item = results.getNode(i);
        org.w3c.dom.Node obj = addObject(docel, item.getStringValue((String)fieldlist.elementAt(0)), (i+1)+"",
                                         cloud.getNodeManager(mainmanager).getName());
        for (int j=1; j<fieldlist.size(); j++) {
            String fieldname = (String)fieldlist.elementAt(j);
            String fieldguiname=fieldname;


                if (multilevel) {
                    int period=fieldname.indexOf('.');
                    String nmname=fieldname.substring(0,period);
                    if (nmname.charAt(period-1)<='9') nmname=nmname.substring(0,period-1);
                    fieldguiname=cloud.getNodeManager(nmname).getField(fieldname.substring(period+1)).getGUIName();
                } else {
                        fieldguiname=item.getNodeManager().getField(fieldname).getGUIName();
                }
            addField(obj, fieldguiname, item.getStringValue(fieldname));

        }
    }


    // place page information
    int pagecount = new Double(Math.floor(results.size() / len)).intValue();
    if (results.size() % len>0) pagecount++;
    int currentpage = new Double(Math.floor((start / len))).intValue();

    org.w3c.dom.Node pages = doc.createElement("pages");
    Utils.setAttribute(pages, "count", pagecount+"");
    Utils.setAttribute(pages, "currentpage", currentpage+"");
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
    String url = "list.jsp?instancename="+instancename;

    // set xsl params
    java.util.Hashtable params = new java.util.Hashtable();
    if (wizard!=null) params.put("wizard",wizard);
    params.put("start",String.valueOf(start));
    params.put("len",String.valueOf(len));
    params.put("url", url);
    params.put("deletable", deletable+"");
    params.put("creatable", creatable+"");
    if (deletedescription!=null) params.put("deletedescription", deletedescription);
    if (deleteprompt!=null) params.put("deleteprompt", deleteprompt);
    if (type!=null) params.put("type", type);

    //XXX: new param
    if (title!=null) params.put("title", title);

    // output html by using xsl stylesheet
    if (template==null) template = "/xsl/list.xsl";
    Utils.transformNode(doc, settings_basedir + template, out, params);

    // nothing more to do...

%><%!

    // local private utils
    private String getParam(String instancename, HttpServletRequest req, String paramname, String defaultvalue, boolean fromsession, HttpSession session) {
        String val=defaultvalue;

        if (fromsession && instancename!=null && session!=null) {
            val = (String)session.getAttribute(instancename+paramname);
            if (val==null) val=defaultvalue;
        }

        // check for overrides in the querystring
        String override = req.getParameter(paramname);

        // make sure "" strings are replaced by null's if the defaultvalue is also null (cleans up the querystring)
        if (defaultvalue==null && override!=null && override.length()==0) override=defaultvalue;

        if (val==null && override==null) val=defaultvalue; // no values supplied in session AND querystring. return defaultvalue.
        if (override!=null) val=override; // override given.

        // store in session?
        if (session!=null && instancename!=null) {
            if (val!=null) {
                session.setAttribute(instancename+paramname, val);
            }
        }

        return val;

    }

    private String getQS(String name, String value) {
        if (name==null || value==null) return "";
        return "&"+name+"="+value;
    }

    private String showError() {
        String s = "MMCI returned an error. You probably did not supply the right parameters for the MMCI getList routines.<br /><br />";
        s += "Please fill in the following params:<br />";
        s += "wizard, nodepath, fields<br /><br />";
        s += "and optional:<br />";
        s += "startnodes, constraints, orderby, directions, distinct";
        return s;
    }

    private String showError(String msg) {
        return msg;
    }

    private boolean getBoolean(String s) {
        Boolean b = new Boolean(s);
        return b.booleanValue();
    }

    private String escape(String s) {
        // no escaping needed for now.
        return s;
    }

    private String placeValue(String s) {
        return s;
    }

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
%></mm:cloud>
