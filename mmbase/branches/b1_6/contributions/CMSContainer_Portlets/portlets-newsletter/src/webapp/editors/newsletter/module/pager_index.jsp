<pg:index>
   <pg:prev>
      <a href="${pageUrl}"> <strong>&#171;</strong></a>
   </pg:prev>
   <pg:pages export="pageNumber,pageUrl">
      <c:choose>
         <c:when test="${currentPage == pageNumber}">
            <b>${pageNumber}</b>
         </c:when>
         <c:otherwise>
            <a href="${pageUrl}">[${pageNumber}]</a>
         </c:otherwise>
      </c:choose>
   </pg:pages>
   <pg:next>
     <a href="${pageUrl}"><strong>&#187;</strong></a>
   </pg:next>
</pg:index>