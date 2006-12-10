<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="asis">
<div
  class="component ${requestScope.className}"
  id="${requestScope.componentId}">
  <mm:haspage page="/mmexamples/index.jsp">
    <%@ include file="/mmexamples/index.jsp" %>
  </mm:haspage>
  <mm:haspage page="/mmexamples/index.jsp" inverse="true">
    <h2>Not present</h2>
  </mm:haspage>
</div>
</mm:cloud>
