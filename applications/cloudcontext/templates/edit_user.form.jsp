<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_user.jsp</mm:param></mm:url>" method="post">
<table>
  <mm:fieldlist type="edit" fields="owner">
    <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo type="input" /></td></tr>
  </mm:fieldlist>
  <mm:fieldlist fields="lastlogon?">
    <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo type="guivalue" /></td></tr>
  </mm:fieldlist>
  <mm:field name="username">
    <mm:compare value="<%=cloud.getUser().getIdentifier()%>" inverse="true">
    <tr>
      <td><%=getPrompt(m,"groups")%></td>
      <td>
        <select name="_groups"  size="15" multiple="multiple">
          <mm:relatednodes id="ingroups" type="mmbasegroups" searchdir="source">
            <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:relatednodes>
          <mm:unrelatednodes type="mmbasegroups" searchdir="source" role="contains">
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
          <mm:relatednodes type="mmbaseranks">
            <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:relatednodes>
          <mm:unrelatednodes type="mmbaseranks">
            <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
          </mm:unrelatednodes>
        </select>
      </td>
    </tr>
    </mm:compare>
    <mm:compare value="<%=cloud.getUser().getIdentifier()%>">
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
            WARN. User may not edit nodes created by himself.
          </mm:booleanfunction>
        </mm:functioncontainer>
      </mm:node>
    </td>
  </tr>
</table>
<mm:import id="back">index_users.jsp</mm:import>
<%@include file="groupOrUserRights.table.jsp" %>
</form>
