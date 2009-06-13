<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:content language="$language" expires="0">
<mm:import externid="context" required="true" />

<mm:cloud  loginpage="login.jsp" jspvar="cloud" rank="$rank">

<mm:compare referid="context" value="new">
  <mm:remove referid="context" />
  <mm:import id="wasnew" />
  <mm:createnode id="context" type="mmbasecontexts" />
</mm:compare>

<mm:node id="currentcontext" referid="context">
  <mm:context>
   <mm:maywrite>
    <mm:fieldlist type="edit" fields="owner">
       <mm:fieldinfo type="useinput" />
    </mm:fieldlist>
   </mm:maywrite>
   <mm:present referid="wasnew">
     <mm:import externid="creategroup" /> 
     <mm:present referid="creategroup">
       <mm:import externid="groupname" /> 
       <mm:createnode id="group" type="mmbasegroups">
         <mm:setfield name="name"><mm:write referid="groupname" /></mm:setfield>
       </mm:createnode>
       <mm:node node="currentcontext">
         <mm:functioncontainer>
           <mm:param name="grouporuser" value="$group" />
           <mm:param name="operation"   value="read" />
           <mm:booleanfunction name="maygrant">
             <mm:voidfunction name="grant" />
           </mm:booleanfunction>
           <mm:param name="operation"   value="write" />
           <mm:booleanfunction name="maygrant">
             <mm:voidfunction name="grant" />
           </mm:booleanfunction>
           <mm:param name="operation"   value="delete" />
           <mm:booleanfunction name="maygrant">
             <mm:voidfunction name="grant" />
           </mm:booleanfunction>
         </mm:functioncontainer>             
       </mm:node>
     </mm:present>
     <!-- if a group with alias 'mayreadallgroup" exists, 'read' rights will be switched on automaticly -->
     <mm:node id="allreadgroup" number="mayreadallgroup" notfound="skip">
       <mm:node referid="context">
         <mm:functioncontainer>
           <mm:param name="grouporuser"><mm:field node="allreadgroup" name="number" /></mm:param>
           <mm:param name="operation">read</mm:param>
           <mm:booleanfunction name="maygrant">
             <mm:voidfunction name="grant" />
           </mm:booleanfunction>
         </mm:functioncontainer>
       </mm:node>
     </mm:node>
   </mm:present>
   <mm:notpresent referid="wasnew">
     <mm:import id="operations" vartype="list"><mm:write referid="visibleoperations" /></mm:import>
     <mm:functioncontainer>
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
   </mm:notpresent>
 </mm:context>
<mm:import id="current">contexts</mm:import>
<%@include file="navigate.div.jsp" %>
<%@include file="you.div.jsp" %>
   <h1><mm:field name="gui()" /> (<%=getPrompt(m, "commited")%>)</h1>
   <%@include file="context.div.jsp" %>
   </mm:node>
  </mm:cloud>
</mm:content>