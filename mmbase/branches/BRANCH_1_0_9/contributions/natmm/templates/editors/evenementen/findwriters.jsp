<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" method="http" rank="basic user">
<mm:import externid="e">-1</mm:import>
<mm:import externid="p">-1</mm:import>
<% 
Calendar cal = Calendar.getInstance(); 
Date now = new Date();
%>
<html>
<head>
   <title>Medewerkers</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
   <div align="right"><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>
   <mm:node number="$p">
      <h4 style="margin-top:0px;">Medewerkers die gewerkt hebben aan 
            <mm:field name="titel" />
            <mm:field name="begindatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field>
      </h4>
      <table class="formcontent" style="width:auto;">
      <mm:related path="schrijver,users" orderby="schrijver.number" directions="DOWN">
         <mm:first>
         <tr>
            <th colspan="6">Medewerkers die aan de beschrijving van deze activiteit hebben gewerkt</th>
         <tr>
         </mm:first>
         <tr>
            <td style="vertical-align:top;"><mm:field name="users.voornaam" /> <mm:field name="users.tussenvoegsel" /> <mm:field name="users.achternaam" /> (<mm:field name="users.account" />)</td>
            <td style="vertical-align:top;padding-left:10px;" colspan="6">
               <mm:node element="schrijver">
                  <mm:function name="age" jspvar="age" vartype="Integer" write="false">
                     <%
                     cal.setTime(now);
                     cal.add(Calendar.DATE,-age.intValue());
                     %><mm:time time="<%= "" + cal.getTime().getTime()/1000 %>" format="yyyy-MM-dd" />
                  </mm:function>
               </mm:node>
            </td>
         </tr>
      </mm:related>
   </mm:node>
   <mm:compare referid="p" referid2="e">
      <mm:node number="$e">
      <mm:related path="schrijver,users" orderby="schrijver.number" directions="DOWN">
         <mm:first>
         <tr>
            <th colspan="6" style="padding-top:10px;">Medewerkers die de datum voor deze activiteit hebben ingesteld</th>
         <tr>
         </mm:first>
         <tr>
            <td style="vertical-align:top;"><mm:field name="users.voornaam" /> <mm:field name="users.tussenvoegsel" /> <mm:field name="users.achternaam" /> (<mm:field name="users.account" />)</td>
            <td style="vertical-align:top;padding-left:10px;" colspan="6">
               <mm:node element="schrijver">
                  <mm:function name="age" jspvar="age" vartype="Integer" write="false">
                     <%
                     cal.setTime(now);
                     cal.add(Calendar.DATE,-age.intValue());
                     %><mm:time time="<%= "" + cal.getTime().getTime()/1000 %>" format="yyyy-MM-dd" />
                  </mm:function>
               </mm:node>
            </td>
         </tr>
      </mm:related>
      </mm:node>
   </mm:compare>
   <mm:node number="$e">
      <mm:related path="posrel,inschrijvingen,posrel,deelnemers,schrijver,users" orderby="schrijver.number" directions="DOWN">
         <mm:first>
         <tr>
            <th colspan="6" style="padding-top:10px;">Redactie die aanmeldingen op deze activiteit hebben geplaatst</th>
         <tr>
         </tr>
            <th>Medewerker</th>
            <th style="padding-left:10px;">Datum</th>
            <th style="padding-left:10px;">Aanmelding</th>
            <th>Aantal</th>
            <th>Categorie</th>
            <th>Datum laatste wijziging</th>
         </tr>
         </mm:first>
         <tr>
            <td style="vertical-align:top;"><mm:field name="users.voornaam" /> <mm:field name="users.tussenvoegsel" /> <mm:field name="users.achternaam" /> (<mm:field name="users.account" />)</td>
            <mm:node element="deelnemers">
               <td style="vertical-align:top;padding-left:10px;"><mm:field name="creatiedatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
               <td style="vertical-align:top;padding-left:10px;"><mm:field name="titel" /></td>
               <td style="vertical-align:top;"><mm:field name="bron" /></td>
               <td style="vertical-align:top;"><mm:related path="related,deelnemers_categorie"><mm:first inverse="true">, </mm:first><mm:field name="deelnemers_categorie.naam" /></mm:related></td>
               <td style="vertical-align:top;padding-right:10px;"><mm:field name="datumlaatstewijziging" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
            </mm:node>
         </tr>
      </mm:related>
   </mm:node>
   </table>

</body>
</html>
</mm:cloud>
