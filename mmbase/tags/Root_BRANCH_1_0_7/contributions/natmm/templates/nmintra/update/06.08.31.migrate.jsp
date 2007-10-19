<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.08.31"); %>
   1. Setting the levels of the rubrieken<br/>
	<mm:node number="root">
      <mm:setfield name="level">0</mm:setfield>
      <mm:relatednodes type="rubriek" searchdir="destination">
         <mm:setfield name="level">1</mm:setfield>
         <mm:relatednodes type="rubriek" searchdir="destination">
            <mm:setfield name="level">2</mm:setfield>
            <mm:setfield name="issearchable">1</mm:setfield>
            <mm:relatednodes type="rubriek" searchdir="destination">
               <mm:setfield name="level">3</mm:setfield>
            </mm:relatednodes>
         </mm:relatednodes>
      </mm:relatednodes>
   </mm:node>
   2. Delete inactive users and setting default fields for remaining users<br/>
	 <mm:listnodes type="users" constraints="password='inactive'">
	   <mm:deletenode deleterelations="true" />
   </mm:listnodes>
   <mm:listnodes type="users" constraints="account!='admin'">
      <mm:setfield name="password">admin2k</mm:setfield>
      <mm:setfield name="gracelogins">3</mm:setfield>
      <mm:setfield name="rank">chiefeditor</mm:setfield>
      <mm:related path="rolerel,rubriek">
        <mm:node element="rolerel">
          <mm:setfield name="rol">3</mm:setfield>
        </mm:node>
      </mm:related>
   </mm:listnodes>
   3. Add extra info to set default relations correctly<br/>
   <mm:listnodes type="teaser" constraints="titel='hard-/software'">
      <mm:node id="teaser" />
      <mm:listnodes type="paginatemplate" constraints="url='producttypes.jsp'">
         <mm:related path="gebruikt,pagina" constraints="pagina.titel='ICT'">
            <mm:node element="pagina" id="page" />
            <mm:createrelation source="page" destination="teaser" role="rolerel" />
         </mm:related>
	   </mm:listnodes>
   </mm:listnodes>
   <mm:listnodes type="pagina" constraints="titel='Zoek een opleiding'">
   		<mm:createalias>educations</mm:createalias>
	</mm:listnodes>
   <mm:listnodes type="pagina" constraints="titel='Voorbeeld-projecten'">
   		<mm:createalias>projects</mm:createalias>
	</mm:listnodes>
   <mm:listnodes type="pagina" constraints="titel='Jeugdactiviteiten'">
   		<mm:createalias>events</mm:createalias>
	</mm:listnodes>
  4. Hide titles of pages and articles<br/>
  <%
	String [] pageTitle = {
		"Opleiding en ontwikkeling"
		};
	for(int i=0; i<pageTitle.length;i++) {
		%><mm:listnodes type="pagina" constraints="<%= "titel = '" + pageTitle[i]  + "'" %>">
			<mm:setfield name="titel_zichtbaar">0</mm:setfield>
		 </mm:listnodes><%
	}
  String [] articleTitle = {
		"Opleiding en ontwikkeling"
		};
	for(int i=0; i<articleTitle.length;i++) {
		%><mm:listnodes type="artikel" constraints="<%= "titel = '" + articleTitle[i]  + "'" %>">
			<mm:setfield name="titel_zichtbaar">0</mm:setfield>
		 </mm:listnodes><%
	}
  %>
	<% (new nl.leocms.util.MMBaseHelper(cloud)).addDefaultRelations(); %>
	<% (new nl.leocms.content.UpdateUnusedElements()).run(); %>
</mm:log>
</mm:cloud>
