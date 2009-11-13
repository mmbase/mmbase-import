<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" 
%><mm:content language="$config.lang" type="text/html" expires="0">
<mm:import externid="origin">media.myfragments</mm:import>
<mm:import externid="superorigin">media.myfragments</mm:import>


<mm:import externid="referrer"><%=new java.io.File(request.getServletPath())%></mm:import>


<mm:import id="jsps"><mm:write referid="config.editwizards" />jsp/</mm:import>
<mm:cloud jspvar="cloud" method="asis" rank="basic user">

<html>
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet"><!-- help IE --></link>
  <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>
<body>
  
  <mm:import externid="showdir" />
  <mm:import externid="diroffset">0</mm:import>
  <mm:import externid="pagelength">40</mm:import>
  <mm:import externid="nofpages">21</mm:import>
  <mm:import id="dirsfields">name</mm:import>

  <mm:import externid="node" required="true" />
  <mm:import externid="type" required="true" />
  

  <%-- determin wether node must be copied to new node, or is already of the requested type --%>
  <mm:node id="orignode" number="$node">
    <mm:nodeinfo type="type">
      <mm:compare value="${type}sources" inverse="true"> <%-- copy the node to the right type --%>
        <mm:createnode id="source" type="${type}sources">
          <mm:setfield name="format"><mm:field node="orignode" name="format" escape="none" /></mm:setfield>
          <mm:setfield name="codec"><mm:field node="orignode" name="codec" escape="none" /></mm:setfield>
          <mm:setfield name="bitrate"><mm:field node="orignode" name="bitrate" escape="none" /></mm:setfield>
          <mm:setfield name="url"><mm:field node="orignode" name="url" escape="none" /></mm:setfield>
          <mm:setfield name="state"><mm:field node="orignode" name="state" escape="none" /></mm:setfield>
          <mm:setfield name="filesize"><mm:field node="orignode" name="filesize" escape="none" /></mm:setfield>
          <mm:setfield name="filelastmodified"><mm:field node="orignode" name="filelastmodified" escape="none" /></mm:setfield>
        </mm:createnode>
        <mm:maydelete>
          <mm:relatednodescontainer path="mediafragments,pools">
            <mm:size>
              <mm:compare value="0">
                <mm:deletenode deleterelations="true" />
              </mm:compare>
            </mm:size>
          </mm:relatednodescontainer>
        </mm:maydelete>
      </mm:compare>
      <mm:compare value="${type}sources"> <%-- already of right type --%>
        <mm:node id="source" />
      </mm:compare>
    </mm:nodeinfo>
  </mm:node>
  
  <mm:node referid="source">
    <mm:relatednodes type="${type}fragments" max="1" searchdir="destination">
      <mm:node id="basefragment" />
    </mm:relatednodes>
  </mm:node>

  <%-- is it needed to make relation to provider? That should happen somewhere here then. --%>

  <%-- create the 'base' fragment if necessary --%>
  <mm:notpresent referid="basefragment">
    <mm:createnode id="basefragment" type="${type}fragments">
      <mm:setfield name="title">Basis fragment <mm:node referid="source"><mm:function name="format" /></mm:node></mm:setfield> 
    </mm:createnode>
    <mm:createrelation source="basefragment" destination="source" role="related">
    </mm:createrelation>
  </mm:notpresent>
    

  <%-- now, create the new fragment we are going to make --%>
  <mm:createnode id="fragment" type="${type}fragments">
    <mm:setfield name="title"><mm:node referid="origin" notfound="skip"><mm:field name="name" /></mm:node> fragment.</mm:setfield>
  </mm:createnode>
 

  <mm:createrelation source="basefragment" destination="fragment" role="posrel">
    <mm:setfield name="pos">1</mm:setfield>
  </mm:createrelation>

  <mm:import externid="quick" /> <!-- create also one subfragment, and go to an editor which edits only that. -->

  <mm:url  id="url" referids="referrer,origin,config.lang@language" page="${jsps}wizard.jsp">
    <mm:param name="objectnumber"><mm:node referid="fragment"><mm:field name="number" /></mm:node></mm:param>
  </mm:url>

  <mm:present referid="quick">
    <mm:createnode id="subfragment" type="${type}fragments">
      <mm:setfield name="title">quick subfragment</mm:setfield>
      <mm:setfield name="start">-1</mm:setfield>
      <mm:setfield name="stop">-1</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="fragment" destination="subfragment" role="posrel">
      <mm:setfield name="pos">1</mm:setfield>
    </mm:createrelation>
    <mm:node id="quickorigin" number="quick-pool" />
    <mm:createrelation source="quickorigin" destination="fragment" role="related" />
    <mm:redirect referid="url">
      <mm:param name="wizard" value="tasks/quick/${type}fragments" />
    </mm:redirect>
  </mm:present>
  <mm:notpresent referid="quick">
    <!-- also link it to origin -->
    <mm:node id="origin" referid="origin" notfound="skip">
      <mm:createrelation source="origin" destination="fragment" role="related" />
    </mm:node>
    
    <mm:redirect referid="url">
      <mm:param name="wizard" value="tasks/clipping/${type}fragments" />
    </mm:redirect>
  </mm:notpresent>
  

    
</body>
</html>
</mm:cloud>
</mm:content>