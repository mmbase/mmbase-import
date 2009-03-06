<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<mm:node number="<%= paginaID %>">
   <mm:relatednodes type="evenement" role="contentrel" jspvar="evenement">
      <mm:redirect page="<%= "/" + ph.getSubDir(cloud,paginaID) + "/SubscribeInitAction.eb" %>" >
         <mm:param name="number"><%= evenement.getStringValue("number") %></mm:param>
         <mm:param name="p"><%= paginaID %></mm:param>
      </mm:redirect>
   </mm:relatednodes>
</mm:node>
</mm:cloud>
