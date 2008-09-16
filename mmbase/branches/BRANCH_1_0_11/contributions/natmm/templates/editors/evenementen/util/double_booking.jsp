<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten Activiteiten Database</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
		<%
		long nowSec = (new java.util.Date()).getTime()/1000;
		String lastName = "";
		String lastTitle = "";
		String lastDateTime = "";
      String ticketOffice = "";
      String ticketOfficeSource = "";
		%>
      <mm:list path="inschrijvingen,posrel,evenement"
			constraints="<%= "evenement.begindatum > " + nowSec %>"
			orderby="inschrijvingen.number">
			<mm:field name="evenement.begindatum" jspvar="cdate" vartype="String" write="false">
			<mm:field name="evenement.titel" jspvar="etitle" vartype="String" write="false">
			<mm:node element="inschrijvingen">
            <mm:field name="ticket_office" jspvar="ticket_office" vartype="String" write="false">
               <%ticketOffice = ticket_office;%>
            </mm:field>
            <mm:field name="ticket_office_source" jspvar="ticket_office_source" vartype="String" write="false">
               <%ticketOfficeSource = ticket_office_source;%>
            </mm:field>
				<mm:field name="number" jspvar="ticket_number" vartype="String" write="false">
				<mm:related path="posrel,deelnemers" max="1" fields="deelnemers.titel">
					<mm:field name="deelnemers.titel" jspvar="name" vartype="String" write="false">
                  <mm:time time="<%= cdate %>" format="dd-MM-yyyy hh:mm" jspvar="dateTime" vartype="String">
               
						<%
						if(lastName.equals(name) && lastTitle.equals(etitle) && lastDateTime.equals(dateTime) ){ // this is probably a double booking 
							%><%= ticket_number %> 
							- <%= name %>
							- <%= etitle %>
							- <%= dateTime %> 
                     - aanmelding via: <%=ticketOffice %> (<%=ticketOfficeSource %>)<br/><%
                     
						}
						lastName= name;
						lastTitle = etitle; 
						lastDateTime = dateTime; %>
                  </mm:time>
					</mm:field>
				</mm:related>
				</mm:field>
			</mm:node>
			</mm:field>
			</mm:field>
      </mm:list>
   </body>
</html>
</mm:cloud>