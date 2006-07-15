<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
	Things to be done in this update: <br/>
	1. Setting correct afdelingen.omschrijving and afdelingen.importstatus value. <br/>
	2. Making all "inactive" medewerkers "active". <br/>
	<mm:listnodes type="afdelingen">
		<mm:field name="omschrijving" jspvar="descr" vartype="String" write="false">
			<% if (descr.equals("inactive") || descr.equals("-1")) {%>
				<mm:setfield name="importstatus">inactive</mm:setfield>
				<mm:setfield name="omschrijving"><%= "" %></mm:setfield>
			<% } else if (descr.indexOf(",")>-1) {
				  String sDescrValue = "";	%>
				  <mm:field name="number" jspvar="number" vartype="String" write="false">
				  		<mm:list nodes="<%= number %>" path="afdelingen1,readmore,afdelingen2" orderby="afdelingen2.titel" directions="UP" searchdir="source">
							<mm:first>
								<% sDescrValue = number + ", "; %>
							</mm:first>
							<mm:field name="readmore.number" jspvar="readmore" vartype="String" write="false">
								<% sDescrValue += readmore;%>
								<mm:last inverse="true">
									<% sDescrValue += ", "; %>
								</mm:last>
							</mm:field>
						</mm:list>
				  </mm:field>
				<mm:setfield name="omschrijving"><%= sDescrValue %></mm:setfield>
				<mm:setfield name="importstatus"><%= "" %></mm:setfield>
			<% } else if (!descr.equals("")){%>
				<mm:field name="number" jspvar="number" vartype="String" write="false">
					<mm:setfield name="omschrijving"><%= number %></mm:setfield>
					<mm:setfield name="importstatus"><%= "" %></mm:setfield>
				</mm:field>
			<% } %>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="medewerkers" constraints="medewerkers.importstatus = 'inactive'">
		<mm:setfield name="importstatus">active</mm:setfield>
	</mm:listnodes>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
