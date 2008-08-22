<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none">
    <mm:cloud>
      <mm:import externid="mode">educations</mm:import>

      <mm:link page="/education/js/tree.jsp" referids="mode">
        <script type="text/javascript" src="${_}"> <!-- help IE --></script>
      </mm:link>
      <mm:remove from="session" referid="path" />
      <mm:import externid="education_topmenu_course" />

      <div id="mode-${mode}">
        <di:include debug="html" page="/education/wizards/modes/${mode}.jsp" />
      </div>

    </mm:cloud>
  </mm:content>
</jsp:root>
