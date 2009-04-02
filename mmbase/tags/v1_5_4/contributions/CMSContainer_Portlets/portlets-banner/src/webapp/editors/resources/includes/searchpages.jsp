<% { %>
<%
   int resultsPerPage = 50;
   try {
      resultsPerPage = Integer.parseInt(com.finalist.cmsc.mmbase.PropertiesUtil.getProperty("repository.search.results.per.page"));
   }
   catch (Exception e) {
      // Do nothing here, the value was already set.
   }
%>
 <table border="0" width="100%">
   <tr>
      <td style="width:50%;">
	     <fmt:message key="searchpages.showresults">
	     	<fmt:param><%=offset.intValue() * resultsPerPage%></fmt:param>
	     	<fmt:param><%=offset.intValue() * resultsPerPage + nodeList.size()%></fmt:param>
	     	<fmt:param><%=resultCount.intValue()%></fmt:param>
	     </fmt:message>
      </td>
      <td style="text-align:right;width:50%;">
         <fmt:message key="searchpages.page" />
         <%
               int maxPage = resultCount.intValue() / resultsPerPage + ((resultCount.intValue() % resultsPerPage == 0) ? 0 : 1);
               int minPage = Math.max(0, offset.intValue() - 5);
               int lastPage = Math.min(maxPage, minPage + 10);
               int firstPage= Math.max(0, offset.intValue() - 5 - (10 - (lastPage - minPage)));
               if (firstPage > 0) {
                  %><a href="javascript:setOffset('0');">|&lt;</a>&nbsp;<%
               }
               for (int i = firstPage  ; i < lastPage ; i++) {
                  %><a href="javascript:setOffset('<%=i%>');" <%=(i == offset.intValue())? "style=\"color:black;text-decoration:none\"":""%>><%=i+1%></a>&nbsp;<%
               }
               if (lastPage < maxPage ) {
                  %>&nbsp;<a href="javascript:setOffset('<%=maxPage - 1%>');">&gt;|</a><%
               }
         %>
      </td>
   </tr>
</table>
<% } %>