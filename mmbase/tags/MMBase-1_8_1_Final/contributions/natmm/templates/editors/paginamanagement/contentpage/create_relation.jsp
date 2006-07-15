<%@page import="org.mmbase.bridge.*"%>
<%@page import="nl.leocms.util.PublishUtil" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<%
   String currentNodeNumber = request.getParameter("currentnodenumber").trim();
   String nodeNumber = request.getParameter("nodenumber").trim();
   String alias = request.getParameter("alias").trim();
   String refreshPage = request.getParameter("refreshpage");
     
   if (!nodeNumber.equals(currentNodeNumber)) {
      if ((nodeNumber.equals("-1")) && (!currentNodeNumber.equals("-1"))) {
         // delete alias from currentNodeNumber
         Node currentPaginaNode = cloud.getNode(currentNodeNumber);
         currentPaginaNode.deleteAlias(alias);
         currentPaginaNode.commit();
         PublishUtil.PublishOrUpdateNode(currentPaginaNode);
      }
      if ((currentNodeNumber.equals("-1")) && (!nodeNumber.equals("-1"))) {
         // create alias from nodenumber
         Node newPaginaNode = cloud.getNode(nodeNumber);
         newPaginaNode.createAlias(alias);
         newPaginaNode.commit();  
         PublishUtil.PublishOrUpdateNode(newPaginaNode);
      }
      if ((!currentNodeNumber.equals("-1")) && (!nodeNumber.equals("-1"))) {
         // delete alias from currentNodeNumber
         Node currentPaginaNode = cloud.getNode(currentNodeNumber);
         currentPaginaNode.deleteAlias(alias);
         currentPaginaNode.commit();  
         // create alias from nodenumber
         Node newPaginaNode = cloud.getNode(nodeNumber);
         newPaginaNode.createAlias(alias);
         newPaginaNode.commit();
         PublishUtil.PublishOrUpdateNode(currentPaginaNode);
         PublishUtil.PublishOrUpdateNode(newPaginaNode);
      }
   }
%>
<html>
<head>
  <title>Creating relation</title>
   <script language="JavaScript1.2">   
      function refreshParentFrameAndClose() {                  
        opener.top.rightpane.location = "<%= refreshPage %>";
        window.close();
      }
   </script>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body bgcolor="#9DBDD8" onload="refreshParentFrameAndClose();">
  Even geduld aub.
</body>
</html>
</mm:cloud>