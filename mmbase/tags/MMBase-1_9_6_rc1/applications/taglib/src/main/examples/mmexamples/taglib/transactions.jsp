<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:content type="text/html" expires="0">
<html>
  <title>Transactions</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
  <body>
    <%@ include file="menu.jsp"%>
    
    <h1>Transactions</h1>
    <mm:cloud rank="basic user">
      <mm:transaction name="mytrans" commitonclose="false" id="trans">
        ${trans.nodes}
        <mm:createnode type="poll" id="poll">
        </mm:createnode>
        <mm:createnode type="answer" id="answer">
        </mm:createnode>
        <mm:createrelation source="poll" destination="answer" role="posrel">
          <mm:field name="pos">123</mm:field>
        </mm:createrelation>
        Created relation between ${poll} and ${answer}
        ${trans}: ${trans.nodes}
      </mm:transaction>
    </mm:cloud>

</body>
</html>

</mm:content>