<%@page language="java" contentType="text/html;charset=UTF-8" 
><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
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
   <span class="kop"><mm:nodeinfo nodetype="pools" type="guitype" /></span>

    <ul>
    <mm:list nodes="media.streams"  path="pools,posrel,pools2" orderby="posrel.pos">
      <mm:context>
       <mm:node id="origin" element="pools2">
         <li><a href="javascript:setContentFrame('<mm:url referids="origin,language" page="entrancepage.jsp" />');"><mm:field name="name" /></a></li>
       </mm:node>
       </mm:context>
    </mm:list>
    </ul>
  </body>
  </mm:cloud>
</html>
</mm:locale>