<%
   String embargoPollConstraint = "(poll.embargo < '" + (nowSec+quarterOfAnHour) + "') AND "
                                + "(poll.use_verloopdatum='0' OR poll.verloopdatum > '" + nowSec + "' )";
%>
<div class="headerBar" style="width:100%;">POLLS</div>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,poll" constraints="<%= embargoPollConstraint %>">
  <mm:field name="poll.omschrijving"/><br/>
  <mm:node element="poll" jspvar="poll">
    <mm:field name="number" jspvar="poll_number" vartype="String" write="false">
    <table width="100%">
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
          <tr><td onclick="postIt()" onmouseover="this.style.cursor='pointer'"
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
    <%= "<!--" %>
      function postIt() {
        var antw = "";
        for (i = 0; i < <%= ""+total_answers %>; i++) {
          if (document.poll<%= poll_number %>.antwoord[i].checked) {
            antw = document.poll<%= poll_number %>.antwoord[i].value;
          }
        }
        window.open("includes/portal/poll_result.jsp?poll=<%= poll_number %>&antw="+antw,'poll<%= poll_number %>','height=<%= 171 + (total_answers*46) %>,width=398, scrollbars=NO, menubar=0, toolbar=0, status=0, directories=0, resizable=0');
      }
    <%= "//-->" %>
    </script>
    </mm:field>
  </mm:node>
</mm:list>
