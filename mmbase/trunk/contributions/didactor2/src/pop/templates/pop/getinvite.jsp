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
        <p>Onderstaand kunt u uw beoordeling geven over de competentie <b><mm:field name="competencies.name"/></b> van <b><mm:list
            nodes="$nfeedback" path="popfeedback,pop,people"
              ><mm:field name="people.firstname"
               /> <mm:field name="people.lastname"
            /></mm:list></b></p>
        <table width="90%" border="1">
          <tr>
            <td width="180">Aan samengewerkt door middel van</td>
            <td><input name="feedback1" type="text" size="50" maxlength="255"></td>
          </tr>
          <tr>
            <td>Beoordeling</td>
            <td><textarea name="feedback2" cols="50" rows="5"></textarea></td>
          </tr>
        </table>
        <input type="submit" value="versturen">
        <input type="submit" value="terug" onClick="givefeedbackform.command.value='no'">
      </form>
    </mm:list>
  </mm:listcontainer>
</div>