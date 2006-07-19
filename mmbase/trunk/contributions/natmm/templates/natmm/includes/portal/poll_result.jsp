<%@page language="java" contentType="text/html; charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page import="java.util.*" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%
   String pollId = request.getParameter("poll"); 
   if(pollId==null){ pollId="-1"; }
%>

<%-- check whether node with number pollId exists --%><%
   try { 
%>
<mm:node number="<%= pollId %>" notfound="throwexception" />
<%
   } catch(Exception e) { 
     pollId = "-1";
   }
%>
<%-- check whether pollId refers to a poll --%>
<mm:list nodes="<%= pollId %>" path="poll" max="1">
  <mm:import id="is_poll" />
</mm:list>
<mm:notpresent referid="is_poll"><% pollId = "-1"; %></mm:notpresent>
<mm:remove referid="is_poll"/>

<% 
   if(!pollId.equals("-1")) {
%>
   <mm:node number="<%= pollId %>" jspvar="poll">
<%
     String antw = request.getParameter("antw");

     // Declare some variable for future use
     String tot_answers = "";	// Total votes for this answers
     String messageString ="";	// String to signal already voted or no answer

     String cookiestr = "poll" + pollId;

     // Check whether this person already voted by using the cookie
     boolean alreadyvoted = false;
     Cookie[] koekjes = request.getCookies();
     if(koekjes!=null){
       for (int c = 0; c < koekjes.length; c++) {
         String koekje = koekjes[c].getName();
         if (koekje!=null&&koekje.equals(cookiestr)) {
           long timeDelta = 0;
           try { timeDelta = (new Date()).getTime() - Long.parseLong(koekjes[c].getValue()); 
           } catch (Exception e) { }			
           out.println("\n<!-- We found our cookie: " + cookiestr + " of age " + timeDelta / 1000 + " seconds -->");
           alreadyvoted = true;
         }
       }
     }

     if (alreadyvoted) {
       messageString = "Je kunt maar 1 keer per dag stemmen!";
     } else if (antw != null && !antw.equals("")) { // we have made a choice, get total votes for this answer
       tot_answers = poll.getStringValue("stemmen" + antw);
       // Add 1 to total_answers 
       int ta = 0;
       try{ ta = Integer.parseInt(tot_answers); } catch (Exception e) { } 
       if(ta<0) ta = 0; // default by new object = -1
       ta++;
       poll.setStringValue("stemmen" + antw, "" + ta);
       // Set the cookie
       Cookie koekje = new Cookie(cookiestr, String.valueOf((new Date()).getTime()) );
       int expires = 60 * 60 * 12;		// Cookie expires after 12 hours
       koekje.setMaxAge(expires);		// The maximum age in seconds
       response.addCookie(koekje);
     }
     else {
       messageString = "Je hebt geen keuze gemaakt!";
     } 

     String[] answer_title = new String[5];
     String[] answer_description = new String[5];
     int[] answer_tot = new int[5];
     int tot_general = 0;            // Total number of votes
     int count = 0;

     for(int i=1; i<=5; i++) {
       String answer = poll.getStringValue("antwoord"+i);
       if (!"".equals(answer)) {
         answer_title[count]=answer;
         String answerNum = poll.getStringValue("stemmen"+i);
         answer_tot[count] = 0;
         try{ answer_tot[count] = Integer.parseInt(answerNum); } catch (Exception e) { }
         if(answer_tot[count]<0) answer_tot[count] = 0; // default by new object = -1
         tot_general = tot_general + answer_tot[count];
         count++;
       }
     }

     // Calculations for the chart
     if(tot_general==0) tot_general = 1;
     long[] procent = new long[5];
     long[] width = new long[5];
     for (int j = 0; j < count; j++) {
       long uitkomst = Math.round(((double) answer_tot[j]/(double) tot_general) * 1000);
       procent[j] = uitkomst / 10;
       width[j] = (257*uitkomst)/1000;
     }

%>
     <html>
     <head><title>Netwerk - <mm:field name="question" /></title></head>
     <link rel="stylesheet" type="text/css" href="../css/netwerk.css">
     <body class="grey">

     <%-- make 5 columns: 25 + 60 + 1 + 287 + 25 = 398 --%>
     <table cellspacing="0" cellpadding="0" border="0" width="398">
       <%-- column 1: netwerk logo, redspace, top logo --%>
       <tr>
         <td colspan="2"><img src="../media/netwerk_logo_small.jpg" alt="" border="0"></td>
         <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="1"></td>
         <td class="red_middle" colspan="2"><div align="right"><div class="pagetitle">OPINIE <span class="lighter">&#124;&#124;</span>&nbsp;<span class="yellow">NETWERK</span>
           <img src="../media/spacer.gif" alt="" border="0" width="6" height="1"></div></div>
         </td>
       </tr>
       <tr>
         <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="25" height="1"></td>
         <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="60" height="1"></td>
         <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="1"></td>
         <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="287" height="1"></td>
         <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="25" height="1"></td>
       </tr>
       <tr>
         <td class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="25" height="16"></td>
         <td class="grey_middle" colspan="3"><div align="center"><span class="pagetitle"><span class="red"><%= messageString %></span></span></div></td>
         <td class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="25" height="16"></td>
       </tr>
       <tr>
         <td class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="25" height="1"></td>
         <td class="grey_light" colspan="3">
           <div class="title"><mm:field name="question" /></div><mm:field name="description" />
         </td>
         <td class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="25" height="1"></td>
       </tr>
       <tr>
         <td class="grey_light" colspan="5"><img src="../media/spacer.gif" alt="" border="0" width="398" height="14"></td>
       </tr>
       <tr>
         <td class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="25" height="1"></td>
         <td class="grey_light" colspan="3">
           <div align="center">
             <%-- make 5 columns: 6 + 1 + 257 + 1 + 35 + 45 = 345 --%>
<% 
             for (int k = 0; k < count; k++) { 
%>
               <table cellspacing="0" cellpadding="0" border="0" width="345">
                 <tr>
                   <td class="grey_middle"><img src="../media/red.gif" alt="" border="0" width="2" height="2"></td>
                   <td colspan="5"><span class="title"><%= answer_title[k] %></span></td>
                 </tr>
                 <tr>
                   <td rowspan="3" class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="6" height="1"></td>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="1"></td>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="257" height="1"></td>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="1"></td>
                   <td><img src="../media/spacer.gif" alt="" border="0" width="35" height="1"></td>
                   <td><img src="../media/spacer.gif" alt="" border="0" width="45" height="1"></td>
                 </tr>
                 <tr>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="14"></td>
                   <td class="white"><img src="../media/black.gif" alt="" border="0" width="<%= width[k] %>" height="14"></td>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="14"></td>
                   <td><img src="../media/spacer.gif" alt="" border="0" width="5" height="1"><span class="title"><%= procent[k] %>%</span></td>
                   <td><img src="../media/spacer.gif" alt="" border="0" width="5" height="1"><span class="title"><%-- (<%= answer_tot[k] %>) --%>&nbsp;</span></td>
                 </tr>
                 <tr>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="1"></td>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="257" height="1"></td>
                   <td class="black"><img src="../media/spacer.gif" alt="" border="0" width="1" height="1"></td>
                   <td><img src="../media/spacer.gif" alt="" border="0" width="35" height="1"></td>
                   <td><img src="../media/spacer.gif" alt="" border="0" width="45" height="1"></td>
                 </tr>
               </table>
               <img src="../media/spacer.gif" alt="" border="0" width="345" height="7">
<%
             }
%>
             <img src="../media/spacer.gif" alt="" border="0" width="345" height="28">
             <a href="javascript:self.close()"><img src="../media/buttons/sluit.gif" alt="sluit dit venster" border="0"></a>
           </div>
         </td>
         <td class="grey_light"><img src="../media/spacer.gif" alt="" border="0" width="25" height="1"></td>
       </tr>
       <tr>
         <td class="grey_light" colspan="5"><img src="../media/spacer.gif" alt="" border="0" width="398" height="32"></td>
       </tr>
     </table>
     </body>
     </html>
   </mm:node>
<%
   } else { 
%>
     <html>
       <head><title>Netwerk - <mm:field name="title" /></title></head>
       <link rel="stylesheet" type="text/css" href="../css/netwerk.css">
       <body>No valid poll number is provided in the url.</body>
     </html>
<% 
   }
%>

</mm:cloud>