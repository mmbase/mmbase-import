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
        <root-layout width="400" height="400" background-color="black" />
        <region id="filmpje"   left="150" top="120" z-index="2" width="175" height="140" background-color="black"  />
        <region id="color1"  left="0"   top="0"   z-index="1" width="200" height="200" background-color="green"  />
        <region id="color2"  left="200" top="200" z-index="0" width="200" height="200" background-color="red"    />
        <region id="filmtext"  left="20"  top="310" z-index="3" width="360" height="90"  background-color="blue"   />
     </layout>
  </head>
  <body>    
     <par>
      <video src="<mm:field name="urlresult()" />" region="filmpje"  />
      <textstream src="<mm:url referids="fragment" page="test.rt.jsp" />"  region="filmtext" />
     </par>
  </body>
</smil>
</mm:node>
</mm:cloud>

