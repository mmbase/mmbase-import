<%@taglib  uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@tag import="org.mmbase.framework.ErrorRenderer"
%>
<jsp:directive.attribute name="exception"   required="true" type="java.lang.Exception" />

<mm:present referid="exception">
  <mm:write referid="exception" jspvar="e" vartype="java.lang.Exception">
    <%
    ErrorRenderer.Error error = new ErrorRenderer.Error(500, e);
    error.getErrorReport(out, request, new org.mmbase.util.transformers.Xml());
    %>
  </mm:write>
</mm:present>
