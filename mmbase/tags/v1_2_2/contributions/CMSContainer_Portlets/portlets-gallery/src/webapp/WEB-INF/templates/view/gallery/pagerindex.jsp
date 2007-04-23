<%-- 
  JSP fragment displaying a page index, 
  the containing JSP should include it inside a <pg:pager> tag
--%>
<!-- Vorige en volgende lijst -->
<ul class="paginanavigatie">
    <pg:last>
        <c:set var="lastPageNumber">${pageNumber}</c:set>
    </pg:last>
      
	<pg:prev>          
		<li class="vorige"><a href="${pageUrl}">&lt; Vorige ${pageName}</a></li>
	</pg:prev>
	<pg:next>
	   	<li class="volgende"><a href="${pageUrl}">Volgende ${pageName} &gt;</a></li>
	</pg:next> 
	<c:if test="${empty mode}">
		<pg:pages export="pageNumber,pageUrl">
			<c:choose>
		      	<c:when test="${currentPage == pageNumber}">
		        	<li class="active"><span>${pageNumber}</span></li>
		     	</c:when>
		     	<c:otherwise>                
		        	<li><a href="${pageUrl}">${pageNumber}</a></li>
		      	</c:otherwise>
		   	</c:choose>
		</pg:pages>
	</c:if>
	
	<c:if test="${!empty mode}">
		<c:if test="${lastPageNumber > 1}">		
			<li>Foto <pg:page>${pageNumber}</pg:page>: ${imageTitle} <h3>(${lastPageNumber} ${pageName} 's)</h3></li>
		</c:if>	
	</c:if>     
</ul>

<c:if test="${empty mode}">
  <c:if test="${lastPageNumber > 1}">
     <h3>${pageName} <pg:page>${pageNumber}</pg:page> van ${lastPageNumber}</h3>
  </c:if>  
</c:if>