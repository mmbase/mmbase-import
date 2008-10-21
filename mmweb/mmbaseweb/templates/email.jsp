<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" 
%><%@ include file="/includes/relatedpages.jsp" %>
<div id="pagecontent">
  <mm:list nodes="$page" path="pages,posrel,articles" searchdir="destination"
    orderby="posrel.pos" directions="UP">
    <mm:node element="articles"><%@ include file="/includes/article.jsp" %></mm:node>
  </mm:list>
<%@ include file="/includes/secretww.jsp" %>
<%!
public boolean isValidEmailAddress(String address) {
    String pattern = "(?i)\\A[A-Z0-9_\\-\\+\\&\"\\*\\#]+([\\.-]?[A-Z0-9_\\-\\+\\&\"\\*\\#])*@([A-Z0-9_-]{2,}[\\.]{1})+([A-Z]{2,})\\z";
    return (address.matches(pattern));
}
%>

<mm:import id="tomail">andre@mmbase.org</mm:import>
<mm:node number="$page" notfound="skip">
  <mm:import id="tomail" reset="true"><mm:field name="menuname"><mm:isempty>jo@mmbase.org</mm:isempty><mm:isnotempty><mm:write /></mm:isnotempty></mm:field></mm:import>
</mm:node>

<mm:import externid="naam" />
<mm:import externid="email" jspvar="email" />
<mm:import externid="bericht" />

<mm:import externid="captcha" jspvar="captcha" from="parameters" />
<mm:import externid="captchakey" from="session" />

<mm:import externid="action" />

<% StringBuffer errMsg = new StringBuffer(); %>
<mm:present referid="action">
  <%-- mm:compare referid="captcha" value="">
	<% 
	errMsg.append("De sleutel is niet overgetypt.<br />"); 
	%>
  </mm:compare>  
  <mm:compare referid="captchakey" value="$captcha" inverse="true">
	<% 
	if (errMsg.length() <= 0) { 
	   errMsg.append("De sleutel '" + captcha + "' komt niet overeen met de afbeelding.<br />"); 
	}
	%>
  </mm:compare --%> 
<%
if (!"".equals(email)) {
	if (!isValidEmailAddress(email) ) {
		errMsg.append("<br />'" + email + "' is not a valid e-mail address. Please provide a valid one.");
	}
} else {
	errMsg.append("<br />You did not provide an e-mail address.");
}

if (errMsg.length() > 0) {
	out.println("<p class=\"err\"><strong>Something went wrong:</strong> " + errMsg + "</p>");
} else {
%>
<mm:cloud username="site" password="$ww" method="pagelogon">
<%-- send an e-mail--%>
<mm:createnode id="mailtje" type="email">
  <mm:setfield name="from"><mm:write referid="naam" /> <<mm:write referid="email" />></mm:setfield>
  <mm:setfield name="replyto"><mm:write referid="email" /></mm:setfield>
  <mm:setfield name="to">${tomail}</mm:setfield>
  <mm:setfield name="bcc">andre@mmbase.org</mm:setfield>
  <mm:setfield name="subject">Contactform MMBase website</mm:setfield>
<mm:setfield name="body">
<mm:write referid="bericht" />
--------------------------------------
naam:  <mm:write referid="naam" />
email:  <mm:write referid="email" />
  </mm:setfield>
</mm:createnode>

<mm:node referid="mailtje"><mm:function name="mail" /></mm:node>

<mm:node referid="mailtje">
  <mm:field name="mailstatus">
	<mm:compare value="1">
	  <p class="msg">
	    <strong>Thank you.</strong><br />
	    Your e-mail has been send.
	  </p>
      <mm:import id="mailsend">ok</mm:import>
	</mm:compare>
	<mm:compare value="2">
	  <div class="err">
        <h4>Your e-mail has not been send.</h4>
        <p>Some error occurred. Please try again or contact the webmaster.</p>
      </div>
	</mm:compare>
  </mm:field>
</mm:node>
</mm:cloud>
 
<% } %>

</mm:present><%-- /action --%>

<mm:notpresent referid="mailsend">
  <mm:link page="/scripts/forms.js"><script src="${_}" type="text/javascript"><!-- help IE --></script></mm:link>
  <mm:link page="/scripts/contact.js"><script src="${_}" type="text/javascript"><!-- help IE --></script></mm:link>
  <mm:link referids="portal?,page?"><form action="${_}" method="post" onsubmit="return checkContact(this);" id="formcontact"></mm:link>
    <fieldset>
      <p>
        <label for="naam">Name</label>
        <input name="naam" id="naam" value="${naam}" type="text" size="46" maxlength="255" />
      </p>
      <p>
        <label for="email">E-mail</label>
        <input name="email" id="email" value="${email}" type="text" size="46" maxlength="255" />
      </p>
      <p>
        <label for="bericht">Message</label>
        <textarea id="bericht" name="bericht" rows="12" cols="68">${bericht}</textarea>
      </p>
      <p>
        <input name="action" type="submit" value="Send" />
      </p>
    </fieldset>
  </form>
</mm:notpresent>
  
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
