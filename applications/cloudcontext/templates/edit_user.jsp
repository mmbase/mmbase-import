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
  
  <form action="<mm:url page="<mm:url page="commit_user.jsp" />" method="post">
   <table>
    <mm:fieldlist type="edit" fields="owner">
    <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
    <mm:field name="username">
    <mm:compare value="<%=cloud.getUser().getIdentifier()%>" inverse="true">
    <tr>
     <td>Groups</td>
     <td>
      <select name="_groups"  size="15" multiple="multiple">
        <mm:relatednodes id="ingroups" type="mmbasegroups" searchdir="source">
         <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>       
        </mm:relatednodes>
        <mm:unrelatednodes type="mmbasegroups" searchdir="source" role="contains">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:unrelatednodes>
      </select>
      <a href="<mm:url page="index_groups.jsp">
      <mm:relatednodes referid="ingroups">
        <mm:param name="group"><mm:field name="number" /></mm:param>
       </mm:relatednodes>
      </mm:url>">View groups</a>
     </td>
     <td>Rank</td>
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
    </mm:field>
    <input type="hidden" name="user" value="<mm:field name="number" />" />
   </table>
   <%@include file="groupOrUserRights.table.jsp" %>
   </form>

   </mm:node>
  </mm:cloud>
  <a href="<mm:url page="." />">Back</a>
</body>
</html>
