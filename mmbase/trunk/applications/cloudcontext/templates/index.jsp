<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><mm:content postprocessor="reducespace"><html>
<%@include file="settings.jsp" %>
<head>
    <title>Cloud Context Users Administration</title>
    <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>

<mm:import externid="orderby">username</mm:import>
<mm:import externid="directions">UP</mm:import>

<mm:import id="fields">username,defaultcontext,status,owner</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:import externid="search" />
<mm:import id="nodetype">mmbaseusers</mm:import>
<body>
 <%@include file="you.div.jsp" %>
 <mm:import id="current">users</mm:import>
 <%@include file="navigate.div.jsp" %>

   <p class="action">
    <mm:maycreate type="mmbaseusers">
     <a href="<mm:url page="create_user.jsp" />"><img src="images/mmbase-new-40.gif" alt="+" title="create user"  /></a>
    </mm:maycreate>
    <mm:maycreate type="mmbaseusers" inverse="true">
      You are not allowed to create new users.
    </mm:maycreate>
   </p>

   <%@include file="search.form.jsp" %>

   <table summary="Users">

   <mm:listnodescontainer type="$nodetype">

    <mm:import externid="offset">0</mm:import>
    <mm:offset value="$offset" />
    <mm:maxnumber value="10" />
    <%@include file="search.jsp" %>

     <tr>
       <th> </th>
       <th>Rang</th>
       <mm:fieldlist nodetype="$nodetype"  fields="$fields">
         <th>
           <a title="order" href='<mm:url referids="search" ><mm:param name="orderby"><mm:fieldinfo type="name" /></mm:param>
               <mm:fieldinfo type="name">
                 <mm:compare referid2="orderby">
                   <mm:write referid="directions">
                     <mm:compare value="UP">
                       <mm:param name="directions">DOWN</mm:param>
                      </mm:compare>
                     <mm:compare value="DOWN">
                       <mm:param name="directions">UP</mm:param>
                      </mm:compare>
                   </mm:write>
                 </mm:compare>
               </mm:fieldinfo>                
               <mm:fieldlist  nodetype="mmbaseusers" fields="$fields"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
            </mm:url>' ><mm:fieldinfo type="guiname" /></a>
         </th>
       </mm:fieldlist>
       <th><%@include file="pager.jsp" %></th>
     </tr>

     

     <mm:listnodes id="user" orderby="$orderby" directions="$directions">  
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
      <td>
        <mm:relatednodes type="mmbaseranks" role="rank">
          <mm:nodeinfo type="gui" />
        </mm:relatednodes>
      </td>
  
      <mm:fieldlist fields="$fields">
         <td><mm:fieldinfo type="guivalue" /></td>
      </mm:fieldlist>

      <td class="commands">
         <mm:maywrite>
           <a href="<mm:url referids="user" page="edit_user.jsp" />"><img src="images/mmbase-edit.gif" alt="Wijzigen" title="Wijzigen" /></a>
         </mm:maywrite>
         <mm:field name="rank" >
           <mm:compare value="<%="" + org.mmbase.security.Rank.ADMIN.getInt()%>" inverse="true">
              <mm:maydelete>
              <a href="<mm:url referids="user" page="delete_user.jsp" />"><img src="images/mmbase-delete.gif" alt="Verwijderen" title="Verwijderen" /></a>
              </mm:maydelete>
           </mm:compare>
         </mm:field>
      </td>
      </tr>
     </mm:listnodes>
  </mm:listnodescontainer>
  </table>

 </body>
</mm:cloud>
</html>
</mm:content>