<!-- Print the paging data. -->
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>

<%
   if (nodeList == null) {
      // do nothing
   }
   else if (resultCount.intValue() == 0) {
      %>Geen resultaten gevonden<%
   }
   else {
      int resultsPerPage = 25;
      String resultsPerPageString = PropertiesUtil.getProperty("repository.search.results.per.page");
      if (resultsPerPageString != null && resultsPerPageString.matches("\\d+")) {
         resultsPerPage = Integer.parseInt(resultsPerPageString);
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
                  %><a href="javascript:setOffset('0');">&lt;&lt;</a>&nbsp;<%
               }
               for (int i = firstPage  ; i < lastPage ; i++) {
                  %><a href="javascript:setOffset('<%=i%>');" <%=(i == offset.intValue())? "style=\"color:red\"":""%>><%=i+1%></a><%
               }
               if (lastPage < maxPage ) {
                  %>&nbsp;<a href="javascript:setOffset('<%=maxPage - 1%>');">&gt;&gt;</a><%
               }
         %>
      </td>
   </tr>
</table>
