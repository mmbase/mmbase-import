<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>
  <mm:import id="compname"><mm:node number="$currentcomp"><mm:field name="name"/></mm:node></mm:import>
      <form name="inviteform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
              referids="$popreferids,currentprofile,currentcomp">
            </mm:treefile>" method="post">
        <input type="hidden" name="command" value="sendinvite">
        <input type="hidden" name="myfeedback1" value="<mm:write referid="myfeedback1"/>">
        <input type="hidden" name="myfeedback2" value="<mm:write referid="myfeedback2"/>">
        <p>Nodig een collega uit om een beoordeling te geven over uw competentie: <b><mm:write referid="compname"/></b></p>
        <table class="font" width="90%">
          <tr>
            <td width="80">Ontvanger</td>
            <td>
              <mm:list nodes="$student" path="people1,classes,people2" constraints="people2.number!='$student'">
                <mm:first><select name="invitee"></mm:first>
                <option value="<mm:field name="people2.number"/>"><mm:field name="people2.firstname"
                    /> <mm:field name="people2.lastname"/></option>
                <mm:last></select></mm:last>
              </mm:list>
            </td>
          </tr>
          <tr>
            <td>Verzoek</td>
            <td><textarea name="query" cols="50" rows="5">Beste ...,
Graag zou ik van je willen weten wat je vindt van mijn kennis- en vaardigheden op het vlak van "<mm:write referid="compname"/>".
Alvast bedankt voor je reactie,
Met vriendelijke groet, <mm:node number="$student"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:node></textarea></td>
          </tr>
        </table>
        <input type="submit" class="formbutton" value="versturen">
        <input type="submit" class="formbutton" value="terug" onClick="inviteform.command.value='continue'">
      </form>
</div>