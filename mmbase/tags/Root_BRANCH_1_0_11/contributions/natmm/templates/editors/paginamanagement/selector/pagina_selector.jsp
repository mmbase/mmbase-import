<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<%
   String refreshTarget = request.getParameter("refreshtarget");
   String targetFrame= request.getParameter("targetframe");
   String fieldNumber = request.getParameter("fieldnumber");
   String fieldName = request.getParameter("fieldname");
   String refrPage = request.getParameter("refrPage");
   String linkLijstNumber = request.getParameter("linklijstnumber");
%>
   

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/tree.css">

<title>Pagina-selector</title>'
<script language="JavaScript" src="../../../js/gen_utils.js"></script>
<script language="Javascript1.2">
   
   function setFieldInOpenerFrameAndClose(pagenumber, pagename) {
      <%
         if ((targetFrame != null) && (!targetFrame.equals(""))) {
      %>
         callFieldNumber = "opener.top.<%= targetFrame %>.document.forms[0].<%= fieldNumber %>.value='" + pagenumber + "'";
         callFieldName = "opener.top.<%= targetFrame %>.document.forms[0].<%= fieldName %>.value='" + pagename + "'";
      <%
         }
         else {
      %>
         callFieldNumber = "opener.document.forms[0].<%= fieldNumber %>.value='" + pagenumber + "'";
         callFieldName = "opener.document.forms[0].<%= fieldName %>.value='" + pagename + "'";
      <%
         }
      %>
      
      eval(callFieldNumber);
      eval(callFieldName);
      window.close();
   }

   function refreshOpenerFrameAndCloseIt(pagenumber) {
      <%
         if ((linkLijstNumber != null) && (!linkLijstNumber.equals(""))) {
      %>
         //openPopupWindow('createrelationwindow', 1, 1);      
         document.createRelationForm.toObjectNumber.value = pagenumber;
         document.createRelationForm.submit();
      <%
         }
         else {
      %>
         if (opener.top.<%= targetFrame %>) {
            callStr = "opener.top.<%= targetFrame %>.location='<%= refreshTarget %>?pagenumber=" + pagenumber + "'";
         }
         else {
            callStr = "opener.location='<%= refreshTarget %>?pagenumber=" + pagenumber + "'";
         }
         eval(callStr);
         window.close();
      <%
         }
      %>
   }

</script>
</head>
<body">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>

<%
   boolean hasRefreshTarget = ((refreshTarget != null) && (!refreshTarget.equals("")));
   PaginaTreeModel model = new PaginaTreeModel(cloud);
   HTMLTree t=new HTMLTree(model);
   String account=cloud.getUser().getIdentifier();
   AuthorizationHelper helper=  new AuthorizationHelper(cloud);
   java.util.Map roles=helper.getRolesForUser( helper.getUserNode(account));
   t.setCellRenderer(new PaginaSelectorRenderer( model, hasRefreshTarget) );
   t.setExpandAll(false);
   t.setImgBaseUrl("../../img/");
   t.render( out ); 
%>
<script language="Javascript1.2">clickNode("node_0");</script>
</mm:cloud>


<form action="../../../oneclickedit/operations/create_relation.jsp" name="createRelationForm">
   <input type="hidden" name="fromObjectNumber" value="<%= linkLijstNumber %>"/>
   <input type="hidden" name="toObjectNumber"/>   
   <input type="hidden" name="relType" value="lijstcontentrel"/>
   <input type="hidden" name="refrPage" value="<%= refrPage %>"/>
</form>

</body>
</html>