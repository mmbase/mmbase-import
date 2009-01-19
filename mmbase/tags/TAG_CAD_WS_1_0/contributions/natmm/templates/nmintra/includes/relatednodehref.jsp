<mm:field name="rubriek1.naam" jspvar="site_title" vartype="String" write="false"
><mm:field name="rubriek1.number" jspvar="site_number" vartype="String" write="false"
><mm:field name="rubriek2.naam" jspvar="rubriek_title" vartype="String" write="false"
><mm:field name="rubriek2.number" jspvar="rubriek_number" vartype="String" write="false"
><mm:field name="pagina.titel" jspvar="page_title" vartype="String" write="false"
><mm:field name="pagina.number" jspvar="page_number" vartype="String" write="false"><%
    rubriekId = rubriek_number;
    subsiteID = site_number; %>
	 <mm:list nodes="<%= page_number %>" path="pagina,gebruikt,paginatemplate">
	 	<mm:field name="paginatemplate.url" jspvar="url" vartype="String" write="false">	
		    <%  nodeUrl = url + "?p=" + page_number; %>
		</mm:field>
	</mm:list>
    <% if(thisPath.equals("producttypes")) { nodeUrl += "&pool=" + node_number; }
    if(thisPath.equals("artikel")) { nodeUrl += "&article=" + node_number; }
    nodeHref += "<a href=\"" + nodeUrl  +  "\">" 
        + site_title + "-" + rubriek_title;
    %><mm:field name="posrel.pos"><mm:compare value="0" inverse="true"><%
        nodeHref += " - " + page_title;
    %></mm:compare></mm:field><%
    nodeHref += "</a>";
%></mm:field
></mm:field
></mm:field
></mm:field
></mm:field
></mm:field>