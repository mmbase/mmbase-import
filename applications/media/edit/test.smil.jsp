<%
    response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><smil>
  <head>
     <meta name="copyright" content="NOS Internet" />
     <layout>
        <root-layout width="260" height="200" background-color="#717171" />
        <region id="filmpje"   left="0" top="20"   z-index="2" width="260" height="200" background-color="#717171"  />
     </layout>
  </head>
  <body>    
    <par>
      <!-- video src="rtsp://streams2.omroep.nl/tv/nos/journaal/sb.maandag.2000.rm" region="filmpje"  /--> <!-- play -->
      <video src="rtsp://streams2.omroep.nl/tv/nos/journaal/maandag.2000.rm" region="filmpje" /> <!-- streams -->
     </par>
  </body>
</smil>
