<%@ tag body-content="scriptless" %>
<%@ attribute name="key" rtexprvalue="true" required="true" %>

<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<edit:ui-tabs>
   <edit:ui-tab key="${key}" active="true"/>
</edit:ui-tabs>
