<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp"
%><%@include file="import.jsp" %>

<mm:import externid="context" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node id="currentcontext" referid="context">
  <mm:context>
   <mm:maywrite>
    <mm:fieldlist type="edit" fields="owner">
       <mm:fieldinfo type="useinput" />
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
            <mm:function write="false" node="currentcontext" name="grant" />
         </mm:compare>
         <mm:compare referid="right" value="on" inverse="true">
             <mm:function write="false" node="currentcontext" name="revoke" />
         </mm:compare>
       </mm:stringlist>
   </mm:listnodes>
   </mm:functioncontainer>
   </mm:context>
        <h1><mm:field name="gui()" /> (commited)</h1>
   <%@include file="context.div.jsp" %>
   </mm:node>
  </mm:cloud>
