<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.08.24"); %>
	Moving the present articles from contentrel to readmore<br/>
	<mm:listnodes type="pagina" constraints="titel = 'Nieuws en vacatures'">
      <% log.info("1"); %>
	   <mm:node id="vacature_page" />
	   <mm:related path="contentrel,artikel">
	      <mm:field name="contentrel.pos" jspvar="pos" vartype="String" write="false">
      	   <mm:node element="artikel" id="artikel">
         	   <mm:createrelation source="vacature_page" destination="artikel" role="readmore">
         	      <mm:setfield name="pos"><%= pos %></mm:setfield>
         	   </mm:createrelation>
      	   </mm:node>
   	   </mm:field>
   	   <mm:deletenode element="contentrel" />
	   </mm:related>
	   <mm:last>
   	   <mm:createnode type="artikel" id="news_artikel">
            <mm:setfield name="titel">demo nieuws artikel</mm:setfield>
            <mm:setfield name="embargo"><%= ((new java.util.Date()).getTime()/1000 -24*60*60) %></mm:setfield>
            <mm:setfield name="verloopdatum"><%= ((new java.util.Date()).getTime()/1000 +365*24*60*60) %></mm:setfield>
         </mm:createnode>
     	   <mm:createrelation source="vacature_page" destination="news_artikel" role="contentrel" />
      </mm:last>
   </mm:listnodes>
</mm:log>
</mm:cloud>
