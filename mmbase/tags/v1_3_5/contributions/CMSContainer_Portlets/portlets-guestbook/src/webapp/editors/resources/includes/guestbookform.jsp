<html:hidden property="contenttypes" value="guestmessage" />
<table border="0">
   <tr>
      <td style="width: 150px"><fmt:message key="guestbook.search.field.name" /></td>
      <td><html:text style="width: 250px" property="name"/></td>
   </tr>
   <tr>
      <td style="width: 150px"><fmt:message key="guestbook.search.field.email" /></td>
      <td><html:text style="width: 250px" property="email"/></td>
   </tr>
   <tr>
      <td style="width: 150px"><fmt:message key="guestbook.search.field.title" /></td>
      <td><html:text style="width: 250px" property="title"/></td>
   </tr>
   <tr>
      <td style="width: 150px"><fmt:message key="guestbook.search.field.body" /></td>
      <td><html:text style="width: 250px" property="body"/></td>
   </tr>
   <tr>
      <td style="width: 150px"><fmt:message key="guestbook.search.field.remote" /></td>
      <td><html:checkbox property="remote"/></td>
   </tr>
   <tr>
      <td></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
      			value="<fmt:message key="guestbook.search.submit" />"/></td>
   </tr>
</table>