<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content>
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
          <img src="${mm:treelink('/education/wizards/gfx/ok.gif', includePath)}"
               onClick="window.top.reloadMode();"
               title="OK" alt="OK" />
        </mm:cloud>
      </body>
    </html>
  </mm:content>
</jsp:root>
