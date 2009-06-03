
<%org.mmbase.security.implementation.cloudcontext.Caches.waitForCacheInvalidation(); %>
<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_user.jsp</mm:param></mm:url>" method="post">
<table>
  <mm:fieldlist type="edit" fields="owner">
    <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo options="noautocomplete" type="input" /><mm:fieldinfo type="errors" /></td></tr>
  </mm:fieldlist>
  <input type="hidden" name="user" value="<mm:field name="number" />" />
  <tr>
    <td colspan="2">
      <mm:field id="defaultcontext" name="defaultcontext" write="false" />
      <mm:field id="usertocheck"    name="number"         write="false" />
      <mm:node number="$defaultcontext">
        <mm:functioncontainer>
          <mm:param name="operation"    value="write" />
          <mm:param name="usertocheck"  value="$usertocheck" />
          <mm:booleanfunction inverse="true" name="may">
            <%=getPrompt(m, "maynoteditself")%>. (<mm:field name="name" />)
          </mm:booleanfunction>
        </mm:functioncontainer>
      </mm:node>
    </td>
  </tr>
</table>
<mm:import id="back">index_users.jsp</mm:import>
<%@include file="groupOrUserRights.table.jsp" %>
</form>
