<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*"
%><%@include file="../config/read.jsp" %>
<mm:locale language="$config.lang"><mm:cloud jspvar="cloud" method="asis"><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
<mm:import externid="max">10</mm:import>
<mm:import externid="offset">0</mm:import>

<body  onload="init('search');">
  <table>
    <tr><th colspan="3">Result of type <mm:nodeinfo nodetype="$type" type="guitype" /></th></tr> 
    <tr><th>Titel</th><th>Items en URL's</th><th><%=m.getString("owner")%></th></tr>
    <mm:listnodescontainer path="pools,$type" element="$type">
      <mm:maxnumber value="$max" />
      <mm:offset    value="$offset" />
      <mm:write referid="origin">
        <mm:isnotempty>
          <mm:constraint field="pools.number" value="$_" />
       </mm:isnotempty>
      </mm:write>
      <mm:constraint field="owner" operator="LIKE" value="%$owner%" />
      <mm:composite operator="OR">
        <mm:constraint field="title" operator="LIKE" value="%$searchvalue%" />
        <mm:constraint field="subtitle" operator="LIKE" value="%$searchvalue%" />
        <mm:constraint field="intro" operator="LIKE" value="%$searchvalue%" />
        <mm:constraint field="body" operator="LIKE" value="%$searchvalue%" />
      </mm:composite>
      <mm:sortorder  field="number" direction="down" />
      <mm:listnodes id="fragment">
        <mm:nodeinfo id="actualtype" type="type" write="false" />
        <tr class="view">
          <td>
            <img src="<mm:url page="../media/${actualtype}.gif" />" alt="" /> <mm:field name="gui()" /> 
          </td>
          <td>      
            <a href="<mm:url referids="fragment" page="showurls.jsp" />">URL's</a>
            <mm:context>
              <mm:relatednodes  id="fragment" type="mediafragments" role="posrel" searchdir="destination">
                <br /><mm:field name="title" /> <a href="<mm:url referids="fragment" page="showurls.jsp" />">URL's</a>
                <ul>
                  <mm:related  path="posrel,mediafragments2" fields="posrel.pos" orderby="posrel.pos" searchdir="destination" >
                    <mm:context>
                      <mm:node id="fragment" element="mediafragments2">
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
      </mm:listnodes>
      <tr class="view">
        <td colspan="100">
          <mm:context>
            <mm:previousbatches>
              <mm:write id="newoffset" write="false" />
              <a href="<mm:url referids="origin,type,owner,searchvalue,max,newoffset@offset" />"><mm:index /></a>|
            </mm:previousbatches>
          </mm:context>
          --
          <mm:context>
            <mm:nextbatches>
              <mm:first>|</mm:first>
              <mm:write id="newoffset" write="false" />
              <a href="<mm:url referids="origin,type,owner,searchvalue,max,newoffset@offset" />"><mm:index  /></a>
              <mm:last inverse="true">|</mm:last>
            </mm:nextbatches>          
          </mm:context>
        </td>
      </tr>
    </mm:listnodescontainer>
  </table>
  <!-- p id="colofon">
    <img src="../images/mmbase.png" />
  </p-->
</body>
</html>
</mm:cloud>
</mm:locale>