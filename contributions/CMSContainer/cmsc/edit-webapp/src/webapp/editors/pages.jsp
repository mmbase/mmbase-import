 <script   language="JavaScript">
 
  function   gotopage()
  {    
      if (document.getElementById('page').value == "")
    alert("please input again !")
  else {
	var max = document.getElementById('max').value;
	var offset = document.getElementById('page').value-1;
		if(offset > max)
			  {var offset = max;}
		else if (offset < 0)
			  {var offset = 0;}
	var status = document.getElementById('status').value;
	var orderby = document.getElementById('orderby').value;
	var extraparams = document.getElementById('extraparams').value;
	 var url = "?status="+status+"&offset="+offset+"&orderby="+orderby+extraparams;
	 window.location.href=url; 
  }
  }
  function enterTo()
	{ 
	  if   (window.event.keyCode   ==   13){ 
		 gotopage();
	  }
  }   
</script>
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
         		<c:if test="${maxPage < 1}">
				1
				</c:if>
				
				<c:if test="${maxPage >= 1}">
				<c:choose>
					<c:when test="${maxPage<13}">
						<c:if test="${offset == null || offset ==0 }">
							<c:forEach var="count" begin="0" end="${maxPage}">
								<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}" 
								<c:if test="${count == 0}">
								style="text-decoration:none"
								</c:if>
								> ${count+1}</a>
							</c:forEach>|
							<a href="?status=${status}&offset=${offset+1}&orderby=${orderby}${extraparams}" style="text-decoration:none"><fmt:message key="pages.next"/>&gt;&gt;</a>
						</c:if>

						<c:if test="${offset >= maxPage-1}">
							<a href="?status=${status}&offset=${offset-1}&orderby=${orderby}${extraparams}" style="text-decoration:none">&lt;&lt;<fmt:message key="pages.previous"/></a>
							<c:forEach var="count" begin="0" end="${maxPage}">
								|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}" 
								<c:if test="${count >= maxPage-1}">
								style="text-decoration:none"
								</c:if>
								> ${count+1}</a>
							</c:forEach>
						</c:if>

						<c:if test="${offset > 0 && offset < maxPage-1}">
							<a href="?status=${status}&offset=${offset-1}&orderby=${orderby}${extraparams}" style="text-decoration:none">&lt;&lt;<fmt:message key="pages.previous"/></a>
								<c:forEach var="count" begin="0" end="${maxPage}">
									|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}" 
									<c:if test="${count == offset}">
									style="text-decoration:none"
									</c:if>
									> ${count+1}</a>
								</c:forEach>|
							<a href="?status=${status}&offset=${offset+1}&orderby=${orderby}${extraparams}" style="text-decoration:none"><fmt:message key="pages.next"/>&gt;&gt;</a>
						</c:if>
					</c:when>

					<c:when test="${maxPage>=13}">
						<!-- offset == null || offset ==0 -->
						<c:if test="${offset == null || offset ==0 }">
							<c:forEach var="count" begin="0" end="2">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}"
								<c:if test="${count == 0}">
									style="text-decoration:none"
								</c:if>>${count+1}</a>
							</c:forEach>
						...
							<c:forEach var="count" begin="${maxPage-2}" end="${maxPage}">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
							</c:forEach>|<a href="?status=${status}&offset=${offset+1}&orderby=${orderby}${extraparams}" style="text-decoration:none"><fmt:message key="pages.next"/>&gt;&gt;</a>
						</c:if>
						
						<!-- offset > 0 && offset <= 5 -->
						<c:if test="${offset > 0 && offset <= 5}">
							<a href="?status=${status}&offset=${offset-1}&orderby=${orderby}${extraparams}" style="text-decoration:none">&lt;&lt;<fmt:message key="pages.previous"/></a>
							<c:forEach var="count" begin="0" end="${offset+2}">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}"
							<c:if test="${count == offset}">
							style="text-decoration:none"
							</c:if>
							>${count+1}</a>
							</c:forEach>
						...
							<c:forEach var="count" begin="${maxPage-2}" end="${maxPage}">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
							</c:forEach>|<a href="?status=${status}&offset=${offset+1}&orderby=${orderby}${extraparams}" style="text-decoration:none"><fmt:message key="pages.next"/>&gt;&gt;</a>
						</c:if>
						
						<!-- offset >5 && offset < maxPage-5-->
						<c:if test="${offset >5 && offset < maxPage-5}">
							<a href="?status=${status}&offset=${offset-1}&orderby=${orderby}${extraparams}" style="text-decoration:none">&lt;&lt;Previous</a>
							<c:forEach var="count" begin="0" end="2">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
							</c:forEach>
						...
							<c:forEach var="count" begin="${offset-2}" end="${offset+2}">
								|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}"
								<c:if test="${count == offset}">
								style="text-decoration:none"
								</c:if>
							>${count+1}</a>
							</c:forEach>
						...
							<c:forEach var="count" begin="${maxPage-2}" end="${maxPage}">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
							</c:forEach>|<a href="?status=${status}&offset=${offset+1}&orderby=${orderby}${extraparams}" style="text-decoration:none"><fmt:message key="pages.next"/>&gt;&gt;</a>
						 </c:if>
						
						<!-- offset >= maxPage-5 && offset < maxPage-->
						<c:if test="${offset >= maxPage-5 && offset < maxPage-1}">
							<a href="?status=${status}&offset=${offset-1}&orderby=${orderby}${extraparams}" style="text-decoration:none">&lt;&lt;<fmt:message key="pages.previous"/></a>
						    <c:forEach var="count" begin="0" end="2">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
							</c:forEach>
						...
						    <c:forEach var="count" begin="${offset-2}" end="${maxPage}">
							|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}"
							<c:if test="${count == offset}">
							style="text-decoration:none"
							</c:if>
							>${count+1}</a>|
							</c:forEach>|<a href="?status=${status}&offset=${offset+1}&orderby=${orderby}${extraparams}" style="text-decoration:none"><fmt:message key="pages.next"/>&gt;&gt;</a>
						</c:if>
						
						<!-- offset==maxPage-->
						<c:if test="${offset >= maxPage-1}">
							<a href="?status=${status}&offset=${offset-1}&orderby=${orderby}${extraparams}" style="text-decoration:none">&lt;&lt;<fmt:message key="pages.previous"/></a>
								<c:forEach var="count" begin="0" end="2">
								|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}">${count+1}</a>
								</c:forEach>
							...
								<c:forEach var="count" begin="${offset-2}" end="${maxPage}">
								|<a href="?status=${status}&offset=${count}&orderby=${orderby}${extraparams}"
								<c:if test="${count >= maxPage-1}">
								style="text-decoration:none"
								</c:if>
								>${count+1}</a>|
								</c:forEach>
						</c:if>
					</c:when>
	         	</c:choose>
				</br>
					<c:forEach var="max" begin="${maxPage}" end="${maxPage}">
						<input type="hidden" name="max" value="${max}"/>
					</c:forEach>
						<input type="hidden" name="status" id="status" value="${status}"/>
						<input type="hidden" name="orderby" id="orderby" value="${orderby}"/>
						<input type="hidden" name="extraparams" id="extraparams" value="${extraparams}"/>
					<fmt:message key="pages.goto"/><input type="text" name="page" size="4" onKeyPress="enterTo()"/>
					<input type="button" name="goto" value="<fmt:message key="pages.go" />"  onclick="gotopage()"/>
			
				</c:if>
			</td>
   </tr>
</table>
  