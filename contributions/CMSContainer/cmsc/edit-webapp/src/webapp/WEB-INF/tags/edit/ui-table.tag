<%@ tag body-content="scriptless" %>

<%@ attribute name="items" rtexprvalue="true" required="true" type="java.util.Collection" %>
<%@ attribute name="var" rtexprvalue="false" required="true" type="java.lang.String" %>
<%@ attribute name="size" rtexprvalue="true" required="false" type="java.lang.Integer" %>
<%@ attribute name="requestURI" rtexprvalue="true" required="false" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>

<%@ variable name-from-attribute="var" alias="current" scope="NESTED" variable-class="java.lang.Object" %>
<jsp:useBean id="now" class="java.util.Date" scope="request"/>
<jsp:useBean id="pagingstatus" class="com.finalist.cmsc.paging.PagingUtils" scope="request"/>


<cmsc:property key="repository.search.results.per.page" var="pagesize"/>

<c:choose>
   <c:when test="${fn:length(items) > 0}">
      <%@ include file="ui-table-paging.tagf" %>
      <table>
         <thead class="listheader">
            <c:set var="tag_op_status" value="header" scope="request"/>
            <jsp:doBody/>
         </thead>
         <tbody class="hover">
            <c:set var="tag_op_status" value="body" scope="request"/>
            <c:forEach varStatus="status" var="current" items="${items}">
               <tr <c:if test="${status.count % 2 != 0}">class="swap"</c:if>>
                  <jsp:doBody/>
               </tr>
            </c:forEach>
         </tbody>
      </table>
      <%@ include file="ui-table-paging.tagf" %>
   </c:when>

   <c:when test="${not empty items}">
      <div class="empty"/>
      <fmt:message key="resultset.blank"/>
      </div>
   </c:when>
</c:choose>

