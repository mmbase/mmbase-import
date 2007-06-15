<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.06.05"); %>
	Things to be done in this update:<br/>
	<mm:node number="home" id="natuurmonumente_subsite" />
	<% log.info("0. Adding users ew to admin"); %>
	<mm:listnodes type="users" constraints="account = 'admin'">
		<mm:node id="admin" />
		<mm:listnodes type="editwizards" constraints="wizard = 'config/users/users'">
			<mm:node id="user_ew">
				<mm:setfield name="name">gebruikers</mm:setfield>
				<mm:setfield name="description">Beheer van gebruikers</mm:setfield>
				<mm:setfield name="type">jsp</mm:setfield>
				<mm:setfield name="wizard">/editors/usermanagement/userlist.jsp</mm:setfield>
				<mm:createrelation source="admin" destination="user_ew" role="gebruikt" />
			</mm:node>
		</mm:listnodes>
	</mm:listnodes>
	<% log.info("1. Changing the wizard type of the page wizards:"); %>
	<mm:listnodes type="editwizards" constraints="wizard LIKE '%pagina_%'">
		<mm:setfield name="type">wizard</mm:setfield>
	</mm:listnodes>
	<% log.info("2. Homepage"); %>
	<mm:createnode type="editwizards" id="news_wizard">
		<mm:setfield name="name">nieuws (list)</mm:setfield>
		<mm:setfield name="description">Bewerk de nieuws artikelen van deze pagina</mm:setfield>
		<mm:setfield name="type">list</mm:setfield>
		<mm:setfield name="wizard">config/artikel/artikel_nieuws_nmintra</mm:setfield>
		<mm:setfield name="nodepath">pagina,contentrel,artikel</mm:setfield>
		<mm:setfield name="fields">artikel.titel,artikel.begindatum,artikel.embargo,artikel.verloopdatum,artikel.use_verloopdatum,artikel.archive</mm:setfield>
		<mm:setfield name="orderby">artikel.begindatum</mm:setfield>
		<mm:setfield name="directions">down</mm:setfield>
		<mm:setfield name="pagelength">50</mm:setfield>
		<mm:setfield name="maxpagecount">100</mm:setfield>
		<mm:setfield name="searchfields">artikel.titel,artikel.metatags</mm:setfield>
		<mm:setfield name="search">yes</mm:setfield>
	</mm:createnode>
	<mm:node number="homepage_template" id="homepage_template" />
	<mm:createrelation source="homepage_template" destination="news_wizard" role="related" />
	<mm:listnodes type="paginatemplate" constraints="url LIKE '%info%'">
		<mm:remove referid="info_template" />
		<mm:node id="info_template" />
		<mm:createrelation source="info_template" destination="news_wizard" role="related" />
	</mm:listnodes>
	<% log.info("3. Archief"); %>
	<mm:list nodes="" path="pagina,rolerel,teaser" constraints="pagina.titel!='Nieuws en informatie'">
		<% String pageIntro = ""; %>
		<mm:field name="teaser.titel" jspvar="teaser_titel" vartype="String" write="false">
			<% if(teaser_titel!=null && !teaser_titel.equals("")) { pageIntro += "<b>" + teaser_titel + "</b><br/>"; } %>
		</mm:field>
		<mm:field name="teaser.omschrijving" jspvar="teaser_intro" vartype="String" write="false">
			<% if(teaser_intro!=null && !teaser_intro.equals("")) { pageIntro += teaser_intro; } %>
		</mm:field>
		<mm:node element="pagina">
			<mm:setfield name="omschrijving"><%= pageIntro %></mm:setfield>
		</mm:node>
		<mm:deletenode element="teaser" deleterelations="true" /> 
	</mm:list>
	<mm:listnodes type="artikel">
		<mm:setfield name="begindatum"><mm:field name="embargo" /></mm:setfield>
		<mm:setfield name="use_verloopdatum">1</mm:setfield>
		<mm:setfield name="archive">unlimited</mm:setfield>
	</mm:listnodes>
	<% log.info("4. Prikbord"); %>
	<mm:createnode type="editwizards" id="ads_wizard">
		<mm:setfield name="name">berichten</mm:setfield>
		<mm:setfield name="description">Berichten uit het gastenboek</mm:setfield>
		<mm:setfield name="type">list</mm:setfield>
		<mm:setfield name="wizard">config/ads/ads</mm:setfield>
		<mm:setfield name="nodepath">pagina,contentrel,ads</mm:setfield>
		<mm:setfield name="fields">ads.title,ads.name,ads.email,ads.postdate,ads.expiredate</mm:setfield>
		<mm:setfield name="orderby">ads.expiredate</mm:setfield>
		<mm:setfield name="directions">down</mm:setfield>
		<mm:setfield name="pagelength">50</mm:setfield>
		<mm:setfield name="maxpagecount">100</mm:setfield>
		<mm:setfield name="searchfields">ads.title,ads.name,ads.email</mm:setfield>
		<mm:setfield name="search">yes</mm:setfield>
	</mm:createnode>
	<mm:node number="prikbord_template" id="prikbord_template" />
	<mm:createrelation source="prikbord_template" destination="ads_wizard" role="related" />
	<mm:listnodes type="pagina" constraints="titel = 'Sjacherhoek'">
		<mm:node id="bb" />
		<mm:listnodes type="ads">
			<mm:field name="expiredate" jspvar="expiredate" vartype="Long" write="false">
				<mm:setfield name="postdate"><%= expiredate.longValue() - 14*24*60*60 %></mm:setfield>
			</mm:field>
			<mm:remove referid="this_ad" />
			<mm:node id="this_ad" />
			<mm:createrelation source="bb" destination="this_ad" role="contentrel" />
		</mm:listnodes>
	</mm:listnodes>
	<% log.info("5. Changing editwizards because of change in builder"); %>
	Todo: check with last version of db.
	<mm:listnodes type="editwizards" constraints="wizard = 'config/educations/wizard'">
	 <mm:node>
       <mm:field name="name" id="ew_name">
         <mm:compare referid="ew_name" value="opleidingen">
           <mm:setfield name="fields">titel</mm:setfield>
           <mm:setfield name="orderby">titel</mm:setfield>
         </mm:compare>
         <mm:compare referid="ew_name" value="pleiding per aanbieder">
           <mm:setfield name="fields">educations.titel,providers.naam</mm:setfield>
           <mm:setfield name="orderby">providers.naam,educations.titel</mm:setfield>
         </mm:compare>
       </mm:field>
     </mm:node>
	</mm:listnodes>
	<% log.info("6. Renaming editwizards that should use the default page editor"); %>
	<mm:createnode type="editwizards" id="def_ew">
		<mm:setfield name="name">standaard pagina</mm:setfield>
		<mm:setfield name="description">Bewerk de basis gegevens van deze pagina</mm:setfield>
		<mm:setfield name="type">wizard</mm:setfield>
		<mm:setfield name="wizard">config/pagina/pagina_default</mm:setfield>
		<mm:setfield name="nodepath">pagina</mm:setfield>
		<mm:setfield name="fields">pagina.titel</mm:setfield>
		<mm:setfield name="orderby">pagina.titel</mm:setfield>
		<mm:setfield name="directions">up</mm:setfield>
		<mm:setfield name="pagelength">50</mm:setfield>
		<mm:setfield name="maxpagecount">100</mm:setfield>
		<mm:setfield name="searchfields">pagina.titel</mm:setfield>
		<mm:setfield name="search">yes</mm:setfield>
	</mm:createnode>
	<%
	String [] templateToChange = {
		"documents.jsp",
		"docpage.jsp",
    "event_blueprints.jsp"
		};
	for(int i=0; i<templateToChange.length;i++) {
		%>
		<mm:listnodes type="paginatemplate" constraints="<%= "url = '" + templateToChange[i]  + "'" %>">
			<mm:related path="related,editwizards" constraints="wizard LIKE '%pagina_%'">
				<mm:deletenode element="related" />
			</mm:related>
			<mm:remove referid="paginatemplate" />
			<mm:node id="paginatemplate" />
			<mm:createrelation source="paginatemplate" destination="def_ew" role="related" />
		 </mm:listnodes>
		<%
		}
	%>
	<% log.info("7. rename artikel ew which collapses with the natmm artikel template"); %>
	<mm:listnodes type="editwizards" constraints="wizard = 'config/pagina/pagina_artikel'">
		<mm:setfield name="wizard">config/pagina/pagina_artikel_nmintra</mm:setfield>
	</mm:listnodes>
	<% log.info("8. move imap ew to overview"); %>
	<mm:listnodes type="editwizards" constraints="wizard = 'config/pagina/pagina_imap'">
		<mm:setfield name="name">pagina met hotspots</mm:setfield>
		<mm:setfield name="description">Bewerk de hotspots op deze pagina</mm:setfield>
		<mm:setfield name="type">jsp</mm:setfield>
		<mm:setfield name="wizard">/editors/imap/imap_overview.jsp</mm:setfield>
		<mm:setfield name="nodepath"></mm:setfield>
		<mm:setfield name="fields"></mm:setfield>
		<mm:setfield name="orderby"></mm:setfield>
		<mm:setfield name="directions"></mm:setfield>
		<mm:setfield name="pagelength"></mm:setfield>
		<mm:setfield name="maxpagecount"></mm:setfield>
		<mm:setfield name="searchfields"></mm:setfield>
		<mm:setfield name="search"></mm:setfield>
	</mm:listnodes>
   <% log.info("9. Change name of page editwizards"); %>
	<mm:listnodes type="editwizards" constraints="name LIKE 'subr. vh genre %'">
	   <mm:field name="name" jspvar="name" vartype="String" write="false">
	      <% name  = name.substring(15) + " pagina"; %>
		   <mm:setfield name="name"><%= name %></mm:setfield>
		   <mm:setfield name="description"><%= "Bewerk deze " + name %></mm:setfield>
		</mm:field>
	</mm:listnodes>
   <% log.info("10. Add project overview ew to project archive template"); %>
	<mm:listnodes type="editwizards" constraints="wizard = '/editors/project_overview.jsp'">
	   <mm:node id="project_overview">
	      <mm:setfield name="name">voorbeeld project</mm:setfield>
   		<mm:setfield name="description">Bewerk de voorbeeld projecten</mm:setfield>
   		<mm:setfield name="type">jsp</mm:setfield>
   		<mm:setfield name="wizard">/editors/projects/project_overview.jsp</mm:setfield>
   		<mm:setfield name="nodepath"></mm:setfield>
   		<mm:setfield name="fields"></mm:setfield>
   		<mm:setfield name="orderby"></mm:setfield>
   		<mm:setfield name="directions"></mm:setfield>
   		<mm:setfield name="pagelength"></mm:setfield>
   		<mm:setfield name="maxpagecount"></mm:setfield>
   		<mm:setfield name="searchfields"></mm:setfield>
   		<mm:setfield name="search"></mm:setfield>
   	   <mm:listnodes type="paginatemplate" constraints="url = 'archive.jsp'">
      	   <mm:node id="project_archive" />
      	   <mm:createrelation source="project_archive" destination="project_overview" role="related" />
      	</mm:listnodes>
      </mm:node>
	</mm:listnodes>
	<% log.info("11. Add terms ew to terms template, add all terms to the page"); %>
	<mm:listnodes type="paginatemplate" constraints="url = 'terms.jsp'">
		<mm:node id="term_template" />
		<mm:listnodes type="editwizards" constraints="wizard = '/editors/config/terms/terms'">
			<mm:node id="term_ew">
				<mm:setfield name="description">Bewerk de begrippen op deze pagina</mm:setfield>
				<mm:setfield name="wizard">config/terms/terms</mm:setfield>
				<mm:setfield name="nodepath">pagina,contentrel,terms</mm:setfield>
				<mm:setfield name="fields">terms.name</mm:setfield>
				<mm:setfield name="orderby">terms.name</mm:setfield>
				<mm:setfield name="directions">up</mm:setfield>
				<mm:setfield name="pagelength">50</mm:setfield>
				<mm:setfield name="maxpagecount">100</mm:setfield>
				<mm:setfield name="searchfields">terms.name</mm:setfield>
				<mm:setfield name="search">yes</mm:setfield>
			</mm:node>
		</mm:listnodes>
		<mm:createrelation source="term_ew" destination="term_template" role="related" />
		<mm:relatednodes type="pagina">
			<mm:node id="terms_pagina" />
			<mm:listnodes type="terms" id="this_term">
				<mm:createrelation source="terms_pagina" destination="this_term" role="contentrel" />
			</mm:listnodes>
		</mm:relatednodes>
	</mm:listnodes>
	<% log.info("12. Set verloopdatum of pages to 2038"); %>
	<mm:listnodes type="pagina">
		<mm:setfield name="verloopdatum">2145913200</mm:setfield>
   </mm:listnodes>
	<% log.info("13. Hide pagina 'Zoeken' from navigation"); %>
	<mm:node number="search">
		<mm:setfield name="embargo">2145913200</mm:setfield>
	</mm:node>
	<% log.info("14. Remove duplicate users"); %>
	<% String lastAccount = ""; %>
	<mm:listnodes type="users" orderby="account,emailadres" directions="DOWN,DOWN">
		<mm:field name="account" jspvar="account" vartype="String" write="false">
			<% if(lastAccount.equals(account)) {
					%>Deleting duplicate account for <%= account %><br/>
					<mm:deletenode deleterelations="true" />
					<% 
				}
				lastAccount = account;
			%>
		</mm:field>
	</mm:listnodes>
	<% log.info("15. Rename education editwizard to their generic counterparts"); %> 
	<mm:listnodes type="editwizards" constraints="wizard = 'config/education_pools/wizard'">
		<mm:setfield name="name">opleidings categorieën</mm:setfield>
		<mm:setfield name="wizard">config/pools/pools_education</mm:setfield>
		<mm:setfield name="nodepath">topics,posrel,pools</mm:setfield>
		<mm:setfield name="fields">pools.name</mm:setfield>
		<mm:setfield name="orderby">pools.name</mm:setfield>
		<mm:setfield name="startnodes">education</mm:setfield>
	</mm:listnodes>
	<mm:listnodes type="editwizards" constraints="wizard = 'config/education_keywords/wizard'">
		<mm:setfield name="wizard">config/keywords/wizard</mm:setfield>
		<mm:setfield name="nodepath">keywords</mm:setfield>
	</mm:listnodes>
	<% log.info("16. Rename some admin editwizards"); %>
	<mm:listnodes type="editwizards" constraints="wizard = 'config/pools/pools'">
		<mm:setfield name="nodepath">pools</mm:setfield>
		<mm:setfield name="fields">name</mm:setfield>
		<mm:setfield name="orderby">name</mm:setfield>
		<mm:setfield name="searchfields">name</mm:setfield>
	</mm:listnodes>
	<mm:listnodes type="editwizards" constraints="wizard = 'config/mmbaseusers/mmbaseusers'">
		<mm:setfield name="wizard">/editors/usermanagement/userlist.jsp</mm:setfield>
		<mm:setfield name="type">jsp</mm:setfield>
	</mm:listnodes>
	<% log.info("17. Rename some shop editwizards"); %>
	<mm:listnodes type="editwizards" constraints="wizard = '/editors/config/items/items'">
		<mm:setfield name="wizard">config/items/items_shop</mm:setfield>
		<mm:setfield name="nodepath">items</mm:setfield>
	</mm:listnodes>
	<mm:listnodes type="editwizards" constraints="wizard = '/editors/config/pools/pools_items'">
		<mm:setfield name="wizard">config/formulier/formulier_shop</mm:setfield>
		<mm:setfield name="nodepath">formulier</mm:setfield>
		<mm:setfield name="fields">titel,type,pos</mm:setfield>
		<mm:setfield name="searchfields">titel,type</mm:setfield>
		<mm:setfield name="orderby">pos</mm:setfield>
		<mm:setfield name="startnodes"></mm:setfield>
	</mm:listnodes>
  <% log.info("18. Rename some producttype editwizards"); %>
  <mm:listnodes type="teaser">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <mm:createnode type="teaser" id="hw_teaser">
    <mm:setfield name="titel">hard-/software</mm:setfield>
  </mm:createnode>
	<mm:listnodes type="producttypes" constraints="title = 'Hardware' OR title = 'Software'" id="hw">
    <mm:createrelation source="hw_teaser" destination="hw" role="posrel" />
  </mm:listnodes>
  <mm:listnodes type="editwizards" constraints="name = 'standaard hard-/software'">
		<mm:setfield name="fields">producttypes.title,products.titel</mm:setfield>
		<mm:setfield name="orderby">producttypes.title,products.titel</mm:setfield>
	</mm:listnodes>
  <mm:listnodes type="editwizards" constraints="name = 'hard-/software per lokatie'">
		<mm:setfield name="fields">locations.naam,readmore.readmore,products.titel</mm:setfield>
		<mm:setfield name="orderby">locations.naam,products.titel</mm:setfield>
    <mm:related path="gebruikt,users" offset="1">
      <mm:deletenode element="gebruikt" />
    </mm:related>
	</mm:listnodes>
  <mm:list path="pagina,gebruikt,paginatemplate,related,editwizards" constraints="pagina.titel = 'Veelgestelde vragen over ICT'">
    <mm:deletenode element="gebruikt" />
    <mm:createnode type="paginatemplate" id="kb_cat_pt">
      <mm:setfield name="naam">kennisbasis</mm:setfield>
      <mm:setfield name="omschrijving">veelgestelde vragen, pagina moet ook link naar kbase bevatten</mm:setfield>
      <mm:setfield name="systemtemplate">0</mm:setfield>
      <mm:setfield name="dynamiclinklijsten">0</mm:setfield>
      <mm:setfield name="dynamicmenu">0</mm:setfield>
      <mm:setfield name="contenttemplate">0</mm:setfield>
      <mm:setfield name="url">iframe.jsp</mm:setfield>
    </mm:createnode>
    <mm:createnode type="editwizards" id="kb_cat_ew">
      <mm:setfield name="name">kennisbasis vraag</mm:setfield>
      <mm:setfield name="wizard">config/kb_question/kb_question</mm:setfield>
      <mm:setfield name="type">list</mm:setfield>
      <mm:setfield name="nodepath">pagina,posrel,kb_question</mm:setfield>
      <mm:setfield name="fields">kb_question.question</mm:setfield>
      <mm:setfield name="searchfields">kb_question.question</mm:setfield>
      <mm:setfield name="orderby">kb_question.question</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
    </mm:createnode>
    <mm:node element="pagina" id="kb_page" />
    <mm:node element="editwizards" id="iframe_ew" />
    <mm:createrelation source="kb_page" destination="kb_cat_pt" role="gebruikt" />
    <mm:createrelation source="kb_cat_pt" destination="iframe_ew" role="related" />
    <mm:createrelation source="kb_cat_pt" destination="kb_cat_ew" role="related" />
    <mm:listnodes type="kb_question" id="kb_q" >
      <mm:createrelation source="kb_page" destination="kb_q" role="posrel" />
    </mm:listnodes>
  </mm:list>
  <% log.info("19. Make the iptree wizard relative"); %>
  <mm:listnodes type="editwizards" constraints="wizard = '/editors/config/pagina/pagina_iptree'">
		<mm:setfield name="wizard">config/pagina/pagina_iptree</mm:setfield>
  </mm:listnodes>
  <% log.info("20. Make the ipmap wizard relative"); %>
  <mm:listnodes type="editwizards" constraints="wizard = '/editors/config/pagina/pagina_ipmap'">
		<mm:setfield name="wizard">config/pagina/pagina_ipmap</mm:setfield>
	</mm:listnodes>
   <% log.info("21. Rename menu for evenement_bleuprints"); %>
   <mm:listnodes type="menu" constraints="naam = 'Activiteiten'">
		<mm:setfield name="naam">Jeugdactiviteiten</mm:setfield>
	</mm:listnodes>
   <% log.info("22. Rename menu 'Archiefkast' to Contentlementen"); %>
   <mm:listnodes type="menu" constraints="naam = 'Archiefkast'">
		<mm:setfield name="naam">Contentelementen</mm:setfield>
	</mm:listnodes>
   <% log.info("23. Rename menu 'Archiefkast' to Contentlementen"); %>
	<mm:listnodes type="menu" constraints="naam = 'Speciaal onderhoud'">
	    <mm:node id="so" />
   	 <mm:createnode type="editwizards" id="stats_ew">
         <mm:setfield name="name">statistieken</mm:setfield>
         <mm:setfield name="wizard">/editors/simplestats/stats.jsp</mm:setfield>
         <mm:setfield name="type">jsp</mm:setfield>
       </mm:createnode>
       <mm:createrelation source="so" destination="stats_ew" role="posrel">
          <mm:setfield name="pos">80</mm:setfield>
       </mm:createrelation>
	</mm:listnodes>
	<% log.info("24. Resize image of project phases"); %>
   <mm:listnodes type="images" constraints="titel = 'Fasen'">
	  <mm:node>
      <mm:setfield name="screensize">1</mm:setfield>
    </mm:node>
	</mm:listnodes>
  <% log.info("25. Changing the product editwizard"); %>
  <mm:listnodes type="editwizards" constraints="wizard = 'config/items/items_shop'">
		<mm:setfield name="fields">titel,id,type,price1,owner,quotetitle</mm:setfield>
	</mm:listnodes>
  <% log.info("26. Adding editwizard for forum template"); %>
  <mm:createnode type="editwizards" id="forum_wizard">
		<mm:setfield name="name">forum pagina</mm:setfield>
		<mm:setfield name="description">Voeg een forum toe aan deze pagina</mm:setfield>
		<mm:setfield name="type">wizard</mm:setfield>
		<mm:setfield name="wizard">config/pagina/pagina_forum</mm:setfield>
		<mm:setfield name="nodepath">pagina</mm:setfield>
		<mm:setfield name="fields">titel</mm:setfield>
		<mm:setfield name="orderby">titel</mm:setfield>
		<mm:setfield name="directions">up</mm:setfield>
		<mm:setfield name="pagelength">50</mm:setfield>
		<mm:setfield name="maxpagecount">100</mm:setfield>
		<mm:setfield name="searchfields">titel</mm:setfield>
		<mm:setfield name="search">yes</mm:setfield>
	</mm:createnode>
	<mm:listnodes type="paginatemplate" constraints="url = 'forum.jsp'">
		<mm:node id="forum_template" />
		<mm:createrelation source="forum_template" destination="forum_wizard" role="related" />
	</mm:listnodes>
	<% log.info("99. Deleting unused editwizards"); %>
	<%
	String [] ewToDelete = {
		"config/divisions/divisions",
		"/editors/empupdates.jsp",
		"/editors/employees.jsp",
		"config/pijler/pijler",
		"config/rubriek/rubriek",
		"config/pagina/pagina",
		"config/menu/menu",
		"/editors/parcleaner/cleanarticles.jsp",
    "config/feedback/wizard",
    "/editors/items.jsp",
    "/editors/imap_overview.jsp",
    "config/linklijst/linklijst_knipsels",
    "config/pagina/pagina_topic",
    "config/images/images_knipsels",
    "config/programs/programs",
    "config/companies/companies"
	};
	for(int i=0; i<ewToDelete.length;i++) {
		%><mm:listnodes type="editwizards" constraints="<%= "wizard = '" + ewToDelete[i]  + "'" %>">
			<mm:deletenode deleterelations="true" />
		 </mm:listnodes><%
	}
	String [] menuToDelete = {
		"Bibliotheek beheer",
		"Home",
		"Subrubriek editors",
		"Website beheer",
    "Interne Webwinkel (redactie)",
    "Kiezen en Delen",
    "Knipsels, bijlagen (niet in gebruik)"
	};
	for(int i=0; i<menuToDelete.length;i++) {
		%><mm:listnodes type="menu" constraints="<%= "naam = '" + menuToDelete[i]  + "'" %>">
			<mm:deletenode deleterelations="true" />
		 </mm:listnodes><%
	}
	String [] ewToRename = {
		"/editors/cache/flush.jsp?command=all",
    "config/pagina/pagina_form",
    "/editors/stats/shopstats.jsp",
    "config/artikel/artikel"
	};
	String [] ewNewName = {
		"/editors/util/flushcache.jsp",
    "config/pagina/pagina_formulier",
    "/editors/simplestats/shopstats.jsp",
    "config/artikel/artikel_nieuws_nmintra_noorigin"
	};
	for(int i=0; i<ewToRename.length;i++) {
		%><mm:listnodes type="editwizards" constraints="<%= "wizard = '" + ewToRename[i]  + "'" %>">
			<mm:setfield name="wizard"><%= ewNewName[i] %></mm:setfield>
		 </mm:listnodes><%
	}
	%>
  <% log.info("100. Create feedback editwizard per template (should come after deletion of general feedback ew)"); %>
  <mm:listnodes type="menu" constraints="naam = 'Opleidingen'">
    <mm:node id="mo" />
    <mm:createnode type="editwizards" id="edu_feedback">
      <mm:setfield name="name">feedback op opleidingen</mm:setfield>
      <mm:setfield name="description"></mm:setfield>
      <mm:setfield name="type">wizard</mm:setfield>
      <mm:setfield name="wizard">config/feedback/wizard</mm:setfield>
      <mm:setfield name="nodepath">educations,feedback</mm:setfield>
      <mm:setfield name="fields">feedback.namesender,feedback.emailsender,feedback.topic</mm:setfield>
      <mm:setfield name="orderby">feedback.namesender</mm:setfield>
      <mm:setfield name="directions">up</mm:setfield>
      <mm:setfield name="pagelength">50</mm:setfield>
      <mm:setfield name="maxpagecount">100</mm:setfield>
      <mm:setfield name="searchfields">feedback.namesender,feedback.emailsender,feedback.topic</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
    </mm:createnode>
  	<mm:createrelation source="mo" destination="edu_feedback" role="posrel">
       <mm:setfield name="pos">80</mm:setfield>
    </mm:createrelation>
	</mm:listnodes>
  <mm:listnodes type="menu" constraints="naam = 'Jeugdactiviteiten'">
    <mm:node id="jo" />
    <mm:createnode type="editwizards" id="ev_feedback">
      <mm:setfield name="name">feedback op activiteiten</mm:setfield>
      <mm:setfield name="description"></mm:setfield>
      <mm:setfield name="type">wizard</mm:setfield>
      <mm:setfield name="wizard">config/feedback/wizard</mm:setfield>
      <mm:setfield name="nodepath">evenement_blueprint,feedback</mm:setfield>
      <mm:setfield name="fields">feedback.namesender,feedback.emailsender,feedback.topic</mm:setfield>
      <mm:setfield name="orderby">feedback.namesender</mm:setfield>
      <mm:setfield name="directions">up</mm:setfield>
      <mm:setfield name="pagelength">50</mm:setfield>
      <mm:setfield name="maxpagecount">100</mm:setfield>
      <mm:setfield name="searchfields">feedback.namesender,feedback.emailsender,feedback.topic</mm:setfield>
      <mm:setfield name="search">yes</mm:setfield>
    </mm:createnode>
  	<mm:createrelation source="jo" destination="ev_feedback" role="posrel">
       <mm:setfield name="pos">80</mm:setfield>
    </mm:createrelation>
	</mm:listnodes>
	Done.<br/>
</mm:log>
</mm:cloud>
