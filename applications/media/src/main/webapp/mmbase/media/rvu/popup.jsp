<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="../readconfig.jsp" 
%><mm:import externid="fragment" required="true"  
/><mm:cloud><mm:node  number="$fragment"><a href="#" onClick="javascript:window.open('http://<%=getHost() + getTemplatesDir() + "rvu/popuppage.jsp?fragment="%><mm:field name="number" />','<mm:field name="title" />', 'width=350,height=280,scrollbars=no');"><mm:field name="title" /></a> <mm:field name="html(intro)" />
    <mm:nodeinfo id="actualtype" type="type" write="false" />
    <mm:related  path="posrel,${actualtype}2" fields="posrel.pos"     orderby="posrel.pos" searchdir="destination">
    <mm:first><ul></mm:first>
       <mm:context>
        <mm:node id="fragment" element="${actualtype}2">
           <li><mm:field name="title" /></li>
        </mm:node>
      </mm:context>
    <mm:last><ul></mm:last>
     </mm:related>
</mm:node>
</mm:cloud>