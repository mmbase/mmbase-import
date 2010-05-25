<%
  response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="fragment" /><mm:cloud><mm:node number="$fragment"><smil>
<head>
 <meta name="title"     content="<mm:field name="title" />" />
 <meta name="copyright" content=""/>
 <meta name="author"    content=""/>
 <meta name="abstract"  content="<mm:field name="intro" />" />
<layout>
 <root-layout width="350" height="160" background-color="#a06534" />
 <region id="logo"        left="115" top="10" width="120" height="34" background-color="#a06534"/>
 <region id="peer"        left="0"   top="0"  width="100" height="100" background-color="#a06534"/>
 <region id="toon"        left="250" top="0"  width="100" height="100" background-color="#a06534"/>

 <region id="episodeinfo" left="0"   top="108" width="350" height="42" background-color="#a06534"/>
 <region id="strip"       left="0"   top="148" width="118" height="12" background-color="#a06534"/>
 <region id="link"        left="257" top="148" width="93" height="12" background-color="#a06534"/>
 <region id="counter"     left="0"  top="0"   width="1"   height="1"  background-color="#a06534" z-index="-1"/>
</layout>
</head>
<body>
<seq>
 <switch>
 <img src="images/mmbase.png" region="counter" dur="0.5s" system-bitrate="128000"/>
 </switch>
 <par>
  <switch>
   <audio src="rtsp://streams.vpro.nl/pac01/9397265/surestream.rm?title=Bergeijk wk 47 wo 20 november 2002&author=" title="Bergeijk wk 47 wo 20 november 2002" system-bitrate="128000"/>
   <audio src="rtsp://streams.vpro.nl/pac01/9397265/surestream.rm?title=Bergeijk wk 47 wo 20 november 2002&author=" title="Bergeijk wk 47 wo 20 november 2002" system-bitrate="64000"/>
   <audio src="rtsp://streams.vpro.nl/pac01/9397265/surestream.rm?title=Bergeijk wk 47 wo 20 november 2002&author=" title="Bergeijk wk 47 wo 20 november 2002" system-bitrate="56000"/>
   <audio src="rtsp://streams.vpro.nl/pac01/9397265/surestream.rm?title=Bergeijk wk 47 wo 20 november 2002&author=" title="Bergeijk wk 47 wo 20 november 2002"/>

  </switch>
  <a href="http://www.omroep.nl" show="new">
   <img src="images/logo_po_ver.gif" region="po" fill="freeze" dur="0.5s"/>
  </a>
 </par>
</seq>

</body>
</smil>
</mm:node>
</mm:cloud>