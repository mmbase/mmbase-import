<table style="border: 1px solid black;width:100%">
   <tr>
      <td colspan="2">
         <!-- First select the contentelement type. -->
         <table border="0" width="100%">
            <tr>
               <td width="100"><fmt:message key="searchform.contenttype" /></td>
               <td>
                  <html:select property="contenttypes" onchange="selectContenttype('${searchinit}');" >
                     <html:option value="contentelement">contentelement</html:option>
                     <mm:listnodes type="editwizards">
                        <mm:field name="nodepath" jspvar="nodepath" id="nodepath" vartype="String">
                           <% if (ContentElementUtil.isContentType(nodepath)) { %>
		                      <html:option value="${nodepath}"><mm:nodeinfo nodetype="${nodepath}" type="guitype"/></html:option>
                           <% } %>
                        </mm:field>
                     </mm:listnodes>
                  </html:select>
               </td>
            </tr>
         </table>
         <hr />
      </td>
   </tr>

   <tr>
      <td>
         <table border="0">
            <tr>
               <td width="100"><fmt:message key="searchform.creationdate" /></td>
               <td>
                  <html:select property="creationdate" size="1">
                     <html:option value="0"> - </html:option>
                     <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                     <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                     <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                     <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                     <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                  </html:select>
               </td>
            </tr>
            <tr>
               <td width="100"><fmt:message key="searchform.lastmodifieddate" /></td>
               <td>
                  <html:select property="lastmodifieddate" size="1">
                     <html:option value="0"> - </html:option>
                     <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                     <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                     <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                     <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                     <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                  </html:select>
               </td>
            </tr>
            <tr>
               <td width="100"><fmt:message key="searchform.embargodate" /></td>
               <td>
                  <html:select property="embargodate" size="1">
                     <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                     <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                     <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                     <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                     <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                     <html:option value="0"> - </html:option>
                     <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                     <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                     <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                     <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                     <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                  </html:select>
               </td>
            </tr>
            <tr>
               <td width="100"><fmt:message key="searchform.expiredate" /></td>
               <td>
                  <html:select property="expiredate" size="1">
                     <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                     <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                     <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                     <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                     <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                     <html:option value="0"> - </html:option>
                     <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                     <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                     <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                     <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                     <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                  </html:select>
               </td>
            </tr>
            <tr>
               <td width="100"><fmt:message key="searchform.personal" /></td>
               <td>
                  <html:select property="personal" size="1">
                     <html:option value=""> - </html:option>
                     <html:option value="lastmodifier"><fmt:message key="searchform.personal.lastmodifier" /></html:option>
                     <html:option value="author"><fmt:message key="searchform.personal.author" /></html:option>
                  </html:select>
               </td>
            </tr>
	<mm:hasrank minvalue="administrator">
            <tr>
               <td width="100"><fmt:message key="searchform.useraccount" /></td>
               <td>
                  <html:select property="useraccount" size="1">
                     <html:option value=""> - </html:option>
               		 <mm:listnodes type='user' orderby='username'>
               		     <mm:field name="username" id="useraccount" write="false"/>
                         <html:option value="${useraccount}"> <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /> </html:option>               		
               		 </mm:listnodes>
                  </html:select>
               </td>
            </tr>
	</mm:hasrank>
            <tr>
               <td><fmt:message key="searchform.title" /></td>
               <td><html:text property="title"/></td>
            </tr>
            <tr>
               <td><fmt:message key="searchform.number" /></td>
               <td><html:text property="objectid"/></td>
            </tr>
            <tr>
               <td></td>
               <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               			value="<fmt:message key="searchform.submit" />"/></td>
            </tr>
         </table>
      </td>
      <!-- Print the advanced search fields. -->
      <td valign="top">
            <%
               try {
                  NodeManager nodeManager = cloud.getNodeManager(contenttypes);
                  if (!nodeManager.getName().equalsIgnoreCase(ContentElementUtil.CONTENTELEMENT)) {
                     %>
			         <table>
                     <tr>
                        <td colspan="2">
                        	<fmt:message key="searchform.searchfor">
                        		<fmt:param><mm:nodeinfo nodetype="${contenttypes}" type="guitype"/></fmt:param>
                        	</fmt:message>
                        </td>
                     </tr>
                     <mm:fieldlist nodetype="${contenttypes}" jspvar="field">
                        <% if (!ContentElementUtil.isContentElementField(field)&& !field.isVirtual()) {
                            String fieldName = nodeManager.getName() + "." + field.getName();
                            String fieldValue = (null == request.getParameter(fieldName))? "" : request.getParameter(fieldName);
                        %>
                           <tr>
                              <td>
                                 <mm:fieldinfo type="guiname" />:
                              </td>
                              <td>
                                 <input type="text" name="<%= fieldName %>" value="<%= fieldValue %>" />
                              </td>
                           </tr>
                        <% } %>
                     </mm:fieldlist>
			         </table>
                     <%
                  }
               }
               catch (Exception e) {
                  // Not a valid contenttype was supplied.
               }
            %>
      </td>
   </tr>
</table>