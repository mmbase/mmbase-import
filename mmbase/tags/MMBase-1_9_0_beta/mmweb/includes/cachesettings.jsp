<%
int expireTime = 10; // 3600*24; cache for one day
String cacheKey = componentTitle;
%><mm:write referid="portal" jspvar="portal_number" write="false"><% cacheKey += "~" + portal_number; %></mm:write
><mm:write referid="page" jspvar="page_number" write="false"><% cacheKey += "~" + page_number; %></mm:write>
