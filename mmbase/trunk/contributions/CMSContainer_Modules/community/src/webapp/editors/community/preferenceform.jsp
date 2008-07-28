 <mm:hasrank minvalue="administrator">
   <p><a href="#" onclick="create()" style="background:url(<cmsc:staticurl page='/editors/gfx/icons/new.png'/>) left no-repeat;padding-left:20px;"><fmt:message key="community.preference.new" /></a></p>
 </mm:hasrank>
 <table border="0" style="width:200px">  
   <tr>
      <td style="width:60px"><fmt:message key="community.preference.user" /></td>
      <td style="width:140px;"><html:text size="20" property="userId"/></td>
   </tr>
   <tr>
      <td><fmt:message key="community.preference.module" /></td>
      <td><html:text size="20" property="module"/></td>
   </tr>
   <tr>
      <td><fmt:message key="community.preference.key" /></td>
      <td><html:text size="20" property="key"/></td>
   </tr>
   <tr>
      <td><fmt:message key="community.preference.value" /></td>
      <td><html:text size="20" property="value"/></td>
   </tr>
   <tr>
   <td></td>
   <td><input type="submit" name="submitButton"  
            value="<fmt:message key="community.preference.submit" />"/>   
  </td>
</tr>
</table>