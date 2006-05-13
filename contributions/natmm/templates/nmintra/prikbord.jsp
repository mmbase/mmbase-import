<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" 
%><%@include file="includes/calendar.jsp" 

%><% if(!emailId.equals("")) {
        if(postingStr.equals("")) {
            int expireDate = (int) Math.floor(Math.random()*70000);
            String commitLink = HttpUtils.getRequestURL(request) + templateQueryString + "&email=" + emailId + "&pst=" + expireDate;
            %><mm:node number="<%= pageId %>" id="this_page" 
                ><mm:createnode type="ads" id="this_post"
                    ><mm:setfield name="title"><%= titleId %></mm:setfield
                    ><mm:setfield name="text"><%= textId %></mm:setfield
                    ><mm:setfield name="email"><%= emailId %></mm:setfield
                    ><mm:setfield name="expiredate"><%= expireDate %></mm:setfield
                ></mm:createnode
                ><mm:createrelation role="contentrel" source="this_post" destination="this_page" 
                /><mm:field name="titel" jspvar="page_title"  vartype="String" write="false"
                    ><mm:createnode type="email" id="thismail"
                        ><mm:setfield name="subject"><%= "Bevestigen plaatsing advertentie op " + page_title %></mm:setfield
                        ><mm:setfield name="from"><%= defaultFromAddress %></mm:setfield
                        ><mm:setfield name="to"><%= emailId %></mm:setfield
                        ><mm:setfield name="replyto"><%= defaultFromAddress %></mm:setfield
                        ><mm:setfield name="body">
                            <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                            </multipart>
                            <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                            <%= "<html>" + "Je hebt het volgende bericht verstuurd naar " + page_title + "<br><br>" 
                                + titleId + "<br><br>" + textId + "<br><br>"
                                + "Klik op de onderstaande link om de plaatsing van je advertentie op " 
                                + page_title  + " te bevestigen.<br><br>"
                                + "<a href=\"" + commitLink + "\">" + commitLink + "</a>"
                                + emailHelpText
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
        } else {
            templateTitle = "prikbord";
            %><mm:list path="ads" constraints="<%= "ads.email='" + emailId + "' AND ads.expiredate='" + postingStr + "'" %>"
                ><mm:node element="ads"
                    ><mm:setfield name="expiredate"><%= nowSec %></mm:setfield
                    ><%@include file="includes/cachekey.jsp"
                    %><cache:flush key="<%= cacheKey %>" scope="application" 
                /></mm:node
            ></mm:list><%
        }
}

int period = -31;
%><mm:list nodes="<%= pageId %>" path="pagina,contentrel,teaser" constraints="contentrel.pos='5'"
    ><mm:field name="teaser.titel" jspvar="teaser_title" vartype="Integer" write="false"
        ><% period = -teaser_title.intValue(); 
    %></mm:field
></mm:list

><%@include file="includes/header.jsp" %> 
<td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "Plaats uw bericht"; 
%><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>"><% 
    if(!emailId.equals("")&&postingStr.equals("")) { 
        String messageTitle = "Bevestigen plaatsing advertentie";
        String messageBody = "Je ontvangt een mail in je mailbox waarmee je het plaatsen van de juist verstuurde advertentie kunt bevestigen.";
        String messageHref = "index.jsp";
        String messageLinktext = "naar de homepage";
        String messageLinkParam = "target=\"_top\"";  
        %><%@include file="includes/showmessage.jsp" %><%

} else { 

%><table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;"><mm:node number="<%= pageId %>"
            ><mm:related path="contentrel,teaser" constraints="contentrel.pos='3'"
            ><p><div class="pageheader"><mm:field name="teaser.titel" /></div>
            <mm:field name="teaser.omschrijving" /></p>
            </mm:related
        ></mm:node>
        </td>
    </tr>
    <tr><td style="padding:10px;padding-top:18px;"><%

cal.add(Calendar.DATE,period);
String ads_constraints = "ads.expiredate <= " +(cal.getTime().getTime()/1000);
%><mm:list nodes="<%= pageId %>" path="pagina,contentrel,ads" constraints="<%= ads_constraints %>" 
    ><mm:node element="contentrel" id="thisrelation" 
    /><mm:deletenode referid="thisrelation"
    /><mm:remove referid="thisrelation" 
/></mm:list><%

ads_constraints = "ads.expiredate > " + (cal.getTime().getTime()/1000);
%><mm:list nodes="<%= pageId %>" path="pagina,contentrel,ads"
        constraints="<%= ads_constraints %>" orderby="ads.expiredate" directions="DOWN"
><mm:first
><table cellspacing="0" cellpadding="0" border="0" width="100%"></mm:first
    ><tr><td colspan="2" class="black" style="width:100%;"><img src="media/spacer.gif" width="1" height="1"></td></tr>
    <tr><td colspan="2" style="width:100%;"><img src="media/spacer.gif" width="1" height="5"></td></tr>
    <tr><td><div class="pageheader"><mm:field name="ads.title" /></div></td>
        <td><div align="right"><nobr><mm:field name="ads.expiredate" id="edate" write="false" 
                /><mm:time format="d MMM yyyy" referid="edate" 
                /><mm:remove referid="edate"
            /></nobr></div></td></tr>
    <tr><td colspan="2"><p><mm:field name="ads.text" /></td></tr>
    <tr><td colspan="2" style="width:100%;"><img src="media/spacer.gif" width="1" height="10"></td></tr>
    <tr><td colspan="2"><a href='mailto:<mm:field name="ads.email" />'><mm:field name="ads.email" /></a></td></tr>
    <tr><td colspan="2" style="width:100%;"><img src="media/spacer.gif" width="1" height="5"></td></tr>
<mm:last
></table></mm:last
></mm:list

    ><%@include file="includes/pageowner.jsp" 
    %></td></tr>
</table><% 

} 

%></div>
</td><%-- 

*************************************** right bar with the form *******************************
--%><td><%@include file="includes/whiteline.jsp" 
    %><form name="prikbord" method="post" onSubmit="return postIt(this);">
<table cellspacing="0" cellpadding="0" border="0" align="center">
    <tr>
        <td><div align="right"><span class="light_<%= cssClassName %>">Uw email adres</span></div></td>
    </tr>
    <tr>
        <td><div align="right"><input class="<%=  cssClassName %>" type="text" style="width:195px;" name="email"></div></td>
    </tr>
    <tr>
        <td><div align="right"><span class="light_<%= cssClassName %>">Titel van uw bericht</span></div></td>
    </tr>
    <tr>
        <td><div align="right"><input class="<%=  cssClassName %>" type="text" style="width:195px;" name="title"></div></td>
    </tr>
    <tr>
        <td><div align="right"><span class="light_<%= cssClassName %>">Uw bericht</span></div></td>
    </tr>
    <tr>
        <td><div align="right"><textarea class="<%=  cssClassName %>" name="text" style="width:195px;" rows="6"></textarea></div></td> 
    </tr>
    <tr>
        <td><br><br>
        <div align="right"><input type="submit" name="Submit" value="Plaats uw bericht" class="<%=  cssClassName 
            %>"  style="text-align:center;font-weight:bold;"></div>
        </td>
    </tr>
</table>
</form>
<script>
<%= "<!--" %>
function postIt(el) {
    var href = "prikbord.jsp<%= templateQueryString %>"; 
    var text = escape(document.prikbord.elements["text"].value);
    var title = escape(document.prikbord.elements["title"].value);
    var email = escape(document.prikbord.elements["email"].value);
    href += "&text=" + text + "&title=" + title + "&email=" + email; 
    document.location = href; 
    return false; 
}
<%= "//-->" %>
</script>
<%@include file="includes/whiteline.jsp" 
%></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>