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
	Changing templates.url from templates/*.jsp to *.jsp<br/>
	Setting the templates to visible in the pagina form<br/>
	<mm:listnodes type="paginatemplate">
		<mm:field name="url" jspvar="url" vartype="String" write="false">
			<mm:setfield name="url"><%= url.substring(10) %></mm:setfield>
		</mm:field>
		<mm:setfield name="systemtemplate">0</mm:setfield>   
		<mm:setfield name="dynamiclinklijsten">0</mm:setfield>    
		<mm:setfield name="dynamicmenu">0</mm:setfield>    
		<mm:setfield name="contenttemplate">0</mm:setfield>   
	</mm:listnodes>
  Making pages deletable<br/>
  <mm:listnodes type="pagina">
  	<mm:setfield name="verwijderbaar">1</mm:setfield>    
	</mm:listnodes>
  Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
