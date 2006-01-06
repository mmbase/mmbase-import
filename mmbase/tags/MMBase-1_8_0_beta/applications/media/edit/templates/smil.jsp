<%@page session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="application/smil" encoding="" expires="0">
<%@ include file="readconfig.jsp"
%><mm:import externid="fragment" required="true"  />
<mm:import externid="format" >rm</mm:import>
<mm:cloud method="asis">
<smil>
<head>
  <meta name="copyright" content="Publieke Omroep Internet Services" />
  <layout>
    <root-layout width="400" height="400" background-color="black" />
    <region id="filmpje"   left="150" top="120" z-index="2" width="175" height="140" background-color="black"  />
    <region id="plaatje1"  left="0"   top="0"   z-index="1" width="200" height="200" background-color="green"  />
    <region id="plaatje2"  left="200" top="200" z-index="0" width="200" height="200" background-color="red"    />
    <region id="filmtext"  left="20"  top="310" z-index="4" width="360" height="90"  background-color="blue"   />
    <region id="imagetext" left="150" top="0"   z-index="4" width="200" height="60"  background-color="yellow" /> 
  </layout>
  <mm:import id="angles" vartype="list">0,90,180,270</mm:import>
</head>
<body>
  <par>
    <mm:listnodes id="image" type="images" orderby="number" directions="DOWN" offset="0" max="10" >
      <mm:first><seq></mm:first>
      <%-- beetje opleuken met plaatjes --%>
      <mm:stringlist id="turn" referid="angles">
        <par> <!-- <mm:field name="number" />.<mm:write /> -->
        <img src='<mm:image template="s(100x100)+r($turn)" />' alt="hoi" region="plaatje1" dur="3s" top="4" />   
        <textstream dur="3s" src='<mm:url referids="image,turn" page="imagedescription.rt.jsp" />' region="imagetext" />
      </par>
    </mm:stringlist>
    <mm:last>
      <par>
        <textstream fill="freeze" src='<mm:url referids="image" page="imagedescription.rt.jsp"/>' region="imagetext" />
        <img fill="freeze" src='<mm:image template="s(100x100)" />' alt="hoi" region="plaatje1" />
      </par>
      </seq></mm:last> 
    </mm:listnodes>
    <!-- the actual interesting things happen here -->
    <mm:context>
      <mm:node id="fragment"  number="$fragment">
        <mm:nodeinfo id="actualtype" type="type" write="false" />
        <seq id="fragment">
          <mm:context>
             <mm:relatednodes type="$actualtype" directions="destination" role="previous">
               <par>
                 <video src="<mm:field name="nudeurl(rm)" />" region="filmpje" />
                 <textstream  src='<mm:url referids="fragment" page="fragmentdescription.rt.jsp" />' region="filmtext" />
               </par>
             </mm:relatednodes>
           </mm:context>
           <par>
             <video src="<mm:field name="nudeurl(rm)" />" region="filmpje" />
             <textstream src='<mm:url referids="fragment" page="fragmentdescription.rt.jsp" />' region="filmtext" />
           </par>
           <mm:context>
             <mm:relatednodes type="$actualtype" directions="source" role="previous">
               <par>
                 <video src="<mm:field name="nudeurl(rm)" />" region="filmpje" />
                 <textstream src='<mm:url referids="fragment" page="fragmentdescription.rt.jsp" />' region="filmtext" />
               </par>
             </mm:relatednodes>
           </mm:context>           
         </seq>
       </mm:node>
       </mm:context>
     </par>
  </body>
</smil>
</mm:cloud>
</mm:content>