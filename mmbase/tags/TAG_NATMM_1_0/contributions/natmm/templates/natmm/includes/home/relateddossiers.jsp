<%@page import="nl.leocms.evenementen.Evenement,nl.leocms.util.PaginaHelper" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
String objectID = request.getParameter("o");
PaginaHelper ph = new PaginaHelper(cloud);
String subsiteID = ph.getSubsiteRubriek(cloud,objectID);
%>
<mm:node number="<%=objectID%>">
<mm:related path="posrel,dossier" fields="dossier.number,dossier.naam" orderby="posrel.pos">
	<mm:field name="dossier.number" jspvar="dossier_number" vartype="String" write="false">
	<mm:field name="dossier.naam" jspvar="dossier_naam" vartype="String" write="false">
	<mm:first inverse="true"><br/></mm:first>
	<span class="colortitle">
   	<% readmoreURL = ""; %>
   	<mm:list nodes="<%=dossier_number%>" path="dossier,readmore,pagina,gebruikt,paginatemplate"
   	      fields="paginatemplate.url,pagina.number"  max="1">
         <mm:field name="pagina.number" write="false" jspvar="pagina_number" vartype="String">
         <mm:field name="paginatemplate.url" write="false" jspvar="template_url" vartype="String">
         <% 
            readmoreURL = template_url + "?p=" + pagina_number;
         %>
         </mm:field>
         </mm:field>
      </mm:list><%
      if(!readmoreURL.equals("")) {
         %><a href="<%= readmoreURL %>" class="maincolor_link"><%
      }
      %><%= dossier_naam.toUpperCase() %><%
      if(!readmoreURL.equals("")) {
         %></a><%
      }
      %>
   </span>
	<div class="rule" style="margin-bottom:6px;"></div>
	<mm:list nodes="<%=dossier_number%>" path="dossier,readmore,artikel"
	   fields="artikel.number,artikel.titel" orderby="readmore.pos" max="3">
		<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
			<% String relatedPage = null; %>
			<mm:list nodes="<%= artikel_number %>" path="artikel,contentrel,pagina" fields="pagina.number">
				<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
					<% if(subsiteID.equals(ph.getSubsiteRubriek(cloud,pagina_number))) { relatedPage = pagina_number; } %>
				</mm:field>
			</mm:list>
      <% 
      if(relatedPage==null) { 
        %>
        <mm:list nodes="<%= artikel_number %>" path="artikel,posrel,dossier,posrel,pagina" fields="pagina.number">
				  <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
					  <% if(subsiteID.equals(ph.getSubsiteRubriek(cloud,pagina_number))) { relatedPage = pagina_number; } %>
          </mm:field>
        </mm:list>
        <%
      } %>
			<table style="width:100%;" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="text-align:left;vertical-align:middle;">
            <%= (relatedPage!=null ? "<a href="+  ph.createItemUrl(artikel_number,relatedPage,null, request.getContextPath()) +" class='hover'>": "" ) %>
            <mm:field name="artikel.titel" />
            <%= (relatedPage!=null ? "</a>": "" ) %>
            </td>
					<%-- <td width="80" align="right" valign="middle"><a href="<%=templateName%>?id=<%=artikel_number%>" class="hp_leesverder">Lees verder &raquo;</a></td> --%>
				</tr>
			</table>
			<table class="dotline" style="margin-top:2px;margin-bottom:2px;"><tr><td height="3"></td></tr></table>			
	   </mm:field>
	</mm:list>
	<mm:list nodes="<%=dossier_number%>" path="dossier,posrel,evenement"
	   fields="evenement.number" orderby="posrel.pos" max="3">
		<mm:field name="evenement.number" jspvar="evenement_number" vartype="String" write="false">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="text-align:left;vertical-align:middle;"><a href="events.jsp?p=agenda&e=<%= Evenement.getNextOccurence(evenement_number) %>" class="hover"><mm:field name="evenement.titel" /></a></td>
				</tr>
			</table>
			<table class="dotline" style="margin-top:2px;margin-bottom:2px;"><tr><td height="3"></td></tr></table>			
	   </mm:field>
	</mm:list>
	</mm:field>
	</mm:field>
</mm:related>
</mm:node>
</mm:cloud>