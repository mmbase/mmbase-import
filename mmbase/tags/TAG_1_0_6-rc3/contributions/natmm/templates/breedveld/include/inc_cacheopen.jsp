<%-- set expire time to one year --%>
<%	int expireTime =  3600*24*365; // cache for one year
	if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } // half hour
%>

<cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" >
<!-- <%= new java.util.Date() %> -->