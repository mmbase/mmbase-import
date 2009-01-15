<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none">
    <mm:cloud>
      <mm:import externid="mode">educations</mm:import>
      <div id="mode-${mode}">
        <di:include debug="html" page="/education/wizards/modes/${mode}.jsp" />
      </div>
    </mm:cloud>
  </mm:content>
</jsp:root>
