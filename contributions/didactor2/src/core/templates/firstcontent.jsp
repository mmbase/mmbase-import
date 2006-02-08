<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
  <%@include file="/shared/setImports.jsp" %>
  <html>
    <head>
      <title>First content</title>
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/loginpage.css" objectlist="$includePath" referids="$referids" />" />
    </head>
    <body>
      <h1><di:translate key="core.welcome" /></h1>
      <br />
      <h3><di:translate key="core.welcomemessage" /></h3>
      <br />
      <p>
        <di:translate key="core.mainText" />
      </p>
      <p>
        <mm:node number="component.portfolio" notfound="skipbody">
          <a href="<mm:treefile write="true" page="/portfolio/listall.jsp" objectlist="$includePath" />"><di:translate key="core.listallportfolios" /></a>
        </mm:node>
      </p>
    </body>
  </html>
</mm:cloud>
</mm:content>
