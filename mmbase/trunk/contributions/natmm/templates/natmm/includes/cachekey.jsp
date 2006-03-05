<%
String cacheKey = rubriekID + "~"
   + paginaID + "~"
   + dossierID + "~"
   + artikelID + "~"
   + evenementID + "~"
   + natuurgebiedID + "~" 
   + provID + "~"
   + vacatureID + "~"
   + imgID + "~"
   + personID + "~"
   + offsetID;
String cacheKey_IE = cacheKey + "~IE";
String cacheKey_NS = cacheKey + "~NS";
cacheKey += "~" + (isIE ? "IE" : "NS" );
%>