<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
  <mm:node number="${elementId}">
${elementTitle}<br/>
<mm:field jspvar="intro" name="intro" escape="none" /><br/>
---------<br/>
</mm:node>
</mm:cloud>
</mm:content>