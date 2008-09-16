<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
  <% log.info("06.03.24"); %>
  0. Check whether the NMIntra application is installed succesfully
  <%
  ApplicationHelper ap = new ApplicationHelper(cloud);
  if(!ap.isInstalled("NMIntra")) {
   %>
   <mm:createnode type="versions">
      <mm:setfield name="name">NMIntra</mm:setfield>   
      <mm:setfield name="type">application</mm:setfield>   
      <mm:setfield name="version">1</mm:setfield>   
      <mm:setfield name="maintainer">mmbase.org</mm:setfield>   
   </mm:createnode>
   <%
  } %>
  1. Add a rubriek Natuurmonumenten with alias root and relate the Intranet and Ontwikkel rubrieken to it<br/>
  <mm:createnode type="rubriek" id="root">
		<mm:setfield name="naam">Natuurmonumenten</mm:setfield>
	</mm:createnode>
	<mm:node number="$root">
		<mm:createalias>root</mm:createalias>
	</mm:node>
	<mm:listnodes type="rubriek" constraints="naam = 'Intranet'">
   	<mm:node id="rubriek1">
         <mm:setfield name="url">www.natuurmonumenten.nl</mm:setfield>
         <mm:setfield name="url_live">nmintra</mm:setfield>
         <mm:setfield name="naam_de">Natuurmonumenten</mm:setfield>
      </mm:node>
   	<mm:createrelation source="root" destination="rubriek1" role="parent">
         <mm:setfield name="pos">10</mm:setfield>
      </mm:createrelation>
	</mm:listnodes>
  <mm:listnodes type="rubriek" constraints="naam = 'Ontwikkel'">
    <mm:node id="rubriek2">
       <mm:setfield name="url">www.natuurmonumenten.nl</mm:setfield>
       <mm:setfield name="url_live">nmintra</mm:setfield>
       <mm:setfield name="naam_de">Natuurmonumenten</mm:setfield>
    </mm:node>
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
  4. Rename page Nieuws en interne informatie<br/>
  <mm:listnodes type="pagina" constraints="titel = 'Nieuws en interne informatie'">
    <mm:setfield name="titel">Nieuws</mm:setfield>
  </mm:listnodes>	
  5. Delete "Wat vindt je ervan?" page in P&O<br/>
  <mm:listnodes type="pagina" constraints="titel = 'Wat vindt je ervan?'" orderby="number" directions="DOWN" max="1">
      <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  6. Analyzing titels of articles and paragraaf to remove #NZ# string.<br/>
  Processing...<br/>
   <mm:listnodes type="artikel" constraints="UPPER(titel) LIKE '#NZ#%'">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<mm:setfield name="titel"><%= titel.replaceAll("#NZ#","").replaceAll("#nz#","").trim() %></mm:setfield>
			<mm:setfield name="titel_zichtbaar">0</mm:setfield>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="paragraaf" constraints="UPPER(titel) LIKE '#NZ#%'">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<mm:setfield name="titel"><%= titel.replaceAll("#NZ#","").replaceAll("#nz#","").trim() %></mm:setfield>
			<mm:setfield name="titel_zichtbaar">0</mm:setfield>
		</mm:field>
	</mm:listnodes>
   Done.
</mm:log>
</mm:cloud>
