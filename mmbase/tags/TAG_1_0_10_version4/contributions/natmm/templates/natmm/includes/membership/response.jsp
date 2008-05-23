<%@page import="nl.leocms.forms.MembershipForm,nl.leocms.evenementen.forms.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%
String localPath = request.getServletPath(); // localPath will always start with a forwardslash
localPath = localPath.substring(0,localPath.lastIndexOf("/"));
%>
<html:form action="<%= localPath + "/MembershipForm" %>" scope="session" name="MembershipForm" type="nl.leocms.forms.MembershipForm">
	<bean:define id="payment_typeId" property="payment_type" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="payment_periodId" property="period" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="amountId" property="samount" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="bankaccountId" property="bankaccount" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="genderId" property="gender" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="initialsId" property="initials" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="suffixId" property="suffix" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="lastnameId" property="lastname" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="streetId" property="street" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="housenumberId" property="housenumber" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="housenumber_extensionId" property="housenumber_extension" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="cityId" property="city" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="zipcodeId" property="zipcode" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="country_codeId" property="country_code" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="phoneId" property="phone" name="MembershipForm" scope="session" type="java.lang.String"/>						
	<bean:define id="dayofbirthDateId" property="dayofbirthDate" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="dayofbirthMonthId" property="dayofbirthMonth" name="MembershipForm" scope="session" type="java.lang.Integer"/>								
	<bean:define id="dayofbirthYearId" property="dayofbirthYear" name="MembershipForm" scope="session" type="java.lang.String"/>								
	<bean:define id="emailId" property="email" name="MembershipForm" scope="session" type="java.lang.String"/>
	<bean:define id="digital_newsletterId" property="digital_newsletter" name="MembershipForm" scope="session" type="java.lang.String"/>
	<% boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1); %>
	<table class="dotline"><tr><td height="3"></td></tr></table>
   <table cellspacing="0" cellpadding="0" border="0" style="width:100%;">
		<tr>
			<td colspan="2">
				<span class="colortitle"><bean:message bundle="LEOCMS" key="membershipform.confirm.header" /></span>
			</td>
		</tr>
		<tr><td colspan="2" style="height:5px;"></td></tr>
		<tr>
			<td colspan="2">
				<bean:message bundle="LEOCMS" key="membershipform.confirm.text.1" />
			</td>
		</tr>
		<tr><td colspan="2" style="height:5px;"></td></tr>
			<% String sPayment = "";
				String sAmount = "";
				if (payment_typeId.equals(MembershipForm.INVOICE)) {
					sPayment = "Betalingswijze:"; 
               sAmount = "&nbsp;Acceptgiro";
				} else {
					sPayment = "Machtiging: ";
               sAmount = "&euro;&nbsp;" + amountId + "&nbsp;";
   				if (payment_periodId.equals(MembershipForm.MONTH)) {
   					sAmount += "per maand";
   				} else {
   					sAmount += "per jaar";
               }
				}   			
				String sName = "";
				if (genderId.equals("M")){
               sName = "De heer&nbsp;";
            } else if (genderId.equals("V")) {
               sName = "Mevrouw&nbsp;";
            }
				sName += initialsId + "&nbsp;";
            if(!suffixId.equals("")) { sName += suffixId + "&nbsp;"; } 
				sName += lastnameId;
				String sBirthday = dayofbirthDateId + "-" + (dayofbirthMonthId.intValue()+1) + "-" + dayofbirthYearId;
				String sNewsLetter = ""; 
				if (digital_newsletterId.equals("J")) {
               sNewsLetter = "ja";
            } else {
               sNewsLetter = "nee";
            }
            if(phoneId.equals(SubscribeForm.initPhone)) {
               phoneId = "";
            }
				String [] info = { sPayment, "Bank/gironummer:", "", "Straat en huisnr:", "", "", "Postcode:", "Plaats:", "Land:", "Telefoon:", "Geboortedatum:", "E-mailadres:", "Digitale nieuwsbrief:" };
		      String [] ids = { sAmount, bankaccountId.toString() ,sName, streetId, housenumberId, housenumber_extensionId, zipcodeId, cityId, country_codeId, phoneId, sBirthday, emailId, sNewsLetter}; 
				for(int i = 0; i<ids.length; i++) {
               if(!ids[i].equals("")) {
                  %>
      				<tr>
      					<td colspan="2" style="width:50%;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
      						<% 
                        if (i==3) {
                           %>
      							<%= info[i] %> <%= ids[i] %> <%= ids[i+1] %> <%= ids[i+2] %> 
      						   <%
                           i = i+2;
      						 } else {
                           %>	
      							<%= info[i] %> <%= ids[i] %> 
      						   <% 
                        } %>
      					</td>
      				</tr>
   			      <%
               } 
               if (i==1) {
                  %>
   					<tr><td colspan="2" style="height:5px;"></td></tr>
   			      <% 
               }
			   } %>
		<tr><td colspan="2" style="height:5px;"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
		<tr><td colspan="2" style="height:5px;"></td></tr>
		<tr>
			<td style="width:50%;">
				<html:submit property="action" value="<%= MembershipForm.backAction %>" styleClass="submit_image" style="width:165;"/>
			</td>
			<td style="width:50%;">
				<html:submit property="action" value="<%= MembershipForm.confirmAction %>" styleClass="submit_image" style="width:165;"/>
			</td>
	   </tr>
</table>
</html:form>
</mm:cloud>