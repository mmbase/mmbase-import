<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:content language="$language">
<mm:import externid="group" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">


<mm:compare referid="group" value="new">
  <mm:remove referid="group" />
  <mm:import id="wasnew" />
  <mm:createnode id="group" type="mmbasegroups" />
</mm:compare>



<mm:node id="group" referid="group">
  <mm:context>
    <mm:fieldlist type="edit" fields="owner">
      <mm:fieldinfo type="useinput" />
    </mm:fieldlist>
    <mm:import externid="_parentgroups" vartype="list" jspvar="parentgroups" /> 
    <mm:import externid="_childgroups" vartype="list" jspvar="childgroups" /> 
     <mm:listrelations type="mmbasegroups" role="contains" searchdir="source">
       <mm:relatednode jspvar="group">
        <% if (! parentgroups.contains("" + group.getNumber())) { %>
          <mm:import id="deleteparent" />
        <% } %>
       </mm:relatednode>
       <mm:present referid="deleteparent">
        <mm:deletenode />
       </mm:present>
     </mm:listrelations>
     <mm:unrelatednodes id="unrelated" type="mmbasegroups" />   
     <mm:write referid="unrelated" jspvar="unrelated" vartype="list">
     <mm:stringlist referid="_parentgroups">              
       <mm:node id="parentgroup" number="$_" jspvar="group">
         <% if (unrelated.contains(group)) { %>
              <mm:createrelation source="parentgroup" destination="group" role="contains" />
         <% } %>
        </mm:node>
     </mm:stringlist>
     </mm:write>
     <mm:listrelations type="mmbasegroups" role="contains" searchdir="destination">
       <mm:relatednode jspvar="group">
        <% if (! childgroups.contains("" + group.getNumber())) { %>
          <mm:import id="deletechild" />
        <% } %>
       </mm:relatednode>
       <mm:present referid="deletechild">
        <mm:deletenode />
       </mm:present>
     </mm:listrelations>
     <mm:write referid="unrelated" jspvar="unrelated" vartype="list">
     <mm:stringlist referid="_childgroups">              
       <mm:node id="childgroup" number="$_" jspvar="group">
         <% if (unrelated.contains(group)) { %>
              <mm:createrelation source="group" destination="childgroup" role="contains" />
         <% } %>
        </mm:node>
     </mm:stringlist>
     </mm:write>
     <mm:present referid="wasnew">
       <mm:import externid="createcontext" /> 
       <mm:present referid="createcontext">
         <mm:import externid="contextname" /> 
         <mm:createnode id="context" type="mmbasecontexts">
           <mm:setfield name="name"><mm:write referid="contextname" /></mm:setfield>
         </mm:createnode>
         <mm:node referid="context">
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
         <mm:node referid="context" id="context">
           <mm:functioncontainer>
             <mm:param name="grouporuser"><mm:field node="group" name="number" /></mm:param>
             <mm:param name="operation">read</mm:param>
             <mm:booleanfunction name="maygrant">
               <mm:voidfunction name="grant" />
             </mm:booleanfunction>
           </mm:functioncontainer>
         </mm:node>
       </mm:present>
       <!-- if a group with alias 'mayreadallgroup" exists, this group is automiticly including this -->
       <mm:node id="allgroup" number="mayreadallgroup" notfound="skip">
         <mm:createrelation source="allgroup" destination="group" role="contains" />
       </mm:node>
     </mm:present>
     <mm:notpresent referid="wasnew">
       <%@include file="commitGroupOrUserRights.jsp" %>
     </mm:notpresent>
   </mm:context>
<mm:import id="current">groups</mm:import>
<%@include file="navigate.div.jsp" %>
<%@include file="you.div.jsp" %>

    <h1><mm:field name="gui()" /> (<%=getPrompt(m, "commited")%>)</h1>
   <%@include file="group.div.jsp" %>

   </mm:node>
  </mm:cloud>
</mm:content>