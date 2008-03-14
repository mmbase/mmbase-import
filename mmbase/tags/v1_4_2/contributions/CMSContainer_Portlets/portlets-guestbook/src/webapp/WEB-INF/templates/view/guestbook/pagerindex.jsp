<%-- 
  JSP fragment displaying a page index, 
  the containing JSP should include it inside a <pg:pager> tag
--%>
<div class="pagination">
    <pg:last>
        <c:set var="lastPageNumber">${pageNumber}</c:set>
    </pg:last>
    <c:if test="${lastPageNumber > 1}">
       <p>
           ${pageName}
           <pg:page>
               ${pageNumber}
           </pg:page> 
           van ${lastPageNumber}
       </p>
    </c:if>
    
    <p>
       <pg:first unless="current">
          <a href="${pageUrl}">&lt;&lt;</a>|&nbsp;
       </pg:first>
       <pg:prev>
          <a href="${pageUrl}">&lt;</a>|&nbsp;
       </pg:prev>
       <pg:pages export="pageNumber,pageUrl">
          <c:choose>
             <c:when test="${currentPage == pageNumber}">
                <b>${pageNumber}</b>|&nbsp;
             </c:when>
             <c:otherwise>
                <a href="${pageUrl}">${pageNumber}</a>|&nbsp;
             </c:otherwise>
          </c:choose>
       </pg:pages>
       <pg:next>
          <a href="${pageUrl}">&gt;</a>|&nbsp;
       </pg:next>
       <pg:last unless="current">
          <a href="${pageUrl}">&gt;&gt;|</a>
       </pg:last>
    </p>
</div>