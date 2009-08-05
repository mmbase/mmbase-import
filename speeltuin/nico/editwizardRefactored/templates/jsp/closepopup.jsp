<%@page contentType="text/html; charset=UTF-8"%>
<html>
<script language="javascript"><%
  String isCommitted = (String)request.getAttribute("committed");
  if ("true".equalsIgnoreCase(isCommitted)) {
    String sendcmd = (String)request.getAttribute("sendcmd");
    String objnumber = (String)request.getAttribute("objnumber");
    %>
    try { // Mac IE doesn't always support window.opener.
       //window.opener.location.reload();
       window.opener.saveScroll();
       window.opener.doSendCommand("<%=sendcmd%>","<%=objnumber%>");
    } catch (e) {}
<%} %>
window.close();
</script>
</html>
