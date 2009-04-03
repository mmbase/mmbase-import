<%@page session="true" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:content postprocessor="reducespace">
  <mm:cloud method="delegate" authenticate="asis">
    <jsp:directive.include file="/shared/setImports.jsp" />
    
    <div class="columns">
      <div class="columnLeft">
      </div>
      <div class="columnMiddle">
        <mm:treeinclude page="/register/thanks.jspx" objectlist="$includePath" referids="$referids" />
      </div>
      <div class="columnRight">
      </div>
    </div>
  </mm:cloud>
</mm:content>
