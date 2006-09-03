<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.08.31"); %>
	<mm:listnodes type="users" constraints="password='inactive'">
	   <mm:deletenode deleterelations="true" />
   </mm:listnodes>
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
	<% (new nl.leocms.util.MMBaseHelper(cloud)).addDefaultRelations(); %>
	<% (new nl.leocms.content.UpdateUnusedElements()).run(); %> 
</mm:log>
</mm:cloud>
