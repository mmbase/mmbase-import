<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis">
  <%@include file="parameters.jsp" %>
  <form action="<mm:url referids="parameters,$parameters"/>" method="GET">
  <input type="hidden" name="btemplate" value="changeUser.jsp"/>
  <input type="hidden" name="action" value="sendaccountinfo"/>
  <table class="list">
    <tr>
      <th>Please give us your email so we can resend the info</th>
      <th><input name="email" value="" size="25" /></th>
    </tr>
    <tr>
      <td colspan="2"><input type="submit" value="Send info" /></td>
    </tr>
  </table>
</form>
</mm:cloud>
