<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.evenementen.EventNotifier" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
	<mm:list nodes="natuurherstel" path="rubriek1,parent,rubriek2" searchdir="destination">
    <mm:remove referid="this_image" />
    <mm:remove referid="this_pagina" />
    <mm:node element="rubriek2" jspvar="rubriek">
      <mm:related path="contentrel,images">
        <mm:deletenode element="contentrel" />
        <mm:node element="images" id="this_image" />
        <mm:list nodes="<%= rubriek.getStringValue("number") %>" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP" max="1">
          <mm:node element="pagina" id="this_pagina">
            <mm:related path="posrel,images">
              <mm:node element="posrel">
                 <mm:setfield name="pos">2</mm:setfield>
              </mm:node>
            </mm:related>
          </mm:node>
          <mm:createrelation source="this_pagina" destination="this_image" role="posrel">
            <mm:setfield name="pos">1</mm:setfield>
          </mm:createrelation>
        </mm:list>
      </mm:related>
    </mm:node>
  </mm:list>
  <mm:createnode type="editwizards" id="home_wizard">
		<mm:setfield name="name">homepage (natuurherstel)</mm:setfield>
		<mm:setfield name="description">Bewerk de homepage van een natuurherstel website</mm:setfield>
		<mm:setfield name="type">wizard</mm:setfield>
		<mm:setfield name="wizard">config/pagina/pagina_natnh_home</mm:setfield>
		<mm:setfield name="nodepath">pagina</mm:setfield>
		<mm:setfield name="fields">titel,embargo,verloopdatum</mm:setfield>
		<mm:setfield name="orderby">titel</mm:setfield>
		<mm:setfield name="directions">down</mm:setfield>
		<mm:setfield name="pagelength">50</mm:setfield>
		<mm:setfield name="maxpagecount">100</mm:setfield>
		<mm:setfield name="searchfields">titel</mm:setfield>
		<mm:setfield name="search">yes</mm:setfield>
	</mm:createnode>
  <mm:listnodes type="paginatemplate" constraints="naam = 'homepage (natuurherstel)'">
    <mm:related path="related,editwizards">
      <mm:deletenode element="related" />
    </mm:related>
		<mm:node id="home_template" />
		<mm:createrelation source="home_template" destination="home_wizard" role="related" />
	</mm:listnodes>
  Migrated images from rubriek to homepage.
</mm:log>
</mm:cloud>

