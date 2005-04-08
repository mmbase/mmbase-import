<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="getids.jsp" %>
<mm:import externid="msg">-1</mm:import>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
  <div class="contentBody">
    empty
  </div>
</fmt:bundle>
</mm:cloud>
</mm:content>
