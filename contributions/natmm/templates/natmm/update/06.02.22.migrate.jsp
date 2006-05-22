<%@page import="java.io.*,org.mmbase.bridge.*" %>
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
   1. Changing all the menu objects for which menu.url field is empty into editwizards objects and relating them to the menu with the object alias "admin_wizards".<br/>
   <mm:node number="67850">
      <mm:createalias>evenementen_beheer</mm:createalias>
   </mm:node>
   <mm:node number="users.admin" id="admin_user" />
	<mm:createnode type="menu" id="admin_wizards">
      <mm:setfield name="naam">Beheer website</mm:setfield>
   </mm:createnode>
	<mm:listnodes type="menu" constraints="url!=''" directions="UP">
		<mm:field name="url" jspvar="url" vartype="String" write="false">
			<mm:field name="naam" jspvar="name" vartype="String" write="false">
				<mm:createnode type="editwizards" id="new_editwizard">
					<mm:setfield name="name"><%= name %></mm:setfield>
					<mm:setfield name="type">jsp</mm:setfield>
					<mm:setfield name="wizard"><%= url %></mm:setfield>
				</mm:createnode>
				<mm:createrelation role="posrel" source="admin_wizards" destination="new_editwizard"/>
				<mm:createrelation role="gebruikt" source="admin_user" destination="admin_wizards"/>
				<mm:remove referid="new_editwizard"/>
				<mm:deletenode deleterelations="true"/>
			</mm:field>	
		</mm:field>
	</mm:listnodes>
   Processing...<br/>
   Done.
  </html>
</mm:cloud>
