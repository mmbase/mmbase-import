<%
    response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<mm:import externid="source" required="true" />
<mm:import externid="fragment" required="true" />
<mm:node number="$source">
<smil>
  <head>
     <meta name="copyright" content="NOS Internet" />
     <layout>
        <root-layout width="200" height="165" background-color="black" />
        <region id="filmpje"   left="20" top="0" z-index="2" width="175" height="140" background-color="black"  />
        <region id="filmtext"  left="5"  top="141" z-index="3" width="190" height="20"  background-color="blue"   />
     </layout>
  </head>
  <body>    
     <par>
      <video src="<mm:field name="urlresult()" />" region="filmpje"  />
      <textstream src="<mm:url referids="fragment" page="display.rt.jsp" />"  region="filmtext" />
     </par>
  </body>
</smil>
</mm:node>
</mm:cloud>

