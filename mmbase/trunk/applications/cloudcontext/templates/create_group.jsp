<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp"
%><%@include file="import.jsp" %>
<mm:content language="$language">
<mm:import id="url">edit_user.jsp</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
 <h1><%=m.getString("create_group")%></h1>

 <%@include file="you.div.jsp" %>


  <form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_group.jsp</mm:param></mm:url>" method="post">
   <table>
    <mm:createnode id="newnode" type="mmbasegroups" makeuniques="true">
    <mm:fieldlist type="edit">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
    </mm:createnode>
    <tr>
     <td>Create associated security context</td>
     <td>
      <input type="checkbox" name="createcontext" />
      <input name="contextname" />
      </tr>
      <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
      <mm:node referid="newnode">
       <input type="hidden" name="group" value="<mm:field name="number" />" />
      </mm:node>
     </tr>
   </table>
   </form>
  </mm:cloud>  
  <a href="<mm:url referids="parameters,$parameters" page="." />">Back</a>
</mm:content>
   