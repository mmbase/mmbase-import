<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><html><mm:content postprocessor="reducespace">
<%@include file="settings.jsp" %>
<head>
    <title>Cloud Context Groups Administration</title>
    <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>
<mm:import externid="offset">0</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:import externid="group" vartype="list" />
<mm:import id="nodetype">mmbasegroups</mm:import>
<mm:import id="fields">name,description,owner</mm:import>
<body>
 <h1>Administrate groups</h1>

 <%@include file="you.div.jsp" %>

 <div id="navigate">
   <p><a href="<mm:url page="index.jsp" />">Users</a></p>
   <p class="current"><a href="<mm:url />">Groups</a></p>
   <p><a href="<mm:url page="index_contexts.jsp" />">Contexts</a></p>
   <p><a target="_new" href="<mm:url page="help.jsp" />">Help</a></p>
 </div>
 
  <p class="action">
    <a href="<mm:url page="create_group.jsp" />"><img src="images/mmbase-new-40.gif" alt="+" tooltip="create group"  /></a>
  </p>
 
   <mm:notpresent referid="group">
     <%@include file="search.form.jsp" %>
     
     <table summary="Groups">

     <mm:listnodescontainer id="cc" type="$nodetype">

     <%@include file="search.jsp" %>
    
     <mm:listnodes id="basegroups" orderby="name">
       <%-- 
       <mm:countrelations type="mmbasegroups" searchdir="source" role="contains">
          <mm:isgreaterthan value="0">
             <mm:removeitem />
          </mm:isgreaterthan>
      </mm:countrelations>
       --%>
     </mm:listnodes>

     <tr><mm:fieldlist nodetype="$nodetype"  fields="$fields">
        <th><mm:fieldinfo type="guiname" /></th>
       </mm:fieldlist>
       <th />
     </tr>
     
     <mm:listnodes id="currentgroup" referid="basegroups">
      <tr <mm:even>class="even"</mm:even> >
      <mm:fieldlist fields="$fields">
         <td><mm:fieldinfo type="guivalue" /></td>
      </mm:fieldlist>
      <td class="commands">
         <a href="<mm:url referids="currentgroup@group" />"><img src="images/mmbase-edit.gif" alt="Wijzigen" title="Wijzigen" /></a>
      </td>
      </tr>
     </mm:listnodes>

    </mm:listnodescontainer>
    </table>
   </mm:notpresent>
   <mm:present referid="group">
     <mm:stringlist referid="group">
      <mm:node id="currentgroup"  number="$_">
          <%@include file="group.div.jsp" %>
      </mm:node>
     </mm:stringlist>
   </mm:present>

 </body>
</mm:cloud>
</mm:content>
</html>
