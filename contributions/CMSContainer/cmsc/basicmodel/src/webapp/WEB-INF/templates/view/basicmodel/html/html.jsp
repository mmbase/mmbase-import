<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@taglib prefix="l1cms" tagdir="/WEB-INF/tags" %>
<mm:cloud>
   <mm:import externid="elementId" required="true" from="request" />
   <mm:node number="$elementId" notfound="skip">
      <mm:field name="code" escape="none"/>
   </mm:node>
</mm:cloud>