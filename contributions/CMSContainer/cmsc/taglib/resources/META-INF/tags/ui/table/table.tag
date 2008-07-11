<%@ tag body-content="scriptless" %>
<%@ attribute name="items" rtexprvalue="true" required="true" %>
<%@ attribute name="var" rtexprvalue="true" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>

   <thead class="listheader">
      <c:set var="tag_op_status" value="header" scope="request"/>
      <jsp:doBody/>
   </thead>
   <tbody class="hover">
      <c:set var="tag_op_status" value="body" scope="request"/>
      <c:set var="useSwapStyle">true</c:set>
      <c:forEach var="${var}" items="${items}">
         <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
            <jsp:doBody/>
         </tr>
         <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
      </c:forEach>
   </tbody>
</table>


