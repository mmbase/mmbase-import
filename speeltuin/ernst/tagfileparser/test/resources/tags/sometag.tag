<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="description"  >
This is a description.
And there is a second line.
And a third...</c:set>
<%@ tag body-content="empty"  description="${description}" %>

<%@include file="somefragment.tagf"%>
<%@ variable name-from-attribute="title" alias="sometitle" variable-class="java.util.Date" %>
