<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import from="request" externid="attribute" jspvar="attribute" vartype="string" />
<%=request.getAttribute(attribute)%>

