<%@page import="java.util.*,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
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
   Changes made in this update:<br/>
	1. Renaming rubriek "Natuurherstelprojecten in Nederland" (portal imported from MicroSites application) into "Natuurherstel in Nederland"<br/>
	2. Relating rubriek "Natuurherstel in Nederland" to the rubriek "Natuurmonumenten" and setting parent.pos equals 50. <br/>
	3. Treating all articles imported from MS. Removing #NZ# string from article titel and setting titel_zichtbaar field to equals 0.<br/>
	4. Treating all paragraafs imported from MS. Removing #NZ# string from paragraaf titel and setting titel_zichtbaar field to equals 0.<br/>
	5. Treating all images imported from MS. Removing #NZ# string from images titel and setting titel_zichtbaar field to equals 0.<br/>
	6. Creating jumpers for rubrieks imported from MicroSite application.<br/>
   7. Setting the levels of the rubrieken<br/>
	8. Changing templates.url from templates/*.jsp to *.jsp<br/>
	9. Adding alias "natuurherstel_home" to the pagina Natuurherstel in Nederland<br/>
	10. Adding the editwizards for the pages<br/>
	11. Setting all rubrieken to visible<br/>
	<span style="color:red;">Run <a href="/editors/util/fill_empty_par_title.jsp">fill empty par title</a> !!!</span><br/>
	Processing...<br/>
	<mm:listnodes type="rubriek" constraints="rubriek.naam='Natuurherstelprojecten in Nederland'">
		<mm:node id="portal">
			<mm:setfield name="naam">Natuurherstel in Nederland</mm:setfield>
		</mm:node>
	</mm:listnodes>
	<mm:node number="root" id="parent"/>
	<mm:createrelation role="parent" source="parent" destination="portal">
		<mm:setfield name="pos">50</mm:setfield>
	</mm:createrelation>
	<mm:listnodes type="artikel">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<mm:field name="omschrijving" jspvar="body" vartype="String" write="false"><% 
		      if(titel==null||titel.trim().equals("#NZ#")) { 
        		 body = HtmlCleaner.cleanText(body,"<",">","").trim();
		         int spacePos = body.indexOf(" ",50); 
        		 if(spacePos>-1) { 
		            body = body.substring(0,spacePos);
        		 } %>
        		 <mm:setfield name="titel"><%= body %></mm:setfield>
				 <mm:setfield name="titel_zichtbaar">0</mm:setfield><% 
		      } 
			  if (titel.indexOf("#NZ#")>-1){
			  	titel = titel.replaceAll("#NZ#","");
				if (titel.startsWith(" ")) {
					titel = titel.substring(1);
				}%>
				<mm:setfield name="titel"><%= titel %></mm:setfield>
  			    <mm:setfield name="titel_zichtbaar">0</mm:setfield>
		   <% }%>
			</mm:field>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="paragraaf">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<mm:field name="omschrijving" jspvar="body" vartype="String" write="false"><% 
		      if(titel==null||titel.trim().equals("#NZ#")) { 
        		 body = HtmlCleaner.cleanText(body,"<",">","").trim();
		         int spacePos = body.indexOf(" ",50); 
        		 if(spacePos>-1) { 
		            body = body.substring(0,spacePos);
        		 } %>
        		 <mm:setfield name="titel"><%= body %></mm:setfield>
				 <mm:setfield name="titel_zichtbaar">0</mm:setfield><% 
		      } 
			  if (titel.indexOf("#NZ#")>-1){
			  	titel = titel.replaceAll("#NZ#","");
				if (titel.startsWith(" ")) {
					titel = titel.substring(1);
				}%>
				<mm:setfield name="titel"><%= titel %></mm:setfield>
  			    <mm:setfield name="titel_zichtbaar">0</mm:setfield>
		   <% }%>
			</mm:field>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="images">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<mm:field name="omschrijving" jspvar="body" vartype="String" write="false"><% 
		      if(titel==null||titel.trim().equals("#NZ#")) { 
        		 body = HtmlCleaner.cleanText(body,"<",">","").trim();
		         int spacePos = body.indexOf(" ",50); 
        		 if(spacePos>-1) { 
		            body = body.substring(0,spacePos);
        		 } %>
        		 <mm:setfield name="titel"><%= body %></mm:setfield>
				 <mm:setfield name="titel_zichtbaar">0</mm:setfield><% 
		      } 
			  if (titel.indexOf("#NZ#")>-1){
			  	titel = titel.replaceAll("#NZ#","");
				if (titel.startsWith(" ")) {
					titel = titel.substring(1);
				}%>
				<mm:setfield name="titel"><%= titel %></mm:setfield>
  			    <mm:setfield name="titel_zichtbaar">0</mm:setfield>
		   <% }%>
			</mm:field>
		</mm:field>
	</mm:listnodes>
	<mm:list nodes="natuurherstel_home" path="rubriek1,parent,rubriek2" orderby="rubriek2.number" directions="down" constraints="parent.pos=-1"
		><mm:field name="rubriek2.number" jspvar="rubriek_number" vartype="String" write="false"
		    ><mm:field name="rubriek2.naam" jspvar="rubriek_name" vartype="String" write="false"
			    ><mm:createnode type="jumpers"
        			><mm:setfield name="name"><%= HtmlCleaner.stripText(rubriek_name).replaceAll("_","") %></mm:setfield
		        	><mm:setfield name="url">/microsites/index.jsp?r=<%= rubriek_number %></mm:setfield
			    ></mm:createnode
		    ></mm:field
	    ></mm:field
	></mm:list>
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
	<mm:listnodes type="paginatemplate">
		<mm:field name="url" jspvar="url" vartype="String" write="false">
			<mm:setfield name="url"><%= url.substring(10) %></mm:setfield>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="pagina" constraints="pagina.titel='Natuurherstelprojecten in Nederland'">
		<mm:node>
			<mm:createalias>natuurherstel_home</mm:createalias>
		</mm:node>
	</mm:listnodes>
	<mm:listnodes type="paginatemplate" constraints="paginatemplate.url='thumbs.jsp'">
		<mm:node id="thumbs_template">
			<mm:createnode type="editwizards" id="thumbs_ew">
				<mm:setfield name="name">foto pagina</mm:setfield>
				<mm:setfield name="description">Bewerk deze fotopagina</mm:setfield>
				<mm:setfield name="type">wizard</mm:setfield>
				<mm:setfield name="wizard">config/pagina/pagina_thumbs</mm:setfield>
				<mm:setfield name="nodepath">pagina</mm:setfield>
				<mm:setfield name="fields">pagina.titel,pagina.kortetitel</mm:setfield>
				<mm:setfield name="orderby">pagina.titel</mm:setfield>
				<mm:setfield name="directions">UP</mm:setfield>
				<mm:setfield name="pagelength">50</mm:setfield>
			</mm:createnode>
			<mm:createrelation source="thumbs_template" destination="thumbs_ew" role="related" />
		</mm:node>
	</mm:listnodes>
	<mm:listnodes type="editwizards" constraints="editwizards.wizard='config/artikel/artikel' AND editwizards.type='list'">
		<mm:node id="info_ew">
			<mm:listnodes type="paginatemplate" constraints="paginatemplate.url='info.jsp'">
				<mm:node id="this_template">
					<mm:createrelation source="this_template" destination="info_ew" role="related" />
				</mm:node>
			</mm:listnodes>
		</mm:node>
	</mm:listnodes>
	<mm:listnodes type="paginatemplate" constraints="paginatemplate.url='article.jsp' OR paginatemplate.url='homepage.jsp'  OR paginatemplate.url='websites.jsp'">
		<mm:node id="article_template">
			<mm:first>
			<mm:createnode type="editwizards" id="artikel_page_ew">
				<mm:setfield name="name">artikel pagina</mm:setfield>
				<mm:setfield name="description">Bewerk deze artikel pagina</mm:setfield>
				<mm:setfield name="type">wizard</mm:setfield>
				<mm:setfield name="wizard">config/pagina/pagina_article</mm:setfield>
				<mm:setfield name="nodepath">pagina</mm:setfield>
				<mm:setfield name="fields">pagina.titel,pagina.kortetitel</mm:setfield>
				<mm:setfield name="orderby">pagina.titel</mm:setfield>
				<mm:setfield name="directions">UP</mm:setfield>
				<mm:setfield name="pagelength">50</mm:setfield>
			</mm:createnode>
			</mm:first>
			<mm:createrelation source="article_template" destination="artikel_page_ew" role="related" />
		</mm:node>
	</mm:listnodes>
	<mm:listnodes type="paragraaf">
		<mm:setfield name="tekst"><mm:field name="omschrijving" /></mm:setfield>
		<mm:setfield name="omschrijving"> </mm:setfield>
	</mm:listnodes>
	<mm:listnodes type="artikel">
		<mm:setfield name="begindatum"><mm:field name="embargo" /></mm:setfield>
		<mm:setfield name="use_verloopdatum">1</mm:setfield>
	</mm:listnodes>
	<mm:list nodes="" path="paginatemplate,gebruikt,pagina,contentrel,artikel" constraints="paginatemplate.url!='info.jsp'">
		<mm:node element="artikel">
			<mm:setfield name="use_verloopdatum">0</mm:setfield>
		</mm:node>
	</mm:list>
	<mm:listnodes type="rubriek">
		<mm:setfield name="url">1</mm:setfield>
	</mm:listnodes>
	<mm:listnodes type="paginatemplate">
		<mm:field name="naam" jspvar="name" vartype="String" write="false">
			<mm:setfield name="naam"><%= name + " (natuurherstel)" %></mm:setfield>   
		</mm:field>
		<mm:setfield name="systemtemplate">0</mm:setfield>   
		<mm:setfield name="dynamiclinklijsten">0</mm:setfield>
		<mm:setfield name="dynamicmenu">0</mm:setfield>
		<mm:setfield name="contenttemplate">0</mm:setfield>
	</mm:listnodes>
   Done.
   </body>
</html>
</mm:cloud>
