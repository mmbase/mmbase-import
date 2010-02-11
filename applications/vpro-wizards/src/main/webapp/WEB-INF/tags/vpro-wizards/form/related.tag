<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><div id="relations" class="relatedview">
  <%-- 'authorized' is set in form:container --%>
  <c:choose>
    <c:when test="${authorized == 'true'}">
      <jsp:doBody/>
    </c:when>
    <c:otherwise><span class='notauthorized'>Not authorized</span></c:otherwise>
  </c:choose>
</div>