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
   Things to be uploaded:<br/>
   1.editors<br/>
   2.dev<br/>
   3.classes<br/>
   4.commons-lang-2.0.jar lib<br/>
   5.editwizards including wizard.xsl<br/>
   Things to be created:<br/>
   5.ew for categories
   Reset embargo and verloopdatum of pages<br/>
   <mm:listnodes type="pagina">
      <mm:setfield name="embargo">1104534000</mm:setfield>
      <mm:setfield name="verloopdatum">2145913200</mm:setfield>
   </mm:listnodes>
   Creating thema's for category in images.<br/>
   <mm:createnode type="thema"><mm:setfield name="naam">Overig</mm:setfield><mm:setfield name="omschrijving">010</mm:setfield></mm:createnode>
   <mm:createnode type="thema"><mm:setfield name="naam">Bezoekerscentrum</mm:setfield><mm:setfield name="omschrijving">020</mm:setfield></mm:createnode>
   <mm:createnode type="thema"><mm:setfield name="naam">Kader</mm:setfield><mm:setfield name="omschrijving">030</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Natuurgebied van de week</mm:setfield><mm:setfield name="omschrijving">040</mm:setfield></mm:createnode>
   <mm:createnode type="thema"><mm:setfield name="naam">Nieuws</mm:setfield><mm:setfield name="omschrijving">050</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Pagina</mm:setfield><mm:setfield name="omschrijving">060</mm:setfield></mm:createnode>
   <mm:createnode type="thema"><mm:setfield name="naam">Route</mm:setfield><mm:setfield name="omschrijving">070</mm:setfield></mm:createnode>
   <mm:createnode type="thema"><mm:setfield name="naam">Drenthe</mm:setfield><mm:setfield name="omschrijving">080</mm:setfield></mm:createnode>
   <mm:createnode type="thema"><mm:setfield name="naam">Flevoland</mm:setfield><mm:setfield name="omschrijving">090</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Friesland</mm:setfield><mm:setfield name="omschrijving">100</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Gelderland</mm:setfield><mm:setfield name="omschrijving">110</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Groningen</mm:setfield><mm:setfield name="omschrijving">120</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Limburg</mm:setfield><mm:setfield name="omschrijving">130</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Noord-Brabant</mm:setfield><mm:setfield name="omschrijving">140</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Noord-Holland</mm:setfield><mm:setfield name="omschrijving">150</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Overijssel</mm:setfield><mm:setfield name="omschrijving">160</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Utrecht</mm:setfield><mm:setfield name="omschrijving">170</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Zeeland</mm:setfield><mm:setfield name="omschrijving">180</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Zuid-Holland</mm:setfield><mm:setfield name="omschrijving">190</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Winkel</mm:setfield><mm:setfield name="omschrijving">200</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Pano</mm:setfield><mm:setfield name="omschrijving">210</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Fun</mm:setfield><mm:setfield name="omschrijving">220</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Banners</mm:setfield><mm:setfield name="omschrijving">230</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Wallpaper</mm:setfield><mm:setfield name="omschrijving">240</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Agenda</mm:setfield><mm:setfield name="omschrijving">250</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Natuurbehoud/televisie</mm:setfield><mm:setfield name="omschrijving">260</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Pano nav</mm:setfield><mm:setfield name="omschrijving">270</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Districtscommissies</mm:setfield><mm:setfield name="omschrijving">290</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Natuurbrief</mm:setfield><mm:setfield name="omschrijving">300</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Banners (oud)</mm:setfield><mm:setfield name="omschrijving">310</mm:setfield></mm:createnode> 
   <mm:createnode type="thema"><mm:setfield name="naam">Voorjaarsforum</mm:setfield><mm:setfield name="omschrijving">320</mm:setfield></mm:createnode> 
   Done.
   </body>
</html>
</mm:cloud>