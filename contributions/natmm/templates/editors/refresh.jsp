<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" rank="basic user">
<html>
<head>
  <script>
    // *** refresh every X minutes , avoid session timeout ***
    function resubmit() {
      document.forms[0].submit();
    }
  </script>
</head>
<body onload="javascript:setTimeout('resubmit()',10*60000);" style="background-color:#00FF00;">
  <form name="dummy" method="post" target=""></form>
</body>
</mm:cloud>
