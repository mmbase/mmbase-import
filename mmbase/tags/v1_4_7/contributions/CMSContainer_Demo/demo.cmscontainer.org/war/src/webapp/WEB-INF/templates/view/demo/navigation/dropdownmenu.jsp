<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<script src="<cmsc:staticurl page='/js/nav.js' />" type="text/javascript"></script>

<cmsc:location var="cur" sitevar="site" />
<cmsc:list-pages var="pages" origin="${site}" />

<!-- hoofdnavigatie 
     onclick events worden geplaatst in de nav.js. Dus niet in de HTML toevoegen!
-->

<c:remove var="istextmenu"/>
<%-- the maximum number of menu items which can be displayed using the css stylesheet --%>
<c:set var="cssmaxitems" value="5"/>
<c:forEach var="page" items="${pages}" varStatus="status">		
	<c:if test="${status.last}">
		<c:set var="menusize" value="${status.count}"/>					
	</c:if>			
</c:forEach>		

<%-- the text menu is used when there are more menu items than css width allows --%>
<c:if test="${menusize > cssmaxitems}">
	<c:set var="istextmenu" value="true"/>
</c:if>	

<c:choose>
	<c:when test="${istextmenu}">	
		<c:set var="navclass" value="navtext"/>
	</c:when>
	<c:otherwise>
		<c:set var="navclass" value="nav"/>
	</c:otherwise>
</c:choose>

<%-- the maximum number of items that can be displayed in the menu is 5 --%>
<c:if test="${menusize > cssmaxitems}">
	<c:set var="menusize" value="${cssmaxitems}"/>		
</c:if>	

<form action="<cmsc:renderURL page="${SearchResultPage}" window="${SearchResultPortlet}"/>" id="zoekform" method="post">

<ul id="${navclass}">
	<c:forEach var="page" items="${pages}" varStatus="status">
		<c:if test="${status.index < menusize}">		
			<li id="${navclass}0${status.index+1}"><a href="#" title="${page.title}">${page.title}</a></li>
		</c:if>
	</c:forEach>  
        <!-- zoek -->        
	        <li id="nav06">
	        <a href="#" onclick="document.forms['zoekform'].submit()" id="zoeklink">zoek</a>	        
	        	<input type="text" id="topzoek" name="searchText" value="Zoek in de website" onFocus="this.value=''"/>
	        	<input type="hidden" id="searchCategory" name="searchCategory" value="" />
	        	<input type="submit" name="zoekknop" id="zoekknop" value="zoek" />	        
	        </li>             
        <!-- /zoek --> 
</ul>
</form>   
<!-- /hoofdnavigatie -->

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

<!-- /header met navigatie -->