<%@include file="../includes/whiteline.jsp" %>
<table cellpadding="0" cellspacing="0"  align="center" border="0">
   <form method="POST" name="infoform" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) + templateQueryString %>" onSubmit="return postIt();">
   <tr><td><input type="text" name="termsearch" style="width:172px;margin-top:16px;" value="<%= termSearchId %>"></td></tr>
   <tr><td>
      <div align="right"><input type="submit" name="submit" value="Zoek" style="text-align:center;font-weight:bold;margin-top:16px;margin-top:16px;"></div>
   </td></tr>
   </form>
</table>
<%@include file="../includes/whiteline.jsp" %>
<script language="JavaScript" type="text/javascript">
<%= "<!--" %>
function postIt() {
    var href = document.infoform.action;
    var termsearch = document.infoform.elements["termsearch"].value;
    if(termsearch != '') href += "&termsearch=" + termsearch;
    document.location = href;
    return false;
}
<%= "//-->" %>
</script>