<%@ page import = "nl.leocms.connectors.UISconnector.*" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
<head>
   <title>Profiles</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <style>
      p { margin: 0px; }
   </style>
</head>
<mm:import externid="action"></mm:import>
<body style="overflow:auto;">
   Overview of profiles
   <table class="formcontent" border="1">
   <mm:listnodes type="persoon">
      <mm:import id="name" jspvar="name"><mm:field name="lastname" />, <mm:field name="firstname" /> <mm:field name="suffix" /></mm:import>
      <% if(name.trim().equals(",")) { %>
         <mm:import id="name" reset="true">from cookie <mm:field name="creatiedatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="dd-MM-yyyy HH:mm" /></mm:field></mm:import>
      <% } %>
      <mm:related path="posrel,dossier">
         <mm:compare referid="action" value="clear_all"><mm:deletenode element="posrel" /></mm:compare>
         <mm:first>
            <mm:present referid="name"><tr><td colspan="2"><li><mm:write referid="name" /></td></tr></mm:present>
            <mm:remove referid="name" />
            <tr><td style="vertical-align:top;">Dossiers</td><td>
         </mm:first>
         <mm:field name="dossier.naam" /><br/>
         <mm:last>
            </td></tr>
         </mm:last>
      </mm:related>
      <% 
      String unam = "";
      String pwd = "";
      %>
      <mm:related path="posrel1,pools,posrel2,topics">
         <mm:compare referid="action" value="clear_all"><mm:deletenode element="posrel1" /></mm:compare>
         <mm:first>
            <mm:present referid="name"><tr><td colspan="2"><li><mm:write referid="name" /></td></tr></mm:present>
            <mm:remove referid="name" />
            <tr><td style="vertical-align:top;">Categorieen</td><td>
         </mm:first>
         <mm:field name="pools.name" /> <mm:field name="pools.externid" /> (<mm:field name="topics.title" />)
         <mm:field name="posrel1.pos">
            <mm:isgreaterthan value="1">
               - <mm:write />
            </mm:isgreaterthan>
         </mm:field>
         <br/>
         <mm:field name="topics.externid" id="topics_externid">
            <mm:compare referid="topics_externid" value="PWD">
                <mm:field name="pools.externid" jspvar="dummy" write="false" vartype="String"><% pwd = dummy; %></mm:field>
            </mm:compare>
            <mm:compare referid="topics_externid" value="UNAM">
                <mm:field name="pools.externid" jspvar="dummy" write="false" vartype="String"><% unam = dummy; %></mm:field>
            </mm:compare>
         </mm:field>
         <mm:last>
            </td></tr>
         </mm:last>
      </mm:related>
      <% if(!unam.equals("") && !pwd.equals("")) { %>
         <tr><td colspan="2"><a href="<%= UISconfig.getCustomersURL(unam,pwd) %>" target="_blank">show xml</a></td></tr>
      <% } %>
   </mm:listnodes>
   </table>
   <a href="view_profiles.jsp?action=clear_all">clear all profiles</a>
</body>
</html>
</mm:log>
</mm:cloud>
