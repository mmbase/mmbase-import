<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="age" required="true" %>
<%@ attribute name="defaultage" %>

<%-- Deze tagfile wordt door andere tagfiles gebruikt en moet
     niet aangeroepen worden vanuit de wizards --%>

<c:if test="${empty age}"><c:set var="age">${defaultage}</c:set></c:if>

  <option value="0" <c:if test="${age eq 0}">selected="selected"</c:if>>0 dagen</option>
  <option value="1" <c:if test="${age eq 1}">selected="selected"</c:if>>1 dag</option>
  <option value="7" <c:if test="${age eq 7}">selected="selected"</c:if>>1 week</option>
  <option value="31" <c:if test="${age eq 31}">selected="selected"</c:if>>1 maand</option> 
  <option value="183" <c:if test="${age eq 183}">selected="selected"</c:if>>1/2 jaar</option>
  <option value="365" <c:if test="${age eq 365}">selected="selected"</c:if>>1 jaar</option>
  <option value="1096" <c:if test="${age eq 1096}">selected="selected"</c:if>>3 jaar</option>
  <option value="999999" <c:if test="${age eq 999999}">selected="selected"</c:if>>alles</option>