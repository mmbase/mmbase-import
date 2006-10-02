<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,nl.leocms.authorization.*,nl.leocms.util.PaginaHelper" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<%
String account = cloud.getUser().getIdentifier();
%>
<cache:cache groups="<%= account %>" key="pagina_all" time="<%= 3600*24*7 %>" scope="application">
<!-- <%= new java.util.Date() %> -->
<html>
<head>
   <link rel="stylesheet" type="text/css" href="../css/tree.css">
   <title>Pagina-selector</title>
   <script language="Javascript1.2" src="../../js/gen_utils.js"/>
   </script>
   <script>
      var cancelClick = false;
      function warnOnEditwizardOpen() {
      	var conf;
      	if(readCookie('ew')==null) {
            conf=true;
      	} else {
         	conf = confirm('Weet u zeker dat u de editwizard wilt afsluiten, zonder op te slaan of te annuleren?');
         	if(conf) { deleteCookie('ew'); }
         }
      	cancelClick=true;
      	return conf;
      }
   </script>
	<script>
    function showTab(tab, hide1) {
      document.getElementById(tab).style.display = 'inline';
      document.getElementById(hide1).style.display = 'none';
    }
   </script>
   <style>
      input {
   	   width:110px;
   		font-family: Tahoma, Verdana, Arial, Helvetica, sans-serif; 
   		font-size: 11px; font-weight: normal;
   		text-align: left;
   		letter-spacing: 0.05em;
         padding-left:3px;
      }
      h3 {
         margin-bottom:0px;
      }
      p {
         margin:3px;
      }
   </style>
</head>
<body style="padding-left:2px;" onLoad="javascript:showEditorsButton();">
<div id="paginas">
	<table cellpadding="3" cellspacing="0" style="width:100%;">
      <tr>
         <td><h3>Pagina's</h3></td>
	      <td style="text-align:right;">
            <div id="buttondiv" style="visibility:hidden;"><input type="button" value="Naar archiefkast" onclick="javascript:showTab('editors','paginas')" /></div>
         </td>
      </tr>
   </table>
	<p><b>Legenda:</b><br>
	<table cellpadding="0" cellspacing="0">
   	<tr><td><img src='../img/new.gif' border='0' align='middle'/></td><td style="font-size:10px;">= nieuwe subrubriek of pagina maken</td></tr>
	   <tr><td><img src='../img/reorder.gif' border='0' align='middle'/></td><td style="font-size:10px;">= volgorde pagina's en rubrieken wijzigen</td></tr>
   	<tr><td><img src='../img/paginalink.gif' border='0' align='middle'/></td><td style="font-size:10px;">= wijzig de titel en de template van deze pagina</td></tr>
	   <tr><td><img src='../img/edit.gif' border='0' align='middle'/></td><td style="font-size:10px;">= bewerk deze pagina</td></tr>
   	<tr><td><img src='../img/refresh.gif' border='0' align='middle'/></td><td style="font-size:10px;">= bekijk deze pagina in de preview</td></tr>
	   <tr><td><img src='../img/remove.gif' border='0' align='middle'/></td><td style="font-size:10px;">= verwijder deze pagina</td></tr>
	</table>
	</p>
	<%
	String contentModusNodeNumber = (String) session.getAttribute("contentmodus.contentnodenumber");
   if ((contentModusNodeNumber != null)  && (!contentModusNodeNumber.equals(""))) {
	%>
	<div style="position: absolute; right:20px; top:10px; z-index: 100">
   	<small>
      	Meervoudige contentmodus: <a href="frames.jsp" target="bottompane">Uitzetten</a>
	   </small>
	</div>
	<%
   	}
	%>
	<span style="width:600px">
	<%
   	PaginaTreeModel model = new PaginaTreeModel(cloud);
	   HTMLTree t=new HTMLTree(model,"pagina");
	   AuthorizationHelper helper = new AuthorizationHelper(cloud);
   	java.util.Map roles=helper.getRolesForUser(helper.getUserNode(account));
	   t.setCellRenderer(new PaginaAllRenderer( model, roles, account, cloud, "workpane", request.getContextPath()) );
   	t.setExpandAll(false);
	   t.setImgBaseUrl("../img/");
   	t.render( out ); 
	%>
	</span>
	<script language="Javascript1.2">restoreTree();</script>
</div>
<div id="editors" style="display: none">
	<mm:listnodes type="users" constraints="<%= "[account]='" + cloud.getUser().getIdentifier() + "'" %>" max="1" id="thisuser" />
   <mm:import externid="language">nl</mm:import>
	<mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?language=<mm:write  referid="language" /></mm:import>
	<mm:import id="jsps"><%= editwizard_location %>/jsp/</mm:import>
   <table cellpadding="3" cellspacing="0" style="width:100%;">
      <tr>
         <td><h3>Archiefkast</h3></td>
	      <td style="text-align:right;">
            <input type="button" value="Naar pagina's" onclick="javascript:showTab('paginas','editors')" />
         </td>
      </tr>
   </table>
	<%@include file="../includes/menu_editwizards.jsp" %>
</div>
</body>
</html>
</cache:cache>
</mm:cloud>