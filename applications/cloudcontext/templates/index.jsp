<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><html>
<%@include file="settings.jsp" %>
<head>
    <title>Cloud Context Authorization Administration</title>
    <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>
<mm:import externid="offset">0</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:import externid="search" />
<body>
 <h1>Administrate users</h1>

 <div id="you">
   <p>you: <%=cloud.getUser().getIdentifier()%></p>
   <p>your rank: <%=cloud.getUser().getRank()%></p>
 </div>
 
 <div class="body">
   <p class="action">
     <a href="<mm:url page="create_user.jsp" />">Create user</a>
   </p>
   <form action="" method="post">
   <table summary="search user">
     <mm:fieldlist nodetype="mmbaseusers" type="search">
       <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="searchinput" /></td</tr>
      </mm:fieldlist>
      <tr><td colspan="2"><input type="submit" value="search" name="search" /></td></tr>
   </table>
   </form>


   <table summary="Users">

   <mm:listnodescontainer type="mmbaseusers">

    <mm:present referid="search">
       <mm:fieldlist nodetype="mmbaseusers" type="search">
         <mm:fieldinfo type="usesearchinput" />
       </mm:fieldlist>
    </mm:present>

     <tr>
       <th> </th>
       <mm:fieldlist nodetype="mmbaseusers"  type="list">
         <th><mm:fieldinfo type="guiname" /></th>
       </mm:fieldlist>
       <th>aantal: <mm:size /></th>
     </tr>

     <mm:listnodes id="user">  
      <tr <mm:even>class="even"</mm:even> >

       <td>
  <%--
         <mm:field name="username">
           <mm:listnodes type="people" constraints="[account]='$_'" max="1">
             <mm:field name="gui()" />
           </mm:listnodes>
         </mm:field>
   --%>
       </td>

      <mm:fieldlist type="list">
         <td><mm:fieldinfo type="guivalue" /></td>
      </mm:fieldlist>

      <td class="commands">
         <a href="<mm:url referids="user" page="edit_user.jsp" />"><img src="../gfx/edit.gif" alt="Wijzigen" title="Wijzigen" border="0" /></a>
         <mm:field name="rank" >
           <mm:compare value="<%="" + org.mmbase.security.Rank.ADMIN.getInt()%>" inverse="true">
              <a href="<mm:url referids="user" page="delete_user.jsp" />"><img src="../gfx/delete.gif" alt="Verwijderen" title="Verwijderen" border="0" /></a>
           </mm:compare>
         </mm:field>
      </td>
      </tr>
     </mm:listnodes>
  </mm:listnodescontainer>
  </table>

</div> 
 </body>
</mm:cloud>
</html>
