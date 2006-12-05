<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
<mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
<mm:import id="user"><%=cloud.getUser().getIdentifier()%></mm:import>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <body>
    <table class="editlist">
<%
    // basic security shortcut, to speed up getting the list of nodemanagers
    // not nice but speedy if you use basic security
    String authtype=null;
    try {
        Module mmbase = cloud.getCloudContext().getModule("mmbase");
        if (mmbase!=null) authtype = mmbase.getInfo("GETAUTHTYPE");
    } catch (Exception e) {}
    
    // check for older code
    if ("basic".equals(authtype)) {
      NodeList l = cloud.getList(null,"people,authrel,typedef", null, "people.account='"+cloud.getUser().getIdentifier()+"'", null, null, null, false);
      java.util.Collections.sort(l, new java.util.Comparator() {
         public int compare(Object o1, Object o2) {
           String s1 = ((Node)o1).getStringValue("typedef.gui()");
           String s2 = ((Node)o2).getStringValue("typedef.gui()");
           return s1.toUpperCase().compareTo(s2.toUpperCase());
         }
      } ); // MMCI doesn't sort, do it ourselves.

      for (int i=0; i<l.size(); i++) {
          Node n = l.getNode(i);
%>
      <tr>
        <td class="editprompt"><%=n.getStringValue("typedef.gui()")%></td>
        <td class="navlink">
          <a href="<mm:url page="editor.jsp"><mm:param name="manager"><%=n.getStringValue("typedef.name")%></mm:param></mm:url>" target="_top">##</a>
        </td>
      </tr>
<%    }
   } else { 
    // functionality for listing nodemanagers is not (yet?) in taglib, using MMCI.
    String liststyle = (String)session.getValue("mmeditors_liststyle");
    if (liststyle==null) liststyle="short";
    
    NodeManagerList l = cloud.getNodeManagers();
    java.util.Collections.sort(l, new java.util.Comparator() {
       public int compare(Object o1, Object o2) {
         NodeManager n1 = ((Node)o1).toNodeManager();
         NodeManager n2 = ((Node)o2).toNodeManager();
         return n1.getGUIName().toUpperCase().compareTo(n2.getGUIName().toUpperCase());
       }
    } ); // MMCI doesn't sort, do it ourselves.
    for (int i=0; i<l.size(); i++) {
        NodeManager nt = l.getNodeManager(i);
        if (nt.mayCreateNode() && (!nt.hasField("dnumber") || "long".equals(liststyle))) {
%>
      <tr>
        <td class="editprompt"><%=nt.getGUIName()%></td>
        <td class="navlink">
          <a href="<mm:url page="editor.jsp"><mm:param name="manager"><%=nt.getName()%></mm:param></mm:url>" target="_top">##</a>
        </td>
      </tr>
<%      }
    }
  }
%>
    </table>
  </body>
  </mm:cloud>
</html>
