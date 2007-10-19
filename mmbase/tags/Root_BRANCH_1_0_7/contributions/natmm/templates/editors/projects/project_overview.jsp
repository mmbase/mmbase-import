<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="nl.leocms.util.PaginaHelper" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%
String paginaID = request.getParameter("p");
if(paginaID==null || paginaID.equals("")) {
   paginaID = "";
   %>
   <mm:list nodes="imap_template" path="paginatemplate,gebruikt,pagina" fields="pagina.number">
      <mm:field name="pagina.number" id="p" jspvar="thisPage" vartype="String" write="false">
         <% paginaID += (!paginaID.equals("") ? "," : "") + thisPage; %>
   	</mm:field>		
   </mm:list>
   <%
}
String sReferrer = "/editors/projects/project_overview.jsp?p=" + paginaID;
%>
<mm:list nodes="" path="projects,phaserel,phases">
   <mm:field name="projects.titel" jspvar="project" vartype="String" write="false">
   <mm:field name="phases.name" jspvar="phase" vartype="String" write="false">
      <mm:node element="phaserel">
         <mm:setfield name="name"><%= phase + " fase van " + project %></mm:setfield>
      </mm:node>
   </mm:field>
   </mm:field>
</mm:list>
<html>
<head>
   <title>imagemap pagina</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <style>
      p { margin: 0px; }
      th { vertical-align: top; }
   </style>
</head>
<body style="overflow:auto;">
   <table>
      <tr>
         <td>
            <a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/projects/projects&objectnumber=new&referrer=<%= sReferrer %>">
         	   <img title="nieuw project aanmaken" border="0" src="<%= editwizard_location %>/media/new.gif"></a>
         </td>
         <td>
            <h1 style="margin:0px;margin-bottom:10px;">voorbeeld projecten</h1>
         </td>
      </tr>
   </table>
   <table class="formcontent" width="100%" border="1"  cellspacing="0" cellpadding="1">
      <tr>
      	<th width="20%" colspan="1">Project</th>
      	<th width="20%" colspan="1">Fases</th>
      </tr>
      <mm:listnodes type="projects" orderby="titel" directions="UP">
      <tr valign="top">	
         <td><table><tr>	
         		<td>
         			<a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/projects/projects&nodepath=projects&objectnumber=<mm:field name="number" />&referrer=<%= sReferrer %>">
         			   <mm:field name="titel" /></a>
         		</td>
         </tr></table></td>	
         <td><table><tr>	
         		<td>
         		<mm:related path="phaserel,phases" orderby="phaserel.pos">
         			<a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/phaserel/phaserel&nodepath=phaserel&objectnumber=<mm:field name="phaserel.number" />&referrer=<%= sReferrer %>">
         			<mm:field name="phases.name" /></a><br/>
         		</mm:related>
         		</td>
         </tr></table></td>
      </tr>	
      </mm:listnodes>
   </table>
</body>
</html>
</mm:cloud>
