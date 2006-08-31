<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="../header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String managerName = state.getProperty("manager");
      String nodeID = state.getProperty("node");
      String currentState = state.getProperty("state");
  %>
  <mm:import externid="field" vartype="String" jspvar="fieldname" required="true" />
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="../css/mmeditors.css" type="text/css" />
  <script language="JavaScript">
    <%="<!--"%>
    function jumpPage(form) {
        i = form.selectmenu.options[form.selectmenu.selectedIndex];
        location.href = "newimage.jsp?field=<%=fieldname%>&device="+i.value+"&devicename="+i.text;
    }
    <%="// -->"%>
  </script>
  </head>
<mm:import externid="action" />
<mm:import externid="device" vartype="String" jspvar="device">upload</mm:import>
<mm:import externid="devicename" vartype="String" jspvar="devicename">upload</mm:import>
<mm:notpresent referid="action">
  <body>
    <form>
      Selecteer apparaat:
      <select name="selectmenu" onChange="jumpPage(this.form)">
        <option value="upload"
<% if (devicename.equals("upload")) { %>selected="selected"<% } %>
        >upload</option>
<%  Transaction trans=cloud.getTransaction(transactionID);
    NodeManager manager =	trans.getNodeManager(managerName);
    NodeList ls = manager.getList("devices",null);
    for (NodeIterator i = ls.nodeIterator(); i.hasNext();) {
      Node devNode = i.nextNode();
%><option value="<%=devNode.getStringValue("item1")%>"
<% if (devicename.equals(devNode.getStringValue("item2"))) { %>selected="selected"<% } %>
   ><%=devNode.getStringValue("item2")%></option>
<%  } %>
      </select>
    </form>
    <hr />
    <mm:include page="imgdevice-${device}.jsp" />
  </body>
</mm:notpresent>

<mm:present referid="action">
    <mm:include page="imgdevice-${device}.jsp" />
<%@include file="nextfield.jsp" %>
</mm:present>
  </mm:cloud>
</html>

