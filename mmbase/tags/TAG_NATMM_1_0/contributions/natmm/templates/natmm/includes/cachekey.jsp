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
   + adID + "~"
   + offsetID;
String cacheKey_IE = cacheKey + "~IE";
String cacheKey_NS = cacheKey + "~NS";
cacheKey += "~" + (isIE ? "IE" : "NS" );
String groupName = "page" + paginaID;
if(isPreview) {
   cacheKey += "~preview";
   expireTime = 0;
}
%>