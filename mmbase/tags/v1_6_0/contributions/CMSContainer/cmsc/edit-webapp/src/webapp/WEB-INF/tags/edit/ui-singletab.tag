<%@ tag body-content="scriptless" %>
<%@ attribute name="key" rtexprvalue="true" required="true" %>
<%@ attribute name="action" rtexprvalue="true" required="false" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<edit:ui-tabs>
   <edit:ui-tab key="${key}" active="true">
      <jsp:body>
         ${action}
      </jsp:body>
 </edit:ui-tab>
</edit:ui-tabs>
