<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="getids.jsp" %>
<% boolean isEmpty = true; %>
<mm:import externid="msg">-1</mm:import>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>
    <mm:compare referid="msg" value="-1" inverse="true">
      <mm:write referid="msg"/>
    </mm:compare>

  <mm:node number="$currentcomp">
    <form name="editcompform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$referids,currentprofile,currentcomp">
        </mm:treefile>" method="post">
    <input type="hidden" name="command" value="savecomp">
    <input type="hidden" name="returnto" value="editcomp">
    <table width="90%" border="1">
      <tr>
        <td width="80">Competentie</td>
        <td><mm:field name="name"/></td>
      </tr>
      <tr>
        <td>Aan gewerkt door middel van</td>
        <td><input name="myfeedback1" type="text" size="50" maxlength="255" value="<mm:write referid="myfeedback1"/>"></td>
      </tr>
      <tr>
        <td>Zelfbeoordeling</td>
        <td><textarea name="myfeedback2" cols="50" rows="5"><mm:write referid="myfeedback2"/></textarea></td>
      </tr>
      <tr>
        <td>Persoonlijke taken</td>
        <td>
          <% isEmpty = true; %>
          <mm:list nodes="$currentpop" path="pop,todoitems,competencies" orderby="todoitems.number" directions="UP"
              constraints="competencies.number LIKE $currentcomp">
            <% isEmpty = false; %>
            <input type="checkbox" name="ids" value="<mm:field name="todoitems.number"/>"><mm:field name="todoitems.name"/><br/>
          </mm:list>
          <% if (isEmpty) { %>&nbsp<% } %>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <a href="#1" onclick="editcompform.command.value='addtodo';editcompform.submit();return false;">
            <img src="<mm:treefile page="/pop/gfx/icon_add_todo.gif" objectlist="$includePath" referids="$referids"/>"
                border="0" alt="Maak een nieuwe persoonlijke taak aan"/></a>
          <a href="#1" onclick="if (!window.confirm('Weet u zeker dat u de geselecteerde persoonlijke taken wilt verwijderen?'))
                return false;editcompform.command.value='deltodo';editcompform.submit();return false;">
            <img src="<mm:treefile page="/pop/gfx/afspraak verwijderen.gif" objectlist="$includePath" referids="$referids"/>"
                border="0" alt="Verwijder de geselecteerde persoonlijke taken"/></a>
        </td>
      </tr>
  </table>
  <table width="100%" border="1">
    <tr>
      <td><input type="submit" value="aanmaken"><input type="submit" value="terug" 
                                                  onClick="editcompform.command.value='no'"></td>
      <td align="right">
        <a href="#1" onclick="editcompform.command.value='invite';editcompform.submit();return false;">
          <img src="<mm:treefile page="/pop/gfx/icon_invitation.gif" objectlist="$includePath" referids="$referids"/>"
              border="0" alt="Nodig een collega uit om een feedback te geven op deze competentie"/></a>
      </td>
    </tr>
  </table>
  </form>
  <p>Beoordelingen</p>
    <mm:relatedcontainer path="popfeedback,pop">
      <mm:constraint field="pop.number" referid="currentpop" operator="EQUAL"/>
      <di:table>
        <di:row>
          <di:headercell>Door</di:headercell>
          <di:headercell>Aan samengewerkt door middel van</di:headercell>
          <di:headercell>Beoordeling</di:headercell>
        </di:row>
        <mm:related>
          <mm:node element="popfeedback">
            <mm:relatedcontainer path="people">
              <mm:constraint field="people.number" referid="user" operator="EQUAL" inverse="true"/>
              <mm:related>
                <di:row>
                  <di:cell><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></di:cell>
                  <mm:field name="popfeedback.status" jspvar="isAnswered" vartype="String">
                    <% if (!isAnswered.equals("0")) { %>
                      <di:cell><mm:field name="popfeedback.rank"/></di:cell>
                      <di:cell><mm:field name="popfeedback.text"/></di:cell>
                    <% } else { %>
                      <di:cell><i>niet geantwoord</i></di:cell>
                      <di:cell>&nbsp;</di:cell>
                    <% } %>
                   </mm:field>
                </di:row>
              </mm:related>
            </mm:relatedcontainer>
          </mm:node>
        </mm:related>
      </di:table>
    </mm:relatedcontainer>
  </mm:node>
</div>
</fmt:bundle>
</mm:cloud>
</mm:content>
