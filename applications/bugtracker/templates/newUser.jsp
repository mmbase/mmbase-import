<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
  <%@include file="parameters.jsp" %>
  <mm:import externid="error">none</mm:import>
  <body class="basic">
    <form action="<mm:url referids="parameters,$parameters"><mm:param name="btemplate" value="newAccount.jsp" /></mm:url>" method="post">
      <table>
        <tr>
          <td colspan="2">
            <b>Create Account
            <mm:compare referid="error" value="email">
              *** Email already has a account, use resend account info !! ***
            </mm:compare>
            <mm:compare referid="error" value="account">
              *** Account name allready in use pick a new one ***
            </mm:compare>
            <mm:compare referid="error" value="info">
              *** Not all field where provided ***
            </mm:compare>
          </b>
        </td>
      </tr>
      
      <tr> <td>Account name </td> <td> <input name="newaccount"  value=""   /> </td></tr>
      <tr> <td>Firstname    </td> <td> <input name="newfirstname" value=""  /> </td></tr>
      <tr> <td>Lastname     </td> <td> <input name="newlastname"  value=""  /> </td></tr>
      <tr> <td>Email	      </td> <td> <input name="newemail"     value=""  /> </td></tr>
      <tr> <td colspan="2"> <input type="submit" value="Create Account" />     </td></tr>
      
    </table>
  </form>
</mm:cloud>

