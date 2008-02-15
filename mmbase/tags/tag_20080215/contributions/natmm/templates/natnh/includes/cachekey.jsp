<%
String cacheKey = rubriekId +"~"+ paginaID +"~"+  articleId +"~"+  imageId  +"~"+  offsetId;
String groupName = "page" + paginaID;
if(isPreview) {
   cacheKey += "~preview";
   expireTime = 0;
}
%>