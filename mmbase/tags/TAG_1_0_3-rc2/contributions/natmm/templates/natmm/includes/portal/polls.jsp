<%
   String requestURL = javax.servlet.http.HttpUtils.getRequestURL(request).toString();
   requestURL = requestURL.substring(0,requestURL.lastIndexOf("/")); 
   String embargoPollConstraint = "(poll.embargo < '" + (nowSec+quarterOfAnHour) + "') AND "
                                + "(poll.use_verloopdatum='0' OR poll.verloopdatum > '" + nowSec + "' )";
%>
<div class="headerBar" style="width:396px;;">OPINIE</div>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,poll" constraints="<%= embargoPollConstraint %>">
  <div style="padding-left:3px;"><mm:field name="poll.titel"/></div>
  <mm:node element="poll" jspvar="poll">
    <mm:field name="number" jspvar="poll_number" vartype="String" write="false">
    <table width="396px;">
    <form name="poll<%= poll_number %>" method="post" target="poll<%= poll_number %>">
    <tr>
      <td style="vertical-align:top;">
<% 
        int total_answers = 0; 
        for(int i=1; i<=5; i++) {
          String answer = poll.getStringValue("antwoord"+i);
          if (!"".equals(answer)) {
            total_answers++;
%>
            <input type="radio" name="antwoord" value="<%= "" + i %>"><%= answer %><br/>
<%                  
          }
        }
%>
      </td>
      <td style="text-align:right;vertical-align:top;">
        <table>
          <tr><td onclick="postIt();setTimeout('newwin.focus();',250);" onmouseover="this.style.cursor='pointer'"
                   style="height:29px; width:54px; background-color:f7f7f7; padding-left: 10px; border:1px solid A79C9F; border">
                <b>Stem</b>&nbsp;
                <img src="media/buttonright_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" alt="" border="0"/>
          </td></tr>
        </table>
      </td>
    </tr>
    </form>
    </table>

    <script language="JavaScript" type="text/javascript">
      function postIt() {
        var antw = "";
        for (i = 0; i < <%= ""+total_answers %>; i++) {
          if (document.poll<%= poll_number %>.antwoord[i].checked) {
            antw = document.poll<%= poll_number %>.antwoord[i].value;
          }
        }
        javascript:launchCenter("<%= requestURL + "/" + (isSubDir? "../" : "" ) %>includes/portal/poll_result.jsp?r=<%= subsiteID %>&rs=<%= styleSheet %>&poll=<%= poll_number %>&antw="+antw,'poll<%= poll_number %>',<%= 171 + (total_answers*46) %>,338,'location=no,directories=no,status=no,toolbars=no,scrollbars=no,resizable=yes');
      }
    </script>
    </mm:field>
  </mm:node>
</mm:list>
