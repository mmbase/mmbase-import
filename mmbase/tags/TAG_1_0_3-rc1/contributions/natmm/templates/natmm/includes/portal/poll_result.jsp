<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="../getstyle.jsp" %>
<%
   String rootId = request.getParameter("r"); 
   if(rootId==null){ rootId="-1"; }
   String pollId = request.getParameter("poll"); 
   if(pollId==null){ pollId="-1"; }
%>
<mm:node number="<%= pollId %>" jspvar="poll" notfound="skipbody">
<mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String">
<%
  if(nType.equals("poll")){ // this node is a poll

     String antw = request.getParameter("antw");

     // Declare some variable for future use
     String tot_answers = ""; // Total votes for this answers
     String messageString ="";   // String to signal already voted or no answer

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
       poll.commit();
       // add all categories to customer
       
       if(NatMMConfig.hasClosedUserGroup) {
       
         %><%@include file="/editors/mailer/util/memberid_get.jsp" %><%
         if(memberid != null){
           PoolUtil.addPools(cloud,pollId,memberid,"posrel.pos=" + antw );
         }
         
       }
       // Set the cookie
       Cookie koekje = new Cookie(cookiestr, String.valueOf((new Date()).getTime()) );
       int expires = 60 * 60 * 12;     // Cookie expires after 12 hours
       koekje.setMaxAge(expires);      // The maximum age in seconds
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
       long result = Math.round(((double) answer_tot[j]/(double) tot_general) * 1000);
       procent[j] = result / 10;
       width[j] = (257*result)/1000;
     }

%> 
  <html>
    <head>
      <title><mm:node number="<%= rootId %>" notfound="skipbody"><mm:field name="naam" /></mm:node> - <mm:field name="question" /></title>
      <link rel="stylesheet" type="text/css" href="../../hoofdsite/themas/main.css" />
      <link rel="stylesheet" type="text/css" href="../../hoofdsite/themas/naardermeer.css" />
      <style>
      td {
        color: black;
        font-size: 70%; 
      }
      td.black {
        background-color: black;
      }
      A:link {
        color: black;
      }
      A:visited{
        color: black;
      }
      A:hover{
        color: black;
      }
      </style>
    </head>
    <body style="margin:0px;">
     <table cellspacing="0" class="maintable">
        <tr>
        <td style="vertical-align:top;" style="width:338px;">
          <div class="headerBar" style="width:100%;"><img src="logo.gif" alt="" border="0"> OPINIE</div>
          <div style="padding:14px;">
              <span class="colortitle"><mm:field name="titel"/></span>
              <% 
              if(!messageString.equals("")) {
                %>
                <br/><br/>
                <span style="color:red;font-weight:bold;"><%= messageString %></span>
                <br/><br/>
                <% 
              } 
              for (int k = 0; k < count; k++) { 
                %>
                <table cellspacing="0" cellpadding="0" border="0">
                  <tr>
                    <td colspan="4"><span class="title"><li><%= answer_title[k] %></span></td>
                  </tr>
                  <tr>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="1" height="1"></td>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="257" height="1"></td>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="1" height="1"></td>
                    <td><img src="../../media/trans.gif" alt="" border="0" width="35" height="1"></td>
                  </tr>
                  <tr>
                    <td class="black" style="width:1px;"><img src="../../media/trans.gif" alt="" border="0" width="1" height="14"></td>
                    <td>
                      <table cellspacing="0" cellpadding="0" border="0" width="257">
                        <tr>
                          <td width="<%= width[k] %>" class="maincolor"><img src="../../media/trans.gif" alt="" border="0" width="1" height="14"></td>
                          <td width="<%= 257- width[k] %>"><img src="../../media/trans.gif" alt="" border="0" width="1" height="14"></td>
                        </tr>
                     </table>
                    </td>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="1" height="14"></td>
                    <td style="padding-left:5px;"><span class="colortitle"><%= procent[k] %>%</span></td>
                  </tr>
                  <tr>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="1" height="1"></td>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="257" height="1"></td>
                    <td class="black"><img src="../../media/trans.gif" alt="" border="0" width="1" height="1"></td>
                    <td><img src="../../media/trans.gif" alt="" border="0" width="35" height="1"></td>
                 </tr>
               </table>
               <%
             }
             %>
             <img src="../../media/trans.gif" alt="" border="0" width="268" height="28">
             <a href="javascript:self.close()"><img src="../../media/close_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" alt="sluit dit venster" border="0"></a>
           </div>
          </td>
        </tr>
       </table>
       </body>
     </html>
<%
   }
%>
</mm:nodeinfo>
</mm:node>
</mm:cloud>
