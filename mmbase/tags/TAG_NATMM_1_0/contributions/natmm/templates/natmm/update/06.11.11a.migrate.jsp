<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
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
<% log.info("Changing posrel.pos field from -1 to positive value in pages related to 'shop' node to correct design of Webwinkel Rubriek. First page in this rubriek should be 'Welkom in de Winkel' page, that has value of posrel.pos field equals to '1'."); %>
<% int iMaxValueOfPosrelPos = 0; %>
<mm:list nodes="shop" path="rubriek,posrel,pagina">
  <mm:field name="posrel.pos" jspvar="dummy" vartype="Integer" write="false">
    <% if (dummy.intValue()>iMaxValueOfPosrelPos) {
          iMaxValueOfPosrelPos = dummy.intValue();
      } %>
  </mm:field>
</mm:list>
<mm:list nodes="shop" path="rubriek,posrel,pagina" constraints="posrel.pos='-1'">
  <mm:node element="pagina">
    <mm:listrelations type="rubriek">
      <mm:setfield name="pos"><%= ++iMaxValueOfPosrelPos %></mm:setfield>
    </mm:listrelations>
  </mm:node>
</mm:list>
<% log.info("Creating an editwizard object which points to this pagina_products.xml."); %>
<mm:createnode type="editwizards" id="editwizards_id">
    <mm:setfield name="name">producten pagina</mm:setfield>
    <mm:setfield name="wizard">config/pagina/pagina_products</mm:setfield>
</mm:createnode>
<% log.info("Relate this editwizard to the template object with url 'shoppincart.jsp'"); %>
<mm:list nodes="shop" path="rubriek,posrel,pagina,gebruikt,paginatemplate">
  <mm:present referid="node">
    <mm:remove referid="node"/>
  </mm:present>
  <mm:node element="paginatemplate" id="node">
    <% boolean flag=false; %>		
    <mm:related path="related,editwizards">
      <% flag=true; %>
    </mm:related>
    <% if (!flag) {%>
      <mm:createrelation role="related" source="node" destination="editwizards_id"/>
    <% } %>	
  </mm:node>	
</mm:list>
<% log.info("For all pages related to 'shop' node check on field verwijderbaar"); %>
<mm:list nodes="shop" path="rubriek,posrel,pagina">
  <mm:node element="pagina" >
    <mm:setfield name="verwijderbaar">1</mm:setfield>
  </mm:node>	
</mm:list>
<% log.info("Adding style to 'shop' node. Setting level field of 'shop' node to 2."); %>
<mm:node number="shop" id="shop">
  <mm:setfield name="level">2</mm:setfield>
  <mm:setfield name="style">hoofdsite/themas/naardermeer.css</mm:setfield>
</mm:node>
<% log.info("Deleting old shop rubriek and the related page"); %>
<mm:node number="winkel_rubriek" notfound="skipbody">
  <mm:relatednodes type="pagina">
    <mm:deletenode />
  </mm:relatednodes>
  <mm:deletenode />
</mm:node>
<% log.info("Adding relations between 'shop' node and vereniging Naturmonumenten rubriek"); %>
<% String sConstraints="rubriek.naam='Vereniging Natuurmonumenten'"; %>
<mm:list path="rubriek" constraints="<%= sConstraints %>">	
  <mm:node element="rubriek" id="parent">
    <mm:createrelation role="parent" source="parent" destination="shop">
      <mm:setfield name="pos">6</mm:setfield>
    </mm:createrelation>
  </mm:node>	
</mm:list>	
<% log.info("Renaming pagina 'Zoekresultaten' to 'Zoeken in de webwinkel'"); %>
<% sConstraints="pagina.titel='Zoekresultaten'"; %>
<mm:listnodes type="pagina" constraints="<%= sConstraints %>">
  <mm:setfield name="titel">Zoeken in de webwinkel</mm:setfield>
</mm:listnodes>
<% log.info("For paginatemplates migrated from WebShop rename url field"); %>
<%
String [] templateToRename = {
	"templates/homepage.jsp",
  "templates/products.jsp",
  "templates/shoppingcart.jsp",
  "templates/search.jsp",
};
String [] templateNewUrl = {
  "shophome.jsp",
  "shopitems.jsp",
  "shoppingcart.jsp",
  "shopsearch.jsp",
};
String [] templateName = {
  "webwinkel home",
  "webwinkel productoverzicht",
  "winkelwagen",
  "webwinkel zoek"
};
for(int i=0;i<templateToRename.length;i++) {
  sConstraints = "paginatemplate.url = '" + templateToRename[i] + "'"; 
  %>
  <mm:listnodes type="paginatemplate" constraints="<%= sConstraints %>">
      <mm:setfield name="url"><%= templateNewUrl[i] %></mm:setfield>
      <mm:setfield name="naam"><%= templateName[i] %></mm:setfield>
      <mm:setfield name="systemtemplate">0</mm:setfield>   
      <mm:setfield name="dynamiclinklijsten">0</mm:setfield>    
      <mm:setfield name="dynamicmenu">0</mm:setfield>    
      <mm:setfield name="contenttemplate">0</mm:setfield>   
  </mm:listnodes>
  <%
}
log.info("replace shop artikel template by default one"); %>
<mm:listnodes type="paginatemplate" constraints="paginatemplate.url = 'artikel.jsp'">
  <mm:node id="default_artikel_template" />
  <mm:listnodes type="paginatemplate" constraints="paginatemplate.url = 'templates/article.jsp'">
    <mm:related path="gebruikt,pagina">
      <mm:node element="pagina" id="p" />
      <mm:createrelation source="p" destination="default_artikel_template" role="gebruikt" />
    </mm:related>
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
</mm:listnodes>
<% log.info("Add the editors/discounts.jsp as a wizard to the shopping cart."); %>
<mm:createnode type="editwizards" id="new_ed">
  <mm:setfield name="name">Aanbiedingen</mm:setfield>
  <mm:setfield name="type">jsp</mm:setfield>
  <mm:setfield name="wizard">/editors/discounts.jsp</mm:setfield>
</mm:createnode>
<mm:list path="paginatemplate" constraints="paginatemplate.url='shoppingcart.jsp'">
  <mm:node element="paginatemplate" id="paginatemplate"/>
  <mm:createrelation role="related" source="paginatemplate" destination="new_ed"/>
</mm:list>
</body>
</html>
</mm:log>
</mm:cloud>
