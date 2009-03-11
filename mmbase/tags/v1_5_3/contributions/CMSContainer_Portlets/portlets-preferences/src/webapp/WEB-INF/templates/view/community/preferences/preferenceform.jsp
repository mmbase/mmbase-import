<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<c:set var="module"> 
   <fmt:message key="community.module.newsletter" />
</c:set>
<c:set var="key1"> 
   <fmt:message key="community.module.newsletter.key.mimitype" />
</c:set>
<c:set var="value1" value=""/> 
<c:set var="key2"> 
   <fmt:message key="community.module.newsletter.key.test" />
</c:set>
<c:set var="value2" value=""/> 
<c:forEach var="preference" items="${preferences}">
   <c:if test="${preference.module == module && preference.key == key1}">
      <c:set var="value1" value="${preference.value}"/>
   </c:if>
   <c:if test="${preference.module == module && preference.key == key2}">
      <c:set var="value2" value="${preference.value}"/>
   </c:if>
</c:forEach>

   <tr>
   <td><c:out value="${module}"/></td>
   <td><c:out value="${key1}"/>
   <input type="hidden" name="${module}_key_1"  value="${key1}"></td>
   <td><input type="text" style="width: 250px" name="${module}_value_1" value="${value1}"></td>
   </tr>

   <tr>
   <td><c:out value="${module}"/></td>
   <td><c:out value="${key2}"/>
   <input type="hidden" name="${module}_key_2"  value="${key2}"></td>
   <td><input type="text" style="width: 250px" name="${module}_value_2" value="${value2}"></td>
   </tr>
