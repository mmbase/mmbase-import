<div class="group">
  <div class="environment">
    <p class="current">
      <%=getPrompt(m,"current_group")%>: <mm:nodeinfo type="gui" />
    </p>
  </div>

  <div class="users">
   <mm:relatednodes id="user" searchdir="destination" role="contains" type="mmbaseusers" orderby="username">
     <mm:first>Users: </mm:first>
     <a href="<mm:url referids="parameters,$parameters,user"><mm:param name="url">edit_user.jsp</mm:param></mm:url>"><mm:nodeinfo type="gui" /></a>
     <mm:last inverse="true">, </mm:last>
   </mm:relatednodes>
 </div>


 <div class="rights">
   <form method="post" action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_group.jsp</mm:param></mm:url>">
   <input type="hidden" name="group" value="<mm:field name="number" />" />
   <table>
    <mm:fieldlist type="edit" fields="owner">
      <mm:maywrite>
       <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo type="input" /></td></tr>
      </mm:maywrite>
      <mm:maywrite inverse="true">
       <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo type="value" /></td></tr>
      </mm:maywrite>
      </mm:fieldlist>

   </table>
   <mm:import id="back">index_groups.jsp</mm:import>
   <%@include file="groupOrUserRights.table.jsp" %>
   </form>
</div>


</div>
