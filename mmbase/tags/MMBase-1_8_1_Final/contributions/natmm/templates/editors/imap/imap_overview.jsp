<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="nl.leocms.util.PaginaHelper" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
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
String sReferrer = "/editors/imap/imap_overview.jsp?p=" + paginaID;
PaginaHelper ph = new PaginaHelper(cloud);
%>
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
   <h1>pagina met hotspots</h1>
   <table class="formcontent" width="100%" border="1"  cellspacing="0" cellpadding="1">
   <tr>
   	<th width="20%" colspan="1">Afbeelding</th>
   	<th width="20%" colspan="1">Bekijk coordinaten van de hotspots op de afbeelding</th>
   	<th width="20%" colspan="1">Bewerk de pagina en de hotspots</th>
   	<th width="20%" colspan="1">Bekijk de aan de hotspots gerelateerde pagina's</th>
   	<th width="20%" colspan="1">Bekijk de aan de hotspots gerelateerde artikelen</th>
   </tr>
   <mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" orderby="images.number" directions="DOWN">
   	<mm:node element="images">
   <tr valign="top">	
   
   <% // View Image of Image map %>
   <td><table class="formcontent"><tr>	
   		<td>
   		<center>
   		<mm:related path="posrel,pagina">
   			<mm:field name="pagina.number" id="p" jspvar="thisPage" vartype="String" write="false">
   			   <a target="_blank" href="<mm:url referids="p" page="<%= "/" + ph.getSubDir(cloud,thisPage) + "imap_preview.jsp" %>" />">
   			</mm:field>
   			<mm:remove referid="p" />
   		</mm:related>
   		<img src=<mm:image template="s(100x100)" />  border="0" alt="Bekijk coordinaten <mm:field name="title" />"></a>
   		</center>
   		</td>
   </tr></table></td>	
   <% // View coordinates %>	
   <td><table class="formcontent"><tr>	
   		<td>
   		<mm:related path="posrel,pagina">
   			<mm:field name="pagina.number" id="p" jspvar="thisPage" vartype="String" write="false">
   			   <a target="_blank" href="<mm:url referids="p" page="<%= "/" + ph.getSubDir(cloud,thisPage) + "imap_preview.jsp" %>" />">
   			</mm:field>
   			<mm:remove referid="p" />
   		</mm:related><mm:field name="title" /></a>
   		</td>
   
   		<td width="1%" align="right">
   		<mm:related path="posrel,pagina">
   			<mm:field name="pagina.number" id="p" jspvar="thisPage" vartype="String" write="false">
   			   <a target="_blank" href="<mm:url referids="p" page="<%= "/" + ph.getSubDir(cloud,thisPage) + "imap_preview.jsp" %>" />">
   			</mm:field>
   			<mm:remove referid="p" />
   		</mm:related>
   		<img title="Bekijk coordinaten <mm:field name="pagina.titel" />" border="0" src="../img/right.gif"></a>
   		</td>
   </tr></table></td>
   
   <% // Edit coordinates %>
   <td><table class="formcontent"><tr>
   		<td>
   		<mm:related path="posrel,pagina">
   			<a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/pagina/pagina_imap&nodepath=pagina&objectnumber=<mm:field name="pagina.number" />&referrer=<%= sReferrer %>">
   			<mm:field name="pagina.titel" /> </a>
   		</mm:related>		
   		</td>
   			
   		<td width="1%" align="right">
   		<mm:related path="posrel,pagina">
      		<a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/pagina/pagina_imap&nodepath=pagina&objectnumber=<mm:field name="pagina.number" />&referrer=<%= sReferrer %>">
      		<img title="Bewerk coordinaten voor <mm:field name="pagina.titel" />"  border="0" src="../img/right.gif"></a>	
   		</mm:related>
   		</td>
   </tr></table></td>	

   <% // View related pagina %>
   <td><table class="formcontent">
      <mm:related path="pos4rel,pagina">
   	 <mm:remove referid="related"/>
   	 <mm:import id="related" />
       <% String editconfig_url = ""; %>
       <mm:node element="pagina">
            <mm:field name="number" jspvar="pagina_number" vartype="String" write="false">
            <%
            TreeMap ewUrls = ph.createEditwizardUrls(pagina_number, request.getContextPath());
            while(!ewUrls.isEmpty()) {
               String nextUrl = (String) ewUrls.firstKey();
               String nextEw =  (String) ewUrls.get(nextUrl);
               if(nextUrl.indexOf("config/pagina/pagina")>-1) {
                  editconfig_url = nextUrl;
               }
               ewUrls.remove(nextUrl);
            }
            %>
            </mm:field>
        </mm:node>
   		<tr>
   		  <td style="vertical-align:middle">
   			<a href="<%= editconfig_url %>">
   			<mm:field name="pagina.titel"  /> </a>
   		  </td>		
   		  <td width="1%" align="right">
            <a href="<%= editconfig_url %>">
      	      <img title="pagina: <mm:field name="pagina.titel" />" border="0" src="../img/right.gif"></a><br/>
      	  </td>
   		</tr>
   	</mm:related>	
   	<mm:notpresent referid="related"><tr><td>Geen gerelateerde pagina</td></tr></mm:notpresent>
   	<mm:remove referid="related"/>
   </table></td>		
   <% // View related artikel %>
   <td><table class="formcontent">
   	<mm:related path="artikel">
   	  <mm:remove referid="related"/>
   	  <mm:import id="related" />		
   		<tr>
   		  <td style="vertical-align:middle">
   			<a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/artikel/artikel&nodepath=artikel&objectnumber=<mm:field name="artikel.number" />&referrer=<%= sReferrer %>">
   			<mm:field name="artikel.titel" /> </a>
   		  </td>	
   		  	
   		  <td width="1%" align="right">
   			<a href="/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/artikel/artikel&nodepath=artikel&objectnumber=<mm:field name="artikel.number" />&referrer=<%= sReferrer %>">
   			<img title="artikel: <mm:field name="artikel.title" />" border="0" src="../img/right.gif"></a>
   		  </td>
   		</tr>
   	</mm:related>	
   	<mm:notpresent referid="related"><tr><td>Geen gerelateerd artikel</td></tr></mm:notpresent>
   	<mm:remove referid="related"/>
   </table></td>		
   </tr>	
   </mm:node>
   </mm:list>
   </table>
</body>
</html>
</mm:cloud>
