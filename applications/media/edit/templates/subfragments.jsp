<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="../config/read.jsp" 
%><mm:import externid="fragment" required="true"  />
<mm:import externid="format" >rm</mm:import>
<mm:cloud>
<mm:node  number="$fragment">
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
</mm:node>
</mm:cloud>