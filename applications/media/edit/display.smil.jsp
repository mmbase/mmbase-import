<%-- Used by player --%><%
    response.setHeader("Content-Type", "application/smil");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<mm:import externid="fragment"   required="true" />
<mm:node number="$fragment">
<smil xmlns="http://www.w3.org/2001/SMIL20/Language" xmlns:rn="http://features.real.com/2001/SMIL20/Extensions">
  <head>
     <meta name="copyright" content="NOS Internet" />
     <layout>
        <root-layout width="260" height="200" background-color="#717171" />
        <region id="video_region"  fit="fill" z-index="2" />
        <regPoint id="middle" regAlign="center" left="50%" top="50%"/>
     </layout>
  </head>
  <body>    
     <par>
      <mm:field name="name">
         <video  src="<mm:field name="url(rm)" />" region="video_region" regPoint="center" regAlign="center" />
      </mm:field>
     </par>
  </body>
</smil>
</mm:node>
</mm:cloud>

