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
<mm:import externid="context" vartype="list" />
<mm:import id="nodetype">mmbasecontexts</mm:import>
<mm:import id="fields">name,description,owner</mm:import>
<body>
 <h1>Administrate contexts</h1>

 <%@include file="you.div.jsp" %>

 <div id="navigate">
   <p><a href="<mm:url page="index.jsp" />">Users</a></p>
   <p><a href="<mm:url page="index_groups.jsp" />">Groups</a></p>
   <p class="current"><a href="<mm:url page="index_contexts.jsp" />">Contexts</a></p>
   <p><a target="_new" href="<mm:url page="help.jsp" />">Help</a></p>
 </div>
 
  <p class="action">
   <mm:maycreate type="mmbasecontexts">
    <a href="<mm:url page="create_context.jsp" />"><img src="images/mmbase-new-40.gif" alt="+" tooltip="create context"  /></a>
   </mm:maycreate>
   <mm:maycreate type="mmbasecontexts" inverse="true">
     You are not allowed to create new security contexts.
   </mm:maycreate>
  </p>
 
   <mm:notpresent referid="context">
     <%@include file="search.form.jsp" %>
     
     <table summary="Contexts">

     <mm:listnodescontainer id="cc" type="$nodetype">

     <%@include file="search.jsp" %>
    


     <tr><mm:fieldlist nodetype="$nodetype"  fields="$fields">
        <th><mm:fieldinfo type="guiname" /></th>
       </mm:fieldlist>
       <th />
     </tr>
     
     <mm:listnodes id="currentcontext">
      <tr <mm:even>class="even"</mm:even> >
      <mm:fieldlist fields="$fields">
         <td><mm:fieldinfo type="guivalue" /></td>
      </mm:fieldlist>
      <td class="commands">
         <a href="<mm:url referids="currentcontext@context" />"><img src="images/mmbase-edit.gif" alt="Wijzigen" title="Wijzigen" /></a>
      </td>
      </tr>
     </mm:listnodes>

    </mm:listnodescontainer>
    </table>
   </mm:notpresent>
   <mm:present referid="context">
     <mm:stringlist referid="context">
      <mm:node id="currentcontext" number="$_">
          <%@include file="context.div.jsp" %>
      </mm:node>
     </mm:stringlist>
   </mm:present>

 </body>
</mm:cloud>
</mm:content>
</html>
