<%@ tag body-content="scriptless" %>
<%@ attribute name="key" rtexprvalue="true" required="true" %>

<%@ taglib prefix="cmsc-ui" uri="http://finalist.com/cmsc-ui" %>
<cmsc-ui:tabs>
   <cmsc-ui:tab key="${key}" active="true"/>
</cmsc-ui:tabs>
