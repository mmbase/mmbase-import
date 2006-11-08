<%@include file="/taglibs.jsp" %>
<%@include file="includes/getactiveaccount.jsp" %>
<mm:cloud logon="<%= account %>" pwd="<%= password %>" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<% expireTime = 5; // this page is also editted by website visitors %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/header.jsp" %>
<td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
<td colspan="2" class="transperant" valign="top">
<div class="<%= infopageClass %>" id="infopage">
<table width="100%" cellspacing="0" cellpadding="0" border="0">
<tr><td style="padding:10px;padding-top:18px;">
<%@include file="includes/back_print.jsp" %>
<%
// like templateQueryString from templatesettings.jsp, but w/o articleId
templateQueryString = ""; 
if(!paginaID.equals("-1")){ templateQueryString += "?p=" + paginaID; } 
if(!categoryId.equals("")){ templateQueryString += "&category=" + categoryId; }
   
if(!articleId.equals("-1")) { 
   
   %>
   <a href="<%= editwizard_location 
       %>/jsp/wizard.jsp?language=nl&wizard=config/artikel/artikel_calendar&objectnumber=<%= articleId 
       %>&referrer=<%= request.getServletPath().replaceAll("//","/")+"?p=" + paginaID
       %>">bewerk activiteit!</a><br/><br/>
   <mm:list nodes="<%= articleId %>" path="artikel"
      ><%@include file="includes/relatedarticle.jsp" 
   %></mm:list><%
    
} else {

   %><%@include file="includes/relatedteaser.jsp" %>
   <a href="<%= editwizard_location 
       %>/jsp/wizard.jsp?language=nl&wizard=config/artikel/artikel_calendar&objectnumber=new&origin=<%= paginaID
       %>&referrer=<%= request.getServletPath().replaceAll("//","/")+"?p=" + paginaID
       %>">voeg nieuwe activiteit toe</a>
   <%
   
   int previousYear = 0;
   int previousMonth = 0;
   Date dd = new Date(); 
   cal.setTime(dd);
   int thisMonth = cal.get(Calendar.MONTH);
   int thisYear = cal.get(Calendar.YEAR);
   
   // *** first get all the articles with an nowSec < expiredate ****
   String thisCalendar = "";
   String dateType = "";

   %><mm:node number="<%= paginaID %>"
      ><mm:field name="titel_fra" jspvar="dummy" vartype="String" write="false"><%
         dateType = dummy;
      %></mm:field
      ><mm:related path="contentrel,artikel" 
            constraints="<%= "artikel.verloopdatum > '" + nowSec + "'"  %>"
        ><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
        thisCalendar += "," + article_number;
        %></mm:field
      ></mm:related
      ><mm:related path="posrel,pools,posrel,artikel" 
            constraints="<%= "artikel.verloopdatum > '" + nowSec + "'" %>"
        ><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
        thisCalendar += "," + article_number;
        %></mm:field
      ></mm:related
   ></mm:node><%

	// -1 - geen verschijningsdatum, geen sluitingsdatum
	// 0 - wel verschijningsdatum, geen sluitingsdatum
	// 1 - zowel verschijnings- en sluitingsdatum
	// 2 - geen verschijningsdatum, wel sluitingsdatum
   if(dateType==null) {  dateType = "0"; }
   String orderby = "artikel.begindatum";
   boolean showTransmissionDate = false;
   boolean showExpireDate = false;
   if(dateType.equals("0")) { showTransmissionDate = true; showExpireDate = false; } 
   if(dateType.equals("1")) { showTransmissionDate = true; showExpireDate = true; } 
   if(dateType.equals("2")) { showTransmissionDate = false; showExpireDate = true; orderby = "artikel.verloopdatum"; }

   // *** list the articles ordered by transmissiondate ***
   if(!thisCalendar.equals("")) {
        thisCalendar = thisCalendar.substring(1);
        
        %><table cellspacing="0" cellpadding="0" border="0" class="black" width="100%">
        <mm:list nodes="<%= thisCalendar %>" path="artikel" orderby="<%= orderby %>" directions="UP" 
            ><mm:node element="artikel"
               ><mm:field name="number" jspvar="article_number" vartype="String" write="false"
               ><mm:field name="begindatum" jspvar="events_transmissiondate" vartype="String" write="false"
               ><mm:field name="verloopdatum" jspvar="events_expiredate" vartype="String" write="false"><%
               
               long tb = Integer.parseInt(events_transmissiondate); tb = 1000 * tb; dd.setTime(tb); cal.setTime(dd);
               int beginDay = cal.get(Calendar.DAY_OF_MONTH);
               int beginMonth = cal.get(Calendar.MONTH);
               int beginYear = cal.get(Calendar.YEAR);
               
               tb = Integer.parseInt(events_expiredate); tb = 1000 * tb; dd.setTime(tb); cal.setTime(dd);
               int endDay = cal.get(Calendar.DAY_OF_MONTH);
               int endMonth = cal.get(Calendar.MONTH);
               int endYear = cal.get(Calendar.YEAR);

               if(previousYear<beginYear||previousMonth<beginMonth) { 
                  %><tr><td class="content" colspan="3" style="padding-top:22px;">
                        <span class="contenttitle">
                          <% if(!showTransmissionDate&&showExpireDate) { %>
                              <%= months_lcase[endMonth] %> <%= endYear %>
                              <%
                              previousYear = endYear;
                              previousMonth = endMonth;
                              } else { 
                              %>
                              <%= months_lcase[beginMonth] %> <%= beginYear %>
                              <%
                              previousYear = beginYear;
                              previousMonth = beginMonth;
                           } 
                           %>
                        </span>
                  </td></tr><%
                  
               }
               if(beginMonth==endMonth
                     && beginDay == endDay
                     && showTransmissionDate
                     && showExpireDate) { showTransmissionDate = false; }
               %><tr>
                  <td class="content"><% 
                      if(!showTransmissionDate&&!showExpireDate) { 
                          %>&nbsp;<% 
                      } else if(showTransmissionDate&&showExpireDate) {
                          %><%= beginDay %> - <%= endDay %><% if(beginMonth!=endMonth) { %> <%= months_lcase[endMonth] %><% }
                      } else if(showTransmissionDate&&!showExpireDate) { 
                          %><%= beginDay %><% 
                      } else if(!showTransmissionDate&&showExpireDate) { 
                          %><%= endDay %><% 
                      } %></td>
                  <td class="content">
                      <a href="<mm:url page="<%= templateQueryString + "&article=" + article_number %>" 
                                  />"><span class="<%= (tb>nowSec ? "red" : "black" ) %>"><mm:field name="titel" /></span></a></td>
                  <td class="content">&nbsp;</td>
               </tr>
               </mm:field
               ></mm:field
               ></mm:field
             ></mm:node>
        </mm:list>
    </table><%
    }
} 
    %></td></tr>
</table>
</div>
</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
