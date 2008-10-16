<%@ include file="/WEB-INF/templates/portletglobals.jsp" %>
<!-- start simplelistHeader -->
<!-- list -->
<div class="content">
  <mm:cloud method="asis">
    <mm:node number="${contentchannel}">              
       <c:choose>
          <c:when test="${not empty portletTitle}">
             <h3>${portletTitle}</h3>
          </c:when>
          <c:otherwise>
             <h3><mm:field name="name"/></h3>
          </c:otherwise>
       </c:choose>
    </mm:node>
  </mm:cloud>
  <div class="divider3"></div>
  <div class="itemlist">
    <div class="list">
      <ul>