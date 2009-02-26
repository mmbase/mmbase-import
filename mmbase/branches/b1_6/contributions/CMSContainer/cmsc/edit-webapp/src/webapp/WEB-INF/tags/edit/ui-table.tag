<%@ tag body-content="scriptless" %>

<%@ attribute name="items" rtexprvalue="true" required="true" type="java.util.Collection" %>
<%@ attribute name="var" rtexprvalue="false" required="true" type="java.lang.String" %>
<%@ attribute name="size" rtexprvalue="true" required="true" type="java.lang.Integer" %>
<%@ attribute name="requestURI" rtexprvalue="true" required="false" type="java.lang.String" %>
<%@ attribute name="bulkbox" rtexprvalue="true" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>

<%@ variable name-from-attribute="var" alias="current" scope="NESTED" variable-class="java.lang.Object" %>
<%@ variable name-given="link" scope="NESTED" variable-class="java.lang.String" %>

<jsp:useBean id="now" class="java.util.Date" scope="request"/>
<jsp:useBean id="pagingstatus" class="com.finalist.cmsc.paging.PagingUtils" scope="request"/>

<cmsc:property key="repository.search.results.per.page" var="pagesize"/>
<c:set var="page" value="${not empty param.page ? param.page : 0}"/>
<c:set var="page" value="${not empty param.page ? param.page : 0}" scope="request"/>
<c:set var="pages" value="${ cmsc:ceil(size/pagesize)}"/>

<c:set var="link" value="${requestURI}?"/>
<c:forEach var="element" items="${param}" varStatus="status">
   <c:if test="${(element.key ne 'page') and (element.key ne 'sortby') and (element.key ne 'dir')}">
      <c:set var="link" value="${link}${element.key}=${element.value}&"/>
   </c:if>
</c:forEach>
<c:set var="sortlink" value="${link}" scope="request"/>
<c:choose>
   <c:when test="${fn:length(items) > 0}">
      <%@ include file="ui-table-paging.tagf" %>
      <table>
         <thead class="listheader">
            <c:set var="tag_op_status" value="header" scope="request"/>
            <c:set var="bulkbox" value="${bulkbox}" scope="request"/>
            <c:set var="size" value="${size}" scope="request"/>
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

