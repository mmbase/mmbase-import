<%@ attribute name="icon"
%><%@ attribute name="mode"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<c:choose>
	<c:when test="${icon eq 'large'}"><c:set var="icondir">/gfx/mimetypes32/</c:set></c:when>
	<c:otherwise><c:set var="icondir">/gfx/mimetypes/</c:set></c:otherwise>
</c:choose>
<mm:function name="format" id="extension" write="false"/>
<mm:haspage page="${icondir}${extension}.gif" inverse="true">
	<c:set var="extension">_default</c:set>
</mm:haspage>
<mm:attachment id="url" write="false" />
<cmsc:staticurl page="${icondir}${extension}.gif" id="imgIcon" write="false"/>
<c:choose>
	<c:when test="${mode eq 'text'}">
<img src="${imgIcon}" alt="${_node.title}" /><mm:field name="title" />
	</c:when>
	<c:when test="${mode eq 'icon'}">
<img src="${imgIcon}" alt="${_node.title}" />
	</c:when>
	<c:otherwise>
<a href="${url}"><img src="${imgIcon}" alt="${_node.title}" /><mm:field name="title" /></a>
	</c:otherwise>
</c:choose>