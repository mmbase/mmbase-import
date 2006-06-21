<html:hidden property="contenttypes" value="attachments" />
<table style="border: 1px solid black;width:100%">
   <tr>
      <td>
         <table border="0">
            <tr>
               <td><fmt:message key="attachmentform.title" /></td>
               <td><html:text property="title"/></td>
            </tr>
            <tr>
               <td><fmt:message key="attachmentform.description" /></td>
               <td><html:text property="description"/></td>
            </tr>
            <tr>
               <td><fmt:message key="attachmentform.filename" /></td>
               <td><html:text property="filename"/></td>
            </tr>
            <tr>
               <td></td>
               <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               			value="<fmt:message key="attachmentform.submit" />"/></td>
            </tr>
         </table>
      </td>
   </tr>
</table>
