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
	Manual<br/>
	1. Delete template for "Wat vindt je ervan?" page in P&O<br/>
	2. Delete double relation for "Zoek een opleiding" page<br/>
	</body>
  </html>
</mm:log>
</mm:cloud>
