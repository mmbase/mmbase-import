<table border="0">
 <mm:hasrank minvalue="administrator">
   <tr>
      <td  style="width: 80px">
      <ul class="shortcuts">
         <li class="new"><a href="#" onclick="create()"><fmt:message key="community.preference.new" /></a></li>
      </ul>
      </td>
      <td></td>
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