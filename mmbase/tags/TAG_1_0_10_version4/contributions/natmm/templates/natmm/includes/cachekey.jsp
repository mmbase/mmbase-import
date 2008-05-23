<%
String cacheKey = rubriekID + "~"
   + paginaID + "~"
   + adID + "~"
   + artikelID + "~"
   + dossierID + "~"
   + imgID + "~"
   + shop_itemID + "~"
   + natuurgebiedID + "~" 
   + provID + "~"
   + vacatureID + "~"
   + offsetID + "~"
   + evenementID + "~"
   + personID;
String cacheKey_IE = cacheKey + "~IE";
String cacheKey_NS = cacheKey + "~NS";
cacheKey += "~" + (isIE ? "IE" : "NS" );
String groupName = "page" + paginaID;
if(isPreview) {
   cacheKey += "~preview";
   expireTime = 0;
}
%>