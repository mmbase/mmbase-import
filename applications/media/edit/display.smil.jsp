<%
    response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<mm:import externid="source"   required="true" />
<mm:import externid="fragment" required="true" />
<mm:node number="$source">
<smil>
  <head>
     <meta name="copyright" content="NOS Internet" />
     <layout>
        <root-layout width="260" height="200" background-color="#717171" />
        <region id="filmpje"   left="0" top="20"   z-index="2" width="260" height="200" background-color="#717171"  />
     </layout>
  </head>
  <body>    
     <par>
      <video      src="<mm:field name="urlresult(streams)" />" region="filmpje"  />
     </par>
  </body>
</smil>
</mm:node>
</mm:cloud>

