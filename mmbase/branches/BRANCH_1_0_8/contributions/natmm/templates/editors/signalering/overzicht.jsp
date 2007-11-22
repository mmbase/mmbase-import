<%@page import="nl.leocms.signalering.SignaleringUtil,java.util.Date,java.text.SimpleDateFormat" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Taken</title>
   <style>
<!--
.erf         { background-color: #C0C0C0; line-height: 100%; font-size: 8pt }
.normal      { background-color: #CFA0A0; font-size: 8pt }
body         { font-family: Verdana; font-size: 8pt }
td           { font-family: Verdana; font-size: 8pt; color: #000000 }
td.titel     { background-color: #C0C0C0 }
th           { background-color: #C0C0C0; text-align: left; font-family: Verdana; font-size: 8pt; }
a.th:link    { color: black }
a.th:visited { color: black }
p.paginatitel { font-family: Verdana; font-size: 10pt; font-weight: bold }
-->
</style>
<script>
    var cancelClick = false;
    function doDelete(prompt) {
     var conf;
     if (prompt && prompt!="") {
         conf = confirm(prompt);
     } else conf=true;
     cancelClick=true;
     return conf;
    }
</script>
</head>

<body>

<mm:import externid="directions"/>
<mm:import externid="orderby"/>
<mm:import externid="signaleringnumber"/>
<mm:import externid="contentelement"/>
<mm:import externid="_hiddenDateVan"/>
<mm:import externid="_hiddenDateTot"/>
<mm:import externid="usernumber"/>
<mm:import externid="search"/>

<%
   String constraints = "[signalering.type] = 4 ";
   String nodes = "";
%>

<mm:compare referid="contentelement" value="">
   <mm:remove referid="contentelement"/>
   <mm:import id="contentelement">-1</mm:import>
</mm:compare>

<mm:compare referid="contentelement" value="-1" inverse="true">
   <mm:write referid="contentelement" jspvar="contentElement" vartype="String" write="false">
      <%
         constraints += "AND [signalering.builder] = '" + contentElement + "'";
      %>
   </mm:write>
</mm:compare>

<mm:present referid="search">
   <mm:compare referid="search" value="" inverse="true">
      <h2>Zoek resultaat</h2>
   </mm:compare>
   <mm:compare referid="search" value="">
      <h2>Overzicht taken</h2>
   </mm:compare>
   <mm:compare referid="usernumber" value="-1" inverse="true">
      <mm:write referid="usernumber" jspvar="userNumber" vartype="String" write="false">
         <%
            nodes = userNumber;
         %>
      </mm:write>
   </mm:compare>
   <mm:compare referid="_hiddenDateVan" value="" inverse="true">
      <mm:write referid="_hiddenDateVan" jspvar="fromDate" vartype="String" write="false">
         <%
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            Date tempDate = dateFormatter.parse(fromDate);
            long from = tempDate.getTime() / 1000;
            
            constraints += " AND [signalering.verloopdatum] > " + from;
         %>
      </mm:write>
   </mm:compare>
   <mm:compare referid="_hiddenDateTot" value="" inverse="true">
      <mm:write referid="_hiddenDateTot" jspvar="toDate" vartype="String" write="false">
         <%
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            Date tempDate = dateFormatter.parse(toDate);
            long to = tempDate.getTime() / 1000;
            
            constraints += " AND [signalering.verloopdatum] < " + to;
         %>
      </mm:write>
   </mm:compare>
</mm:present>

<mm:notpresent referid="search">
   <h2>Overzicht taken</h2>
</mm:notpresent>

<mm:present referid="signaleringnumber">
   <mm:write referid="signaleringnumber" jspvar="signaleringNumber" vartype="String" write="false">
      <%
         SignaleringUtil.removeNode(signaleringNumber);
      %>
   </mm:write>
</mm:present>

<mm:notpresent referid="directions">
   <mm:remove referid="directions"/>
   <mm:import id="directions">DOWN</mm:import>
</mm:notpresent>
<mm:notpresent referid="orderby">
   <mm:remove referid="orderby"/>
   <mm:import id="orderby">signalering.creatiedatum</mm:import>
</mm:notpresent>

<mm:compare referid="directions" value="UP" inverse="true">
   <mm:import id="newdirections">UP</mm:import>
</mm:compare>
<mm:compare referid="directions" value="DOWN" inverse="true">
   <mm:import id="newdirections">DOWN</mm:import>
</mm:compare>

<table border="1" cellpadding="0" cellspacing="0" width="100%">
   <tr>
      <td>
         <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
               <th class="td.titel"></th>
               <th class="td.titel"><a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement"><mm:param name="orderby">signalering.creatiedatum</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Creatiedatum</a></th>
               <th class="td.titel"><a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement"><mm:param name="orderby">signalering.verloopdatum</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Verloopdatum</a></th>
               <th class="td.titel"><a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement"><mm:param name="orderby">signalering.builder</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Content Type</a></th>
               <th class="td.titel" nowrap><a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement"><mm:param name="orderby">pagina.titel</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Pagina/Rubriek</a></th>
               <th class="td.titel"><a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement"><mm:param name="orderby">users.achternaam</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Gebruiker</a></th>
               <th class="td.titel"><a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement"><mm:param name="orderby">signalering.herhalingdatum</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Emailmelding</a></th>               
            </tr>
            <mm:list nodes="<%= nodes %>" path="users,aan,signalering,betreft,pagina" constraints="<%= constraints %>" orderby="$orderby" directions="$directions">
               <tr>
                  <td>
                     <a href="<mm:url referids="_hiddenDateVan,_hiddenDateTot,usernumber,search,contentelement,directions,orderby"><mm:param name="signaleringnumber"><mm:field name="signalering.number"/></mm:param></mm:url>"
                        onclick="return doDelete('Weet u zeker dat u deze signalering wilt verwijderen?');" 
                        onmousedown="cancelClick=true;"><img src="../img/remove.gif" border="0" title="Verwijder signalering"/></a>
                  </td>
                  <td><mm:field name="signalering.creatiedatum"><mm:time format="dd-MM-yy"/></mm:field></td>
                  <td>
                     <mm:remove referid="verloop"/>
                     <mm:import id="verloop"><mm:field name="signalering.verloopdatum"/></mm:import>
                     <mm:compare referid="verloop" value="-1" inverse="true">
                        <mm:write referid="verloop"><mm:time format="dd-MM-yy"/></mm:write>
                     </mm:compare>
                     <mm:compare referid="verloop" value="-1">
                        nvt
                     </mm:compare>
                  </td>
                  <td>
                     <mm:remove referid="buildername"/>
                     <mm:import id="buildername"><mm:field name="signalering.builder"/></mm:import>
                     <mm:compare referid="buildername" value="" inverse="true">
                        <mm:nodeinfo nodetype="$buildername" type="guitype"/>
                     </mm:compare>
                  </td>
                  <td>
                     <mm:node element="pagina">
                        <mm:field name="titel"/>/
                        <mm:related path="posrel,rubriek" max="1">
                           <mm:field name="rubriek.naam"/>
                        </mm:related>
                     </mm:node>
                  </td>
                  <td><mm:field name="users.voornaam"/> <mm:field name="users.tussenvoegsel"/> <mm:field name="users.achternaam"/></td>
                  <td>
                     <mm:remove referid="herhaling"/>
                     <mm:import id="herhaling"><mm:field name="signalering.herhalingdatum"/></mm:import>           
                     <mm:compare referid="herhaling" value="-1" inverse="true">
                        Ja
                     </mm:compare>
                     <mm:compare referid="herhaling" value="-1">
                        Nee
                     </mm:compare>
                  </td>
               </tr>
            </mm:list>
        </table>
      </td>
   </tr>
</table>

</mm:cloud>
</body>
</html>
