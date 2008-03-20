<%@page import="org.mmbase.bridge.*,nl.leocms.servlets.UrlConverter" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   1. Delete expired account.<br/>
	 <mm:listnodes type="users" constraints="account='website_user'">
     <mm:deletenode />
   </mm:listnodes>
   2. Move articles of Interne Nieuws to Thuispagina.<br/>
   <mm:listnodes type="pagina" constraints="titel = 'Thuispagina'">
     <mm:setfield name="bron">0</mm:setfield>
     <mm:setfield name="titel_fra">1</mm:setfield>
     <mm:setfield name="titel_zichtbaar">0</mm:setfield>
     <mm:node id="tp" />
     <mm:listnodes type="pagina" constraints="titel = 'Intern nieuws'">
        <mm:node id="in" />
        <mm:related path="contentrel,artikel">
          <mm:node element="artikel" id="a" />
          <mm:createrelation source="tp" destination="a" role="contentrel" />
          <mm:deletenode element="contentrel" />
        </mm:related>
        <mm:deletenode deleterelations="true" />
     </mm:listnodes>
     <mm:relatednodes type="paginatemplate">
       <mm:setfield name="url">homepage.jsp</mm:setfield>
       <mm:node id="home_template" />
       <mm:relatednodes type="editwizards">
          <mm:setfield name="wizard">config/pagina/pagina_homepage</mm:setfield>
       </mm:relatednodes>
       <mm:listnodes type="editwizards" constraints="wizard = 'config/artikel/artikel_nieuws_nmintra'">
         <mm:node id="ew" />
         <mm:createrelation source="home_template" destination="ew" role="related" />
       </mm:listnodes>
     </mm:relatednodes>
   </mm:listnodes>
   3. Flush OS cache<br/>
   <% UrlConverter.getCache().flushAll(); %>
   <cache:flush scope="application"/>
</mm:log>
</mm:cloud>
