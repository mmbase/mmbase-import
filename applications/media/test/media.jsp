<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
 </head>
  <body>
    <h1></h1>
<mm:cloud>
 <mm:listnodes id="fragment" type="mediafragments">
  <mm:field name="title" />:<br />
  <mm:related path="posrel,mediasources" fields="posrel.pos" orderby="posrel.pos"><mm:context>
     <mm:node id="source" element="mediasources">
     source: <a href="<mm:field  name="absoluteurl(streams)"  />"><mm:field name="str(format)" /></a><br />
     <mm:field name="format" vartype="integer">
        <mm:compare value="12">
              <a href="<mm:url referids="fragment,source" page="test.ram.jsp" />">smil</a><br />
         </mm:compare>
     </mm:field>
     </mm:node>
  </mm:context></mm:related>
</mm:listnodes>

</mm:cloud>
    <hr />
  </body>
</html>
