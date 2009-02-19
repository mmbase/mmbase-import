<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:content language="$language">
<mm:import id="url">create_group.jsp</mm:import>
<mm:cloud loginpage="login.jsp"  rank="$rank">
 <h1><%=getPrompt(m,"create_group")%></h1>

 <%@include file="you.div.jsp" %>


  <form name="form" action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_group.jsp</mm:param></mm:url>" method="post">
   <table>
     <mm:fieldlist fields="name" nodetype="mmbasegroups">
       <mm:import id="extra">onkeyup="document.forms['form'].elements['contextname'].value = this.value;"</mm:import>
       <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo options="extra:$extra" type="input" /></td></tr>
     </mm:fieldlist>
     <mm:fieldlist fields="description" nodetype="mmbasegroups">
       <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
     </mm:fieldlist>
     <input type="hidden" name="group" value="new" />
    <tr>
     <td><%=getPrompt(m, "createassociatedcontext")%></td>
     <td>
      <input type="checkbox" name="createcontext" />
      <input name="contextname" />
      </tr>
      <tr><td><input type="submit"  name="submit" value="<%=getPrompt(m, "submit")%>" /></td></tr>
   </table>
   </form>
  </mm:cloud>
  <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_groups.jsp</mm:param></mm:url>"><%=getPrompt(m, "back")%></a>
</mm:content>
