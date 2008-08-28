<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<cmsc:pager maxPageItems="${elementsPerPage}" items="${totalElements}" 
		index="${pagesIndex}" maxIndexPages="${showPages}" isOffset="true"
		export="offset,currentPage=pageNumber">

<c:choose>
	<c:when test="${totalElements == 0}">
		<c:set var="pageInfo">${startPage} - ${endPage}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="pageInfo">${startPage} - ${endPage} (${totalElements})</c:set>
	</c:otherwise>
</c:choose>
<p>${pageInfo}</p>
<p>
   <pg:first unless="current">
      <a href="${pageUrl}"><img border="0" src="<cmsc:staticurl page='/images/pager/arrow_backward_double.gif'/>" alt="" /></a>&nbsp;
   </pg:first>
   <pg:prev>
      <a href="${pageUrl}"><img border="0" src="<cmsc:staticurl page='/images/pager/arrow_backward.gif'/>" alt="" /></a>&nbsp;
   </pg:prev>
   <pg:pages export="pageNumber,pageUrl">
      <c:choose>
         <c:when test="${currentPage == pageNumber}">
            <b>${pageNumber}</b>
         </c:when>
         <c:otherwise>
            <a href="${pageUrl}">${pageNumber}</a>
         </c:otherwise>
      </c:choose>
   </pg:pages>
   <pg:next>
      &nbsp;<a href="${pageUrl}"><img border="0" src="<cmsc:staticurl page='/images/pager/arrow_forward.gif'/>" alt="" /></a>
   </pg:next>
   <pg:last unless="current">
      &nbsp;<a href="${pageUrl}"><img border="0" src="<cmsc:staticurl page='/images/pager/arrow_forward_double.gif'/>" alt="" /></a>
   </pg:last>
</p>

</cmsc:pager>