<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none">
    <mm:cloud>
      <mm:import externid="mode">educations</mm:import>
      <mm:treefile
          write="false"
          objectlist="${includePath}"
          page="/education/wizards/modes/${mode}.jsp"
          referids="education_topmenu_course?">
        <mm:param name="expires">0</mm:param>
        <script type="text/javascript">
          <jsp:directive.include file="reloadMode.js" />
        </script>
      </mm:treefile>
    </mm:cloud>
  </mm:content>
</jsp:root>
