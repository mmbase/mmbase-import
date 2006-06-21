<html:hidden property="contenttypes" value="urls" />
<table style="border: 1px solid black;width:100%">
   <tr>
      <td>
         <table border="0">
            <tr>
               <td><fmt:message key="urlform.name" /></td>
               <td><html:text property="name"/></td>
            </tr>
            <tr>
               <td><fmt:message key="urlform.description" /></td>
               <td><html:text property="description"/></td>
            </tr>
            <tr>
               <td><fmt:message key="urlform.url" /></td>
               <td><html:text property="url"/></td>
            </tr>
            <tr>
               <td></td>
               <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               			value="<fmt:message key="urlform.submit" />"/></td>
            </tr>
         </table>
      </td>
   </tr>
</table>
