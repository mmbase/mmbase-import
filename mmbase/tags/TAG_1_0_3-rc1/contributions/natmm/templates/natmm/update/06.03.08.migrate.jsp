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
   Changes made in this update:<br/>
   1. Moved to MMBase 1.7.4<br/>
   2. Moved to OSCache 2.2, added groups="&lt;%= paginaID %&gt;" to cache tag and added preview<br/>
   3. Added startnodes field to editwizard builder (also in util.PaginaHelper.java), the following SQL query has to be carried out:<br/>
   ALTER TABLE `v1_editwizards` ADD `startnodes` VARCHAR(255);<br/>
   4. Added description to list of editwizards in authorization.forms.CheckBoxTree.java<br/>
   5. CAD users with editwizards now automatically get Pagina-editor tab<br/>
   6. Solved bug with double editwizards for users.<br/>
   7. Solved bug in EvenementForm.createDatesFromPeriod()<br/>
   8. Added inschrijvingen_categorie<br/>
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">vakgenoten of groene verenigingen</mm:setfield></mm:createnode> 
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">maatschappelijke groeperingen</mm:setfield></mm:createnode> 
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">sponsoren</mm:setfield></mm:createnode> 
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">(personeels)verenigingen/bedrijven</mm:setfield></mm:createnode> 
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">scholen</mm:setfield></mm:createnode> 
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">familiegezelschappen, reunies</mm:setfield></mm:createnode> 
   <mm:createnode type="inschrijvings_categorie"><mm:setfield name="name">andere groepen</mm:setfield></mm:createnode>
   <mm:node number="67865" id="menu" />
   <mm:createnode type="editwizards" id="ew">
      <mm:setfield name="name">aanmeldingscategorie</mm:setfield>
      <mm:setfield name="description">Categorie van aanmeldingen (speciaal bedoeld voor groepsexcursies)</mm:setfield>
      <mm:setfield name="wizard">config/inschrijvings_categorie/inschrijvings_categorie</mm:setfield>
      <mm:setfield name="type">wizard</mm:setfield>
      <mm:setfield name="nodepath">inschrijvings_categorie</mm:setfield>
      <mm:setfield name="fields">name</mm:setfield>
      <mm:setfield name="orderby">name</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
      <mm:setfield name="searchfields">name</mm:setfield>
   </mm:createnode>
   <mm:createrelation source="menu" destination="ew" role="posrel" />
   Done.
   </body>
</html>
</mm:cloud>
