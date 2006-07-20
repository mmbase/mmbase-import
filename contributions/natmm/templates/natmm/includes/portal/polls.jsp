<%
   String embargoPollConstraint = "(poll.embargo < '" + (nowSec+quarterOfAnHour) + "') AND "
                                + "(poll.use_verloopdatum='0' OR poll.verloopdatum > '" + nowSec + "' )";
%>
<%--  constraints="<%= embargoPollConstraint %>" --%>
<div style="background-color: #BDBDBD; color:black; padding-left:10px; font-weight:bold; width:100%; height:18px">
  POLLS
</div>

<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,poll">
  <mm:field name="poll.omschrijving"/><br/>
  <mm:node element="poll" jspvar="poll">
    <mm:field name="number" jspvar="poll_number" vartype="String" write="false">
    <table width="100%">
    <form name="poll<%= poll_number %>" method="post" target="poll<%= poll_number %>">
    <tr>
      <td>
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
      <td align="right">
        <input type="image" value="Kies" onclick="postIt()" src="media/buttonright_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" alt="" border="0">
      </td>
    </tr>
    </form>
    </table>

    <script language="JavaScript" type="text/javascript">
    <%= "<!--" %>
      function postIt() {
        window.open('','poll<%= poll_number %>','height=800,width=1000, scrollbars=YES, location = 1, menubar=1, toolbar=1, status=1, directories=1, resizable=1');
        var antw = "";
        for (i = 0; i < <%= ""+total_answers %>; i++) {
          if (document.poll<%= poll_number %>.antwoord[i].checked) {
            antw = document.poll<%= poll_number %>.antwoord[i].value;
          }
        }
        document.poll<%= poll_number %>.action = "includes/portal/poll_result.jsp?poll=<%= poll_number %>&antw="+antw;
      }
    <%= "//-->" %>
    </script>
    </mm:field>
  </mm:node>
</mm:list>
