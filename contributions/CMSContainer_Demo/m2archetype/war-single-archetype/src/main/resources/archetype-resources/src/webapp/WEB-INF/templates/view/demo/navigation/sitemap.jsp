<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
#set( $dollar = "$" )
<cmsc:location sitevar="cursite" var="curpage" />
<cmsc:list-pages origin="${cursite}" var="sites" />

<c:forEach var="site" items="${sites}" varStatus="siteStatus">
		<div id="column${dollar}{siteStatus.index % 5 +1}">
			<div class="title"><h2><a href="<cmsc:link dest="${site}"/>"><c:out value='${site.title}' /></a></h2>
			</div>
			
			<cmsc:list-pages var="pagesOne" origin="${site}" />
			<c:forEach var="pageOne" items="${pagesOne}"  varStatus="siteStatus">
				<c:if test="${siteStatus.first}">
					<ul class="sitemaplist">
				</c:if>
		        <c:choose>
		           <c:when test="${dollar}{not empty pageOne.externalurl}">
		              <c:set var="urlOne"><cmsc:link dest="${pageOne.externalurl}" /></c:set>
		           </c:when>
		           <c:otherwise>
		              <c:set var="urlOne"><cmsc:link dest="${pageOne}"/></c:set>
		           </c:otherwise>
		         </c:choose>
				<li class="sublv1"><a href="${urlOne}"><c:out value='${pageOne.title}' /></a></li>
	
				<cmsc:list-pages var="pagesTwo" origin="${pageOne}" />
				<c:forEach var="pageTwo" items="${pagesTwo}"  varStatus="pageStatus">
			        <c:choose>
			           <c:when test="${dollar}{not empty pageTwo.externalurl}">
			              <c:set var="urlTwo"><cmsc:link dest="${pageTwo.externalurl}" /></c:set>
			           </c:when>
			           <c:otherwise>
			              <c:set var="urlTwo"><cmsc:link dest="${pageTwo}"/></c:set>
			           </c:otherwise>
			         </c:choose>
					<li class="sublv2"><a href="${urlTwo}"><c:out value='${pageTwo.title}' /></a></li>
				</c:forEach>
	
				<c:if test="${siteStatus.last}">
					</ul>
				</c:if>
	
			</c:forEach>
		
			<div class="scheiding"></div>
		</div>
</c:forEach>