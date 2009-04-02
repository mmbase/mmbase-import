<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="requestURI" rtexprvalue="true" required="false" type="java.lang.String" %>

<c:set var="link" value="${requestURI}?"/>
<c:forEach var="element" items="${param}" varStatus="status">
   <c:if test="${(element.key ne 'page') and (element.key ne 'sortby') and (element.key ne 'dir')}">
      <c:set var="link" value="${link}${element.key}=${element.value}&"/>
   </c:if>
</c:forEach>
<c:if test="${not empty param.page}">
   <c:set var="link" value="${link}page=${param.page}"/>
</c:if>
