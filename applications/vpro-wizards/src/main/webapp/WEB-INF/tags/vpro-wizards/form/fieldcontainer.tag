<%--
    This is a wrapper tag for custom fields. if you want to be completely free, but the
    fields should go with the layout of the other fields, use this tag
--%>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="label" %>

<div class="inputBlock">
    <div class="fieldName">${label}</div>
    <div class="fieldValue"><jsp:doBody/> </div>
</div>
