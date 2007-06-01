<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content postprocessor="reducespace">
<mm:cloud>
  <mm:import externid="mode">components</mm:import>
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="roles_defs.jsp" />
  <mm:link page="/education/js/tree.jsp" referids="mode">
    <script type="text/javascript" src="${_}"><!-- help IE --></script>
  </mm:link>
  <mm:remove from="session" referid="path" />
  <mm:import externid="education_topmenu_course" />
  <mm:link page="modes/${mode}.jsp" referids="education_topmenu_course">
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
  </mm:link> 
  
  <div id="mode-${mode}">
    <mm:include page="modes/${mode}.jsp" />
  </div>
  
  </mm:cloud>
</mm:content>
