<%
String cacheKey = rubriekId + "~" + paginaID + "~" + refererId  
        + "~" + articleId + "~" + offsetId + "~" + poolId + "~" + periodId 
        + "~" + locationId + "~" + productId + "~" + departmentId + "~" + programId + "~" + shop_itemId + "~" + abcId + "~" + projectId + "~" + imageId
		    + "~" + educationId + "~" + keywordId + "~" + providerId + "~" + competenceId
        + "~" + eventId + "~" + termSearchId + "~" + eTypeId + "~" + pCategorieId + "~" + pAgeId + "~" + nReserveId + "~" + eDistanceId + "~" + eDurationId
        + "~" + printPage;
String groupName = "page" + paginaID;
if(isPreview || !"".equals(actionId)) {
   cacheKey += "~preview";
   expireTime = 0;
   newsExpireTime = 0;
}
%>