<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%><mm:content language="$language" type="text/html" expires="0">
<mm:import id="url">create_context.jsp</mm:import>
<mm:cloud loginpage="login.jsp" rank="$rank">
  <h1><%=getPrompt(m,"create_context")%></h1>
  <%@include file="you.div.jsp" %>
  
  <form name="form" action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_context.jsp</mm:param></mm:url>" method="post">
  <table>
    <mm:fieldlist fields="name" nodetype="mmbasecontexts">
      <mm:import id="extra">onkeyup="document.forms['form'].elements['groupname'].value = this.value;"</mm:import>
      <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo options="extra:$extra" type="input" /></td></tr>
    </mm:fieldlist>
    <mm:fieldlist fields="description" nodetype="mmbasecontexts">
      <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
    <input type="hidden" name="context" value="new" />
    <tr>
      <td><%=getPrompt(m, "createassociatedgroup")%></td>
      <td>
        <input type="checkbox" name="creategroup" />
        <input name="groupname" />
      </tr>
      <tr><td>&nbsp;</td><td><input type="submit" name="create" value="<%=getPrompt(m,"submit")%>" /></td></tr>
    </table>
  </form>
</mm:cloud>
<a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_contexts.jsp</mm:param></mm:url>"><%=getPrompt(m, "back")%></a>
</mm:content>