<%
String cacheKey = rubriekId + "~" + paginaID + "~" + refererId  
        + "~" + articleId + "~" + offsetId + "~" + poolId + "~" + periodId 
        + "~" + locationId + "~" + productId + "~" + departmentId + "~" + programId + "~" + shop_itemId + "~" + abcId + "~" + projectId
		    + "~" + educationId + "~" + keywordId + "~" + providerId + "~" + competenceId
        + "~" + eventId + "~" + termSearchId + "~" + eTypeId + "~" + pCategorieId + "~" + pAgeId + "~" + nReserveId + "~" + eDistanceId + "~" + eDurationId;
String groupName = "page" + paginaID;
if(isPreview) {
   cacheKey += "~preview";
   expireTime = 0;
}
%>