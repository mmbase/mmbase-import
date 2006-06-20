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
	Things to be done in this update:<br/>
	<mm:node number="home" id="natuurmonumente_subsite" />
	0. Adding users ew to admin<br/>
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
	1. Changing the wizard type of the page wizards:<br/>
	<mm:listnodes type="editwizards" constraints="wizard LIKE '%pagina_%'">
		<mm:setfield name="type">wizard</mm:setfield>
	</mm:listnodes>
	2. Homepage<br/>
	<mm:createnode type="editwizards" id="news_wizard">
		<mm:setfield name="name">nieuws (list)</mm:setfield>
		<mm:setfield name="description">Bewerk de nieuws artikelen van deze pagina</mm:setfield>
		<mm:setfield name="type">list</mm:setfield>
		<mm:setfield name="wizard">config/artikel/artikel_nieuws</mm:setfield>
		<mm:setfield name="nodepath">pagina,contentrel,artikel</mm:setfield>
		<mm:setfield name="fields">artikel.titel,artikel.begindatum,artikel.embargo,artikel.verloopdatum,artikel.use_verloopdatum</mm:setfield>
		<mm:setfield name="orderby">artikel.begindatum</mm:setfield>
		<mm:setfield name="directions">down</mm:setfield>
		<mm:setfield name="pagelength">50</mm:setfield>
		<mm:setfield name="maxpagecount">100</mm:setfield>
		<mm:setfield name="searchfields">artikel.titel</mm:setfield>
		<mm:setfield name="search">yes</mm:setfield>
	</mm:createnode>
	<mm:node number="homepage_template" id="homepage_template" />
	<mm:createrelation source="homepage_template" destination="news_wizard" role="related" />
	<mm:listnodes type="paginatemplate" constraints="url LIKE '%info%'">
		<mm:remove referid="info_template" />
		<mm:node id="info_template" />
		<mm:createrelation source="info_template" destination="news_wizard" role="related" />
	</mm:listnodes>
	3. Archief<br/>
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
	</mm:listnodes>
	4. Prikbord<br/>
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
	5. Changing editwizards because of change in builder<br/>
	Todo: check with last version of db.
	<mm:listnodes type="editwizards" constraints="wizard = 'config/educations/wizard'">
		<mm:setfield name="fields">titel</mm:setfield>
		<mm:setfield name="orderby">titel</mm:setfield>
	</mm:listnodes>
	6. Renaming editwizards that should use the default page editor<br/>
	<mm:createnode type="editwizards" id="def_ew">
		<mm:setfield name="name">standaard pagina</mm:setfield>
		<mm:setfield name="description">Bewerk de basis gegevens van deze pagina</mm:setfield>
		<mm:setfield name="type">wizard</mm:setfield>
		<mm:setfield name="wizard">config/pagina/pagina_default</mm:setfield>
		<mm:setfield name="nodepath"></mm:setfield>
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
		"docpage.jsp"
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
	7. rename artikel ew which collapses with the natmm artikel template
	<mm:listnodes type="editwizards" constraints="wizard = 'config/pagina/pagina_artikel'">
		<mm:setfield name="wizard">config/pagina/pagina_artikel_nmintra</mm:setfield>
	</mm:listnodes>
	8. move imap ew to overview
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
   9. Change name of page editwizards<br/>
	<mm:listnodes type="editwizards" constraints="name LIKE 'subr. vh genre %'">
	   <mm:field name="name" jspvar="name" vartype="String" write="false">
	      <% name  = name.substring(15) + " pagina"; %>
		   <mm:setfield name="name"><%= name %></mm:setfield>
		   <mm:setfield name="description"><%= "Bewerk deze " + name %></mm:setfield>
		</mm:field>
	</mm:listnodes>
   10. Add project overview ew to project archive template<br/>
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
	11. Add terms ew to terms template, add all terms to the page<br/>
	<mm:listnodes type="paginatemplate" constraints="url = 'terms.jsp'">
		<mm:node id="term_template" />
		<mm:listnodes type="editwizards" constraints="wizard = '/editors/config/terms/terms'">
			<mm:node id="term_ew">
				<mm:setfield name="description">Bewerk de begrippen op deze pagina</mm:setfield>
				<mm:setfield name="wizard">/editors/projects/project_overview.jsp</mm:setfield>
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
	12. Set verloopdatum of pages to 2038<br/>		
	<mm:listnodes type="pagina">
		<mm:setfield name="verloopdatum">2145913200</mm:setfield>
   </mm:listnodes>
	13. Hide pagina "Zoeken" from navigation
	<mm:node number="search">
		<mm:setfield name="embargo">2145913200</mm:setfield>
	</mm:node>
	14. Remove duplicate users
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
	14. Move news archief to main level<br/>
	<mm:listnodes type="rubriek" constraints="naam = 'Home'">
		<mm:node id="home_rubriek" />
			<mm:listnodes type="users" constraints="account = 'KemperinkM'">
				<mm:node id="news_editor" />
				<mm:node number="archief" id="archief">
					<mm:relatednodes type="artikel" id="this_artikel">
						<mm:createrelation source="this_artikel" destination="natuurmonumente_subsite" role="subsite" />
						<mm:createrelation source="this_artikel" destination="home_rubriek" role="creatierubriek" />
						<mm:createrelation source="this_artikel" destination="home_rubriek" role="hoofdrubriek" />
						<mm:createrelation source="this_artikel" destination="news_editor" role="schrijver" />
					</mm:relatednodes>
					<mm:related path="posrel,rubriek">
						<mm:deletenode element="posrel" />
					</mm:related>
					<mm:createrelation source="natuurmonumente_subsite" destination="archief" role="posrel">
						<mm:setfield name="pos">99</mm:setfield>
					</mm:createrelation>
				</mm:node>
			</mm:listnodes>
		</mm:node>
	</mm:listnodes>
	14. Merge library archief with news archief<br/>
	<mm:createnode type="pools" id="bib_pool">
		<mm:setfield name="name">Bibliotheek</mm:setfield>
	</mm:createnode>
	<mm:listnodes type="rubriek" constraints="naam = 'Bibliotheek'">
		<mm:node id="bib_rubriek" />
			<mm:listnodes type="users" constraints="account = 'BieW'">
				<mm:node id="library_editor" />
				<mm:node number="archief" id="news_archief" />
				<mm:node number="bibarchief">
					<mm:relatednodes type="artikel" id="this_artikel">
						<mm:createrelation source="this_artikel" destination="natuurmonumente_subsite" role="subsite" />
						<mm:createrelation source="this_artikel" destination="bib_rubriek" role="creatierubriek" />
						<mm:createrelation source="this_artikel" destination="bib_rubriek" role="hoofdrubriek" />
						<mm:createrelation source="this_artikel" destination="library_editor" role="schrijver" />
						<mm:createrelation source="this_artikel" destination="bib_pool" role="posrel" />
						<mm:createrelation source="news_archief" destination="this_artikel" role="contentrel" />
					</mm:relatednodes>
				</mm:node>
				<mm:deletenode number="bibarchief" deleterelations="true" />
			</mm:listnodes>
		</mm:node>
	</mm:listnodes>
	15. artikel met info pagina<br/>
	<mm:node number="104653" id="def_ew" />
	<mm:listnodes type="editwizards" constraints="wizard = 'config/pagina/pagina_artikel_info'">
		<mm:relatednodes type="paginatemplate" id="artikel_info_template">
			<mm:createrelation source="artikel_info_template" destination="def_ew" role="related" />
			<mm:relatednodes type="pagina">
				<% String artikel_text = ""; %>
				<mm:related path="contentrel,artikel" fields="artikel.intro" constraints="contentrel.pos='0'"
					orderby="artikel.embargo" directions="UP" searchdir="destination" max="1">
					<mm:field name="artikel.intro" jspvar="dummy" vartype="String" write="false">
						<% artikel_text = dummy + "<br/><br/>"; %>
					</mm:field>
					<mm:node element="artikel">
						<mm:related path="posrel,paragraaf" fields="paragraaf.titel,paragraaf.tekst"  orderby="posrel.pos" directions="UP">
							<mm:field name="paragraaf.titel" jspvar="dummy" vartype="String" write="false">
								<% artikel_text += "<b>" + dummy + "</b><br/>"; %>
							</mm:field>
							<mm:field name="paragraaf.tekst" jspvar="dummy" vartype="String" write="false">
								<% artikel_text += dummy + "<br/><br/>"; %>
							</mm:field>
							<mm:deletenode element="paragraaf" deleterelations="true" />
						</mm:related>
					</mm:node>
					<mm:deletenode element="artikel" deleterelations="true" />
				</mm:related>
				<mm:setfield name="omschrijving"><%= artikel_text %></mm:setfield>
			</mm:relatednodes>
		</mm:relatednodes>
	   <mm:deletenode deleterelations="true" />
	</mm:listnodes>
	99. Deleting unused editwizards<br/>
	<%
	String [] ewToDelete = {
		"config/divisions/divisions",
		"/editors/empupdates.jsp",
		"/editors/employees.jsp"
		};
	for(int i=0; i<ewToDelete.length;i++) {
		%><mm:listnodes type="editwizards" constraints="<%= "wizard = '" + ewToDelete[i]  + "'" %>">
			<mm:deletenode deleterelations="true" />
		 </mm:listnodes><%
	}
	String [] ewToRename = {
		"/editors/cache/flush.jsp?command=all"
		};
	String [] ewNewName = {
		"/editors/util/flushcache.jsp"
		};
	for(int i=0; i<ewToRename.length;i++) {
		%><mm:listnodes type="editwizards" constraints="<%= "wizard = '" + ewToRename[i]  + "'" %>">
			<mm:setfield name="wizard"><%= ewNewName[i] %></mm:setfield>
		 </mm:listnodes><%
	}
	%>
	Done.<br/>
	</body>
  </html>
</mm:log>
</mm:cloud>
