<%--
  This servlet produces pieces of html, and is meant to use in an
  object or perhaps an mm:include or so.


--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="../config/read.jsp" 
%><mm:content type="text/html">

<mm:import externid="type">link</mm:import>
<mm:import externid="fragment" required="true"  />
<mm:import externid="format" >rm</mm:import>
<mm:cloud>
<mm:node  number="$fragment">
    <a href="<mm:field name="url($format)" />"><mm:field name="title" /></a> <mm:field name="intro" escape="p" />
</mm:node>
</mm:cloud>
</mm:content>