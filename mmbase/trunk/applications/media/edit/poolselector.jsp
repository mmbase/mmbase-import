<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:locale language="$config.lang">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<html>
  <head>
    <title></title>
    <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;" />" language="javascript"><!--help IE--></script>
  </head>
  <mm:cloud jspvar="cloud" method="asis">
  <body class="left">
    <mm:import id="current">edit</mm:import>
    <%@include file="submenu.jsp" %>
    <hr />
   <mm:node number="media.streams">
   <h1><mm:field name="html(name)" /></h1>
   <p><mm:field name="html(description)" /></p>
   <h1><%=m.getString("category")%></h1>   
    <ul>
    <mm:related path="parent,pools2" orderby="pools2.name">
      <mm:context>
       <mm:node id="origin" element="pools2">
         <li><a href="javascript:setContentFrame('<mm:url referids="origin" page="edit.jsp" />');"><mm:field name="name" /></a></li>
       </mm:node>
       </mm:context>
    </mm:related>
    </ul>

   </mm:node>
  </body>
  </mm:cloud>
</html>
</mm:locale>