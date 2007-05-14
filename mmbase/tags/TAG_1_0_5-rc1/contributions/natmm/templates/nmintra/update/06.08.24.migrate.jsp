<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
  <% log.info("06.08.24"); %>
	<% log.info("Moving the present articles from contentrel to readmore"); %>
	<mm:listnodes type="pagina" constraints="titel = 'Nieuws en vacatures'">
	   <mm:node id="news_page" />
	   <mm:relatednodes type="rubriek">
         <mm:node id="peno" />
      </mm:relatednodes>
      <mm:listnodes type="paginatemplate" constraints="url = 'vacature_info.jsp'">
         <mm:node id="template" />
      </mm:listnodes>
      <mm:createnode type="rubriek" id="vacatures">
          <mm:setfield name="naam">Vacatures</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="peno" destination="vacatures" role="parent">
        <mm:setfield name="pos">60</mm:setfield>
      </mm:createrelation>      
      <mm:createnode type="pagina" id="internal">
         <mm:setfield name="titel">Vacatures binnen NM</mm:setfield>
         <mm:setfield name="verwijderbaar">1</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="vacatures" destination="internal" role="posrel">
          <mm:setfield name="pos">10</mm:setfield>
      </mm:createrelation>
      <mm:createrelation source="internal" destination="template" role="gebruikt" />
      <mm:createnode type="pagina" id="external">
         <mm:setfield name="titel">Vacatures bij anderen</mm:setfield>
         <mm:setfield name="verwijderbaar">1</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="vacatures" destination="external" role="posrel">
          <mm:setfield name="pos">20</mm:setfield>
      </mm:createrelation>

      <mm:createrelation source="external" destination="template" role="gebruikt" />
      
	   <mm:related path="contentrel,vacature">
         <mm:node element="vacature" id="vacature">
            <mm:field name="metatags">
               <mm:compare value="interne vacature">
      	         <mm:createrelation source="internal" destination="vacature" role="contentrel" />
      	          <% log.info("+"); %>
      	      </mm:compare>
      	      <mm:compare value="externe vacature">
      	         <mm:createrelation source="external" destination="vacature" role="contentrel" />      	      
      	          <% log.info("+"); %>
      	      </mm:compare>
      	   </mm:field>
   	   </mm:node>
   	   <mm:deletenode element="contentrel" />
         <% log.info("-"); %>
	   </mm:related>
	   
	   <mm:related path="contentrel,artikel">
	      <mm:field name="contentrel.pos" jspvar="pos" vartype="String" write="false">
      	   <mm:node element="artikel" id="artikel">
         	   <mm:createrelation source="news_page" destination="artikel" role="readmore">
         	      <mm:setfield name="pos"><%= pos %></mm:setfield>
         	   </mm:createrelation>
      	   </mm:node>
   	   </mm:field>
   	   <mm:deletenode element="contentrel" />
	   </mm:related>
	   <mm:last>
   	   <mm:createnode type="artikel" id="news_artikel">
            <mm:setfield name="titel">demo nieuws artikel</mm:setfield>
            <mm:setfield name="embargo"><%= ((new java.util.Date()).getTime()/1000 -24*60*60) %></mm:setfield>
            <mm:setfield name="verloopdatum"><%= ((new java.util.Date()).getTime()/1000 +365*24*60*60) %></mm:setfield>
         </mm:createnode>
     	   <mm:createrelation source="news_page" destination="news_artikel" role="contentrel" />
      </mm:last>
      <mm:setfield name="titel">Nieuws</mm:setfield>
      
   </mm:listnodes>
   <% log.info("Add an extra question to the 'Wat vind je ervan?' form"); %>
   <mm:listnodes type="formulier" constraints="titel = 'Wat vind je ervan?'">
      <mm:node id="form" />
      <mm:setfield name="titel">Contact</mm:setfield>
      <mm:setfield name="omschrijving">Heb je nieuwsbericht, een opmerking of vraag? Dan kan je die hier kwijt!</mm:setfield>
      <mm:setfield name="titel_fra">Bedankt voor uw nieuwsbericht of commentaar.</mm:setfield>
      <mm:createnode type="formulierveld" id="field">
          <mm:setfield name="label">Ik heb</mm:setfield>
          <mm:setfield name="type">4</mm:setfield>
          <mm:setfield name="verplicht">1</mm:setfield>
       </mm:createnode>
       <mm:createrelation source="form" destination="field" role="posrel">
          <mm:setfield name="pos">3</mm:setfield>
       </mm:createrelation>
       <mm:createnode type="formulierveldantwoord" id="a1">
          <mm:setfield name="waarde">een nieuwsbericht voor intranieuws@natuurmonumenten.nl </mm:setfield>
       </mm:createnode>
       <mm:createrelation source="field" destination="a1" role="posrel">
          <mm:setfield name="pos">1</mm:setfield>
       </mm:createrelation>
       <mm:createnode type="formulierveldantwoord" id="a2">
          <mm:setfield name="waarde">een opmerking of vraag aan de beheerder(s)</mm:setfield>
       </mm:createnode>
       <mm:createrelation source="field" destination="a2" role="posrel">
          <mm:setfield name="pos">2</mm:setfield>
       </mm:createrelation>
       <mm:related path="posrel,formulierveld"  constraints="label = 'Je commentaar op de nieuwe intranet site'">
          <mm:node element="formulierveld">
            <mm:setfield name="label">Je nieuwsbericht of commentaar</mm:setfield>
          </mm:node>
          <mm:node element="posrel">
            <mm:setfield name="pos">4</mm:setfield>
          </mm:node>
       </mm:related>
       <mm:relatednodes type="pagina">
          <mm:node>
            <mm:createalias>feedback</mm:createalias>
          </mm:node>
       </mm:relatednodes>
   </mm:listnodes>
   <% log.info("Some changes to the existing navigation"); %>
   <mm:listnodes type="pagina" constraints="titel = 'Intervisie en coaching'">
     <mm:node id="c1" />
     <mm:listnodes type="pagina" constraints="titel = 'Loopbaan coaching'">
       <mm:related path="contentrel,artikel">
          <mm:node element="artikel" id="a" />
          <mm:deletenode element="contentrel" />
          <mm:createrelation source="c1" destination="a" role="contentrel">
             <mm:setfield name="pos">3</mm:setfield>
           </mm:createrelation>
       </mm:related>
       <mm:deletenode deleterelations="true" />
     </mm:listnodes>
   </mm:listnodes>
   <mm:listnodes type="pagina" constraints="titel = 'Wie-is-wie?'">
     <mm:setfield name="bron">1</mm:setfield>
   </mm:listnodes>
   <%
   String [] pageToRename = {
		"Nieuws en informatie",
    "Wie-is-wie?",
    "Arbeidsvoorwaarden en Beleid",
    "Studiekosten-regeling",
    "Subsidieregeling",
    "Intervisie en coaching"
		};
	String [] pageNewName = {
		"Nieuws",
    "Wie-is-wie",
    "Arbeidsvoorwaarden",
    "Studiekosten",
    "Subsidies",
    "Coaching"
		};
	for(int i=0; i<pageToRename.length;i++) {
		%><mm:listnodes type="pagina" constraints="<%= "titel = '" + pageToRename[i]  + "'" %>">
			<mm:setfield name="titel"><%= pageNewName[i] %></mm:setfield>
		 </mm:listnodes><%
	}
  String [] rubriekToRename = {
		"Wie-is-wie?",
    "Personeel & organisatie",
    "De Vraagbaak",
    "Huisstijl Handboek"
		};
	String [] rubriekNewName = {
		"Wie-is-wie",
    "P&O",
    "Vraagbaak",
    "Huisstijl"
		};
	for(int i=0; i<rubriekToRename.length;i++) {
		%><mm:listnodes type="rubriek" constraints="<%= "naam = '" + rubriekToRename[i]  + "'" %>">
			<mm:setfield name="naam"><%= rubriekNewName[i] %></mm:setfield>
		 </mm:listnodes><%
	}
	%>
  <!--
    Nieuws
    Arbeidsvoorwaarden
    Opleiding en Ontwikkeling
    Vacatures
    Formulieren
    Nieuwsbrieven
  -->
  <mm:listnodes type="rubriek" constraints="naam = 'P&O'">
			<mm:related path="parent,rubriek" searchdir="destination">
        <mm:field name="rubriek.naam" jspvar="rname" vartype="String" write="false">
        <mm:node element="parent">
          <% 
          int pos = -1;
          if(rname.equals("Opleiding en ontwikkeling")) { pos = 3; }
          if(rname.equals("Vacatures")) { pos = 4; }
          if(pos!=-1) {
            %>
            <mm:setfield name="pos"><%= "" + pos %></mm:setfield>
            <%
          } else { log.info(rname + " is not a known rubriek"); }
          %>
        </mm:node>
        </mm:field>
      </mm:related>
      <mm:related path="posrel,pagina" searchdir="destination">
        <mm:field name="pagina.titel" jspvar="ptitel" vartype="String" write="false">
        <mm:node element="posrel">
          <% 
          int pos = -1;
          if(ptitel.equals("Nieuws")) { pos = 1; } 
          if(ptitel.equals("Arbeidsvoorwaarden")) { pos = 2; } 
          if(ptitel.equals("Formulieren")) { pos = 5; }
          if(ptitel.equals("Nieuwsbrieven")) { pos = 6; }
          if(pos!=-1) {
            %>
            <mm:setfield name="pos"><%= "" + pos %></mm:setfield>
            <%
          } else { log.info(ptitel + " is not a known page"); }
          %>
        </mm:node>
        </mm:field>
      </mm:related>
  </mm:listnodes>
  <mm:node number="home">
      <mm:related path="parent,rubriek" searchdir="destination">
        <mm:field name="rubriek.naam" jspvar="rname" vartype="String" write="false">
        <mm:node element="parent" jspvar="parent">
          <% 
          int pos = -1;
          if(rname.equals("Home")) { pos = 1; }
          if(rname.equals("Wie-is-wie")) { pos = 2; } 
          if(rname.equals("CABS")) { pos = 3; }
          if(rname.equals("P&O")) { pos = 4; }
          if(rname.equals("Interne Webwinkel")) { pos = 5; }
          if(rname.equals("Helpdesk ICT")) { pos = 6; }
          if(rname.equals("Dienstenpakketten")) { pos = 7; }
          if(rname.equals("Huisstijl")) { pos = 8; }
          if(rname.equals("Vraagbaak")) { pos = 9; }
          if(rname.equals("Bibliotheek")) { pos = 10; }
          if(rname.equals("Projectmatig werken")) { pos = 11; }
          if(rname.equals("Kiezen & Delen")) { pos = 12; }
          if(rname.equals("Sjacherhoek")) { pos = 13; }
          if(rname.equals("Archief")) { pos = 14; }
          if(rname.equals("Wat vind je ervan?")) { pos = 15; }
          if(pos!=-1) {
            %>
            <mm:setfield name="pos"><%= "" + pos %></mm:setfield>
            <%
          } else { log.info(rname + " is not a known rubriek"); }
          %>
        </mm:node>
        </mm:field>
      </mm:related>
   </mm:node>
   <% log.info("Add default editwizard for pages"); %>
   <mm:createnode type="editwizards">
      <mm:setfield name="name">pagina</mm:setfield>
      <mm:setfield name="wizard">config/pagina/pagina_default</mm:setfield>
      <mm:setfield name="type">wizard</mm:setfield>
      <mm:setfield name="nodepath">pagina</mm:setfield>
      <mm:setfield name="fields">titel</mm:setfield>
      <mm:setfield name="searchfields">titel</mm:setfield>
      <mm:setfield name="orderby">titel</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
   </mm:createnode>
   <% log.info("Add default editwizard for projects"); %>
   <mm:createnode type="editwizards">
      <mm:setfield name="name">projecten</mm:setfield>
      <mm:setfield name="wizard">config/projects/projects</mm:setfield>
      <mm:setfield name="type">wizard</mm:setfield>
      <mm:setfield name="nodepath">projects</mm:setfield>
      <mm:setfield name="fields">titel</mm:setfield>
      <mm:setfield name="searchfields">titel</mm:setfield>
      <mm:setfield name="orderby">titel</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
   </mm:createnode>
   <% log.info("Change wizard for urls"); %>
   <mm:listnodes type="editwizards" constraints="name = 'links'">
      <mm:setfield name="name">pagina</mm:setfield>
      <mm:setfield name="wizard">config/link/link</mm:setfield>
      <mm:setfield name="type">wizard</mm:setfield>
      <mm:setfield name="nodepath">link</mm:setfield>
      <mm:setfield name="fields">titel,url</mm:setfield>
      <mm:setfield name="searchfields">titel</mm:setfield>
      <mm:setfield name="orderby">titel</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
   </mm:listnodes>
   <% log.info("Change wizard for teaser"); %>
   <mm:listnodes type="editwizards" constraints="wizard = 'config/teaser/teaser_quote'">
      <mm:setfield name="wizard">config/teaser/teaser_nmintra</mm:setfield>
   </mm:listnodes>
   <% log.info("Change wizard for vacatures"); %>
   <mm:listnodes type="editwizards" constraints="wizard = 'config/vacature/vacature'">
      <mm:setfield name="wizard">config/vacature/vacature_nmintra</mm:setfield>
   </mm:listnodes>
</mm:log>
</mm:cloud>
