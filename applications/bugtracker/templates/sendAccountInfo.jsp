<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis">
  <%@include file="parameters.jsp" %>

  <mm:import externid="error">none</mm:import>

  <form action="<mm:url referids="parameters,$parameters">
     <mm:param name="template" value="showMessage.jsp" />
     <mm:param name="action"   value="sendaccountinfo" />
    </mm:url> method="POST">
  <table class="list">
    <mm:compare referid="error" value="login">
      <tr>
        <td>login <font color="#cc0000">** wrong account or password **</font></td>
      </tr>
    </mm:compare>

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
