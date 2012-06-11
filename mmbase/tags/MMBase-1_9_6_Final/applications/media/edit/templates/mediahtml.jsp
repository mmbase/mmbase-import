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
<mm:write referid="type">
   <mm:compare value="link">
    <a href="<mm:field name="url($format)" />"><mm:field name="title" /></a>
   </mm:compare>

   <mm:compare value="link_intro">
    <a href="<mm:field name="url($format)" />"><mm:field name="title" /></a> <mm:field name="intro" escape="p" />
   </mm:compare>

   <mm:compare value="link_subfragments">
    <a href="<mm:field name="url($format)" />"><mm:field name="title" /></a> 
    <ul>
    <mm:related  path="posrel,mediafragments" fields="posrel.pos" orderby="posrel.pos">
       <mm:context>
        <mm:node id="fragment" element="mediafragments2">
           <li><mm:field name="title" /> <a href="<mm:url referids="fragment" page="showurls.jsp" />">URL's</a></li>
        </mm:node>
      </mm:context>
     </mm:related>
    </ul>
   </mm:compare>

</mm:write>
</mm:node>
</mm:cloud>
</mm:content>