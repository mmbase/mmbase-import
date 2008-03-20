<%@page import="nl.leocms.evenementen.forms.SubscribeForm" %>
<%@include file="../includes/top0.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%@include file="../includes/top2_cacheparams.jsp" %>
<% 
// try this with mm:import and the non-ascii input will not be dealt with correctly
String nameID = request.getParameter("name"); if(nameID==null) { nameID = ""; }
String emailID = request.getParameter("email"); if(emailID==null) { emailID = ""; }
String titleID = request.getParameter("title"); if(titleID==null) { titleID = ""; }
String textID = request.getParameter("text"); if(textID==null) { textID = ""; }
String expiredateID = request.getParameter("ed"); if(expiredateID==null) { expiredateID = ""; }

if(nameID==null) { nameID = ""; }
if(emailID==null) { emailID = ""; }
if(titleID==null) { titleID = ""; }
if(textID==null) { textID = ""; }
if(expiredateID==null) { expiredateID = ""; }

String warningMessage = "";
int rI = 70000;
String confirmConstraint = "ads.expiredate > '" + rI + "'";

int objectPerPage = 5;

boolean showOne = false;

boolean isPosting = !(nameID+emailID+titleID+textID).equals("");
if(isPosting) {

   if(!expiredateID.equals("")) {
     %><mm:list path="ads" constraints="<%= "ads.email='" + emailID + "' AND ads.expiredate='" + expiredateID + "'" %>"
        ><mm:node element="ads"
            ><mm:setfield name="expiredate"><%= nowSec %></mm:setfield
            ><cache:flush scope="application" group="<%= paginaID %>" 
        /></mm:node
     ></mm:list><%

     emailID = ""; // clean the form

   } else {

      if(nameID.equals("")) {  warningMessage += "<li>Er moet een naam worden ingevuld.</li><br/>"; }
      if(emailID.equals("")) {  
         warningMessage += "<li>Er moet een email worden ingevuld.</li><br/>"; 
      } else {
         emailID = SubscribeForm.cleanEmail(emailID);
         String emailMessage = SubscribeForm.getEmailMessage(emailID,"email_message_required");
         if(!emailMessage.equals("")) {
            warningMessage += "<li>Het opgegeven emailadres bestaat niet.</li><br/>";
         }
      }
      if(titleID.equals("")) {  warningMessage += "<li>Er moet een onderwerp worden ingevuld.</li><br/>"; }
      if(textID.equals("")) {  warningMessage += "<li>Er moet een bericht worden ingevuld.</li><br/>"; }

      if(!nameID.equals("")&&warningMessage.equals("")) {

        int expireDate = (int) Math.floor(Math.random()*rI);
        String commitLink = HttpUtils.getRequestURL(request) + "?" + request.getQueryString() + "&email=" + emailID + "&ed=" + expireDate;
        %><mm:node number="<%= paginaID %>" id="this_page" jspvar="thisPage"
           ><mm:createnode type="ads" id="this_post"
               ><mm:setfield name="name"><%= nameID %></mm:setfield
               ><mm:setfield name="email"><%= emailID %></mm:setfield
               ><mm:setfield name="title"><%= titleID %></mm:setfield
               ><mm:setfield name="text"><%= textID %></mm:setfield
               ><mm:setfield name="postdate"><%= nowSec-1 %></mm:setfield
               ><mm:setfield name="expiredate"><%= expireDate %></mm:setfield
           ></mm:createnode
           ><mm:createrelation role="contentrel" source="this_post" destination="this_page" 
           /><mm:field name="title" jspvar="page_title"  vartype="String" write="false"
               ><mm:createnode type="email" id="thismail"
                   ><mm:setfield name="subject"><%= "Bevestigen plaatsing bericht op " + thisPage.getStringValue("titel") %></mm:setfield
                   ><mm:setfield name="from"><%= NatMMConfig.getFromEmailAddress() %></mm:setfield
                   ><mm:setfield name="to"><%= emailID %></mm:setfield
                   ><mm:setfield name="replyto"><%= NatMMConfig.getFromEmailAddress() %></mm:setfield
                   ><mm:setfield name="body">
                       <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                       </multipart>
                       <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                       <%= "<html>" 
                           + "Beste " + nameID + ",<br/><br/>"
                           + "Je hebt het volgende bericht verstuurd naar " + thisPage.getStringValue("titel") + "<br/><br/>" 
                           + titleID + "<br/><br/>" + textID + "<br/><br/>"
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

        nameID = ""; emailID = ""; titleID = ""; textID = ""; // clean the form

      }
     // show uncached page to the visitor who (attempts to) post
     cacheKey += "~preview";
     expireTime = 0;      

   }
}

%>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="../includes/top4_head.jsp" %>
<script>
<!--
function handleEnter (field, event) {
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if (keyCode == 13) {
		var i;
		for (i = 0; i < field.form.elements.length; i++)
			if (field == field.form.elements[i])
				break;
		i = (i + 1) % field.form.elements.length;
		field.form.elements[i].focus();
		return false;
	} 
	else
	return true;
}
//-->
</script>
<div style="position:absolute"><%@include file="/editors/paginamanagement/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<mm:node number="<%= paginaID %>">
<%
if(adID.equals("-1")) { 
  %>
  <mm:relatednodes type="ads" path="contentrel,ads" max="1" orderby="postdate" directions="DOWN" constraints="<%= confirmConstraint %>">
     <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
        <% 
        adID = artikel_number;
        %>
     </mm:field>
  </mm:relatednodes>
  <%
} 
%>
  <table cellspacing="0" cellpadding="0" width="744" align="center" border="0" valign="top">
    <tr>
      <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:4px">
        <%@include file="includes/homelink.jsp" %>
        <table cellspacing="0" cellpadding="0" border="0" style="width:173px;margin-bottom:10px;margin-top:3px;">
          <form name="emailform" method="post" target="" action="bulletinboard.jsp?p=<%= paginaID %>">
          <tr><td class="maincolor" style="width:173px;padding:5px;line-height:0.85em;">Naam *</td></tr>
          <tr>
            <td class="maincolor" style="width:173px;padding:0px;padding-left:1px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
              <input type="text" name="name" value="<%= nameID %>" style="width:171px;border:0;" onkeypress="return handleEnter(this, event)">
            </td>
          </tr>
          <tr><td colspan="2" style="height:7px;"></td></tr>
          <tr><td class="maincolor" style="width:173px;padding:5px;line-height:0.85em;">Email *</td></tr>
          <tr>
            <td class="maincolor" style="width:173px;padding:0px;padding-left:1px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
              <input type="text" name="email" value="<%= emailID %>" style="width:171px;border:0;" onkeypress="return handleEnter(this, event)">
            </td>
          </tr>
          <tr><td colspan="2" style="height:7px;"></td></tr>
          <tr>
               <td class="maincolor" style="width:1737px;padding:5px;line-height:0.85em;">
                  <a href="#" title="De tekst die u hier invult komt boven uw bericht te staan." style="color:#FFFFFF;text-decoration: none;">Quote *</a>
               </td>
          </tr>
          <tr>
            <td class="maincolor" style="width:173px;padding:0px;padding-left:1px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
              <input type="text" name="title" value="<%= titleID %>" style="width:171px;border:0;" onkeypress="return handleEnter(this, event)">
            </td>
          </tr>
          <tr><td colspan="2" style="height:7px;"></td></tr>
          <tr><td class="maincolor" style="width:173px;padding:5px;line-height:0.85em;">Uw bericht *</td></tr>
          <tr>
            <td colspan="2" class="maincolor" style="width:173px;<% if(!isIE) { %>padding-right:2px;<% } %>">
              <textarea rows="4" name="text" wrap="physical" style="width:171px;margin-left:1px;margin-right:1px;border:0;"><%= textID %></textarea>
            </td>
          </tr>
          <tr><td colspan="2" style="height:7px;"></td></tr>
          <tr>
             <td style="width:173px;text-align:right;">
                <input type="submit" value="VERSTUUR" class="submit_image" style="width:110;" /> 
             </td>
          </tr>
          </form>
        </table>
        <%@include file="includes/mailtoafriend.jsp" %>
        <br/>
        <jsp:include page="../includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="0" />
        </jsp:include>
      </td>
      <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:10px">
          <%
          if(isPosting && expiredateID.equals("")) {
             if(!warningMessage.equals("")) {
               %>
               <table cellspacing="0" cellpadding="0" style="vertical-align:top;width:350px">
                 <tr align="left" valign="top">
                    <td style="padding:10px 0px 10px 10px">
                      <div class="colortitle" style="font:bold 110%;">Uw bericht kon nog niet worden verwerkt om de volgende reden:</div>
                      <br/>
                      <ul>
                        <%= warningMessage %>
                      </ul>
                      <br/>
                      Pas alstublieft u bericht aan en verstuur het opnieuw.
                    </td>
                 </tr>
               </table>
               <%
             } else {
               %>
               <table cellspacing="0" cellpadding="0" style="vertical-align:top;width:350px">
                 <tr align="left" valign="top">
                   <td style="padding:10px 0px 10px 10px">
                     <div class="colortitle" style="font:bold 110%;">Bevestigen plaatsing bericht</div>
                     Je ontvangt een mail in je mailbox waarmee je het plaatsen van de juist verstuurde bericht kunt bevestigen.
                     <br/><br/>
                     <table border="0" cellpadding="0" cellspacing="0" class="maincolor" style="width:177px;">
                       <tr>
                          <td class="submitbutton" >&nbsp;<a href="/<mm:node number="<%= subsiteID %>"><mm:field name="naam" /></mm:node>" class="submitbutton">Naar de homepage</a>&nbsp;</td>
                          <td class="submit_image"></td>
                       </tr>
                     </table>
                    </td>
                 </tr>
               </table>
               <%
             }
          } else {

            String sNodes = paginaID;
            String sPath = "pagina,contentrel,ads";
            String sConstraints = confirmConstraint;
            int thisOffset = 1;
            try{
               if(!offsetID.equals("0")){
                  thisOffset = Integer.parseInt(offsetID);
                  offsetID ="";
               }
            } catch(Exception e) {} 

            if(showOne) {
               sNodes = adID;
               sPath = "ads";
               sConstraints = "";
               objectPerPage = 1;
               thisOffset = 1;
            }
            %>
            <mm:list nodes="<%= sNodes %>" path="<%= sPath %>" constraints="<%= sConstraints %>"
               offset="<%= "" + (thisOffset-1)*objectPerPage %>" max="<%= "" +  objectPerPage %>" 
               orderby="ads.postdate" directions="DOWN">
               <mm:first><table cellspacing="0" cellpadding="0" style="vertical-align:top;width:350px"></mm:first>
               <mm:node element="ads">
               <tr align="left" valign="top">
                  <td style="width:170px;">
                    <div style="padding-left:6px;padding-top:8px;">
                      <mm:first>
                         <mm:node number="<%= paginaID %>">
                           <div class="colortitle" style="font:bold 110%;"><mm:field name="titel"/></div>
                           <div style="padding-bottom:5px;"><b><mm:field name="kortetitel"/></b></div>
                         </mm:node>
                      </mm:first>
                      <span style="font:bold 110%;color:red">></span>
                      <span class="colortitle"><mm:field name="name"/></span>
                      <span class="colortxt"><mm:field name="postdate" jspvar="ads_postdate" vartype="String" write="false"
                       ><mm:time time="<%=ads_postdate%>" format="d MMM yyyy"/></mm:field></span>
                    </div>
                  </td>
                  <td style="padding-left:10px;padding-top:7px;font:bold;">
                    "<mm:field name="title" />"
                  </td>
                </tr>
                <tr align="left" valign="top">
                  <td colspan="2" style="padding:10px 0px 10px 10px">
                     <mm:field name="text"/>
                     <mm:last inverse="true"><table class="dotline"><tr><td height="3"></td></tr></table></mm:last>
                  </td>
               </tr>
               </mm:node>
               <mm:last></table></mm:last>
            </mm:list>
            <%
          } %>
      </td>
      <td style="padding-right:10px;padding-left:10px;padding-bottom:10px;padding-top:10px;vertical-align:top;width:190px;">
         <jsp:include page="includes/nav.jsp">
            <jsp:param name="a" value="<%= adID %>" />
            <jsp:param name="p" value="<%= paginaID %>" />
            <jsp:param name="object_type" value="ads" />
            <jsp:param name="object_title" value="name" />
            <jsp:param name="object_intro" value="title" />
            <jsp:param name="object_date" value="postdate" />
            <jsp:param name="extra_constraint" value="<%= confirmConstraint %>" />
            <jsp:param name="show_links" value="<%= "" + showOne %>" />
				<jsp:param name="object_per_page" value="<%= "" + objectPerPage %>" />
         </jsp:include>
   		<jsp:include page="../includes/shorty.jsp">
   	      <jsp:param name="s" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
   	      <jsp:param name="sr" value="2" />
   	   </jsp:include>
      </td>
    </tr>
  </table>
</mm:node>
<%@include file="includes/footer.jsp" %>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>
</cache:cache>
</mm:cloud>
