<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp" %><html>
<mm:import externid="user" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node id="user" referid="user">
  <head>
    <title>Commit user <mm:field name="gui()" /></title>
   <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
  </head>
  <body>
 <h1><mm:field name="gui()" /></h1>

  <%@include file="you.div.jsp" %>


   <table>
    <mm:fieldlist type="edit">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="useinput" /></td></tr>
    </mm:fieldlist>
    <mm:import externid="_groups" vartype="list" jspvar="groups" /> 
    <tr>
     <td>Groups</td>
     <td>
     <mm:listrelations type="mmbasegroups" role="contains">
       <mm:relatednode jspvar="group">
         <%= group.getNumber() %>
        <% if (! groups.contains("" + group.getNumber())) { %>
          <mm:import id="delete" />
        <% } %>
       </mm:relatednode>
       <mm:present referid="delete">
        <mm:deletenode />
       </mm:present>
     </mm:listrelations>
     <mm:unrelatednodes id="unrelated" type="mmbasegroups" />   
     <mm:write referid="unrelated" jspvar="unrelated" vartype="list">
     <mm:stringlist referid="_groups">              
       <mm:node id="group" number="$_" jspvar="group">
         <% if (unrelated.contains(group)) { %>
              <mm:createrelation source="group" destination="user" role="contains" />
         <% } %>
        </mm:node>
     </mm:stringlist>
     </mm:write>
     </td>
    </tr>
    <tr>
     <td>Rank</td>
     <td>
     <mm:import externid="_rank" />
     <mm:isnotempty referid="_rank">      
       <mm:listrelations type="mmbaseranks" role="rank">
          <mm:deletenode />
       </mm:listrelations>
       <mm:node id="ranknode" number="$_rank" />
       <mm:createrelation source="user" destination="ranknode" role="rank" />
 
     </mm:isnotempty>

   </table>


   </mm:node>
  </mm:cloud>
  <% response.sendRedirect("index.jsp"); %>
  </body>
</html>
