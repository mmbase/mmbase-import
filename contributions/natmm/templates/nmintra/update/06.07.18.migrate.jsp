<%@page import="org.mmbase.bridge.*" %>
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
	<mm:createnode type="topics" id="news_topic">
		<mm:setfield name="title">news</mm:setfield>
	</mm:createnode>
	<mm:node number="news_topic">
		<mm:setalias>nieuws</mm:setalias>
	</mm:node>
	<mm:listnodes type="rubriek" constraints="naam = 'Home'" id="news_rubriek">
		<mm:creatrelation source="news_rubriek" destination="news_topic" role="related" />
	</mm:listnodes>
	<mm:createnode type="topics" id="edu_topic">
		<mm:setfield name="title">opleidingen</mm:setfield>
	</mm:createnode>
	<mm:node number="edu_topic">
		<mm:setalias>education</mm:setalias>
	</mm:node>
	<mm:listnodes type="rubriek" constraints="naam = 'Opleiding en ontwikkeling'" id="edu_rubriek">
		<mm:creatrelation source="edu_rubriek" destination="edu_topic" role="related" />
	</mm:listnodes>
	<mm:listnodes type="menu" constraints="naam = 'Opleidingen'" id="edu_menu">
		<mm:creatrelation source="edu_menu" destination="edu_topic" role="related" />
	</mm:listnodes>
	1. Relates the already present pools to the topic "nieuws".<br/>
   <mm:listnodes type="pools" id="news_pool">
      <mm:createrelation role="posrel" source="news_topic" destination="news_pool" />
   </mm:listnodes>
	2. Moves the education_keywords to keywords with a related topic "opleidingen".<br/>
   <mm:listnodes type="education_keywords">
      <mm:field name="word" id="kw_word" write="false" />
		<mm:createnode type="keywords" id="kw">
			<mm:setfield name="word"><mm:write referid="kw_word" /></mm:setfield>
		</mm:createnode>
		<mm:relatednodes type="educations" id="edu">
			<mm:createrelation source="edu" destination="kw" role="related" />
		</mm:relatednodes>
		<mm:createrelation role="posrel" source="edu_topic" destination="kw" />
		<mm:deletenode deleterelations="true" />
   </mm:listnodes>
	3. Moves the education_pools to pools with a related topic "opleiding".<br/>
	<mm:listnodes type="education_pools">
			<mm:field name="name" jspvar="name" vartype="String" write="false">
			<mm:field name="description" jspvar="description" vartype="String" write="false">
			<mm:field name="language" jspvar="language" vartype="String" write="false">
			<mm:field name="view" jspvar="view" vartype="String" write="false">
				<mm:remove referid="this_pool" />
				<mm:listnodes type="pools" constraints="<%= "UPPER(name) LIKE '%" + name.toUpperCase() + "%'" %>">
					<mm:node id="this_pool" />
				</mm:listnodes>
				<mm:notpresent referid="this_pool">
					<mm:createnode type="pools" id="this_pool">
						<mm:setfield name="name"><%= name %></mm:setfield>
						<mm:setfield name="description"><%= description %></mm:setfield>
						<mm:setfield name="language"><%= language %></mm:setfield>
						<mm:setfield name="view"><%= view %></mm:setfield>
					</mm:createnode>
				</mm:notpresent>
			</mm:field>
			</mm:field>
			</mm:field>
			</mm:field>
			<mm:related path="posrel,educations">
				<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false">
					<mm:node element="educations" id="this_edu">
						<mm:createrelation role="posrel" source="this_pool" destination="this_edu">
							<mm:setfield name="pos"><%= posrel_pos %></mm:setfield>
						</mm:createrelation>
					</mm:node>
				</mm:field>
			</mm:related>
			<mm:createrelation role="posrel" source="edu_topic" destination="this_pool" />
			<mm:deletenode deleterelations="true" />
	</mm:listnodes>
	</body>
  </html>
</mm:log>
</mm:cloud>
