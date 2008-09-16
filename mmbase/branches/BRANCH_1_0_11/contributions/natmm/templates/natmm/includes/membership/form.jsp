<%@page import="nl.leocms.forms.MembershipForm,nl.leocms.evenementen.forms.SubscribeAction" %>
<%@include file="/taglibs.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@page import="nl.mmatch.CSVReader"%>
<%@page import="java.util.Enumeration"%>
<mm:cloud jspvar="cloud">
   <%@include file="../calendar.jsp" %>
   <%@include file="../image_vars.jsp" %>
   <%@include file="countryNames.jsp" %>
   <%
   
   String paginaID = request.getParameter("p");
   boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);
   
   String sReferid = "I007";
   if (request.getParameter("referid")!=null) {
      sReferid = request.getParameter("referid");
   }
   %>
   <script>
      var iPaymentButtons = 3;
      var iPayment_periodButtons = 2;

      function clearPaymentRadio() {
         for (i=0;i<iPaymentButtons;i++) {
            document.MembershipForm.payment[i].checked=false;
         }
      }

      function clearPayment_periodRadio() {
         for (i=0;i<iPayment_periodButtons;i++) {
            document.MembershipForm.payment_period[i].checked=false;
         }
      }

		function clearAmountInput() {
			document.MembershipForm.amount.value='';
		}

      function disableAuthorizeInputs() {
         for (i=0;i<iPaymentButtons;i++) {
            document.MembershipForm.payment[i].disabled=true;
         }
         for (i=0;i<iPayment_periodButtons;i++) {
            document.MembershipForm.payment_period[i].disabled=true;
         }
			document.MembershipForm.amount.disabled=true;
      }

      function enableAuthorizeInputs() {
         for (i=0;i<iPaymentButtons;i++) {
            document.MembershipForm.payment[i].disabled=false;
         }
			for (i=0;i<iPayment_periodButtons;i++) {
            document.MembershipForm.payment_period[i].disabled=false;
         }
         document.MembershipForm.amount.disabled=false;
      }
      function setAuthorize() {
         document.MembershipForm.payment_type[0].checked=true;
      }
   </script>
   <%
   String localPath = request.getServletPath(); // localPath will always start with a forwardslash
   localPath = localPath.substring(0,localPath.lastIndexOf("/"));
   %>
   <html:form action="<%= localPath + "/MembershipForm" %>" scope="session" name="MembershipForm" type="nl.leocms.forms.MembershipForm">
		<html:hidden property="referid" value="<%= sReferid %>" />
		<bean:define id="actionId" property="action" name="MembershipForm" scope="session" type="java.lang.String"/>
      <bean:define id="validateCounter" property="validateCounter" name="MembershipForm" scope="session" type="java.lang.Integer"/>
		<bean:define id="phoneOnClickEvent" property="phoneOnClickEvent" name="MembershipForm" scope="session" type="java.lang.String"/>
		<logic:notEmpty property="street" name="MembershipForm">
			<bean:define id="selectedStreet" property="street" name="MembershipForm" scope="session" type="java.lang.String" />
		</logic:notEmpty>
		<bean:define id="allStreets" property="streets" name="MembershipForm" scope="session" type="java.util.ArrayList" />
		
		<% 
      if ((actionId.equals(MembershipForm.initAction)) || (actionId.equals(MembershipForm.correctAction))) {
		int ti = 0;
         %>
         <table cellspacing="0" cellpadding="0" border="0" style="width:100%;">
				<tr>
					<td colspan="2">
                  <%@include file="../page_intro.jsp" %>
      				<logic:equal name="MembershipForm" property="action" value="<%= MembershipForm.correctAction %>">
      			      <span class="colortitle">De door u ingevoerde gegevens kunnen niet worden verwerkt:
      				      <span style="color:red;"><html:errors bundle="LEOCMS" property="warning"/></span>
      			      </span><br/><br/>
      				</logic:equal>
                  <span class="colortitle"><bean:message bundle="LEOCMS" key="membershipform.intro" /></span>
      			</td>
				</tr>
				<tr><td colspan="2" style="height:5px;"></td></tr>
				<tr>
              <td class="maincolor" style="width:177px;">
						<% ti++; %>
                  <html:radio property="gender" style="border:0;" value="M" tabindex="<%= "" + ti %>"/>Dhr
						<% ti++; %>
                  <html:radio property="gender" style="border:0;" value="V" tabindex="<%= "" + ti %>"/>Mw
				  </td>
              <td style="width:177px;"></td>
            </tr>
				<tr><td colspan="2" style="height:5px;"></td></tr>
				<% String [] labels = { "Voorletters", "Tussenvoegsel", "Achternaam", "Straat", "Huisnummer", "Huisnummer toevoeging",
						"Postcode", "Woonplaats", "Land", "Geboortedatum", "E-mail", "Telefoon"};
			      String [] properties = { "initials", "suffix", "lastname", "street", "housenumber", "housenumber_extension", 
						"zipcode", "city", "", "","email", "phone"};
				   boolean [] requiredFields = { true, false, true, true, true, false, true, true, true, true, true, false }; 
					for(int i= 0; i< labels.length; i++) {
                  %>
						<tr>
				         <td class="maincolor" style="width:177px;padding:5px;line-height:0.80em;">
			               <nobr><%= labels[i] %>&nbsp;<%= (requiredFields[i] ? "*" : "") %></nobr>
			            </td>
			            <td class="maincolor" style="width:177px;padding:0px;padding-right:1px;vertical-align:top;text-align:right;<% if(!isIE) { %>padding-top:1px;<% } %>"><% 
                        
                        ti++;
			            
			            switch(i) {
			            	
			            	case 8: {
                           %>
                           <html:select property="country_code" style="width:177px;font-size:11px;" tabindex="<%= "" + ti %>"><% 
                              for(int j = 0; j< countryNames.length; j++) {
                                 %>
                                 <html:option value="<%= countryNames[j].toUpperCase() %>"><%= countryNames[j].toUpperCase() %></html:option><%
                              } 
                           %></html:select>
                           <%
                           		break;
                        	} 
			            	case 9: {

                           %><html:text property="dayofbirthDate" style="width:43px;border:0;" tabindex="<%= "" + ti %>"/><%
                           ti++;
									%><html:select property="dayofbirthMonth" style="width:89px;font-size:10px;" tabindex="<%= "" + ti %>">
               			      <html:option value="-1">...</html:option><%
   	               	      for(int m = 0; m < 12; m++) { 
                                 %><html:option value="<%= new Integer(m).toString() %>"><%= months_lcase[m] %></html:option><%
                              } 
                           %></html:select><%
                           ti++;
									%><html:text property="dayofbirthYear" style="width:43px;border:0;" tabindex="<%= "" + ti %>"/><% 
								break;
			            	}
			            	case 3: {
			            		String userSelectedStreet = (String)pageContext.getAttribute("selectedStreet");
                           if (userSelectedStreet == null) userSelectedStreet = "leeg";
								// maybe nothing has been entered or we have only one street
			            		if(allStreets == null || allStreets.size() == 0) {
			            		%>
								<html:text property="street" style="width:100%;border:0;" tabindex="<%= "" + ti %>"/>
								<%
			            		} else if(allStreets.size() == 1) {
			            		%>
								<input type="text" name="street" value="<%= allStreets.get(0) %>" style="width:100%;border:0;" tabindex="<%= "" + ti %>"/>
								<%
                        } else {
                           %>
			            		<select name="street" style="width:100%;border:0;" tabindex="<%= "" + ti %>">
				            		<logic:iterate id="currentStreet" property="streets" name="MembershipForm" type="java.lang.String">
				            			<logic:equal name="currentStreet" value="<%= userSelectedStreet %>">
											<option value="<bean:write name="currentStreet" />" selected="selected"><bean:write name="currentStreet" /></option>
										</logic:equal>
										<logic:notEqual name="currentStreet" value="<%= userSelectedStreet %>">
											<option value="<bean:write name="currentStreet" />"><bean:write name="currentStreet" /></option>
										</logic:notEqual>
				            		</logic:iterate>
			            		</select>
			            		
			            		<%
			            		}
								break;
	                        }
			            	default: {
                           %>
                           <html:text property="<%= properties[i] %>" style="width:100%;border:0;" onclick="<%= (labels[i].equals("Telefoon") ? phoneOnClickEvent : "") %>" tabindex="<%= "" + ti %>"/>
                           <%
                           		break;
			            	}
                        }

                     %></td>
			         </tr>
			         <tr><td colspan="2" style="height:5px;"></td></tr>
   				<%
			      } %>
				<tr>
				    <td colspan="2" class="maincolor">
                <table cellspacing="0" cellpadding="0" border="0" style="width:100%;">
                  <tr>
                     <td class="maincolor" style="padding-top:3px;padding-left:5px;">
                        <bean:message bundle="LEOCMS" key="membershipform.nieuwsbrief" />
                     </td>
                  <tr>
                  </tr>
                     <td class="maincolor" style="line-height:0.85em;text-align:right;padding-right:5px;">
                        <% ti++; %>
   			            <html:radio property="digital_newsletter" style="border:0;" value="J" tabindex="<%= "" + ti %>"/>ja
   			            <% ti++; %>
                        <html:radio property="digital_newsletter" style="border:0;" value="N" tabindex="<%= "" + ti %>"/>nee
                     </td>
          		   </tr>
               </table>
               </td>
            </tr>
            <tr><td colspan="2" style="height:15px;"></td></tr>
				<tr>
              <td colspan="2" class="maincolor" style="width:100%;padding:1px;">
                 <% ti++; %>
				  	  <table cellspacing="0" cellpadding="0"><tr>
                     <td style="vertical-align:top;"><html:radio property="payment_type" value="<%= MembershipForm.AUTHORIZE %>" onclick="enableAuthorizeInputs();" style="border:0;" tabindex="<%= "" + ti %>" />&nbsp;</td>
                     <td class="maincolor"><bean:message bundle="LEOCMS" key="membershipform.authorize" /></td>
                  </tr></table>
               </td>
			   </tr>
				<tr><td colspan="2" style="height:5px;"></td></tr>
				<tr>
              <td colspan="2" class="maincolor" style="width:354px;padding:1px;line-height:0.85em;valign:top;">
                 <% ti++; %>
					  <html:radio property="payment" style="border:0;" value="M200" onclick="clearAmountInput();clearPayment_periodRadio();setAuthorize();" tabindex="<%= "" + ti %>" 
                     />&nbsp;&euro;&nbsp;2,-&nbsp;<bean:message bundle="LEOCMS" key="membershipform.per.maand" />
                 <% ti++; %>
					  <html:radio property="payment" style="border:0;" value="M500" onclick="clearAmountInput();clearPayment_periodRadio();setAuthorize();" tabindex="<%= "" + ti %>"
                     />&nbsp;&euro;&nbsp;5,-&nbsp;<bean:message bundle="LEOCMS" key="membershipform.per.maand" />
                 <% ti++; %>
					  <html:radio property="payment" style="border:0;" value="M700" onclick="clearAmountInput();clearPayment_periodRadio();setAuthorize();" tabindex="<%= "" + ti %>"
                     />&nbsp;&euro;&nbsp;7,-&nbsp;<bean:message bundle="LEOCMS" key="membershipform.per.maand" />
				  </td>
            </tr>
				<tr><td colspan="2" style="height:5px;"></td></tr>
				<tr>
              <td class="maincolor" style="width:50%;padding:5px;line-height:0.85em;"><nobr>&nbsp;<bean:message bundle="LEOCMS" key="membershipform.bedrag" />&nbsp;&euro;&nbsp;</nobr></td>
              <td class="maincolor" style="width:50%;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
                  <% ti++; %>
                  <html:text property="amount" onfocus="clearPaymentRadio();setAuthorize();" style="width:50%;border:0;" tabindex="<%= "" + ti %>"
                     />
              </td>
            </tr>
				<tr>
              <td class="maincolor" colspan="2" style="width:50%;padding:1px;line-height:0.85em;valign:top;">
                 <% ti++; %>
                 <html:radio property="payment_period" style="border:0;" value="<%= MembershipForm.MONTH %>" onclick="clearPaymentRadio();setAuthorize();" tabindex="<%= "" + ti %>" 
                     />&nbsp;<bean:message bundle="LEOCMS" key="membershipform.per.maand.minimum" /><br/>
                 <% ti++; %>
                 <html:radio property="payment_period" style="border:0;" value="<%= MembershipForm.YEAR %>" onclick="clearPaymentRadio();setAuthorize();" tabindex="<%= "" + ti %>" 
                     />&nbsp;<bean:message bundle="LEOCMS" key="membershipform.per.jaar.minimum" />                     
              </td>
            </tr>
            <tr><td colspan="2" style="height:5px;"></td></tr>
				<tr>
              <td class="maincolor" style="width:50%;padding:5px;line-height:0.85em;"><nobr>&nbsp;Bank-/&nbsp;gironummer&nbsp;</nobr></td>
              <td class="maincolor" style="width:50%;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
                  <% ti++; %>
                  <html:text property="bankaccount" style="width:100%;border:0;" tabindex="<%= "" + ti %>" />
              </td>
            </tr>
            <tr><td colspan="2" style="height:15px;"></td></tr>
				<tr>
              <td colspan="2" class="maincolor" style="width:100%;padding:1px;">
                 <% ti++; %>
				  	  <table cellspacing="0" cellpadding="0"><tr>
                     <td style="vertical-align:top;"><html:radio property="payment_type" value="<%= MembershipForm.INVOICE 
                           %>" onclick="clearAmountInput();clearPaymentRadio();clearPayment_periodRadio();disableAuthorizeInputs();" style="border:0;" tabindex="<%= "" + ti %>" />&nbsp;</td>
                     <td class="maincolor"><div><bean:message bundle="LEOCMS" key="membershipform.invoice" /></div>
                 </tr></table>
               </td>
			   </tr>
				<tr><td colspan="2" style="height:10px;"></td></tr>
				<tr>
              <td colspan="2">
                  <bean:message bundle="LEOCMS" key="membershipform.required.fields.explanation" />
              </td>
           </tr>
           <tr>
               <td colspan="2" style="text-align:right;padding-top:10px;padding-bottom:10px;">
                  <% ti++; %>
                  <html:submit property="action" value="<%= MembershipForm.submitAction %>" styleClass="submit_image" style="width:165;" tabindex="<%= "" + ti %>"/>               
                  <% if(validateCounter.intValue()>0) { %>
                     <br/><br/>
                     <% ti++; %>
                     <html:submit property="action" value="<%= MembershipForm.skipValidationAction %>" styleClass="submit_image" style="width:270;" tabindex="<%= "" + ti %>"/>
                  <% } %>
               </td>
           </tr>
         </table>
         <% 
      } else if(actionId.equals(MembershipForm.submitAction) || actionId.equals(MembershipForm.skipValidationAction)) {
         %>
			<jsp:include page="response.jsp" />
         <% 
      } else if(actionId.equals(MembershipForm.confirmAction)) { 
         %>
      	<jsp:include page="thank_you.jsp" />
         <% 
      } %>
   </html:form>
</mm:cloud>