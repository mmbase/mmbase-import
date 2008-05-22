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
   Solved issues:<br/>
   - default color for home is set<br/>
   - croppen van de pano<br/>
   - image cropper<br/>
   - http://www.natuurmonumenten.nl/events.jsp?p=393&e=228073 is te breed op de site<br/>
   - 'sluiting aanmelding' toevoegen aan keuze lijst en op default zetten:  2 uur voor activiteit<br/>
   - removed unneccessary max number of relations in editwizard, added warning to other<br/>
   Issues that could not be reproduced:<br/>
   - space in zipcode gives problem for members when trying to log in<br/>
   Subsite1 implementation:<br/>
   - change order of pano and navigation<br/>
   - image in right column<br/>
   - icon in left column
   - footer<br/>
   - people, photo and subhome templates<br/>
   To upload:<br/>
   - classes because of NatMMConfig: CSVReader.java, NatureReservesReader.java, EventNotifier.java and templates e.g. image_crop.jsp<br/>
   - list.jsp<br/>
   - application<br/>
   - builder payment_type<br/>
   - images because filename is set to editable<br/>
   To update:<br/>
   - this update<br/>
   - link pages to Naardermeer<br/>
   - settings for templates photo and smoelenboek<br/>
   - home template and relation to editwizard<br/>
   - payment_type-(posrel)-page, editwizard for payment_type<br/>
   - relation evenement_type posrel to page<br/>
   - relation rubriek contentrel to images<br/>
   - layout, style Naardermeer<br/>
   - layout, style Natuurmonumenten<br/>
   - tekst uit artikel home naar artikel<br/>
   - plaatjes for Naardermeer<br/>
   - jumper for Naardermeer<br/>
   The following SQL statements have to be carried out:<br/>
   ALTER TABLE `v1_images` ADD `filename` VARCHAR( 255 );<br/>
   Remarks:<br/>
   - no breadcrumb in Subsite1 layout
   - larger font-size in footer will push text against table border
   - icons of other styles are still missing
   - namen van de rubrieken moeten korter
   - twee templates in plaats van één
   Things to be done:<br/>
   1. template object with url people.jsp <br/>
   2. page with titel "Smoelenboek" <br/>
   3. "gebruikt" relation between the template object with url people.jsp and the page with titel "Smoelenboek"<br/>
   4. editwizard object which points to this pagina_people.xml <br/>
   5. relating this editwizard to the template object with url people.jsp <br/>
   6. template object with url photo.jsp <br/>
   7. page with titel "Fotoalbum" <br/>
   8. "gebruikt" relation between the template object with url photo.jsp and the page with titel "Fotoalbum" <br/>
   9. relating the editwizard with the name "pagina van genre fun" with template object with url photo.jsp <br/>
   Processing ...<br/>
   <mm:createnode type="paginatemplate" id="pagina_template_id">
      <mm:setfield name="naam">Smoelenboek</mm:setfield>
      <mm:setfield name="url">people.jsp</mm:setfield>
   </mm:createnode>
   <mm:createnode type="pagina" id="pagina_id">
      <mm:setfield name="titel">Smoelenboek</mm:setfield>
   </mm:createnode>
   <mm:createrelation role="gebruikt" source="pagina_template_id" destination="pagina_id"/>
      <mm:createnode type="editwizards" id="editwizards_id">
      <mm:setfield name="name">pagina van genre people</mm:setfield>
      <mm:setfield name="wizard">config/pagina/pagina_people</mm:setfield>
   </mm:createnode>
   <mm:createrelation role="related" source="editwizards_id" destination="pagina_template_id"/>
   <mm:createnode type="paginatemplate" id="photo_template_id">
      <mm:setfield name="naam">Fotoalbum</mm:setfield>
      <mm:setfield name="url">photo.jsp</mm:setfield>
   </mm:createnode>
   <mm:createnode type="pagina" id="photo_pagina_id">
      <mm:setfield name="titel">Fotoalbum</mm:setfield>
   </mm:createnode>
   <mm:createrelation role="gebruikt" source="photo_template_id" destination="photo_pagina_id"/>
   <mm:listnodes type="editwizards" constraints="editwizards.name='pagina van genre fun'" id="fun_editwizards_id">
      <mm:createrelation role="related" source="fun_editwizards_id" destination="photo_template_id"/>
   </mm:listnodes>
   Done.
   </body>
</html>
</mm:cloud>
