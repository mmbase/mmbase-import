<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp" %><html>


<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">



  <head>
    <title>Create group</title>
   <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
  </head>
  <body>
 <h1>Create group</h1>

 <%@include file="you.div.jsp" %>


  <form action="<mm:url page="commit_group.jsp" />"method="post">
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
  <a href="<mm:url page="." />">Back</a>

  </body>
</html>
