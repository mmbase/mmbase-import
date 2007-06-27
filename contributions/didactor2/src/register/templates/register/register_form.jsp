<%@page session="true" language="java" contentType="text/html; charset=UTF-8"  buffer="500kb"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:cloud method="delegate" authenticate="asis" id="cloud">
<jsp:directive.include file="/shared/setImports.jsp" />
<mm:content postprocessor="none">  

  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <h2><di:translate key="register.registration" /></h2>

      <mm:treeinclude page="/register/form.jspx" objectlist="$includePath" />
      <p>
        <di:translate key="register.extra" />
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
</mm:content>
</mm:cloud>

