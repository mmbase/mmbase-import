<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content type="text/html">
    <mm:cloud authenticate="asis">
      <html>
        <head>
          <title><di:translate key="core.welcome" /></title>
          <link rel="stylesheet" type="text/css"
                href="${mm:treelink('/css/loginpage.css', includePath)}" />
        </head>
        <body>
          <h1><di:translate key="core.welcome" /></h1>
          <h3><di:translate key="core.welcomemessage" /></h3>
          <p>
            <di:translate key="core.mainText" arg0="2.3 β" />
          </p>
          <p>
            <mm:hasnode number="component.portfolio">
              <mm:treefile write="false" page="/portfolio/listall.jsp" objectlist="$includePath">
                <a href="${_}"><di:translate key="core.listallportfolios" /></a>
              </mm:treefile>
            </mm:hasnode>
          </p>
        </body>
      </html>
    </mm:cloud>
  </mm:content>
</jsp:root>
