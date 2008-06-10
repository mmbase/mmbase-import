   <table border="0">
      <tr>
         <td style="width: 150px"><fmt:message key="community.preference.user" /></td>
         <td><html:text style="width: 250px" property="userId"/></td>
      </tr>
      <tr>
         <td style="width: 150px"><fmt:message key="community.preference.module" /></td>
         <td><html:text style="width: 250px" property="module"/></td>
      </tr>
      <tr>
         <td style="width: 150px"><fmt:message key="community.preference.key" /></td>
         <td><html:text style="width: 250px" property="key"/></td>
      </tr>
      <tr>
         <td style="width: 150px"><fmt:message key="community.preference.value" /></td>
         <td><html:text style="width: 250px" property="value"/></td>
      </tr>
      <tr>
      <td style="width: 150px"></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               value="<fmt:message key="community.preference.submit" />"/><c:forEach var="space" begin="1" end="15" step="1">&nbsp; </c:forEach>
                  <mm:hasrank minvalue="administrator">
          <a href="<mm:url page="PreferenceAction.do" >
               <mm:param name="method">addInit</mm:param>
                </mm:url>">

               <img src="../gfx/icons/new.png" title="<fmt:message key="community.preference.add" />"/>New 
          </a>
             </mm:hasrank>
     </td>
   </tr>
   </table>
