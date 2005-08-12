<div class="contentBody">
  <mm:import externid="nfeedback"/>
  <mm:listcontainer path="people,popfeedback,competencies">
    <mm:constraint field="people.number" value="$user" operator="EQUAL" />
    <mm:constraint field="popfeedback.number" value="$nfeedback" operator="EQUAL" />
    <mm:constraint field="popfeedback.status" value="0" />
    <mm:list nodes="$user">
      <form name="givefeedbackform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
              referids="$referids">
            </mm:treefile>" method="post">
        <input type="hidden" name="command" value="sendfeedback">
        <input type="hidden" name="nfeedback" value="<mm:write referid="nfeedback"/>">
        <p><fmt:message key="GetInviteMessagePart1"/> <b><mm:field name="competencies.name"/></b> <fmt:message key="GetInviteMessagePart2"/> <b><mm:list
            nodes="$nfeedback" path="popfeedback,pop,people"
              ><mm:field name="people.firstname"
               /> <mm:field name="people.lastname"
            /></mm:list></b><fmt:message key="GetInviteMessagePart3"/></p>
        <table class="font" width="90%">
          <tr>
            <td width="180"><fmt:message key="CompEditWorkTogetherEtc"/></td>
            <td><input name="feedback1" type="text" class="formInput" size="50" maxlength="255"></td>
          </tr>
          <tr>
            <td><fmt:message key="CompEditGrade"/></td>
            <td><textarea name="feedback2" cols="50" rows="5"></textarea></td>
          </tr>
          <tr>
            <td><fmt:message key="Score"/></td>
            <td>
              <mm:list path="ratings" orderby="ratings.pos" directions="DOWN">
                <mm:first><select name="rating"></mm:first>
                  <option value="<mm:field name="number"/>"><mm:field name="name"/></option>
                <mm:last></select></mm:last>
              </mm:list>
            </td>
          </tr>
        </table>
        <input type="submit" class="formbutton" value="<fmt:message key="SendButton"/>">
        <input type="submit" class="formbutton" value="<fmt:message key="BackButtonLC"/>" onClick="givefeedbackform.command.value='no'">
      </form>
    </mm:list>
  </mm:listcontainer>
</div>
