<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="learnobject" required="true"/>

<!-- TODO Need this page? -->

<%@include file="/shared/setImports.jsp" %>

<%-- remember this page --%>
<mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="learnobject"><mm:write referid="learnobject"/></mm:param>
    <mm:param name="learnobjecttype">learnblocks</mm:param>
</mm:treeinclude>



<html>
<head>
   <title>Learnblock content</title>
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
</head>
<body>

<div class="learnenvironment">

<mm:node number="$learnobject">

   <mm:field name="showtitle">
      <mm:compare value="1">
         <h1><mm:field name="name"/></h1>
      </mm:compare>
   </mm:field>

   
   <mm:import id="layout"><mm:field name="layout"/></mm:import>
   <mm:import id="imagelayout"><mm:field name="imagelayout"/></mm:import>
  <mm:import jspvar="text" reset="true"><mm:field name="intro" escape="none"/></mm:import>

  <table width="100%" border="0" class="Font">
  
  <mm:compare referid="layout" value="0">
  <tr><td width="50%"><%@include file="/shared/cleanText.jsp"%></td></tr>
  <tr><td><%@include file="../pages/images.jsp"%></td></tr>
  </mm:compare>
  <mm:compare referid="layout" value="1">
  <tr><td  width="50%"><%@include file="../pages/images.jsp"%></td></tr>
  <tr><td><%@include file="/shared/cleanText.jsp"%></td></tr>
  </mm:compare>
  <mm:compare referid="layout" value="2">
  <tr><td><%@include file="/shared/cleanText.jsp"%></td>
      <td><%@include file="../pages/images.jsp"%></td></tr>
  </mm:compare>
  <mm:compare referid="layout" value="3">
  <tr><td><%@include file="../pages/images.jsp"%></td>
      <td><%@include file="/shared/cleanText.jsp"%></td></tr>
  </mm:compare>
 
  </table>
 


   <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="node_id"><mm:write referid="learnobject"/></mm:param>
      <mm:param name="path_segment">../</mm:param>
   </mm:treeinclude>
</mm:node>

</div>


</body>
</html>
</mm:cloud>
</mm:content>
