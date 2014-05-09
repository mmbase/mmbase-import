<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="readconfig.jsp" 
%><mm:import externid="fragment" required="true"  />
<mm:import externid="format" >rm</mm:import>
<mm:cloud>
<mm:node  number="$fragment">
  <a href="<mm:field name="url($format)" />"><mm:field name="title"  /></a> 
  <mm:nodeinfo id="actualtype" type="type" write="false" />
   <mm:context>

    <ul>
    <mm:related  path="posrel,${actualtype}2" fields="posrel.pos" orderby="posrel.pos" searchdir="destination">
       <mm:context>
        <mm:node id="fragment" element="${actualtype}2">
           <li>
               <a href="<mm:field name="url($format)" />"><mm:field name="title" /></a>
           </li>
        </mm:node>
      </mm:context>
     </mm:related>
    </ul>
   </mm:context>
</mm:node>
</mm:cloud>