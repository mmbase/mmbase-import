<%@page session="true" language="java" contentType="text/html; charset=UTF-8"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:cloud  method="asis">
<mm:content postprocessor="none">
  <mm:import externid="formId"></mm:import>
  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <h2><di:translate key="register.registration" /></h2>
      <mm:log>Bla ${formId} </mm:log>
      <mm:treeinclude page="/register/form${formId}.jspx" objectlist="$includePath" />
      <p>
        <di:translate key="register.extra" />
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
</mm:content>
</mm:cloud>

