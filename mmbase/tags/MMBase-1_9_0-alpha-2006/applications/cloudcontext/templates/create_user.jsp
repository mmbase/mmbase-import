<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:import id="url">edit_user.jsp</mm:import>
<mm:cloud  loginpage="login.jsp"  rank="$rank">

<h1><%=getPrompt(m,"create_user")%></h1>

<%@include file="you.div.jsp" %>

<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_user.jsp</mm:param></mm:url>" method="post">
 <table>
   <mm:fieldlist type="edit" nodetype="mmbaseusers">
     <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
   </mm:fieldlist>

   <tr>
     <td><%=getPrompt(m, "groups")%></td>
     <td>
      <select name="_groups" size="4" multiple="multiple">
        <%-- if a group with alias 'mayreadallgroup" exists, this group is automiticly including this --%>
        <mm:node  number="mayreadallgroup" notfound="skip">
          <mm:field id="mayall" name="number" />
        </mm:node>

        <mm:listnodes type="mmbasegroups" orderby="name">
         <option value="<mm:field name="number" />" <mm:present referid="mayall"><mm:field name="number"><mm:compare referid2="mayall">selected="selected" </mm:compare></mm:field></mm:present> ><mm:nodeinfo type="gui" /></option>
        </mm:listnodes>
      </select>
     </td>
    </tr>
    <tr>
     <td><%=getPrompt(m, "rang")%></td>
     <td>
      <select name="_rank" size="4">
        <mm:listnodes type="mmbaseranks" orderby="name">
         <option value="<mm:field name="number" />" <mm:field name="name"><mm:compare value="basic user">selected="selected"</mm:compare></mm:field>><mm:nodeinfo type="gui" /></option>
        </mm:listnodes>
      </select>
     </td>
    </tr>
    <tr><td><input type="submit"  name="submit" value="<%=getPrompt(m, "submit")%>" /></td></tr>
    <input type="hidden" name="user" value="new" />

   </table>
   </form>
  </mm:cloud>
  <a href="<mm:url referids="parameters,$parameters" page="." ><mm:param name="url">index_users.jsp</mm:param></mm:url>"><%=getPrompt(m, "back")%></a>

