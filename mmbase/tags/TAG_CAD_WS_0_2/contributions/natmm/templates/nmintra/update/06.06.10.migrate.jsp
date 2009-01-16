<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.06.10"); %>
   <% log.info("1. Move news archief to seperate rubriek"); %>
   <mm:node number="home" id="natuurmonumente_subsite" />
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
        <mm:createnode type="rubriek" id="archive_rubriek">
          <mm:setfield name="naam">Archief</mm:setfield>
        </mm:createnode>
        <mm:node number="$archive_rubriek">
           <mm:createalias>archive</mm:createalias>
        </mm:node>
        <mm:createrelation source="natuurmonumente_subsite" destination="archive_rubriek" role="parent">
          <mm:setfield name="pos">99</mm:setfield>
        </mm:createrelation>
         <mm:createrelation source="archive_rubriek" destination="archief" role="posrel">
          <mm:setfield name="pos">1</mm:setfield>
        </mm:createrelation>
      </mm:node>
    </mm:listnodes>
   </mm:listnodes>
   <% log.info("2. Merge library archief with news archief"); %>
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
   </mm:listnodes>
   <% log.info("3. artikel met info pagina"); %>
   <mm:listnodes type="editwizards" constraints="name = 'standaard pagina'">
      <mm:node id="def_ew" />
   </mm:listnodes>
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
</mm:log>
</mm:cloud>
