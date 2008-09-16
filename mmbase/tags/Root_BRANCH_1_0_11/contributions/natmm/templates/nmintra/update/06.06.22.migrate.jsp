<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("06.06.22"); %>
	Things to be done in this update:<br/>
	1. Moves the item_pools related to the community to formulieren.<br/>
	For adding the pos and type field to already existing dbs:<br/> 
	ALTER TABLE `v1_formulier` ADD `pos` INT( 11 ) DEFAULT '-1' NOT NULL;<br/>
	ALTER TABLE `v1_formulier` ADD `m_type` VARCHAR( 64 );<br/>
	<mm:node number="webwinkel_community">
		<mm:relatednodes type="pools" id="this_pool">
			<mm:field name="name" jspvar="name" vartype="String" write="false">
			<mm:field name="description" jspvar="description" vartype="String" write="false">
			<mm:field name="language" jspvar="language" vartype="String" write="false">
			<mm:field name="view" jspvar="view" vartype="String" write="false">
			<mm:createnode type="formulier" id="this_form">
				<mm:setfield name="titel"><%= name %></mm:setfield>
				<mm:setfield name="omschrijving"><%= description %></mm:setfield>
				<mm:setfield name="pos"><%= language %></mm:setfield>
				<mm:setfield name="type"><%= view %></mm:setfield>
			</mm:createnode>
			</mm:field>
			</mm:field>
			</mm:field>
			</mm:field>
			<mm:related path="posrel,formulierveld">
				<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false">
					<mm:node element="formulierveld" id="this_field">
						<mm:createrelation role="posrel" source="this_form" destination="this_field">
							<mm:setfield name="pos"><%= posrel_pos %></mm:setfield>
						</mm:createrelation>
					</mm:node>
				</mm:field>
			</mm:related>
			<mm:related path="posrel,items">
				<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false">
					<mm:node element="items" id="this_item">
						<mm:createrelation role="posrel" source="this_item" destination="this_form">
							<mm:setfield name="pos"><%= posrel_pos %></mm:setfield>
						</mm:createrelation>
					</mm:node>
				</mm:field>
			</mm:related>
			<mm:deletenode deleterelations="true" />
		</mm:relatednodes>
	</mm:node>
	<mm:deletenode number="webwinkel_community" />
</mm:log>
</mm:cloud>
