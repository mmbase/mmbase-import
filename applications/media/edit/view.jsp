<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*,org.mmbase.module.builders.media.ResponseInfo"
%><mm:import externid="language">nl</mm:import>
<%@include file="readconfig.jsp" %>
<mm:locale language="$config.lang"><mm:cloud jspvar="cloud" loginpage="login.jsp"><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<mm:import externid="origin">media.myfragments</mm:import>
<mm:import externid="type">mediafragments</mm:import>
<body  onload="init('search');">
  <table>
    <tr><th colspan="3">Result of type <mm:write value="$type" /></th></tr> 

  <tr><td colspan="3"><%=m.getString("demoinfo")%></td></tr>
  <mm:list nodes="$origin" path="pools,$type" max="10" orderby="${type}.number" directions="down">
  <mm:context>
  <mm:node  element="$type">
  <tr><td><mm:field name="title" /> </td>
      <td>      
      <mm:nodeinfo id="actualtype" type="type" write="false" />
      <img src="<mm:url page="media/${actualtype}.gif" />" alt="" />
      <%@ include file="showurls.jsp" %><br />
      <mm:relatednodes  type="$actualtype" role="parent" directions="source">
          <mm:field name="gui()" />
      <mm:related  path="posrel,${actualtype}2" orderby="posrel.pos">
         <mm:node element="${actualtype}2">
          <br />
           --- <mm:field name="gui()" /> (<%@ include file="showurls.jsp" %>)
          </mm:node>
       </mm:related>
       </mm:relatednodes>
      </td>
      <td>
        <mm:field name="owner" />
      </td>
  </tr>
  </mm:node>
  </mm:context>
  </mm:list>
  </table>
  <p id="colofon">
    <img src="images/mmbase.png" />
  </p>
</body>
</html>
</mm:cloud>
</mm:locale>