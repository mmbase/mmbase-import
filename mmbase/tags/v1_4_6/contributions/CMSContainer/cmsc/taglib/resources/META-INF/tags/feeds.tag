<%@ attribute name="icon"
%><%@ attribute name="mode"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<cmsc:location var="cur" sitevar="site" />
<cmsc:list-navigations origin="${cur}" type="rssfeed" mode="all" var="rssfeeds" />
<c:choose>
	<c:when test="${icon eq 'large'}"><c:set var="icondir">/gfx/mimetypes32/</c:set></c:when>
	<c:otherwise><c:set var="icondir">/gfx/mimetypes/</c:set></c:otherwise>
</c:choose>
<cmsc:staticurl page="${icondir}rss.gif" id="imgIcon" write="false"/>
<c:forEach var="rss" items="${rssfeeds}">
	<cmsc:link dest='${rss}' var="rssurl" />
	<c:choose>
		<c:when test="${mode eq 'link'}">
	<a href="${rssurl}" title="${rss.title}"><img src="${imgIcon}" alt="${_node.title}" />${rss.title}</a>
		</c:when>
		<c:when test="${mode eq 'icon'}">
	<a href="${rssurl}" title="${rss.title}"><img src="${imgIcon}" alt="${rss.title}" /></a>
		</c:when>
		<c:otherwise>
	<link rel="alternate" type="application/rss+xml" href="${rssurl}" title="${rss.title}" /> 
		</c:otherwise>
	</c:choose>
</c:forEach>