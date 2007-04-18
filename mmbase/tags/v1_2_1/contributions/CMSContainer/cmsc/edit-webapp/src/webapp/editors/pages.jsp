 <table border="0" width="100%">
   <tr>
      <td style="width:50%;">
	     <fmt:message key="searchpages.showresults">
 	     	<fmt:param>${offset * resultsPerPage +1}</fmt:param>
	     	<fmt:param>${(listSize > (offset+1) * resultsPerPage)?((offset+1) * resultsPerPage):listSize }</fmt:param>
	     	<fmt:param>${listSize}</fmt:param>
	     </fmt:message>
      </td>
      <td style="text-align:right;width:50%;">
         <fmt:message key="searchpages.page" />
         	<c:set var="maxPage" value="${listSize/resultsPerPage - ((listSize > 0 && listSize mod resultsPerPage == 0)?1:0)}"/>
         	<c:forEach var="count" begin="0" end="${maxPage}">
         		<c:choose>
	         		<c:when test="${(count == 0 && offset == null) || count == offset}">
	         			${count+1}
	    	     	</c:when>
	    	     	<c:otherwise>
		         		<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
	         		</c:otherwise>
	         	</c:choose>
         	</c:forEach>
      </td>
   </tr>
</table>