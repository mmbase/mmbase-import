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
  1. Add a rubriek Natuurmonumenten with alias root and relate the Intranet and Ontwikkel rubrieken to it<br/>
  <mm:createnode type="rubriek" id="root">
		<mm:setfield name="naam">Natuurmonumenten</mm:setfield>
	</mm:createnode>
	<mm:node number="$root">
		<mm:createalias>root</mm:createalias>
	</mm:node>
	<mm:listnodes type="rubriek" constraints="naam = 'Intranet'" id="rubriek1">
    <mm:setfield name="url">www.natuurmonumenten.nl</mm:setfield>
    <mm:setfield name="naam_de">Natuurmonumenten</mm:setfield>
		<mm:createrelation source="root" destination="rubriek1" role="parent">
      <mm:setfield name="pos">10</mm:setfield>
    </mm:createrelation>
	</mm:listnodes>
  <mm:listnodes type="rubriek" constraints="naam = 'Ontwikkel'" id="rubriek2">
    <mm:setfield name="url">www.natuurmonumenten.nl</mm:setfield>
    <mm:setfield name="url_live">nmintra</mm:setfield>
    <mm:setfield name="naam_de">Natuurmonumenten</mm:setfield>
		<mm:createrelation source="root" destination="rubriek2" role="parent">
      <mm:setfield name="pos">20</mm:setfield>
    </mm:createrelation>
	</mm:listnodes>
  2. Link admin to root (rolerel.iRol=100).<br/>
  <mm:node number="users.admin" id="admin" />
  <mm:createrelation source="root" destination="admin" role="rolerel">
    <mm:setfield name="rol">100</mm:setfield>
  </mm:createrelation>
	3. Delete pages Nieuws and Interne Mededelingen (2x)<br/>
  <mm:listnodes type="pagina" constraints="titel = 'Nieuws' OR titel = 'Interne Mededelingen'">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
	4. Delete template for "Wat vindt je ervan?" page in P&O<br/>
  <mm:listnodes type="pagina" constraints="titel = 'Wat vindt je ervan?'" orderby="number" directions="DOWN" max="1">
    <mm:related path="gebruikt,paginatemplate">
      <mm:deletenode element="gebruikt" />
    </mm:related>
  </mm:listnodes>
	5. Analyzing titels of articles and paragraaf to remove #NZ# string.<br/>
  Processing...<br/>
	<mm:listnodes type="artikel">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<% if ((titel.indexOf("#NZ#")>-1)||(titel.indexOf("#nz#")>-1)) {
					titel = titel.replaceAll("#NZ#","").trim();
					titel = titel.replaceAll("#nz#","").trim(); %>
					<mm:setfield name="titel"><%= titel %></mm:setfield>
					<mm:setfield name="titel_zichtbaar">0</mm:setfield>
			<% } %>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="paragraaf">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<% if ((titel.indexOf("#NZ#")>-1)||(titel.indexOf("#nz#")>-1)) {
					titel = titel.replaceAll("#NZ#","").trim();
					titel = titel.replaceAll("#nz#","").trim(); %>
					<mm:setfield name="titel"><%= titel %></mm:setfield>
					<mm:setfield name="titel_zichtbaar">0</mm:setfield>
			<% } %>
		</mm:field>
	</mm:listnodes>
  Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
