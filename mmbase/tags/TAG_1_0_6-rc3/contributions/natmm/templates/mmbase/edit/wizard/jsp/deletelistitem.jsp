.<%@ include file="settings.jsp"
%><mm:locale language="<%=ewconfig.language%>">
<mm:cloud name="mmbase" method="http" jspvar="cloud"
><mm:log jspvar="log"
><%@ page import="org.mmbase.bridge.*"
%><%@ page import="org.w3c.dom.Node"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.Config"
%><%
    /**
     * deletelistitem.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: deletelistitem.jsp,v 1.4 2006-10-06 09:04:51 henk Exp $
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

    String wizard="";
    Map attributes=null;
    Object con=ewconfig.subObjects.peek();
    if (con instanceof Config.SubConfig) {
        wizard=((Config.SubConfig)con).wizard;
        attributes = ((Config.SubConfig)con).getAttributes();
    }

    Wizard wiz = new Wizard(request.getContextPath(), ewconfig.uriResolver, wizard, null, cloud);
    Node deleteaction = Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='delete']");
    if (deleteaction != null) {
        String objectnumber = request.getParameter("objectnumber");
        // check whether the relation should be deleted instead of the node, in case of newfromlist
        if(attributes!=null && attributes.containsKey("startnodes") && attributes.containsKey("nodepath")) {
          boolean isNewFromList = newFromListPath(cloud, (String) attributes.get("startnodes"), (String) attributes.get("nodepath"))!=null;
          if(isNewFromList) {
              String nodePath = (String) attributes.get("nodepath");
              String splitNodepath[] = nodePath.split(",",0);
              NodeList nl =
                     cloud.getList(
                        (String) attributes.get("startnodes"),
                        nodePath,
                        splitNodepath[1] + ".number",
                        splitNodepath[2] + ".number = '" + objectnumber + "'", 
                        null, null, null, true);
              if(nl.size()==1) {
                objectnumber = nl.getNode(0).getStringValue(splitNodepath[1] + ".number");
              }
           }
        }
        // Ok. let's delete this object.
        org.mmbase.bridge.Node obj = cloud.getNode(objectnumber);
        obj.delete(true);
        // prevent from losing nodepath
        String params = "";
        if(attributes!=null) {
          String [] keys = { "startnodes", "nodepath", "fields", "search", "wizard" };
          for(int i=0; i<keys.length; i++) {
            if(attributes.containsKey(keys[i]) ) {
              params += "&" + keys[i] + "=" + (String) attributes.get(keys[i]);
            }
          }
        }
        response.sendRedirect(response.encodeRedirectURL("list.jsp?proceed=true&sessionkey=" + sessionKey + params));
    } else {
        // No delete action defined in the wizard schema. We cannot delete.
        log.error("No delete action is defined in the wizard schema: '"+ wizard + "'. <br />You should place &lt;action type=\"delete\" /> in your schema so that delete actions will be allowed.");

    }
%>
</mm:log></mm:cloud></mm:locale>
