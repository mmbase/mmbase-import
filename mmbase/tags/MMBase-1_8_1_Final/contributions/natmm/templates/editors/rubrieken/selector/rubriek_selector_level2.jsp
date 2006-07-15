<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,
      nl.leocms.authorization.*,
      nl.leocms.rubrieken.*,
      nl.leocms.rubrieken.RubriekTreeModel" %>
<%@include file="/taglibs.jsp" %>

<%
   String refreshTarget = request.getParameter("refreshtarget");
   String targetFrame= request.getParameter("targetframe");
   String refrPage = request.getParameter("refrPage");
   String linkLijstNumber = request.getParameter("linklijstnumber");
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/tree.css">

<title>Rubrieken-selector</title>'
<script language="JavaScript" src="../../../js/gen_utils.js"></script>
<script language="Javascript1.2">
   
   function refreshOpenerFrameAndClose(rubriekNumber) {
      //openPopupWindow('createrelationwindow', 1, 1);      
      document.createRelationForm.toObjectNumber.value = rubriekNumber;
      document.createRelationForm.submit();
   }

</script>
</head>
<body">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>

<%
   RubriekTreeModel model = new RubriekTreeModel (cloud);
    HTMLTree t=new HTMLTree(model);
    String account=cloud.getUser().getIdentifier();
    AuthorizationHelper helper= new AuthorizationHelper(cloud);
    java.util.Map roles=helper.getRolesForUser( helper.getUserNode(account));
    t.setCellRenderer(new RubriekLevel2SelectorRenderer( model, roles, refreshTarget, targetFrame) );
    t.setExpandAll(true);
    t.setImgBaseUrl("../../img/");
    t.render( out );
%>
</mm:cloud>


<form action="../../../oneclickedit/operations/create_relation.jsp" name="createRelationForm">
   <input type="hidden" name="fromObjectNumber" value="<%= linkLijstNumber %>"/>
   <input type="hidden" name="toObjectNumber"/>   
   <input type="hidden" name="relType" value="lijstcontentrel"/>
   <input type="hidden" name="refrPage" value="<%= refrPage %>"/>
</form>

</body>
</html>