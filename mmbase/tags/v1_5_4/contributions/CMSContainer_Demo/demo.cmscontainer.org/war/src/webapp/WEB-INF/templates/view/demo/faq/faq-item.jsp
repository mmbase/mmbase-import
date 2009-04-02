<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:cloud>
  <mm:import externid="elementId" required="true" />
  <mm:node number="${elementId}" notfound="skip">
    <div class="heading">
      <h2><mm:field name="title"/></h2>
    </div>
    <div class="content">
      <mm:field name="question" escape="none">
        <mm:isnotempty>
          <p><mm:write /></p>
        </mm:isnotempty>
      </mm:field>
      <mm:field name="body" escape="none">
        <mm:isnotempty>
          <p class="body"><mm:write /></p>
        </mm:isnotempty>
      </mm:field>
    </div>
  </mm:node>
</mm:cloud>