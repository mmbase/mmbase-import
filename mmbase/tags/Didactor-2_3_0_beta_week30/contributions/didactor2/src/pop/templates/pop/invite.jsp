<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>
  <mm:import id="compname"><mm:node number="$currentcomp"><mm:field name="name"/></mm:node></mm:import>
      <form name="inviteform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
              referids="$popreferids,currentprofile,currentcomp">
            </mm:treefile>" method="post">
        <input type="hidden" name="popcmd" value="sendinvite">
        <input type="hidden" name="myfeedback1" value="<mm:write referid="myfeedback1"/>">
        <input type="hidden" name="myfeedback2" value="<mm:write referid="myfeedback2"/>">
        <p><di:translate key="pop.invitecolleague" />: <b><mm:write referid="compname"/></b></p>
        <table class="font" width="90%">
          <tr>
            <td width="80"><di:translate key="pop.inviterecipient" /></td>
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
            <td><di:translate key="pop.inviterequest" /></td>
            <td><textarea name="query" cols="50" rows="5"><di:translate key="pop.invitemessagepart1" /> "<mm:write referid="compname"/>".
<di:translate key="pop.invitemessagepart2" /> <mm:node number="$student"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:node><di:translate key="pop.invitemessagepart3" /></textarea></td>
          </tr>
        </table>
        <input type="submit" class="formbutton" value="<di:translate key="pop.sendbutton" />">
        <input type="submit" class="formbutton" value="<di:translate key="pop.backbuttonlc" />" onClick="inviteform.popcmd.value='continue'">
      </form>
</div>
