<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
This is a description.
And there is a second line.
And a third...
<%@ tag body-content="empty"  description="This is a description.
And there is a second line.
And a third..." %>

<%@include file="somefragment.tagf"%>
<%@ variable name-from-attribute="title" alias="sometitle" variable-class="java.lang.String" %>
