<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<head>
<title>Testing MMBase/taglib</title>
</head>
<body>
<mm:cloud>
<h1>Testing MMBase/taglib</h1>

<h3>previousbatches/nextbatches</h3>
<mm:import id="offset">50</mm:import>
<mm:import id="max">5</mm:import>
<mm:import externid="indexoffset">0</mm:import>
<mm:import id="list" vartype="list">a,b,c,d,e,f</mm:import>
<mm:listnodescontainer type="object">
  <p>
    There are <mm:size /> objects in the cloud.
  </p>
  <p>
    paging with mm:index:
  </p>
  <mm:offset    value="$offset" />
  <mm:maxnumber value="$max" />
  <mm:previousbatches indexoffset="$indexoffset">
    <mm:first>first page: <mm:index /> ----</mm:first>
  </mm:previousbatches>

  <mm:previousbatches max="5"  indexoffset="$indexoffset">
    <mm:first> ---</mm:first><mm:index /><mm:last inverse="true">, </mm:last>
  </mm:previousbatches>

  <font color="green"><mm:write value="$[+$offset / $max + $indexoffset]" vartype="integer" /></font><!-- current page -->
  <mm:nextbatches max="5" indexoffset="$indexoffset">
    <mm:index /><mm:last inverse="true">, </mm:last>
  </mm:nextbatches>

  <mm:nextbatches indexoffset="$indexoffset">
    <mm:last>---last page: <mm:index /></mm:last>
  </mm:nextbatches>

  <p>
    <a href="<mm:url><mm:param name="indexoffset"><mm:write id="newoffset" vartype="integer" value="$[+1 - $indexoffset]" /></mm:param></mm:url>">With offset <mm:write referid="newoffset" /></a>
</p>
  <hr />
  <p>
    paging with mm:write:
  </p>

  <mm:previousbatches max="5">
    <mm:write /><mm:last inverse="true">, </mm:last>
  </mm:previousbatches>
  <font color="green"><mm:write value="$offset" /></font><!-- current page -->
  <mm:nextbatches max="5">
    <mm:write /><mm:last inverse="true">, </mm:last>
  </mm:nextbatches>

  <hr />
  <p>
    General demo of mm:stringlist
  </p>
  <mm:stringlist referid="list">
    <mm:index />: <mm:write /><mm:last inverse="true">, </mm:last>
  </mm:stringlist>

  <hr />
  <a href=".">Back</a>


</mm:listnodescontainer>

</mm:cloud>
</body>
</html>
