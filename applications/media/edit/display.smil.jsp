<%@ include file="server.jsp" 
%><%
    response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<mm:import externid="source" required="true" />
<smil>
  <head>
     <meta name="copyright" content="NOS Internet" />
     <layout>
        <root-layout width="400" height="400" background-color="black" />
        <region id="filmpje"   left="150" top="120" z-index="2" width="175" height="140" background-color="black"  />
     </layout>
  </head>
  <body>    
      <video src="rtsp://streams.omroep.nl/tv/vpro/sb.noorderlicht.rm" region="filmpje"  />
  </body>
</smil>
</mm:cloud>

