<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:import id="url">edit_user.jsp</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">

<h1><%=getPrompt(m,"create_user")%></h1>

 <%@include file="you.div.jsp" %>

<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_user.jsp</mm:param></mm:url>" method="post">
 <table>
   <mm:createnode id="newnode" type="mmbaseusers">
      <mm:fieldlist type="edit">
        <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
      </mm:fieldlist>
    </mm:createnode>
   <tr>
     <td>Groups</td>
     <td>
      <select name="_groups" size="4" multiple="multiple">
        <mm:listnodes type="mmbasegroups">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:listnodes>
      </select>
     </td>
    </tr>
    <tr>
     <td>Rank</td>
     <td>
      <select name="_rank" size="4">
        <mm:listnodes type="mmbaseranks">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:listnodes>
      </select>
     </td>
    </tr>
    <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
    <mm:node referid="newnode">
      <input type="hidden" name="user" value="<mm:field name="number" />" />
    </mm:node>
   </table>
   </form>
  </mm:cloud>
  <a href="<mm:url referids="parameters,$parameters" page="." ><mm:param name="url">index_users.jsp</mm:param></mm:url>">Back</a>

