<html:hidden property="contenttypes" value="reaction" />
<table border="0">
   <tr>
      <td style="width: 103px"><fmt:message key="reactionform.name" /></td>
      <td><html:text style="width: 200px" property="name"/></td>
   </tr>
   <tr>
      <td style="width: 103px"><fmt:message key="reactionform.email" /></td>
      <td><html:text style="width: 200px" property="email"/></td>
   </tr>
   <tr>
      <td style="width: 103px"><fmt:message key="reactionform.title" /></td>
      <td><html:text style="width: 200px" property="title"/></td>
   </tr>
   <tr>
      <td style="width: 103px"><fmt:message key="reactionform.body" /></td>
      <td><html:text style="width: 200px" property="body"/></td>
   </tr>
   <tr>
      <td></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
      			value="<fmt:message key="reactionform.submit" />"/></td>
   </tr>
</table>
