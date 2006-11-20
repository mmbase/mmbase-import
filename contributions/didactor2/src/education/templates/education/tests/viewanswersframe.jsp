<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate">
  <mm:import externid="testNo" required="true"/>
  <mm:import externid="madetestNo" required="true"/>
  <mm:import externid="userNo" required="true"/>

  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="/education/tests/definitions.jsp" />

  <html>
    <head>
      <title>View Answers</title>
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
    </head>
    <body>

      <mm:treeinclude page="/education/tests/totalscore.jsp"  objectlist="$includePath" referids="$referids,madetestNo@madetest,testNo@tests" />
      <mm:treeinclude page="/education/tests/feedback.jsp" objectlist="$includePath" referids="$referids,testNo@tests,madetestNo@madetest" />
      <hr/>
      <mm:treeinclude page="/education/tests/viewanswers.jsp" objectlist="$includePath" referids="$referids,testNo,madetestNo,userNo" />

    </body>
  </html>
</mm:cloud>
</mm:content>
