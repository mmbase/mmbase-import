<%@tag import="org.mmbase.framework.ErrorRenderer"
%><%@attribute name="exception"   required="true" type="java.lang.Throwable"
%><%@attribute name="showversion" type="java.lang.Boolean"
%><%@attribute name="showsession" type="java.lang.Boolean"
%><%@attribute name="requestignore" type="java.lang.String"
%><%@attribute name="sessionignore" type="java.lang.String"
%><%

ErrorRenderer.Error error = new ErrorRenderer.Error(500, (Exception) jspContext.getAttribute("exception"));
error.setShowVersion((Boolean) jspContext.getAttribute("showversion"));
error.setShowSession((Boolean) jspContext.getAttribute("showsession"));
error.setRequestIgnore((String) jspContext.getAttribute("requestignore"));
error.setSessionIgnore((String) jspContext.getAttribute("sessionignore"));
error.getErrorReport(out, request, new org.mmbase.util.transformers.Xml());
%>
