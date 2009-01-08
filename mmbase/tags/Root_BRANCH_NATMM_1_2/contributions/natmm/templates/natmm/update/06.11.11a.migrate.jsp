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
<%
String sConstraints = "";
log.info("Adding style to 'shop' node. Setting level field of 'shop' node to 2."); %>
<mm:node number="shop" id="shop">
  <mm:setfield name="level">2</mm:setfield>
  <mm:setfield name="style">hoofdsite/themas/naardermeer.css</mm:setfield>
</mm:node>
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
<% log.info("For all pages related to 'shop' node check on field verwijderbaar"); %>
<mm:list nodes="shop" path="rubriek,posrel,pagina">
  <mm:node element="pagina" >
    <mm:setfield name="verwijderbaar">1</mm:setfield>
  </mm:node>	
</mm:list>
<% log.info("Adding relations between 'shop' node and vereniging Naturmonumenten rubriek"); %>
<% sConstraints="rubriek.naam='Vereniging Natuurmonumenten'"; %>
<mm:list path="rubriek" constraints="<%= sConstraints %>">	
  <mm:node element="rubriek" id="parent">
    <mm:createrelation role="parent" source="parent" destination="shop">
      <mm:setfield name="pos">6</mm:setfield>
    </mm:createrelation>
  </mm:node>	
</mm:list>	
<% log.info("Renaming pagina 'Zoekresultaten' to 'Zoeken in de webwinkel'"); %>
<% sConstraints="pagina.titel='Zoekresultaten'"; %>
<mm:listnodes type="pagina" constraints="<%= sConstraints %>" orderby="number" directions="down" max="1">
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
  <mm:listnodes type="paginatemplate" constraints="<%= sConstraints %>"  orderby="number" directions="down" max="1">
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
<% log.info("Migrate intro and tekst field of artikel"); %>
<mm:listnodes type="artikel" orderby="number" directions="down" max="43">
  <mm:remove referid="intro" />
  <mm:field name="intro" id="intro" write="false">
    <mm:isnotempty>
      <mm:setfield name="tekst"><mm:write referid="intro" /></mm:setfield>
      <mm:setfield name="intro"><% %></mm:setfield>
    </mm:isnotempty>
  </mm:field>
</mm:listnodes>
<% log.info("Migrate tekst and omschrijving field of paragraaf"); %>
<mm:listnodes type="paragraaf" orderby="number" directions="down" max="27">
  <mm:remove referid="omschrijving" />
  <mm:field name="omschrijving" id="omschrijving" write="false">
    <mm:isnotempty>
      <mm:setfield name="tekst"><mm:write referid="omschrijving" /></mm:setfield>
      <mm:setfield name="omschrijving"><% %></mm:setfield>
    </mm:isnotempty>
  </mm:field>
</mm:listnodes>
<% 
log.info("Migrate some articles to teasers"); 
String [] migrateToTeaser = {
  "Introtekst home",
  "Kalender 2007",
  "Agenda 2007",
  "Telefonisch bestellen",
  "Kerstkaartje sturen en natuurmonumenten steunen",
  "Intro tekst DVD/video",
  "Welkom agenda/kalender",
  "Welkom ansichtkaart",
  "Welkom natuurgids",
  "Welkom verzamelband",
  "Welkom wandel en fietskaarten",
  "Welkom bij de Jeugdproducten. Leuke en leerzame boeken voor de jonge natuurliefhebber.",
  "Welkom boeken",
  "Welkom wandel en fietskaarten",
  "Meer lezen?",
  "RPI Family Cards",
  "Scholen, bedrijven en kerstkaarten",
  "Bedrijven",
  "Meer boeken",
};
String [] teaserRole =   {"0","2","2","2","2","0","0","0","0","0","0","0","0","0","2","2","2","2","2",};
String [] titleVisible = {"0","1","1","1","1","0","0","0","0","0","0","0","0","0","1","1","1","1","1",};

for(int i=0;i<migrateToTeaser.length;i++) {
  sConstraints = "artikel.titel = '" + migrateToTeaser[i] + "'"; 
  %>
  <mm:listnodes type="artikel" constraints="<%= sConstraints %>"  orderby="number" directions="down" max="1">
    <mm:field name="titel" jspvar="titel" vartype="String" write="false">
    <mm:field name="titel_eng" jspvar="titel_eng" vartype="String" write="false">
    <mm:field name="tekst" jspvar="omschrijving" vartype="String" write="false">
      <mm:remove referid="thisteaser" />
      <mm:createnode type="teaser" id="thisteaser">
        <mm:setfield name="size"><%= titleVisible[i] %></mm:setfield>
        <mm:setfield name="titel_zichtbaar">0</mm:setfield>
        <mm:setfield name="reageer">0</mm:setfield>
        <mm:setfield name="titel"><%= titel %></mm:setfield>
        <mm:setfield name="titel_eng"><%= titel_eng %></mm:setfield>
        <mm:setfield name="omschrijving"><%= omschrijving %></mm:setfield>
      </mm:createnode>
      <mm:related path="contentrel,pagina">
          <mm:remove referid="thispage" />
          <mm:node element="pagina" id="thispage">
          <mm:createrelation source="thispage" destination="thisteaser" role="rolerel">
            <mm:setfield name="pos">1</mm:setfield>
            <mm:setfield name="rol"><%= teaserRole[i] %></mm:setfield>
          </mm:createrelation>
        </mm:node>
      </mm:related>
      <mm:related path="readmore,pagina">
        <mm:field name="readmore.readmore" jspvar="readmore" vartype="String" write="false">
          <mm:remove referid="thispage" />
          <mm:node element="pagina" id="thispage">
            <mm:createrelation source="thispage" destination="thisteaser" role="readmore">
              <mm:setfield name="readmore"><%= readmore %></mm:setfield>
            </mm:createrelation>
          </mm:node>
        </mm:field>
      </mm:related>
      <mm:related path="posrel,images">
        <mm:remove referid="thisimage" />
        <mm:node element="images" id="thisimage">
          <mm:createrelation source="thisteaser" destination="thisimage" role="posrel" />
        </mm:node>
        <mm:deletenode element="posrel" />
      </mm:related>
    </mm:field>
    </mm:field>
    </mm:field>
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <%
}
log.info("Delete some old articles");
String [] articlesToDelete = {
  "Prijsvraag voor kinderen",
  "Welkom verkoopadres",
  "EXTRA GIFT",
  "Bestelling Natuurmonumenten",
  "Website teksten",
  "Link in de rechterkolom winkelwagen",
};
for(int i=0;i<articlesToDelete.length;i++) {
  sConstraints = "artikel.titel = '" +articlesToDelete[i] + "'"; 
  %>
  <mm:listnodes type="artikel" constraints="<%= sConstraints %>" orderby="number" directions="down" max="1">
    <mm:relatednodes type="paragraaf">
       <mm:deletenode deleterelations="true" />
    </mm:relatednodes>
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <%
}
log.info("Move some articles");
String [] articlesToMove = {
  "#NZ# Geen bestelling",
  "#NZ# Uw winkelwagen",
  "#NZ# Dank voor uw bestelling",
};
String [] newTitle = {
  "Geen bestelling",
  "Uw winkelwagen",
  "Dank voor uw bestelling"
};
String [] articlePosition = {
  "1",
  "2",
  "3"
};
for(int i=0;i<articlesToMove.length;i++) {
  sConstraints = "artikel.titel = '" +articlesToMove[i] + "'"; 
  %>
  <mm:listnodes type="artikel" constraints="<%= sConstraints %>" orderby="number" directions="down" max="1">
    <mm:relatednodes type="paragraaf">
      <mm:deletenode deleterelations="true" />
    </mm:relatednodes>
    <mm:related path="contentrel,pagina">
       <mm:node element="contentrel">
          <mm:setfield name="pos"><%= articlePosition[i] %></mm:setfield>
       </mm:node>
    </mm:related>
    <mm:setfield name="titel"><%= newTitle[i] %></mm:setfield>
    <mm:setfield name="titel_zichtbaar">0</mm:setfield>
  </mm:listnodes>
  <%
}

String [] pages = {
  "Welkom in de Winkel", // 1
  "Jeugdproducten", // 2
  "Video/dvd", // 3
  "Boeken", // 4
  "Agenda/kalender", // 5
  "Natuurgidsen- en waaiers", // 6
  "Wandelen en fietsen", // 7
  "Kerstkaarten", // 8
  "Verzamelbanden Natuurbehoud", // 9
  "Verkoopadressen en Bezoekerscentra", // 10
  "Contact", // 11
  "Leveringsvoorwaarden", // 12
  "Disclaimer", // 13

  "Prijsvraag voor kinderen", // 14
  "Telefonisch bestellen", // 15
  "Scholen en bedrijven", // 16
  "Zoeken in de webwinkel", // 17
  "Winkelwagen" // 18
};
String [] pagesPos      = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18" };
String [] pagesVisible  = {"1","1","1","1","1","1","1","1","1", "1", "1", "1", "1", "0", "0", "0", "0", "0" };
for(int i=0;i<pages.length;i++) {
  sConstraints = "pagina.titel = '" +pages[i] + "'"; 
  %>
  <mm:listnodes type="pagina" constraints="<%= sConstraints %>" orderby="number" directions="down" max="1">
    <% if(i==9) { %>
      <mm:remove referid="thispage" />
      <mm:node id="thispage" />
      <mm:createrelation source="shop" destination="thispage" role="posrel" /> 
    <% } %>
    <mm:related path="posrel,rubriek">
       <mm:node element="posrel">
          <mm:setfield name="pos"><%= pagesPos[i] %></mm:setfield>
       </mm:node>
   </mm:related>
   <% if(pagesVisible[i].equals("0")) { %>
      <mm:setfield name="verloopdatum">1166310000</mm:setfield>
   <% } else { %>
      <mm:setfield name="verloopdatum">2145913200</mm:setfield>
   <% }%>
  </mm:listnodes>
  <%
}

String [] pagesToDelete = {
  "Word lid",
  "Privacy",
};
for(int i=0;i<pagesToDelete.length;i++) {
  sConstraints = "pagina.titel = '" +pagesToDelete[i] + "'"; 
  %>
  <mm:listnodes type="pagina" constraints="<%= sConstraints %>" orderby="number" directions="down" max="1">
    <mm:relatednodes type="artikel">
      <mm:relatednodes type="paragraaf">
        <mm:deletenode deleterelations="true" />
      </mm:relatednodes>
      <mm:deletenode deleterelations="true" />
    </mm:relatednodes>
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <%
}
%>
<mm:createnode type="menu" id="ww_menu">
  <mm:setfield name="naam">Webwinkel</mm:setfield>
</mm:createnode>
<% log.info("Create product ew."); %>
<mm:createnode type="editwizards" id="product_ew">
  <mm:setfield name="name">producten</mm:setfield>
  <mm:setfield name="description">Producten in de webwinkel</mm:setfield>
  <mm:setfield name="type">list</mm:setfield>
  <mm:setfield name="wizard">config/items/items_shop_natmm</mm:setfield>
  <mm:setfield name="nodepath">items</mm:setfield>
  <mm:setfield name="fields">titel,id,type,price1,owner,subtitle</mm:setfield>
  <mm:setfield name="orderby">titel</mm:setfield>
  <mm:setfield name="directions">UP</mm:setfield>
  <mm:setfield name="search">yes</mm:setfield>
  <mm:setfield name="searchfields">titel,id,type,price1,owner,subtitle</mm:setfield>
</mm:createnode>
<mm:createrelation role="posrel" source="ww_menu" destination="product_ew">
  <mm:setfield name="pos">10</mm:setfield>
</mm:createrelation>
<% log.info("Create productoverview page ew."); %>
<mm:createnode type="editwizards" id="productpage_ew">
  <mm:setfield name="name">productoverzicht pagina</mm:setfield>
  <mm:setfield name="description">Bewerk deze productoverzicht pagina</mm:setfield>
  <mm:setfield name="type">wizard</mm:setfield>
  <mm:setfield name="wizard">config/pagina/pagina_items_natmm</mm:setfield>
  <mm:setfield name="nodepath">paginatemplate,pagina</mm:setfield>
  <mm:setfield name="fields">pagina.titel,pagina.titel_fra</mm:setfield>
  <mm:setfield name="orderby">pagina.titel</mm:setfield>
  <mm:setfield name="directions">UP</mm:setfield>
  <mm:setfield name="search">yes</mm:setfield>
</mm:createnode>
<% log.info("relate editwizards to the shopitems template"); %>
<mm:list path="paginatemplate" constraints="paginatemplate.url='shopitems.jsp'">
  <mm:node element="paginatemplate" id="template"/>
  <mm:createrelation role="related" source="template" destination="productpage_ew"/>	
</mm:list>
<% log.info("for items move omschrijving to body and metatags to id"); %>
<mm:listnodes type="items">
  <mm:remove referid="omschrijving" />
  <mm:field name="omschrijving" id="omschrijving" write="false">
    <mm:isnotempty>
      <mm:setfield name="body"><mm:write referid="omschrijving" /></mm:setfield>
      <mm:setfield name="omschrijving"><% %></mm:setfield>
    </mm:isnotempty>
  </mm:field>
  <mm:remove referid="metatags" />
  <mm:field name="metatags" id="metatags" write="false">
    <mm:isnotempty>
      <mm:setfield name="id"><mm:write referid="metatags" /></mm:setfield>
      <mm:setfield name="metatags"><% %></mm:setfield>
    </mm:isnotempty>
  </mm:field>
  <mm:remove referid="item" />
  <mm:node id="item" />
  <mm:related path="posrel,keywords">
      <mm:remove referid="keyword" />
      <mm:node element="keywords" id="keyword" />
      <mm:createrelation source="item" destination="keyword" role="related" />
      <mm:deletenode element="posrel" />
   </mm:related>
</mm:listnodes>
<% log.info("Create editwizards for shophome page and relate it to template shophome"); %>
<mm:createnode type="editwizards" id="shophome_ew">
  <mm:setfield name="name">webwinkel homepage</mm:setfield>
  <mm:setfield name="description">Bewerk deze webwinkel homepage</mm:setfield>
  <mm:setfield name="type">wizard</mm:setfield>
  <mm:setfield name="wizard">config/pagina/pagina_shophome</mm:setfield>
  <mm:setfield name="nodepath">paginatemplate,pagina</mm:setfield>
  <mm:setfield name="fields">pagina.titel,pagina.titel_fra</mm:setfield>
  <mm:setfield name="orderby">pagina.titel</mm:setfield>
  <mm:setfield name="directions">UP</mm:setfield>
  <mm:setfield name="search">yes</mm:setfield>
</mm:createnode>
<mm:createnode type="editwizards" id="keywords_ew">
  <mm:setfield name="name">trefwoorden</mm:setfield>
  <mm:setfield name="description">Bewerk de trefwoorden</mm:setfield>
  <mm:setfield name="type">list</mm:setfield>
  <mm:setfield name="wizard">config/keywords/wizard_natmm</mm:setfield>
  <mm:setfield name="nodepath">keywords</mm:setfield>
  <mm:setfield name="fields">word</mm:setfield>
  <mm:setfield name="orderby">word</mm:setfield>
  <mm:setfield name="directions">UP</mm:setfield>
  <mm:setfield name="search">yes</mm:setfield>
  <mm:setfield name="searchfields">word</mm:setfield>
</mm:createnode>
<mm:createrelation role="posrel" source="ww_menu" destination="keywords_ew">
  <mm:setfield name="pos">20</mm:setfield>
</mm:createrelation>
<mm:listnodes type="editwizards" constraints="wizard = 'config/items/items_shop_natmm'">
	<mm:node id="shopitem_ew" />
</mm:listnodes>
<mm:list path="paginatemplate" constraints="paginatemplate.url='shophome.jsp'">
  <mm:node element="paginatemplate" id="shophome_template"/>
  <mm:createrelation role="related" source="shophome_template" destination="shophome_ew"/>	
  <mm:createrelation role="related" source="shophome_template" destination="shopitem_ew"/>	
  <mm:createrelation role="related" source="shophome_template" destination="keywords_ew"/>	
</mm:list>

</body>
</html>
</mm:log>
</mm:cloud>
