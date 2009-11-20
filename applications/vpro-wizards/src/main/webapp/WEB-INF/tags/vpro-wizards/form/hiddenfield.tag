<%@ tag body-content="empty"  %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<%@ include file="fieldinit.tagf" %>
<input type="hidden" name="actions[${modifier}${action}][${actionnr}].fields[${field}]" value="${fieldvalue}" id="field_${nodetype}_${field}">
