<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="../css/tree.css">
   <title>Pagina-indeling</title>
   <script language="Javascript1.2" src="../../js/gen_utils.js">
   </script>
</head>

<body>

<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<%
   String targetPage = request.getParameter("target_page");
   String mode = request.getParameter("mode");
   //
    PaginaTreeModel model = new PaginaTreeModel(cloud);
    HTMLTree t=new HTMLTree(model,"pagina");
    String account=cloud.getUser().getIdentifier();
    AuthorizationHelper helper= new AuthorizationHelper(cloud);
    java.util.Map roles=helper.getRolesForUser( helper.getUserNode(account));
    t.setCellRenderer(new PaginaRenderer( model, roles, account, cloud) );
    t.setExpandAll(false);
    t.setImgBaseUrl("../img/");
    t.render( out ); 
%>
</mm:cloud>
<script language="Javascript1.2">restoreTree();</script>
</body>
</html>