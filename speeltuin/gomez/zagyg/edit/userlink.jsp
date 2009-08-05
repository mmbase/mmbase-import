<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ include file="util/headernocache.jsp"
%><mm:import externid="user" />

<mm:notpresent referid="user">
  User
</mm:notpresent>

<mm:present referid="user">

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud">
  <mm:cloudinfo type="rank">
    <mm:isgreaterthan value="1999">
      <mm:node referid="user">
        <mm:field name="username">
          <mm:listnodes type="people" constraints="[account]='$_'" orderby="lastname" >
             <mm:first><mm:import id="found" /></mm:first>
             <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
               <mm:param name="wizard">tasks/people/people</mm:param>
               <mm:param name="objectnumber"><mm:field  name="number" /></mm:param>
               </mm:url>">
               <mm:field name="firstname" /> <mm:field name="middle" /> <mm:field name="lastname" />
             </a>
          </mm:listnodes>
          <mm:notpresent referid="found">
             <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
               <mm:param name="wizard">tasks/people/people</mm:param>
               <mm:param name="objectnumber">new</mm:param>
               <mm:param name="origin"><mm:field name="username" /></mm:param>
               </mm:url>">
                Nieuw
             </a>
         </mm:notpresent>
        </mm:field>
       </mm:node>
     </mm:isgreaterthan>
   </mm:cloudinfo>
</mm:cloud>

</mm:present>