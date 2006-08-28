<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.05.09"); %>
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
		<mm:field name="medewerkers.firstname" jspvar="medewerkers_firstname" vartype="String" write="false">
			<mm:node element="users">
				<mm:setfield name="voornaam"><%= medewerkers_firstname %></mm:setfield>
			</mm:node>
		</mm:field>
		<mm:field name="medewerkers.suffix" jspvar="medewerkers_suffix" vartype="String" write="false">
			<mm:node element="users">
				<mm:setfield name="tussenvoegsel"><%= medewerkers_suffix %></mm:setfield>
			</mm:node>
		</mm:field>
		<mm:field name="medewerkers.lastname" jspvar="medewerkers_lastname" vartype="String" write="false">
			<mm:node element="users">
				<mm:setfield name="achternaam"><%= medewerkers_lastname %></mm:setfield>
			</mm:node>
		</mm:field>
	</mm:list>
	3. Clean up the styles from not used styles<br/>
	<mm:listnodes type="style">
		<mm:field name="title" jspvar="title" vartype="String" write="false">
		<mm:remove referid="isused" />
		<mm:related path="related,rubriek">
			<mm:node element="rubriek">
				<mm:setfield name="style"><%= "css/" + title + ".css" %></mm:setfield> 
			</mm:node>
			<mm:remove referid="isused" />
			<mm:import id="isused" />
		</mm:related>
		<mm:notpresent referid="isused">
			<% try { %> <mm:deletenode deleterelations="true" /> <% } catch (Exception e) {} %>
		</mm:notpresent>
		</mm:field>
	</mm:listnodes>
   Done.
</mm:log>
</mm:cloud>
