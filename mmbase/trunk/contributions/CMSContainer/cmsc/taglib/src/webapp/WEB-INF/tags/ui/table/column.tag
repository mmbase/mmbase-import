<%@ tag body-content="scriptless" %>
<%@ attribute name="titlekey" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
   <c:when test="${tag_op_status eq 'header'}">
      <th>
         <c:choose>
            <c:when test="${not empty titlekey}">
               <fmt:message key="${titlekey}"/>
            </c:when>
            <c:otherwise>
               <jsp:doBody/>
            </c:otherwise>
         </c:choose>
      </th>
   </c:when>
   <c:otherwise>
      <td>
         <jsp:doBody/>
      </td>
   </c:otherwise>
</c:choose>