<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:content language="$language">
<mm:import externid="group" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node id="group" referid="group">

   <mm:context>


    <mm:fieldlist type="edit">
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
    <mm:import externid="createcontext" /> 
    <mm:present referid="createcontext">
      <mm:import externid="contextname" /> 
      <mm:createnode type="mmbasecontexts">
        <mm:setfield name="name"><mm:write referid="contextname" /></mm:setfield>
      </mm:createnode>
    </mm:present>
   <%@include file="commitGroupOrUserRights.jsp" %>
   </mm:context>
<mm:import id="current">groups</mm:import>
<%@include file="navigate.div.jsp" %>
<%@include file="you.div.jsp" %>

    <h1><mm:field name="gui()" /> (commited)</h1>
   <%@include file="group.div.jsp" %>

   </mm:node>
  </mm:cloud>
</mm:content>