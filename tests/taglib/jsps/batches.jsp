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
  <mm:previousbatches> 
    <mm:first>first page: <mm:index /> ----</mm:first> 
  </mm:previousbatches>

  <mm:previousbatches max="5"> 
    <mm:index /><mm:last inverse="true">, </mm:last>
  </mm:previousbatches>
  <font color="green"><mm:write value="${+$offset / $max}" vartype="integer" /></font><!-- current page -->
  <mm:nextbatches max="5">
    <mm:index /><mm:last inverse="true">, </mm:last>
  </mm:nextbatches>

  <mm:nextbatches> 
    <mm:last>---last page: <mm:index /></mm:last> 
  </mm:nextbatches>

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
  
  <p>
    Problem: mm:index of batches tags have offset 0. This is logical because the actual offset for
    the query can be calculated very easily then. But people like the first page to be 1, not 0.
    - 'indexoffset' attribute on batches tag? - offset attribute on index-tag should work differently?
  </p>
  
</mm:listnodescontainer>

</mm:cloud>
</body>
</html>