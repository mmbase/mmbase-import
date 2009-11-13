<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" 
%><mm:content language="$config.lang" type="text/html" expires="0">

<mm:cloud method="http"  rank="basic user">
<html>
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet"><!-- help IE --></link>
  <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>
<body  onload="parent.frames['left'].location.replace('<mm:url page="poolselector.jsp" />');" class="left">   


  <mm:import externid="type">any</mm:import>
  <mm:import externid="origin" />
  <mm:import externid="superorigin" />
  <mm:import externid="onlyquick" />

  <h1>Zoeken op Stream server<mm:compare value="yes" referid="onlyquick"> (Alleen quick-knip)</mm:compare><mm:write referid="type"><mm:compare valueset="video,audio">, <mm:write /></mm:compare></mm:write><mm:node number="$origin" notfound="skip">, voor categorie <mm:field name="name" /></mm:node></h1>

  <mm:import externid="dirs_name"><mm:node number="$superorigin" notfound="skip">/<mm:field name="name" />/</mm:node></mm:import>



  <mm:import externid="showdir" />
  <mm:import externid="diroffset">0</mm:import>
  <mm:import externid="pagelength">40</mm:import>
  <mm:import externid="nofpages">21</mm:import>

  <mm:import id="dirsfields">name</mm:import>


  <mm:notpresent referid="showdir"><%-- not showing dir --%>
  <mm:listnodescontainer type="dirs">
    <mm:sortorder field="name" />
    
    <mm:url id="baseurl"  referids="origin,superorigin,type,onlyquick,dirs_name" write="false">
      <mm:fieldlist id="dirs" nodetype="dirs" fields="$dirsfields">
        <mm:fieldinfo type="usesearchinput" />
        <mm:fieldinfo type="reusesearchinput" />
      </mm:fieldlist>
    </mm:url>
    
    <mm:size id="size" write="false" />
    <mm:write value="$[+$size/$pagelength + 1]" vartype="integer" id="lastpage" write="false" />
    
    <mm:maxnumber value="$pagelength" />
    <mm:offset    value="$diroffset" />
    


  
    <table style="width: 100%">  
        <tr><th colspan="100"><mm:write referid="size"><mm:write /> <mm:compare value="1">directory</mm:compare><mm:compare value="1" inverse="true">directories</mm:compare></mm:write> (<mm:write referid="diroffset" /> -  <mm:size id="actualsize" write="false" /> <mm:write value="$[+ $diroffset + $actualsize]" vartype="integer" />) </th></tr>
      <mm:import id="paging">
        <tr>
          <td colspan="100">
            <mm:previousbatches indexoffset="1"  maxtotal="$nofpages">
              <mm:first><mm:index><mm:compare value="1" inverse="true">...</mm:compare></mm:index></mm:first>                    
              <a href="<mm:url referid="baseurl" referids="_@diroffset" />"><mm:index /></a> ,
            </mm:previousbatches>
            <mm:index offset="1"/>
            <mm:nextbatches indexoffset="1" maxtotal="$nofpages">
              , <a href="<mm:url referid="baseurl" referids="_@diroffset" />"><mm:index /></a>          
              <mm:last><mm:index><mm:compare referid2="lastpage" inverse="true">...</mm:compare></mm:index></mm:last>
            </mm:nextbatches>
          </td>
        </tr>
      </mm:import>
            
      <mm:write referid="paging" escape="none" />
    
      <tr>
        <form method="post" action="<mm:url referids="type,origin,superorigin,onlyquick" />">
          <mm:fieldlist id="dirs" referid="dirs">
            <td><mm:fieldinfo type="searchinput" /></td>
          </mm:fieldlist>
          <td colspan="100"><input type="submit" value="Zoek" /></td>
        </form>
      </tr>
      
      <mm:listnodes id="dir">
        <tr><td><a href="<mm:url referid="baseurl" referids="dir@showdir,diroffset" />"><mm:field name="name" /></a></td></tr>
      </mm:listnodes>
      
      <mm:write referid="paging" escape="none" />
    </table>
  </mm:listnodescontainer>
  </mm:notpresent><%-- not showing dir --%>
  
  <%-- ================================================================================ --%>
  <mm:present referid="showdir">

    <mm:import externid="offset">0</mm:import>
    <mm:node number="$showdir">
      <mm:field id="url" name="name" write="false" />
    </mm:node>
    

    <mm:listnodescontainer type="mediasources">

      <mm:write referid="type">
        <mm:compare value="video">          
          <mm:constraint field="otype" value="audiosources" inverse="true"  />
        </mm:compare>
        <mm:compare value="audio">          
          <mm:constraint field="otype" value="videosources" inverse="true"  />
        </mm:compare>
      </mm:write>
      
      <mm:sortorder field="filelastmodified" direction="down" />
      <mm:constraint field="url" operator="LIKE" value="$url%" />
      <mm:constraint field="filelastmodified" value="0" operator=">" />


      <mm:url id="dirsbaseurl" referids="origin,superorigin,diroffset,type,onlyquick,dirs_name" write="false">
        <mm:fieldlist id="dirs" nodetype="dirs" fields="$dirsfields">
          <mm:fieldinfo type="reusesearchinput" />
        </mm:fieldlist>
      </mm:url>
      <mm:url id="baseurl" referid="dirsbaseurl" referids="showdir" write="false">
        <mm:fieldlist id="media" nodetype="mediasources" fields="url,filelastmodified">
          <mm:fieldinfo options="date" type="usesearchinput" />
          <mm:fieldinfo type="reusesearchinput" />
        </mm:fieldlist>
      </mm:url>

      <mm:size id="size" write="false" />

      <mm:maxnumber value="$pagelength" />
      <mm:offset    value="$offset" />
      <mm:write value="$[+$size/$pagelength + 1]" vartype="integer" id="lastpage" write="false" />



      <mm:import id="referrer"><%=new java.io.File(request.getServletPath()).getParent()%>/<mm:url referid="baseurl" escapeamps="false" /></mm:import>




      <mm:url id="preediturl" referids="origin,superorigin,diroffset,showdir,type,referrer" write="false" page="preedit.jsp">
        <mm:fieldlist id="dir" referid="dirs">
          <mm:fieldinfo type="reusesearchinput" />
        </mm:fieldlist>
        <mm:fieldlist id="media" referid="media">
          <mm:fieldinfo type="reusesearchinput" />
        </mm:fieldlist>
      </mm:url>
        

      <table style="width: 100%">  
        <tr><th colspan="100"><mm:write referid="size"><mm:write /> <mm:compare value="1">resultaat</mm:compare><mm:compare value="1" inverse="true">resultaten</mm:compare></mm:write> (<mm:write referid="offset" /> -  <mm:size id="actualsize" write="false" /> <mm:write value="$[+ $offset + $actualsize]" vartype="integer" />) </th></tr>
<mm:import id="paging">
        <tr>
          <td colspan="100">
            <p>
              <mm:previousbatches indexoffset="1"  maxtotal="$nofpages">
                <mm:first><mm:index><mm:compare value="1" inverse="true">...</mm:compare></mm:index></mm:first>                    
                <a href="<mm:url referid="baseurl" referids="_@offset" />"><mm:index /></a> ,
              </mm:previousbatches>
              <mm:isgreaterthan referid="lastpage" value="1"><mm:index offset="1" /></mm:isgreaterthan>
              <mm:nextbatches indexoffset="1" maxtotal="$nofpages">
                , <a href="<mm:url referid="baseurl" referids="_@offset" />"><mm:index /></a>                
                <mm:last><mm:index><mm:compare referid2="lastpage" inverse="true">...</mm:compare> </mm:index></mm:last>
              </mm:nextbatches>
            </p>
            <p>
              <a href="<mm:url referid="dirsbaseurl" />">terug</a>
            </p>
          </td>
        </tr>
        </mm:import>
        <mm:write referid="paging" escape="none" />
        <tr>
          <th>Zoek:</th>
          <form method="post" action="<mm:url referid="dirsbaseurl" referids="showdir" />">
          <mm:fieldlist id="media" referid="media">
            <td><nobr><mm:fieldinfo options="date" type="searchinput" /></nobr></td>
          </mm:fieldlist>
          <td colspan="4"><input type="submit" value="Zoek" /></td>
          </form>
        </tr>
        <tr><th>type</th><th>Link</th><th>Datum</th><th colspan="4">Edit links</th></tr>
        <mm:listnodes id="node">
          <tr>
            <mm:nodeinfo type="type">
              <td><img src="<mm:url page="media/${_}.gif" />" title="<mm:write /> <mm:field name="number" />" /></td>
            </mm:nodeinfo>
            <td><a title="open deze stream <mm:function name="format" />  <mm:function name="mimetype" /> " " target="_new" href="<mm:function name="url" />"><mm:field name="url" /></a></td>
            <td><mm:field name="filelastmodified"><mm:time format=":FULL.SHORT" /></mm:field></td>
            
            <mm:countrelations>
              <mm:write referid="type">
                <mm:compare referid="onlyquick" value="yes" inverse="true">
                <mm:compare value="video" inverse="true">
                  <td>
                    <a title="edit als audio" class="groupclick" href="<mm:url referids="node" referid="preediturl"><mm:param name="type" value="audio" /></mm:url>"><img src="<mm:url page="media/audiosources.gif" />" /></a>
                  </td>
                </mm:compare>
                <mm:compare value="audio" inverse="true">
                  <td>
                    <a title="edit als video" class="groupclick" href="<mm:url referids="node" referid="preediturl"><mm:param name="type" value="video" /></mm:url>"><img src="<mm:url page="media/videosources.gif" />" /></a>
                  </td>
                </mm:compare>
                </mm:compare>
                <mm:compare value="video" inverse="true">
                  <td>
                    <a title="quick edit als audio" class="groupclick" href="<mm:url referids="node" referid="preediturl"><mm:param name="type" value="audio" /><mm:param name="quick" value="quick" /></mm:url>"><img src="<mm:url page="media/audiosources.gif" />" /><img src="<mm:url page="media/quick.gif" />" /></a>
                  </td>
                </mm:compare>
                <mm:compare value="audio" inverse="true">
                  <td>
                    <a title="quick edit als video" class="groupclick"  href="<mm:url referids="node" referid="preediturl"><mm:param name="type" value="video" /><mm:param name="quick" value="quick" /></mm:url>"><img src="<mm:url page="media/videosources.gif" />" /><img src="<mm:url page="media/quick.gif" />" /></a>
                  </td>
                </mm:compare>
              </mm:write>
            </mm:countrelations>           
          </tr>
        </mm:listnodes>
        
        <mm:write referid="paging" escape="none" />
        
      </table>
    </mm:listnodescontainer>
  </mm:present><%-- show dir --%>
    
</body>
</html>
</mm:cloud>
</mm:content>