<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
	<% log.info("06.03.30"); %>
	Changing templates.url from templates/*.jsp to *.jsp<br/>
	Setting the templates to visible in the pagina form<br/>
	<mm:listnodes type="paginatemplate">
		<mm:field name="url" jspvar="url" vartype="String" write="false">
			<mm:setfield name="url"><%= url.substring(10) %></mm:setfield>
		</mm:field>
		<mm:setfield name="systemtemplate">0</mm:setfield>   
		<mm:setfield name="dynamiclinklijsten">0</mm:setfield>    
		<mm:setfield name="dynamicmenu">0</mm:setfield>    
		<mm:setfield name="contenttemplate">0</mm:setfield>   
	</mm:listnodes>
  Making pages deletable and dont show title<br/>
  <mm:listnodes type="pagina">
  	<mm:setfield name="verwijderbaar">1</mm:setfield>
  	<mm:setfield name="titel_zichtbaar">0</mm:setfield>    
	</mm:listnodes>
	<mm:list path="pagina,lijstcontentrel,linklijst">
	   <mm:node element="pagina" id="pagina" />
	   <mm:node element="linklijst" id="linklijst" />
	   <mm:createrelation source="pagina" destination="linklijst" role="posrel" />
	   <mm:deletenode element="lijstcontentrel" />
	</mm:list>
  Done.
</mm:log>
</mm:cloud>
