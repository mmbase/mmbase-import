<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Gebruikers</title>
</head>
<body style="overflow:auto;">
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<mm:import externid="userid">-1</mm:import>
<mm:compare referid="userid" value="-1">
   <h1>Alle gebruikers</h1>
   <table class="formcontent" style="width:auto;">
   <tr><th>Account</th><th>Naam</th></tr>
   <mm:listnodes type='users' orderby='account'>
      <mm:remove referid="isactive" />
      <mm:related path="schrijver,evenement">
         <mm:import id="isactive" />
      </mm:related>
      <mm:notpresent referid="isactive">
         <mm:related path="schrijver,deelnemers">
            <mm:import id="isactive" />
         </mm:related>
      </mm:notpresent>
      <mm:present referid="isactive">
      <tr>
         <td style="vertical-align:top;"><a href="userlist.jsp?userid=<mm:field name='number'/>"><mm:field name="account"/></a></td>
         <td style="vertical-align:top;"><nobr><mm:field name="voornaam"/> <mm:field name="tussenvoegsel"/> <mm:field name="achternaam"/></nobr></td>
      </tr>
      </mm:present>
   </mm:listnodes>
   </table>
</mm:compare>
<mm:compare referid="userid" value="-1" inverse="true">
   <mm:node number="$userid">
      <h5>Activiteiten en inschrijvingen ingevoerd en/of gewijzigd door
      <mm:field name="voornaam" /> <mm:field name="tussenvoegsel" /> <mm:field name="achternaam" /></h5>
      <a href="javascript:history.go(-1)">terug naar overzicht</a><br/><br/>
      <table class="formcontent" style="width:auto;">
      <mm:related path="schrijver,evenement" orderby="schrijver.number" directions="DOWN">
         <mm:first>
         <tr>
            <th>Activiteit</th>
            <th>Datum</th>
            <th>Soort</th>
            <th></th>
            <th></th>
            <th>Creatiedatum</th>
            <th>Datum laatste wijziging</th>
         </tr>
         </mm:first>
         <tr>
            <td style="vertical-align:top;"><mm:field name="evenement.titel" jspvar="titel" vartype="String" write="false"><%= (titel.length()>40 ? titel.substring(0,40) : titel ) %></mm:field></td>
            <td style="vertical-align:top;padding-right:10px;"><mm:field name="evenement.begindatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
            <td style="vertical-align:top;padding-right:10px;"><mm:field name="evenement.soort" jspvar="soort" vartype="String" write="false"><%= (soort.equals("parent") ? "datum en omschrijving" : "alleen datum" ) %></mm:field></td>
            <td></td>
            <td></td>
            <td style="vertical-align:top;padding-right:10px;"><mm:field name="evenement.creatiedatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
            <td style="vertical-align:top;padding-right:10px;"><mm:field name="evenement.datumlaatstewijziging" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
         </tr>
      </mm:related>
      <mm:related path="schrijver,deelnemers" orderby="schrijver.number" directions="DOWN">
         <mm:first>
         <tr>
            <th>Deelnemers</th>
            <th>Aantal</th>
            <th>Categorie</th>
            <th>Activiteit</th>
            <th>Datum</th>
            <th>Creatiedatum</th>
            <th>Datum laatste wijziging</th>
         </tr>
         </mm:first>
         <mm:node element="deelnemers">
         <tr>
            <td style="vertical-align:top;"><mm:field name="titel" /></td>
            <td style="vertical-align:top;"><mm:field name="bron" /></td>
            <td style="vertical-align:top;"><mm:related path="related,deelnemers_categorie"><mm:first inverse="true">, </mm:first><mm:field name="deelnemers_categorie.naam" /></mm:related></td>
            <mm:related path="posrel,inschrijvingen,posrel,evenement">
            <td style="vertical-align:top;"><mm:field name="evenement.titel" /></td>
            <td style="vertical-align:top;"><mm:field name="evenement.begindatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
            </mm:related>
            <td style="vertical-align:top;padding-right:10px;"><mm:field name="creatiedatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
            <td style="vertical-align:top;padding-right:10px;"><mm:field name="datumlaatstewijziging" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="yyyy-MM-dd hh:mm" /></mm:field></td>
         </tr>
         </mm:node>
      </mm:related>
   </mm:node>
   </table>
   <br/><br/>
   <a href="javascript:history.go(-1)">terug naar overzicht</a>
   <br/><br/>
</mm:compare>
</mm:cloud>
</body>
</html>