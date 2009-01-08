<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
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

   Things to be done:<br/>
   1. create 2 typedef: vraagbaak and vraagbaak_keywords <br/>
   2. new typedef from: vraagbaak to: bijlage relation: posrel <br/>
   3. new typedef from: vraagbaak to: afbeelding relation: posrel <br/>
   4. new typedef from: vraagbaak to: paragraaf relation: posrel <br/>
   5. new typedef from: vraagbaak to: item relation: readmore <br/>
   6. new typedef from: vraagbaak to: persoon relation: related (max:1) <br/>
   7. new typedef from: vraagbaak to: contentblok relation: readmore <br/>
   8. new typedef from: afbeelding to: vraagbaak relation: pos4rel <br/>
   9. new typedef from: item to: vraagbaak relation: posrel <br/>
   10. new typedef from: pagina to: vraagbaak relation: readmore <br/>
   11. new typedef from: vraagbaak to: vraagbaak_keywords relation: related <br/>
   12. create 2 paginatemplate: vraagbaak and begrippenlijst <br/>    
   13. create new editwizards: vraagbaak pagina <br/>   
   14. create new editwizards: begrippenlijst pagina <br/>   
   15. create new editwizards: vraagbaak trefwoorden <br/>   
   16. relations from paginatemplates (in 12), to editwizards(vraagbaak pagina) (13) and editwizards(begrippenlijst pagina) (14) <br/>
       relations from users(admin) and menu(Contentelementen) to editwizards(vraagbaak trefwoorden) in (15) <br/>    
   17. new typedef: thema_plot_kaart <br/>
   18. new typerel from: thema_plot_kaart to: images relation: related <br/>
   19. new paginatemplate: Bestelformulier Thema- en Plotkaarten <br/>    
   20. new editwizards: Bewerk ThemaPlotKaarten <br/>    
   21. new menu: ThemaPlotKaarten. create relations for this node, from user(admin) and to editwizard(themaplotkaart)<br/>    
   22. new typedef for bestelling_vastgoed <br/>
   23. new typedef and new reldef for kaart_bestel_regel <br/>
   24. new typerel source: bestellingvastgoed, destination: themaplotkaart, relation: kaartbestelregel<br/>
   25. create new editwizards: vraagbaak artikel <br/>     
 
<br/><br/>

   Processing ...<br/>

1. <br/>


2. <br/>
<mm:listnodes type="typedef" constraints="name='vraagbaak'" max="1" id="type_vraagbaak"/>
<mm:listnodes type="typedef" constraints="name='attachments'" max="1" id="type_bijlage" />
<mm:listnodes type="typedef" constraints="name='images'" max="1" id="type_afbeelding" />
<mm:listnodes type="typedef" constraints="name='paragraaf'" max="1" id="type_paragraaf" />
<mm:listnodes type="typedef" constraints="name='items'" max="1" id="type_item" />
<mm:listnodes type="typedef" constraints="name='persoon'" max="1" id="type_persoon" />
<mm:listnodes type="typedef" constraints="name='contentblocks'" max="1" id="type_contentblock" />
<mm:listnodes type="typedef" constraints="name='pagina'" max="1" id="type_pagina" />
<mm:listnodes type="typedef" constraints="name='vraagbaak_keywords'" max="1" id="type_vraagbaak_keywords" />

<mm:listnodes type="reldef" constraints="sname='posrel'" id="rel_posrel" />
<mm:listnodes type="reldef" constraints="sname='readmore'" id="rel_readmore" />
<mm:listnodes type="reldef" constraints="sname='related'" id="rel_related" />
<mm:listnodes type="reldef" constraints="sname='pos4rel'" id="rel_pos4rel" />

<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_bijlage" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_posrel" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>

3. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_afbeelding" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_posrel" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
4. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_paragraaf" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_posrel" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
5. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_item" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_readmore" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
6. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_persoon" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_related" /></mm:setfield>
      <mm:setfield name="max">1</mm:setfield>
</mm:createnode>
7. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_contentblock" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_readmore" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
8. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_afbeelding" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_pos4rel" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
9. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_item" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_posrel" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
10. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_pagina" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_readmore" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>
11. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_vraagbaak" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_vraagbaak_keywords" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_related" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>

12. <br/>
<mm:createnode type="paginatemplate" id="pagetemplate_vraagbaak">
     <mm:setfield name="naam">vraagbaak</mm:setfield>
     <mm:setfield name="url">vraagbaak.jsp</mm:setfield>
     <mm:setfield name="omschrijving"></mm:setfield>
     <mm:setfield name="systemtemplate">0</mm:setfield>
     <mm:setfield name="dynamiclinklijsten">0</mm:setfield>
     <mm:setfield name="dynamicmenu">0</mm:setfield>
     <mm:setfield name="contenttemplate">0</mm:setfield>
</mm:createnode>
<mm:createnode type="paginatemplate" id="pagetemplate_begrippenlijst">
     <mm:setfield name="naam">begrippenlijst</mm:setfield>
     <mm:setfield name="url">begrippenlijst.jsp</mm:setfield>
     <mm:setfield name="omschrijving"></mm:setfield>
     <mm:setfield name="systemtemplate">0</mm:setfield>
     <mm:setfield name="dynamiclinklijsten">0</mm:setfield>
     <mm:setfield name="dynamicmenu">0</mm:setfield>
     <mm:setfield name="contenttemplate">0</mm:setfield>
</mm:createnode>

13. <br/>
<mm:createnode type="editwizards" id="editwizard_vraagbaakpagina">
     <mm:setfield name="name">vraagbaak pagina</mm:setfield>
     <mm:setfield name="description">Bewerk deze vraagbaak pagina</mm:setfield>
     <mm:setfield name="type">wizard</mm:setfield>
     <mm:setfield name="wizard">config/pagina/pagina_vraagbaak</mm:setfield>
     <mm:setfield name="age">-1</mm:setfield>
     <mm:setfield name="m_distinct">1</mm:setfield>
     <mm:setfield name="orderby">pagina.titel</mm:setfield>
     <mm:setfield name="directions">down</mm:setfield>
     <mm:setfield name="pagelength">50</mm:setfield>
     <mm:setfield name="maxpagecount">100</mm:setfield>
     <mm:setfield name="maxupload">-1</mm:setfield>
</mm:createnode>

14. <br/>
<mm:createnode type="editwizards" id="editwizard_begrippenlijstpagina">
     <mm:setfield name="name">begrippenlijst pagina</mm:setfield>
     <mm:setfield name="description">Bewerk deze begrippenlijst pagina</mm:setfield>
     <mm:setfield name="type">wizard</mm:setfield>
     <mm:setfield name="wizard">config/pagina/pagina_begrippenlijst</mm:setfield>
     <mm:setfield name="age">-1</mm:setfield>
     <mm:setfield name="m_distinct">1</mm:setfield>
     <mm:setfield name="orderby">pagina.titel</mm:setfield>
     <mm:setfield name="directions">down</mm:setfield>
     <mm:setfield name="pagelength">50</mm:setfield>
     <mm:setfield name="maxpagecount">100</mm:setfield>
     <mm:setfield name="maxupload">-1</mm:setfield>
</mm:createnode>

15. <br/>
<mm:createnode type="editwizards" id="editwizard_trefwoordenpagina">
     <mm:setfield name="name">vraagbaak trefwoorden</mm:setfield>
     <mm:setfield name="description">Bewerk de vraagbaak trefwoorden</mm:setfield>
     <mm:setfield name="type">list</mm:setfield>
     <mm:setfield name="wizard">config/vraagbaak_keywords/wizard</mm:setfield>
     <mm:setfield name="nodepath">vraagbaak_keywords</mm:setfield>
     <mm:setfield name="fields">word</mm:setfield>
     <mm:setfield name="orderby">word</mm:setfield>
     <mm:setfield name="directions">up</mm:setfield>
     <mm:setfield name="pagelength">50</mm:setfield>
     <mm:setfield name="maxpagecount">100</mm:setfield>
     <mm:setfield name="maxupload">-1</mm:setfield>
     <mm:setfield name="searchfields">word</mm:setfield>
     <mm:setfield name="search">yes</mm:setfield>
</mm:createnode>


16. <br/>

<mm:listnodes type="users" constraints="account='admin'" max="1" id="user_admin" >
	<mm:createrelation role="gebruikt" source="user_admin" destination="editwizard_trefwoordenpagina" />
</mm:listnodes>

<mm:listnodes type="menu" constraints="naam='Contentelementen'" max="1" id="menu_contentelementen">
	<mm:createrelation role="posrel" source="menu_contentelementen" destination="editwizard_trefwoordenpagina" />
</mm:listnodes>

<mm:createrelation role="related" source="pagetemplate_vraagbaak" destination="editwizard_vraagbaakpagina" />

<mm:createrelation role="related" source="pagetemplate_begrippenlijst" destination="editwizard_begrippenlijstpagina" />

17. <br/>

<mm:listnodes constraints="[name]='thema_plot_kaart'" type="typedef" id="type_themaplotkaart" />


18. <br/>

<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_themaplotkaart" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_afbeelding" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_related" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>


19. <br/>

<mm:createnode type="paginatemplate" id="pagetemplate_plotkaart">
     <mm:setfield name="naam">Bestelformulier Thema- en Plotkaarten</mm:setfield>
     <mm:setfield name="url">KaartenInitAction.eb</mm:setfield>
     <mm:setfield name="omschrijving"></mm:setfield>
     <mm:setfield name="systemtemplate">0</mm:setfield>
     <mm:setfield name="dynamiclinklijsten">0</mm:setfield>
     <mm:setfield name="dynamicmenu">0</mm:setfield>
     <mm:setfield name="contenttemplate">0</mm:setfield>
</mm:createnode>

20. <br/>

<mm:createnode type="editwizards" id="editwizard_themaplotkaart">
     <mm:setfield name="name">Bewerk ThemaPlotKaarten</mm:setfield>
     <mm:setfield name="description"></mm:setfield>
     <mm:setfield name="type">list</mm:setfield>
     <mm:setfield name="wizard">config/thema_plot_kaart/wizard</mm:setfield>
     <mm:setfield name="nodepath">thema_plot_kaart</mm:setfield>
     <mm:setfield name="fields">naam,type_gebied</mm:setfield>
     <mm:setfield name="constraints">[hidden]=0</mm:setfield>
     <mm:setfield name="m_distinct">1</mm:setfield>
     <mm:setfield name="orderby">naam</mm:setfield>
     <mm:setfield name="directions">up</mm:setfield>
     <mm:setfield name="searchfields">naam</mm:setfield>
     <mm:setfield name="search">yes</mm:setfield>
</mm:createnode>


21. <br/>
<mm:createnode type="menu" id="menu_themaplotkaart">
      <mm:setfield name="naam">ThemaPlotKaarten</mm:setfield>
      <mm:setfield name="omschrijving"></mm:setfield>
</mm:createnode>

<mm:createrelation role="posrel" source="menu_themaplotkaart" destination="editwizard_themaplotkaart" />

<mm:listnodes type="users" constraints="account='admin'" max="1" id="user_admin_2" >
	<mm:createrelation role="gebruikt" source="user_admin_2" destination="menu_themaplotkaart" />
</mm:listnodes>

<mm:listnodes type="users" constraints="account='admin'" max="1" id="user_admin_3" >
	<mm:createrelation role="gebruikt" source="user_admin_3" destination="editwizard_themaplotkaart" />
</mm:listnodes>


22. <br/>

<mm:listnodes constraints="[name]='bestelling_vastgoed'" type="typedef" id="type_bestellingvastgoed" />

23. <br/>

<mm:listnodes constraints="[name]='kaart_bestel_regel'" type="typedef" id="type_kaartbestelregel" />

<mm:createnode type="reldef" id="rel_kaartbestelregel">
      <mm:setfield name="sname">kaart_bestel_regel</mm:setfield>
      <mm:setfield name="dname">kaart_bestel_regel</mm:setfield>
      <mm:setfield name="builder"><mm:write referid="type_kaartbestelregel" /></mm:setfield>
      <mm:setfield name="dir">2</mm:setfield>
      <mm:setfield name="sguiname">KaartBestelRegel</mm:setfield>
      <mm:setfield name="dguiname">KaartBestelRegel</mm:setfield>
</mm:createnode>

24. <br/>
<mm:createnode type="typerel">
      <mm:setfield name="snumber"><mm:write referid="type_bestellingvastgoed" /></mm:setfield>
      <mm:setfield name="dnumber"><mm:write referid="type_themaplotkaart" /></mm:setfield>
      <mm:setfield name="rnumber"><mm:write referid="rel_kaartbestelregel" /></mm:setfield>
      <mm:setfield name="max">-1</mm:setfield>
</mm:createnode>

25. <br/>
<mm:createnode type="editwizards" id="editwizard_vraagbaak">
     <mm:setfield name="name">vraagbaak</mm:setfield>
     <mm:setfield name="description">Editwizard voor vraagbaak artikelen</mm:setfield>
     <mm:setfield name="type">list</mm:setfield>
     <mm:setfield name="wizard">config/vraagbaak/vraagbaak</mm:setfield>
     <mm:setfield name="nodepath">vraagbaak</mm:setfield>
     <mm:setfield name="fields">titel,embargo,verloopdatum</mm:setfield>
     <mm:setfield name="age">-1</mm:setfield>
     <mm:setfield name="m_distinct">1</mm:setfield>     
     <mm:setfield name="orderby">titel</mm:setfield>
     <mm:setfield name="directions">up</mm:setfield>
     <mm:setfield name="pagelength">-1</mm:setfield>
     <mm:setfield name="maxpagecount">-1</mm:setfield>
     <mm:setfield name="maxupload">-1</mm:setfield>

</mm:createnode>

<mm:listnodes type="users" constraints="account='admin'" max="1" id="user_admin_4" >
   <mm:createrelation role="gebruikt" source="user_admin_4" destination="editwizard_vraagbaak" />
</mm:listnodes>



<br/><br/>   
      Done.     
      
   </body>
</html>
</mm:cloud>
