<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*,java.util.ArrayList" %>

<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="action"></mm:import>
<mm:import id="deletedobjects">goals, problems, made tests, given answers, information about learnblocks finished by the students and email messages</mm:import>

<html>
<head>
  <title>Verwijder assessment resultaten</title>
</head>
<body>
<mm:compare referid="action" value="delete">
  <mm:listnodes type="madetests">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <mm:listnodes type="givenanswers">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <mm:list path="people,classrel,learnblocks">
    <mm:deletenode element="classrel" deleterelations="true" />
  </mm:list>
  <mm:listnodes type="emails">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <mm:listnodes type="problems">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <mm:listnodes type="goals">
    <mm:deletenode deleterelations="true" />
  </mm:listnodes>
  <h3><mm:write referid="deletedobjects" /> are deleted</h3>
</mm:compare>

<a href="?action=delete">Click here</a> to delete all <mm:write referid="deletedobjects" />.
</body>
</html>
</mm:cloud>

