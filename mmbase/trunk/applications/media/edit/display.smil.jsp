<%
    response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<mm:import externid="source"   required="true" />
<mm:node id="s" number="$source">
<!-- this is completey stupid, but it works for the moment -->
<mm:relatednodes type="mediaproviders" max="1">
<smil>
  <head>
     <meta name="copyright" content="NOS Internet" />
     <layout>
        <root-layout width="260" height="200" background-color="#717171" />
        <region id="filmpje"   left="0" top="10"   z-index="2" width="260" height="200" background-color="#717171"  />
     </layout>
  </head>
  <body>    
     <par>
      <mm:field name="name">
         <video  screen-height="200" screen-width="260" src="<mm:field node="s" name="urlresult($_)" />" region="filmpje"  />
      </mm:field>
     </par>
  </body>
</smil>
</mm:relatednodes>
</mm:node>
</mm:cloud>

