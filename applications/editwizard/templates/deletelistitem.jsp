<%@ include file="settings.jsp"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"
><%@ page import="org.mmbase.bridge.*"
%><%@ page import="java.util.*"
%><%@ page import="org.w3c.dom.Document"
%><%@ page import="org.w3c.dom.Node"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%
    response.addHeader("Cache-Control","no-cache");
    response.addHeader("Pragma","no-cache");
    response.addHeader("Expires","0");

    // wizard	name of the wizard to use if a create or edit command is fired
    String instancename = getParam(null, request,"instancename","editwizard.", false, null);
    String wizard = getParam(instancename, request, "wizard", null, false, null);
    String objectnumber  = getParam(instancename, request, "objectnumber", null, false, null);

    if (wizard==null || objectnumber==null) {
        out.println("Could not delete object. No wizard or objectnumber given.");
        return;
    }

    // create wizard object so that delete/create actions are correctly loaded. No need to store. We'll create another wizard automatically if a button in the list is pressed.
    String username = (String)session.getAttribute("editwizard.username");
    String password = (String)session.getAttribute("editwizard.password");
    Wizard wiz = new Wizard(settings_context, settings_basedir, wizard, null, cloud);

    Node deleteaction = Utils.selectSingleNode(wiz.schema, "/*/action[@type='delete']");

    if (deleteaction!=null) {
        // Ok. let's delete this object.
        // more sophisticated code could be placed here so that
    } else {
        // no delete action defined in the wizard schema. We cannot delete.
        out.println("No delete action is defined in the wizard schema: '"+ wizard + "'. <br />You should place &lt;action type=\"delete\" /> in your schema so that delete actions will be allowed.");
        return;
    }

    // All checks are done. Let's try to delete.

    try {
        org.mmbase.bridge.Node obj = cloud.getNode(objectnumber);
        try {
            obj.delete(true);
            response.sendRedirect("list.jsp");
        } catch (Exception e) {
            out.println("You do not have the rights to delete the node with number "+objectnumber+". Delete not succeeded.<br />Click <a href=list.jsp>here</a> to go back to the list.");
        }
    } catch (Exception e) {
        out.println("Node with number "+objectnumber+" not found in cloud. Delete not succeeded.<br />Click <a href=list.jsp>here</a> to go back to the list.");
    }

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
            session.setAttribute(instancename+paramname, val);
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
