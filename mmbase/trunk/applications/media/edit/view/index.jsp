<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*"
%><%@include file="../config/read.jsp" %>
<mm:locale language="$config.lang"><mm:cloud jspvar="cloud" loginpage="../login.jsp"><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="../style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="../style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<mm:import externid="origin">media.myfragments</mm:import>
<mm:import externid="type">mediafragments</mm:import>
<mm:import externid="owner"      />
<mm:import externid="searchvalue"/>


<mm:import id="ownerconstraints"><mm:write referid="owner"><mm:isnotempty><mm:write referid="type" />.owner LIKE '%<mm:write />%'</mm:isnotempty></mm:write></mm:import>

<mm:import id="textconstraints"><mm:write referid="searchvalue"><mm:isnotempty><mm:isnotempty referid="ownerconstraints"> AND </mm:isnotempty><mm:write referid="type" />.title LIKE '%<mm:write />%'</mm:isnotempty></mm:write></mm:import>



<body  onload="init('search');">
  <table>
    <tr><th colspan="3">Result of type <mm:write value="$type" /></th></tr> 
    <tr><th>Titel</th><th>Items en URL's</th><th>Eigenaar</th></tr>
  <mm:list nodes="$origin" path="pools,$type" max="100" orderby="${type}.number" directions="down" constraints="$ownerconstraints $textconstraints">
  <mm:context>
  <mm:node  id="fragment" element="$type">
   <mm:nodeinfo id="actualtype" type="type" write="false" />
   <tr class="view"><td><img src="<mm:url page="../media/${actualtype}.gif" />" alt="" /><mm:field name="gui()" /> 
                   </td>
      <td>      
        <a href="<mm:url referids="fragment" page="showurls.jsp" />">URL's</a>
      <mm:context>
      <mm:relatednodes  id="fragment" type="$actualtype" role="parent" directions="source">
          <br /><mm:field name="title" /> <a href="<mm:url referids="fragment" page="showurls.jsp" />">URL's</a>
          <ul>
      <mm:related  path="posrel,${actualtype}2" fields="posrel.pos" orderby="posrel.pos">
          <mm:context>
         <mm:node id="fragment" element="${actualtype}2">
             <li><mm:field name="title" /> <a href="<mm:url referids="fragment" page="showurls.jsp" />">URL's</a></li>
          </mm:node>
       </mm:context>
       </mm:related>
          </ul>
       </mm:relatednodes>
       </mm:context>
      </td>
      <td>
        <mm:field name="owner" />
      </td>
  </tr>
  </mm:node>
  </mm:context>
  </mm:list>
  </table>
  <!-- p id="colofon">
    <img src="../images/mmbase.png" />
  </p-->
</body>
</html>
</mm:cloud>
</mm:locale>