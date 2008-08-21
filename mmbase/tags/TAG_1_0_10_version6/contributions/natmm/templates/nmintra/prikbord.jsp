<%@page import="nl.leocms.evenementen.forms.SubscribeForm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%@include file="includes/calendar.jsp" %><%

// try this with mm:import and the non-ascii input will not be dealt with correctly
String emailId = request.getParameter("email"); if(emailId==null) { emailId = ""; }
String titleId = request.getParameter("title"); if(titleId==null) { titleId = ""; }
String textId = request.getParameter("text"); if(textId==null) { textId = ""; }
String expiredateId = request.getParameter("ed"); if(expiredateId==null) { expiredateId = ""; }

if(emailId==null) { emailId = ""; }
if(titleId==null) { titleId = ""; }
if(textId==null) { textId = ""; }
if(expiredateId==null) { expiredateId = ""; }

int rI = 70000;

String warningMessage = "";
boolean isPosting = !(emailId+titleId+textId).equals("");
if(isPosting) {

   if(!expiredateId.equals("")) {

	  int period = 14;
	  cal.add(Calendar.DATE,period);
     %><mm:list path="ads" constraints="<%= "ads.email='" + emailId + "' AND ads.expiredate='" + expiredateId + "'" %>"
        ><mm:node element="ads"
            ><mm:setfield name="expiredate"><%= (cal.getTime().getTime()/1000) %></mm:setfield
            ><cache:flush scope="application" group="<%= paginaID %>" 
        /></mm:node
     ></mm:list><%

     emailId = ""; // clean the form

   } else {

      if(emailId.equals("")) {  
         warningMessage += "<li>Er moet een email worden ingevuld.</li><br/>"; 
      } else {
         emailId = SubscribeForm.cleanEmail(emailId);
         if(!com.cfdev.mail.verify.EmailVerifier.validateEmailAddressSyntax(emailId)) {
            warningMessage += "<li>Het opgegeven emailadres bestaat niet.</li><br/>";
         }
      }
      if(titleId.equals("")) {  warningMessage += "<li>Er moet een onderwerp worden ingevuld.</li><br/>"; }
      if(textId.equals("")) {  warningMessage += "<li>Er moet een bericht worden ingevuld.</li><br/>"; }

      if(!titleId.equals("")&&warningMessage.equals("")) {

        int expireDate = (int) Math.floor(Math.random()*rI);
        String commitLink = HttpUtils.getRequestURL(request) + "?" + request.getQueryString() + "&email=" + emailId + "&ed=" + expireDate;
        %><mm:node number="<%= paginaID %>" id="this_page" jspvar="thisPage"
           ><mm:createnode type="ads" id="this_post"
               ><mm:setfield name="name"><%= nameId %></mm:setfield
               ><mm:setfield name="email"><%= emailId %></mm:setfield
               ><mm:setfield name="title"><%= titleId %></mm:setfield
               ><mm:setfield name="text"><%= textId %></mm:setfield
               ><mm:setfield name="postdate"><%= nowSec-1 %></mm:setfield
               ><mm:setfield name="expiredate"><%= expireDate %></mm:setfield
           ></mm:createnode
           ><mm:createrelation role="contentrel" source="this_post" destination="this_page" 
           /><mm:field name="title" jspvar="page_title"  vartype="String" write="false"
               ><mm:createnode type="email" id="thismail"
                   ><mm:setfield name="subject"><%= "Bevestigen plaatsing bericht op " + thisPage.getStringValue("titel") %></mm:setfield
                   ><mm:setfield name="from"><%= NMIntraConfig.getFromEmailAddress() %></mm:setfield
                   ><mm:setfield name="to"><%= emailId %></mm:setfield
                   ><mm:setfield name="replyto"><%= NMIntraConfig.getFromEmailAddress() %></mm:setfield
                   ><mm:setfield name="body">
                       <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                       </multipart>
                       <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                       <%= "<html>" 
                           + "Beste " + nameId + ",<br/><br/>"
                           + "Je hebt het volgende bericht verstuurd naar " + thisPage.getStringValue("titel") + "<br/><br/>" 
                           + titleId + "<br/><br/>" + textId + "<br/><br/>"
                           + "Klik op de onderstaande link om de plaatsing van je bericht op " 
                           + thisPage.getStringValue("naam") + " te bevestigen.<br/><br/>"
                           + "<a href=\"" + commitLink + "\">" + commitLink + "</a>"
                           + "</html>" %>
                       </multipart>
                   </mm:setfield
               ></mm:createnode
               ><mm:node referid="thismail"
                   ><mm:field name="mail(oneshot)" 
               /></mm:node
               ><mm:remove referid="thismail" 
           /></mm:field
        ></mm:node><%

        emailId = ""; titleId = ""; textId = ""; // clean the form

      }
     // show uncached page to the visitor who (attempts to) post
     cacheKey += "~preview";
     expireTime = 0;
   }
}

%><cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
	<%@include file="includes/header.jsp" %> 
	<td><%@include file="includes/pagetitle.jsp" %></td>
	<td><% String rightBarTitle = "Plaats uw bericht"; 
	%><%@include file="includes/rightbartitle.jsp" 
	%></td>
	</tr>
	<tr>
	<td class="transperant">
		<div class="<%= infopageClass %>" id="infopage">
		<%
		if(isPosting && expiredateId.equals("")) {
		
			String messageTitle = "";
			String messageBody = "";
			String messageHref = "";
			String messageLinktext = "";
			String messageLinkParam = "";  
		
			if(!warningMessage.equals("")) {
				messageTitle = "Uw bericht kon niet worden geplaatst";
				messageBody = "Uw bericht kon niet worden geplaatst om de volgende reden:<br/><br/>" + warningMessage + ".<br/><br/>Pas alstublieft u bericht aan en verstuur het opnieuw." ;
				messageHref = "";
				messageLinktext = "";
				messageLinkParam = "";  
			} else {
				messageTitle = "Bevestigen plaatsing advertentie";
				messageBody = "Je ontvangt een mail in je mailbox waarmee je het plaatsen van de juist verstuurde advertentie kunt bevestigen.";
				messageHref = "/index.jsp?r=" + subsiteID;
				messageLinktext = "naar de homepage";
				messageLinkParam = "";  
			}
			
			%><%@include file="includes/showmessage.jsp" %><%
			
		} else {
			
			%>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				 <tr>
					<td style="padding:10px;padding-top:18px;">
						<%@include file="includes/relatedteaser.jsp" %>
					</td>
				</tr>
				<tr>
					<td style="padding:10px;padding-top:18px;">
						<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,ads"
							constraints="<%= "ads.expiredate > " + nowSec %>"
							orderby="ads.postdate" directions="DOWN"
							 ><mm:first>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
							 </mm:first
							 ><tr><td colspan="2" class="black" style="width:100%;"><img src="media/spacer.gif" width="1" height="1"></td></tr>
							 <tr><td colspan="2" style="width:100%;"><img src="media/spacer.gif" width="1" height="5"></td></tr>
							 <tr><td><div class="pageheader"><mm:field name="ads.title" /></div></td>
								  <td><div align="right"><nobr><mm:field name="ads.postdate" id="edate" write="false" 
											 /><mm:time format="d MMM yyyy" referid="edate" 
											 /><mm:remove referid="edate"
										/></nobr></div></td></tr>
							 <tr><td colspan="2"><span class="black"><mm:field name="ads.text" /></span></td></tr>
							 <tr><td colspan="2" style="width:100%;"><img src="media/spacer.gif" width="1" height="10"></td></tr>
							 <tr><td colspan="2"><a href='mailto:<mm:field name="ads.email" />'><mm:field name="ads.email" /></a></td></tr>
							 <tr><td colspan="2" style="width:100%;"><img src="media/spacer.gif" width="1" height="5"></td></tr>
							 <mm:last>
								</table>
							 </mm:last
						></mm:list>
						<%@include file="includes/pageowner.jsp" %>
					</td>
				</tr>
			</table>
			<%
		}
		%>
		</div>
	</td><%
	
	// *************************************** right bar with the form *******************************
	%>
	<td>
		<%@include file="includes/whiteline.jsp"%>
		<form name="prikbord" method="post">
		<table cellspacing="0" cellpadding="0" border="0" align="center">
			 <tr>
				  <td><div align="right"><span class="light">Uw email adres</span></div></td>
			 </tr>
			 <tr>
				  <td><div align="right"><input type="text" style="width:195px;" name="email" value="<%= emailId %>"></div></td>
			 </tr>
			 <tr>
				  <td><div align="right"><span class="light">Titel van uw bericht</span></div></td>
			 </tr>
			 <tr>
				  <td><div align="right"><input type="text" style="width:195px;" name="title" value="<%= titleId %>"></div></td>
			 </tr>
			 <tr>
				  <td><div align="right"><span class="light">Uw bericht</span></div></td>
			 </tr>
			 <tr>
				  <td><div align="right"><textarea name="text" style="width:195px;" rows="6"><%= textId %></textarea></div></td> 
			 </tr>
			 <tr>
				  <td><br><br>
				  <div align="right"><input type="submit" name="Submit" value="Plaats uw bericht" style="text-align:center;font-weight:bold;"></div>
				  </td>
			 </tr>
			</table>
		</form>
		<%@include file="includes/whiteline.jsp" %>
	</td>
	<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>