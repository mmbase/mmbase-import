<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp" %><html>
<mm:import externid="context" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node id="context" referid="context">
  <head>
    <title>Commit context <mm:field name="gui()" /></title>
   <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
  </head>
  <body>
 <h1><mm:field name="gui()" /></h1>
  <%@include file="you.div.jsp" %>

   <table>
   <mm:maywrite>
    <mm:fieldlist type="edit" fields="owner">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="useinput" /></td></tr>
    </mm:fieldlist>
   </mm:maywrite>
   <mm:import id="operations" vartype="list">create,read,write,delete</mm:import>
   <mm:functioncontainer argumentsdefinition="org.mmbase.security.implementation.cloudcontext.builders.Contexts.GRANT_ARGUMENTS">
     <mm:listnodes id="thisgroup"  type="mmbasegroups">  
       <mm:param name="grouporuser"><mm:field name="number" /></mm:param>
       <mm:stringlist referid="operations">
         <mm:param name="operation"><mm:write /></mm:param>
         <mm:import id="right" externid="$_:$thisgroup" />
         <mm:compare referid="right" value="on">
            <mm:function node="context" name="grant" />
         </mm:compare>
         <mm:compare referid="right" value="on" inverse="true">
             <mm:function node="context" name="revoke" />
         </mm:compare>
       </mm:stringlist>
   <tr><td></td></tr>
   </mm:listnodes>
   </mm:functioncontainer>
   </table>
   </mm:node>

<mm:write referid="context" jspvar="node" vartype="node">
 <% response.sendRedirect(response.encodeRedirectURL("index_contexts.jsp?context=" + node.getNumber())); %>
</mm:write>

  </mm:cloud>
  </body>
</html>
