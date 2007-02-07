<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
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
   Create keywords from the values of images.metatags and use the keywords instead of the metatag field.<br/>
   <mm:listnodes type="images" id="image">
    <mm:field name="metatags" jspvar="mt" vartype="String" write="false">
      <mm:isnotempty>
        <mm:remove referid="kw" />
        <mm:listnodes type="keywords" constraints="<%= "word = '" + mt + "'" %>">
          <mm:node id="kw" />
        </mm:listnodes>
        <mm:notpresent referid="kw">
          <mm:createnode type="keywords" id="kw">
            <mm:setfield name="word"><%= mt %></mm:setfield>
          </mm:createnode>
        </mm:notpresent>
        <mm:createrelation source="image" destination="kw" role="related" />
        <mm:setfield name="metatags"> </mm:setfield>
      </mm:isnotempty>
    </mm:field>
   </mm:listnodes>
   <mm:listnodes type="thema">
    <mm:deletenode deleterelations="true" />
   </mm:listnodes>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
