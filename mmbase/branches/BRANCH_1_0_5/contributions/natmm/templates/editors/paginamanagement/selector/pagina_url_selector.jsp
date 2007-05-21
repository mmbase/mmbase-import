<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="../../css/tree.css">
   <title>Pagina-selector</title>
   <script language="Javascript1.2" src="../../../js/gen_utils.js">
   </script>
</head>

<body>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<span style="width:500">
<%
   String target = request.getParameter("refreshtarget");
   String targetFrame= request.getParameter("refreshframe");
   String modus = request.getParameter("mode");
   boolean jsModus = "js".equals(modus);
   PaginaTreeModel model = new PaginaTreeModel(cloud);
   HTMLTree t=new HTMLTree(model);
   String account = cloud.getUser().getIdentifier();
   AuthorizationHelper helper = new AuthorizationHelper(cloud);
   java.util.Map roles=helper.getRolesForUser(helper.getUserNode(account));
   t.setCellRenderer(new PaginaURLRenderer( model, roles, account, cloud, "workpane", request.getContextPath()) );
   t.setExpandAll(true);
   t.setImgBaseUrl("../../img/");
   t.render( out ); 
%>
</span>
</mm:cloud>
</body>
