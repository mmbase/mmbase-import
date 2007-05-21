<%@include file="globals.jsp" %>
<html>
<head>
   <title>Footer</title>
   <link rel="stylesheet" type="text/css" href="./css/main.css" />
   <style type="text/css">
      body { behavior: url(./css/hover.htc);}
   </style>
</head>
<body>
   <div id="footer">
      <br />
      <fmt:message key="createdby" />
      <div style="float:right">&nbsp;</div>      
      <br/>
      <fmt:message key="version" /> <cmsc:version/>
   </div>
</body>
</html>
