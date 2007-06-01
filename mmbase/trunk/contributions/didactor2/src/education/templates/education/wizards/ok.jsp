<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content>
<html>
  <head>
    <title>OK</title>
  </head>
  <body>
    <mm:import externid="reload" />
    <mm:present referid="reload">
      <script type="text/javascript">
        window.top.reloadMode();
      </script>
    </mm:present>
    <mm:cloud method="asis">
      <jsp:directive.include file="/shared/setImports.jsp" />
      <img src="${mm:treefile('/education/wizards/gfx/ok.gif', pageContext, includePath)}"  
           onClick="window.top.reloadMode();"
           title="OK" alt="OK" />
    </mm:cloud>
  </body>
</html>
</mm:content>
