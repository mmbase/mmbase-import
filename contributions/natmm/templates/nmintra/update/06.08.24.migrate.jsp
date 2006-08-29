<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.08.24"); %>
	Moving the present articles from contentrel to readmore<br/>
	<mm:listnodes type="pagina" constraints="titel = 'Nieuws en vacatures'">
	   <mm:node id="news_page" />
	   <mm:relatednodes type="rubriek">
         <mm:node id="peno" />
      </mm:relatednodes>
      <mm:listnodes type="paginatemplate" constraints="url = 'vacature_info.jsp'">
         <mm:node id="template" />
      </mm:listnodes>
      <mm:createnode type="rubriek" id="vacatures">
          <mm:setfield name="naam">Vacatures</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="peno" destination="vacatures" role="parent">
        <mm:setfield name="pos">60</mm:setfield>
      </mm:createrelation>      
      <mm:createnode type="pagina" id="internal">
         <mm:setfield name="titel">Vacatures binnen NM</mm:setfield>
         <mm:setfield name="verwijderbaar">1</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="vacatures" destination="internal" role="posrel">
          <mm:setfield name="pos">10</mm:setfield>
      </mm:createrelation>
      <mm:createrelation source="internal" destination="template" role="gebruikt" />
      <mm:createnode type="pagina" id="external">
         <mm:setfield name="titel">Vacatures externe organisaties</mm:setfield>
         <mm:setfield name="verwijderbaar">1</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="vacatures" destination="external" role="posrel">
          <mm:setfield name="pos">20</mm:setfield>
      </mm:createrelation>

      <mm:createrelation source="external" destination="template" role="gebruikt" />
      
	   <mm:related path="contentrel,vacature">
         <mm:node element="vacature" id="vacature">
            <mm:field name="metatags">
               <mm:compare value="interne vacature">
      	         <mm:createrelation source="internal" destination="vacature" role="contentrel" />
      	          <% log.info("+"); %>
      	      </mm:compare>
      	      <mm:compare value="externe vacature">
      	         <mm:createrelation source="external" destination="vacature" role="contentrel" />      	      
      	          <% log.info("+"); %>
      	      </mm:compare>
      	   </mm:field>
   	   </mm:node>
   	   <mm:deletenode element="contentrel" />
         <% log.info("-"); %>
	   </mm:related>
	   
	   <mm:related path="contentrel,artikel">
	      <mm:field name="contentrel.pos" jspvar="pos" vartype="String" write="false">
      	   <mm:node element="artikel" id="artikel">
         	   <mm:createrelation source="news_page" destination="artikel" role="readmore">
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
     	   <mm:createrelation source="news_page" destination="news_artikel" role="contentrel" />
      </mm:last>
      <mm:setfield name="titel">Nieuws</mm:setfield>
      
   </mm:listnodes>
</mm:log>
</mm:cloud>
