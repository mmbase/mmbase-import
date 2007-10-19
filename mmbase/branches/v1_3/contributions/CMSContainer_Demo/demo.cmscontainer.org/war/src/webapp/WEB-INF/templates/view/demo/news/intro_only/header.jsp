<%@ include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="heading">
  <mm:cloud method="asis">
    <mm:node number="${contentchannel}">              
      <h2><mm:field name="name"/></h2>
    </mm:node>
  </mm:cloud>
</div>
  
<div class="content">
  <div>