<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="language">nl</mm:import><mm:locale language="$language">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;language=$language" />" language="javascript"><!--help IE--></script>
  </head>
  <mm:cloud>
  <body background="images/bckplaceholder.gif">
   <mm:node number="media.streams">
   <span class="kop"><mm:field name="html(name)" /></span>
   <mm:field name="html(description)" />
   <span class="kop"><mm:nodeinfo nodetype="pools" type="guitype" /></span>
   
    <ul>
    <mm:related path="parent,pools2" orderby="pools2.name">
      <mm:context>
       <mm:node id="origin" element="pools2">
         <li><a href="javascript:setContentFrame('<mm:url referids="origin,language" page="entrancepage.jsp" />');"><mm:field name="name" /></a></li>
       </mm:node>
       </mm:context>
    </mm:related>
    </ul>
   </mm:node>
  </body>
  </mm:cloud>
</html>
</mm:locale>