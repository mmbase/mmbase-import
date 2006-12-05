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
   1. added vacature.jsp + editwizards<br/>
   2. changed natuurgebieden.jsp by putting related natuurgebied in include<br/>
   3. added new design for wallpaper.jsp<br/>
   4. added pagina-related-email<br/>
   5. fixed bug with sending of list with subscriptions from /editors/evenementen/subscribe.jsp<br/>
   6. page title is now always shown<br/>
   7. prevented SubscribeAction.sendConfirmEmail from sending email to empty emailaddresses<br/>
   8. removed includes/searchresult.jsp (seems to be an old lucene demo file)<br/>
   9. removed " " around search terms of Lucene in zoek_hashsets.jsp<br/>
   10. links on link page are now sorted on position (removed second orderby)<br/>
   11. solved bug in news page on Naardermeer<br/>
   12. changed icon for booking by telephone (renamed events_parents_status)<br/>
   13. switched on booking via internet<br/>
   14. added email stats<br/>
   15. change bug with title in route_pop, logo's could not be added to "in het kort" because this is a single text field.<br/>
   16. added the possibility to search on memberid based on lastname and zipcode and/or housenumber

   Things to be done:<br/>
   1. menu object for Email statistieken<br/>
   <mm:createnode type="menu">
      <mm:setfield name="naam">Formulier statistieken</mm:setfield>
      <mm:setfield name="url">../util/email_stats.jsp</mm:setfield>
      <mm:setfield name="rank">administrator</mm:setfield>
      <mm:setfield name="pos">30</mm:setfield>
   </mm:createnode>
   2. remove email with empty to field<br/>
   <mm:listnodes type="email" constraints="email.to=''">
      <mm:deletenode deleterelations="true" />
   </mm:listnodes>
   Done.
   </body>
</html>
</mm:cloud>
