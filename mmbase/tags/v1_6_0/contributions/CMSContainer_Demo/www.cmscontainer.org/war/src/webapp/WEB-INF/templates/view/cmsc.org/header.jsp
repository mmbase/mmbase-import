<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<cmsc:location var="cur" sitevar="site" />
<cmsc:list-pages var="pages" origin="${site}" />



<!-- De header met de navigatie -->
<h1 class="header" id="headerImage">&nbsp;</h1>
<!-- hoofdnavigatie 
     onclick events worden geplaatst in de nav.js. Dus niet in de HTML toevoegen!
-->
<form action="//zoek/_pid/links1" id="zoekform" method="post">
<ul id="nav">
	<c:forEach var="page" items="${pages}" varStatus="status">
		<li id="nav0${status.index+1}"><a href="<cmsc:link dest="${page.id}"/>" title="${page.title}">${page.title}</a></li>
		
		<%-- zoek optie --%>
<%-- 		<c:if test="${status.last}">
        <li id="nav06">
	        <a class="" style="display: block;" href="#" onclick="document.forms['zoekform'].submit()" id="zoeklink">Zoek</a>	        
	        <input id="topzoek" name="searchText" value="Zoeken" onfocus="this.value=''" type="text">
	     </li>             
		</c:if>--%>
	</c:forEach>  
</ul>
</form>   

<!-- Elke subvanv zit in een ul met een unieke id en een class="subnav"
        De id wordt gebruikt voor styling dus niet veranderen  -->
<c:forEach var="page" items="${pages}" varStatus="status">
	<cmsc:list-pages var="subpages" origin="${page}" />
	<c:set var="subnavclass" value="subnav0${status.index + 1}"/> 
	<c:if test="${not empty subpages}">
		<ul id="${subnavclass}" class="subnav">
		<c:forEach var="subpage" items="${subpages}">
			<c:set var="external">${subpage.externalurl}</c:set>						
			<c:choose>				
				<c:when test="${empty external}"> 											
					<li><a href="<cmsc:link dest="${subpage.id}"/>" title="<c:out value="${subpage.title}"/>">${subpage.title}</a></li>				
				</c:when>
				<c:otherwise>
					<li><a href="${external}" target="_blank" title="<c:out value="${subpage.title}"/>">${subpage.title}</a></li>				
				</c:otherwise>									
			</c:choose>		
		</c:forEach>  
		</ul>
	</c:if>
</c:forEach>  
