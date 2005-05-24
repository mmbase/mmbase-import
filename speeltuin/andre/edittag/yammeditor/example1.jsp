<%@ include file="inc/top.jsp" %>
<mm:cloud>
<html>
<head>
    <title>edittag - example 1</title>
<%@ include file="inc/head.jsp" %>
</head>
<body>
<%@ include file="inc/nav.jsp" %>
<h4>eerste voorbeeld met edittag</h4>

<mm:edit editor="yammeditor.jsp" icon="/mmbase/edit/my_editors/img/mmbase-edit.gif">
  <h3>Magazine</h3>
  <mm:import id="mynewsinstalled">no</mm:import>
  <mm:node number="default.mags" notfound="skip">
    <mm:import reset="true" id="mynewsinstalled">yes</mm:import>
    <mm:related path="posrel,news,people"
      fields="news.number,news.title,people.email,posrel.pos" orderby="posrel.pos">
      [<mm:field name="news.number" />] <strong><mm:field name="news.title" /></strong><br />
      by <mm:field name="people.email" /><br />
    </mm:related>
  </mm:node>
</mm:edit>

<mm:compare referid="mynewsinstalled" value="no">
  <p>You need to install the MyNews example. 
  Look for the <a href="<mm:url page="/mmexamples"/>"><mm:url page="/mmexamples"/></a>.</p>
</mm:compare>

</body>
</html>
</mm:cloud>
