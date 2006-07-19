<%
   String embargoPollConstraint = "(poll.embargo < '" + (nowSec+quarterOfAnHour) + "') AND "
                                + "(poll.use_verloopdatum='0' OR poll.verloopdatum > '" + nowSec + "' )";
%>
<%--  constraints="<%= embargoPollConstraint %>" --%>
polls<br/>

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
        window.open('','poll<%= poll_number %>','height=<%= 171 + (total_answers*46) %>,width=398, scrollbars=NO, menubar=0, toolbar=0, status=0, directories=0, resizable=0');
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
