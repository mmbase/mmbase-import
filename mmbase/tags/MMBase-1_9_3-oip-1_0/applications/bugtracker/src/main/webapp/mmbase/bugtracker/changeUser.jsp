<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
  <%@include file="parameters.jsp" %>
  <mm:import externid="error">none</mm:import>
  <form action="<mm:url referids="parameters,$parameters"/>" method="POST">
   <input type="hidden" name="action" value="checkuser"/>
   <table class="list">
     <mm:compare referid="error" value="login">
       <tr>
         <td colspan="2">
           feedback <font color="#cc0000">** wrong account or password **</font>
         </td>
       </tr>
     </mm:compare>

    <tr><th>Login</th><td><input name="account" value="" size="10" /></td></tr>
    <tr><th>Password</th><td><input name="password" value="" size="10" type="password" /></td></tr>
    <tr>
      <td colspan="2">
        <input type="submit" value="login" />
        <p>
          I forgot my password mail me the info 
          <a href="<mm:url referids="parameters,$parameters"><mm:param name="btemplate" value="sendAccountInfo.jsp" /></mm:url>">resend info</a>
        </p>
        <p>
          I don't have a password yet
          <a href="<mm:url referids="parameters,$parameters"><mm:param name="btemplate" value="newUser.jsp" /></mm:url>">get account</a>
        </p>
      </td>
    </tr>
  </table>
</form>
</mm:cloud>
