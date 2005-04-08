<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>
  <mm:import externid="todonumber">-1</mm:import>
  <mm:import externid="todocomp">-1</mm:import>

  <mm:import id="todoname"/>
  <mm:import id="tododesc"/>
  <mm:import id="durationvalue"/>
  <mm:import id="durationmeasure"/>
  <mm:compare referid="todonumber" value="-1" inverse="true">
    <mm:node number="$todonumber">
      <mm:import id="todoname" reset="true"><mm:field name="name"/></mm:import>
      <mm:import id="tododesc" reset="true"><mm:field name="description"/></mm:import>
      <mm:import id="durationvalue" reset="true"><mm:field name="durationvalue"/></mm:import>
      <mm:import id="durationmeasure" reset="true"><mm:field name="durationmeasure"/></mm:import>
      <mm:compare referid="currentcomp" value="-1">
        <mm:related path="competencies">
          <mm:import id="todocomp" reset="true"><mm:field name="competencies.number"/></mm:import>
        </mm:related>
      </mm:compare>
    </mm:node>
  </mm:compare>

  <form name="newtodoform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$referids,currentfolder,currentcomp">
        </mm:treefile>" method="post">
    <input type="hidden" name="command" value="savetodo">
    <input type="hidden" name="returnto" value="<mm:write referid="returnto"/>">
    <input type="hidden" name="todonumber" value="<mm:write referid="todonumber"/>">
    <input type="hidden" name="myfeedback1" value="<mm:write referid="myfeedback1"/>">
    <input type="hidden" name="myfeedback2" value="<mm:write referid="myfeedback2"/>">
    <table class="font" width="90%">
      <tr>
        <td width="80">Taak</td>
        <td><input name="todoname" class="popFormInput" type="text" size="50" maxlength="255" value="<mm:write referid="todoname"/>"></td>
      </tr>
      <tr>
        <td>Beschrijving</td>
        <td><textarea name="tododesc" class="popFormInput" cols="50" rows="5"><mm:write referid="tododesc"/></textarea></td>
      </tr>
      <tr>
        <td>Verwachte duur</td>
        <td>
          <input name="durationvalue" class="popDurationFormInput" type="text" size="15" maxlength="15" value="<mm:write referid="durationvalue"/>">
          <select name="durationmeasure">
            <option value="1"<mm:compare referid="durationmeasure" value="1"> selected</mm:compare>>uur</option>
            <option value="2"<mm:compare referid="durationmeasure" value="2"> selected</mm:compare>>dag</option>
            <option value="3"<mm:compare referid="durationmeasure" value="3"> selected</mm:compare>>week</option>
            <option value="4"<mm:compare referid="durationmeasure" value="4"> selected</mm:compare>>maand</option>
            <option value="5"<mm:compare referid="durationmeasure" value="5"> selected</mm:compare>>jaar</option>
          </select>
        </td>
      </tr>
      <mm:compare referid="currentcomp" value="-1">
        <tr>
          <td>Competentie</td>
          <td>
            <select name="todocomp">
              <option value="-1">...</option>
              <%@ include file="getcompetencies.jsp" %>
              <%  TreeMap competenciesIterator = (TreeMap) allCompetencies.clone();
                  while(competenciesIterator.size()>0) { 
                    String thisCompetencie = (String) competenciesIterator.firstKey(); %>
                    
                    <mm:node number="<%= thisCompetencie %>">
                      <option value="<%= thisCompetencie %>"<mm:compare referid="todocomp"
                            value="<%= thisCompetencie %>"> selected</mm:compare>><mm:field name='name'/></option>
                    </mm:node>
                    <% competenciesIterator.remove(thisCompetencie);
                  } %>
            </select>
          </td>
        </tr>
      </mm:compare>
    </table>
    <input type="submit" class="formbutton" value="aanmaken">
    <input type="submit" class="formbutton" value="terug" onClick="newtodoform.command.value='continue'">
  </form>
</div>