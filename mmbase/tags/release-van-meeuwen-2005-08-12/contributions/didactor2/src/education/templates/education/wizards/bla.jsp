<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:import externid="depth" jspvar="depth" vartype="Integer" required="true" />
<%
 String[] values = request.getParameterValues("depth");

 System.err.println("Get request for depth " + depth);
 System.err.println(values[0] + " - " + values[values.length - 1]);

 int newDepth = depth.intValue() - 1;
 if (newDepth > 1) {
%>
 <mm:include page="bla.jsp">
   <mm:param name="depth"><%=newDepth%></mm:param>
 </mm:include>
<%
 }
%>
</mm:content>
