<%@ page import="java.text.NumberFormat"%>

<table border="1" cellpadding="0" cellspacing="0">
   <tr>
      <td>
         <table bgcolor="#FFFFFF" cellspacing="0" cellpadding="0" border="0">
            <tr style="font-family: Tahoma, Verdana, Arial, Helvetica, sans-serif; font-size: 11px; font-weight: bold;">
               <td align="left" valign="top">&nbsp;<bean:message bundle="LEOCMS" key="poll.antwoord"/>&nbsp;</td>
               <td align="right" valign="top">&nbsp;<bean:message bundle="LEOCMS" key="poll.score"/>&nbsp;</td>
               <td align="right" valign="top">&nbsp;<bean:message bundle="LEOCMS" key="poll.aantal.hits"/>&nbsp;</td>
            </tr>

   <%
      int votes[] = new int[5];
      votes[0] = node.getIntValue("stemmen1");
      votes[0] = (votes[0] < 0) ? 0 : votes[0];
      votes[1] = node.getIntValue("stemmen2");
      votes[1] = (votes[1] < 0) ? 0 : votes[1];
      votes[2] = node.getIntValue("stemmen3");
      votes[2] = (votes[2] < 0) ? 0 : votes[2];
      votes[3] = node.getIntValue("stemmen4");
      votes[3] = (votes[3] < 0) ? 0 : votes[3];
      votes[4] = node.getIntValue("stemmen5");
      votes[4] = (votes[4] < 0) ? 0 : votes[4];
      int totalVotes = votes[0] + votes[1] + votes[2] + votes[3] + votes[4];
      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(1);
      for (int index = 1 ; index <= 5 ; index++) {


   %>

            <mm:field name='<%= "antwoord" + index%>'>
               <mm:isnotempty>
                  <tr style="font-family: Tahoma, Verdana, Arial, Helvetica, sans-serif; font-size: 11px; font-weight: normal;">
                     <td align="left" valign="top">&nbsp;<mm:write/>&nbsp;</td>
                     <td align="right" valign="top">&nbsp;<%=(totalVotes == 0)? "0" : numberFormat.format(((votes[index - 1] / (float) totalVotes) * 100))%>%&nbsp;</td>
                     <td align="center" valign="top">&nbsp;(<%=votes[index - 1]%>)&nbsp;</td>
                  </tr>
               </mm:isnotempty>
            </mm:field>
            <%
               }
            %>
          </table>
      </td>
   </tr>
</table>
