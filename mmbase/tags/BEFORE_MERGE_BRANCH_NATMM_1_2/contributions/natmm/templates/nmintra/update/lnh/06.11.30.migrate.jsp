<%@page import="org.mmbase.bridge.*,nl.leocms.servlets.UrlConverter" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   1. Move Thuispagina to its own rubriek.<br/>
   <mm:createnode type="rubriek" id="home_rubriek">
      <mm:setfield name="naam">Thuispagina</mm:setfield>
      <mm:setfield name="isvisible">Thuispagina</mm:setfield>   
      <mm:setfield name="issearchable">Thuispagina</mm:setfield>   
      <mm:setfield name="level">Thuispagina</mm:setfield>
   </mm:createnode>
   <mm:listnodes type="pagina" constraints="titel = 'Thuispagina'">
      <mm:node id="home_page" />
      <mm:related path="posrel,rubriek">
        <mm:deletenode element="posrel" />
        <mm:node element="rubriek" id="root" />
        <mm:createrelation source="root" destination="home_rubriek" role="parent">
            <mm:setfield name="pos">0</mm:setfield>
        </mm:createrelation>
        <mm:createrelation source="home_rubriek" destination="home_page" role="posrel">
            <mm:setfield name="pos">1</mm:setfield>
         </mm:createrelation>
      </mm:related>
   </mm:listnodes>
   2. Set all news pages to show date<br/>
   <mm:node number="homepage_template">
    <mm:relatednodes type="pagina">
      <mm:setfield name="titel_fra">1</mm:setfield>
    </mm:relatednodes>
   </mm:node>
   <mm:node number="info_template">
    <mm:relatednodes type="pagina">
      <mm:setfield name="titel_fra">1</mm:setfield>
    </mm:relatednodes>
   </mm:node>
   3. Create alias archive for archive rubriek<br/>
   <mm:node number="archive" notfound="skipbody">
    <mm:import id="found_archive_alias" />
   </mm:node>
   <mm:notpresent referid="found_archive_alias">
    <mm:listnodes type="rubriek" constraints="naam = 'Archief'">
      <mm:createalias>archive</mm:createalias>
    </mm:listnodes>
   </mm:notpresent>
   4. Flush OS cache<br/>
   <% UrlConverter.getCache().flushAll(); %>
   <cache:flush scope="application"/>
</mm:log>
</mm:cloud>
