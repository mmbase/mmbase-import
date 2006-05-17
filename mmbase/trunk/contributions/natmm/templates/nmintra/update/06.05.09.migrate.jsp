<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
	Things to be done in this update: <br/>
	1. Setting the levels of the rubrieken<br/>
	<mm:node number="root">
      <mm:setfield name="level">0</mm:setfield>
      <mm:relatednodes type="rubriek" searchdir="destination">
         <mm:setfield name="level">1</mm:setfield>
         <mm:relatednodes type="rubriek" searchdir="destination">
            <mm:setfield name="level">2</mm:setfield>
            <mm:relatednodes type="rubriek" searchdir="destination">
               <mm:setfield name="level">3</mm:setfield>
            </mm:relatednodes>
         </mm:relatednodes>
      </mm:relatednodes>
   </mm:node>
	2. Putting the emailaddress of medewerkers into the user emailadress<br/>
	<mm:list nodes="" path="medewerkers,related,users">
		<mm:field name="medewerkers.email" jspvar="medewerkers_email" vartype="String" write="false">
			<mm:node element="users">
				<mm:setfield name="emailadres"><%= medewerkers_email %></mm:setfield>
			</mm:node>
		</mm:field>
	</mm:list>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
