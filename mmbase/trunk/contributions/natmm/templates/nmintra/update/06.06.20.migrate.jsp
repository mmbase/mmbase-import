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
	1. Moving attachments that are related directly to the artikel to the first paragraaf related to the artikel.
	If the article does not contain an paragraaf a paragraaf should be created. 
	In that case the title of the paragraaf should be the title of the attachment + showtitle field of paragraaf should be set to false.
	<mm:list path="attachments,posrel,artikel" fields="attachments.number" orderby="attachments.number" directions="UP">
		<mm:deletenode element="posrel" />
		<mm:node element="artikel" id="artikel">
			<mm:related path="artikel,posrel,paragraaf" orderby="posrel.pos" directions="UP" max="1">
				<mm:node element="paragraaf" id="paragraaf"/>
			</mm:related>
			<mm:notpresent referid="paragraaf">
				<mm:createnode type="paragraaf" id="paragraaf">
					<mm:setfield name="titel"><mm:field name="attachments.number"/></mm:setfield>
					<mm:setfield name="titel_zichtbaar">0</mm:setfield>
				</mm:createnode>
				<mm:createrelation role="posrel" source="artikel" destination="paragraaf"/>
			</mm:notpresent>
		</mm:node>
		<mm:node element="attachments" id="attachment"/>
		<mm:createrelation role="posrel" source="paragraaf" destination="attachment"/>
		<mm:remove referid="paragraaf"/>
		<mm:remove referid="artikel"/>
		<mm:remove referid="attachment"/>
	</mm:list>
	</body>
  </html>
</mm:log>
</mm:cloud>
