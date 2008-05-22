<%@page language="java" contentType="text/html;charset=UTF-8"%>

<% 
   int messageNumber = new Integer(request.getParameter("messageNumber")).intValue();
   String messageText = new String("Geen tekst beschikbaar.");
   switch (messageNumber) {
      case 0: messageText="Kies hier de gebieden waarvan je kaarten wilt toevoegen aan je bestelling. Bij de keuzemogelijkheid &#39;natuurgebieden&#39; kunnen meerdere natuurgebieden binnen &eacute;&eacute;n eenheid tegelijkertijd (gebruik de shift toets) worden geselecteerd en dus toegevoegd aan je bestelling. Bij de keuzemogelijkheden &#39;Eenheid/Regio/Provincie&#39;, &#39;Nederland&#39; of &#39;co&ouml;rdinaten&#39; kun je slechts &eacute;&eacute;n gebied per keer toevoegen aan je bestelling."; break;
      case 1: messageText="Er kunnen meerdere kaartsoorten tegelijkertijd worden geselecteerd en dus worden toegevoegd aan je bestelling.<br/>Let op, niet alle kaartsoorten zijn bij elke schaal en/of gebied leverbaar. Bij een niet leverbare keuze krijg je een bericht van de GIS-afdeling. Geef speciale wensen op bij opmerkingen."; break;
      case 2: messageText="Kies hier voor een schaal of een papierformaat. Maak hier ook de keuze of je de kaarten gerold of gevouwen wilt ontvangen en geef ook het aantal exemplaren op.<br/>Let op, niet elke schaal of papierformaat is bij elke kaartsoort en/of gebied leverbaar. Bij een niet-leverbare keuze krijg je een bericht van de GIS-afdeling. Geef speciale wensen op bij opmerkingen."; break;
      case 3: messageText="<center>Vul hier eventueel je speciale wensen in en/of eventuele andere opmerkingen.</center><br/>"; break;
   }
%>

<html>
   <head>
      <title>Informatie</title>
   </head>
   
   <body onBlur="window.close();">
      <center>
         <div style="font-size: 12px;vertical-align: top;line-height: 130%;font-family: arial,sans-serif;text-align: left;">
            <%= messageText %>
         </div>
         
         <div style="padding-top: 10px;">
            <input type="button" value="OK" onclick="window.close();"/>
         </div>
      </center>
   </body>
</html>