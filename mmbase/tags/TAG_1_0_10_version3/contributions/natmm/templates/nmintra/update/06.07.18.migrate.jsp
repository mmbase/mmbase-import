<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.07.18"); %>
	Things to be done in this update:<br/>
	<mm:createnode type="topics" id="news_topic">
		<mm:setfield name="title">nieuws</mm:setfield>
	</mm:createnode>
	<mm:node number="$news_topic">
		<mm:createalias>news</mm:createalias>
	</mm:node>
	<mm:listnodes type="rubriek" constraints="naam = 'Home'" id="news_rubriek">
		<mm:createrelation source="news_rubriek" destination="news_topic" role="related" />
	</mm:listnodes>
	<mm:createnode type="topics" id="edu_topic">
		<mm:setfield name="title">opleidingen</mm:setfield>
	</mm:createnode>
	<mm:node number="$edu_topic">
		<mm:createalias>education</mm:createalias>
	</mm:node>
	<mm:listnodes type="rubriek" constraints="naam = 'Opleiding en ontwikkeling'">
    <mm:node id="edu_rubriek" />
		<mm:createrelation source="edu_rubriek" destination="edu_topic" role="related" />
	</mm:listnodes>
	<mm:listnodes type="menu" constraints="naam = 'Opleidingen'">
    <mm:node id="edu_menu" />
		<mm:createrelation source="edu_menu" destination="edu_rubriek" role="related" />
	</mm:listnodes>
	1. Relates the already present pools to the topic "nieuws".<br/>
  <mm:listnodes type="pools">
      <mm:node id="news_pool" />
      <mm:createrelation source="news_topic" destination="news_pool" role="posrel"/>
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
      	<mm:createnode type="pools" id="this_pool">
      		<mm:setfield name="name"><%= name %></mm:setfield>
      		<mm:setfield name="description"><%= description %></mm:setfield>
      		<mm:setfield name="language"><%= language %></mm:setfield>
      		<mm:setfield name="view"><%= view %></mm:setfield>
      	</mm:createnode>
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
</mm:log>
</mm:cloud>
