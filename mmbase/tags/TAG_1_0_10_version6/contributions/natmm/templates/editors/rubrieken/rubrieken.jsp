<%@page import="com.finalist.tree.*,nl.leocms.rubrieken.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
   <link href="../css/tree.css" type="text/css" rel="stylesheet"/>
   <title>Rubriekenindeling</title>
   <script language="Javascript1.2" src="../../js/gen_utils.js"></script>
</head>
<body style="padding-left:2px;">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<h3>Rubrieken-editor</h3>
<p><b>Legenda: </b><br>
<table cellpadding="0" cellspacing="0">
   <tr><td><img src='../img/new_rubriek.gif' border='0' align='middle'/></td><td style="font-size:10px;">= nieuwe subrubriek maken</td></tr>
   <tr><td><img src='../img/rubrieklink.gif' border='0' align='middle'/></td><td style="font-size:10px;">= wijzig de titel, style en zichtbaarheid van deze rubriek</td></tr>
   <tr><td><img src='../img/edit.gif' border='0' align='middle'/></td><td style="font-size:10px;">= bewerk deze rubriek</td></tr>
   <tr><td><img src='../img/remove.gif' border='0' align='middle'/></td><td style="font-size:10px;">= verwijder deze rubriek</td></tr>
</table>
</p>
<%
    RubriekTreeModel model = new RubriekTreeModel(cloud);
    HTMLTree t=new HTMLTree(model,"rubriek");
    String account=cloud.getUser().getIdentifier();
    AuthorizationHelper helper= new AuthorizationHelper(cloud);
    java.util.Map roles=helper.getRolesForUser( helper.getUserNode(account));
    t.setCellRenderer(new RubriekRenderer( model, roles  ) );
    t.setExpandAll(false);
    t.setImgBaseUrl("../img/");
    t.render( out );
%>
<script language="Javascript1.2">restoreTree();</script>
<br>
</mm:cloud>
</body>
