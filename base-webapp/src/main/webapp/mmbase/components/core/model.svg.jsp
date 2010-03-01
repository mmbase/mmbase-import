<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,java.util.*"
%><mm:escaper id="svg" type="graphviz">
     <mm:param name="command">dot</mm:param>
 </mm:escaper
><mm:content type="image/svg+xml" postprocessor="svg">
 <mm:include page="model.dot.jsp" />
</mm:content>
