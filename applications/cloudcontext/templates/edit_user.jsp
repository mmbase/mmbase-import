<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp" %><html>
<mm:import externid="user" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node referid="user">
  <head>
   <title>View user <mm:field name="gui()" /></title>
   <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
  </head>
  <body>
 <h1><mm:field name="gui()" /></h1>

 <%@include file="you.div.jsp" %>
 <%@include file="navigate.div.jsp" %>
  
  <form action="commit_user.jsp" method="post">
   <table>
    <mm:fieldlist type="edit">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
    <tr>
     <td>Groups</td>
     <td>
      <select name="_groups" size="4" multiple="multiple">
        <mm:relatednodes id="ingroups" type="mmbasegroups">
         <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>       
        </mm:relatednodes>
        <mm:unrelatednodes type="mmbasegroups">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:unrelatednodes>
      </select>
      <a href="<mm:url page="index_groups.jsp">
      <mm:relatednodes referid="ingroups">
        <mm:param name="group"><mm:field name="number" /></mm:param>
       </mm:relatednodes>
      </mm:url>">View groups</a>
     </td>
    </tr>
    <tr>
     <td>Rank</td>
     <td>
      <select name="_rank" size="4">
        <mm:relatednodes type="mmbaseranks">
         <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:relatednodes>
        <mm:unrelatednodes type="mmbaseranks">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:unrelatednodes>
      </select>
     </td>
    </tr>
    <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
    <input type="hidden" name="user" value="<mm:field name="number" />" />
   </table>
   </form>

   </mm:node>
  </mm:cloud>
  <a href=".">Terug</a>
</body>
</html>
