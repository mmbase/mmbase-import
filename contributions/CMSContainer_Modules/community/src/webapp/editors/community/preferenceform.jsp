<table border="0">
 <mm:hasrank minvalue="administrator">
   <tr>
      <td style="width: 80px"></td>
      <td><img src="../gfx/icons/new.png" title="<fmt:message key="community.preference.add" />"/><fmt:message key="community.preference.new" />
      <input type="submit" name="createButton" onclick="create()" 
            value="<fmt:message key="community.preference.create" />"/></td>
   </tr>
 </mm:hasrank>  
   <tr>
      <td style="width: 80px"><fmt:message key="community.preference.user" /></td>
      <td><html:text style="width: 250px" property="userId"/></td>
   </tr>
   <tr>
      <td><fmt:message key="community.preference.module" /></td>
      <td><html:text style="width: 250px" property="module"/></td>
   </tr>
   <tr>
      <td><fmt:message key="community.preference.key" /></td>
      <td><html:text style="width: 250px" property="key"/></td>
   </tr>
   <tr>
      <td><fmt:message key="community.preference.value" /></td>
      <td><html:text style="width: 250px" property="value"/></td>
   </tr>
   <tr>
   <td></td>
   <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
            value="<fmt:message key="community.preference.submit" />"/>   
  </td>
</tr>
</table>