<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none">
    <mm:cloud>
      <mm:import externid="mode">components</mm:import>
      <mm:link page="/education/js/tree.jsp" referids="mode">
        <script type="text/javascript" src="${_}"> <!-- help IE --></script>
      </mm:link>
      <mm:remove from="session" referid="path" />
      <mm:import externid="education_topmenu_course" />
      <mm:treefile
          write="false"
          objectlist="${includePath}"
          page="/education/wizards/modes/${mode}.jsp"
          referids="education_topmenu_course?">
        <mm:param name="expires">0</mm:param>
        <script type="text/javascript">
          function reloadMode() {
          var xmlhttp =  new XMLHttpRequest();
          xmlhttp.open('GET', '${_}', true);
          xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4) {
          var ser = new XMLSerializer();
          var s = ser.serializeToString(xmlhttp.responseXML);
          document.getElementById('mode-${mode}').innerHTML = s;
          restoreTree();
          storeTree();
          }
          }
          xmlhttp.send(null);
          }
        </script>
      </mm:treefile>

      <div id="mode-${mode}">
        <di:include debug="html" page="/education/wizards/modes/${mode}.jsp" />
      </div>

    </mm:cloud>
  </mm:content>
</jsp:root>
