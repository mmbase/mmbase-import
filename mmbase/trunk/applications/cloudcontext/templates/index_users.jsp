<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp"
%><%@include file="settings.jsp"
%><mm:content language="$language" postprocessor="reducespace">
<mm:import id="url">index_users.jsp</mm:import>

<mm:import externid="orderby">username</mm:import>
<mm:import externid="directions">UP</mm:import>

<mm:import id="fields">username,defaultcontext,status,owner</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
  <mm:import externid="search" />
  <mm:import id="nodetype">mmbaseusers</mm:import>

  <%@include file="you.div.jsp" %>
  <mm:import id="current">users</mm:import>
  <%@include file="navigate.div.jsp" %>

   <p class="action">
     <mm:maycreate type="mmbaseusers">
       <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">create_user.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-new.gif" />" alt="+" title="create user"  /></a>
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
       <th><mm:present referid="extrauserlink">
          <mm:include  page="$extrauserlink" />
         </mm:present>
       </th>
       <th><%=m.getString("rank")%></th>
       <mm:fieldlist nodetype="$nodetype"  fields="$fields">
         <th>
           <a title="order" href='<mm:url referids="search,parameters,$parameters" ><mm:param name="orderby"><mm:fieldinfo type="name" /></mm:param>
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
       <th><a title="order" href='<mm:url referids="search,parameters,$parameters"><mm:param name="orderby">number</mm:param>
       <mm:compare referid="orderby" value="number">
         <mm:write referid="directions">
           <mm:compare value="UP">
             <mm:param name="directions">DOWN</mm:param>
           </mm:compare>
           <mm:compare value="DOWN">
             <mm:param name="directions">UP</mm:param>
           </mm:compare>
         </mm:write>
       </mm:compare>
     </mm:url>'>
     *</a>
          <%@include file="pager.jsp" %></th>
     </tr>


    <mm:sortorder field="$orderby" direction="$directions" />
     <mm:listnodes id="user">
      <tr <mm:even>class="even"</mm:even> >
       <td>
         <mm:present referid="extrauserlink">
          <mm:include referids="user" page="$extrauserlink" />
         </mm:present>
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
           <a href="<mm:url referids="user,parameters,$parameters"><mm:param name="url">edit_user.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-edit.gif" />" alt="Wijzigen" title="Wijzigen" /></a>
         </mm:maywrite>
         <mm:field name="rank" >
           <mm:compare value="<%="" + org.mmbase.security.Rank.ADMIN.getInt()%>" inverse="true">
              <mm:maydelete>
              <a href="<mm:url referids="user,parameters,$parameters"><mm:param name="url">delete_user.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-delete.gif" />" alt="Verwijderen" title="Verwijderen" /></a>
              </mm:maydelete>
           </mm:compare>
         </mm:field>
      </td>
      </tr>
     </mm:listnodes>
  </mm:listnodescontainer>
  </table>
  </mm:cloud>
</mm:content>
