<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis">
<div
    class="mm_c mm_c_core mm_c_b_components ${requestScope.componentClassName}"
    id="${requestScope.componentId}">

  <mm:haspage page="/mmexamples/index.jsp">
    <jsp:directive.include file="/mmexamples/index.jsp" />
  </mm:haspage>
  <mm:haspage page="/mmexamples/index.jsp" inverse="true">
    <h2>Not present</h2>
  </mm:haspage>
</div>
</mm:cloud>
