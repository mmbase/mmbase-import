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
    <mm:import externid="_parentgroups" vartype="list" jspvar="parentgroups" /> 
    <mm:import externid="_childgroups" vartype="list" jspvar="childgroups" /> 
    <tr>
     <td>Parent Groups</td>
     <td>
     <mm:listrelations type="mmbasegroups" role="contains" searchdir="source">
       <mm:relatednode jspvar="group">
         <%= group.getNumber() %>
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
     </td>     
     <td>Child Groups</td>
     <td>
     <mm:listrelations type="mmbasegroups" role="contains" searchdir="destination">
       <mm:relatednode jspvar="group">
         <%= group.getNumber() %>
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
     </td>
    </tr>
    <mm:import externid="createcontext" /> 
    <mm:present referid="createcontext">
      <mm:import externid="contextname" /> 
      <mm:createnode type="mmbasecontexts">
      </mm:createnode>
    </mm:present>
   <%@include file="commitGroupOrUserRights.jsp" %>
   </table>
   </mm:node>
<mm:write referid="group" jspvar="group" vartype="node">
 <% response.sendRedirect("index_groups.jsp?group=" + group.getNumber()); %>
</mm:write>
  </mm:cloud>
  </body>
</html>
