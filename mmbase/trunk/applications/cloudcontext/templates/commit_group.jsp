<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp" %><html>
<mm:import externid="group" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node id="group" referid="group">
  <head>
    <title>Commit group <mm:field name="gui()" /></title>
   <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
  </head>
  <body>
 <h1><mm:field name="gui()" /></h1>
  <%@include file="you.div.jsp" %>

   <table>
    <mm:fieldlist type="edit">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="useinput" /></td></tr>
    </mm:fieldlist>
    <mm:import externid="createcontext" /> 
    <mm:present referid="createcontext">
      <mm:import externid="contextname" /> 
      <mm:createnode type="mmbasecontexts">
      </mm:createnode>
    </mm:present>
   <mm:import id="operations" vartype="list">create,read,write,delete</mm:import>
   <mm:functioncontainer argumentsdefinition="org.mmbase.security.implementation.cloudcontext.builders.Groups.GRANT_ARGUMENTS">
     <mm:listnodes id="thiscontext" type="mmbasecontexts">  
       <mm:param name="context"><mm:field name="name" /></mm:param>
       <mm:stringlist referid="operations">
         <mm:param name="operation"><mm:write /></mm:param>
         <mm:import id="right" externid="$_:$thiscontext" />
         <mm:compare referid="right" value="on">
            <mm:function node="group" name="grant" />
         </mm:compare>
         <mm:compare referid="right" value="on" inverse="true">
             <mm:function node="group" name="revoke" />
         </mm:compare>
       </mm:stringlist>
   <tr><td></td></tr>
   </mm:listnodes>
   </mm:functioncontainer>
   </table>
   </mm:node>

  <mm:write referid="group" jspvar="group" vartype="node">
 <% response.sendRedirect("index_groups.jsp?group=" + group.getNumber()); %>
  </mm:write>
  </mm:cloud>
  </body>
</html>
