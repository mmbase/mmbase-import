<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp" %><html>


<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">



  <head>
    <title>Create user</title>
   <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
  </head>
  <body>
 <h1>Create user</h1>

 <%@include file="you.div.jsp" %>

  <form action="<mm:url page="commit_user.jsp" />" method="post">
   <table>
    <mm:createnode id="newnode" type="mmbaseusers" makeuniques="true">
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
  <a href="<mm:url page="." />">Back</a>

  </body>
</html>
