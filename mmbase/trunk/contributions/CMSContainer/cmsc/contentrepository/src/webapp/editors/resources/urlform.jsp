<html:hidden property="contenttypes" value="urls" />
<table border="0">
   <tr>
      <td style="width: 105px"><fmt:message key="urlform.title" /></td>
      <td><html:text style="width: 200px" property="title"/></td>
   </tr>
   <tr>
      <td><fmt:message key="imageform.description" /></td>
      <td><html:text style="width: 200px" property="description"/></td>
   </tr>
   <tr>
      <td><fmt:message key="urlform.url" /></td>
      <td><html:text style="width: 200px" property="url"/></td>
   </tr>
   <tr>
      <td><fmt:message key="urlform.valid" /></td>
      <td>
        <html:select property="valid">
            <html:option value=""><fmt:message key="urlform.valid.option.all" /></html:option>
            <html:option value="0"><fmt:message key="urlform.valid.option.invalid" /></html:option>
            <html:option value="1"><fmt:message key="urlform.valid.option.valid" /></html:option>
        </html:select>
      </td>
   </tr>
   <tr>
      <td></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               value="<fmt:message key="urlform.submit" />"/></td>
   </tr>
</table>