<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*,nl.leocms.util.DoubleDateNode" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" method="http" rank="basic user">
<mm:import externid="zipcode" jspvar="zipCode" vartype="String"> </mm:import>
<mm:import externid="housenumber" jspvar="houseNumber" vartype="String"> </mm:import>
<mm:import externid="houseext" jspvar="houseExt" vartype="String"> </mm:import>
<mm:import externid="lastname" jspvar="lastName" vartype="String"> </mm:import>
<%! 

public Vector findKeys(TreeMap thisMap, String thisValue,  Vector fromKeys) { 
   
   Vector keys = new Vector();
   if(thisValue.equals("")) {
      keys = fromKeys;
   } else {
      if(thisMap.containsValue(thisValue)) { // e.g. zipcode is a value in zipCodeTable
         for(int i=0; i<fromKeys.size(); i++) {
            String nextValue = (String) thisMap.get(fromKeys.get(i));
            if(nextValue!=null&&nextValue.equals(thisValue)) { // e.g. found a key for zipcode
                keys.add(fromKeys.get(i));
            }
         }
      }
   }
   return keys;
}

%>
<html>
<head>
   <title>Lidnummers</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
   <div align="right"><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>
   <h4>Lidnummers op basis van achternaam, postcode en huisnummer</h4>
   <% zipCode = zipCode.trim().toUpperCase();
      houseNumber = houseNumber.trim();
      houseExt = houseExt.trim().toUpperCase();
      lastName = lastName.trim().toUpperCase();
      
      TreeMap zipCodeTable = (TreeMap) application.getAttribute("zipCodeTable");     
      TreeMap invZipCodeTable = (TreeMap) application.getAttribute("invZipCodeTable");
      TreeMap houseNumberTable = (TreeMap) application.getAttribute("houseNumberTable");
      TreeMap houseExtTable = (TreeMap) application.getAttribute("houseExtTable");
      TreeMap invLastNameTable =  (TreeMap) application.getAttribute("invLastNameTable");
      TreeMap lastNameTable =  (TreeMap) application.getAttribute("lastNameTable");

      if(zipCodeTable==null||houseNumberTable==null||houseExtTable==null||invLastNameTable==null) {      

            %><br/><br/>Het zoeken naar lidnummers is op dit moment niet mogelijk.<br/>Neem alstublieft contact op met de webredactie.<br/><%

      } else if(!lastName.equals("")||!zipCode.equals("")) { // only search when at least lastname or zipcode is available

         Vector memberIdVector = null;
			if(!lastName.equals("")) {
				memberIdVector = (Vector) invLastNameTable.get(lastName);
			}
         if(memberIdVector==null) { // lastname did not return anything, try zipcode
				if(!zipCode.equals("")) {
					memberIdVector = (Vector) invZipCodeTable.get(zipCode);
				}
				if(memberIdVector==null) { // lastname or zipcode did not return anything, return empty set
					memberIdVector = new Vector(); 
				}
			}
			if(memberIdVector.size()>1 && !houseNumber.equals("")) { // use housenumber to narrow search down
	         memberIdVector = findKeys(houseNumberTable,houseNumber,memberIdVector);
			}
     		if(memberIdVector.size()>1 && !houseExt.equals("")) { // use housenumber extension to narrow search down
	         memberIdVector = findKeys(houseExtTable,houseExt.toUpperCase(),memberIdVector);
			}
         %>
         <table class="formcontent" style="width:auto;">
            <tr><td colspan="2"><h5 style="margin-bottom:0px;">U hebt gezocht op:</h5></td></tr>
            <tr><td>Achternaam</td><td><%= lastName %></td></tr>
            <tr><td>Postcode</td><td><%= zipCode %></td></tr>
            <tr><td>Huisnummer</td><td><%= houseNumber %></td></tr>
            <tr><td>Achtervoegsel</td><td><%= houseExt %></td></tr>
            <% if(memberIdVector.isEmpty()) { %>
               <tr><td colspan="2"><h5 style="margin-bottom:0px;margin-top:10px;">Er is geen lidnummer gevonden die voldoet aan deze gegevens.</h5></td></tr>
            <% } else { %>
               <tr><td colspan="2">
                     <h5 style="margin-bottom:10px;margin-top:10px;">
                        <% if(memberIdVector.size()==1) { %>
                            Het volgende lidnummer is gevonden.
                        <% } else { %>
                            De volgende lidnummers zijn gevonden.
                        <% } %>
                     </h5></td></tr>
               <tr><td colspan="2">
                  <table class="formcontent" style="width:auto;" cellpadding="2">
                     <tr><td>Achternaam</td><td>Huisnummer</td><td>Achtervoegsel</td><td>Postcode</td><td>Lidnummer</td></tr>
                  <% for(int i = 0; i< memberIdVector.size(); i++) { 
                     String sMemberId = (String) memberIdVector.get(i);
                     String thisLastName = (String) lastNameTable.get(sMemberId); if(thisLastName==null) { thisLastName = ""; } 
                     String thisHouseNumber = (String) houseNumberTable.get(sMemberId); if(thisHouseNumber==null) { thisHouseNumber = ""; } 
                     String thisHouseExt = (String) houseExtTable.get(sMemberId); if(thisHouseExt==null) { thisHouseExt = ""; } 
                     String thisZipCode = (String) zipCodeTable.get(sMemberId); if(thisZipCode==null) { thisZipCode = ""; } 
                     %>
                     <tr>
                        <td><%= thisLastName %></td>
                        <td><%= thisHouseNumber %></td>
                        <td><%= thisHouseExt %></td>
                        <td><%= thisZipCode %></td>
                        <td><%= sMemberId %></td>
                     </tr>
                  <% } %>
                  </table>
               </td></tr>
            <% } %>
         </table>
         <% 

      } else { 
	  		%>
         <table class="formcontent" style="width:auto;">
            <tr><td colspan="2">
               <h5 style="margin-bottom:0px;margin-top:10px;">Er is geen achternaam, postcode en/of huisnummer ingevuld.</h5>
               Er kan daarom niet op lidnummer worden gezocht.
               Sluit dit venster om alsnog een achternaam, postcode en/of huisnummer in te vullen.
            </td></tr>
         </table>
			<%
		} %>
</body>
</html>
</mm:cloud>
