
<%org.mmbase.security.implementation.cloudcontext.Caches.waitForCacheInvalidation(); %>
<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_user.jsp</mm:param></mm:url>" method="post">
<table>
  <mm:fieldlist type="edit" fields="owner">
    <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo options="noautocomplete" type="input" /><mm:fieldinfo type="errors" /></td></tr>
  </mm:fieldlist>
  <mm:cloudinfo type="user" id="cloudusername" write="false" />
  <mm:field name="username">
    <mm:compare referid2="cloudusername" inverse="true">
    <tr>
      <td><%=getPrompt(m,"groups")%></td>
      <td>
        <select name="_groups"  size="15" multiple="multiple">
          <mm:relatednodes id="ingroups" type="mmbasegroups" searchdir="source" orderby="name">
            <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:relatednodes>
          <mm:unrelatednodes type="mmbasegroups" searchdir="source" role="contains" orderby="name">
            <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:unrelatednodes>
        </select>
        <a href="<mm:url referids="parameters,$parameters">
          <mm:param name="url">index_groups.jsp</mm:param>
          <mm:relatednodes referid="ingroups">
            <mm:param name="group"><mm:field name="number" /></mm:param>
          </mm:relatednodes>
          </mm:url>"><%=getPrompt(m,"view_groups")%>
        </a>
      </td>
      <td><%=getPrompt(m,"rank")%></td>
      <td>
        <select name="_rank" size="15">
          <mm:relatednodes type="mmbaseranks" orderby="name">
            <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:relatednodes>
          <mm:unrelatednodes type="mmbaseranks" orderby="name">
            <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:unrelatednodes>
        </select>
      </td>
    </tr>
    </mm:compare>
    <mm:compare referid2="cloudusername">
    <tr>
      <td><%=getPrompt(m,"groups")%></td>
      <td>
        <mm:relatednodes id="ingroups" type="mmbasegroups" searchdir="source">
          <mm:nodeinfo type="gui" /><mm:last inverse="true">, </mm:last>
        </mm:relatednodes>
        <a href="<mm:url referids="parameters,$parameters">
          <mm:param name="url">index_groups.jsp</mm:param>
          <mm:relatednodes referid="ingroups">
            <mm:param name="group"><mm:field name="number" /></mm:param>
          </mm:relatednodes>
          </mm:url>"><%=getPrompt(m,"view_groups")%>
        </a>
      </td>
      <td><%=getPrompt(m,"rank")%></td>
      <td>
        <mm:relatednodes type="mmbaseranks">
          <mm:nodeinfo type="gui" />
        </mm:relatednodes>
      </td>
    </tr>

    </mm:compare>
  </mm:field>
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
