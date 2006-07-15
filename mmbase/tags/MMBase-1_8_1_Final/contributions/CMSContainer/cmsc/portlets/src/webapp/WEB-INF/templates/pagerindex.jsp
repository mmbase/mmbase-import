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